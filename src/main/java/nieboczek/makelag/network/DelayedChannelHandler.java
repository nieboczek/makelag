package nieboczek.makelag.network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.PacketType;
import net.minecraft.network.packet.PlayPackets;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import nieboczek.makelag.MakeLag;
import nieboczek.makelag.module.Modules;
import nieboczek.makelag.module.backend.ModuleState;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class DelayedChannelHandler extends ChannelDuplexHandler {
    private static final ArrayList<PacketType<?>> ACCEPTED_TYPES = new ArrayList<>();

    private final ServerPlayNetworkHandler handler;

    public DelayedChannelHandler(ServerPlayNetworkHandler handler) {
        this.handler = handler;
    }

    // TODO: Delaying S2C packets makes random teleports way more frequent, fix at your own sanity.
//    @Override
//    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
//        if (!(msg instanceof Packet<?> packet)) {
//            super.write(ctx, msg, promise);
//            return;
//        }
//
//        if (!ACCEPTED_TYPES.contains(packet.getPacketType())) {
//            super.write(ctx, msg, promise);
//            return;
//        }
//
//        ModuleState state = MakeLag.getConfig(handler.player).getState(Modules.PACKET);
//
//        if (MakeLag.random.nextFloat() < state.get(Modules.PACKET.dropChance)) {
//            MakeLag.droppedPackets++;
//            return;
//        }
//
//        int delay = MakeLag.random.nextInt(
//                state.get(Modules.PACKET.delay) - state.get(Modules.PACKET.delayDelta),
//                state.get(Modules.PACKET.delay) + state.get(Modules.PACKET.delayDelta) + 1
//        );
//
//        if (MakeLag.random.nextFloat() < state.get(Modules.PACKET.lagSpikeChance)) {
//            delay *= state.get(Modules.PACKET.lagSpikeMultiplier);
//            MakeLag.lagSpikes++;
//        }
//
//        MakeLag.scheduler.schedule(() -> ctx.writeAndFlush(msg, promise), delay, TimeUnit.MILLISECONDS);
//    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof Packet<?> packet)) {
            super.channelRead(ctx, msg);
            return;
        }

        if (!ACCEPTED_TYPES.contains(packet.getPacketType())) {
            super.channelRead(ctx, msg);
            return;
        }

        ModuleState state = MakeLag.getConfig(handler.player).getState(Modules.PACKET);

        if (MakeLag.random.nextFloat() < state.get(Modules.PACKET.dropChance)) {
            MakeLag.droppedPackets++;
            return;
        }

        int delay = MakeLag.random.nextInt(
                state.get(Modules.PACKET.delay) - state.get(Modules.PACKET.delayDelta),
                state.get(Modules.PACKET.delay) + state.get(Modules.PACKET.delayDelta) + 1
        );

        if (MakeLag.random.nextFloat() < state.get(Modules.PACKET.lagSpikeChance)) {
            delay *= state.get(Modules.PACKET.lagSpikeMultiplier);
            MakeLag.lagSpikes++;
        }

        MakeLag.scheduler.schedule(() -> ctx.fireChannelRead(msg), delay, TimeUnit.MILLISECONDS);
    }

    static {
        ACCEPTED_TYPES.add(PlayPackets.BUNDLE);
        ACCEPTED_TYPES.add(PlayPackets.BUNDLE_DELIMITER);
        ACCEPTED_TYPES.add(PlayPackets.ADD_ENTITY);
        ACCEPTED_TYPES.add(PlayPackets.ANIMATE);
        ACCEPTED_TYPES.add(PlayPackets.AWARD_STATS);
        ACCEPTED_TYPES.add(PlayPackets.BLOCK_CHANGED_ACK);
        ACCEPTED_TYPES.add(PlayPackets.BLOCK_DESTRUCTION);
        ACCEPTED_TYPES.add(PlayPackets.BLOCK_ENTITY_DATA);
        ACCEPTED_TYPES.add(PlayPackets.BLOCK_EVENT);
        ACCEPTED_TYPES.add(PlayPackets.BLOCK_UPDATE);
        ACCEPTED_TYPES.add(PlayPackets.BOSS_EVENT);
        ACCEPTED_TYPES.add(PlayPackets.CHANGE_DIFFICULTY_S2C);
        ACCEPTED_TYPES.add(PlayPackets.CHUNK_BATCH_FINISHED);
        ACCEPTED_TYPES.add(PlayPackets.CHUNK_BATCH_START);
        ACCEPTED_TYPES.add(PlayPackets.CHUNKS_BIOMES);
        ACCEPTED_TYPES.add(PlayPackets.CLEAR_TITLES);
        ACCEPTED_TYPES.add(PlayPackets.COMMAND_SUGGESTIONS);
        ACCEPTED_TYPES.add(PlayPackets.COMMANDS);
        ACCEPTED_TYPES.add(PlayPackets.CONTAINER_CLOSE_S2C);
        ACCEPTED_TYPES.add(PlayPackets.CONTAINER_SET_CONTENT);
        ACCEPTED_TYPES.add(PlayPackets.CONTAINER_SET_DATA);
        ACCEPTED_TYPES.add(PlayPackets.CONTAINER_SET_SLOT);
        ACCEPTED_TYPES.add(PlayPackets.COOLDOWN);
        ACCEPTED_TYPES.add(PlayPackets.CUSTOM_CHAT_COMPLETIONS);
        ACCEPTED_TYPES.add(PlayPackets.DAMAGE_EVENT);
        ACCEPTED_TYPES.add(PlayPackets.DEBUG_SAMPLE);
        ACCEPTED_TYPES.add(PlayPackets.DELETE_CHAT);
        ACCEPTED_TYPES.add(PlayPackets.DISGUISED_CHAT);
        ACCEPTED_TYPES.add(PlayPackets.ENTITY_EVENT);
        ACCEPTED_TYPES.add(PlayPackets.ENTITY_POSITION_SYNC);
        ACCEPTED_TYPES.add(PlayPackets.EXPLODE);
        ACCEPTED_TYPES.add(PlayPackets.FORGET_LEVEL_CHUNK);
        ACCEPTED_TYPES.add(PlayPackets.GAME_EVENT);
        ACCEPTED_TYPES.add(PlayPackets.HORSE_SCREEN_OPEN);
        ACCEPTED_TYPES.add(PlayPackets.HURT_ANIMATION);
        ACCEPTED_TYPES.add(PlayPackets.INITIALIZE_BORDER);
        ACCEPTED_TYPES.add(PlayPackets.LEVEL_CHUNK_WITH_LIGHT);
        ACCEPTED_TYPES.add(PlayPackets.LEVEL_EVENT);
        ACCEPTED_TYPES.add(PlayPackets.LEVEL_PARTICLES);
        ACCEPTED_TYPES.add(PlayPackets.LIGHT_UPDATE);
//        ACCEPTED_TYPES.add(PlayPackets.LOGIN); // let's not delay/drop this for obvious reasons
        ACCEPTED_TYPES.add(PlayPackets.MAP_ITEM_DATA);
        ACCEPTED_TYPES.add(PlayPackets.MERCHANT_OFFERS);
        ACCEPTED_TYPES.add(PlayPackets.MOVE_ENTITY_POS);
        ACCEPTED_TYPES.add(PlayPackets.MOVE_ENTITY_POS_ROT);
        ACCEPTED_TYPES.add(PlayPackets.MOVE_MINECART_ALONG_TRACK);
        ACCEPTED_TYPES.add(PlayPackets.MOVE_ENTITY_ROT);
        ACCEPTED_TYPES.add(PlayPackets.MOVE_VEHICLE_S2C);
        ACCEPTED_TYPES.add(PlayPackets.OPEN_BOOK);
        ACCEPTED_TYPES.add(PlayPackets.OPEN_SCREEN);
        ACCEPTED_TYPES.add(PlayPackets.OPEN_SIGN_EDITOR);
        ACCEPTED_TYPES.add(PlayPackets.PLACE_GHOST_RECIPE);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_ABILITIES_S2C);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_CHAT);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_COMBAT_END);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_COMBAT_ENTER);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_COMBAT_KILL);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_INFO_REMOVE);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_INFO_UPDATE);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_LOOK_AT);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_POSITION);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_ROTATION);
        ACCEPTED_TYPES.add(PlayPackets.RECIPE_BOOK_ADD);
        ACCEPTED_TYPES.add(PlayPackets.RECIPE_BOOK_REMOVE);
        ACCEPTED_TYPES.add(PlayPackets.RECIPE_BOOK_SETTINGS);
        ACCEPTED_TYPES.add(PlayPackets.REMOVE_ENTITIES);
        ACCEPTED_TYPES.add(PlayPackets.REMOVE_MOB_EFFECT);
