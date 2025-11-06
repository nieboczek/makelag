package nieboczek.makelag.mixin;

import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import nieboczek.makelag.MakeLag;
import nieboczek.makelag.config.Config;
import nieboczek.makelag.module.ChangeDeathMessageModule;
import nieboczek.makelag.module.Modules;
import nieboczek.makelag.module.backend.ModuleState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;getDeathMessage()Lnet/minecraft/text/Text;"))
    public Text onDeath(DamageTracker instance) {
        if (!(instance.entity instanceof ServerPlayerEntity player)) {
            return instance.getDeathMessage();
        }

        ModuleState state = MakeLag.getState(player, Modules.CHANGE_DEATH_MESSAGE);
        boolean force = state.get(ChangeDeathMessageModule.forceCustomDeathMessage);
        float chance = state.get(ChangeDeathMessageModule.intensity);

        if (force || MakeLag.random.nextFloat() < chance) {
            String name = player.getDisplayName().getString();
            state.set(ChangeDeathMessageModule.forceCustomDeathMessage, false);

            return Text.literal(Config.deathMessages[MakeLag.random.nextInt(Config.deathMessages.length)].replace("{}", name));
        }
        return instance.getDeathMessage();
    }
}
