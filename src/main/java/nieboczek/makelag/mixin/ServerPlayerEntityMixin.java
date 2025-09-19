package nieboczek.makelag.mixin;

import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import nieboczek.makelag.MakeLag;
import nieboczek.makelag.config.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Redirect(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/damage/DamageTracker;getDeathMessage()Lnet/minecraft/text/Text;"))
    public Text onDeath(DamageTracker instance) {
        String name = instance.entity.getDisplayName().getString();
        return Text.literal(Config.deathMessages[MakeLag.random.nextInt(Config.deathMessages.length)].replace("{}", name));
    }
}
