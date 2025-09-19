package nieboczek.makelag.module;

import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import nieboczek.makelag.MakeLag;
import nieboczek.makelag.module.backend.Module;
import nieboczek.makelag.module.backend.ModuleState;

public class AdvancementUnlockModule extends Module {
    private static final UnlockInfo[] ADVANCEMENTS = {
            UnlockInfo.of("story/upgrade_tools", "stone_pickaxe"),
            UnlockInfo.of("story/smelt_iron", "iron"),
            UnlockInfo.of("story/mine_stone", "get_stone"),
            UnlockInfo.of("story/iron_tools", "iron_pickaxe"),
            UnlockInfo.of("story/mine_diamond", "diamond"),
            UnlockInfo.of("story/deflect_arrow", "deflect_projectile")
    };

    @Override
    public void run(ServerPlayerEntity player, ModuleState state) {
        UnlockInfo info = ADVANCEMENTS[MakeLag.random.nextInt(ADVANCEMENTS.length)];
        AdvancementEntry entry = MakeLag.server.getAdvancementLoader().get(Identifier.ofVanilla(info.id));
        player.getAdvancementTracker().revokeCriterion(entry, info.criterion);
        player.getAdvancementTracker().grantCriterion(entry, info.criterion);
    }

    @Override
    public String getName() {
        return "advancementUnlock";
    }

    private record UnlockInfo(String id, String criterion) {
        private static UnlockInfo of(String id, String criterion) {
            return new UnlockInfo(id, criterion);
        }
    }
}
