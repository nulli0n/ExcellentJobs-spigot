package su.nightexpress.excellentjobs.job.dialog;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.JobManager;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.excellentjobs.job.impl.JobState;
import su.nightexpress.excellentjobs.user.JobUser;
import su.nightexpress.nightcore.bridge.common.NightNbtHolder;
import su.nightexpress.nightcore.bridge.dialog.wrap.button.WrappedActionButton;
import su.nightexpress.nightcore.core.config.CoreLang;
import su.nightexpress.nightcore.locale.entry.ButtonLocale;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.*;
import su.nightexpress.nightcore.util.Enums;

import java.util.ArrayList;
import java.util.List;

import static su.nightexpress.excellentjobs.Placeholders.*;
import static su.nightexpress.excellentjobs.Placeholders.GENERIC_SECONDARY_LIMIT;

public class JobDialogs {

    public static void openJobStateDialog(@NotNull JobsPlugin plugin, @NotNull Player player, @NotNull Job job) {
        JobManager manager = plugin.getJobManager();
        JobUser user = plugin.getUserManager().getOrFetch(player);
        JobData data = user.getData(job);

        int primLimit = JobManager.getJobsLimit(player, JobState.PRIMARY);
        int secondLimit = JobManager.getJobsLimit(player, JobState.SECONDARY);

        List<WrappedActionButton> buttons = new ArrayList<>();
        for (JobState state : JobState.values()) {
            if (!data.isState(state) && job.isAllowedState(state) && manager.canGetMoreJobs(player, state)) {
                ButtonLocale locale = switch (state) {
                    case PRIMARY -> Lang.DIALOG_JOB_STATUS_BUTTON_GET_PRIMARY;
                    case SECONDARY -> Lang.DIALOG_JOB_STATUS_BUTTON_GET_SECONDARY;
                    case INACTIVE -> Lang.DIALOG_JOB_STATUS_BUTTON_LEAVE;
                };

                buttons.add(DialogButtons.action(locale).action(DialogActions.customClick("state", NightNbtHolder.builder().put("state", state.name()).build())).build());
            }
        }
        if (buttons.isEmpty()) return;

        Dialogs.createAndShow(player, builder -> builder
                .base(DialogBases.builder(Lang.DIALOG_JOB_STATUS_TITLE)
                    .body(DialogBodies.plainMessage(Lang.DIALOG_JOB_STATUS_BODY))
                    .build()
                )
                .type(DialogTypes.multiAction(buttons).columns(1).exitAction(DialogButtons.action(Lang.DIALOG_BUTTON_BACK).build()).build())
                .handleResponse("state", (human, identifier, nbtHolder) -> {
                    if (nbtHolder == null) return;

                    JobState state = Enums.get(nbtHolder.getText("state", "null"), JobState.class);
                    if (state == null) return;

                    manager.joinOrLeaveJob(human, job, state, false);
                    manager.openLevelsMenu(human, job);
                })
            , replacer -> replacer
                .replace(data.replaceAllPlaceholders())
                .replace(GENERIC_PRIMARY_COUNT, () -> String.valueOf(user.countJobs(JobState.PRIMARY)))
                .replace(GENERIC_SECONDARY_COUNT, () -> String.valueOf(user.countJobs(JobState.SECONDARY)))
                .replace(GENERIC_PRIMARY_LIMIT, () -> primLimit < 0 ? CoreLang.OTHER_INFINITY.text() : String.valueOf(primLimit))
                .replace(GENERIC_SECONDARY_LIMIT, () -> secondLimit < 0 ? CoreLang.OTHER_INFINITY.text() : String.valueOf(secondLimit))
        );
    }
}
