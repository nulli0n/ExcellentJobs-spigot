package su.nightexpress.excellentjobs.util;

public class Calc {

    public static double toMult(double value) {
        return (1D + toRawMod(value));
    }

    public static double toRawMod(double value) {
        return value / 100D;
    }

    public static double toMult(double... values) {
        double total = 0D;

        for (double value : values) {
            total += value;
        }

        return toMult(total);
    }
}
