package su.nightexpress.excellentjobs.zone.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.economybridge.api.Currency;
import su.nightexpress.economybridge.currency.CurrencyId;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.util.Hours;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.bukkit.NightItem;
import su.nightexpress.nightcore.util.geodata.Cuboid;
import su.nightexpress.nightcore.util.geodata.pos.BlockPos;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.function.UnaryOperator;

public class Zone extends AbstractFileData<JobsPlugin> {

    private final Map<DayOfWeek, Hours>     hoursByDayMap;
    private final Map<String, BlockList>    blockListMap;
    private final Map<String, Modifier>     paymentModifierMap;
    private final Map<BlockPos, RenewBlock> renewBlocks;

    private World  world;
    private String worldName;
    private Cuboid cuboid;

    private String        name;
    private List<String>  description;
    private NightItem     icon;
    private boolean       permissionRequired;
    private boolean       pvpAllowed;
    private Set<String>   linkedJobs;
    private int           minJobLevel;
    private int           maxJobLevel;
    private Modifier      xpModifier;
    private Set<Material> disabledInteractions;
    private boolean       hoursEnabled;

    public Zone(@NotNull JobsPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.hoursByDayMap = new HashMap<>();
        this.blockListMap = new HashMap<>();
        this.paymentModifierMap = new HashMap<>();
        this.renewBlocks = new HashMap<>();

        this.linkedJobs = new HashSet<>();
        this.disabledInteractions = new HashSet<>();
        this.xpModifier = Modifier.add(0, 0, 0);
        this.icon = new NightItem(Material.MAP);
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        // ----------- UPDATE - START since 1.8.0 -----------
        if (config.contains("Job.Id")) {
            String jobId = config.getString("Job.Id");
            if (jobId != null) {
                config.set("Job.LinkedIDs", Lists.newSet(jobId));
            }
            config.remove("Job.Id");
        }

        if (config.contains("Open_Times")) {
            config.getSection("Open_Times").forEach(dayName -> {
                DayOfWeek day = StringUtil.getEnum(dayName, DayOfWeek.class).orElse(null);
                if (day == null) return;

                var list = config.getStringList("Open_Times." + dayName);
                if (list.isEmpty()) return;

                config.set("Hours.ByDay." + dayName, list.getFirst());
            });
            config.remove("Open_Times");
        }
        // ----------- UPDATE - END -----------


        this.setWorldName(config.getString("Bounds.World", "null"));

        BlockPos minPos = BlockPos.read(config, "Bounds.P1");
        BlockPos maxPos = BlockPos.read(config, "Bounds.P2");
        this.setCuboid(new Cuboid(minPos, maxPos));

        this.setLinkedJobs(config.getStringSet("Job.LinkedIDs"));
        this.setMinJobLevel(config.getInt("Job.Min_Level", -1));
        this.setMaxJobLevel(config.getInt("Job.Max_Level", -1));

        this.setName(config.getString("Name", StringUtil.capitalizeFully(this.getId())));
        this.setDescription(config.getStringList("Description"));
        this.setIcon(ConfigValue.create("Icon", new NightItem(Material.MAP)).read(config));
        this.setPermissionRequired(config.getBoolean("Permission_Required"));
        this.setPvPAllowed(config.getBoolean("PvP_Allowed"));

        config.getSection("Hours.ByDay").forEach(dayName -> {
            DayOfWeek day = StringUtil.getEnum(dayName, DayOfWeek.class).orElse(null);
            if (day == null) return;

            Hours hours = Hours.parse(String.valueOf(config.getString("Hours.ByDay." + dayName)));
            if (hours == null) return;

            this.hoursByDayMap.put(day, hours);
        });

        config.getSection("Block_Lists").forEach(sId -> {
            BlockList blockList = BlockList.read(config, "Block_Lists." + sId, sId);
            this.blockListMap.put(blockList.getId(), blockList);
        });

        config.getSection("Payment_Modifier").forEach(curId -> {
            Modifier modifier = Modifier.read(config, "Payment_Modifier." + curId);
            this.paymentModifierMap.put(CurrencyId.reroute(curId), modifier);
        });

        this.setXPModifier(Modifier.read(config, "XP_Modifier"));

        this.disabledInteractions = Lists.modify(config.getStringSet("Disabled_Block_Interactions"), BukkitThing::getMaterial);

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        config.set("Bounds.World", this.worldName);
        this.cuboid.getMin().write(config, "Bounds.P1");
        this.cuboid.getMax().write(config, "Bounds.P2");
        config.set("Name", this.getName());
        config.set("Description", this.getDescription());
        config.set("Icon", this.icon);
        config.set("Permission_Required", this.isPermissionRequired());
        config.set("PvP_Allowed", this.isPvPAllowed());
        config.set("Disabled_Block_Interactions", Lists.modify(this.disabledInteractions, BukkitThing::toString));

        config.remove("Hours.ByDay");
        this.hoursByDayMap.forEach((day, hours) -> {
            config.set("Hours.ByDay." + day.name(), hours.serialize());
        });

        config.remove("Block_Lists");
        this.blockListMap.forEach((id, blockList) -> config.set("Block_Lists." + id, blockList));

        config.set("Job.LinkedIDs", this.linkedJobs);
        config.set("Job.Min_Level", this.getMinJobLevel());
        config.set("Job.Max_Level", this.getMaxJobLevel());
        config.remove("Payment_Modifier");
        this.paymentModifierMap.forEach((currencyId, mod) -> {
            mod.write(config, "Payment_Modifier." + currencyId);
        });
        this.getXPModifier().write(config, "XP_Modifier");
    }

