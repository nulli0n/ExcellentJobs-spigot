package su.nightexpress.excellentjobs.grind.adapter;

import su.nightexpress.excellentjobs.grind.adapter.impl.*;

public class GrindAdapters {

    public static final VanillaMobAdapter         VANILLA_MOB         = new VanillaMobAdapter("vanilla_mob");
    public static final VanillaBlockAdapter       VANILLA_BLOCK       = new VanillaBlockAdapter("vanilla_block");
    public static final VanillaBlockStateAdapter  VANILLA_BLOCK_STATE = new VanillaBlockStateAdapter("vanilla_block_state");
    public static final VanillaItemAdapter        VANILLA_ITEM        = new VanillaItemAdapter("vanilla_item");
    public static final VanillaEnchantmentAdapter VANILLA_ENCHANTMENT = new VanillaEnchantmentAdapter("enchantment");

}
