package com.chaosbuffalo.mkfaction.client.gui;

import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.mkfaction.faction.PlayerFactionEntry;
import com.chaosbuffalo.mkfaction.faction.PlayerFactionStatus;
import com.chaosbuffalo.mkwidgets.MKWidgets;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.CenterXConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.MarginConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.VerticalStackConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKLayout;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKStackLayoutVertical;
import com.chaosbuffalo.mkwidgets.client.gui.screens.MKScreen;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKAbstractGui;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKScrollView;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKText;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FactionScreen extends MKScreen {
    private final int PANEL_WIDTH = 320;
    private final int PANEL_HEIGHT = 240;
    private static final ResourceLocation BG_LOC = new ResourceLocation(MKWidgets.MODID,
            "textures/gui/background_320.png");

    public FactionScreen() {
        super(new StringTextComponent("Faction Screen"));
    }

    private MKLayout getRootWithTitle(int xPos, int yPos, String title){
        MKLayout root = new MKLayout(xPos, yPos, PANEL_WIDTH, PANEL_HEIGHT);
        root.setMargins(5, 5, 5, 5);
        root.setPaddingBot(10);
        root.setPaddingTop(10);
        MKText titleText = new MKText(font, title);
        root.addWidget(titleText);
        root.addConstraintToWidget(new MarginConstraint(MarginConstraint.MarginType.TOP), titleText);
        root.addConstraintToWidget(new CenterXConstraint(), titleText);
        return root;
    }

    public MKLayout getFactionEntryLayout(PlayerFactionEntry entry, MKFaction faction, int width){
        MKLayout root = new MKLayout(0, 0, width, font.FONT_HEIGHT + 10);
        root.setMarginBot(5).setMarginTop(5).setMarginLeft(5).setMarginRight(5);
        TranslationTextComponent nameText = new TranslationTextComponent(faction.getTranslationKey());
        MKText factionName = new MKText(this.font, nameText, 200, font.FONT_HEIGHT);
        factionName.setWidth(font.getStringWidth(nameText.getFormattedText()));
        root.addWidget(factionName);
        root.addConstraintToWidget(new MarginConstraint(MarginConstraint.MarginType.TOP), factionName);
        root.addConstraintToWidget(new MarginConstraint(MarginConstraint.MarginType.LEFT), factionName);
        PlayerFactionStatus factionStatus = entry.getFactionStatus();
        ITextComponent valueText = new TranslationTextComponent(
                PlayerFactionStatus.translationKeyFromFactionStatus(factionStatus))
                .appendText(String.format("(%d)", entry.getFactionScore())).applyTextStyle(
                        PlayerFactionStatus.colorForFactionStatus(factionStatus));
        MKText factionValue = new MKText(this.font, valueText, 200, font.FONT_HEIGHT);
        factionValue.setWidth(font.getStringWidth(valueText.getFormattedText()));
        root.addWidget(factionValue);
        root.addConstraintToWidget(new MarginConstraint(MarginConstraint.MarginType.TOP), factionValue);
        root.addConstraintToWidget(new MarginConstraint(MarginConstraint.MarginType.RIGHT), factionValue);
        return root;
    }


    public MKLayout factionList(int xPos, int yPos){
        MKLayout root = getRootWithTitle(xPos, yPos, "Factions");
        MKScrollView scrollView = new MKScrollView(0, 0, PANEL_WIDTH, PANEL_HEIGHT, true);
        root.addWidget(scrollView);
        scrollView.setScrollVelocity(3.0);
        root.addConstraintToWidget(new VerticalStackConstraint(), scrollView);
        root.addConstraintToWidget(new CenterXConstraint(), scrollView);
        MKStackLayoutVertical verticalLayout = new MKStackLayoutVertical(0, 0, PANEL_WIDTH);
        verticalLayout.doSetChildWidth(true).setPaddingBot(5).setMarginTop(5).setMarginRight(5)
                .setMarginLeft(5).setMarginBot(5);
        if (getMinecraft().player == null){
            return root;
        }
        getMinecraft().player.getCapability(FactionCapabilities.PLAYER_FACTION_CAPABILITY).ifPresent(playerFaction -> {
            List<MKFaction> factions = new ArrayList<>();
            for (ResourceLocation factionName : playerFaction.getFactionMap().keySet()){
                MKFaction faction = MKFactionRegistry.getFaction(factionName);
                if (faction != null){
                    factions.add(faction);
                }
            }
            factions.sort(Comparator.comparing(mkFaction -> I18n.format(mkFaction.getTranslationKey())));
            for (MKFaction faction : factions){
                MKLayout factionLayout = getFactionEntryLayout(
                        playerFaction.getFactionEntry(faction.getRegistryName()),
                        faction,
                        PANEL_WIDTH - 10);
                verticalLayout.addWidget(factionLayout);
            }

        });
        scrollView.addWidget(verticalLayout);
        // we need to resolve constraints so we can center scrollview content properly
        root.manualRecompute();
        scrollView.centerContentX();
        scrollView.setToTop();
        return root;
    }

    @Override
    public void setupScreen() {
        super.setupScreen();
        int xPos = width / 2 - PANEL_WIDTH / 2;
        int yPos = height / 2 - PANEL_HEIGHT / 2;
        addState("factionList", () -> factionList(xPos, yPos));
        pushState("factionList");
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        int xPos = width / 2 - PANEL_WIDTH / 2;
        int yPos = height / 2 - PANEL_HEIGHT / 2;
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getInstance().getTextureManager().bindTexture(BG_LOC);
        RenderSystem.disableLighting();
        MKAbstractGui.mkBlitUVSizeSame(xPos, yPos, 0, 0, PANEL_WIDTH, PANEL_HEIGHT, 512, 512);
        super.render(mouseX, mouseY, partialTicks);
        RenderSystem.enableLighting();
    }
}
