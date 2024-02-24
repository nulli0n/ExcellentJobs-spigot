package su.nightexpress.excellentjobs.zone;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.command.zone.ZoneMainCommand;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.util.BlockPos;
import su.nightexpress.excellentjobs.util.Cuboid;
import su.nightexpress.excellentjobs.zone.editor.*;
import su.nightexpress.excellentjobs.zone.impl.BlockList;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.excellentjobs.zone.listener.ZoneGenericListener;
import su.nightexpress.excellentjobs.zone.listener.ZoneSetupListener;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.*;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ZoneManager extends AbstractManager<JobsPlugin> {

    private final Map<String, Zone> zoneMap;

    private ZoneListEditor editor;
    private ZoneSettingsEditor settingsEditor;
    private ZoneTimesEditor timesEditor;
    private ZoneModifiersEditor modifiersEditor;
    private ZoneBlocksEditor    blocksEditor;
    private ZoneBlockListEditor blockListEditor;

    public ZoneManager(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.zoneMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        for (File file : FileUtil.getFiles(plugin.getDataFolder() + Config.DIR_ZONES, false)) {
            Zone zone = new Zone(plugin, file);
            this.loadZone(zone);
        }
        this.plugin.info("Loaded " + this.getZoneMap().size() + " job zones.");

        this.editor = new ZoneListEditor(this.plugin);
        this.settingsEditor = new ZoneSettingsEditor(this.plugin, this);
        this.timesEditor = new ZoneTimesEditor(this.plugin, this);
        this.modifiersEditor = new ZoneModifiersEditor(this.plugin, this);
        this.blocksEditor = new ZoneBlocksEditor(this.plugin, this);
        this.blockListEditor = new ZoneBlockListEditor(this.plugin, this);

        this.plugin.getBaseCommand().addChildren(new ZoneMainCommand(this.plugin, this));

        this.addListener(new ZoneGenericListener(this.plugin, this));
        this.addListener(new ZoneSetupListener(this.plugin, this));

        this.addTask(this.plugin.createTask(this::regenerateBlocks).setSecondsInterval(Config.ZONES_REGENERATION_TASK_INTERVAL.get()));
    }

    @Override
    protected void onShutdown() {
        this.regenerateBlocks(true);

        this.editor.clear();
        this.settingsEditor.clear();
        this.timesEditor.clear();
        this.modifiersEditor.clear();
        this.blocksEditor.clear();
        this.blockListEditor.clear();

        this.getZoneMap().clear();
    }

    private boolean loadZone(@NotNull Zone zone) {
        if (zone.load()) {
            this.getZoneMap().put(zone.getId(), zone);
        }
        else this.plugin.error("Zone not loaded: '" + zone.getFile().getName() + "'.");
        return false;
    }

    @NotNull
    public ZoneListEditor getEditor() {
        return editor;
    }

    @NotNull
    public ZoneSettingsEditor getSettingsEditor() {
        return settingsEditor;
    }

    @NotNull
    public ZoneTimesEditor getTimesEditor() {
        return timesEditor;
    }

    @NotNull
    public ZoneModifiersEditor getModifiersEditor() {
        return modifiersEditor;
    }

    @NotNull
    public ZoneBlocksEditor getBlocksEditor() {
        return blocksEditor;
    }

    @NotNull
    public ZoneBlockListEditor getBlockListEditor() {
        return blockListEditor;
    }

    @NotNull
    public Map<String, Zone> getZoneMap() {
        return zoneMap;
    }

    @NotNull
    public Collection<Zone> getZones() {
        return this.getZoneMap().values();
    }

    @NotNull
    public List<String> getZoneIds() {
        return new ArrayList<>(this.getZoneMap().keySet());
    }

    @NotNull
    public Collection<Zone> getZones(@NotNull World world) {
        return this.getZones().stream().filter(zone -> zone.hasSelection() && zone.getWorld() == world).collect(Collectors.toSet());
    }

    @Nullable
    public Zone getZoneById(@NotNull String id) {
        return this.getZoneMap().get(id.toLowerCase());
    }

    @Nullable
    public Zone getZoneByLocation(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        return this.getZones(world).stream().filter(zone -> zone.contains(location)).findFirst().orElse(null);
    }

    @Nullable
    public Zone getZoneByWandItem(@NotNull ItemStack item) {
        String id = PDCUtil.getString(item, Keys.WAND_ITEM_ZONE_ID).orElse(null);
        return id == null ? null : this.getZoneById(id);
    }

    @Nullable
    public Zone getZone(@NotNull Entity entity) {
        return this.getZoneByLocation(entity.getLocation());
    }

    @Nullable
    public Zone getZone(@NotNull Block block) {
        return this.getZoneByLocation(block.getLocation());
    }

    public boolean isInZone(@NotNull Block block) {
        return this.getZone(block) != null;
    }

    public boolean isInZone(@NotNull Entity entity) {
        return this.getZone(entity) != null;
    }

    public void openEditor(@NotNull Player player) {
        this.getEditor().open(player, this);
    }

    public void openEditor(@NotNull Player player, @NotNull Zone zone) {
        this.getSettingsEditor().open(player, zone);
    }

    public void openTimesEditor(@NotNull Player player, @NotNull Zone zone) {
        this.getTimesEditor().open(player, zone);
    }

    public void openModifiersEditor(@NotNull Player player, @NotNull Zone zone) {
        this.getModifiersEditor().open(player, zone);
    }

    public void openBlocksEditor(@NotNull Player player, @NotNull Zone zone) {
        this.getBlocksEditor().open(player, zone);
    }

    public void openBlockListEditor(@NotNull Player player, @NotNull Zone zone, @NotNull BlockList blockList) {
        this.getBlockListEditor().open(player, Pair.of(zone, blockList));
    }

    public void giveWand(@NotNull Player player, @NotNull Zone zone) {
        zone.highlightPoints(player);
        Players.addItem(player, zone.getCuboidSelector());
    }

    public void regenerateBlocks() {
        this.regenerateBlocks(false);
    }

    public void regenerateBlocks(boolean force) {
        this.getZones().forEach(zone -> {
            if (!zone.hasSelection()) return;

            World world = zone.getWorld();
            zone.getBlockLists().forEach(blockList -> blockList.regenerateBlocks(world, force));
        });
    }

    public boolean createZone(@NotNull String id) {
        return this.createZone(id, zone -> {});
    }

    public boolean createZone(@NotNull String id, @NotNull Consumer<Zone> consumer) {
        id = StringUtil.lowerCaseUnderscoreStrict(id);

        if (this.getZoneById(id) != null) return false;

        File file = new File(plugin.getDataFolder() + Config.DIR_ZONES, id + ".yml");
        if (file.exists()) return false;

        FileUtil.create(file);
        Zone zone = new Zone(plugin, file);
        //zone.setWorld(world);
        zone.setCuboid(new Cuboid(BlockPos.empty(), BlockPos.empty()));
        zone.setName(StringUtil.capitalizeUnderscored(id));
        zone.setMinJobLevel(-1);
        zone.setMaxJobLevel(-1);
        consumer.accept(zone);
        zone.save();

        this.loadZone(zone);
        return true;
    }

    public boolean deleteZone(@NotNull String id) {
        Zone zone = this.getZoneMap().remove(id.toLowerCase());
        return zone != null && zone.getFile().delete();
    }
}
