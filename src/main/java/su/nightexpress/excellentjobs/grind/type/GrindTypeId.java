package su.nightexpress.excellentjobs.grind.type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GrindTypeId {

    public static final String BREEDING         = "breeding";
    public static final String BREWING          = "brewing";
    public static final String BUILDING         = "building";
    public static final String COOKING          = "cooking";
    public static final String CRAFTING         = "crafting";
    public static final String ENCHANTING       = "enchanting";
    public static final String FERTILIZING      = "fertilizing";
    public static final String FISHING          = "fishing";
    public static final String FORGING          = "forging";
    public static final String GATHERING        = "gathering";
    public static final String GRINDSTONE       = "grindstone";
    public static final String KILLING          = "killing";
    public static final String MILKING          = "milking";
    public static final String MINING           = "mining";
    public static final String SHEARING         = "shearing";
    public static final String TAMING           = "taming";

    @Nullable
    public static String fromLegacy(@NotNull String legacyName) {
        return switch (legacyName) {
            case "brew_potion" -> BREWING;
            case "block_place" -> BUILDING;
            case "craft_item" -> CRAFTING;
            case "block_fertilize" -> FERTILIZING;
            case "fish_item", "emf_fish_item" -> FISHING;
            case "block_harvest, collect_honey" -> GATHERING;
            case "kill_entity", "kill_mythic_mob" -> KILLING;
            case "milk_entity" -> MILKING;
            case "breed_entity" -> BREEDING;
            case "block_break" -> MINING;
            case "tame_entity" -> TAMING;
            case "repair_item", "rename_item" -> FORGING;
            case "shear_entity" -> SHEARING;
            case "smelt_item" -> COOKING;
            case "get_enchant" -> ENCHANTING;
            case "disenchant_item" -> GRINDSTONE;
            default -> null;
        };
    }
}
