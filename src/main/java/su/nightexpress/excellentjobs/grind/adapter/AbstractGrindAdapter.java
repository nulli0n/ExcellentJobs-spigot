package su.nightexpress.excellentjobs.grind.adapter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGrindAdapter<I, O> implements GrindAdapter<I, O> {

    protected final String name;
    //protected final String key;

    public AbstractGrindAdapter(@NotNull String name/*, @NotNull String key*/) {
        this.name = name.toLowerCase();
        //this.key = key;
    }

    @Override
    @NotNull
    public String getName() {
        return this.name;
    }

    /*@Override
    @NotNull
    public String getKey() {
        return this.key;
    }*/

    @Nullable
    public String toFullNameOfEntity(@NotNull O entity) {
        I type = this.getType(entity);
        if (type == null) return null;

        return this.toFullNameOfType(type);
    }

    @NotNull
    public String toFullNameOfType(@NotNull I type) {
        return this.getName(type);
    }

    @NotNull
    public String toFullName(@NotNull String name) {
        I type = this.getTypeByName(name);
        return type == null ? name : this.toFullNameOfType(type);
    }
}
