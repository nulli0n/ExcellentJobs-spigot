package su.nightexpress.excellentjobs.job.dialog;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.data.impl.JobData;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.bridge.dialog.wrap.WrappedDialog;
import su.nightexpress.nightcore.ui.dialog.Dialogs;
import su.nightexpress.nightcore.ui.dialog.build.*;

public class JobDialogs {

    private static final String LEAVE_YES = "yes";

    public static void openLeaveConfirm(@NotNull JobsPlugin plugin, @NotNull Player player, @NotNull Job job) {
        JobData data = plugin.getUserManager().getOrFetch(player).getData(job);

        WrappedDialog dialog = Dialogs.create(builder -> builder
            .type(DialogTypes.confirmation(
                DialogButtons.action(Lang.DIALOG_LEAVE_BUTTON_CONFIRM.getString()).action(DialogActions.customClick(LEAVE_YES)).build(),
                DialogButtons.action(Lang.DIALOG_LEAVE_BUTTON_CANCEL.getString()).build())
            )
            .base(DialogBases.builder(Lang.DIALOG_LEAVE_TITLE.getString()).body(DialogBodies.plainMessage(data.replaceAllPlaceholders().apply(Lang.DIALOG_LEAVE_BODY.getString()))).build())
            .handleResponse(LEAVE_YES, (player1, identifier, nbtHolder) -> {
                plugin.getJobManager().leaveJob(player1, job);
                player.closeInventory();
            })
        );

        Dialogs.showDialog(player, dialog);
    }
}
