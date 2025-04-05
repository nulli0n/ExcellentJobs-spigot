package su.nightexpress.excellentjobs.config;

import org.bukkit.Sound;
import su.nightexpress.excellentjobs.booster.impl.BoosterType;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.zone.command.ZoneCommands;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.*;
import su.nightexpress.nightcore.language.message.OutputType;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.OUTPUT;
import static su.nightexpress.nightcore.language.tag.MessageTags.SOUND;
import static su.nightexpress.nightcore.util.Placeholders.GENERIC_ENTRY;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Lang extends CoreLang {

    public static final LangEnum<JobState>    JOB_STATE    = LangEnum.of("Job.State", JobState.class);
    public static final LangEnum<BoosterType> BOOSTER_TYPE = LangEnum.of("BoosterType", BoosterType.class);

    public static final LangString COMMAND_ARGUMENT_NAME_ZONE           = LangString.of("Command.Argument.Name.Zone", "zone");
    public static final LangString COMMAND_ARGUMENT_NAME_CURRENCY       = LangString.of("Command.Argument.Name.Currency", "currency");
    public static final LangString COMMAND_ARGUMENT_NAME_JOB            = LangString.of("Command.Argument.Name.Job", "job");
    public static final LangString COMMAND_ARGUMENT_NAME_JOB_STATE      = LangString.of("Command.Argument.Name.JobState", "job state");
    public static final LangString COMMAND_ARGUMENT_NAME_XP_MULTIPLIER  = LangString.of("Command.Argument.Name.XPMultiplier", "xp mult");
    public static final LangString COMMAND_ARGUMENT_NAME_PAY_MULTIPLIER = LangString.of("Command.Argument.Name.PayMultiplier", "pay mult");
    public static final LangString COMMAND_ARGUMENT_NAME_DURATION       = LangString.of("Command.Argument.Name.Duration", "duration");
    public static final LangString COMMAND_ARGUMENT_NAME_PAGE           = LangString.of("Command.Argument.Name.Page", "page");
    public static final LangString COMMAND_ARGUMENT_NAME_ACTION         = LangString.of("Command.Argument.Name.Action", "action");

    public static final LangString COMMAND_ZONE_DESC        = LangString.of("Command.Zone.Desc", "Zone commands.");
    public static final LangString COMMAND_ZONE_CREATE_DESC = LangString.of("Command.Zone.Create.Desc", "Create a new zone from selection.");
    public static final LangString COMMAND_ZONE_WAND_DESC   = LangString.of("Command.Zone.Wand.Desc", "Get zone selection tool.");
    public static final LangString COMMAND_ZONE_EDITOR_DESC = LangString.of("Command.Zone.Editor.Desc", "Open zones editor.");

    public static final LangString COMMAND_LEVEL_DESC      = LangString.of("Command.Level.Desc", "Level management.");
    public static final LangString COMMAND_XP_DESC         = LangString.of("Command.XP.Desc", "XP management.");
    public static final LangString COMMAND_RESET_DESC      = LangString.of("Command.Reset.Desc", "Reset job progress.");
    public static final LangString COMMAND_MENU_DESC       = LangString.of("Command.Menu.Desc", "Open Jobs GUI.");
    public static final LangString COMMAND_OBJECTIVES_DESC = LangString.of("Command.Objectives.Desc", "Browse job objectives.");
    public static final LangString COMMAND_JOIN_DESC       = LangString.of("Command.Join.Desc", "Join a job.");
    public static final LangString COMMAND_LEAVE_DESC      = LangString.of("Command.Leave.Desc", "Leave a job.");
    public static final LangString COMMAND_SET_STATE_DESC  = LangString.of("Command.SetState.Desc", "Set player's job state.");
    public static final LangString COMMAND_TOP_DESC        = LangString.of("Command.Top.Desc", "List most levelled players.");
    public static final LangString COMMAND_STATS_DESC      = LangString.of("Command.Stats.Desc", "View job stats.");

    public static final LangString COMMAND_BOOSTS_DESC           = LangString.of("Command.Boosters.Desc", "View all current boosters.");
    public static final LangString COMMAND_BOOSTER_DESC          = LangString.of("Command.Booster.Desc", "Booster management.");
    public static final LangString COMMAND_BOOSTER_ACTIVATE_DESC = LangString.of("Command.Booster.Activate.Desc", "Activate global scheduled booster.");
    public static final LangString COMMAND_BOOSTER_CREATE_DESC   = LangString.of("Command.Booster.Create.Desc", "Create global or player booster.");
    public static final LangString COMMAND_BOOSTER_INFO_DESC     = LangString.of("Command.Booster.Info.Desc", "View active booster names.");
    public static final LangString COMMAND_BOOSTER_REMOVE_DESC   = LangString.of("Command.Booster.Removal.Desc", "Remove global or personal booster.");

    public static final LangText COMMAND_STATS_DISPLAY = LangText.of("Command.Stats.Display.Info",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.wrap(ORANGE.wrap(PLAYER_NAME) + "'s Job Stats:"),
        " ",
        GENERIC_ENTRY,
        " "
    );

    public static final LangString COMMAND_STATS_ENTRY = LangString.of("Command.Stats.Display.Job",
        LIGHT_GRAY.wrap(ORANGE.wrap("▪ ") + JOB_NAME + ":   Level: " + ORANGE.wrap(JOB_DATA_LEVEL) + ", XP: " + ORANGE.wrap(JOB_DATA_XP) + "/" + ORANGE.wrap(JOB_DATA_XP_MAX))
    );


    public static final LangText COMMAND_XP_ADD_DONE = LangText.of("Command.XP.Add.Done",
        LIGHT_GRAY.wrap("Added " + LIGHT_GREEN.wrap(GENERIC_AMOUNT) + " XP to " + LIGHT_GREEN.wrap(PLAYER_NAME) + "'s " + LIGHT_GREEN.wrap(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_XP_ADD_NOTIFY = LangText.of("Command.XP.Add.Notify",
        LIGHT_GRAY.wrap(LIGHT_GREEN.wrap(GENERIC_AMOUNT) + " XP has been added to your " + LIGHT_GREEN.wrap(JOB_NAME) + " job!")
    );

    public static final LangText COMMAND_XP_REMOVE_DONE = LangText.of("Command.XP.Remove.Done",
        LIGHT_GRAY.wrap("Removed " + LIGHT_RED.wrap(GENERIC_AMOUNT) + " XP from " + LIGHT_RED.wrap(PLAYER_NAME) + "'s " + LIGHT_RED.wrap(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_XP_REMOVE_NOTIFY = LangText.of("Command.XP.Remove.Notify",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_AMOUNT) + " XP has been removed from your " + LIGHT_RED.wrap(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_XP_SET_DONE = LangText.of("Command.XP.Set.Done",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " XP for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_XP_SET_NOTIFY = LangText.of("Command.XP.Set.Notify",
        LIGHT_GRAY.wrap("Your " + LIGHT_YELLOW.wrap(JOB_NAME) + " job XP has been set to " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + ".")
    );


    public static final LangText COMMAND_LEVEL_ADD_DONE = LangText.of("Command.Level.Add.Done",
        LIGHT_GRAY.wrap("Added " + LIGHT_GREEN.wrap(GENERIC_AMOUNT) + " level(s) to " + LIGHT_GREEN.wrap(PLAYER_NAME) + "'s " + LIGHT_GREEN.wrap(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_LEVEL_ADD_NOTIFY = LangText.of("Command.Level.Add.Notify",
        LIGHT_GRAY.wrap(LIGHT_GREEN.wrap(GENERIC_AMOUNT) + " level(s) has been added to your " + LIGHT_GREEN.wrap(JOB_NAME) + " job!")
    );

    public static final LangText COMMAND_LEVEL_REMOVE_DONE = LangText.of("Command.Level.Remove.Done",
        LIGHT_GRAY.wrap("Removed " + LIGHT_RED.wrap(GENERIC_AMOUNT) + " level(s) from " + LIGHT_RED.wrap(PLAYER_NAME) + "'s " + LIGHT_RED.wrap(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_LEVEL_REMOVE_NOTIFY = LangText.of("Command.Level.Remove.Notify",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_AMOUNT) + " level(s) has been removed from your " + LIGHT_RED.wrap(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_LEVEL_SET_DONE = LangText.of("Command.Level.Set.Done",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " level for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_LEVEL_SET_NOTIFY = LangText.of("Command.Level.Set.Notify",
        LIGHT_GRAY.wrap("Your " + LIGHT_YELLOW.wrap(JOB_NAME) + " job level has been set to " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + ".")
    );


    public static final LangText COMMAND_RESET_DONE = LangText.of("Command.Reset.Done",
        LIGHT_GRAY.wrap("Successfully reset " + LIGHT_YELLOW.wrap(JOB_NAME) + " progress for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + ".")
    );


    public static final LangText COMMAND_SET_STATE_DONE = LangText.of("Command.SetState.Done",
        LIGHT_GRAY.wrap("Set " + LIGHT_YELLOW.wrap(GENERIC_STATE) + " state for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(JOB_NAME) + " job!"));


    public static final LangText COMMAND_TOP_LIST = LangText.of("Command.Top.List",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.wrap(BOLD.wrap(JOB_NAME + " Level Top:")),
        " ",
        GENERIC_ENTRY,
        " ",
        LIGHT_GRAY.wrap("Page " + LIGHT_YELLOW.wrap(GENERIC_CURRENT) + " of " + LIGHT_YELLOW.wrap(GENERIC_MAX) + "."),
        " "
    );

    public static final LangString COMMAND_TOP_ENTRY = LangString.of("Command.Top.Entry",
        LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap(GENERIC_POS + ". ") + PLAYER_NAME + ": " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT) + " Levels")
    );


    public static final LangText COMMAND_BOOSTER_ACTIVATE_DONE = LangText.of("Command.Booster.Activate.Done",
        LIGHT_GRAY.wrap("Booster activated!")
    );

    public static final LangText COMMAND_BOOSTER_CREATE_DONE_GLOBAL = LangText.of("Command.Booster.Create.Done.Global",
        LIGHT_GRAY.wrap("Added global " + LIGHT_YELLOW.wrap(BOOSTER_XP_PERCENT + " XP") + " " + LIGHT_YELLOW.wrap(BOOSTER_INCOME_PERCENT + " Payment") + " job booster " + GRAY.wrap("(" + WHITE.wrap(GENERIC_TIME) + ")"))
    );

    public static final LangText COMMAND_BOOSTER_CREATE_DONE_PERSONAL = LangText.of("Command.Booster.Create.Done.Personal",
        LIGHT_GRAY.wrap("Added personal " + LIGHT_YELLOW.wrap(BOOSTER_XP_PERCENT + " XP") + " " + LIGHT_YELLOW.wrap(BOOSTER_INCOME_PERCENT + " Payment") + " job booster " + GRAY.wrap("(" + WHITE.wrap(GENERIC_TIME) + ")") + " for " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_BOOSTER_REMOVE_DONE_PERSONAL = LangText.of("Command.Booster.Remove.Done.Personal",
        LIGHT_GRAY.wrap("Disabled " + LIGHT_YELLOW.wrap(PLAYER_NAME) + "'s " + LIGHT_YELLOW.wrap(JOB_NAME) + " job booster.")
    );

    public static final LangText COMMAND_BOOSTER_REMOVE_DONE_GLOBAL = LangText.of("Command.Booster.Remove.Done.Global",
        LIGHT_GRAY.wrap("Disabled global job booster.")
    );

    public static final LangText COMMAND_BOOSTER_REMOVE_ERROR_NOTHING = LangText.of("Command.Booster.Remove.Error.Nothing",
        LIGHT_RED.wrap("There is no booster.")
    );



    public static final LangText JOB_JOIN_ERROR_ALREADY_HIRED = LangText.of("Job.Join.Error.AlreadyHired",
        LIGHT_GRAY.wrap("You're already hired for the " + LIGHT_RED.wrap(JOB_NAME) + " job!"));

    public static final LangText JOB_JOIN_ERROR_LIMIT_STATE = LangText.of("Job.Join.Error.StateLimit",
        LIGHT_GRAY.wrap("You can't get more than " + LIGHT_RED.wrap(GENERIC_AMOUNT + " " + GENERIC_STATE) + " jobs!"));

    public static final LangText JOB_JOIN_ERROR_LIMIT_GENERAL = LangText.of("Job.Join.Error.GeneralLimit",
        LIGHT_GRAY.wrap("You can't get more jobs!"));

    public static final LangText JOB_JOIN_SUCCESS = LangText.of("Job.Join.Success",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_PLAYER_LEVELUP) +
            LIGHT_GREEN.wrap(BOLD.wrap("Hired!")),
        LIGHT_GRAY.wrap("You're hired for the " + LIGHT_GREEN.wrap(JOB_NAME) + " job!")
    );

    public static final LangText JOB_LEAVE_SUCCESS = LangText.of("Job.Leave.Success",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.BLOCK_WOODEN_DOOR_CLOSE) +
            LIGHT_GREEN.wrap(BOLD.wrap("Job Quit")),
        LIGHT_GRAY.wrap("You quit the " + LIGHT_GREEN.wrap(JOB_NAME) + " job!")
    );

    public static final LangText JOB_LEAVE_ERROR_NOT_JOINED = LangText.of("Job.Leave.Error.NotJoined",
        LIGHT_GRAY.wrap("You're not employed for the " + LIGHT_RED.wrap(JOB_NAME) + " job!"));

    public static final LangText JOB_LEAVE_ERROR_NOT_ALLOWED = LangText.of("Job.Leave.Error.NotAllowed",
        LIGHT_GRAY.wrap("You can't leave the " + LIGHT_RED.wrap(JOB_NAME) + " job!"));

    public static final LangText JOB_RESET_NOTIFY = LangText.of("Job.Reset.Notify",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR) +
            LIGHT_GREEN.wrap(BOLD.wrap("Job Reset!")),
        LIGHT_GRAY.wrap("All " + LIGHT_GREEN.wrap(JOB_NAME) + " progress have been reset!")
    );

    public static final LangText JOB_PAYMENT_RECEIPT = LangText.of("Job.Payment.Receipt.General",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.wrap(BOLD.wrap("Work Payment for " + LIGHT_GREEN.wrap(GENERIC_TIME) + ":")),
        " ",
        GENERIC_ENTRY,
        " ",
        LIGHT_ORANGE.wrap(BOLD.wrap("Total: ") + LIGHT_GREEN.wrap(GENERIC_TOTAL)),
        " "
    );

    public static final LangString JOB_PAYMENT_RECEIPT_ENTRY_JOB = LangString.of("Job.Payment.Receipt.Entry.Job",
        LIGHT_YELLOW.wrap(BOLD.wrap(JOB_NAME) + " " + HOVER.wrapShowText("[" + LIGHT_GREEN.wrap("Details") + "]", GENERIC_CURRENCY))
    );

    public static final LangString JOB_PAYMENT_RECEIPT_ENTRY_CURRENCY = LangString.of("Job.Payment.Receipt.Entry.Currency",
        LIGHT_GREEN.wrap(GENERIC_AMOUNT)
    );

    public static final LangText SPECIAL_ORDER_ERROR_DISABLED_SERVER = LangText.of("SpecialOrder.Error.DisabledByServer",
        GRAY.wrap("Special Orders are " + LIGHT_RED.wrap("disabled") + " on the server."));

    public static final LangText SPECIAL_ORDER_ERROR_DISABLED_JOB = LangText.of("SpecialOrder.Error.DisabledByJob",
        GRAY.wrap("Special Orders are " + LIGHT_RED.wrap("disabled") + " for this job."));

    public static final LangText SPECIAL_ORDER_ERROR_ALREADY_HAVE = LangText.of("SpecialOrder.Error.AlreadyHave",
        GRAY.wrap("You " + LIGHT_RED.wrap("already have") + " a Special Order of this job."));

    public static final LangText SPECIAL_ORDER_ERROR_MAX_AMOUNT = LangText.of("SpecialOrder.Error.MaxAmount",
        GRAY.wrap("You can not have more than " + LIGHT_RED.wrap(GENERIC_AMOUNT) + " active Special Orders."));

    public static final LangText SPECIAL_ORDER_ERROR_COOLDOWN = LangText.of("SpecialOrder.Error.Cooldown",
        GRAY.wrap("You can take next Special Order in " + LIGHT_RED.wrap(GENERIC_TIME)));

    public static final LangText SPECIAL_ORDER_ERROR_NOT_ENOUGH_FUNDS_INFO = LangText.of("SpecialOrder.Error.NotEnoughMoney.Info",
        LIGHT_RED.wrap("You can't afford Special Order! You need:"),
        GENERIC_ENTRY
    );

    public static final LangString SPECIAL_ORDER_ERROR_NOT_ENOUGH_FUNDS_ENTRY = LangString.of("SpecialOrder.Error.NotEnoughMoney.Entry",
        LIGHT_YELLOW.wrap("- " + GENERIC_AMOUNT)
    );

    public static final LangText SPECIAL_ORDER_ERROR_GENERATION = LangText.of("SpecialOrder.Error.Generation",
        GRAY.wrap(LIGHT_RED.wrap("Whoops!") + " Unable to create Special Order."));

    public static final LangText SPECIAL_ORDER_TAKEN_INFO = LangText.of("SpecialOrder.Taken.Info",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.wrap("You took a " + ORANGE.wrap("Special Order") + " for " + ORANGE.wrap(JOB_NAME) + " job!"),
        " ",
        ORANGE.wrap(BOLD.wrap("Objectives:")),
        GENERIC_ENTRY,
        " ",
        ORANGE.wrap(BOLD.wrap("Rewards:")),
        GENERIC_REWARD,
        " ",
        LIGHT_YELLOW.wrap("Complete it in: " + ORANGE.wrap(GENERIC_TIME)),
        " "
    );

    public static final LangString SPECIAL_ORDER_TAKEN_REWARD = LangString.of("SpecialOrder.Taken.Reward",
        LIGHT_YELLOW.wrap(ORANGE.wrap("▪ ") + GENERIC_NAME));

    public static final LangString SPECIAL_ORDER_TAKEN_ENTRY = LangString.of("SpecialOrder.Taken.Entry",
        LIGHT_YELLOW.wrap(ORANGE.wrap("▪ ") + GENERIC_NAME + ": " + ORANGE.wrap("x" + GENERIC_AMOUNT) + " " + LIGHT_GREEN.wrap(HOVER.wrapShowText("[Details]", GENERIC_ENTRY)))
    );

    public static final LangString SPECIAL_ORDER_TAKEN_DETAIL = LangString.of("SpecialOrder.Taken.Detail",
        WHITE.wrap(GENERIC_NAME + ": ") + GRAY.wrap("x" + GENERIC_AMOUNT)
    );

    public static final LangText SPECIAL_ORDER_PROGRESS = LangText.of("SpecialOrder.Progress",
        OUTPUT.wrap(OutputType.ACTION_BAR) +
            LIGHT_YELLOW.wrap("(Special Order) " + ORANGE.wrap(GENERIC_NAME + ": ") + WHITE.wrap(GENERIC_CURRENT) + GRAY.wrap("/") + WHITE.wrap(GENERIC_MAX))
    );

    public static final LangText SPECIAL_ORDER_COMPLETED = LangText.of("SpecialOrder.Completed",
        OUTPUT.wrap(20, 80) + SOUND.wrap(Sound.ENTITY_PLAYER_LEVELUP) +
            LIGHT_GREEN.wrap(BOLD.wrap("Order Completed!")),
        LIGHT_GRAY.wrap("You have completed a Special Order in " + LIGHT_GREEN.wrap(JOB_NAME) + " job!")
    );

    public static final LangText JOB_XP_GAIN = LangText.of("Job.XP.Gain",
        LIGHT_GRAY.wrap("You gain " + LIGHT_YELLOW.wrap(GENERIC_AMOUNT + " XP") + " for " + LIGHT_YELLOW.wrap(JOB_NAME) + " job.")
    );

    public static final LangText JOB_XP_LOSE = LangText.of("Job.XP.Lose",
        LIGHT_GRAY.wrap("You lost " + LIGHT_RED.wrap(GENERIC_AMOUNT + " XP") + " from " + LIGHT_RED.wrap(JOB_NAME) + " job.")
    );

    public static final LangText JOB_LEVEL_UP = LangText.of("Job.Level.Up",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_PLAYER_LEVELUP) +
            LIGHT_GREEN.wrap(BOLD.wrap("Job Level Up!")),
        LIGHT_GRAY.wrap(LIGHT_GREEN.wrap(JOB_NAME) + " is now level " + LIGHT_GREEN.wrap(JOB_DATA_LEVEL) + "!")
    );

    public static final LangText JOB_LEVEL_DOWN = LangText.of("Job.Level.Down",
        OUTPUT.wrap(20, 60) + SOUND.wrap(Sound.ENTITY_IRON_GOLEM_DEATH) +
            LIGHT_RED.wrap(BOLD.wrap("Job Level Down!")),
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(JOB_NAME) + " is now level " + LIGHT_RED.wrap(JOB_DATA_LEVEL) + "!")
    );

    public static final LangText JOB_LEVEL_REWARDS_LIST = LangText.of("Job.Level.Rewards.List",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.wrap(BOLD.wrap("Level Rewards:")),
        DARK_GRAY.wrap("Hover over reward name for details."),
        " ",
        GENERIC_ENTRY,
        " "
    );

    public static final LangString JOB_LEVEL_REWARDS_ENTRY = LangString.of("Job.Level.Rewards.Entry",
        LIGHT_YELLOW.wrap("✔ " + HOVER.wrapShowText(LIGHT_GRAY.wrap(REWARD_NAME), LIGHT_GRAY.wrap(REWARD_DESCRIPTION)))
    );

    public static final LangText JOB_LIMIT_XP_NOTIFY = LangText.of("Job.Limit.XP.Notify",
        LIGHT_GRAY.wrap("You have reached daily " + LIGHT_RED.wrap("XP") + " limit for " + LIGHT_RED.wrap(JOB_NAME) + " job. You can't get more today.")
    );

    public static final LangText JOB_LIMIT_CURRENCY_NOTIFY = LangText.of("Job.Limit.Currency.Notify",
        LIGHT_GRAY.wrap("You have reached daily " + LIGHT_RED.wrap(CURRENCY_NAME) + " limit for " + RED.wrap(JOB_NAME) + " job. You can't get more today.")
    );


    public static final LangString BOOSTER_FORMAT_POSITIVE = LangString.of("Boosters.Format.Positive", GREEN.wrap("+" + GENERIC_AMOUNT + "%"));
    public static final LangString BOOSTER_FORMAT_NEGATIVE = LangString.of("Boosters.Format.Negative", RED.wrap(GENERIC_AMOUNT + "%"));

    public static final LangText BOOSTER_ACTIVATED_GLOBAL = LangText.of("Booster.Activated.Global",
        TAG_NO_PREFIX + SOUND.wrap(Sound.BLOCK_NOTE_BLOCK_BELL),
        " ",
        LIGHT_YELLOW.wrap(BOLD.wrap("Job Booster Activated!")),
        " ",
        ITALIC.wrap(GRAY.wrap("Available for " + WHITE.wrap("all jobs") + " and " + WHITE.wrap("all players") + "!")),
        " ",
        LIGHT_YELLOW.wrap("✔ " + LIGHT_GRAY.wrap("XP Boost: ") + BOOSTER_XP_PERCENT),
        LIGHT_YELLOW.wrap("✔ " + LIGHT_GRAY.wrap("Income Boost: ") + BOOSTER_INCOME_PERCENT),
        LIGHT_YELLOW.wrap("✔ " + LIGHT_GRAY.wrap("Duration: ") + GENERIC_TIME),
        " "
    );

    public static final LangText BOOSTER_ACTIVATED_PERSONAL = LangText.of("Booster.Activated.Personal",
        TAG_NO_PREFIX + SOUND.wrap(Sound.BLOCK_NOTE_BLOCK_BELL),
        " ",
        LIGHT_YELLOW.wrap(BOLD.wrap("Job Booster Received!")),
        " ",
        ITALIC.wrap(GRAY.wrap("Available " + WHITE.wrap("personally") + " for your " + WHITE.wrap(JOB_NAME) + " job.")),
        " ",
        LIGHT_YELLOW.wrap("✔ " + LIGHT_GRAY.wrap("XP Boost: ") + BOOSTER_XP_PERCENT),
        LIGHT_YELLOW.wrap("✔ " + LIGHT_GRAY.wrap("Income Boost: ") + BOOSTER_INCOME_PERCENT),
        LIGHT_YELLOW.wrap("✔ " + LIGHT_GRAY.wrap("Duration: ") + GENERIC_TIME),
        " "
    );

    public static final LangText BOOSTER_EXPIRED_GLOBAL = LangText.of("Booster.Expired.Global",
        LIGHT_GRAY.wrap("Global job booster has been expired.")
    );

    public static final LangText BOOSTER_EXPIRED_PERSONAL = LangText.of("Booster.Expired.Personal",
        LIGHT_GRAY.wrap("Your " + LIGHT_YELLOW.wrap(JOB_NAME) + " job booster has been expired.")
    );

    public static final LangText BOOSTER_LIST_INFO = LangText.of("Booster.List.Info",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.wrap(BOLD.wrap(JOB_NAME + " Job Boosters:")),
        " ",
        ITALIC.wrap(GRAY.wrap("Legend: " + WHITE.wrap("XP") + " | " + WHITE.wrap("Income"))),
        " ",
        GENERIC_ENTRY,
        " ",
        LIGHT_YELLOW.wrap(BOLD.wrap("Total:")) + " " + GENERIC_XP_BONUS + GRAY.wrap(" | ") + GENERIC_INCOME_BONUS,
        " "
    );

    public static final LangString BOOSTER_LIST_ENTRY = LangString.of("Booster.List.Entry",
        LIGHT_YELLOW.wrap("✔ " + LIGHT_GRAY.wrap(GENERIC_TYPE + " Booster: ") + GENERIC_XP_BOOST + GRAY.wrap(" | ") + GENERIC_INCOME_BOOST + " " + GRAY.wrap("(" + WHITE.wrap(GENERIC_TIME) + ")"))
    );

    public static final LangText BOOSTER_LIST_NOTHING = LangText.of("Booster.List.Nothing",
        LIGHT_RED.wrap("You have no active boosters for this job.")
    );


    public static final LangText ZONE_NOT_AVAILABLE = LangText.of("Zone.Info.NotAvailable",
        OUTPUT.wrap(OutputType.ACTION_BAR),
        LIGHT_RED.wrap("You can't work in this zone currently!")
    );

    public static final LangText ZONE_NO_PVP = LangText.of("Zone.Info.NoPvP",
        OUTPUT.wrap(OutputType.ACTION_BAR),
        LIGHT_RED.wrap("PvP is disabled in this zone!")
    );

    public static final LangText ZONE_CREATE_SUCCESS = LangText.of("Zone.Creation.Success",
        LIGHT_GRAY.wrap("Successfully created job zone: " + LIGHT_GREEN.wrap(ZONE_NAME) + "!")
    );

    public static final LangText ZONE_CREATE_INFO = LangText.of("Zone.Creation.Info",
        LIGHT_GRAY.wrap("Select two corners and use " + LIGHT_GREEN.wrap("/" + ZoneCommands.DEF_ROOT_NAME + " " + ZoneCommands.DEF_CREATE_NAME) + " to create a new zone.")
    );

    public static final LangText ZONE_SELECTION_INFO = LangText.of("Zone.Selection.Info",
        LIGHT_GRAY.wrap("Selected " + LIGHT_YELLOW.wrap("#" + GENERIC_VALUE) + " zone position.")
    );

    public static final LangText ZONE_ERROR_EXISTS = LangText.of("Zone.Error.AlreadyExists",
        LIGHT_GRAY.wrap("Zone with name " + LIGHT_RED.wrap(GENERIC_NAME) + " is already created!")
    );

    public static final LangText ZONE_ERROR_NO_SELECTION = LangText.of("Zone.Error.NoSelection",
        LIGHT_GRAY.wrap("You must select zone corners first: " + LIGHT_RED.wrap("/" + ZoneCommands.DEF_ROOT_NAME + " " + ZoneCommands.DEF_WAND_NAME))
    );

    public static final LangText ZONE_ERROR_INCOMPLETE_SELECTION = LangText.of("Zone.Error.IncompleteSelection",
        LIGHT_GRAY.wrap("You must select 2 zone corners!")
    );


    public static final LangUIButton UI_JOB_LEAVE_INFO = LangUIButton.builder("UI.Job.LeaveInfo", JOB_NAME)
        .description(
            LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap("▪ ") + "XP: " + LIGHT_YELLOW.wrap(JOB_DATA_XP) + "/" + LIGHT_YELLOW.wrap(JOB_DATA_XP_MAX)),
            LIGHT_GRAY.wrap(LIGHT_YELLOW.wrap("▪ ") + "Level: " + LIGHT_YELLOW.wrap(JOB_DATA_LEVEL) + "/" + LIGHT_YELLOW.wrap(JOB_DATA_LEVEL_MAX))
        ).build();


    public static final LangText ERROR_COMMAND_INVALID_ZONE_ARGUMENT = LangText.of("Error.Command.Argument.InvalidZone",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid zone!"));

    public static final LangText ERROR_COMMAND_INVALID_JOB_ARGUMENT = LangText.of("Error.Command.Argument.InvalidJob",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid job!"));

    public static final LangText ERROR_COMMAND_INVALID_JOB_STATE_ARGUMENT = LangText.of("Error.Command.Argument.InvalidJobState",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid job state!"));

    public static final LangText ERROR_COMMAND_INVALID_ACTION_ARGUMENT = LangText.of("Error.Command.Argument.InvalidAction",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid action!"));

    public static final LangText ERROR_INVALID_BOOSTER = LangText.of("Error.InvalidBooster",
        LIGHT_RED.wrap("Invalid booster!"));

    public static final LangText ERROR_COMMAND_INVALID_CURRENCY_ARGUMENT = LangText.of("Error.Error.Command.Argument.InvalidCurrency",
        LIGHT_GRAY.wrap(LIGHT_RED.wrap(GENERIC_VALUE) + " is not a valid currency!"));


    public static final LangString EDITOR_TITLE_ZONES                  = LangString.of("Editor.Title.Zone.List", BLACK.wrap("Job Zones"));
    public static final LangString EDITOR_TITLE_ZONE_SETTINGS          = LangString.of("Editor.Title.Zone.Settings", BLACK.wrap("Zone Settings"));
    public static final LangString EDITOR_TITLE_ZONE_BLOCK_LIST        = LangString.of("Editor.Title.Zone.BlockList", BLACK.wrap("Zone Block Lists"));
    public static final LangString EDITOR_TITLE_ZONE_BLOCK_SETTINGS    = LangString.of("Editor.Title.Zone.BlockSettings", BLACK.wrap("Block List Settings"));
    public static final LangString EDITOR_TITLE_ZONE_MODIFIER_LIST     = LangString.of("Editor.Title.Zone.ModifierList", BLACK.wrap("Zone Modifiers"));
    public static final LangString EDITOR_TITLE_ZONE_MODIFIER_SETTINGS = LangString.of("Editor.Title.Zone.ModifierSettings", BLACK.wrap("Modifier Settings"));
    public static final LangString EDITOR_TITLE_ZONE_HOURS             = LangString.of("Editor.Title.Zone.Hours", BLACK.wrap("Zone Hours"));

    public static final LangString EDITOR_GENERIC_ENTER_ID = LangString.of("Editor.Generic.Enter.Id",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Unique Identifier]")));

    public static final LangString EDITOR_GENERIC_ENTER_NAME = LangString.of("Editor.Generic.Enter.Name",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Display Name]")));

    public static final LangString EDITOR_GENERIC_ENTER_NUMBER = LangString.of("Editor.Generic.Enter.Number",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Number]")));

    public static final LangString EDITOR_GENERIC_ENTER_MIN_MAX = LangString.of("Editor.Generic.Enter.MinMax",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Min] [Max]")));

    public static final LangString EDITOR_GENERIC_ENTER_TIMES = LangString.of("Editor.Generic.Enter.Times",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Hours] [12:00 17:00]")));

    public static final LangString EDITOR_GENERIC_ENTER_CURRENCY = LangString.of("Editor.Generic.Enter.Currency",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Currency Identifier]")));

    public static final LangString EDITOR_GENERIC_ENTER_MATERIAL = LangString.of("Editor.Generic.Enter.Material",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Material Name]")));

    public static final LangString EDITOR_ZONE_ENTER_DESCRIPTION = LangString.of("Editor.Zone.Enter.Description",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Description Line]")));

    public static final LangString EDITOR_ZONE_ENTER_JOB_ID = LangString.of("Editor.Zone.Enter.JobId",
        LIGHT_GRAY.wrap("Enter " + LIGHT_GREEN.wrap("[Job Identifier]")));

    public static final LangItem EDITOR_ZONE_OBJECT = LangItem.builder("Editor.Zone.Objectv180")
        .name(ZONE_NAME + RESET.getBracketsName() + GRAY.wrap(" (ID: " + WHITE.wrap(ZONE_ID) + ")"))
        .textRaw(ZONE_JOB_NAMES)
        .emptyLine()
        .leftClick("edit")
        .dragAndDrop("set icon")
        .shiftRight("delete " + LIGHT_RED.wrap("(no undo)"))
        .build();

    public static final LangItem EDITOR_ZONE_CREATE = LangItem.builder("Editor.Zone.Create")
        .name("New Zone")
        .click("create a zone")
        .build();

    public static final LangItem EDITOR_ZONE_SELECTION = LangItem.builder("Editor.Zone.Selection")
        .name("Selection")
        .text(ZONE_INSPECT_SELECTION)
        .emptyLine()
        .text("Sets zone bounds (with a cuboid shape).")
        .emptyLine()
        .click("get a wand").build();

    public static final LangItem EDITOR_ZONE_NAME = LangItem.builder("Editor.Zone.Name")
        .name("Display Name")
        .current("Current", ZONE_NAME)
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_ZONE_DESCRIPTION = LangItem.builder("Editor.Zone.Description")
        .name("Description")
        .textRaw(ZONE_DESCRIPTION)
        .emptyLine()
        .leftClick("add line")
        .rightClick("clear all")
        .build();

    public static final LangItem EDITOR_ZONE_LINKED_JOBS = LangItem.builder("Editor.Zone.LinkedJobs")
        .name("Linked Jobs")
        .textRaw(ZONE_JOB_NAMES)
        .emptyLine()
        .text("Links job with the zone.")
        .emptyLine()
        .leftClick("add job")
        .rightClick("remove all")
        .build();

    public static final LangItem EDITOR_ZONE_JOB_LEVEL = LangItem.builder("Editor.Zone.JobLevel")
        .name("Job Levels")
        .current("Min", ZONE_JOB_MIN_LEVEL)
        .current("Max", ZONE_JOB_MAX_LEVEL)
        .emptyLine()
        .text("Sets job levels required to", "have access to this zone.")
        .emptyLine()
        .click("change")
        .dropKey("disable")
        .build();

    public static final LangItem EDITOR_ZONE_PERMISSION_REQUIRED = LangItem.builder("Editor.Zone.PermissionRequired")
        .name("Permission Requirement")
        .current("Enabled", ZONE_PERMISSION_REQUIRED)
        .current("Node", ZONE_PERMISSION)
        .emptyLine()
        .text("Sets whether or not players", "must have permission to", "have access to this zone.")
        .emptyLine()
        .click("toggle")
        .build();

    public static final LangItem EDITOR_ZONE_PVP_ALLOWED = LangItem.builder("Editor.Zone.PvPAllowed")
        .name("PvP Allowed")
        .current("Enabled", ZONE_PVP_ALLOWED)
        .emptyLine()
        .text("Sets whether or not players", "can damage each other in", "this zone.")
        .emptyLine()
        .click("toggle")
        .build();

    public static final LangItem EDITOR_ZONE_DISABLED_BLOCKS = LangItem.builder("Editor.Zone.DisabledBlockInteractions")
        .name("Disabled Block Interactions")
        .textRaw(ZONE_DISABLED_BLOCK_INTERACTIONS)
        .emptyLine()
        .text("Prevents players from interacting", "with specified block types.")
        .emptyLine()
        .leftClick("add block")
        .dropKey("remove all")
        .build();

    public static final LangItem EDITOR_ZONE_HOURS = LangItem.builder("Editor.Zone.Hours")
        .name("Hours")
        .current("Enabled", ZONE_HOURS_ENABLED)
        .emptyLine()
        .text("Controls whether job zone is", "active during certain", "hours only.")
        .emptyLine()
        .leftClick("navigate")
        .rightClick("toggle")
        .build();

    public static final LangItem EDITOR_ZONE_MODIFIERS = LangItem.builder("Editor.Zone.Modifiers")
        .name("Modifiers")
        .text("Adjust amount of job XP & payments", "players get when working", "in this zone.")
        .emptyLine()
        .click("navigate")
        .build();

    public static final LangItem EDITOR_ZONE_TIME_OBJECT = LangItem.builder("Editor.Zone.Time.Object")
        .name(GENERIC_NAME)
        .textRaw(GENERIC_VALUE)
        .emptyLine()
        .leftClick("add hours")
        .rightClick("remove")
        .build();

    public static final LangItem EDITOR_ZONE_MODIFIER_CURRENCY_CREATE = LangItem.builder("Editor.Zone.Modifier.Currency_Create")
        .name("New Currency Modifier")
        .build();

    public static final LangItem EDITOR_ZONE_MODIFIER_XP_OBJECT = LangItem.builder("Editor.Zone.Modifier.XP_Object")
        .name("Job XP Modifier")
        .current("Base Value", MODIFIER_BASE)
        .current("Per Job Level", MODIFIER_PER_LEVEL)
        .current("Level Step", MODIFIER_STEP)
        .current("Action", MODIFIER_ACTION)
        .emptyLine()
        .text("Adjusts job XP amount a player", "can get for their work in this zone.")
        .emptyLine()
        .text("Final value should be plain multipier:")
        .text(LIGHT_YELLOW.wrap("0.5") + " = " + LIGHT_GREEN.wrap("+50%"))
        .text(LIGHT_YELLOW.wrap("-0.25") + " = " + LIGHT_RED.wrap("-25%"))
        .emptyLine()
        .click("edit")
        .build();

    public static final LangItem EDITOR_ZONE_MODIFIER_CURRENCY_OBJECT = LangItem.builder("Editor.Zone.Modifier.Currency_Object")
        .name(CURRENCY_NAME + " Payment Modifier")
        .current("Base Value", MODIFIER_BASE)
        .current("Per Job Level", MODIFIER_PER_LEVEL)
        .current("Level Step", MODIFIER_STEP)
        .current("Action", MODIFIER_ACTION)
        .emptyLine()
        .text("Adjusts " + LIGHT_YELLOW.wrap(CURRENCY_NAME) + " amount a player", "can get for their work in this zone.")
        .emptyLine()
        .text("Final value should be plain multipier:")
        .text(LIGHT_YELLOW.wrap("0.5") + " = " + LIGHT_GREEN.wrap("+50%"))
        .text(LIGHT_YELLOW.wrap("-0.25") + " = " + LIGHT_RED.wrap("-25%"))
        .emptyLine()
        .click("edit")
        .dropKey("delete " + LIGHT_RED.wrap("(no undo)"))
        .build();

    public static final LangItem EDITOR_MODIFIER_BASE = LangItem.builder("Editor.Modifier.Base")
        .name("Base Value")
        .current("Current", MODIFIER_BASE)
        .emptyLine()
        .text("Base (start) modifier value.")
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_MODIFIER_PER_LEVEL = LangItem.builder("Editor.Modifier.PerLevel")
        .name("Per Level Value")
        .current("Current", MODIFIER_PER_LEVEL)
        .emptyLine()
        .text("The number increases by itself", "for each " + LIGHT_YELLOW.wrap("<Level Step>") + " job levels.")
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_MODIFIER_STEP = LangItem.builder("Editor.Modifier.Step")
        .name("Level Step")
        .current("Current", MODIFIER_STEP)
        .emptyLine()
        .text("Sets how often " + LIGHT_YELLOW.wrap("Per Level Value"), "should increase in job levels.")
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_MODIFIER_ACTION = LangItem.builder("Editor.Modifier.Action")
        .name("Action")
        .current("Current", MODIFIER_ACTION)
        .emptyLine()
        .text("Sets math action", "between " + LIGHT_YELLOW.wrap("Base") + " and " + LIGHT_YELLOW.wrap("Per Level") + " values.")
        .emptyLine()
        .click("toggle")
        .build();

    public static final LangItem EDITOR_ZONE_BLOCK_LISTS = LangItem.builder("Editor.Zone.BlockLists")
        .name("Block Lists")
        .text("Here you can create lists of blocks")
        .text("allowed for breaking by players")
        .text("and with regeneration interval.")
        .emptyLine()
        .click("navigate")
        .build();

    public static final LangItem EDITOR_ZONE_BLOCK_LIST_CREATE = LangItem.builder("Editor.Zone.BlockList.Create")
        .name("New Block List").build();

    public static final LangItem EDITOR_ZONE_BLOCK_LIST_OBJECT = LangItem.builder("Editor.Zone.BlockList.Object")
        .name("Block List: " + RESET.getBracketsName() + WHITE.wrap(BLOCK_LIST_ID))
        .text(LIGHT_YELLOW.wrap(BOLD.wrap("Blocks:")))
        .textRaw(BLOCK_LIST_MATERIALS)
        .emptyLine()
        .click("edit")
        .shiftRight("delete " + LIGHT_RED.wrap("(no undo)"))
        .build();

    public static final LangItem EDITOR_ZONE_BLOCK_LIST_MATERIALS = LangItem.builder("Editor.Zone.BlockList.Materials")
        .name("Materials")
        .text("The following block types are", "allowed for breaking by players:")
        .textRaw(BLOCK_LIST_MATERIALS)
        .emptyLine()
        .leftClick("add material")
        .rightClick("remove all")
        .build();

    public static final LangItem EDITOR_ZONE_BLOCK_LIST_FALLBACK_MATERIAL = LangItem.builder("Editor.Zone.BlockList.FallbackMaterial")
        .name("Fallback Material")
        .text("Broken blocks will be replaced with", "specified material until restored.")
        .emptyLine()
        .textRaw(BLOCK_LIST_FALLBACK_MATERIAL)
        .emptyLine()
        .leftClick("change")
        .build();

    public static final LangItem EDITOR_ZONE_BLOCK_LIST_RESET_TIME = LangItem.builder("Editor.Zone.BlockList.ResetTime")
        .name("Reset Time")
        .text("Sets how soon (in seconds) broken blocks", "will be regenerated back to", "their original materials.")
        .emptyLine()
        .textRaw(BLOCK_LIST_RESET_TIME)
        .emptyLine()
        .leftClick("change")
        .build();

    public static final LangItem EDITOR_ZONE_BLOCK_LIST_DROP_ITEMS = LangItem.builder("Editor.Zone.BlockList.DropItems")
        .name("Drop Items")
        .text("Sets whether or not broken blocks", "will drop items.")
        .emptyLine()
        .textRaw(BLOCK_LIST_DROP_ITEMS)
        .emptyLine()
        .click("toggle")
        .build();
}
