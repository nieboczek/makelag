package nieboczek.makelag;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nieboczek.makelag.config.Config;
import nieboczek.makelag.config.PlayerConfig;
import nieboczek.makelag.module.Modules;
import nieboczek.makelag.module.SendStatsModule;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.module.backend.ModuleState;
import nieboczek.makelag.network.DelayedChannelHandler;
import nieboczek.makelag.network.PingDisplayS2CPacket;
import nieboczek.makelag.network.PingS2CPacket;
import nieboczek.makelag.progression.Progression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MakeLag implements ModInitializer {
    private static final int MIN_TICKS_UNTIL_NEW_POSITION = 1200;
    private static final int MAX_TICKS_UNTIL_NEW_POSITION = 2400;
    private static final int TICKS_UNTIL_PING_SEND = 40;
    public static final String MSG_PREFIX = "§6MakeLag §8>>§r ";

    public static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static final Logger log = LoggerFactory.getLogger("MakeLag");
    public static final Map<UUID, PlayerConfig> playerConfigs = new LinkedHashMap<>();
    public static final Random random = new Random();
    public static MinecraftServer server;

    public static ModuleState configState = new ModuleState(Modules.CONFIG);
    public static Progression progression = new Progression();
    public static ArrayList<Vec3d> positions = new ArrayList<>();
    public static boolean pingDisplayed = false;
    public static int pausedTicks = -1;
    public static int ticks = -1;

    public int ticksUntilNewPosition = MIN_TICKS_UNTIL_NEW_POSITION;
    public int ticksUntilPingSend = TICKS_UNTIL_PING_SEND;

    public static int droppedPackets = 0;
    public static int modulesRan = 0;
    public static int fakeLagSpikes = 0;
    public static int lagSpikes = 0;
    public static int ticksUntilSendStats = SendStatsModule.STATS_INTERVAL;

    public static PlayerConfig getConfig(ServerPlayerEntity player) {
        return playerConfigs.get(player.getUuid());
    }

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((x, y, z) -> x.register(Commands.command));

        PayloadTypeRegistry.playS2C().register(PingS2CPacket.ID, PingS2CPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(PingDisplayS2CPacket.ID, PingDisplayS2CPacket.CODEC);

        ServerPlayerEvents.JOIN.register(this::setupPlayer);
        UseBlockCallback.EVENT.register(this::useBlock);

        ServerTickEvents.END_SERVER_TICK.register(mcServer -> {
            if (server == null) {
                server = mcServer;
            }
            sendPings();

            if (ticks < 0) {
                return;
            }

            progression.tick(ticks);
            ticksUntilSendStats--;
            ticks++;

            addNewPosition();
            executeFakeLagSpike();
            runModules();
        });

        ServerLifecycleEvents.SERVER_STOPPED.register($ -> Config.save());
        Config.load();
    }

    private void sendPings() {
        ticksUntilPingSend--;
        if (ticksUntilPingSend <= 0) {
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();

            for (ServerPlayerEntity player : players) {
                ModuleState state = getConfig(player).getState(Modules.PACKET);
                int delay = random.nextInt(
                        state.get(Modules.PACKET.delay) - state.get(Modules.PACKET.delayDelta) + 20,
                        state.get(Modules.PACKET.delay) + state.get(Modules.PACKET.delayDelta) + 31
                );

                CustomPayload payload = new PingS2CPacket(player.getName().getString(), delay);
                for (ServerPlayerEntity recipient : players) {
                    ServerPlayNetworking.send(recipient, payload);
                }
            }
            ticksUntilPingSend = random.nextInt(TICKS_UNTIL_PING_SEND, TICKS_UNTIL_PING_SEND + 20);
        }
    }

    private void addNewPosition() {
        ticksUntilNewPosition--;
        if (ticksUntilNewPosition == 0) {
            List<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
            if (!players.isEmpty()) {
                ServerPlayerEntity player = players.get(random.nextInt(players.size()));

                if (positions.size() == 5) {
                    positions.removeFirst();
                }
                positions.add(player.getPos());
            }
            ticksUntilNewPosition = random.nextInt(MIN_TICKS_UNTIL_NEW_POSITION, MAX_TICKS_UNTIL_NEW_POSITION);
        }
    }

    private void executeFakeLagSpike() {
        if (random.nextFloat() < configState.get(Modules.CONFIG.fakeLagSpikeChance)) {
            server.getTickManager().setFrozen(true);
            scheduler.schedule(() -> {
                server.executeSync(() -> {
                    server.getTickManager().setFrozen(false);
                    server.getTickManager().setTickRate(60);
                });
                scheduler.schedule(() -> server.executeSync(() -> {
                    server.getTickManager().setTickRate(20);
                    fakeLagSpikes++;
                }), 1, TimeUnit.SECONDS);
            }, 2, TimeUnit.SECONDS);
        }
    }

    private void runModules() {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            PlayerConfig config = getConfig(player);

            for (Module module : Modules.MODULES) {
                ModuleState state = config.getState(module);

                boolean canRun = module.didRandomChanceSucceed(state, random) && module.canRun(player, state);
                if (canRun) {
                    module.run(player, state);
                    modulesRan++;
                }
            }
        }
    }

    private ActionResult useBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || !player.isInSneakingPose()) {
            return ActionResult.PASS;
        }

        ItemStack stack = player.getStackInHand(hand);
        if (stack.getItem() instanceof BlockItem) {
            ModuleState state = getConfig((ServerPlayerEntity) player).getState(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS);
            ArrayList<BlockPos> positionsToDisappear = state.get(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS.positionsToDisappear);

            if (
                    positionsToDisappear.isEmpty()
                            && random.nextFloat() >= state.get(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS.intensity)
            ) {
                return ActionResult.PASS;
            }

            int blocksToStartTimer = state.get(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS.blocksToStartTimer);
            BlockPos placedBlockPos = hitResult.getBlockPos().offset(hitResult.getSide());

            // Remove far away blocks from the list
            if (!positionsToDisappear.isEmpty()) {
                if (!positionsToDisappear.getLast().isWithinDistance(placedBlockPos, 32)) {
                    positionsToDisappear.clear();
                }
            }

            positionsToDisappear.add(placedBlockPos);

            if (!state.get(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS.timerStarted) && positionsToDisappear.size() >= blocksToStartTimer) {
                int timerLength = MakeLag.random.nextInt(
                        state.get(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS.timerLength) - state.get(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS.timerLengthDelta),
                        state.get(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS.timerLength) + state.get(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS.timerLengthDelta) + 1
                );

                state.set(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS.timerStarted, true);
                scheduler.schedule(() -> server.executeSync(() -> {
                    for (BlockPos pos : positionsToDisappear) {
                        world.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                    state.set(Modules.DISAPPEAR_SHIFT_PLACED_BLOCKS.timerStarted, false);
                    positionsToDisappear.clear();
                    modulesRan++;
                }), timerLength, TimeUnit.MILLISECONDS);
            }
        }
        return ActionResult.PASS;
    }

    private void setupPlayer(ServerPlayerEntity player) {
        if (playerConfigs.containsKey(player.getUuid())) {
            return;
        }

        PlayerConfig config = new PlayerConfig();
        playerConfigs.put(player.getUuid(), config);

        DelayedChannelHandler handler = new DelayedChannelHandler(player.networkHandler);
        player.networkHandler.connection.channel.pipeline().addBefore("packet_handler", "makelag", handler);

        ModuleState state = config.getState(Modules.PACKET);
        state.set(Modules.PACKET.handler, handler);

        if (pingDisplayed) {
            ServerPlayNetworking.send(player, new PingDisplayS2CPacket(true));
        }
    }
}
