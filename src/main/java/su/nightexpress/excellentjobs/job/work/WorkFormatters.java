package su.nightexpress.excellentjobs.job.work;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.excellentjobs.job.work.wrapper.WrappedEnchant;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.LangUtil;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.bridge.RegistryType;

import java.util.function.Function;

public class WorkFormatters {

    public static <T extends Keyed> WorkFormatter<T> forKeyed(@NotNull RegistryType<T> registry, @NotNull Function<T, String> localized) {

        return new WorkFormatter<>() {

            @Override
            @NotNull
            public String getName(@NotNull T object) {
                return BukkitThing.getValue(object);
            }

            @Override
            @NotNull
            public String getLocalized(@NotNull T object) {
                return localized.apply(object);
            }

            @Override
            @Nullable
            public T parseObject(@NotNull String name) {
                return BukkitThing.getByString(registry, name);
            }
        };
    }

    public static final WorkFormatter<Material> MATERIAL = forKeyed(RegistryType.MATERIAL, LangUtil::getSerializedName);

    public static final WorkFormatter<EntityType> ENITITY_TYPE = forKeyed(RegistryType.ENTITY_TYPE, LangUtil::getSerializedName);

    public static final WorkFormatter<PotionEffectType> EFFECT = forKeyed(RegistryType.MOB_EFFECT, LangUtil::getSerializedName);

    public static final WorkFormatter<Enchantment> ENCHANTMENT = forKeyed(RegistryType.ENCHANTMENT, LangUtil::getSerializedName);

    public static final WorkFormatter<DamageType> DAMAGE_TYPE = forKeyed(RegistryType.DAMAGE_TYPE, LangAssets::get);

    public static final WorkFormatter<WrappedEnchant> WRAPPED_ENCHANTMENT = new WorkFormatter<>() {

        @Override
        @NotNull
        public String getName(@NotNull WrappedEnchant object) {
            return object.serialize();
        }

        @Override
        @NotNull
        public String getLocalized(@NotNull WrappedEnchant object) {
            return LangUtil.getSerializedName(object.getEnchantment()) + " " + NumberUtil.toRoman(object.getLevel());
        }

        @Override
        @Nullable
        public WrappedEnchant parseObject(@NotNull String name) {
            return WrappedEnchant.deserialize(name);
        }
    };
}
