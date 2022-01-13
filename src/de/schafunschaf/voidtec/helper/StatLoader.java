package de.schafunschaf.voidtec.helper;

import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.BaseStatMod;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.durability.*;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.engine.*;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.flux.*;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.logistic.*;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.projectile.*;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.resistance.*;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.sensor.AutofireAccuracy;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.sensor.SensorProfile;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.sensor.SensorStrength;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.sensor.SightRadius;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.shield.*;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.special.SModSlot;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.system.SystemCooldown;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.system.SystemRegen;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.system.SystemUses;
import de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.weapon.*;

import java.util.HashMap;
import java.util.Map;

import static de.schafunschaf.voidtec.scripts.combat.effects.statmodifiers.VT_StatModKeys.*;

public class StatLoader {
    public static Map<String, BaseStatMod> getAllStatMods() {
        Map<String, BaseStatMod> map = new HashMap<>();

        map.put(ARMOR_HEALTH, new ArmorBonus());
        map.put(ENGINE_HEALTH, new EngineHealth());
        map.put(ENGINE_REPAIR, new EngineRepair());
        map.put(HULL_HEALTH, new HullBonus());
        map.put(WEAPON_HEALTH, new WeaponHealth());
        map.put(WEAPON_REPAIR, new WeaponRepair());

        map.put(SHIP_ACCELERATION, new ShipAcceleration());
        map.put(BURN_LEVEL, new BurnLevel());
        map.put(FUEL_USE, new FuelUse());
        map.put(SHIP_MAX_SPEED, new MaxSpeed());
        map.put(SHIP_TURN, new ShipTurnRate());
        map.put(ZERO_FLUX_SPEED, new ZeroFluxSpeed());

        map.put(FLUX_CAPACITY, new FluxCapacity());
        map.put(FLUX_DISSIPATION, new FluxDissipation());
        map.put(HARD_FLUX_DISSIPATION, new HardFluxDissipation());
        map.put(OVERLOAD_DURATION, new OverloadDuration());
        map.put(VENT_RATE, new VentRate());
        map.put(ZERO_FLUX_LIMIT, new ZeroFluxLimit());

        map.put(CR_DEPLOYMENT, new CRDeployment());
        map.put(CRITICAL_MALFUNCTIONS, new CriticalMalfunction());
        map.put(CR_LOSS, new CRLoss());
        map.put(ENGINE_MALFUNCTIONS, new EngineMalfunction());
        map.put(MAX_CR, new MaxCR());
        map.put(PEAK_CR, new PeakCR());
        map.put(SHIELD_MALFUNCTIONS, new ShieldMalfunction());
        map.put(WEAPON_MALFUNCTIONS, new WeaponMalfunction());

        map.put(BALLISTIC_DAMAGE, new BallisticDamage());
        map.put(BALLISTIC_FLUX, new BallisticFluxCost());
        map.put(BEAM_DAMAGE, new BeamDamage());
        map.put(ENERGY_DAMAGE, new EnergyDamage());
        map.put(ENERGY_FLUX, new EnergyFluxCost());
        map.put(HIT_STRENGTH, new HitStrength());
        map.put(MISSILE_DAMAGE, new MissileDamage());
        map.put(MISSILE_RANGE, new MissileRange());
        map.put(MISSILE_SPEED, new MissileSpeed());
        map.put(PROJECTILE_SPEED, new ProjectileSpeed());
        map.put(SHIELD_DAMAGE, new ShieldDamage());

        map.put(EMP_DAMAGE_TAKEN, new EMPDamageTaken());
        map.put(ENERGY_ARMOR_DAMAGE_TAKEN, new EnergyArmorDamageTaken());
        map.put(ENERGY_SHIELD_DAMAGE_TAKEN, new EnergyShieldDamageTaken());
        map.put(EXPLOSIVE_ARMOR_DAMAGE_TAKEN, new ExplosiveArmorDamageTaken());
        map.put(EXPLOSIVE_SHIELD_DAMAGE_TAKEN, new ExplosiveShieldDamageTaken());
        map.put(FRAG_ARMOR_DAMAGE_TAKEN, new FragmentationArmorDamageTaken());
        map.put(FRAG_SHIELD_DAMAGE_TAKEN, new FragmentationShieldDamageTaken());
        map.put(KINETIC_ARMOR_DAMAGE_TAKEN, new KineticArmorDamageTaken());
        map.put(KINETIC_SHIELD_DAMAGE_TAKEN, new KineticShieldDamageTaken());

        map.put(AUTOFIRE_ACCURACY, new AutofireAccuracy());
        map.put(SENSOR_PROFILE, new SensorProfile());
        map.put(SENSOR_STRENGTH, new SensorStrength());
        map.put(SIGHT_RADIUS, new SightRadius());

        map.put(SHIELD_ABSORPTION, new ShieldAbsorbtion());
        map.put(SHIELD_ARC, new ShieldArc());
        map.put(SHIELD_TURN, new ShieldTurnRate());
        map.put(SHIELD_UNFOLD, new ShieldUnfold());
        map.put(SHIELD_UPKEEP, new ShieldUpkeep());

        map.put(MOD_SLOT, new SModSlot());

        map.put(SYSTEM_COOLDOWN, new SystemCooldown());
        map.put(SYSTEM_REGEN, new SystemRegen());
        map.put(SYSTEM_USES, new SystemUses());

        map.put(BALLISTIC_RANGE, new BallisticRange());
        map.put(BALLISTIC_ROF, new BallisticROF());
        map.put(ENERGY_RANGE, new EnergyRange());
        map.put(ENERGY_ROF, new EnergyROF());
        map.put(MISSILE_ECCM, new MissileECCM());
        map.put(MISSILE_GUIDANCE, new MissileGuidance());
        map.put(MISSILE_HEALTH, new MissileHealth());
        map.put(MISSILE_RELOAD, new MissileReload());
        map.put(WEAPON_RECOIL, new WeaponRecoil());
        map.put(WEAPON_TURN, new WeaponTurnRate());

        return map;
    }
}
