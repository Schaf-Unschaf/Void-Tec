package de.schafunschaf.voidtec.combat.scripts.stats;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineLayers;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import data.scripts.util.MagicRender;
import de.schafunschaf.voidtec.combat.vesai.CombatScriptRunner;
import de.schafunschaf.voidtec.ids.VT_DynamicStatKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.lazywizard.lazylib.FastTrig;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@RequiredArgsConstructor
public class TimeAccelerationEffect implements CombatScriptRunner {

    private static final float AFTERIMAGE_THRESHOLD = 0.2f;
    private static final Color AFTERIMAGE_COLOR_POSITIVE = new Color(50, 50, 100);
    private static final Color AFTERIMAGE_COLOR_NEGATIVE = new Color(100, 50, 50);
    private static final String HUD_ICON = "graphics/icons/hullsys/temporal_shell.png";

    private final String scriptID;

    @Override
    public void run(ShipAPI ship, float amount, Object data) {
        MutableStat stat = ship.getMutableStats().getDynamic().getStat(VT_DynamicStatKeys.TIME_ACCELERATION_TRACKER);
        int timeAcceleration = (int) (ship.getMutableStats().getTimeMult().getModifiedValue() * 100);
        boolean isDebuff = timeAcceleration < 100;
        Color afterimageColor = isDebuff ? AFTERIMAGE_COLOR_NEGATIVE : AFTERIMAGE_COLOR_POSITIVE;

        Global.getCombatEngine().maintainStatusForPlayerShip(scriptID, HUD_ICON,
                                                             "Time Acceleration",
                                                             String.format("Total acceleration at %s%%",
                                                                           timeAcceleration),
                                                             isDebuff);

        MutableStat.StatMod statMod = stat.getFlatStatMod(scriptID);
        if (isNull(statMod)) {
            stat.modifyFlat(scriptID, AFTERIMAGE_THRESHOLD);
            statMod = stat.getFlatStatMod(scriptID);
        }

        stat.modifyFlat(scriptID, statMod.getValue() + amount);

        if (statMod.getValue() > AFTERIMAGE_THRESHOLD) {
            SpriteAPI sprite = ship.getSpriteAPI();

            float offsetX = sprite.getWidth() / 2 - sprite.getCenterX();
            float offsetY = sprite.getHeight() / 2 - sprite.getCenterY();

            float trueOffsetX = (float) FastTrig.cos(Math.toRadians(ship.getFacing() - 90f))
                    * offsetX - (float) FastTrig.sin(Math.toRadians(ship.getFacing() - 90f)) * offsetY;
            float trueOffsetY = (float) FastTrig.sin(Math.toRadians(ship.getFacing() - 90f))
                    * offsetX + (float) FastTrig.cos(Math.toRadians(ship.getFacing() - 90f)) * offsetY;

            MagicRender.battlespace(
                    Global.getSettings().getSprite(ship.getHullSpec().getSpriteName()),
                    new Vector2f(ship.getLocation().getX() + trueOffsetX, ship.getLocation().getY() + trueOffsetY),
                    new Vector2f(0f, 0f),
                    new Vector2f(ship.getSpriteAPI().getWidth(), ship.getSpriteAPI().getHeight()),
                    new Vector2f(0, 0),
                    ship.getFacing() - 90f,
                    0f,
                    afterimageColor,
                    true,
                    0f,
                    0f,
                    0f,
                    0f,
                    0f,
                    0.1f,
                    amount,
                    0.5f,
                    CombatEngineLayers.BELOW_SHIPS_LAYER);

            stat.modifyFlat(scriptID, statMod.getValue() - AFTERIMAGE_THRESHOLD);
        }
    }
}
