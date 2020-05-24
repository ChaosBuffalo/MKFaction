package com.chaosbuffalo.mkfaction.client.gui;

import com.chaosbuffalo.mkwidgets.MKWidgets;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.CenterXConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.MarginConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.constraints.VerticalStackConstraint;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKLayout;
import com.chaosbuffalo.mkwidgets.client.gui.layouts.MKStackLayoutVertical;
import com.chaosbuffalo.mkwidgets.client.gui.screens.MKScreen;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKScrollView;
import com.chaosbuffalo.mkwidgets.client.gui.widgets.MKText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

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


    public MKLayout textListDemo(int xPos, int yPos){
        MKLayout root = getRootWithTitle(xPos, yPos, "Factions");
        MKScrollView scrollView = new MKScrollView(0, 0, PANEL_WIDTH, PANEL_HEIGHT, true);
        root.addWidget(scrollView);
        scrollView.setScrollVelocity(3.0);
        root.addConstraintToWidget(new VerticalStackConstraint(), scrollView);
        root.addConstraintToWidget(new CenterXConstraint(), scrollView);
        MKStackLayoutVertical verticalLayout = new MKStackLayoutVertical(0, 0, PANEL_WIDTH);
        verticalLayout.doSetChildWidth(true).setPaddingBot(5).setMarginTop(5).setMarginRight(5)
                .setMarginLeft(5).setMarginBot(5);
        for (int i = 0; i < 25; i++){
            String buttonText = String.format("Test Text: %d", i);
            MKText testText = new MKText(this.font, buttonText);
            testText.setTooltip(buttonText);
            testText.setIsCentered(true);
            testText.setDebugColor(0x3f0000ff);
            verticalLayout.addWidget(testText);
        }
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
        addState("testList", () -> textListDemo(xPos, yPos));
        pushState("intro");
    }
}
