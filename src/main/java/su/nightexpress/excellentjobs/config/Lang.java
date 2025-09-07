package su.nightexpress.excellentjobs.config;

import org.bukkit.Sound;
import su.nightexpress.excellentjobs.booster.impl.BoosterType;
import su.nightexpress.excellentjobs.command.impl.BaseCommands;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.zone.command.ZoneCommands;
import su.nightexpress.nightcore.language.entry.*;
import su.nightexpress.nightcore.locale.LangContainer;
import su.nightexpress.nightcore.locale.LangEntry;
import su.nightexpress.nightcore.locale.entry.*;
import su.nightexpress.nightcore.locale.message.MessageData;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.nightcore.util.text.night.wrapper.TagWrappers.*;

public class Lang implements LangContainer {

    public static final EnumLocale<JobState>    JOB_STATE    = LangEntry.builder("Job.State").enumeration(JobState.class);
    public static final EnumLocale<BoosterType> BOOSTER_TYPE = LangEntry.builder("BoosterType").enumeration(BoosterType.class);

    public static final TextLocale COMMAND_ARGUMENT_NAME_ZONE           = LangEntry.builder("Command.Argument.Name.Zone").text("zone");
    public static final TextLocale COMMAND_ARGUMENT_NAME_CURRENCY       = LangEntry.builder("Command.Argument.Name.Currency").text("currency");
    public static final TextLocale COMMAND_ARGUMENT_NAME_JOB            = LangEntry.builder("Command.Argument.Name.Job").text("job");
    public static final TextLocale COMMAND_ARGUMENT_NAME_JOB_STATE      = LangEntry.builder("Command.Argument.Name.JobState").text("job state");
    public static final TextLocale COMMAND_ARGUMENT_NAME_XP_MULTIPLIER  = LangEntry.builder("Command.Argument.Name.XPMultiplier").text("xp mult");
    public static final TextLocale COMMAND_ARGUMENT_NAME_PAY_MULTIPLIER = LangEntry.builder("Command.Argument.Name.PayMultiplier").text("pay mult");
    public static final TextLocale COMMAND_ARGUMENT_NAME_DURATION       = LangEntry.builder("Command.Argument.Name.Duration").text("duration");
    public static final TextLocale COMMAND_ARGUMENT_NAME_PAGE           = LangEntry.builder("Command.Argument.Name.Page").text("page");
    public static final TextLocale COMMAND_ARGUMENT_NAME_ACTION         = LangEntry.builder("Command.Argument.Name.Action").text("action");

    public static final TextLocale COMMAND_ZONE_DESC        = LangEntry.builder("Command.Zone.Desc").text("Zone commands.");
    public static final TextLocale COMMAND_ZONE_CREATE_DESC = LangEntry.builder("Command.Zone.Create.Desc").text("Create a new zone from selection.");
    public static final TextLocale COMMAND_ZONE_WAND_DESC   = LangEntry.builder("Command.Zone.Wand.Desc").text("Get zone selection tool.");
    public static final TextLocale COMMAND_ZONE_EDITOR_DESC = LangEntry.builder("Command.Zone.Editor.Desc").text("Open zones editor.");

    public static final TextLocale COMMAND_LEVEL_DESC      = LangEntry.builder("Command.Level.Desc").text("Level management.");
    public static final TextLocale COMMAND_XP_DESC         = LangEntry.builder("Command.XP.Desc").text("XP management.");
    public static final TextLocale COMMAND_RESET_DESC      = LangEntry.builder("Command.Reset.Desc").text("Reset job progress.");
    public static final TextLocale COMMAND_MENU_DESC       = LangEntry.builder("Command.Menu.Desc").text("Open Jobs GUI.");
    public static final TextLocale COMMAND_LEVELS_DESC     = LangEntry.builder("Command.Levels.Desc").text("Browse job levels.");
    public static final TextLocale COMMAND_JOIN_DESC       = LangEntry.builder("Command.Join.Desc").text("Join a job.");
    public static final TextLocale COMMAND_LEAVE_DESC      = LangEntry.builder("Command.Leave.Desc").text("Leave a job.");
    public static final TextLocale COMMAND_SET_STATE_DESC  = LangEntry.builder("Command.SetState.Desc").text("Set player's job state.");
    public static final TextLocale COMMAND_TOP_DESC        = LangEntry.builder("Command.Top.Desc").text("List most levelled players.");
    public static final TextLocale COMMAND_STATS_DESC      = LangEntry.builder("Command.Stats.Desc").text("View job stats.");

