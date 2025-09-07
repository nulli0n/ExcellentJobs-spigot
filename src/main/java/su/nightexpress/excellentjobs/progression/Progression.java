package su.nightexpress.excellentjobs.progression;

public interface Progression {

    int    INITIAL_XP            = 900;
    double XP_FACTOR             = 1.091001D;
    int    DEFAULT_MAX_JOB_LEVEL = 100;
    double DEFAULT_XP_SCALE      = 5D;

    boolean canProgress();

    int getStartLevel();

    int getStartXP();

    int getXPToLevel(int level);

    int getInitialXP();

    double getXPFactor();

    int getMaxLevel();
}
