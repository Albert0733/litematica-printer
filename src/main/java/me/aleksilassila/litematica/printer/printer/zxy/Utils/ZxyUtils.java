package me.aleksilassila.litematica.printer.printer.zxy.Utils;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import me.aleksilassila.litematica.printer.LitematicaMixinMod;
import me.aleksilassila.litematica.printer.printer.Printer;
import me.aleksilassila.litematica.printer.printer.State;

import me.aleksilassila.litematica.printer.printer.bedrockUtils.BreakingFlowController;
import me.aleksilassila.litematica.printer.printer.bedrockUtils.Messager;
import me.aleksilassila.litematica.printer.printer.zxy.inventory.InventoryUtils;
import me.aleksilassila.litematica.printer.printer.zxy.inventory.OpenInventoryPacket;
import me.aleksilassila.litematica.printer.printer.zxy.inventory.SwitchItem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.*;
import java.util.function.Consumer;

//#if MC < 12101
//$$ import net.minecraft.world.item.enchantment.EnchantmentHelper;
//#endif

//#if MC >= 12105
import net.minecraft.network.HashedStack;
//#endif

//#if MC >= 12006
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.ItemEnchantments;
//#endif
import static me.aleksilassila.litematica.printer.printer.zxy.inventory.OpenInventoryPacket.*;

public class ZxyUtils {
    //旧版箱子追踪
    public static boolean qw = false;
    public static int currWorldId = 0;

    @NotNull
    public static Minecraft client = Minecraft.getInstance();
    public static int tick = 0;

    public static void tick() {
        tick++;
        tick %= Integer.MAX_VALUE;

        if (LitematicaMixinMod.CLOSE_ALL_MODE.getKeybind().isPressed()) {
            LitematicaMixinMod.BEDROCK_SWITCH.setBooleanValue(false);
            LitematicaMixinMod.EXCAVATE.setBooleanValue(false);
            LitematicaMixinMod.REPLACE_BLOCK.setBooleanValue(false);
            LitematicaMixinMod.TOGGLE_PRINTING_MODE.setBooleanValue(false);
            LitematicaMixinMod.PRINTER_MODE.setOptionListValue(State.PrintModeType.PRINTER);
            Printer.currentAction = null;
            Messager.actionBar("已关闭全部模式");
        }
        OpenInventoryPacket.tick();
        test();
    }

    static ItemStack itemStack;
    public static void test() {
        if (LitematicaMixinMod.TEST.getKeybind().isPressed()) {
//            QuickShulkerUtils.test();
//            if (itemStack == null) itemStack = client.player.getInventory().getMainHandStack();
//            if (!InventoryUtils.areStacksEqual(client.player.getInventory().getMainHandStack(), itemStack)) {
//                itemStack = client.player.getInventory().getMainHandStack();
//                System.out.println("=======");
//            }
//            OpenInventoryPacket.sendOpenInventory(DataManager.getSelectionManager().getCurrentSelection().getSubRegionBox(DataManager.getSimpleArea().getName()).getPos1(),Minecraft.getInstance().world.getRegistryKey());
        }
    }

    public static void switchPlayerInvToHotbarAir(int slot) {
        if (client.player == null) return;
        LocalPlayer player = client.player;
        AbstractContainerMenu sc = player.containerMenu;
        NonNullList<Slot> slots = sc.slots;
        int i = sc.equals(player.inventoryMenu) ? 9 : 0;
        for (; i < slots.size(); i++) {
            if (slots.get(i).getItem().isEmpty() && slots.get(i).container instanceof Inventory) {
                fi.dy.masa.malilib.util.InventoryUtils.swapSlots(sc, i, slot);
                return;
            }
        }
    }

    public static boolean canInteracted(Vec3 d, double range) {
        IConfigOptionListEntry optionListValue = LitematicaMixinMod.RANGE_MODE.getOptionListValue();
        return optionListValue != State.ListType.SPHERE || canInteracted(range,d);
    }
    public static boolean canInteracted(double range,Vec3 d){
        return client.player != null &&
                d != null &&
                client.player.getEyePosition().distanceToSqr(d) < range * range;
    }

    public static boolean canInteracted(BlockPos blockPos) {
        return blockPos != null && canInteracted(Vec3.atCenterOf(blockPos),getRage());
    }

    public static boolean bedrockCanInteracted(BlockPos blockPos,double range) {
        return client.player != null && client.player.getEyePosition().distanceToSqr(Vec3.atCenterOf(blockPos)) < range * range;
    }
    public static int getRage(){
        return LitematicaMixinMod.PRINTER_RANGE.getIntegerValue();
    }

    public static int maximumFrameRate = 10;
    public static int frameGenerationTime = getMonitorRefreshRate();
    //根据帧率计算超时时间 尽量不占用帧生成时间 6毫秒预留给打印机处理
    public static int printTimedOut = Math.max(3,frameGenerationTime -6);