//        ACCEPTED_TYPES.add(PlayPackets.RESPAWN); // some players might not get that they should press "title screen" > "respawn" to retry the respawn
        ACCEPTED_TYPES.add(PlayPackets.ROTATE_HEAD);
        ACCEPTED_TYPES.add(PlayPackets.SECTION_BLOCKS_UPDATE);
        ACCEPTED_TYPES.add(PlayPackets.SELECT_ADVANCEMENTS_TAB);
        ACCEPTED_TYPES.add(PlayPackets.SERVER_DATA);
        ACCEPTED_TYPES.add(PlayPackets.SET_ACTION_BAR_TEXT);
        ACCEPTED_TYPES.add(PlayPackets.SET_BORDER_CENTER);
        ACCEPTED_TYPES.add(PlayPackets.SET_BORDER_LERP_SIZE);
        ACCEPTED_TYPES.add(PlayPackets.SET_BORDER_SIZE);
        ACCEPTED_TYPES.add(PlayPackets.SET_BORDER_WARNING_DELAY);
        ACCEPTED_TYPES.add(PlayPackets.SET_BORDER_WARNING_DISTANCE);
        ACCEPTED_TYPES.add(PlayPackets.SET_CAMERA);
        ACCEPTED_TYPES.add(PlayPackets.SET_CHUNK_CACHE_CENTER);
        ACCEPTED_TYPES.add(PlayPackets.SET_CHUNK_CACHE_RADIUS);
        ACCEPTED_TYPES.add(PlayPackets.SET_DEFAULT_SPAWN_POSITION);
        ACCEPTED_TYPES.add(PlayPackets.SET_DISPLAY_OBJECTIVE);
        ACCEPTED_TYPES.add(PlayPackets.SET_ENTITY_DATA);
        ACCEPTED_TYPES.add(PlayPackets.SET_ENTITY_LINK);
        ACCEPTED_TYPES.add(PlayPackets.SET_ENTITY_MOTION);
        ACCEPTED_TYPES.add(PlayPackets.SET_EQUIPMENT);
        ACCEPTED_TYPES.add(PlayPackets.SET_EXPERIENCE);
        ACCEPTED_TYPES.add(PlayPackets.SET_HEALTH);
        ACCEPTED_TYPES.add(PlayPackets.SET_CARRIED_ITEM_S2C);
        ACCEPTED_TYPES.add(PlayPackets.SET_OBJECTIVE);
        ACCEPTED_TYPES.add(PlayPackets.SET_PASSENGERS);
        ACCEPTED_TYPES.add(PlayPackets.SET_PLAYER_TEAM);
        ACCEPTED_TYPES.add(PlayPackets.SET_SCORE);
        ACCEPTED_TYPES.add(PlayPackets.SET_SIMULATION_DISTANCE);
        ACCEPTED_TYPES.add(PlayPackets.SET_SUBTITLE_TEXT);
        ACCEPTED_TYPES.add(PlayPackets.SET_TIME);
        ACCEPTED_TYPES.add(PlayPackets.SET_TITLE_TEXT);
        ACCEPTED_TYPES.add(PlayPackets.SET_TITLES_ANIMATION);
        ACCEPTED_TYPES.add(PlayPackets.SOUND_ENTITY);
        ACCEPTED_TYPES.add(PlayPackets.SOUND);
        ACCEPTED_TYPES.add(PlayPackets.START_CONFIGURATION);
        ACCEPTED_TYPES.add(PlayPackets.STOP_SOUND);
        ACCEPTED_TYPES.add(PlayPackets.SYSTEM_CHAT);
        ACCEPTED_TYPES.add(PlayPackets.TAB_LIST);
        ACCEPTED_TYPES.add(PlayPackets.TAG_QUERY);
        ACCEPTED_TYPES.add(PlayPackets.TAKE_ITEM_ENTITY);
        ACCEPTED_TYPES.add(PlayPackets.TELEPORT_ENTITY);
        ACCEPTED_TYPES.add(PlayPackets.TEST_INSTANCE_BLOCK_STATUS);
        ACCEPTED_TYPES.add(PlayPackets.UPDATE_ADVANCEMENTS);
        ACCEPTED_TYPES.add(PlayPackets.UPDATE_ATTRIBUTES);
        ACCEPTED_TYPES.add(PlayPackets.UPDATE_MOB_EFFECT);
        ACCEPTED_TYPES.add(PlayPackets.UPDATE_RECIPES);
        ACCEPTED_TYPES.add(PlayPackets.PROJECTILE_POWER);
        ACCEPTED_TYPES.add(PlayPackets.WAYPOINT);
