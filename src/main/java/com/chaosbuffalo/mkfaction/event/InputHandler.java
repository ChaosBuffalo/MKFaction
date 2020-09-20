package com.chaosbuffalo.mkfaction.event;

import com.chaosbuffalo.mkfaction.MKFactionMod;

import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mkfaction.client.gui.FactionScreen;
import com.chaosbuffalo.mkfaction.faction.PlayerFactionStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid=MKFactionMod.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
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

    public static <E extends Entity> EntityRayTraceResult rayTraceEntities(Class<E> clazz, World world,
                                                                           Vec3d from, Vec3d to,
                                                                           Vec3d aaExpansion,
                                                                           float aaGrowth,
                                                                           float entityExpansion,
                                                                           final Predicate<E> filter) {
        Entity nearest = null;
        double distance = 0;
        Vec3d hitVec = null;
        AxisAlignedBB bb = new AxisAlignedBB(new BlockPos(from), new BlockPos(to))
                .expand(aaExpansion.x, aaExpansion.y, aaExpansion.z)
                .grow(aaGrowth);
        List<E> entities = world.getEntitiesWithinAABB(clazz, bb, filter);
        for (Entity entity : entities) {
            AxisAlignedBB entityBB = entity.getBoundingBox().grow(entityExpansion);
            Optional<Vec3d> intercept = entityBB.rayTrace(from, to);
            if (intercept.isPresent()) {
                Vec3d vec = intercept.get();
                double dist = from.distanceTo(vec);
                if (dist < distance || distance == 0.0D) {
                    nearest = entity;
                    hitVec = vec;
                    distance = dist;
                }
            }
        }
        if (nearest != null)
            return new EntityRayTraceResult(nearest, hitVec);
        return null;
    }


    @Nullable
    public static <E extends Entity> EntityRayTraceResult getLookingAtNonPlayer(Class<E> clazz,
                                                                                final Entity mainEntity,
                                                                                double distance) {
        Predicate<E> finalFilter = e -> e != mainEntity &&
                !(e instanceof PlayerEntity) &&
                e.canBeCollidedWith() && clazz.isInstance(e);

        EntityRayTraceResult position = null;
        if (mainEntity.world != null) {
            Vec3d look = mainEntity.getLookVec().scale(distance);
            Vec3d from = mainEntity.getPositionVector().add(0, mainEntity.getEyeHeight(), 0);
            Vec3d to = from.add(look);
            position = rayTraceEntities(clazz, mainEntity.world, from, to, Vec3d.ZERO, .5f,
                    .5f, finalFilter);
        }
        return position;
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onMouseEvent(InputEvent.MouseInputEvent event) {
        handleInputEvent();
    }

    private static void handleInputEvent() {
        while (CON_KEY_BIND.isPressed()) {
            PlayerEntity player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            EntityRayTraceResult result = getLookingAtNonPlayer(LivingEntity.class, player, 30.0f);
            if (result != null) {
                result.getEntity().getCapability(FactionCapabilities.MOB_FACTION_CAPABILITY).ifPresent(
                        (mobFaction) -> player.getCapability(FactionCapabilities.PLAYER_FACTION_CAPABILITY)
                                .ifPresent((playerFaction) -> {
                                    PlayerFactionStatus status = playerFaction.getFactionStatus(mobFaction.getFactionName());
                                    ITextComponent msg = new TranslationTextComponent(status.getTranslationKey() + ".con",
                                            result.getEntity().getName())
                                            .applyTextStyle(status.getColor());
                                    if (player.isCreative()) {
                                        msg.appendText(String.format(" (%s)", mobFaction.getFactionName()));
                                    }
                                    player.sendMessage(msg);
                                }));
            }
        }
        while (FACTION_PANEL_KEY_BIND.isPressed()) {
            Minecraft.getInstance().displayGuiScreen(new FactionScreen());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onEvent(InputEvent.KeyInputEvent event) {
        handleInputEvent();
    }
}