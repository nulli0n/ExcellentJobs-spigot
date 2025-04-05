package su.nightexpress.excellentjobs.api;

import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public enum ModifyAction {

    ADD(Integer::sum),
    SET((in, amount) -> amount),
    REMOVE((in, amount) -> in - amount);

    private final BiFunction<Integer, Integer, Integer> function;

    ModifyAction(@NotNull BiFunction<Integer, Integer, Integer> function) {
        this.function = function;
    }

    public int modify(int input, int amount) {
        return this.function.apply(input, amount);
    }
}