    public static int getMonitorRefreshRate() {
        // 26.3 起不再直接暴露 LWJGL GLFW，改用 Minecraft 的 Window API
        int refreshRate;
        try {
            //#if MC >= 260300
            refreshRate = Math.max((int) client.getWindow().getActiveVideoMode().getRefreshRate(), 60);
            //#else
            //$$ refreshRate = Math.max(client.getWindow().getRefreshRate(), 60);
            //#endif
        } catch (Exception e) {
            refreshRate = 60;
        }
        maximumFrameRate = refreshRate;
        return Math.max(1,1000 / refreshRate);
//        System.out.println("The monitor refresh rate is " + refreshRate);
    }
    public static void exitGameReSet(){
        SwitchItem.reSet();
        Verify.verify = null;
        BreakingFlowController.poslist = new ArrayList<>();
        isRemote = false;
        clientTry = false;
        remoteTime = 0;
    }
    public static Optional<LocalPlayer> getPlayer(){
        return Optional.ofNullable(client.player);
    }

    //刷新物品栏
    public static void refreshPlayerInventory(){
        ClientPacketListener networkHandler = client.getConnection();
        if (getPlayer().isEmpty()) return;
        LocalPlayer player = getPlayer().get();
        if(networkHandler == null) return;
        ItemStack uniqueItem = new ItemStack(Items.STONE);

        // Tags with NaN are not equal, so the server will find an inventory desync and send an inventory refresh to the client
        //#if MC >= 12006
        var nbt = new CompoundTag();
        nbt.putDouble("force_sync", Double.NaN);
        CustomData.set(DataComponents.CUSTOM_DATA, uniqueItem, nbt);
        //#else
        //$$ uniqueItem.getOrCreateTag().putDouble("force_resync", Double.NaN);
        //#endif

        //#if MC >= 12105
        HashedStack itemStackHash = HashedStack.create(uniqueItem, networkHandler.decoratedHashOpsGenenerator());
        //#endif

        networkHandler.send(new ServerboundContainerClickPacket(
                player.containerMenu.containerId,
                player.containerMenu.getStateId(),
                (short) -999, (byte) 2,
                ContainerInput.QUICK_CRAFT,
                //#if MC < 12105
                //$$ uniqueItem,
                //$$ new Int2ObjectOpenHashMap<>()
                //#else
                new Int2ObjectOpenHashMap<>(),
                itemStackHash
                //#endif


        ));
    }

    public static int getEnchantmentLevel(ItemStack itemStack,
                                          //#if MC > 12006
                                          ResourceKey<Enchantment> enchantment
                                          //#else
                                          //$$ Enchantment enchantment
                                          //#endif
    ){
        //#if MC > 12006
        ItemEnchantments enchantments = itemStack.getEnchantments();

        if (enchantments.equals(ItemEnchantments.EMPTY)) return -1;
        Set<Holder<Enchantment>> enchantmentsEnchantments = enchantments.keySet();
        for (Holder<Enchantment> entry : enchantmentsEnchantments) {
            if (entry.is(enchantment)) {
                return enchantments.getLevel(entry);
            }
        }
        return -1;
        //#else
        //$$ return EnchantmentHelper.getItemEnchantmentLevel(enchantment,itemStack);
        //#endif
    }

    public static void eachBlock(Consumer<Block> consumer){
        for (Block block : BuiltInRegistries.BLOCK) {
            consumer.accept(block);
        }
    }

    public static void eachItem(Consumer<Item> consumer){
        for (Item item : BuiltInRegistries.ITEM) {
            consumer.accept(item);
        }
    }

    public static void setClientScreen(Screen screen){
        client
                //#if MC > 260100
                .gui
                //#endif
                .setScreen(screen);
    }
    //右键单击
//              client.gameMode.handleInventoryMouseClick(sc.containerId, i, 1, ClickType.PICKUP, client.player);
    //左键单击
//              client.gameMode.handleInventoryMouseClick(sc.containerId, i, 0, ClickType.PICKUP, client.player);
    //点击背包外
//              client.gameMode.handleInventoryMouseClick(sc.containerId, -999, 0, ClickType.PICKUP, client.player);
    //丢弃一个
//              client.gameMode.handleInventoryMouseClick(sc.syncId, i, 0, ClickType.THROW, client.player);
    //丢弃全部
//              client.gameMode.handleInventoryMouseClick(sc.syncId, i, 1, ClickType.THROW, client.player);
    //开始拖动
//              client.gameMode.handleInventoryMouseClick(sc.syncId, -999, 0, ClickType.QUICK_CRAFT, client.player);
    //拖动经过的槽
//              client.gameMode.handleInventoryMouseClick(sc.syncId, i1, 1, ClickType.QUICK_CRAFT, client.player);
    //结束拖动
//              client.gameMode.handleInventoryMouseClick(sc.syncId, -999, 2, ClickType.QUICK_CRAFT, client.player);
    //副手交换
//              client.gameMode.handleInventoryMouseClick(sc.syncId, i, 40, ClickType.SWAP, client.player);

}
