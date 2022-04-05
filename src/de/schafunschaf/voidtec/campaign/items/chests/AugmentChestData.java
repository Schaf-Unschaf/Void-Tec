package de.schafunschaf.voidtec.campaign.items.chests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.SpecialItemPlugin.SpecialItemRendererAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.helper.ColorShifter;
import de.schafunschaf.voidtec.util.CargoUtils;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class AugmentChestData extends BaseChestData {

    private final ColorShifter colorShifter;

    public AugmentChestData(String id, String data, String name, int maxSize) {
        super(id, data, name, "VoidTec", maxSize, null, false);
        this.colorShifter = new ColorShifter(null);
    }

    @Override
    protected void addDescription(TooltipMakerAPI tooltip) {
        float largePad = 10f;
        int maxSize = getMaxSize();

        String hlString = maxSize + " Augment Chips";
        tooltip.addPara(String.format(
                "This handy little chest was designed for any eager captain or collector in the sector and stores up to %s inside.",
                hlString), largePad, Misc.getHighlightColor(), hlString);
    }

    @Override
    public void render(float x, float y, float w, float h, float alphaMult, float glowMult, SpecialItemRendererAPI renderer) {
        SpriteAPI glow = Global.getSettings().getSprite(glowSprite);
        Color glowColor = Misc.scaleColor(colorShifter.shiftColor(0.5f), 0.5f);

        SpriteAPI base = Global.getSettings().getSprite(baseSprite);
        base.renderAtCenter(0f, 0f);

        glow.setColor(glowColor);
        glow.setAdditiveBlend();
        glow.renderAtCenter(0f, 0f);

    }

    @Override
    public String getTitle() {
        return "Store Augments";
    }

    @Override
    public CargoAPI getAllowedCargo() {
        return CargoUtils.getAugmentsInPlayerCargo();
    }

    @Override
    public List<String> getAllowedItemsString() {
        return Collections.singletonList("Augments");
    }
}
