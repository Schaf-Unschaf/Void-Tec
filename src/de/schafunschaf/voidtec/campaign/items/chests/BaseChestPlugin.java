package de.schafunschaf.voidtec.campaign.items.chests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.scripts.VT_DialogHelperOpenStorage;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import lombok.SneakyThrows;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class BaseChestPlugin extends BaseSpecialItemPlugin implements StorageChestPlugin {

    @Override
    public void performRightClickAction() {
        if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
            getChestData().setCurrentMode(getChestData().getCurrentMode().cycleNextMode());
            return;
        }

        if (getChestData().getCurrentMode() != BaseChestData.UseMode.LOCKED) {
            VoidTecUtils.openDialogPlugin(new VT_DialogHelperOpenStorage(this));
        }
    }

    @Override
    public boolean hasRightClickAction() {
        return true;
    }

    @Override
    public boolean shouldRemoveOnRightClickAction() {
        return false;
    }

    @Override
    public float getTooltipWidth() {
        return 320f;
    }

    @SneakyThrows
    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float smallPad = 3f;
        float largePad = 10f;

        String manufacturerName = isNull(getChestData().getManufacturer()) ? "Unknown" : getChestData().getManufacturer();
        FactionAPI faction = Global.getSector().getFaction(manufacturerName);
        Color manufacturerColor = VoidTecUtils.getManufacturerColor(manufacturerName);
        if (!isNull(faction)) {
            manufacturerName = Misc.ucFirst(faction.getDisplayNameWithArticleWithoutArticle());
        }

        tooltip.addTitle(getName(), getChestData().getColor());
        tooltip.addPara("Manufacturer: %s", largePad, Misc.getGrayColor(), manufacturerColor, manufacturerName);
        tooltip.addPara("Needed Space: %s", smallPad, Misc.getGrayColor(), Misc.getHighlightColor(),
                        String.valueOf(((int) stack.getCargoSpacePerUnit())));

        getChestData().createTooltip(tooltip, expanded, transferHandler, stackSource);

        if (!isNull(stackSource)) {
            int currentSize = getChestData().getCurrentSize();
            int maxSize = getChestData().getMaxSize();
            BaseChestData.UseMode currentMode = getChestData().getCurrentMode();
            Color spaceHLColor = currentSize < maxSize ? Misc.getHighlightColor() : Misc.getNegativeHighlightColor();

            if (expanded && (transferHandler.getSubmarketTradedWith()
                                            .getSpecId()
                                            .equals(Submarkets.SUBMARKET_STORAGE) || transferHandler.getSubmarketTradedWith()
                                                                                                    .getSpecId()
                                                                                                    .equals(Submarkets.LOCAL_RESOURCES))) {
                addCostLabel(tooltip, largePad, transferHandler, stackSource);
            } else if (!(transferHandler.getSubmarketTradedWith()
                                        .getSpecId()
                                        .equals(Submarkets.SUBMARKET_STORAGE) || transferHandler.getSubmarketTradedWith()
                                                                                                .getSpecId()
                                                                                                .equals(Submarkets.LOCAL_RESOURCES))) {
                addCostLabel(tooltip, largePad, transferHandler, stackSource);
            }

            tooltip.addPara(String.format("Inventory: %s/%s", currentSize, maxSize), largePad, Misc.getGrayColor(), spaceHLColor,
                            String.valueOf(currentSize), String.valueOf(maxSize));

            if (currentMode == BaseChestData.UseMode.LOCKED) {
                tooltip.addPara("This Chest is %s!", largePad, currentMode.getColor(),
                                FormattingTools.capitalizeFirst(currentMode.name().toLowerCase()));
                tooltip.addPara("Ctrl+Right-Click to unlock with the %s", smallPad, Misc.getGrayColor(), Misc.getHighlightColor(),
                                "Skeleton Key");
            } else {
                tooltip.addPara("Right-Click to %s", smallPad, currentMode.getColor(),
                                FormattingTools.capitalizeFirst(currentMode.name().toLowerCase()));
                tooltip.addPara("Ctrl+Right-Click to switch modes", Misc.getGrayColor(), largePad);
            }
        }
    }

    @Override
    public boolean isTooltipExpandable() {
        return true;
    }

    @Override
    public String getName() {
        return getChestData().getName();
    }

    @Override
    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        return super.getPrice(market, submarket);
    }

    @Override
    public void render(float x, float y, float w, float h, float alphaMult, float glowMult, SpecialItemRendererAPI renderer) {
        getChestData().render(x, y, w, h, alphaMult, glowMult, renderer);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
//            renderInventoryCapacity(x, y, w);
        }
    }

    @Override
    public String getDesignType() {
        return super.getDesignType();
    }

    public void setSize(int num) {
        StorageChestData augmentChestData = getChestData();
        augmentChestData.setCurrentSize(num);
    }

    public StorageChestData getChestData() {
        return (StorageChestData) stack.getSpecialDataIfSpecial();
    }

    public void setName(String name) {
        getChestData().setName(name);
    }

    protected void renderInventoryCapacity(float x, float y, float w) {
        float width = w - 10f;
        float height = 10f;
        float borderPadding = 5f;
        float borderSize = 1f;
        float margin = 3f;
        float fillPercent = ((float) getChestData().getCurrentSize()) / getChestData().getMaxSize();
        float barWidth = (width - margin * 2) * fillPercent;

        Color barColor = fillPercent > 0.9f
                         ? Misc.getNegativeHighlightColor() : fillPercent > 0.5f
                                                              ? Misc.getHighlightColor() : Misc.getPositiveHighlightColor();

        renderBorder(x, y, width, height, borderPadding);
        renderBG(x, y, width, height, borderPadding, borderSize);
        renderBar(x, y, barWidth, height, margin, borderPadding, barColor);
    }

    private void renderBorder(float x, float y, float width, float height, float borderPadding) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Color color = getChestData().getColor();

        GL11.glColor4ub((byte) color.getRed(),
                        (byte) color.getGreen(),
                        (byte) color.getBlue(),
                        (byte) color.getAlpha());

        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(x + borderPadding, y + borderPadding); // BL
            GL11.glVertex2f(x + borderPadding, y + borderPadding + height); // TL
            GL11.glVertex2f(x + borderPadding + width, y + borderPadding + height); // TR
            GL11.glVertex2f(x + borderPadding + width, y + borderPadding); // BR
        }
        GL11.glEnd();
    }

    private void renderBG(float x, float y, float width, float height, float margin, float borderSize) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Color color = Color.GRAY;

        GL11.glColor4ub((byte) color.getRed(),
                        (byte) color.getGreen(),
                        (byte) color.getBlue(),
                        (byte) color.getAlpha());

        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(x + borderSize + margin, y + borderSize + margin); // BL
            GL11.glVertex2f(x + borderSize + margin, y + borderSize + height); // TL
            GL11.glVertex2f(x + borderSize + width + margin, y + borderSize + height + margin); // TR
            GL11.glVertex2f(x + borderSize + width + margin, y + borderSize + margin); // BR
        }
        GL11.glEnd();
    }

    private void renderBar(float x, float y, float width, float height, float margin, float borderPadding, Color color) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GL11.glColor4ub((byte) color.getRed(),
                        (byte) color.getGreen(),
                        (byte) color.getBlue(),
                        (byte) color.getAlpha());

        GL11.glBegin(GL11.GL_QUADS);
        {
            GL11.glVertex2f(x + borderPadding + margin, y + borderPadding + margin); // BL
            GL11.glVertex2f(x + borderPadding + margin, y + borderPadding + height - margin); // TL
            GL11.glVertex2f(x + borderPadding + width + margin, y + borderPadding + height - margin); // TR
            GL11.glVertex2f(x + borderPadding + width + margin, y + borderPadding + margin); // BR
        }
        GL11.glEnd();
    }
}