//        ACCEPTED_TYPES.add(PlayPackets.ACCEPT_TELEPORTATION); // if you delay this, the nether portal kinda breaks and the overall gameplay is frustrating
        ACCEPTED_TYPES.add(PlayPackets.BLOCK_ENTITY_TAG_QUERY);
        ACCEPTED_TYPES.add(PlayPackets.BUNDLE_ITEM_SELECTED);
        ACCEPTED_TYPES.add(PlayPackets.CHANGE_DIFFICULTY_C2S);
        ACCEPTED_TYPES.add(PlayPackets.CHANGE_GAME_MODE);
        ACCEPTED_TYPES.add(PlayPackets.CHAT_ACK);
        ACCEPTED_TYPES.add(PlayPackets.CHAT_COMMAND);
        ACCEPTED_TYPES.add(PlayPackets.CHAT_COMMAND_SIGNED);
        ACCEPTED_TYPES.add(PlayPackets.CHAT);
        ACCEPTED_TYPES.add(PlayPackets.CHAT_SESSION_UPDATE);
        ACCEPTED_TYPES.add(PlayPackets.CHUNK_BATCH_RECEIVED);
//        ACCEPTED_TYPES.add(PlayPackets.CLIENT_COMMAND); // some players might not get that they should press "title screen" > "respawn" to retry the respawn; also has REQUEST_STATS but that's fine
        ACCEPTED_TYPES.add(PlayPackets.CLIENT_TICK_END);
        ACCEPTED_TYPES.add(PlayPackets.COMMAND_SUGGESTION);
        ACCEPTED_TYPES.add(PlayPackets.CONFIGURATION_ACKNOWLEDGED);
        ACCEPTED_TYPES.add(PlayPackets.CONTAINER_BUTTON_CLICK);
        ACCEPTED_TYPES.add(PlayPackets.CONTAINER_CLICK);
        ACCEPTED_TYPES.add(PlayPackets.CONTAINER_CLOSE_C2S);
        ACCEPTED_TYPES.add(PlayPackets.CONTAINER_SLOT_STATE_CHANGED);
        ACCEPTED_TYPES.add(PlayPackets.DEBUG_SAMPLE_SUBSCRIPTION);
        ACCEPTED_TYPES.add(PlayPackets.EDIT_BOOK);
        ACCEPTED_TYPES.add(PlayPackets.ENTITY_TAG_QUERY);
        ACCEPTED_TYPES.add(PlayPackets.INTERACT);
        ACCEPTED_TYPES.add(PlayPackets.JIGSAW_GENERATE);
        ACCEPTED_TYPES.add(PlayPackets.LOCK_DIFFICULTY);
        ACCEPTED_TYPES.add(PlayPackets.MOVE_PLAYER_POS);
        ACCEPTED_TYPES.add(PlayPackets.MOVE_PLAYER_POS_ROT);
        ACCEPTED_TYPES.add(PlayPackets.MOVE_PLAYER_ROT);
        ACCEPTED_TYPES.add(PlayPackets.MOVE_PLAYER_STATUS_ONLY);
        ACCEPTED_TYPES.add(PlayPackets.MOVE_VEHICLE_C2S);
        ACCEPTED_TYPES.add(PlayPackets.PADDLE_BOAT);
        ACCEPTED_TYPES.add(PlayPackets.PICK_ITEM_FROM_BLOCK);
        ACCEPTED_TYPES.add(PlayPackets.PICK_ITEM_FROM_ENTITY);
        ACCEPTED_TYPES.add(PlayPackets.PLACE_RECIPE);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_ABILITIES_C2S);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_ACTION);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_COMMAND);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_INPUT);
        ACCEPTED_TYPES.add(PlayPackets.PLAYER_LOADED);
        ACCEPTED_TYPES.add(PlayPackets.RECIPE_BOOK_CHANGE_SETTINGS);
        ACCEPTED_TYPES.add(PlayPackets.RECIPE_BOOK_SEEN_RECIPE);
        ACCEPTED_TYPES.add(PlayPackets.RENAME_ITEM);
        ACCEPTED_TYPES.add(PlayPackets.SEEN_ADVANCEMENTS);
        ACCEPTED_TYPES.add(PlayPackets.SELECT_TRADE);
        ACCEPTED_TYPES.add(PlayPackets.SET_BEACON);
        ACCEPTED_TYPES.add(PlayPackets.SET_CARRIED_ITEM_C2S);
        ACCEPTED_TYPES.add(PlayPackets.SET_COMMAND_BLOCK);
        ACCEPTED_TYPES.add(PlayPackets.SET_COMMAND_MINECART);
        ACCEPTED_TYPES.add(PlayPackets.SET_CREATIVE_MODE_SLOT);
        ACCEPTED_TYPES.add(PlayPackets.SET_JIGSAW_BLOCK);
        ACCEPTED_TYPES.add(PlayPackets.SET_STRUCTURE_BLOCK);
        ACCEPTED_TYPES.add(PlayPackets.SET_TEST_BLOCK);
        ACCEPTED_TYPES.add(PlayPackets.TEST_INSTANCE_BLOCK_ACTION);
        ACCEPTED_TYPES.add(PlayPackets.SIGN_UPDATE);
        ACCEPTED_TYPES.add(PlayPackets.SWING);
        ACCEPTED_TYPES.add(PlayPackets.TELEPORT_TO_ENTITY);
        ACCEPTED_TYPES.add(PlayPackets.USE_ITEM_ON);
        ACCEPTED_TYPES.add(PlayPackets.USE_ITEM);
        ACCEPTED_TYPES.add(PlayPackets.RESET_SCORE);
        ACCEPTED_TYPES.add(PlayPackets.TICKING_STATE);
        ACCEPTED_TYPES.add(PlayPackets.TICKING_STEP);
        ACCEPTED_TYPES.add(PlayPackets.SET_CURSOR_ITEM);
        ACCEPTED_TYPES.add(PlayPackets.SET_PLAYER_INVENTORY);
    }
}
