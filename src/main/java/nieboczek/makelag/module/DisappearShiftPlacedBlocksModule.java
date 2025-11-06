package nieboczek.makelag.module;

import net.minecraft.util.math.BlockPos;
import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.NotRunnableModule;

import java.util.ArrayList;

public class DisappearShiftPlacedBlocksModule extends NotRunnableModule {
    public static final Key<Integer> blocksToStartTimer = Key.notNegativeInt("blocksToStartTimer");
    public static final Key<Integer> timerLength = Key.notNegativeInt("timerLength");
    public static final Key<Integer> timerLengthDelta = Key.notNegativeInt("timerLengthDelta");

    public static final Key<Boolean> timerStarted = Key.bool();
    public static final Key<ArrayList<BlockPos>> positionsToDisappear = Key.of(new ArrayList<>());

    @Override
    public ArrayList<Key<?>> getAllKeys() {
        var keys = super.getAllKeys();

        keys.add(blocksToStartTimer);
        keys.add(timerLength);
        keys.add(timerLengthDelta);
        keys.add(timerStarted);
        keys.add(positionsToDisappear);

        return keys;
    }

    @Override
    public String getId() {
        return "disappearShiftPlacedBlocks";
    }
}
