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

        map.put(ARMOR_HEALTH, new ArmorBonus(ARMOR_HEALTH, "Armor bonus"));
        map.put(ENGINE_HEALTH, new EngineHealth(ENGINE_HEALTH, "Engine health"));
        map.put(ENGINE_REPAIR, new EngineRepair(ENGINE_REPAIR, "Engine repair time"));
        map.put(HULL_HEALTH, new HullBonus(HULL_HEALTH, "Hull bonus"));
        map.put(WEAPON_HEALTH, new WeaponHealth(WEAPON_HEALTH, "Weapon health"));
        map.put(WEAPON_REPAIR, new WeaponRepair(WEAPON_REPAIR, "Weapon repair time"));

        map.put(SHIP_ACCELERATION, new ShipAcceleration(SHIP_ACCELERATION, "Ship acceleration"));
        map.put(BURN_LEVEL, new BurnLevel(BURN_LEVEL, "Burn level"));
        map.put(FUEL_USE, new FuelUse(FUEL_USE, "Fuel use"));
        map.put(SHIP_MAX_SPEED, new MaxSpeed(SHIP_MAX_SPEED, "Ship max speed"));
        map.put(SHIP_TURN, new ShipTurnRate(SHIP_TURN, "Ship turn rate"));
        map.put(ZERO_FLUX_SPEED, new ZeroFluxSpeed(ZERO_FLUX_SPEED, "Zero-Flux speed"));

        map.put(FLUX_CAPACITY, new FluxCapacity(FLUX_CAPACITY, "Flux capacity"));
        map.put(FLUX_DISSIPATION, new FluxDissipation(FLUX_DISSIPATION, "Flux dissipation"));
        map.put(HARD_FLUX_DISSIPATION, new HardFluxDissipation(HARD_FLUX_DISSIPATION, "Hard-Flux dissipation"));
        map.put(OVERLOAD_DURATION, new OverloadDuration(OVERLOAD_DURATION, "Overload duration"));
        map.put(VENT_RATE, new VentRate(VENT_RATE, "Vent rate"));
        map.put(ZERO_FLUX_LIMIT, new ZeroFluxLimit(ZERO_FLUX_LIMIT, "Zero-Flux limit"));

        map.put(CR_DEPLOYMENT, new CRDeployment(CR_DEPLOYMENT, "CR per deployment"));
        map.put(CRITICAL_MALFUNCTIONS, new CriticalMalfunction(CRITICAL_MALFUNCTIONS, "Critical malfunction chance"));
        map.put(CR_LOSS, new CRLoss(CR_LOSS, "CR Loss per second"));
        map.put(ENGINE_MALFUNCTIONS, new EngineMalfunction(ENGINE_MALFUNCTIONS, "Engine malfunction chance"));
        map.put(CR_MAX, new CRMax(CR_MAX, "Maximum CR"));
        map.put(CR_PEAK, new CRPeak(CR_PEAK, "Peak CR duration"));
        map.put(SHIELD_MALFUNCTIONS, new ShieldMalfunction(SHIELD_MALFUNCTIONS, "Shield malfunction chance"));
        map.put(WEAPON_MALFUNCTIONS, new WeaponMalfunction(WEAPON_MALFUNCTIONS, "Weapon malfunction chance"));
        map.put(SUPPLIES_PER_MONTH, new SuppliesPerMonth(SUPPLIES_PER_MONTH, "Supplies per month"));

        map.put(BALLISTIC_DAMAGE, new BallisticDamage(BALLISTIC_DAMAGE, "Ballistic damage dealt"));
        map.put(BALLISTIC_FLUX, new BallisticFluxCost(BALLISTIC_FLUX, "Ballistic flux generation"));
        map.put(BEAM_DAMAGE, new BeamDamage(BEAM_DAMAGE, "Beam damage dealt"));
        map.put(ENERGY_DAMAGE, new EnergyDamage(ENERGY_DAMAGE, "Energy damage dealt"));
        map.put(ENERGY_FLUX, new EnergyFluxCost(ENERGY_FLUX, "Energy flux generation"));
        map.put(HIT_STRENGTH, new HitStrength(HIT_STRENGTH, "Hit-Strength calculation bonus"));
        map.put(MISSILE_DAMAGE, new MissileDamage(MISSILE_DAMAGE, "Missile damage dealt"));
        map.put(MISSILE_RANGE, new MissileRange(MISSILE_RANGE, "Missile range"));
        map.put(MISSILE_SPEED, new MissileSpeed(MISSILE_SPEED, "Missile speed"));
        map.put(PROJECTILE_SPEED, new ProjectileSpeed(PROJECTILE_SPEED, "Projectile speed"));
        map.put(SHIELD_DAMAGE, new ShieldDamage(SHIELD_DAMAGE, "Damage dealt to Shields"));

        map.put(EMP_DAMAGE_TAKEN, new EMPDamageTaken(EMP_DAMAGE_TAKEN, "EMP damage taken"));
        map.put(ENERGY_ARMOR_DAMAGE_TAKEN, new EnergyArmorDamageTaken(ENERGY_ARMOR_DAMAGE_TAKEN, "Energy damage taken (Armor/Hull)"));
        map.put(ENERGY_SHIELD_DAMAGE_TAKEN, new EnergyShieldDamageTaken(ENERGY_SHIELD_DAMAGE_TAKEN, "Energy damage taken (Shield)"));
        map.put(EXPLOSIVE_ARMOR_DAMAGE_TAKEN,
                new ExplosiveArmorDamageTaken(EXPLOSIVE_ARMOR_DAMAGE_TAKEN, "Explosive damage taken (Armor/Hull)"));
        map.put(EXPLOSIVE_SHIELD_DAMAGE_TAKEN,
                new ExplosiveShieldDamageTaken(EXPLOSIVE_SHIELD_DAMAGE_TAKEN, "Explosive damage taken (Shield)"));
        map.put(FRAG_ARMOR_DAMAGE_TAKEN,
                new FragmentationArmorDamageTaken(FRAG_ARMOR_DAMAGE_TAKEN, "Fragmentation damage taken (Armor/Hull)"));
        map.put(FRAG_SHIELD_DAMAGE_TAKEN,
                new FragmentationShieldDamageTaken(FRAG_SHIELD_DAMAGE_TAKEN, "Fragmentation damage taken (Shield)"));
        map.put(KINETIC_ARMOR_DAMAGE_TAKEN, new KineticArmorDamageTaken(KINETIC_ARMOR_DAMAGE_TAKEN, "Kinetic damage taken (Armor/Hull)"));
        map.put(KINETIC_SHIELD_DAMAGE_TAKEN, new KineticShieldDamageTaken(KINETIC_SHIELD_DAMAGE_TAKEN, "Kinetic damage taken (Shield)"));

        map.put(AUTOFIRE_ACCURACY, new AutofireAccuracy(AUTOFIRE_ACCURACY, "Autofire accuracy"));
        map.put(SENSOR_PROFILE, new SensorProfile(SENSOR_PROFILE, "Sensor profile"));
        map.put(SENSOR_STRENGTH, new SensorStrength(SENSOR_STRENGTH, "Sensor strength"));
        map.put(SIGHT_RADIUS, new SightRadius(SIGHT_RADIUS, "Sight radius"));

        map.put(SHIELD_ABSORPTION, new ShieldAbsorbtion(SHIELD_ABSORPTION, "Shield damage taken"));
        map.put(SHIELD_ARC, new ShieldArc(SHIELD_ARC, "Shield arc"));
        map.put(SHIELD_TURN, new ShieldTurnRate(SHIELD_TURN, "Shield turn rate"));
        map.put(SHIELD_UNFOLD, new ShieldUnfold(SHIELD_UNFOLD, "Shield unfold rate"));
        map.put(SHIELD_UPKEEP, new ShieldUpkeep(SHIELD_UPKEEP, "Shield upkeep per second"));

        map.put(MOD_SLOT, new SModSlot(MOD_SLOT, "Number of SMod-Slots"));
        map.put(TIME_MULT, new TimeMult(TIME_MULT, "Time-Flow"));

        map.put(SYSTEM_COOLDOWN, new SystemCooldown(SYSTEM_COOLDOWN, "System cooldown"));
        map.put(SYSTEM_REGEN, new SystemRegen(SYSTEM_REGEN, "System charge regeneration"));
        map.put(SYSTEM_USES, new SystemUses(SYSTEM_USES, "System charge amount"));

        map.put(BALLISTIC_RANGE, new BallisticRange(BALLISTIC_RANGE, "Ballistic weapon range"));
        map.put(BALLISTIC_ROF, new BallisticROF(BALLISTIC_ROF, "Ballistic rate of fire"));
        map.put(ENERGY_RANGE, new EnergyRange(ENERGY_RANGE, "Energy weapon range"));
        map.put(ENERGY_ROF, new EnergyROF(ENERGY_ROF, "Energy rate of fire"));
        map.put(MISSILE_ECCM, new MissileECCM(MISSILE_ECCM, "Missile ECCM chance"));
        map.put(MISSILE_GUIDANCE, new MissileGuidance(MISSILE_GUIDANCE, "Missile tracking"));
        map.put(MISSILE_HEALTH, new MissileHealth(MISSILE_HEALTH, "Missile health"));
        map.put(MISSILE_ROF, new MissileROF(MISSILE_ROF, "Missile rate of fire"));
        map.put(WEAPON_RECOIL, new WeaponRecoil(WEAPON_RECOIL, "Weapon recoil"));
        map.put(WEAPON_TURN, new WeaponTurnRate(WEAPON_TURN, "Weapon turn rate"));

        return map;
    }
}
