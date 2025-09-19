package nieboczek.makelag.module;

import nieboczek.makelag.module.backend.Module;

import java.util.ArrayList;

public class Modules {
    public static final ArrayList<Module> MODULES = new ArrayList<>();

    // Global config module
    public static final ConfigModule CONFIG = new ConfigModule();

    public static final AdvancementUnlockModule ADVANCEMENT_UNLOCK = add(new AdvancementUnlockModule());
    public static final DeathModule DEATH = add(new DeathModule());
    public static final DisappearShiftPlacedBlocksModule DISAPPEAR_SHIFT_PLACED_BLOCKS = add(new DisappearShiftPlacedBlocksModule());
    public static final DismountModule DISMOUNT = add(new DismountModule());
    public static final PacketModule PACKET = add(new PacketModule());
    public static final SendStatsModule SEND_STATS = add(new SendStatsModule());
    public static final StopSprintingModule STOP_SPRINTING = add(new StopSprintingModule());
    public static final TeleportModule TELEPORT = add(new TeleportModule());

    private static <T extends Module> T add(T module) {
        MODULES.add(module);
        return module;
    }
}