    public static final TextLocale COMMAND_BOOSTS_DESC           = LangEntry.builder("Command.Boosters.Desc").text("View all current boosters.");
    public static final TextLocale COMMAND_BOOSTER_DESC          = LangEntry.builder("Command.Booster.Desc").text("Booster management.");
    public static final TextLocale COMMAND_BOOSTER_ACTIVATE_DESC = LangEntry.builder("Command.Booster.Activate.Desc").text("Activate global scheduled booster.");
    public static final TextLocale COMMAND_BOOSTER_CREATE_DESC   = LangEntry.builder("Command.Booster.Create.Desc").text("Create global or player booster.");
    public static final TextLocale COMMAND_BOOSTER_INFO_DESC     = LangEntry.builder("Command.Booster.Info.Desc").text("View active booster names.");
    public static final TextLocale COMMAND_BOOSTER_REMOVE_DESC   = LangEntry.builder("Command.Booster.Removal.Desc").text("Remove global or personal booster.");

    public static final MessageLocale COMMAND_STATS_DISPLAY = LangEntry.builder("Command.Stats.Display.Info").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        YELLOW.wrap(ORANGE.wrap(PLAYER_NAME) + "'s Job Stats:"),
        " ",
        GENERIC_ENTRY,
        " "
    );

    public static final TextLocale COMMAND_STATS_ENTRY = LangEntry.builder("Command.Stats.Display.Job").text(
        GRAY.wrap(ORANGE.wrap("▪ ") + JOB_NAME + ":   Level: " + ORANGE.wrap(JOB_DATA_LEVEL) + ", XP: " + ORANGE.wrap(JOB_DATA_XP) + "/" + ORANGE.wrap(JOB_DATA_XP_MAX))
    );


    public static final MessageLocale COMMAND_XP_ADD_DONE = LangEntry.builder("Command.XP.Add.Done").chatMessage(
        GRAY.wrap("Added " + GREEN.wrap(GENERIC_AMOUNT) + " XP to " + GREEN.wrap(PLAYER_NAME) + "'s " + GREEN.wrap(JOB_NAME) + " job.")
    );

    public static final MessageLocale COMMAND_XP_REMOVE_DONE = LangEntry.builder("Command.XP.Remove.Done").chatMessage(
        GRAY.wrap("Removed " + RED.wrap(GENERIC_AMOUNT) + " XP from " + RED.wrap(PLAYER_NAME) + "'s " + RED.wrap(JOB_NAME) + " job.")
    );

    public static final MessageLocale COMMAND_XP_SET_DONE = LangEntry.builder("Command.XP.Set.Done").chatMessage(
        GRAY.wrap("Set " + YELLOW.wrap(GENERIC_AMOUNT) + " XP for " + YELLOW.wrap(PLAYER_NAME) + "'s " + YELLOW.wrap(JOB_NAME) + " job.")
    );



    public static final MessageLocale COMMAND_LEVEL_ADD_DONE = LangEntry.builder("Command.Level.Add.Done").chatMessage(
        GRAY.wrap("Added " + GREEN.wrap(GENERIC_AMOUNT) + " level(s) to " + GREEN.wrap(PLAYER_NAME) + "'s " + GREEN.wrap(JOB_NAME) + " job.")
    );

    public static final MessageLocale COMMAND_LEVEL_REMOVE_DONE = LangEntry.builder("Command.Level.Remove.Done").chatMessage(
        GRAY.wrap("Removed " + RED.wrap(GENERIC_AMOUNT) + " level(s) from " + RED.wrap(PLAYER_NAME) + "'s " + RED.wrap(JOB_NAME) + " job.")
    );

    public static final MessageLocale COMMAND_LEVEL_SET_DONE = LangEntry.builder("Command.Level.Set.Done").chatMessage(
        GRAY.wrap("Set " + YELLOW.wrap(GENERIC_AMOUNT) + " level for " + YELLOW.wrap(PLAYER_NAME) + "'s " + YELLOW.wrap(JOB_NAME) + " job.")
    );



    public static final MessageLocale COMMAND_RESET_DONE = LangEntry.builder("Command.Reset.Done").chatMessage(
        GRAY.wrap("Successfully reset " + YELLOW.wrap(JOB_NAME) + " progress for " + YELLOW.wrap(PLAYER_NAME) + ".")
    );


    public static final MessageLocale COMMAND_SET_STATE_DONE = LangEntry.builder("Command.SetState.Done").chatMessage(
        GRAY.wrap("Set " + YELLOW.wrap(GENERIC_STATE) + " state for " + YELLOW.wrap(PLAYER_NAME) + "'s " + YELLOW.wrap(JOB_NAME) + " job!"));


    public static final MessageLocale COMMAND_TOP_LIST = LangEntry.builder("Command.Top.List").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        YELLOW.wrap(BOLD.wrap(JOB_NAME + " Level Top:")),
        " ",
        GENERIC_ENTRY,
        " ",
        GRAY.wrap("Page " + YELLOW.wrap(GENERIC_CURRENT) + " of " + YELLOW.wrap(GENERIC_MAX) + "."),
        " "
    );

    public static final TextLocale COMMAND_TOP_ENTRY = LangEntry.builder("Command.Top.Entry").text(
        GRAY.wrap(YELLOW.wrap(GENERIC_POS + ". ") + PLAYER_NAME + ": " + YELLOW.wrap(GENERIC_AMOUNT) + " Levels")
    );


    public static final MessageLocale COMMAND_BOOSTER_ACTIVATE_DONE = LangEntry.builder("Command.Booster.Activate.Done").chatMessage(
        GRAY.wrap("Booster activated!")
    );

    public static final MessageLocale COMMAND_BOOSTER_CREATE_DONE_GLOBAL = LangEntry.builder("Command.Booster.Create.Done.Global").chatMessage(
        GRAY.wrap("Added global " + YELLOW.wrap(BOOSTER_XP_PERCENT + " XP") + " " + YELLOW.wrap(BOOSTER_INCOME_PERCENT + " Payment") + " job booster " + GRAY.wrap("(" + WHITE.wrap(GENERIC_TIME) + ")"))
    );

    public static final MessageLocale COMMAND_BOOSTER_CREATE_DONE_PERSONAL = LangEntry.builder("Command.Booster.Create.Done.Personal").chatMessage(
        GRAY.wrap("Added personal " + YELLOW.wrap(BOOSTER_XP_PERCENT + " XP") + " " + YELLOW.wrap(BOOSTER_INCOME_PERCENT + " Payment") + " job booster " + GRAY.wrap("(" + WHITE.wrap(GENERIC_TIME) + ")") + " for " + YELLOW.wrap(PLAYER_NAME) + "'s " + YELLOW.wrap(JOB_NAME) + " job.")
    );

    public static final MessageLocale COMMAND_BOOSTER_REMOVE_DONE_PERSONAL = LangEntry.builder("Command.Booster.Remove.Done.Personal").chatMessage(
        GRAY.wrap("Disabled " + YELLOW.wrap(PLAYER_NAME) + "'s " + YELLOW.wrap(JOB_NAME) + " job booster.")
    );

    public static final MessageLocale COMMAND_BOOSTER_REMOVE_DONE_GLOBAL = LangEntry.builder("Command.Booster.Remove.Done.Global").chatMessage(
        GRAY.wrap("Disabled global job booster.")
    );

    public static final MessageLocale COMMAND_BOOSTER_REMOVE_ERROR_NOTHING = LangEntry.builder("Command.Booster.Remove.Error.Nothing").chatMessage(
        RED.wrap("There is no booster.")
    );



    public static final MessageLocale JOB_JOIN_ERROR_ALREADY_HIRED = LangEntry.builder("Job.Join.Error.AlreadyHired").chatMessage(
        GRAY.wrap("You're already hired for the " + RED.wrap(JOB_NAME) + " job!"));

    public static final MessageLocale JOB_JOIN_ERROR_COOLDOWN = LangEntry.builder("Job.Join.Error.Cooldown").chatMessage(
        GRAY.wrap("You can join the " + RED.wrap(JOB_NAME) + " job again in " + RED.wrap(GENERIC_TIME)));

    public static final MessageLocale JOB_JOIN_ERROR_LIMIT_STATE = LangEntry.builder("Job.Join.Error.StateLimit").chatMessage(
        GRAY.wrap("You can't get more than " + RED.wrap(GENERIC_AMOUNT + " " + GENERIC_STATE) + " jobs!"));

    public static final MessageLocale JOB_JOIN_ERROR_LIMIT_GENERAL = LangEntry.builder("Job.Join.Error.GeneralLimit").chatMessage(
        GRAY.wrap("You can't get more jobs!"));

    public static final MessageLocale JOB_JOIN_NOT_JOINABLE = LangEntry.builder("Job.Join.NotJoinable").chatMessage(
        GRAY.wrap("The " + SOFT_YELLOW.wrap(JOB_NAME) + " job does not accept new members currently.")
    );

    public static final MessageLocale JOB_PRIORITY_CHANGED = LangEntry.builder("Job.Priority.Changed").chatMessage(
        Sound.ENTITY_PLAYER_LEVELUP,
        GRAY.wrap("You changed " + YELLOW.wrap(JOB_NAME) + "'s job priority to " + YELLOW.wrap(JOB_DATA_STATE) + ".")
    );

    public static final MessageLocale JOB_JOIN_SUCCESS = LangEntry.builder("Job.Join.Success").message(
        MessageData.chat().usePrefix(false).sound(Sound.ENTITY_PLAYER_LEVELUP).build(),
        " ",
        GRAY.wrap("You joined the " + YELLOW.wrap(JOB_NAME) + " job!"),
        " "
    );

    public static final MessageLocale JOB_LEAVE_SUCCESS = LangEntry.builder("Job.Leave.Success").titleMessage(
        GREEN.wrap(BOLD.wrap("Job Quit")),
        GRAY.wrap("You quit the " + GREEN.wrap(JOB_NAME) + " job!"),
        20, 60, Sound.BLOCK_WOODEN_DOOR_CLOSE
    );

    public static final MessageLocale JOB_LEAVE_ERROR_NOT_JOINED = LangEntry.builder("Job.Leave.Error.NotJoined").chatMessage(
        GRAY.wrap("You're not employed for the " + RED.wrap(JOB_NAME) + " job!"));

    public static final MessageLocale JOB_LEAVE_ERROR_COOLDOWN = LangEntry.builder("Job.Leave.Error.Cooldown").chatMessage(
        GRAY.wrap("You can leave the " + RED.wrap(JOB_NAME) + " job in " + RED.wrap(GENERIC_TIME)));

    public static final MessageLocale JOB_LEAVE_ERROR_NOT_ALLOWED = LangEntry.builder("Job.Leave.Error.NotAllowed").chatMessage(
        GRAY.wrap("You can't leave the " + RED.wrap(JOB_NAME) + " job!"));

    public static final MessageLocale JOB_RESET_NOTIFY = LangEntry.builder("Job.Reset.Notify").titleMessage(
        GREEN.wrap(BOLD.wrap("Job Reset!")),
        GRAY.wrap("All " + GREEN.wrap(JOB_NAME) + " progress have been reset!"),
        Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR
    );



    public static final MessageLocale JOB_PAYMENT_NOTIFY = LangEntry.builder("Job.Payment.Notify").message(
        MessageData.CHAT_NO_PREFIX,
        GRAY.wrap(GREEN.wrap("[$]") + " You got paid for the " + WHITE.wrap(JOB_NAME) + "'s work: " + GREEN.wrap(GENERIC_AMOUNT))
    );

    public static final MessageLocale JOB_XP_GAIN = LangEntry.builder("Job.XP.Gain").chatMessage(
        GRAY.wrap("You gain " + YELLOW.wrap(GENERIC_AMOUNT + " XP") + " for " + YELLOW.wrap(JOB_NAME) + " job.")
    );

    public static final MessageLocale JOB_XP_LOSE = LangEntry.builder("Job.XP.Lose").chatMessage(
        GRAY.wrap("You lost " + RED.wrap(GENERIC_AMOUNT + " XP") + " from " + RED.wrap(JOB_NAME) + " job.")
    );

    public static final MessageLocale JOB_LEVEL_UP = LangEntry.builder("Job.Level.Up").titleMessage(
        GREEN.wrap(BOLD.wrap("Job Level Up!")),
        GRAY.wrap(GREEN.wrap(JOB_NAME) + " is now level " + GREEN.wrap(JOB_DATA_LEVEL) + "!"),
        Sound.ENTITY_PLAYER_LEVELUP
    );

    public static final MessageLocale JOB_LEVEL_DOWN = LangEntry.builder("Job.Level.Down").titleMessage(
        RED.wrap(BOLD.wrap("Job Level Down!")),
        GRAY.wrap(RED.wrap(JOB_NAME) + " is now level " + RED.wrap(JOB_DATA_LEVEL) + "!"),
        Sound.ENTITY_IRON_GOLEM_DEATH
    );

    public static final MessageLocale JOB_REWARDS_NOTIFY = LangEntry.builder("Job.Rewards.Notify").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        SOFT_YELLOW.and(BOLD).and(UNDERLINED).wrap("Job Notification"),
        " ",
        GRAY.wrap("You have " + SOFT_YELLOW.wrap(GENERIC_AMOUNT) + " unclaimed rewards in " + SOFT_YELLOW.wrap(JOB_NAME) + " job."),
        GRAY.wrap("Click " + RUN_COMMAND.with("/" + BaseCommands.JOBS_ALIAS + " " + BaseCommands.LEVELS_ALIAS + " " + JOB_ID).wrap(SOFT_YELLOW.wrap("[Here]")) + " to claim now!"),
        " "
    );

    public static final MessageLocale JOB_LEVEL_REWARDS_LIST = LangEntry.builder("Job.Level.Rewards.List").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        YELLOW.wrap(BOLD.wrap("Level Rewards:")),
        DARK_GRAY.wrap("Hover over reward name for details."),
        " ",
        GENERIC_ENTRY,
        " "
    );

    public static final TextLocale JOB_LEVEL_REWARDS_ENTRY = LangEntry.builder("Job.Level.Rewards.Entry").text(
        YELLOW.wrap("✔ " + SHOW_TEXT.with(GRAY.wrap(REWARD_DESCRIPTION)).wrap(GRAY.wrap(REWARD_NAME)))
    );

    public static final MessageLocale JOB_LIMIT_XP_NOTIFY = LangEntry.builder("Job.Limit.XP.Notify").chatMessage(
        GRAY.wrap("You have reached daily " + RED.wrap("XP") + " limit for " + RED.wrap(JOB_NAME) + " job. You can't get more today.")
    );

    public static final MessageLocale JOB_LIMIT_CURRENCY_NOTIFY = LangEntry.builder("Job.Limit.Currency.Notify").chatMessage(
        GRAY.wrap("You have reached daily " + RED.wrap(CURRENCY_NAME) + " limit for " + RED.wrap(JOB_NAME) + " job. You can't get more today.")
    );

    public static final MessageLocale BOOSTER_ACTIVATED_GLOBAL = LangEntry.builder("Booster.Activated.Global").message(
        MessageData.chat().usePrefix(false).sound(Sound.BLOCK_NOTE_BLOCK_BELL).build(),
        " ",
        YELLOW.wrap(BOLD.wrap("Job Booster Activated!")),
        " ",
        ITALIC.wrap(GRAY.wrap("Available for " + WHITE.wrap("all jobs") + " and " + WHITE.wrap("all players") + "!")),
        " ",
        YELLOW.wrap("✔ " + GRAY.wrap("XP Boost: ") + BOOSTER_XP_PERCENT),
        YELLOW.wrap("✔ " + GRAY.wrap("Income Boost: ") + BOOSTER_INCOME_PERCENT),
        YELLOW.wrap("✔ " + GRAY.wrap("Duration: ") + GENERIC_TIME),
        " "
    );

    public static final MessageLocale BOOSTER_ACTIVATED_PERSONAL = LangEntry.builder("Booster.Activated.Personal").message(
        MessageData.chat().usePrefix(false).sound(Sound.BLOCK_NOTE_BLOCK_BELL).build(),
        " ",
        YELLOW.wrap(BOLD.wrap("Job Booster Received!")),
        " ",
        ITALIC.wrap(GRAY.wrap("Available " + WHITE.wrap("personally") + " for your " + WHITE.wrap(JOB_NAME) + " job.")),
        " ",
        YELLOW.wrap("✔ " + GRAY.wrap("XP Boost: ") + BOOSTER_XP_PERCENT),
        YELLOW.wrap("✔ " + GRAY.wrap("Income Boost: ") + BOOSTER_INCOME_PERCENT),
        YELLOW.wrap("✔ " + GRAY.wrap("Duration: ") + GENERIC_TIME),
        " "
    );

    public static final MessageLocale BOOSTER_EXPIRED_GLOBAL = LangEntry.builder("Booster.Expired.Global").chatMessage(
        GRAY.wrap("Global job booster has been expired.")
    );

    public static final MessageLocale BOOSTER_EXPIRED_PERSONAL = LangEntry.builder("Booster.Expired.Personal").chatMessage(
        GRAY.wrap("Your " + YELLOW.wrap(JOB_NAME) + " job booster has been expired.")
    );

    public static final MessageLocale BOOSTER_LIST_INFO = LangEntry.builder("Booster.List.Info").message(
        MessageData.CHAT_NO_PREFIX,
        " ",
        YELLOW.wrap(BOLD.wrap(JOB_NAME + " Job Boosters:")),
        " ",
        ITALIC.wrap(GRAY.wrap("Legend: " + WHITE.wrap("XP") + " | " + WHITE.wrap("Income"))),
        " ",
        GENERIC_ENTRY,
        " ",
        YELLOW.wrap(BOLD.wrap("Total:")) + " " + GENERIC_XP_BONUS + GRAY.wrap(" | ") + GENERIC_INCOME_BONUS,
        " "
    );

    public static final TextLocale BOOSTER_LIST_ENTRY = LangEntry.builder("Booster.List.Entry").text(
        YELLOW.wrap("✔ " + GRAY.wrap(GENERIC_TYPE + " Booster: ") + GENERIC_XP_BOOST + GRAY.wrap(" | ") + GENERIC_INCOME_BOOST + " " + GRAY.wrap("(" + WHITE.wrap(GENERIC_TIME) + ")"))
    );

    public static final MessageLocale BOOSTER_LIST_NOTHING = LangEntry.builder("Booster.List.Nothing").chatMessage(
        RED.wrap("You have no active boosters for this job.")
    );


    public static final MessageLocale ZONE_NOT_AVAILABLE = LangEntry.builder("Zone.Info.NotAvailable").actionBarMessage(
        RED.wrap("You can't work in this zone currently!")
    );

    public static final MessageLocale ZONE_NO_PVP = LangEntry.builder("Zone.Info.NoPvP").actionBarMessage(
        RED.wrap("PvP is disabled in this zone!")
    );

    public static final MessageLocale ZONE_CREATE_SUCCESS = LangEntry.builder("Zone.Creation.Success").chatMessage(
        GRAY.wrap("Successfully created job zone: " + GREEN.wrap(ZONE_NAME) + "!")
    );

    public static final MessageLocale ZONE_CREATE_INFO = LangEntry.builder("Zone.Creation.Info").chatMessage(
        GRAY.wrap("Select two corners and use " + GREEN.wrap("/" + ZoneCommands.DEF_ROOT_NAME + " " + ZoneCommands.DEF_CREATE_NAME) + " to create a new zone.")
    );

    public static final MessageLocale ZONE_SELECTION_INFO = LangEntry.builder("Zone.Selection.Info").chatMessage(
        GRAY.wrap("Selected " + YELLOW.wrap("#" + GENERIC_VALUE) + " zone position.")
    );

    public static final MessageLocale ZONE_ERROR_EXISTS = LangEntry.builder("Zone.Error.AlreadyExists").chatMessage(
        GRAY.wrap("Zone with name " + RED.wrap(GENERIC_NAME) + " is already created!")
    );

    public static final MessageLocale ZONE_ERROR_NO_SELECTION = LangEntry.builder("Zone.Error.NoSelection").chatMessage(
        GRAY.wrap("You must select zone corners first: " + RED.wrap("/" + ZoneCommands.DEF_ROOT_NAME + " " + ZoneCommands.DEF_WAND_NAME))
    );

    public static final MessageLocale ZONE_ERROR_INCOMPLETE_SELECTION = LangEntry.builder("Zone.Error.IncompleteSelection").chatMessage(
        GRAY.wrap("You must select 2 zone corners!")
    );


    public static final ButtonLocale DIALOG_BUTTON_BACK = LangEntry.builder("Dialog.Generic.Button.Back").button(SOFT_YELLOW.wrap("←") + " Back");

    public static final TextLocale DIALOG_JOB_STATUS_TITLE = LangEntry.builder("Dialog.JobStatus.Title").text(SOFT_YELLOW.wrap(BOLD.and(UNDERLINED).wrap("Job Status")));

    public static final DialogElementLocale DIALOG_JOB_STATUS_BODY = LangEntry.builder("Dialog.JobStatus.Body").dialogElement(
        400,
        "Select a new status for the " + SOFT_YELLOW.wrap(JOB_NAME) + " job.",
        "",
        "Current Status: " + SOFT_YELLOW.wrap(JOB_DATA_STATE),
        "",
        GREEN.wrap("Primary") + " jobs give extra, but " + GREEN.wrap("smaller") + ", income and level rewards.",
        "",
        SOFT_BLUE.wrap("Secondary") + " jobs give the " + SOFT_BLUE.wrap("highest") + " income and level rewards.",
        ""
    );
    
    public static final ButtonLocale DIALOG_JOB_STATUS_BUTTON_GET_PRIMARY   = LangEntry.builder("Dialog.JobStatus.Button.GetPrimary").button(GREEN.wrap("✔") + " Get as Primary");
    public static final ButtonLocale DIALOG_JOB_STATUS_BUTTON_GET_SECONDARY = LangEntry.builder("Dialog.JobStatus.Button.GetSecondary").button(BLUE.wrap("✔") + " Get as Secondary");
    public static final ButtonLocale DIALOG_JOB_STATUS_BUTTON_LEAVE         = LangEntry.builder("Dialog.JobStatus.Button.Leave").button(RED.wrap("✘") + " Leave Job");



    public static final MessageLocale ERROR_COMMAND_INVALID_ZONE_ARGUMENT = LangEntry.builder("Error.Command.Argument.InvalidZone").chatMessage(
        GRAY.wrap(RED.wrap(GENERIC_VALUE) + " is not a valid zone!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_JOB_ARGUMENT = LangEntry.builder("Error.Command.Argument.InvalidJob").chatMessage(
        GRAY.wrap(RED.wrap(GENERIC_VALUE) + " is not a valid job!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_JOB_STATE_ARGUMENT = LangEntry.builder("Error.Command.Argument.InvalidJobState").chatMessage(
        GRAY.wrap(RED.wrap(GENERIC_VALUE) + " is not a valid job state!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_ACTION_ARGUMENT = LangEntry.builder("Error.Command.Argument.InvalidAction").chatMessage(
        GRAY.wrap(RED.wrap(GENERIC_VALUE) + " is not a valid action!"));

    public static final MessageLocale ERROR_INVALID_BOOSTER = LangEntry.builder("Error.InvalidBooster").chatMessage(
        RED.wrap("Invalid booster!"));

    public static final MessageLocale ERROR_COMMAND_INVALID_CURRENCY_ARGUMENT = LangEntry.builder("Error.Error.Command.Argument.InvalidCurrency").chatMessage(
        GRAY.wrap(RED.wrap(GENERIC_VALUE) + " is not a valid currency!"));


    public static final TextLocale OTHER_NO_JOBS            = LangEntry.builder("Other.NoJobs").text(GRAY.wrap("<No Jobs>"));
    public static final TextLocale OTHER_NO_INCOME          = LangEntry.builder("Other.NoIncome").text(GRAY.wrap("$0"));
    public static final TextLocale OTHER_JOB_DELIMITER      = LangEntry.builder("Other.JobDelimiter").text(", ");
    public static final TextLocale OTHER_CURRENCY_DELIMITER = LangEntry.builder("Other.CurrencyDelimiter").text(", ");
    public static final TextLocale OTHER_BONUS_POSITIVE = LangEntry.builder("Other.BonusPlus").text(GREEN.wrap("+" + GENERIC_VALUE + "%"));
    public static final TextLocale OTHER_BONUS_NEGATIVE = LangEntry.builder("Other.BonusMinus").text(RED.wrap(GENERIC_VALUE + "%"));

    public static final TextLocale EDITOR_TITLE_ZONES                  = LangEntry.builder("Editor.Title.Zone.List").text(BLACK.wrap("Job Zones"));
    public static final TextLocale EDITOR_TITLE_ZONE_SETTINGS          = LangEntry.builder("Editor.Title.Zone.Settings").text(BLACK.wrap("Zone Settings"));
    public static final TextLocale EDITOR_TITLE_ZONE_BLOCK_LIST        = LangEntry.builder("Editor.Title.Zone.BlockList").text(BLACK.wrap("Zone Block Lists"));
    public static final TextLocale EDITOR_TITLE_ZONE_BLOCK_SETTINGS    = LangEntry.builder("Editor.Title.Zone.BlockSettings").text(BLACK.wrap("Block List Settings"));
    public static final TextLocale EDITOR_TITLE_ZONE_MODIFIER_LIST     = LangEntry.builder("Editor.Title.Zone.ModifierList").text(BLACK.wrap("Zone Modifiers"));
    public static final TextLocale EDITOR_TITLE_ZONE_MODIFIER_SETTINGS = LangEntry.builder("Editor.Title.Zone.ModifierSettings").text(BLACK.wrap("Modifier Settings"));
    public static final TextLocale EDITOR_TITLE_ZONE_HOURS             = LangEntry.builder("Editor.Title.Zone.Hours").text(BLACK.wrap("Zone Hours"));

    public static final TextLocale EDITOR_GENERIC_ENTER_ID = LangEntry.builder("Editor.Generic.Enter.Id").text(GRAY.wrap("Enter " + GREEN.wrap("[Unique Identifier]")));
    public static final TextLocale EDITOR_GENERIC_ENTER_NAME = LangEntry.builder("Editor.Generic.Enter.Name").text(GRAY.wrap("Enter " + GREEN.wrap("[Display Name]")));
    public static final TextLocale EDITOR_GENERIC_ENTER_NUMBER = LangEntry.builder("Editor.Generic.Enter.Number").text(GRAY.wrap("Enter " + GREEN.wrap("[Number]")));
    public static final TextLocale EDITOR_GENERIC_ENTER_MIN_MAX = LangEntry.builder("Editor.Generic.Enter.MinMax").text(GRAY.wrap("Enter " + GREEN.wrap("[Min] [Max]")));
    public static final TextLocale EDITOR_GENERIC_ENTER_TIMES = LangEntry.builder("Editor.Generic.Enter.Times").text(GRAY.wrap("Enter " + GREEN.wrap("[Hours] [12:00 17:00]")));
    public static final TextLocale EDITOR_GENERIC_ENTER_CURRENCY = LangEntry.builder("Editor.Generic.Enter.Currency").text(GRAY.wrap("Enter " + GREEN.wrap("[Currency Identifier]")));
    public static final TextLocale EDITOR_GENERIC_ENTER_MATERIAL = LangEntry.builder("Editor.Generic.Enter.Material").text(GRAY.wrap("Enter " + GREEN.wrap("[Material Name]")));
    public static final TextLocale EDITOR_ZONE_ENTER_DESCRIPTION = LangEntry.builder("Editor.Zone.Enter.Description").text(GRAY.wrap("Enter " + GREEN.wrap("[Description Line]")));
    public static final TextLocale EDITOR_ZONE_ENTER_JOB_ID = LangEntry.builder("Editor.Zone.Enter.JobId").text(GRAY.wrap("Enter " + GREEN.wrap("[Job Identifier]")));

    public static final LangItem EDITOR_ZONE_OBJECT = LangItem.builder("Editor.Zone.Objectv180")
        .name(ZONE_NAME + RESET.opening() + GRAY.wrap(" (ID: " + WHITE.wrap(ZONE_ID) + ")"))
        .textRaw(ZONE_JOB_NAMES)
        .emptyLine()
        .leftClick("edit")
        .dragAndDrop("set icon")
        .shiftRight("delete " + RED.wrap("(no undo)"))
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
        .text(YELLOW.wrap("0.5") + " = " + GREEN.wrap("+50%"))
        .text(YELLOW.wrap("-0.25") + " = " + RED.wrap("-25%"))
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
        .text("Adjusts " + YELLOW.wrap(CURRENCY_NAME) + " amount a player", "can get for their work in this zone.")
        .emptyLine()
        .text("Final value should be plain multipier:")
        .text(YELLOW.wrap("0.5") + " = " + GREEN.wrap("+50%"))
        .text(YELLOW.wrap("-0.25") + " = " + RED.wrap("-25%"))
        .emptyLine()
        .click("edit")
        .dropKey("delete " + RED.wrap("(no undo)"))
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
        .text("The number increases by itself", "for each " + YELLOW.wrap("<Level Step>") + " job levels.")
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_MODIFIER_STEP = LangItem.builder("Editor.Modifier.Step")
        .name("Level Step")
        .current("Current", MODIFIER_STEP)
        .emptyLine()
        .text("Sets how often " + YELLOW.wrap("Per Level Value"), "should increase in job levels.")
        .emptyLine()
        .click("change")
        .build();

    public static final LangItem EDITOR_MODIFIER_ACTION = LangItem.builder("Editor.Modifier.Action")
        .name("Action")
        .current("Current", MODIFIER_ACTION)
        .emptyLine()
        .text("Sets math action", "between " + YELLOW.wrap("Base") + " and " + YELLOW.wrap("Per Level") + " values.")
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
        .name("Block List: " + RESET.opening() + WHITE.wrap(BLOCK_LIST_ID))
        .text(YELLOW.wrap(BOLD.wrap("Blocks:")))
        .textRaw(BLOCK_LIST_MATERIALS)
        .emptyLine()
        .click("edit")
        .shiftRight("delete " + RED.wrap("(no undo)"))
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
