package su.nightexpress.excellentjobs.job.work;

import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.nightcore.language.LangAssets;
import su.nightexpress.nightcore.util.BukkitThing;

import java.util.function.Function;

@SuppressWarnings("UnstableApiUsage")
public class WorkFormatters {

    public static <T extends Keyed> WorkFormatter<T> forKeyed(@NotNull Registry<T> registry, @NotNull Function<T, String> localized) {

        return new WorkFormatter<>() {

            @Override
            @NotNull
            public String getName(@NotNull T object) {
                return BukkitThing.toString(object);
            }

            @Override
            @NotNull
            public String getLocalized(@NotNull T object) {
                return localized.apply(object);
            }

            @Override
            @Nullable
            public T parseObject(@NotNull String name) {
                return BukkitThing.fromRegistry(registry, name);
            }
        };
    }

    public static final WorkFormatter<Material> MATERIAL = forKeyed(Registry.MATERIAL, LangAssets::get);

    public static final WorkFormatter<EntityType> ENITITY_TYPE = forKeyed(Registry.ENTITY_TYPE, LangAssets::get);

    public static final WorkFormatter<PotionEffectType> EFFECT = forKeyed(Registry.EFFECT, LangAssets::get);

    public static final WorkFormatter<Enchantment> ENCHANTMENT = forKeyed(Registry.ENCHANTMENT, LangAssets::get);

    public static final WorkFormatter<DamageType> DAMAGE_TYPE = forKeyed(Registry.DAMAGE_TYPE, LangAssets::get);
}
