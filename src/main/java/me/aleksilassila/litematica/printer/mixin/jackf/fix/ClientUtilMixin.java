package me.aleksilassila.litematica.printer.mixin.jackf.fix;

import me.aleksilassila.litematica.printer.printer.zxy.Utils.ZxyUtils;
import me.aleksilassila.litematica.printer.printer.zxy.inventory.OpenInventoryPacket;
import net.kyrptonaught.quickshulker.client.ClientUtil;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientUtil.class)
public class ClientUtilMixin {
    @Inject(at = @At("HEAD"),method = "CheckAndSend")
    private static void CheckAndSend(ItemStack stack, int slot, CallbackInfoReturnable<Boolean> cir) {
        ZxyUtils.getPlayer().ifPresent(player -> OpenInventoryPacket.reSet());
    }
}
