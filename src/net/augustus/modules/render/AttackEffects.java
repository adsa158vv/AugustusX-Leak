package net.augustus.modules.render;

import java.awt.Color;
import net.augustus.events.EventAttackEntity;
import net.augustus.events.EventClickKillAura;
import net.augustus.modules.Categorys;
import net.augustus.modules.Module;
import net.augustus.modules.combat.KillAura;
import net.augustus.settings.DoubleValue;
import net.lenni0451.eventapi.reflection.EventTarget;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MovingObjectPosition;

public class AttackEffects extends Module {
   private final DoubleValue explosion = new DoubleValue(11563, "Explosion", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue waterSplash = new DoubleValue(7515, "WaterSplash", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue waterWake = new DoubleValue(4013, "WaterWake", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue waterDrop = new DoubleValue(173, "WaterDrop", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue suspendedDepth = new DoubleValue(7774, "SuspDepth", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue crit = new DoubleValue(3, "Crit", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue critMagic = new DoubleValue(10727, "CtitMagic", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue smokeNormal = new DoubleValue(2226, "SmokeNormal", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue smokeLarge = new DoubleValue(3592, "SmokeLarge", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue spellInstant = new DoubleValue(2998, "SpellInstant", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue spellMob = new DoubleValue(15526, "SpellMob", this, 1.0, 0.0, 20.0, 0);
   private final DoubleValue spellMobAmbient = new DoubleValue(13034, "SpellMAmbient", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue spellWitch = new DoubleValue(14100, "SpellWitch", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue dripWater = new DoubleValue(2654, "DripWater", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue dripLava = new DoubleValue(4993, "DripLava", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue villagerAngry = new DoubleValue(10128, "VillagerAngry", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue villagerHappy = new DoubleValue(9458, "VillagerHappy", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue note = new DoubleValue(5596, "Note", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue portal = new DoubleValue(7259, "Portal", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue flame = new DoubleValue(5331, "Flame", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue lava = new DoubleValue(14375, "Lava", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue redStone = new DoubleValue(12455, "RedStone", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue snowShovel = new DoubleValue(15467, "SnowShovel", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue slime = new DoubleValue(10979, "Slime", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue heart = new DoubleValue(11573, "Heart", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue barrier = new DoubleValue(14577, "Barrier", this, 0.0, 0.0, 20.0, 0);
   private final DoubleValue fireworkSpark = new DoubleValue(2623, "FireworkSpark", this, 0.0, 0.0, 20.0, 0);

   public AttackEffects() {
      super("AttackEffects", new Color(36, 169, 176), Categorys.RENDER);
   }

   @EventTarget
   public void onEventAttack(EventAttackEntity eventAttackEntity) {
      this.renderEffects();
   }

   @EventTarget
   public void onEventClickKillAura(EventClickKillAura eventClickKillAura) {
      this.renderEffects();
   }

   private void renderEffects() {
      Entity entity = MovingObjectPosition.entityHit;
      if (mm.killAura.isToggled() && mm.killAura.mode.getSelected().equals("Basic") && KillAura.target != null) {
         entity = KillAura.target;
      }

      for(int i = 0; (double)i < this.explosion.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.EXPLOSION_NORMAL);
      }

      for(int i = 0; (double)i < this.waterSplash.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.WATER_SPLASH);
      }

      for(int i = 0; (double)i < this.waterWake.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.WATER_WAKE);
      }

      for(int i = 0; (double)i < this.waterDrop.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.WATER_DROP);
      }

      for(int i = 0; (double)i < this.suspendedDepth.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.SUSPENDED_DEPTH);
      }

      for(int i = 0; (double)i < this.crit.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT);
      }

      for(int i = 0; (double)i < this.critMagic.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.CRIT_MAGIC);
      }

      for(int i = 0; (double)i < this.smokeNormal.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.SMOKE_NORMAL);
      }

      for(int i = 0; (double)i < this.smokeLarge.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.SMOKE_LARGE);
      }

      for(int i = 0; (double)i < this.spellInstant.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.SPELL_INSTANT);
      }

      for(int i = 0; (double)i < this.spellMob.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.SPELL_MOB);
      }

      for(int i = 0; (double)i < this.spellMobAmbient.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.SPELL_MOB_AMBIENT);
      }

      for(int i = 0; (double)i < this.spellWitch.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.SPELL_WITCH);
      }

      for(int i = 0; (double)i < this.dripWater.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.DRIP_WATER);
      }

      for(int i = 0; (double)i < this.dripLava.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.DRIP_LAVA);
      }

      for(int i = 0; (double)i < this.villagerAngry.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.VILLAGER_ANGRY);
      }

      for(int i = 0; (double)i < this.villagerHappy.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.VILLAGER_HAPPY);
      }

      for(int i = 0; (double)i < this.note.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.NOTE);
      }

      for(int i = 0; (double)i < this.portal.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.PORTAL);
      }

      for(int i = 0; (double)i < this.flame.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.FLAME);
      }

      for(int i = 0; (double)i < this.lava.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.LAVA);
      }

      for(int i = 0; (double)i < this.redStone.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.REDSTONE);
      }

      for(int i = 0; (double)i < this.snowShovel.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.SNOW_SHOVEL);
      }

      for(int i = 0; (double)i < this.slime.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.SLIME);
      }

      for(int i = 0; (double)i < this.heart.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.HEART);
      }

      for(int i = 0; (double)i < this.barrier.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.BARRIER);
      }

      for(int i = 0; (double)i < this.fireworkSpark.getValue(); ++i) {
         mc.effectRenderer.emitParticleAtEntity(entity, EnumParticleTypes.FIREWORKS_SPARK);
      }
   }
}
