package me.aleksilassila.litematica.printer.config;

import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.malilib.hotkeys.IHotkeyCallback;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import me.aleksilassila.litematica.printer.printer.State;
import me.aleksilassila.litematica.printer.printer.bedrockUtils.Messager;
import me.aleksilassila.litematica.printer.printer.zxy.Utils.ZxyUtils;
import net.minecraft.client.Minecraft;

import static me.aleksilassila.litematica.printer.LitematicaMixinMod.*;
import static me.aleksilassila.litematica.printer.printer.Test.t1;
import static me.aleksilassila.litematica.printer.config.Configs.PRINTER;

//监听按键
public class HotkeysCallback implements IHotkeyCallback {
    private final Minecraft client = Minecraft.getInstance();

    //激活的热键会被key记录
    @Override
    public boolean onKeyAction(KeyAction action, IKeybind key) {
        if (this.client.player == null || this.client.level == null) return false;
        if(key == TEST.getKeybind()){
            t1();
            return true;
        }
        if(key == PRINTER.getKeybind()){
            ZxyUtils.setClientScreen(new ConfigUi());
            return true;
        }
        if(MODE_SWITCH.getOptionListValue().equals(State.ModeType.SINGLE) && key == SWITCH_PRINTER_MODE.getKeybind()){
            IConfigOptionListEntry cycle = PRINTER_MODE.getOptionListValue().cycle(true);
            PRINTER_MODE.setOptionListValue(cycle);
            Messager.actionBar(PRINTER_MODE.getOptionListValue().getDisplayName());
            return true;
        }
        return false;
    }

    //设置反馈到onKeyAction()方法的快捷键
    public static void init(){
        HotkeysCallback hotkeysCallback = new HotkeysCallback();

        for (ConfigHotkey configHotkey : Configs.addKeyList()) {
            configHotkey.getKeybind().setCallback(hotkeysCallback);
        }
    }
}
