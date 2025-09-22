package nieboczek.makelag.progression;

//public class DefaultProgression extends ProgressionManager.Provider {
//    private static final int sessionStart = 0;
//    private static final int halfAHour = 20 * 60 * 30;
//    private static final int hour = 2 * halfAHour;
//    private static final int twoHours = 2 * hour;
//    private static final int sessionEnd = 3 * hour;
//
//    @Override
//    protected void load() {
//        add(ADVANCEMENT_UNLOCK,            ADVANCEMENT_UNLOCK.intensity,                     halfAHour,    sessionEnd, 0, 0.0003);
//
//        add(DEATH,                         DEATH.intensity,                                  hour,         sessionEnd, 0, 0.0001);
//
//        add(DISAPPEAR_SHIFT_PLACED_BLOCKS, DISAPPEAR_SHIFT_PLACED_BLOCKS.intensity,          halfAHour,    sessionEnd, 0, 0.1);
//        add(DISAPPEAR_SHIFT_PLACED_BLOCKS, DISAPPEAR_SHIFT_PLACED_BLOCKS.blocksToStartTimer, halfAHour,    sessionEnd, 5, 2);
//        add(DISAPPEAR_SHIFT_PLACED_BLOCKS, DISAPPEAR_SHIFT_PLACED_BLOCKS.timerLength,        halfAHour,    sessionEnd, 4, 2);
//        add(DISAPPEAR_SHIFT_PLACED_BLOCKS, DISAPPEAR_SHIFT_PLACED_BLOCKS.timerLengthDelta,   halfAHour,    sessionEnd, 2, 0.5);
//
//        add(DISMOUNT,                      DISMOUNT.intensity,                               halfAHour,    sessionEnd, 0, 0.0004);
//
//        add(PACKET,                        PACKET.delay,                                     sessionStart, halfAHour,  0, 400);
//        add(PACKET,                        PACKET.delay,                                     halfAHour,    hour,       400, 1200);
//        add(PACKET,                        PACKET.delay,                                     hour,         twoHours,   1200, 3400);
//        add(PACKET,                        PACKET.delay,                                     twoHours,     sessionEnd, 3400, 7000);
//
//        add(PACKET,                        PACKET.delayDelta,                                sessionStart, hour,       0, 50);
//        add(PACKET,                        PACKET.delayDelta,                                hour,         sessionEnd, 50, 500);
//
//        add(PACKET,                        PACKET.dropChance,                                halfAHour,    hour,       0, 0.02);
//        add(PACKET,                        PACKET.dropChance,                                hour,         sessionEnd, 0.02, 0.1);
//
//        add(PACKET,                        PACKET.lagSpikeChance,                            halfAHour,    hour,       0, 0.0002);
//        add(PACKET,                        PACKET.lagSpikeChance,                            hour,         sessionEnd, 0.0002, 0.001);
//
//        add(PACKET,                        PACKET.lagSpikeMultiplier,                        halfAHour,    hour,       2, 3);
//        add(PACKET,                        PACKET.lagSpikeMultiplier,                        hour,         sessionEnd, 3, 7);
//
//        add(STOP_SPRINTING,                STOP_SPRINTING.intensity,                         sessionStart, sessionEnd, 0, 0.0007);
//
//        add(TELEPORT,                      TELEPORT.intensity,                               hour,         sessionEnd, 0, 0.0002);
//
//        add(CONFIG,                        CONFIG.fakeLagSpikeChance,                        halfAHour,    hour,       0, 0.0001);
//        add(CONFIG,                        CONFIG.fakeLagSpikeChance,                        hour,         sessionEnd, 0.0001, 0.0007);
//    }
//
//    @Override
//    public String getId() {
//        return "default";
//    }
//}
