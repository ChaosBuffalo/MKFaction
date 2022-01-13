package com.chaosbuffalo.mkfaction.client.gui;

import com.chaosbuffalo.mkcore.client.gui.PlayerPageBase;
import com.chaosbuffalo.mkcore.client.gui.PlayerPageRegistry;
import com.chaosbuffalo.mkcore.core.MKPlayerData;
import com.chaosbuffalo.mkfaction.MKFactionMod;
import com.chaosbuffalo.mkfaction.capabilities.FactionCapabilities;
import com.chaosbuffalo.mkfaction.event.MKFactionRegistry;
import com.chaosbuffalo.mkfaction.faction.MKFaction;
import com.chaosbuffalo.mkfaction.faction.PlayerFactionEntry;
import com.chaosbuffalo.mkfaction.faction.PlayerFactionStatus;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.MarginConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKLayout;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKStackLayoutVertical;
import com.chaosbuffalo.mkwidgets.client.gui.screens.MKScreen;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKScrollView;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKText;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.InterModComms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FactionPage extends PlayerPageBase {

    protected MKScrollView scrollView;

    public FactionPage(MKPlayerData playerData) {
        super(playerData, new StringTextComponent("Factions"));
    }

    @Override
    public ResourceLocation getPageId() {
        return new ResourceLocation(MKFactionMod.MODID, "factions");
    }

    @Override
    public void setupScreen() {
        super.setupScreen();
        addWidget(createScrollingPanelWithContent(this::createFactionEntryList, this::setupFactionHeader, v -> scrollView = v));
    }

    @Override
    protected void persistState(boolean wasResized) {
        super.persistState(wasResized);
        persistScrollView(() -> scrollView, wasResized);
    }

    public MKLayout getFactionEntryLayout(PlayerFactionEntry entry, MKFaction faction, int width) {
        MKLayout entryLayout = new MKLayout(0, 0, width, font.FONT_HEIGHT + 10);
        entryLayout.setMargins(5, 5, 5, 5);

        TranslationTextComponent nameText = new TranslationTextComponent(faction.getTranslationKey());
        MKText factionName = new MKText(font, nameText);
        factionName.setColor(0xffffffff);
        factionName.setWidth(font.getStringWidth(nameText.getString()));
        entryLayout.addWidget(factionName, MarginConstraint.TOP, MarginConstraint.LEFT);

        PlayerFactionStatus factionStatus = entry.getFactionStatus();
        ITextComponent valueText = new TranslationTextComponent(factionStatus.getTranslationKey())
                .appendSibling(new StringTextComponent(String.format("(%d)", entry.getFactionScore())))
                .mergeStyle(factionStatus.getColor());
        MKText factionValue = new MKText(font, valueText);
        factionValue.setWidth(font.getStringWidth(valueText.getString()));
        entryLayout.addWidget(factionValue, MarginConstraint.TOP, MarginConstraint.RIGHT);
        return entryLayout;
    }

    private MKWidget createFactionEntryList(MKPlayerData pData, int panelWidth) {
        MKStackLayoutVertical stackLayout = new MKStackLayoutVertical(0, 0, panelWidth);
        stackLayout.setMargins(4, 4, 4, 4);
        stackLayout.setPaddingTop(2).setPaddingBot(2);
        stackLayout.doSetChildWidth(true);

        pData.getEntity().getCapability(FactionCapabilities.PLAYER_FACTION_CAPABILITY).ifPresent(playerFaction -> {
            List<MKFaction> factions = new ArrayList<>();
            for (ResourceLocation factionName : playerFaction.getFactionMap().keySet()) {
                MKFaction faction = MKFactionRegistry.getFaction(factionName);
                if (faction != null) {
                    factions.add(faction);
                }
            }

            factions.sort(Comparator.comparing(mkFaction -> I18n.format(mkFaction.getTranslationKey())));
            for (MKFaction faction : factions) {
                playerFaction.getFactionEntry(faction.getRegistryName()).ifPresent(entry -> {
                    MKLayout factionLayout = getFactionEntryLayout(entry, faction, panelWidth - 10);
                    stackLayout.addWidget(factionLayout);
                });
            }
        });
        return stackLayout;
    }

    public void setupFactionHeader(MKPlayerData playerData, MKLayout layout) {

    }

    static class PageFactory implements PlayerPageRegistry.Extension {

        @Override
        public ResourceLocation getId() {
            return new ResourceLocation(MKFactionMod.MODID, "factions");
        }

        @Override
        public ITextComponent getDisplayName() {
            return new StringTextComponent("Factions");
        }

        @Override
        public MKScreen createPage(MKPlayerData playerData) {
            return new FactionPage(playerData);
        }
    }

    public static void registerPlayerPage() {
        PlayerPageRegistry.ExtensionProvider provider = PageFactory::new;
        InterModComms.sendTo("mkcore", "register_player_page", () -> {
            MKFactionMod.LOGGER.info("Faction register player page");
            return provider;
        });
    }
}
