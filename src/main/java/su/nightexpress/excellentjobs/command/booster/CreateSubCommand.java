package su.nightexpress.excellentjobs.command.booster;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.excellentjobs.JobsPlugin;
import su.nightexpress.excellentjobs.Placeholders;
import su.nightexpress.excellentjobs.booster.config.BoosterInfo;
import su.nightexpress.excellentjobs.booster.impl.ExpirableBooster;
import su.nightexpress.excellentjobs.command.CommandFlags;
import su.nightexpress.excellentjobs.config.Config;
import su.nightexpress.excellentjobs.config.Lang;
import su.nightexpress.excellentjobs.config.Perms;
import su.nightexpress.excellentjobs.job.impl.Job;
import su.nightexpress.nightcore.command.CommandResult;
import su.nightexpress.nightcore.command.impl.AbstractCommand;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.Players;
import su.nightexpress.nightcore.util.TimeUtil;

import java.util.*;
import java.util.stream.Collectors;

class CreateSubCommand extends AbstractCommand<JobsPlugin> {

    public CreateSubCommand(@NotNull JobsPlugin plugin) {
        super(plugin, new String[]{"create"}, Perms.COMMAND_BOOSTER);
        this.setDescription(Lang.COMMAND_BOOSTER_CREATE_DESC);
        this.setUsage(Lang.COMMAND_BOOSTER_CREATE_USAGE);
        this.addFlag(CommandFlags.SILENT);
    }

    @Override
    @NotNull
    public List<String> getTab(@NotNull Player player, int arg, @NotNull String[] args) {
        if (arg == 2) {
            return Players.playerNames(player);
        }
        if (arg == 3) {
            return new ArrayList<>(Config.BOOSTERS_CUSTOM.get().keySet());
        }
        if (arg == 4) {
            return Arrays.asList("600", "3600", "7200", "86400");
        }
        return super.getTab(player, arg, args);
    }

    @Override
    protected void onExecute(@NotNull CommandSender sender, @NotNull CommandResult result) {
        if (result.length() < 5) {
            this.errorUsage(sender);
            return;
        }

        BoosterInfo boosterInfo = Config.BOOSTERS_CUSTOM.get().get(result.getArg(3));
        if (boosterInfo == null) {
            Lang.BOOSTER_ERROR_INVALID.getMessage().send(sender);
            return;
        }

        Set<Job> jobs = boosterInfo.getJobs().stream()
            .map(id -> plugin.getJobManager().getJobById(id))
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (jobs.isEmpty()) {
            Lang.BOOSTER_ERROR_INVALID.getMessage().send(sender);
            return;
        }

        int duration = result.getInt(4, 0);
        if (duration <= 0) return;

        plugin.getUserManager().getUserDataAndPerformAsync(result.getArg(2), user -> {
            if (user == null) {
                this.errorPlayer(sender);
                return;
            }

            ExpirableBooster booster = new ExpirableBooster(boosterInfo.getMultiplier(), duration);
            jobs.forEach(job -> {
                user.getBoosterMap().put(job.getId(), booster);
            });
            this.plugin.getUserManager().saveAsync(user);

            Lang.COMMAND_BOOSTER_CREATE_DONE.getMessage()
                .replace(Placeholders.GENERIC_NAME, result.getArg(2))
                .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(booster.getExpireDate()))
                .replace(Placeholders.PLAYER_NAME, user.getName())
                .send(sender);

            Player player = user.getPlayer();
            if (player != null && !result.hasFlag(CommandFlags.SILENT)) {
                String jobName = jobs.stream().map(Job::getName).collect(Collectors.joining(", "));

                Lang.COMMAND_BOOSTER_CREATE_NOTIFY.getMessage()
                    .replace(Placeholders.JOB_NAME, jobName)
                    .replace(Placeholders.GENERIC_TIME, TimeUtil.formatDuration(booster.getExpireDate()))
                    .replace(Placeholders.XP_BOOST_MODIFIER, NumberUtil.format(booster.getMultiplier().getXPMultiplier()))
                    .replace(Placeholders.XP_BOOST_PERCENT, NumberUtil.format(booster.getMultiplier().getXPPercent()))
                    .replace(Placeholders.GENERIC_CURRENCY, list -> {
                        plugin.getCurrencyManager().getCurrencies().forEach(currency -> {
                            double percent = booster.getMultiplier().getCurrencyPercent(currency);
                            double modifier = booster.getMultiplier().getCurrencyMultiplier(currency);
                            list.add(currency.replacePlaceholders().apply(Lang.COMMAND_BOOSTER_CREATE_NOTIFY_CURRENCY.getString())
                                .replace(Placeholders.CURRENCY_BOOST_PERCENT, NumberUtil.format(percent))
                                .replace(Placeholders.CURRENCY_BOOST_MODIFIER, NumberUtil.format(modifier))
                            );
                        });
                    })
                    .send(player);
            }
        });
    }
}