    @NotNull
    public UnaryOperator<String> replacePlaceholders() {
        return Placeholders.ZONE.replacer(this);
    }

    @NotNull
    public UnaryOperator<String> replaceAllPlaceholders() {
        return Placeholders.ZONE_EDITOR.replacer(this);
    }

    public boolean handleBlockBreak(@NotNull BlockBreakEvent event, @NotNull Block block) {
        if (!this.isActive()) return false;
        if (block.getWorld() != this.world) return false;

        BlockList blockList = this.getBlockList(block);
        if (blockList == null) return false;

        if (!blockList.isDropItems()) {
            event.setExpToDrop(0);
            event.setDropItems(false);
        }

        BlockData blockData = block.getBlockData();
        BlockPos pos = BlockPos.from(block.getLocation());
        long resetDate = TimeUtil.createFutureTimestamp(blockList.getResetTime());

        this.renewBlocks.put(pos, new RenewBlock(blockData, resetDate));
        this.plugin.runTask(() -> this.world.setBlockData(block.getLocation(), blockList.getFallbackMaterial().createBlockData()));
        return true;
    }

    public void regenerateBlocks() {
        this.regenerateBlocks(false);
    }

    public void regenerateBlocks(boolean force) {
        if (!this.isActive()) return;

        this.renewBlocks.entrySet().removeIf(entry -> {
            BlockPos pos = entry.getKey();
            RenewBlock renewBlock = entry.getValue();

            if (!force) {
                if (!renewBlock.isReady()) return false;
                if (!pos.isChunkLoaded(this.world)) return false;
            }

            BlockData blockData = renewBlock.getBlockData();
            Location location = pos.toLocation(this.world);
            this.world.setBlockData(location, blockData);
            UniParticle.of(Particle.BLOCK, blockData).play(LocationUtil.setCenter3D(location), 0.35, 0.05, 60);

            return true;
        });
    }

    public boolean contains(@NotNull Location location) {
        return this.cuboid.contains(location);
    }

    public boolean isAvailable(@NotNull Player player) {
        if (!this.isActive()) return false;
        if (player.hasPermission(Perms.BYPASS_ZONE_ACCESS)) return true;
        if (!this.isGoodHours()) return false;
        if (!this.hasPermission(player)) return false;

        return this.hasAnyGoodJob(player);
    }

    public boolean hasAnyGoodJob(@NotNull Player player) {
        JobUser user = this.plugin.getUserManager().getOrFetch(player);

        return user.getDatas().stream().anyMatch(jobData -> jobData.getState() != JobState.INACTIVE && this.isGoodLevel(jobData.getLevel()));
    }

    public boolean isGoodJob(@NotNull Job job) {
        return this.linkedJobs.contains(job.getId());
    }

    public boolean isGoodLevel(int level) {
        boolean underMin = this.minJobLevel <= 0 || level > this.minJobLevel;
        boolean underMax = this.maxJobLevel <= 0 || level < this.maxJobLevel;

        return underMin && underMax;
    }

    public boolean isLevelRequired() {
        return this.getMinJobLevel() > 0 || this.getMaxJobLevel() > 0;
    }

    public boolean isGoodHours() {
        if (!this.isHoursEnabled()) return true;

        Hours hours = this.getTodayHours();
        return hours != null && hours.isAvailable();
    }

    public boolean hasPermission(@NotNull Player player) {
        return !this.isPermissionRequired() || (player.hasPermission(this.getPermission()) || player.hasPermission(Perms.ZONE));
    }

    public boolean isDisabledInteraction(@NotNull Block block) {
        return this.isDisabledInteraction(block.getType());
    }

    public boolean isDisabledInteraction(@NotNull Material material) {
        return this.disabledInteractions.contains(material);
    }

    @Nullable
    public Hours getTodayHours() {
        return this.getHours(LocalDate.now().getDayOfWeek());
    }

    @Nullable
    public LocalTime getNearestCloseTime() {
        if (!this.isHoursEnabled() || this.hoursByDayMap.isEmpty()) return null;

        Hours hours = this.getTodayHours();
        return hours == null ? null : hours.getTo();
    }

