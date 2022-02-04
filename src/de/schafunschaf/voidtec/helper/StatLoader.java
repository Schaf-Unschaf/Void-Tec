package de.schafunschaf.voidtec.helper;

import de.schafunschaf.voidtec.combat.vesai.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.durability.*;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.engine.*;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.flux.*;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.logistic.*;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.projectile.*;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.resistance.*;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.sensor.AutofireAccuracy;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.sensor.SensorProfile;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.sensor.SensorStrength;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.sensor.SightRadius;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.shield.*;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.special.SModSlot;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.special.TimeMult;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.system.SystemCooldown;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.system.SystemRegen;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.system.SystemUses;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.weapon.*;

import java.util.HashMap;
import java.util.Map;

import static de.schafunschaf.voidtec.ids.VT_StatModKeys.*;

public class StatLoader {

    public static Map<String, BaseStatMod> initStatMods() {
        Map<String, BaseStatMod> map = new HashMap<>();

        map.put(ARMOR_HEALTH, new ArmorBonus(ARMOR_HEALTH));
        map.put(ENGINE_HEALTH, new EngineHealth(ENGINE_HEALTH));
        map.put(ENGINE_REPAIR, new EngineRepair(ENGINE_REPAIR));
        map.put(HULL_HEALTH, new HullBonus(HULL_HEALTH));
        map.put(WEAPON_HEALTH, new WeaponHealth(WEAPON_HEALTH));
        map.put(WEAPON_REPAIR, new WeaponRepair(WEAPON_REPAIR));

        map.put(SHIP_ACCELERATION, new ShipAcceleration(SHIP_ACCELERATION));
        map.put(BURN_LEVEL, new BurnLevel(BURN_LEVEL));
        map.put(FUEL_USE, new FuelUse(FUEL_USE));
        map.put(SHIP_MAX_SPEED, new MaxSpeed(SHIP_MAX_SPEED));
        map.put(SHIP_TURN, new ShipTurnRate(SHIP_TURN));
        map.put(ZERO_FLUX_SPEED, new ZeroFluxSpeed(ZERO_FLUX_SPEED));

        map.put(FLUX_CAPACITY, new FluxCapacity(FLUX_CAPACITY));
        map.put(FLUX_DISSIPATION, new FluxDissipation(FLUX_DISSIPATION));
        map.put(HARD_FLUX_DISSIPATION, new HardFluxDissipation(HARD_FLUX_DISSIPATION));
        map.put(OVERLOAD_DURATION, new OverloadDuration(OVERLOAD_DURATION));
        map.put(VENT_RATE, new VentRate(VENT_RATE));
        map.put(ZERO_FLUX_LIMIT, new ZeroFluxLimit(ZERO_FLUX_LIMIT));

        map.put(CR_DEPLOYMENT, new CRDeployment(CR_DEPLOYMENT));
        map.put(CRITICAL_MALFUNCTIONS, new CriticalMalfunction(CRITICAL_MALFUNCTIONS));
        map.put(CR_LOSS, new CRLoss(CR_LOSS));
        map.put(ENGINE_MALFUNCTIONS, new EngineMalfunction(ENGINE_MALFUNCTIONS));
        map.put(MAX_CR, new MaxCR(MAX_CR));
        map.put(PEAK_CR, new PeakCR(PEAK_CR));
        map.put(SHIELD_MALFUNCTIONS, new ShieldMalfunction(SHIELD_MALFUNCTIONS));
        map.put(WEAPON_MALFUNCTIONS, new WeaponMalfunction(WEAPON_MALFUNCTIONS));

        map.put(BALLISTIC_DAMAGE, new BallisticDamage(BALLISTIC_DAMAGE));
        map.put(BALLISTIC_FLUX, new BallisticFluxCost(BALLISTIC_FLUX));
        map.put(BEAM_DAMAGE, new BeamDamage(BEAM_DAMAGE));
        map.put(ENERGY_DAMAGE, new EnergyDamage(ENERGY_DAMAGE));
        map.put(ENERGY_FLUX, new EnergyFluxCost(ENERGY_FLUX));
        map.put(HIT_STRENGTH, new HitStrength(HIT_STRENGTH));
        map.put(MISSILE_DAMAGE, new MissileDamage(MISSILE_DAMAGE));
        map.put(MISSILE_RANGE, new MissileRange(MISSILE_RANGE));
        map.put(MISSILE_SPEED, new MissileSpeed(MISSILE_SPEED));
        map.put(PROJECTILE_SPEED, new ProjectileSpeed(PROJECTILE_SPEED));
        map.put(SHIELD_DAMAGE, new ShieldDamage(SHIELD_DAMAGE));

        map.put(EMP_DAMAGE_TAKEN, new EMPDamageTaken(EMP_DAMAGE_TAKEN));
        map.put(ENERGY_ARMOR_DAMAGE_TAKEN, new EnergyArmorDamageTaken(ENERGY_ARMOR_DAMAGE_TAKEN));
        map.put(ENERGY_SHIELD_DAMAGE_TAKEN, new EnergyShieldDamageTaken(ENERGY_SHIELD_DAMAGE_TAKEN));
        map.put(EXPLOSIVE_ARMOR_DAMAGE_TAKEN, new ExplosiveArmorDamageTaken(EXPLOSIVE_ARMOR_DAMAGE_TAKEN));
        map.put(EXPLOSIVE_SHIELD_DAMAGE_TAKEN, new ExplosiveShieldDamageTaken(EXPLOSIVE_SHIELD_DAMAGE_TAKEN));
        map.put(FRAG_ARMOR_DAMAGE_TAKEN, new FragmentationArmorDamageTaken(FRAG_ARMOR_DAMAGE_TAKEN));
        map.put(FRAG_SHIELD_DAMAGE_TAKEN, new FragmentationShieldDamageTaken(FRAG_SHIELD_DAMAGE_TAKEN));
        map.put(KINETIC_ARMOR_DAMAGE_TAKEN, new KineticArmorDamageTaken(KINETIC_ARMOR_DAMAGE_TAKEN));
        map.put(KINETIC_SHIELD_DAMAGE_TAKEN, new KineticShieldDamageTaken(KINETIC_SHIELD_DAMAGE_TAKEN));

        map.put(AUTOFIRE_ACCURACY, new AutofireAccuracy(AUTOFIRE_ACCURACY));
        map.put(SENSOR_PROFILE, new SensorProfile(SENSOR_PROFILE));
        map.put(SENSOR_STRENGTH, new SensorStrength(SENSOR_STRENGTH));
        map.put(SIGHT_RADIUS, new SightRadius(SIGHT_RADIUS));

        map.put(SHIELD_ABSORPTION, new ShieldAbsorbtion(SHIELD_ABSORPTION));
        map.put(SHIELD_ARC, new ShieldArc(SHIELD_ARC));
        map.put(SHIELD_TURN, new ShieldTurnRate(SHIELD_TURN));
        map.put(SHIELD_UNFOLD, new ShieldUnfold(SHIELD_UNFOLD));
        map.put(SHIELD_UPKEEP, new ShieldUpkeep(SHIELD_UPKEEP));

        map.put(MOD_SLOT, new SModSlot(MOD_SLOT));
        map.put(TIME_MULT, new TimeMult(TIME_MULT));

        map.put(SYSTEM_COOLDOWN, new SystemCooldown(SYSTEM_COOLDOWN));
        map.put(SYSTEM_REGEN, new SystemRegen(SYSTEM_REGEN));
        map.put(SYSTEM_USES, new SystemUses(SYSTEM_USES));

        map.put(BALLISTIC_RANGE, new BallisticRange(BALLISTIC_RANGE));
        map.put(BALLISTIC_ROF, new BallisticROF(BALLISTIC_ROF));
        map.put(ENERGY_RANGE, new EnergyRange(ENERGY_RANGE));
        map.put(ENERGY_ROF, new EnergyROF(ENERGY_ROF));
        map.put(MISSILE_ECCM, new MissileECCM(MISSILE_ECCM));
        map.put(MISSILE_GUIDANCE, new MissileGuidance(MISSILE_GUIDANCE));
        map.put(MISSILE_HEALTH, new MissileHealth(MISSILE_HEALTH));
        map.put(MISSILE_ROF, new MissileROF(MISSILE_ROF));
        map.put(WEAPON_RECOIL, new WeaponRecoil(WEAPON_RECOIL));
        map.put(WEAPON_TURN, new WeaponTurnRate(WEAPON_TURN));

        return map;
    }
}
