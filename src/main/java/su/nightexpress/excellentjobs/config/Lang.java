package su.nightexpress.excellentjobs.config;

import org.bukkit.Sound;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.util.report.ReportType;
import su.nightexpress.excellentjobs.zone.command.ZoneCommands;
import su.nightexpress.nightcore.core.CoreLang;
import su.nightexpress.nightcore.language.entry.LangEnum;
import su.nightexpress.nightcore.language.entry.LangItem;
import su.nightexpress.nightcore.language.entry.LangString;
import su.nightexpress.nightcore.language.entry.LangText;
import su.nightexpress.nightcore.language.message.OutputType;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.language.tag.MessageTags.*;
import static su.nightexpress.nightcore.util.text.tag.Tags.*;

public class Lang extends CoreLang {

    public static final LangEnum<JobState> JOB_STATE = LangEnum.of("Job.State", JobState.class);

    public static final LangString COMMAND_ARGUMENT_NAME_ZONE = LangString.of("Command.Argument.Name.Zone", "zone");

    public static final LangString COMMAND_ZONE_DESC        = LangString.of("Command.Zone.Desc", "Zone commands.");
    public static final LangString COMMAND_ZONE_CREATE_DESC = LangString.of("Command.Zone.Create.Desc", "Create a new zone from selection.");
    public static final LangString COMMAND_ZONE_WAND_DESC   = LangString.of("Command.Zone.Wand.Desc", "Get zone selection tool.");
    public static final LangString COMMAND_ZONE_EDITOR_DESC = LangString.of("Command.Zone.Editor.Desc", "Open zones editor.");

    public static final LangString COMMAND_STATS_DESC  = LangString.of("Command.Stats.Desc", "Show [player's] job stats.");
    public static final LangString COMMAND_STATS_USAGE = LangString.of("Command.Stats.Usage", "[player]");

    public static final LangText COMMAND_STATS_DISPLAY = LangText.of("Command.Stats.Display.Info",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.enclose(ORANGE.enclose(PLAYER_NAME) + "'s Job Stats:"),
        " ",
        GENERIC_ENTRY,
        " "
    );

    public static final LangString COMMAND_STATS_ENTRY = LangString.of("Command.Stats.Display.Job",
        LIGHT_GRAY.enclose(ORANGE.enclose("▪ ") + JOB_NAME + ":   Level: " + ORANGE.enclose(JOB_DATA_LEVEL) + ", XP: " + ORANGE.enclose(JOB_DATA_XP) + "/" + ORANGE.enclose(JOB_DATA_XP_MAX))
    );