    @Nullable
    public LocalTime getNearestOpenTime() {
        if (!this.isHoursEnabled() || this.hoursByDayMap.isEmpty()) return null;

        Hours hours = null;
        int dayCount = 0;

        while (dayCount < 8 && hours == null) {
            LocalDate dateLookup = LocalDate.now().plusDays(dayCount++);
            hours = this.getHours(dateLookup.getDayOfWeek());
        }

        return hours == null ? null : hours.getFrom();
    }

    @NotNull
    public String getPermission() {
        return Perms.PREFIX_ZONE + this.getId();
    }

    public boolean isActive() {
        return this.world != null;
    }

    public boolean isInactive() {
        return !this.isActive();
    }

    @NotNull
    public World getWorld() {
        if (this.world == null) throw new IllegalStateException("Zone world is null! You must check Zone#isActive before calling this method.");

        return this.world;
    }

    public void reactivate() {
        World world = this.plugin.getServer().getWorld(this.worldName);
        if (world == null) {
            this.deactivate();
        }
        else if (this.isInactive()) {
            this.activate(world);
        }
    }

    public void activate(@NotNull World world) {
        if (this.worldName.equalsIgnoreCase(world.getName())) {
            this.world = world;
        }
    }

    public void deactivate(@NotNull World world) {
        if (this.worldName.equalsIgnoreCase(world.getName())) {
            this.deactivate();
        }
    }

    public void deactivate() {
        this.world = null;
    }

    @NotNull
    public String getWorldName() {
        return this.worldName;
    }

    public void setWorldName(@NotNull String worldName) {
        this.worldName = worldName;
    }

    @NotNull
    public Cuboid getCuboid() {
        return this.cuboid;
    }

    public void setCuboid(@NotNull Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public List<String> getDescription() {
        return this.description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = description;
    }

    @NotNull
    public NightItem getIcon() {
        return this.icon.copy();
    }

    public void setIcon(@NotNull NightItem icon) {
        this.icon = icon.copy().ignoreNameAndLore().setHideComponents(true);
    }

    public boolean isPermissionRequired() {
        return permissionRequired;
    }

    public void setPermissionRequired(boolean permissionRequired) {
        this.permissionRequired = permissionRequired;
    }

    public boolean isPvPAllowed() {
        return pvpAllowed;
    }

    public void setPvPAllowed(boolean pvpAllowed) {
        this.pvpAllowed = pvpAllowed;
    }

    @NotNull
    public Set<String> getLinkedJobs() {
        return this.linkedJobs;
    }

    public void setLinkedJobs(@NotNull Set<String> linkedJobs) {
        this.linkedJobs = Lists.modify(linkedJobs, String::toLowerCase);
    }

    public int getMinJobLevel() {
        return this.minJobLevel;
    }

    public void setMinJobLevel(int minJobLevel) {
        this.minJobLevel = minJobLevel;
    }

    public int getMaxJobLevel() {
        return this.maxJobLevel;
    }

    public void setMaxJobLevel(int maxJobLevel) {
        this.maxJobLevel = maxJobLevel;
    }

    @NotNull
    public Set<Material> getDisabledInteractions() {
        return disabledInteractions;
    }

    public boolean isHoursEnabled() {
        return this.hoursEnabled;
    }

    public void setHoursEnabled(boolean hoursEnabled) {
        this.hoursEnabled = hoursEnabled;
    }

    @NotNull
    public Map<DayOfWeek, Hours> getHoursByDayMap() {
        return this.hoursByDayMap;
    }

    @Nullable
    public Hours getHours(@NotNull DayOfWeek day) {
        return this.hoursByDayMap.get(day);
    }

    @NotNull
    public Map<String, BlockList> getBlockListMap() {
        return this.blockListMap;
    }

    @NotNull
    public Set<BlockList> getBlockLists() {
        return new HashSet<>(this.blockListMap.values());
    }

    @Nullable
    public BlockList getBlockList(@NotNull String id) {
        return this.blockListMap.get(id.toLowerCase());
    }

    @Nullable
    public BlockList getBlockList(@NotNull Block block) {
        Material material = block.getType();

        return this.getBlockLists().stream().filter(blockList -> blockList.contains(material)).findFirst().orElse(null);
    }

    @Nullable
    public Modifier getPaymentModifier(@NotNull Currency currency) {
        return this.paymentModifierMap.get(currency.getInternalId());
    }

    @NotNull
    public Map<String, Modifier> getPaymentModifierMap() {
        return this.paymentModifierMap;
    }

    @NotNull
    public Set<String> getPaymentCurrencyIds() {
        return new HashSet<>(this.paymentModifierMap.keySet());
    }

    @NotNull
    public Modifier getXPModifier() {
        return this.xpModifier;
    }

    public void setXPModifier(@NotNull Modifier xpModifier) {
        this.xpModifier = xpModifier;
    }
}
