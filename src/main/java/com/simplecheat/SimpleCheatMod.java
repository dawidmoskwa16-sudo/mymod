package com.simplecheat;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SimpleCheatMod implements ClientModInitializer {

    private static KeyBinding flyKey;
    private static KeyBinding speedKey;
    private static KeyBinding nofallKey;

    private static boolean flyEnabled = false;
    private static boolean speedEnabled = false;
    private static boolean nofallEnabled = false;

    @Override
    public void onInitializeClient() {
        flyKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.simplecheat.fly", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F, "category.simplecheat.cheats"
        ));
        speedKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.simplecheat.speed", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.simplecheat.cheats"
        ));
        nofallKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.simplecheat.nofall", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "category.simplecheat.cheats"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // F - Fly
            if (flyKey.wasPressed()) {
                flyEnabled = !flyEnabled;
                client.player.getAbilities().allowFlying = flyEnabled;
                client.player.getAbilities().flying = flyEnabled;
                client.player.sendMessage(Text.literal("§aFly: " + (flyEnabled ? "§2ON" : "§cOFF")), false);
            }

            // G - Speed hack
            if (speedKey.wasPressed()) {
                speedEnabled = !speedEnabled;
                client.player.sendMessage(Text.literal("§aSpeed: " + (speedEnabled ? "§2ON" : "§cOFF")), false);
            }

            // H - NoFall
            if (nofallKey.wasPressed()) {
                nofallEnabled = !nofallEnabled;
                client.player.sendMessage(Text.literal("§aNoFall: " + (nofallEnabled ? "§2ON" : "§cOFF")), false);
            }

            // Speed hack - mocny boost
            if (speedEnabled) {
                double speed = 0.6; // możesz zmienić na wyższą wartość
                if (client.player.forwardSpeed != 0 || client.player.sidewaysSpeed != 0) {
                    double yaw = Math.toRadians(client.player.getYaw());
                    double vx = -Math.sin(yaw) * speed * Math.signum(client.player.forwardSpeed) - Math.cos(yaw) * speed * client.player.sidewaysSpeed;
                    double vz = Math.cos(yaw) * speed * Math.signum(client.player.forwardSpeed) - Math.sin(yaw) * speed * client.player.sidewaysSpeed;
                    client.player.setVelocity(vx, client.player.getVelocity().y, vz);
                }
            }

            // NoFall
            if (nofallEnabled) {
                client.player.fallDistance = 0;
            }
        });
    }
}