    public static final LangString COMMAND_INFO_CURRENCY_MULTIPLIER = LangString.of("Command.Info.Display.Currency.Multiplier",
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("▪ ") + CURRENCY_NAME + " Multiplier: " + LIGHT_GREEN.enclose("x" + CURRENCY_MULTIPLIER))
    );
    public static final LangString COMMAND_INFO_CURRENCY_BOOSTER    = LangString.of("Command.Info.Display.Currency.Booster",
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose("▪ ") + CURRENCY_NAME + " Boost: " + LIGHT_GREEN.enclose(CURRENCY_BOOST_PERCENT + "%"))
    );

    public static final LangString COMMAND_BOOSTER_DESC  = LangString.of("Command.Booster.Desc", "Booster management.");
    public static final LangString COMMAND_BOOSTER_USAGE = LangString.of("Command.Booster.Usage", "[help]");

    public static final LangString COMMAND_BOOSTER_CREATE_DESC  = LangString.of("Command.Booster.Create.Desc", "Create personal booster.");
    public static final LangString COMMAND_BOOSTER_CREATE_USAGE = LangString.of("Command.Booster.Create.Usage", "<player> <booster> <duration> [-s]");

    public static final LangText COMMAND_BOOSTER_CREATE_DONE = LangText.of("Command.Booster.Create.Done",
        LIGHT_GRAY.enclose("Added " + LIGHT_YELLOW.enclose(GENERIC_NAME) + " job booster to " + LIGHT_YELLOW.enclose(PLAYER_NAME) + " for " + LIGHT_YELLOW.enclose(GENERIC_TIME))
    );

    public static final LangText COMMAND_BOOSTER_CREATE_NOTIFY = LangText.of("Command.Booster.Create.Notify.Info",
        TAG_NO_PREFIX + SOUND.enclose(Sound.BLOCK_NOTE_BLOCK_BELL),
        " ",
        LIGHT_GRAY.enclose("You received a " + LIGHT_YELLOW.enclose(BOLD.enclose("Job Booster")) + "!"),
        " ",
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("▪ ") + "Jobs: " + LIGHT_YELLOW.enclose(JOB_NAME)),
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("▪ ") + "XP Bonus: " + LIGHT_YELLOW.enclose("+" + XP_BOOST_PERCENT + "%")),
        GENERIC_CURRENCY,
        LIGHT_GRAY.enclose(LIGHT_RED.enclose("▪ ") + "Duration: " + LIGHT_RED.enclose(GENERIC_TIME)),
        " "
    );

    public static final LangString COMMAND_BOOSTER_CREATE_NOTIFY_CURRENCY = LangString.of("Command.Booster.Create.Notify.Currency",
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose("▪ ") + CURRENCY_NAME + " Bonus: " + LIGHT_YELLOW.enclose("+" + CURRENCY_BOOST_PERCENT + "%"))
    );

    public static final LangString COMMAND_BOOSTER_CLEAR_DESC  = LangString.of("Command.Booster.Clear.Desc", "Remove personal boosters.");
    public static final LangString COMMAND_BOOSTER_CLEAR_USAGE = LangString.of("Command.Booster.Clear.Usage", "<player> [job]");

    public static final LangText COMMAND_BOOSTER_CLEAR_DONE_JOB = LangText.of("Command.Booster.Clear.Done.Job",
        LIGHT_GRAY.enclose("Removed personal " + LIGHT_YELLOW.enclose(JOB_NAME) + " job booster from " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );

    public static final LangText COMMAND_BOOSTER_CLEAR_DONE_ALL = LangText.of("Command.Booster.Clear.Done.All",
        LIGHT_GRAY.enclose("Removed " + LIGHT_YELLOW.enclose("All") + " personal job boosters from " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );

    public static final LangString COMMAND_XP_DESC  = LangString.of("Command.XP.Desc", "Manage player's job XP.");
    public static final LangString COMMAND_XP_USAGE = LangString.of("Command.XP.Usage", "<action> <job> <amount> [player] [-s]");

    public static final LangText COMMAND_XP_ADD_DONE = LangText.of("Command.XP.Add.Done",
        LIGHT_GRAY.enclose("Added " + LIGHT_GREEN.enclose(GENERIC_AMOUNT) + " XP to " + LIGHT_GREEN.enclose(PLAYER_NAME) + "'s " + LIGHT_GREEN.enclose(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_XP_ADD_NOTIFY = LangText.of("Command.XP.Add.Notify",
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose(GENERIC_AMOUNT) + " XP has been added to your " + LIGHT_GREEN.enclose(JOB_NAME) + " job!")
    );

    public static final LangText COMMAND_XP_REMOVE_DONE = LangText.of("Command.XP.Remove.Done",
        LIGHT_GRAY.enclose("Removed " + LIGHT_RED.enclose(GENERIC_AMOUNT) + " XP from " + LIGHT_RED.enclose(PLAYER_NAME) + "'s " + LIGHT_RED.enclose(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_XP_REMOVE_NOTIFY = LangText.of("Command.XP.Remove.Notify",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_AMOUNT) + " XP has been removed from your " + LIGHT_RED.enclose(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_XP_SET_DONE = LangText.of("Command.XP.Set.Done",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " XP for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_XP_SET_NOTIFY = LangText.of("Command.XP.Set.Notify",
        LIGHT_GRAY.enclose("Your " + LIGHT_YELLOW.enclose(JOB_NAME) + " job XP has been set to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + ".")
    );

    public static final LangString COMMAND_LEVEL_DESC  = LangString.of("Command.Level.Desc",
        "Manage player's job levels.");

    public static final LangString COMMAND_LEVEL_USAGE = LangString.of("Command.Level.Usage",
        "<action> <job> <amount> [player] [-s]");

    public static final LangText COMMAND_LEVEL_ADD_DONE = LangText.of("Command.Level.Add.Done",
        LIGHT_GRAY.enclose("Added " + LIGHT_GREEN.enclose(GENERIC_AMOUNT) + " level(s) to " + LIGHT_GREEN.enclose(PLAYER_NAME) + "'s " + LIGHT_GREEN.enclose(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_LEVEL_ADD_NOTIFY = LangText.of("Command.Level.Add.Notify",
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose(GENERIC_AMOUNT) + " level(s) has been added to your " + LIGHT_GREEN.enclose(JOB_NAME) + " job!")
    );

    public static final LangText COMMAND_LEVEL_REMOVE_DONE = LangText.of("Command.Level.Remove.Done",
        LIGHT_GRAY.enclose("Removed " + LIGHT_RED.enclose(GENERIC_AMOUNT) + " level(s) from " + LIGHT_RED.enclose(PLAYER_NAME) + "'s " + LIGHT_RED.enclose(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_LEVEL_REMOVE_NOTIFY = LangText.of("Command.Level.Remove.Notify",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_AMOUNT) + " level(s) has been removed from your " + LIGHT_RED.enclose(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_LEVEL_SET_DONE = LangText.of("Command.Level.Set.Done",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " level for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(JOB_NAME) + " job.")
    );

    public static final LangText COMMAND_LEVEL_SET_NOTIFY = LangText.of("Command.Level.Set.Notify",
        LIGHT_GRAY.enclose("Your " + LIGHT_YELLOW.enclose(JOB_NAME) + " job level has been set to " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + ".")
    );

    public static final LangString COMMAND_RESET_DESC  = LangString.of("Command.Reset.Desc",
        "Reset [player's] job progress.");

    public static final LangString COMMAND_RESET_USAGE = LangString.of("Command.Reset.Usage",
        "<job> [player] [-s]");

    public static final LangText COMMAND_RESET_DONE = LangText.of("Command.Reset.Done",
        LIGHT_GRAY.enclose("Successfully reset " + LIGHT_YELLOW.enclose(JOB_NAME) + " progress for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + ".")
    );

    public static final LangString COMMAND_MENU_DESC = LangString.of("Command.Menu.Desc",
        "Open Jobs GUI.");

    public static final LangString COMMAND_MENU_USAGE = LangString.of("Command.Menu.Usage", "");

    public static final LangString COMMAND_OBJECTIVES_USAGE = LangString.of("Command.Objectives.Usage",
        "<job>");

    public static final LangString COMMAND_OBJECTIVES_DESC  = LangString.of("Command.Objectives.Desc",
        "Browse job objectives.");


    public static final LangString COMMAND_JOIN_DESC = LangString.of("Command.Join.Desc", "Join a job.");
    public static final LangString COMMAND_JOIN_USAGE = LangString.of("Command.Join.Usage", "<job>");

    public static final LangString COMMAND_LEAVE_DESC = LangString.of("Command.Leave.Desc", "Leave a job.");
    public static final LangString COMMAND_LEAVE_USAGE = LangString.of("Command.Leave.Usage", "<job>");

    public static final LangString COMMAND_SET_STATE_DESC  = LangString.of("Command.SetState.Desc", "Set player's job state.");
    public static final LangString COMMAND_SET_STATE_USAGE = LangString.of("Command.State.Usage", "<player> <job> <state>");
    public static final LangText   COMMAND_SET_STATE_DONE  = LangText.of("Command.SetState.Done",
        LIGHT_GRAY.enclose("Set " + LIGHT_YELLOW.enclose(GENERIC_STATE) + " state for " + LIGHT_YELLOW.enclose(PLAYER_NAME) + "'s " + LIGHT_YELLOW.enclose(JOB_NAME) + " job!"));

    public static final LangString COMMAND_TOP_USAGE = LangString.of("Command.Top.Usage",
        "<job> [page]");

    public static final LangString COMMAND_TOP_DESC  = LangString.of("Command.Top.Desc",
        "List most levelled players.");

    public static final LangText COMMAND_TOP_LIST = LangText.of("Command.Top.List",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.enclose(BOLD.enclose(JOB_NAME + " Level Top:")),
        " ",
        GENERIC_ENTRY,
        " ",
        LIGHT_GRAY.enclose("Page " + LIGHT_YELLOW.enclose(GENERIC_CURRENT) + " of " + LIGHT_YELLOW.enclose(GENERIC_MAX) + "."),
        " "
    );

    public static final LangString COMMAND_TOP_ENTRY = LangString.of("Command.Top.Entry",
        LIGHT_GRAY.enclose(LIGHT_YELLOW.enclose(GENERIC_POS + ". ") + PLAYER_NAME + ": " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT) + " Levels")
    );

    public static final LangText BOOSTER_ERROR_INVALID = LangText.of("Booster.Error.Invalid",
        LIGHT_RED.enclose("Invalid booster!")
    );

    public static final LangText CURRENCY_ERROR_INVALID = LangText.of("Currency.Error.Invalid",
        LIGHT_RED.enclose("Invalid currency!")
    );

    public static final LangText JOB_ERROR_INVALID = LangText.of("Job.Error.Invalid",
        LIGHT_RED.enclose("Invalid job!")
    );

    public static final LangText ERROR_INVALID_STATE = LangText.of("Error.InvalidState",
        LIGHT_RED.enclose("Invalid state!")
    );

    public static final LangText JOB_JOIN_ERROR_ALREADY_HIRED = LangText.of("Job.Join.Error.AlreadyHired",
        LIGHT_GRAY.enclose("You're already hired for the " + LIGHT_RED.enclose(JOB_NAME) + " job!"));

    public static final LangText JOB_JOIN_ERROR_LIMIT_STATE = LangText.of("Job.Join.Error.StateLimit",
        LIGHT_GRAY.enclose("You can't get more than " + LIGHT_RED.enclose(GENERIC_AMOUNT + " " + GENERIC_STATE) + " jobs!"));

    public static final LangText JOB_JOIN_ERROR_LIMIT_GENERAL = LangText.of("Job.Join.Error.GeneralLimit",
        LIGHT_GRAY.enclose("You can't get more jobs!"));

    public static final LangText JOB_JOIN_SUCCESS = LangText.of("Job.Join.Success",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_PLAYER_LEVELUP) +
            LIGHT_GREEN.enclose(BOLD.enclose("Hired!")),
        LIGHT_GRAY.enclose("You're hired for the " + LIGHT_GREEN.enclose(JOB_NAME) + " job!")
    );

    public static final LangText JOB_LEAVE_SUCCESS = LangText.of("Job.Leave.Success",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.BLOCK_WOODEN_DOOR_CLOSE) +
            LIGHT_GREEN.enclose(BOLD.enclose("Job Quit")),
        LIGHT_GRAY.enclose("You quit the " + LIGHT_GREEN.enclose(JOB_NAME) + " job!")
    );

    public static final LangText JOB_LEAVE_ERROR_NOT_JOINED = LangText.of("Job.Leave.Error.NotJoined",
        LIGHT_GRAY.enclose("You're not employed for the " + LIGHT_RED.enclose(JOB_NAME) + " job!"));

    public static final LangText JOB_LEAVE_ERROR_NOT_ALLOWED = LangText.of("Job.Leave.Error.NotAllowed",
        LIGHT_GRAY.enclose("You can't leave the " + LIGHT_RED.enclose(JOB_NAME) + " job!"));

    public static final LangText JOB_RESET_NOTIFY = LangText.of("Job.Reset.Notify",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR) +
            LIGHT_GREEN.enclose(BOLD.enclose("Job Reset!")),
        LIGHT_GRAY.enclose("All " + LIGHT_GREEN.enclose(JOB_NAME) + " progress have been reset!")
    );

    public static final LangText JOB_PAYMENT_RECEIPT = LangText.of("Job.Payment.Receipt.General",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.enclose(BOLD.enclose("Work Payment for " + LIGHT_GREEN.enclose(GENERIC_TIME) + ":")),
        " ",
        GENERIC_ENTRY,
        " ",
        LIGHT_ORANGE.enclose(BOLD.enclose("Total: ") + LIGHT_GREEN.enclose(GENERIC_TOTAL)),
        " "
    );

    public static final LangString JOB_PAYMENT_RECEIPT_ENTRY_JOB = LangString.of("Job.Payment.Receipt.Entry.Job",
        LIGHT_YELLOW.enclose(BOLD.enclose(JOB_NAME) + " " + HOVER.encloseHint("[" + LIGHT_GREEN.enclose("Details") + "]", GENERIC_CURRENCY))
    );

    public static final LangString JOB_PAYMENT_RECEIPT_ENTRY_CURRENCY = LangString.of("Job.Payment.Receipt.Entry.Currency",
        LIGHT_GREEN.enclose(GENERIC_AMOUNT)
    );

    public static final LangText SPECIAL_ORDER_ERROR_DISABLED_SERVER = LangText.of("SpecialOrder.Error.DisabledByServer",
        GRAY.enclose("Special Orders are " + LIGHT_RED.enclose("disabled") + " on the server."));

    public static final LangText SPECIAL_ORDER_ERROR_DISABLED_JOB = LangText.of("SpecialOrder.Error.DisabledByJob",
        GRAY.enclose("Special Orders are " + LIGHT_RED.enclose("disabled") + " for this job."));

    public static final LangText SPECIAL_ORDER_ERROR_ALREADY_HAVE = LangText.of("SpecialOrder.Error.AlreadyHave",
        GRAY.enclose("You " + LIGHT_RED.enclose("already have") + " a Special Order of this job."));

    public static final LangText SPECIAL_ORDER_ERROR_MAX_AMOUNT = LangText.of("SpecialOrder.Error.MaxAmount",
        GRAY.enclose("You can not have more than " + LIGHT_RED.enclose(GENERIC_AMOUNT) + " active Special Orders."));

    public static final LangText SPECIAL_ORDER_ERROR_COOLDOWN = LangText.of("SpecialOrder.Error.Cooldown",
        GRAY.enclose("You can take next Special Order in " + LIGHT_RED.enclose(GENERIC_TIME)));

    public static final LangText SPECIAL_ORDER_ERROR_NOT_ENOUGH_FUNDS_INFO = LangText.of("SpecialOrder.Error.NotEnoughMoney.Info",
        LIGHT_RED.enclose("You can't afford Special Order! You need:"),
        GENERIC_ENTRY
    );

    public static final LangString SPECIAL_ORDER_ERROR_NOT_ENOUGH_FUNDS_ENTRY = LangString.of("SpecialOrder.Error.NotEnoughMoney.Entry",
        LIGHT_YELLOW.enclose("- " + GENERIC_AMOUNT)
    );

    public static final LangText SPECIAL_ORDER_ERROR_GENERATION = LangText.of("SpecialOrder.Error.Generation",
        GRAY.enclose(LIGHT_RED.enclose("Whoops!") + " Unable to create Special Order."));

    public static final LangText SPECIAL_ORDER_TAKEN_INFO = LangText.of("SpecialOrder.Taken.Info",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.enclose("You took a " + ORANGE.enclose("Special Order") + " for " + ORANGE.enclose(JOB_NAME) + " job!"),
        " ",
        ORANGE.enclose(BOLD.enclose("Objectives:")),
        GENERIC_ENTRY,
        " ",
        ORANGE.enclose(BOLD.enclose("Rewards:")),
        GENERIC_REWARD,
        " ",
        LIGHT_YELLOW.enclose("Complete it in: " + ORANGE.enclose(GENERIC_TIME)),
        " "
    );

    public static final LangString SPECIAL_ORDER_TAKEN_REWARD = LangString.of("SpecialOrder.Taken.Reward",
        LIGHT_YELLOW.enclose(ORANGE.enclose("▪ ") + GENERIC_NAME));

    public static final LangString SPECIAL_ORDER_TAKEN_ENTRY = LangString.of("SpecialOrder.Taken.Entry",
        LIGHT_YELLOW.enclose(ORANGE.enclose("▪ ") + GENERIC_NAME + ": " + ORANGE.enclose("x" + GENERIC_AMOUNT) + " " + LIGHT_GREEN.enclose(HOVER.encloseHint("[Details]", GENERIC_ENTRY)))
    );

    public static final LangString SPECIAL_ORDER_TAKEN_DETAIL = LangString.of("SpecialOrder.Taken.Detail",
        WHITE.enclose(GENERIC_NAME + ": ") + GRAY.enclose("x" + GENERIC_AMOUNT)
    );

    public static final LangText SPECIAL_ORDER_PROGRESS = LangText.of("SpecialOrder.Progress",
        OUTPUT.enclose(OutputType.ACTION_BAR) +
            LIGHT_YELLOW.enclose("(Special Order) " + ORANGE.enclose(GENERIC_NAME + ": ") + WHITE.enclose(GENERIC_CURRENT) + GRAY.enclose("/") + WHITE.enclose(GENERIC_MAX))
    );

    public static final LangText SPECIAL_ORDER_COMPLETED = LangText.of("SpecialOrder.Completed",
        OUTPUT.enclose(20, 80) + SOUND.enclose(Sound.ENTITY_PLAYER_LEVELUP) +
            LIGHT_GREEN.enclose(BOLD.enclose("Order Completed!")),
        LIGHT_GRAY.enclose("You have completed a Special Order in " + LIGHT_GREEN.enclose(JOB_NAME) + " job!")
    );

    public static final LangText JOB_XP_GAIN = LangText.of("Job.XP.Gain",
        LIGHT_GRAY.enclose("You gain " + LIGHT_YELLOW.enclose(GENERIC_AMOUNT + " XP") + " for " + LIGHT_YELLOW.enclose(JOB_NAME) + " job.")
    );

    public static final LangText JOB_XP_LOSE = LangText.of("Job.XP.Lose",
        LIGHT_GRAY.enclose("You lost " + LIGHT_RED.enclose(GENERIC_AMOUNT + " XP") + " from " + LIGHT_RED.enclose(JOB_NAME) + " job.")
    );

    public static final LangText JOB_LEVEL_UP = LangText.of("Job.Level.Up",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_PLAYER_LEVELUP) +
            LIGHT_GREEN.enclose(BOLD.enclose("Job Level Up!")),
        LIGHT_GRAY.enclose(LIGHT_GREEN.enclose(JOB_NAME) + " is now level " + LIGHT_GREEN.enclose(JOB_DATA_LEVEL) + "!")
    );

    public static final LangText JOB_LEVEL_DOWN = LangText.of("Job.Level.Down",
        OUTPUT.enclose(20, 60) + SOUND.enclose(Sound.ENTITY_IRON_GOLEM_DEATH) +
            LIGHT_RED.enclose(BOLD.enclose("Job Level Down!")),
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(JOB_NAME) + " is now level " + LIGHT_RED.enclose(JOB_DATA_LEVEL) + "!")
    );

    public static final LangText JOB_LEVEL_REWARDS_LIST = LangText.of("Job.Level.Rewards.List",
        TAG_NO_PREFIX,
        " ",
        LIGHT_YELLOW.enclose(BOLD.enclose("Level Rewards:")),
        DARK_GRAY.enclose("Hover over reward name for details."),
        " ",
        GENERIC_ENTRY,
        " "
    );

    public static final LangString JOB_LEVEL_REWARDS_ENTRY = LangString.of("Job.Level.Rewards.Entry",
        LIGHT_YELLOW.enclose("✔ " + HOVER.encloseHint(LIGHT_GRAY.enclose(REWARD_NAME), LIGHT_GRAY.enclose(REWARD_DESCRIPTION)))
    );

    public static final LangText JOB_LIMIT_XP_NOTIFY = LangText.of("Job.Limit.XP.Notify",
        LIGHT_GRAY.enclose("You have reached daily " + LIGHT_RED.enclose("XP") + " limit for " + LIGHT_RED.enclose(JOB_NAME) + " job. You can't get more today.")
    );

    public static final LangText JOB_LIMIT_CURRENCY_NOTIFY = LangText.of("Job.Limit.Currency.Notify",
        LIGHT_GRAY.enclose("You have reached daily " + LIGHT_RED.enclose(CURRENCY_NAME) + " limit for " + RED.enclose(JOB_NAME) + " job. You can't get more today.")
    );

    public static final LangText BOOSTER_NOTIFY_GLOBAL = LangText.of("Booster.Notify.Global",
        TAG_NO_PREFIX + SOUND.enclose(Sound.BLOCK_NOTE_BLOCK_BELL),
        "",
        LIGHT_YELLOW.enclose("Jobs Booster Activated!"),
        LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Jobs: ") + JOB_NAME),
        LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("XP Bonus: ") + "+" + XP_BOOST_PERCENT + "%"),
        GENERIC_CURRENCY,
        LIGHT_RED.enclose("▪ " + LIGHT_GRAY.enclose("Duration: ") + GENERIC_TIME),
        ""
    );

    public static final LangText BOOSTER_NOTIFY_TOTAL = LangText.of("Booster.Notify.Total",
        TAG_NO_PREFIX,
        "",
        LIGHT_YELLOW.enclose("Hey! T"),
        LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("Jobs: ") + JOB_NAME),
        LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose("XP Bonus: ") + "+" + XP_BOOST_PERCENT + "%"),
        GENERIC_CURRENCY,
        LIGHT_RED.enclose("▪ " + LIGHT_GRAY.enclose("Duration: ") + GENERIC_TIME),
        ""
    );

    public static final LangString BOOSTER_CURRENCY_INFO = LangString.of("Booster.CurrencyLine",
        LIGHT_YELLOW.enclose("▪ " + LIGHT_GRAY.enclose(CURRENCY_NAME + " Bonus: ") + "+" + CURRENCY_BOOST_PERCENT + "%")
    );


    public static final LangText ZONE_NOT_AVAILABLE = LangText.of("Zone.Info.NotAvailable",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_RED.enclose("You can't work in this zone currently!")
    );

    public static final LangText ZONE_NO_PVP = LangText.of("Zone.Info.NoPvP",
        OUTPUT.enclose(OutputType.ACTION_BAR),
        LIGHT_RED.enclose("PvP is disabled in this zone!")
    );

    public static final LangText ZONE_CREATE_SUCCESS = LangText.of("Zone.Creation.Success",
        LIGHT_GRAY.enclose("Successfully created job zone: " + LIGHT_GREEN.enclose(ZONE_NAME) + "!")
    );

    public static final LangText ZONE_CREATE_INFO = LangText.of("Zone.Creation.Info",
        LIGHT_GRAY.enclose("Select two corners and use " + LIGHT_GREEN.enclose("/" + ZoneCommands.DEF_ROOT_NAME + " " + ZoneCommands.DEF_CREATE_NAME) + " to create a new zone.")
    );

    public static final LangText ZONE_SELECTION_INFO = LangText.of("Zone.Selection.Info",
        LIGHT_GRAY.enclose("Selected " + LIGHT_YELLOW.enclose("#" + GENERIC_VALUE) + " zone position.")
    );

    public static final LangText ZONE_ERROR_EXISTS = LangText.of("Zone.Error.AlreadyExists",
        LIGHT_GRAY.enclose("Zone with name " + LIGHT_RED.enclose(GENERIC_NAME) + " is already created!")
    );

    public static final LangText ZONE_ERROR_NO_SELECTION = LangText.of("Zone.Error.NoSelection",
        LIGHT_GRAY.enclose("You must select zone corners first: " + LIGHT_RED.enclose("/" + ZoneCommands.DEF_ROOT_NAME + " " + ZoneCommands.DEF_WAND_NAME))
    );

    public static final LangText ZONE_ERROR_INCOMPLETE_SELECTION = LangText.of("Zone.Error.IncompleteSelection",
        LIGHT_GRAY.enclose("You must select 2 zone corners!")
    );

    public static final LangText ERROR_COMMAND_INVALID_ZONE_ARGUMENT = LangText.of("Error.Command.Argument.InvalidZone",
        LIGHT_GRAY.enclose(LIGHT_RED.enclose(GENERIC_VALUE) + " is not a valid zone!")
    );



    public static final LangString EDITOR_TITLE_ZONES = LangString.of("Editor.Title.Zone.List",
        BLACK.enclose("Job Zones"));

    public static final LangString EDITOR_TITLE_ZONE_SETTINGS = LangString.of("Editor.Title.Zone.Settings",
        BLACK.enclose("Zone Settings"));

    public static final LangString EDITOR_TITLE_ZONE_BLOCK_LIST = LangString.of("Editor.Title.Zone.BlockList",
        BLACK.enclose("Zone Block Lists"));

    public static final LangString EDITOR_TITLE_ZONE_BLOCK_SETTINGS = LangString.of("Editor.Title.Zone.BlockSettings",
        BLACK.enclose("Block List Settings"));

    public static final LangString EDITOR_TITLE_ZONE_MODIFIER_LIST = LangString.of("Editor.Title.Zone.ModifierList",
        BLACK.enclose("Zone Modifiers"));

    public static final LangString EDITOR_TITLE_ZONE_MODIFIER_SETTINGS = LangString.of("Editor.Title.Zone.ModifierSettings",
        BLACK.enclose("Modifier Settings"));

    public static final LangString EDITOR_GENERIC_ENTER_ID = LangString.of("Editor.Generic.Enter.Id",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Unique Identifier]")));

    public static final LangString EDITOR_GENERIC_ENTER_NAME = LangString.of("Editor.Generic.Enter.Name",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Display Name]")));

    public static final LangString EDITOR_GENERIC_ENTER_NUMBER = LangString.of("Editor.Generic.Enter.Number",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Number]")));

    public static final LangString EDITOR_GENERIC_ENTER_MIN_MAX = LangString.of("Editor.Generic.Enter.MinMax",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Min] [Max]")));

    public static final LangString EDITOR_GENERIC_ENTER_TIMES = LangString.of("Editor.Generic.Enter.Times",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Time1 Time2] [12:00 17:00]")));

    public static final LangString EDITOR_GENERIC_ENTER_CURRENCY = LangString.of("Editor.Generic.Enter.Currency",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Currency Identifier]")));

    public static final LangString EDITOR_GENERIC_ENTER_MATERIAL = LangString.of("Editor.Generic.Enter.Material",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Material Name]")));

    public static final LangString EDITOR_ZONE_ENTER_DESCRIPTION = LangString.of("Editor.Zone.Enter.Description",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Description Line]")));

    public static final LangString EDITOR_ZONE_ENTER_JOB_ID = LangString.of("Editor.Zone.Enter.JobId",
        LIGHT_GRAY.enclose("Enter " + LIGHT_GREEN.enclose("[Job Identifier]")));

    public static final LangItem EDITOR_ZONE_OBJECT = LangItem.builder("Editor.Zone.Object")
        .name(ZONE_NAME + RESET.getBracketsName() + GRAY.enclose(" (ID: " + WHITE.enclose(ZONE_ID) + ")"))
        .text(ZONE_REPORT)
        .emptyLine()
        .current("Linked Job", ZONE_JOB_NAME)
        .emptyLine()
        .leftClick("edit")
        .dragAndDrop("set icon")
        .shiftRight("delete " + LIGHT_RED.enclose("(no undo)"))
        .build();

    public static final LangItem EDITOR_ZONE_CREATE = LangItem.builder("Editor.Zone.Create")
        .name("New Zone")
        .click("create a zone")
        .build();

    public static final LangItem EDITOR_ZONE_SELECTION = LangItem.builder("Editor.Zone.Selection")
        .name("Selection")
        .text(ZONE_INSPECT.apply(ReportType.ZONE_SELECTION))
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
        .rightClick("remove latest")
        .dropKey("clear all")
        .build();

    public static final LangItem EDITOR_ZONE_LINKED_JOB = LangItem.builder("Editor.Zone.LinkedJob")
        .name("Linked Job")
        .textRaw(ZONE_INSPECT.apply(ReportType.ZONE_JOB))
        .emptyLine()
        .current("Current", ZONE_JOB_NAME)
        .emptyLine()
        .text("Links job with the zone.")
        .emptyLine()
        .click("change")
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

    public static final LangItem EDITOR_ZONE_OPEN_TIMES = LangItem.builder("Editor.Zone.OpenTimes")
        .name("Open Times")
        .text("Sets day times when this zone", "is available for players.")
        .emptyLine()
        .click("navigate")
        .dropKey("disable")
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
        .leftClick("add time")
        .rightClick("remove latest")
        .dropKey("remove all")
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
        .text(LIGHT_YELLOW.enclose("0.5") + " = " + LIGHT_GREEN.enclose("+50%"))
        .text(LIGHT_YELLOW.enclose("-0.25") + " = " + LIGHT_RED.enclose("-25%"))
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
        .text("Adjusts " + LIGHT_YELLOW.enclose(CURRENCY_NAME) + " amount a player", "can get for their work in this zone.")
        .emptyLine()
        .text("Final value should be plain multipier:")
        .text(LIGHT_YELLOW.enclose("0.5") + " = " + LIGHT_GREEN.enclose("+50%"))
        .text(LIGHT_YELLOW.enclose("-0.25") + " = " + LIGHT_RED.enclose("-25%"))
        .emptyLine()
        .click("edit")
        .dropKey("delete " + LIGHT_RED.enclose("(no undo)"))
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
        .text("The number increases by itself", "for each " + LIGHT_YELLOW.enclose("<Step>") + " job levels.")
        .emptyLine()
        .text("Where " + LIGHT_YELLOW.enclose("<Step>") + " is the " + LIGHT_YELLOW.enclose("Level Step") + ".")
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_MODIFIER_STEP = LangItem.builder("Editor.Modifier.Step")
        .name("Level Step")
        .current("Current", MODIFIER_STEP)
        .emptyLine()
        .text("Determines how often " + LIGHT_YELLOW.enclose("Per Level Value"), "should increase in job levels.")
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_MODIFIER_ACTION = LangItem.builder("Editor.Modifier.Action")
        .name("Action")
        .current("Current", MODIFIER_ACTION)
        .emptyLine()
        .text("Determines math action", "between " + LIGHT_YELLOW.enclose("Base") + " and " + LIGHT_YELLOW.enclose("Per Level") + " values.")
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
        .name("Block List: " + RESET.getBracketsName() + WHITE.enclose(BLOCK_LIST_ID))
        .text(LIGHT_YELLOW.enclose(BOLD.enclose("Blocks:")))
        .textRaw(BLOCK_LIST_MATERIALS)
        .emptyLine()
        .click("edit")
        .shiftRight("delete " + LIGHT_RED.enclose("(no undo)"))
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
