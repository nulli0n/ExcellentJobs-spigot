package su.nightexpress.excellentjobs.action;

import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.JobsAPI;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.StringUtil;

public class ObjectFormatters {

    public static final ObjectFormatter<Material> MATERIAL = new ObjectFormatter<>() {

        @NotNull
        @Override
        public String getName(@NotNull Material object) {
            return object.getKey().getKey();
        }

        @NotNull
        @Override
        public String getLocalizedName(@NotNull Material material) {
            return LangAssets.get(material);
        }

        @Nullable
        @Override
        public Material parseObject(@NotNull String name) {
            return BukkitThing.getMaterial(name);
        }
    };

    public static final ObjectFormatter<EntityType> ENITITY_TYPE = new ObjectFormatter<>() {
        @NotNull
        @Override
        public String getName(@NotNull EntityType type) {
            return type.getKey().getKey();//type.name();
        }

        @NotNull
        @Override
        public String getLocalizedName(@NotNull EntityType type) {
            return LangAssets.get(type);
        }

        @Nullable
        @Override
        public EntityType parseObject(@NotNull String name) {
            return BukkitThing.getEntityType(name);
            //return StringUtil.getEnum(name, EntityType.class).orElse(null);
        }
    };

    public static final ObjectFormatter<PotionEffectType> POTION_TYPE = new ObjectFormatter<>() {
        @NotNull
        @Override
        public String getName(@NotNull PotionEffectType object) {
            return object.getKey().getKey();// object.getName();
        }

        @NotNull
        @Override
        public String getLocalizedName(@NotNull PotionEffectType object) {
            return LangAssets.get(object);
        }

        @Nullable
        @Override
        public PotionEffectType parseObject(@NotNull String name) {
            PotionEffectType effectType = BukkitThing.fromRegistry(Registry.EFFECT, name);
            if (effectType == null) {
                return PotionEffectType.getByName(name.toUpperCase());
            }
            return effectType;
        }
    };

    public static final ObjectFormatter<Enchantment> ENCHANTMENT = new ObjectFormatter<>() {
        @NotNull
        @Override
        public String getName(@NotNull Enchantment object) {
            return object.getKey().getKey();
        }

        @NotNull
        @Override
        public String getLocalizedName(@NotNull Enchantment object) {
            return LangAssets.get(object);
        }

        @Nullable
        @Override
        public Enchantment parseObject(@NotNull String name) {
            return BukkitThing.getEnchantment(name);
            //return Enchantment.getByKey(NamespacedKey.minecraft(name.toLowerCase()));
        }
    };

    public static final ObjectFormatter<EntityDamageEvent.DamageCause> DAMAGE_CAUSE = new ObjectFormatter<>() {
        @NotNull
        @Override
        public String getName(@NotNull EntityDamageEvent.DamageCause object) {
            return object.name();
        }

        @NotNull
        @Override
        public String getLocalizedName(@NotNull EntityDamageEvent.DamageCause object) {
            return JobsAPI.PLUGIN.getLangManager().getEnum(object);
        }

        @Nullable
        @Override
        public EntityDamageEvent.DamageCause parseObject(@NotNull String name) {
            return StringUtil.getEnum(name, EntityDamageEvent.DamageCause.class).orElse(null);
        }
    };
}
