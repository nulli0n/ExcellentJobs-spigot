package su.nightexpress.excellentjobs.zone;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Keys;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.hook.HookId;
import su.nightexpress.excellentjobs.util.Cuboid;
import su.nightexpress.excellentjobs.util.Modifier;
import su.nightexpress.excellentjobs.util.pos.BlockPos;
import su.nightexpress.excellentjobs.zone.command.ZoneCommands;
import su.nightexpress.excellentjobs.zone.editor.*;
import su.nightexpress.excellentjobs.zone.impl.BlockList;
import su.nightexpress.excellentjobs.zone.impl.Selection;
import su.nightexpress.excellentjobs.zone.impl.Zone;
import su.nightexpress.excellentjobs.zone.listener.GenericListener;
import su.nightexpress.excellentjobs.zone.listener.SelectionListener;
import su.nightexpress.excellentjobs.zone.visual.BlockHighlighter;
import su.nightexpress.excellentjobs.zone.visual.BlockPacketsHighlighter;
import su.nightexpress.excellentjobs.zone.visual.BlockProtocolHighlighter;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.*;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ZoneManager extends AbstractManager<JobsPlugin> {

    private final Map<String, Zone>    zoneMap;
    private final Map<UUID, Selection> selectionMap;

    private ZonesEditor     editor;
    private ZoneEditor      settingsEditor;
    private ZoneTimesEditor timesEditor;
    private ModifiersEditor modifiersEditor;
    private BlocksEditor    blocksEditor;
    private BlockListEditor blockListEditor;
    private ModifierEditor  modifierEditor;

    private BlockHighlighter blockHighlighter;

    public ZoneManager(@NotNull JobsPlugin plugin) {
        super(plugin);
        this.zoneMap = new HashMap<>();
        this.selectionMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        this.loadCommands();
        this.loadHighlighter();
        this.loadZones();
        this.loadEditor();

        this.addListener(new GenericListener(this.plugin, this));
        this.addListener(new SelectionListener(this.plugin, this));

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
        this.modifierEditor.clear();

        this.zoneMap.clear();

        if (this.blockHighlighter != null) this.blockHighlighter = null;

        ZoneCommands.unload(this.plugin);
    }

    private void loadCommands() {
        ZoneCommands.load(this.plugin, this);
    }

    private void loadHighlighter() {
        if (Plugins.isInstalled(HookId.PACKET_EVENTS)) {
            this.blockHighlighter = new BlockPacketsHighlighter(this.plugin);
        }
        else if (Plugins.isInstalled(HookId.PROTOCOL_LIB)) {
            this.blockHighlighter = new BlockProtocolHighlighter(this.plugin);
        }
    }

    private void loadEditor() {
        this.editor = new ZonesEditor(this.plugin);
        this.settingsEditor = new ZoneEditor(this.plugin, this);
        this.timesEditor = new ZoneTimesEditor(this.plugin, this);
        this.modifiersEditor = new ModifiersEditor(this.plugin, this);
        this.blocksEditor = new BlocksEditor(this.plugin, this);
        this.blockListEditor = new BlockListEditor(this.plugin, this);
        this.modifierEditor = new ModifierEditor(this.plugin, this);
    }

    private void loadZones() {
        for (File file : FileUtil.getFiles(this.getZonesDirectory())) {
            Zone zone = new Zone(plugin, file);
            this.loadZone(zone);
        }
        this.plugin.info("Loaded " + this.zoneMap.size() + " job zones.");
    }

    private boolean loadZone(@NotNull Zone zone) {
        if (zone.load()) {
            zone.reactivate();
            this.zoneMap.put(zone.getId(), zone);
            return true;
        }
        else this.plugin.error("Zone not loaded: '" + zone.getFile().getName() + "'.");
        return false;
    }

    public boolean defineZone(@NotNull Player player, @NotNull String name) {
        String id = StringUtil.lowerCaseUnderscoreStrict(name);

        if (this.getZoneById(id) != null) {
            Lang.ZONE_ERROR_EXISTS.getMessage().replace(Placeholders.GENERIC_NAME, id).send(player);
            return false;
        }

        Selection selection = this.getSelection(player);
        if (selection == null) {
            Lang.ZONE_ERROR_NO_SELECTION.getMessage().send(player);
            return false;
        }

        Cuboid cuboid = selection.toCuboid();
        if (cuboid == null) {
            Lang.ZONE_ERROR_INCOMPLETE_SELECTION.getMessage().send(player);
            return false;
        }

        World world = player.getWorld();
        Zone zone = this.createZone(world, cuboid, id);

        this.exitSelection(player);
        this.openEditor(player, zone);

        Lang.ZONE_CREATE_SUCCESS.getMessage().replace(zone.replacePlaceholders()).send(player);
        return true;
    }

    @NotNull
    public Zone createZone(@NotNull World world, @NotNull Cuboid cuboid, @NotNull String id) {
        Zone current = this.getZoneById(id);
        if (current != null) return current;

        File file = new File(this.getZonesDirectory(), id + ".yml");
        Zone zone = new Zone(plugin, file);
        zone.setWorldName(world.getName());
        zone.setCuboid(cuboid);
        zone.setName(StringUtil.capitalizeUnderscored(id));
        zone.setMinJobLevel(-1);
        zone.setMaxJobLevel(-1);
        zone.save();

        this.loadZone(zone);
        return zone;
    }

    public boolean deleteZone(@NotNull String id) {
        Zone zone = this.zoneMap.remove(id.toLowerCase());
        return zone != null && zone.getFile().delete();
    }

    @NotNull
    public String getZonesDirectory() {
        return this.plugin.getDataFolder() + Config.DIR_ZONES;
    }

    @NotNull
    public Map<String, Zone> getZoneMap() {
        return zoneMap;
    }

    @NotNull
    public Set<Zone> getZones() {
        return new HashSet<>(this.zoneMap.values());
    }

    @NotNull
    public List<String> getZoneIds() {
        return new ArrayList<>(this.zoneMap.keySet());
    }

    @NotNull
    public Set<Zone> getZones(@NotNull World world) {
        return this.getZones(world.getName());
    }

    @NotNull
    public Set<Zone> getZones(@NotNull String worldName) {
        return this.getZones().stream().filter(zone -> zone.getWorldName().equalsIgnoreCase(worldName)).collect(Collectors.toSet());
    }

    @Nullable
    public Zone getZoneById(@NotNull String id) {
        return this.zoneMap.get(id.toLowerCase());
    }

    @Nullable
    public Zone getZoneByLocation(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return null;

        return this.getZones(world).stream().filter(zone -> zone.contains(location)).findFirst().orElse(null);
    }

    @Nullable
    public Zone getZoneByWandItem(@NotNull ItemStack item) {
        String id = PDCUtil.getString(item, Keys.wandZoneId).orElse(null);
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
        this.editor.open(player, this);
    }

    public void openEditor(@NotNull Player player, @NotNull Zone zone) {
        this.settingsEditor.open(player, zone);
    }

    public void openTimesEditor(@NotNull Player player, @NotNull Zone zone) {
        this.timesEditor.open(player, zone);
    }

    public void openModifiersEditor(@NotNull Player player, @NotNull Zone zone) {
        this.modifiersEditor.open(player, zone);
    }

    public void openModifierEditor(@NotNull Player player, @NotNull Zone zone, @NotNull Modifier modifier) {
        this.modifierEditor.open(player, Pair.of(zone, modifier));
    }

    public void openBlocksEditor(@NotNull Player player, @NotNull Zone zone) {
        this.blocksEditor.open(player, zone);
    }

    public void openBlockListEditor(@NotNull Player player, @NotNull Zone zone, @NotNull BlockList blockList) {
        this.blockListEditor.open(player, Pair.of(zone, blockList));
    }

    public void regenerateBlocks() {
        this.regenerateBlocks(false);
    }

    public void regenerateBlocks(boolean force) {
        this.getZones().forEach(zone -> {
            if (!zone.isActive()) return;

            World world = zone.getWorld();
            if (world == null) return;

            zone.getBlockLists().forEach(blockList -> blockList.regenerateBlocks(world, force));
        });
    }

    @NotNull
    public ItemStack getCuboidWand(@Nullable Zone zone) {
        ItemStack item = new ItemStack(Config.ZONES_WAND_ITEM.get());
        if (zone != null) {
            PDCUtil.set(item, Keys.wandZoneId, zone.getId());
        }
        PDCUtil.set(item, Keys.wandItem, true);
        return item;
    }

    public boolean isCuboidWand(@NotNull ItemStack itemStack) {
        return PDCUtil.getBoolean(itemStack, Keys.wandItem).isPresent();
    }

    public boolean isInSelection(@NotNull Player player) {
        return this.getSelection(player) != null;
    }

    @Nullable
    public Selection getSelection(@NotNull Player player) {
        return this.selectionMap.get(player.getUniqueId());
    }

    @NotNull
    public Selection startSelection(@NotNull Player player, @Nullable Zone zone) {
        Selection selection = new Selection();
        this.selectionMap.put(player.getUniqueId(), selection);

        Players.addItem(player, this.getCuboidWand(zone));

        if (zone != null && this.blockHighlighter != null) {
            this.blockHighlighter.highlightPoints(player, zone.getCuboid());
        }
        if (zone == null) {
            Lang.ZONE_CREATE_INFO.getMessage().send(player);
        }

        return selection;
    }

    public void exitSelection(@NotNull Player player) {
        if (this.isInSelection(player)) {
            Players.takeItem(player, this::isCuboidWand);
            this.selectionMap.remove(player.getUniqueId());
        }
        if (this.blockHighlighter != null) {
            this.blockHighlighter.removeVisuals(player);
        }
    }

    public void selectPosition(@NotNull Player player, @NotNull ItemStack itemStack, @NotNull Location location, @NotNull Action action) {
        if (!this.isCuboidWand(itemStack)) return;

        Selection selection = this.getSelection(player);
        if (selection == null) return;

        BlockPos blockPos = BlockPos.from(location);
        if (action == Action.LEFT_CLICK_BLOCK) {
            selection.setFirst(blockPos);
        }
        else selection.setSecond(blockPos);

        int position = action == Action.LEFT_CLICK_BLOCK ? 1 : 2;
        Lang.ZONE_SELECTION_INFO.getMessage().replace(Placeholders.GENERIC_VALUE, position).send(player);

        Cuboid cuboid = selection.toCuboid();
        Zone zone = this.getZoneByWandItem(itemStack);
        if (cuboid != null && zone != null) {
            World world = player.getWorld();

            zone.deactivate();
            zone.setWorldName(world.getName());
            zone.activate(world);
            zone.setCuboid(cuboid);
            zone.save();

            this.exitSelection(player);
            this.openEditor(player, zone);
        }
        else {
            if (this.blockHighlighter != null) {
                this.blockHighlighter.highlightPoints(player, selection);
            }
        }
    }
}
