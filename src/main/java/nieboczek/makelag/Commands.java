package nieboczek.makelag;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import nieboczek.makelag.config.PlayerConfig;
import nieboczek.makelag.module.Modules;
import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.network.PingDisplayS2CPacket;
import nieboczek.makelag.progression.Progression;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Commands {
    public static final LiteralArgumentBuilder<ServerCommandSource> command = literal("makelag");

    private static void sendFeedback(CommandContext<ServerCommandSource> context, String msg) {
        context.getSource().sendFeedback(() -> Text.literal(MakeLag.MSG_PREFIX + msg), true);
    }

    private static <T> int executeCommand(CommandContext<ServerCommandSource> context, String moduleName, Key<T> key) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        PlayerConfig config = MakeLag.getConfig(player);

        T value = context.getArgument("value", key.clazz());
        config.states.stream()
                .filter(state -> state.module.getName().equals(moduleName))
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

    private static void loadDefaultProgression() {
        for (Progression.Provider provider : Progression.PROVIDERS) {
            if (provider.getId().equals("default")) {
                MakeLag.progression.load(provider);
                break;
            }
        }
    }

    static {
        //  /makelag start
        command.then(literal("start")
                .executes(context -> {
                    loadDefaultProgression();
                    MakeLag.ticks = 0;

                    sendFeedback(context, "Started making lag with the default progression");
                    return 0;
                })
        );

        //  /makelag pause
        command.then(literal("pause")
                .executes(context -> {
                    if (MakeLag.ticks == -1) {
                        sendFeedback(context, "Can't pause, making lag is already paused");
                        return 1;
                    }

                    MakeLag.pausedTicks = MakeLag.ticks;
                    MakeLag.ticks = -1;
                    MakeLag.progression.resetToDefaults();

                    sendFeedback(context, "Paused making lag at tick " + MakeLag.pausedTicks);
                    return 0;
                })
        );

        //  /makelag resume
        command.then(literal("resume")
                .executes(context -> {
                    if (MakeLag.ticks > 0) {
                        sendFeedback(context, "Can't resume, making lag is already resumed");
                        return 1;
                    }

                    MakeLag.ticks = MakeLag.pausedTicks;
                    MakeLag.progression.setTick(MakeLag.ticks);
                    sendFeedback(context, "Resumed making lag at tick " + MakeLag.ticks);
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
            String moduleName = module.getName();
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

        //  /makelag progression
        LiteralArgumentBuilder<ServerCommandSource> loadCmd = literal("load");

        for (Progression.Provider provider : Progression.PROVIDERS) {
            loadCmd.then(literal(provider.getId())
                    .executes(context -> {
                        MakeLag.progression.load(provider);

                        sendFeedback(context, "Loaded progression " + provider.getId());
                        return 0;
                    })
            );
        }

        command.then(literal("progression")
                .then(loadCmd)
                .then(literal("start")
                        .executes(context -> {
                            MakeLag.ticks = 0;

                            sendFeedback(context, "Started progression " + MakeLag.progression.getId());
                            return 0;
                        })
                )
                .then(literal("tick")
                        .then(argument("tick", IntegerArgumentType.integer(-1))
                                .executes(context -> {
                                    MakeLag.ticks = context.getArgument("tick", Integer.class);
                                    MakeLag.progression.setTick(MakeLag.ticks);

                                    sendFeedback(context, "Set progression tick of " + MakeLag.progression.getId() + " to " + MakeLag.ticks);
                                    return 0;
                                })
                        )
                )
                .then(literal("skip")
                        .then(argument("ticks", IntegerArgumentType.integer(0))
                                .executes(context -> {
                                    int skippedTicks = context.getArgument("ticks", Integer.class);
                                    MakeLag.ticks += skippedTicks;

                                    sendFeedback(context, "Skipped " + skippedTicks + "ticks of progression " + MakeLag.progression.getId() + ". Progression tick is now " + MakeLag.ticks);
                                    return 0;
                                })
                        )
                )
                .then(literal("tickRate")
                        .then(argument("rate", IntegerArgumentType.integer(1, 100))
                                .executes(context -> {
                                    MakeLag.tickRate = context.getArgument("rate", Integer.class);

                                    sendFeedback(context, "Set progression tick rate of " + MakeLag.progression.getId() + " to " + MakeLag.tickRate);
                                    return 0;
                                })
                        )
                )
        );
    }
}
