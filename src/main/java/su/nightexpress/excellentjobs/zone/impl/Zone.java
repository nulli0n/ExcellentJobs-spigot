package su.nightexpress.excellentjobs.zone.impl;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.api.currency.Currency;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.data.impl.JobUser;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.util.*;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractFileData;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.placeholder.Placeholder;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Zone extends AbstractFileData<JobsPlugin> implements Placeholder, Inspectable {

    private final Map<DayOfWeek, List<Pair<LocalTime, LocalTime>>> openTimes;
    private final Map<String, BlockList>                           blockListMap;
    private final Map<Currency, Modifier>                          paymentModifierMap;
    private final PlaceholderMap                                   placeholderMap;

    private World    world;
    private Cuboid   cuboid;
    private Job      linkedJob;
    private int      minJobLevel;
    private int      maxJobLevel;
    private String   name;
    private List<String> description;
    private ItemStack icon;
    private boolean  permissionRequired;
    private boolean pvpAllowed;
    private Modifier xpModifier;

    public Zone(@NotNull JobsPlugin plugin, @NotNull File file) {
        super(plugin, file);
        this.openTimes = new HashMap<>();
        this.blockListMap = new HashMap<>();
        this.paymentModifierMap = new HashMap<>();
        this.xpModifier = Modifier.add(0, 0, 0);
        this.icon = new ItemStack(Material.MAP);

        this.placeholderMap = Placeholders.forZone(this);
    }

    @Override
    protected boolean onLoad(@NotNull FileConfig config) {
        this.world = plugin.getServer().getWorld(config.getString("Bounds.World", "null"));
        if (this.world == null) {
            this.plugin.warn("Invalid world in zone '" + this.getFile().getName() + "'!");
            //return false;
        }

        BlockPos pos1 = BlockPos.read(config, "Bounds.P1");
        BlockPos pos2 = BlockPos.read(config, "Bounds.P2");
        this.setCuboid(new Cuboid(pos1, pos2));

        String jobId = ConfigValue.create("Job.Id", "null").read(config);
        Job job = this.plugin.getJobManager().getJobById(jobId);
        if (job == null) {
            this.plugin.warn("Invalid job '" + jobId + "' in zone '" + this.getFile().getName() + "'!");
            //return false;
        }

        this.setLinkedJob(job);
        this.setMinJobLevel(config.getInt("Job.Min_Level", -1));
        this.setMaxJobLevel(config.getInt("Job.Max_Level", -1));

        this.setName(config.getString("Name", StringUtil.capitalizeFully(this.getId())));
        this.setDescription(config.getStringList("Description"));
        this.setIcon(ConfigValue.create("Icon", new ItemStack(Material.MAP)).read(config));
        this.setPermissionRequired(config.getBoolean("Permission_Required"));
        this.setPvPAllowed(config.getBoolean("PvP_Allowed"));

        config.getSection("Open_Times").forEach(dayName -> {
            DayOfWeek day = StringUtil.getEnum(dayName, DayOfWeek.class).orElse(null);
            if (day == null) return;

            config.getStringList("Open_Times." + dayName).forEach(raw -> {
                var times = JobUtils.parseTimes(raw);
                if (times == null) return;

                this.getOpenTimes(day).add(times);
            });
        });

        config.getSection("Block_Lists.").forEach(id -> {
            BlockList blockList = BlockList.read(config, "Block_Lists." + id, id);
            this.getBlockListMap().put(blockList.getId(), blockList);
        });

        config.getSection("Payment_Modifier").forEach(curId -> {
            Currency currency = this.plugin.getCurrencyManager().getCurrency(curId);
            if (currency == null) {
                this.plugin.warn("Invalid currency '" + curId + "' in zone '" + this.getFile().getName() + "'!");
                return;
            }

            Modifier modifier = Modifier.read(config, "Payment_Modifier." + curId);
            this.getPaymentModifierMap().put(currency, modifier);
        });

        this.setXPModifier(Modifier.read(config, "XP_Modifier"));

        return true;
    }

    @Override
    protected void onSave(@NotNull FileConfig config) {
        if (this.world != null) {
            config.set("Bounds.World", this.getWorld().getName());
        }
        this.getCuboid().getMin().write(config, "Bounds.P1");
        this.getCuboid().getMax().write(config, "Bounds.P2");
        config.set("Name", this.getName());
        config.set("Description", this.getDescription());
        config.setItem("Icon", this.getIcon());
        config.set("Permission_Required", this.isPermissionRequired());
        config.set("PvP_Allowed", this.isPvPAllowed());
        config.remove("Open_Times");
        this.getOpenTimes().forEach((day, times) -> {
            config.set("Open_Times." + day.name(), times.stream().map(JobUtils::serializeTimes).toList());
        });
        config.remove("Block_Lists");
        this.getBlockLists().forEach(blockList -> blockList.write(config, "Block_Lists." + blockList.getId()));
        if (this.linkedJob != null) {
            config.set("Job.Id", this.getLinkedJob().getId());
        }
        config.set("Job.Min_Level", this.getMinJobLevel());
        config.set("Job.Max_Level", this.getMaxJobLevel());
        config.remove("Payment_Modifier");
        this.getPaymentModifierMap().forEach((currency, mod) -> {
            mod.write(config, "Payment_Modifier." + currency.getId());
        });
        this.getXPModifier().write(config, "XP_Modifier");
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @Override
    @NotNull
    public Report getReport() {
        Report report = new Report();

        if (!this.hasSelection()) {
            report.addProblem("Invalid or incomplete selection!");
        }
        if (this.linkedJob == null) {
            report.addProblem("Invalid job assigned!");
        }

        return report;
    }

    public boolean hasSelection() {
        return this.world != null && !this.getCuboid().isEmpty();
    }

    public void highlightPoints(@NotNull Player player) {
        if (this.world == null) return;

        Visuals.highlightPoints(player, this.world, new BlockPos[]{this.getCuboid().getMin(), this.getCuboid().getMax()});
    }

    @Nullable
    public BlockList getBlockList(@NotNull String id) {
        return this.getBlockListMap().get(id.toLowerCase());
    }

    @Nullable
    public BlockList getBlockList(@NotNull Block block) {
        Material material = block.getType();

        return this.getBlockLists().stream().filter(blockList -> blockList.contains(material)).findFirst().orElse(null);
    }

    public boolean contains(@NotNull Location location) {
        if (!this.hasSelection()) return false;

        return this.getCuboid().contains(BlockPos.from(location));
    }

    public boolean isAvailable(@NotNull Player player) {
        if (player.hasPermission(Perms.BYPASS_ZONE_ACCESS)) return true;
        if (!this.isGoodTime()) return false;
        if (!this.hasPermission(player)) return false;

        if (this.getLinkedJob() != null) {
            JobUser user = this.plugin.getUserManager().getUserData(player);
            JobData jobData = user.getData(this.getLinkedJob());
            if (jobData.getState() == JobState.INACTIVE) return false;

            return this.isGoodLevel(jobData.getLevel());
        }

        return true;
    }

    public boolean isGoodJob(@NotNull Job job) {
        return this.getLinkedJob() == job;
    }

    public boolean isGoodLevel(int level) {
        int min = this.getMinJobLevel();
        if (min > 0 && level < min) return false;

        int max = this.getMaxJobLevel();
        return max <= 0 || level < max;
    }

    public boolean isLevelRequired() {
        return this.getMinJobLevel() > 0 || this.getMaxJobLevel() > 0;
    }

    public boolean isGoodTime() {
        if (this.getOpenTimes().isEmpty()) return true;

        return this.getCurrentOpenTimes() != null;

        /*if (this.getOpenTimes().isEmpty()) return true;

        DayOfWeek day = LocalDate.now().getDayOfWeek();
        var times = this.getOpenTimes().getOrDefault(day, Collections.emptySet());
        if (times.isEmpty()) return false;

        LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        return times.stream().anyMatch(pair -> time.isAfter(pair.getFirst()) && time.isBefore(pair.getSecond()));*/
    }

    public boolean hasPermission(@NotNull Player player) {
        return !this.isPermissionRequired() || (player.hasPermission(this.getPermission()) || player.hasPermission(Perms.ZONE));
    }

    @Nullable
    public Pair<LocalTime, LocalTime> getCurrentOpenTimes() {
        if (this.getOpenTimes().isEmpty()) return null;

        DayOfWeek day = LocalDate.now().getDayOfWeek();
        List<Pair<LocalTime, LocalTime>> times = this.getOpenTimes(day);
        if (times.isEmpty()) return null;

        LocalTime time = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
        return times.stream().filter(pair -> time.isAfter(pair.getFirst()) && time.isBefore(pair.getSecond())).findFirst().orElse(null);
    }

    @Nullable
    public LocalTime getNearestOpenTime() {
        if (this.getOpenTimes().isEmpty()) return null;

        LocalDate date = LocalDate.now();
        DayOfWeek day = date.getDayOfWeek();
        while (!this.getOpenTimes().containsKey(day)) {
            date = date.plusDays(1);
            day = date.getDayOfWeek();
        }

        List<Pair<LocalTime, LocalTime>> times = this.getOpenTimes(day);
        if (times.isEmpty()) return null;

        return times.stream().map(Pair::getFirst).min(LocalTime::compareTo).orElse(null);
        //return LocalDateTime.of(date, time);
    }

    @NotNull
    public String getPermission() {
        return Perms.PREFIX_ZONE + this.getId();
    }

    @NotNull
    public ItemStack getCuboidSelector() {
        ItemStack item = new ItemStack(Material.GOLDEN_AXE);
        ItemReplacer.create(item).trimmed().hideFlags().readLocale(Lang.EDITOR_ZONE_WAND_ITEM).writeMeta();
        PDCUtil.set(item, Keys.WAND_ITEM_ZONE_ID, this.getId());
        return item;
    }

    @Nullable
    public Modifier getPaymentModifier(@NotNull Currency currency) {
        return this.getPaymentModifierMap().get(currency);
    }

    /*public Location getLocation(@NotNull BlockPos pos) {
        if (this.world == null) return null;

        return pos.toLocation(this.getWorld());
    }*/

    public World getWorld() {
        return world;
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
    }

    @NotNull
    public Cuboid getCuboid() {
        return cuboid;
    }

    public void setCuboid(@NotNull Cuboid cuboid) {
        this.cuboid = cuboid;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public List<String> getDescription() {
        return description;
    }

    public void setDescription(@NotNull List<String> description) {
        this.description = description;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
        ItemUtil.editMeta(this.icon, meta -> meta.addItemFlags(ItemFlag.values()));
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
    public Map<DayOfWeek, List<Pair<LocalTime, LocalTime>>> getOpenTimes() {
        return openTimes;
    }

    @NotNull
    public List<Pair<LocalTime, LocalTime>> getOpenTimes(@NotNull DayOfWeek day) {
        return this.getOpenTimes().computeIfAbsent(day, k -> new ArrayList<>());
    }

    @NotNull
    public Map<String, BlockList> getBlockListMap() {
        return blockListMap;
    }

    @NotNull
    public Collection<BlockList> getBlockLists() {
        return this.getBlockListMap().values();
    }

    public Job getLinkedJob() {
        return linkedJob;
    }

    public void setLinkedJob(@Nullable Job linkedJob) {
        this.linkedJob = linkedJob;
    }

    public int getMinJobLevel() {
        return minJobLevel;
    }

    public void setMinJobLevel(int minJobLevel) {
        this.minJobLevel = minJobLevel;
    }

    public int getMaxJobLevel() {
        return maxJobLevel;
    }

    public void setMaxJobLevel(int maxJobLevel) {
        this.maxJobLevel = maxJobLevel;
    }

    @NotNull
    public Map<Currency, Modifier> getPaymentModifierMap() {
        return paymentModifierMap;
    }

    @NotNull
    public Modifier getXPModifier() {
        return xpModifier;
    }

    public void setXPModifier(@NotNull Modifier xpModifier) {
        this.xpModifier = xpModifier;
    }
}
