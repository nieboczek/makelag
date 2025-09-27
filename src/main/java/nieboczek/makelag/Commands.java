package nieboczek.makelag;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import nieboczek.makelag.config.Config;
import nieboczek.makelag.config.PlayerConfig;
import nieboczek.makelag.module.Modules;
import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.network.PingDisplayS2CPacket;
import nieboczek.makelag.progression.ProgressionManager;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static final LiteralArgumentBuilder<ServerCommandSource> command = literal("makelag");

    private static final SuggestionProvider<ServerCommandSource> PROGRESSION_SUGGESTIONS = (context, builder) ->
            getFileSuggestionsWithoutExtensions(builder, new File(Config.getCfgDir(), "progressions"));

    private static void sendFeedback(CommandContext<ServerCommandSource> context, String msg) {
        context.getSource().sendFeedback(() -> Text.literal(MakeLag.MSG_PREFIX + msg), true);
    }

    private static <T> int executeCommand(CommandContext<ServerCommandSource> context, String moduleName, Key<T> key) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        PlayerConfig config = MakeLag.getConfig(player);

        T value = context.getArgument("value", key.clazz());
        config.states.stream()
                .filter(state -> state.module.getId().equals(moduleName))
                .findFirst().orElseThrow().set(key, value);

        sendFeedback(context, "Set %s of %s for %s to %s".formatted(key.id(), moduleName, player.getName().getString(), value));
        return 0;
    }

    private static <T> int executeConfigCommand(CommandContext<ServerCommandSource> context, Key<T> key) {
        T value = context.getArgument("value", key.clazz());
        MakeLag.configState.set(key, value);

        sendFeedback(context, "Set %s to %s".formatted(key.id(), value));
        return 0;
    }

    private static CompletableFuture<Suggestions> getFileSuggestionsWithoutExtensions(SuggestionsBuilder builder, File dir) {
        for (File file : dir.listFiles()) {
            builder.suggest(FilenameUtils.removeExtension(file.getName()));
        }
        return builder.buildFuture();
    }

    static {
        //  /makelag start
        command.then(literal("start")
                .executes(context -> {
                    MakeLag.tickRate = 1;
                    MakeLag.ticks = 0;

                    sendFeedback(context, "Started making lag with progression " + ProgressionManager.loadedId);
                    return 0;
                })
        );

        //  /makelag togglePingDisplay
        command.then(literal("togglePingDisplay")
                .executes(context -> {
                    MakeLag.pingDisplayed = !MakeLag.pingDisplayed;
                    CustomPayload payload = new PingDisplayS2CPacket(MakeLag.pingDisplayed);

                    for (ServerPlayerEntity player : MakeLag.server.getPlayerManager().getPlayerList()) {
                        ServerPlayNetworking.send(player, payload);
                    }
                    return 0;
                })
        );

        //  /makelag state
        RequiredArgumentBuilder<ServerCommandSource, EntitySelector> stateCmd = argument("player", EntityArgumentType.player());

        for (Module module : Modules.MODULES) {
            String moduleName = module.getId();
            LiteralArgumentBuilder<ServerCommandSource> moduleCmd = literal(moduleName);

            for (Key<?> key : module.configurableKeys) {
                moduleCmd.then(literal(key.id())
                        .then(argument("value", key.argumentType())
                                .executes(context -> executeCommand(context, moduleName, key))
                        )
                );
            }

            stateCmd.then(moduleCmd);
        }
        command.then(literal("state").then(stateCmd));

        //  /makelag config
        LiteralArgumentBuilder<ServerCommandSource> configCmd = literal("config");

        for (Key<?> key : Modules.CONFIG.configurableKeys) {
            configCmd.then(literal(key.id())
                    .then(argument("value", key.argumentType())
                            .executes(context -> executeConfigCommand(context, key))
                    )
            );
        }
        command.then(configCmd);

        //  /makelag reload
        command.then(literal("reload")
                .executes(context -> {
                    String[] previousDeathMessages = Config.deathMessages.clone();
                    ProgressionManager.load(ProgressionManager.loadedId);
                    Config.reload();

                    boolean sameDeathMessages = Arrays.equals(previousDeathMessages, Config.deathMessages);

                    if (sameDeathMessages) {
                        sendFeedback(context, "Reloaded progression " + ProgressionManager.loadedId);
                    } else {
                        sendFeedback(context, "Reloaded death_messages.json and progression " + ProgressionManager.loadedId);
                    }
                    return 0;
                })
        );

        //  /makelag progression
        command.then(literal("progression")
                .then(literal("pause")
                        .executes(context -> {
                            if (MakeLag.tickRate == 0) {
                                sendFeedback(context, "Can't pause, making lag is already paused");
                                return 1;
                            }

                            MakeLag.tickRate = 0;
                            ProgressionManager.resetToDefaults();

                            sendFeedback(context, "Paused making lag at tick " + MakeLag.ticks);
                            return 0;
                        })
                )
                .then(literal("resume")
                        .executes(context -> {
                            if (MakeLag.tickRate > 0) {
                                sendFeedback(context, "Can't resume, making lag is already resumed");
                                return 1;
                            }

                            MakeLag.tickRate = 1;
                            ProgressionManager.setTick(MakeLag.ticks);

                            sendFeedback(context, "Resumed making lag at tick " + MakeLag.ticks);
                            return 0;
                        })
                )
                .then(literal("load")
                        .then(argument("progression", StringArgumentType.word())
                                .suggests(PROGRESSION_SUGGESTIONS)
                                .executes(context -> {
                                    String id = context.getArgument("progression", String.class);
                                    List<String> messages = ProgressionManager.load(id);

                                    for (String message : messages) {
                                        sendFeedback(context, message);
                                    }
                                    return 0;
                                })
                        )
                )
                .then(literal("tick")
                        .then(argument("tick", IntegerArgumentType.integer(-1))
                                .executes(context -> {
                                    MakeLag.ticks = context.getArgument("tick", Integer.class);
                                    if (MakeLag.tickRate > 0) {
                                        ProgressionManager.setTick(MakeLag.ticks);
                                    }

                                    sendFeedback(context, "Set progression tick of " + ProgressionManager.loadedId + " to " + MakeLag.ticks);
                                    return 0;
                                })
                        )
                )
                .then(literal("skip")
                        .then(argument("ticks", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    int skippedTicks = context.getArgument("ticks", Integer.class);
                                    MakeLag.ticks += skippedTicks;

                                    sendFeedback(context, "Skipped " + skippedTicks + " ticks of progression " + ProgressionManager.loadedId + ", progression tick is now " + MakeLag.ticks);
                                    return 0;
                                })
                        )
                )
                .then(literal("tickRate")
                        .then(argument("rate", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    MakeLag.tickRate = context.getArgument("rate", Integer.class);

                                    sendFeedback(context, "Set progression tick rate of " + ProgressionManager.loadedId + " to " + MakeLag.tickRate);
                                    return 0;
                                })
                        )
                )
        );
    }
}
