package com.chaosbuffalo.mkfaction.event;

import com.chaosbuffalo.mkcore.MKCore;
import com.chaosbuffalo.mkcore.utils.RayTraceUtils;
import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mkfaction.client.gui.FactionPage;
import com.chaosbuffalo.mkfaction.faction.PlayerFactionStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = MKFactionMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InputHandler {

    public static final KeyBinding CON_KEY_BIND = new KeyBinding("key.mkfaction.con.desc",
            GLFW.GLFW_KEY_C,
            "key.mkfaction.category");
    public static final KeyBinding FACTION_PANEL_KEY_BIND = new KeyBinding("key.mkfaction.panel.desc",
            GLFW.GLFW_KEY_P,
            "key.mkfaction.category");

    public static void registerKeybinds() {
        ClientRegistry.registerKeyBinding(CON_KEY_BIND);
        ClientRegistry.registerKeyBinding(FACTION_PANEL_KEY_BIND);
    }

    @Nullable
    public static <E extends Entity> EntityRayTraceResult getLookingAtNonPlayer(Class<E> clazz,
                                                                                final Entity mainEntity,
                                                                                double distance) {
        RayTraceResult result = RayTraceUtils.getLookingAt(clazz, mainEntity, 30.f, e -> !(e instanceof PlayerEntity));
        return result instanceof EntityRayTraceResult ? (EntityRayTraceResult) result : null;
    }

    private static void handleInputEvent() {
        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        while (CON_KEY_BIND.isPressed()) {
            EntityRayTraceResult trace = getLookingAtNonPlayer(LivingEntity.class, player, 30.0f);
            if (trace != null && trace.getType() != RayTraceResult.Type.MISS) {
                Entity target = trace.getEntity();
                target.getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY).ifPresent(mobFaction ->
                        player.getCapability(FactionCapabilities.PLAYER_FACTION_CAPABILITY).ifPresent(playerFaction -> {
                            PlayerFactionStatus status = playerFaction.getFactionStatus(mobFaction);
                            IFormattableTextComponent msg = new TranslationTextComponent(status.getTranslationKey() + ".con",
                                    target.getName()).mergeStyle(status.getColor());
                            if (player.isCreative()) {
                                msg.appendString(String.format(" (%s)", mobFaction.getFactionName()));
                            }
                            player.sendMessage(msg, Util.DUMMY_UUID);
                        }));
            }
        }
        while (FACTION_PANEL_KEY_BIND.isPressed()) {
            MKCore.getPlayer(player).ifPresent(playerData -> Minecraft.getInstance().displayGuiScreen(new FactionPage(playerData)));
        }
    }

    @SubscribeEvent
    public static void onMouseEvent(InputEvent.MouseInputEvent event) {
        handleInputEvent();
    }

    @SubscribeEvent
    public static void onKeyEvent(InputEvent.KeyInputEvent event) {
        handleInputEvent();
    }
}
