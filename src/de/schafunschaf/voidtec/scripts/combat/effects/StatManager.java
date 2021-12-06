package de.schafunschaf.voidtec.scripts.combat.effects;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.UpgradeQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.durability.WeaponRepair;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.engine.FuelUse;
import de.schafunschaf.voidtec.scripts.combat.effects.esu.flux.ZeroFluxLimit;
import lombok.extern.log4j.Log4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static de.schafunschaf.bountiesexpanded.util.ComparisonTools.isNull;

@Log4j
public class StatManager implements StatApplier {
    private static transient StatManager statManager;
    private final Set<StatApplier> allStatAppliers = new HashSet<>();

    private StatManager(StatApplier... statAppliers) {
        allStatAppliers.addAll(Arrays.asList(statAppliers));
    }

    public static StatManager getInstance() {
        if (isNull(statManager)) {
            statManager = new StatManager(
//                    new ShieldAbsorption(0.15f), new ShieldArc(0.35f), new ShieldUpkeep(0.25f), new ShieldUnfold(0.50f), new ShieldTurnRate(0.50f),
//                    new BallisticRange(0.25f), new BallisticROF(0.20f), new EnergyRange(0.25f), new EnergyROF(0.20f),
//                    new MissileECCM(0.20f), new MissileGuidance(0.35f), new MissileHealth(0.50f), new MissileRange(0.35f), new MissileReload(0.40f),
//                    new WeaponRecoil(0.40f), new WeaponTurnRate(0.50f),
//                    new CRDeployment(0.25f), new CRLoss(0.25f), new MaxCR(0.10f), new PeakCR(0.30f),
//                    new CriticalMalfunction(0.40f), new EngineMalfunction(0.40f), new ShieldMalfunction(0.40f), new WeaponMalfunction(0.40f),
//                    new AutofireAccuracy(0.40f), new SensorProfile(0.35f), new SensorStrength(0.50f), new SightRadius(0.40f),
//                    new EMPDamageTaken(0.30f), new EnergyArmorDamageTaken(0.20f), new ExplosiveArmorDamageTaken(0.20f), new FragmentationArmorDamageTaken(0.20f), new KineticArmorDamageTaken(0.20f),
//                    new EnergyShieldDamageTaken(0.20f), new ExplosiveShieldDamageTaken(0.20f), new FragmentationShieldDamageTaken(0.20f), new KineticShieldDamageTaken(0.20f),
//                    new BallisticDamage(0.15f), new BeamDamage(0.15f), new EnergyDamage(0.15f),
//                    new MissileDamage(0.15f), new ShieldDamage(0.15f), new BallisticFluxCost(0.15f), new EnergyFluxCost(0.15f),
//                    new MissileSpeed(0.30f), new ProjectileSpeed(0.30f), new HitStrength(0.25f),
                    new de.schafunschaf.voidtec.scripts.combat.effects.esu.flux.FluxCapacity(0.30f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.flux.FluxDissipation(0.30f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.flux.HardFluxDissipation(0.5f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.flux.OverloadDuration(0.25f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.flux.VentRate(0.30f), new ZeroFluxLimit(0.2f),
                    new de.schafunschaf.voidtec.scripts.combat.effects.esu.engine.Acceleration(0.50f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.engine.MaxSpeed(0.20f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.engine.TurnRate(0.50f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.engine.ZeroFluxSpeed(0.25f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.engine.BurnLevel(1.5f), new FuelUse(0.30f),
                    new de.schafunschaf.voidtec.scripts.combat.effects.esu.durability.ArmorBonus(0.25f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.durability.HullBonus(0.25f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.durability.EngineHealth(0.50f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.durability.WeaponHealth(0.50f), new de.schafunschaf.voidtec.scripts.combat.effects.esu.durability.EngineRepair(0.50f), new WeaponRepair(0.50f)
            );
        }
        return statManager;
    }

    @Override
    public void apply(MutableShipStatsAPI stats, String id, Random random, UpgradeQuality quality) {
        WeightedRandomPicker<StatApplier> picker = new WeightedRandomPicker<>(random);
        picker.addAll(allStatAppliers);
        int desiredUpgrades = allStatAppliers.size();
        while (!picker.isEmpty() && desiredUpgrades > 0) {
            StatApplier statApplier = picker.pickAndRemove();
            if (!statApplier.canApply(stats)) {
                log.debug("Skipping conflicting applicator " + statApplier);
                continue;
            }
            log.debug("Adding stats for " + statApplier);
            quality = UpgradeQuality.getRandomQuality(random);
            statApplier.apply(stats, id, random, quality);
            desiredUpgrades--;
        }
    }

    @Override
    public void remove(MutableShipStatsAPI stats, String id) {

    }

    @Override
    public boolean canApply(MutableShipStatsAPI stats) {
        return true;
    }

    @Override
    public void generateTooltipEntry(MutableShipStatsAPI stats, String id, TooltipMakerAPI tooltip) {
        CategoryManager categoryManager = CategoryManager.getInstance();
        categoryManager.generateCategoryEntries(stats, id, tooltip);
    }

    @Override
    public UpgradeCategory getCategory() {
        return null;
    }
}
