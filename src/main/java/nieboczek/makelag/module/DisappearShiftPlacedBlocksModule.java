package nieboczek.makelag.module;

import net.minecraft.util.math.BlockPos;
import nieboczek.makelag.module.backend.Key;
import nieboczek.makelag.module.backend.NotRunnableModule;

import java.util.ArrayList;

public class DisappearShiftPlacedBlocksModule extends NotRunnableModule {
    public Key<Integer> blocksToStartTimer = configKey(Key.notNegativeInt("blocksToStartTimer"));
    public Key<Integer> timerLength = configKey(Key.notNegativeInt("timerLength"));
    public Key<Integer> timerLengthDelta = configKey(Key.notNegativeInt("timerLengthDelta"));

    public Key<Boolean> timerStarted = key(Key.bool());
    public Key<ArrayList<BlockPos>> positionsToDisappear = key(Key.of(new ArrayList<>()));

    @Override
    public String getName() {
        return "disappearShiftPlacedBlocks";
    }
}
