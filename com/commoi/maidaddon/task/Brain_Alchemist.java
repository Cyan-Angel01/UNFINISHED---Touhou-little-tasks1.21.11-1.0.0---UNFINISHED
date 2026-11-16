package com.commoi.maidaddon.task;

import cn.sh1rocu.touhoulittlemaid.util.itemhandler.ItemStackHandler;
import com.commoi.maidaddon.config.MaidAddonConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1310;
import net.minecraft.class_1588;
import net.minecraft.class_1657;
import net.minecraft.class_1686;
import net.minecraft.class_1799;
import net.minecraft.class_1803;
import net.minecraft.class_1828;
import net.minecraft.class_1842;
import net.minecraft.class_1844;
import net.minecraft.class_1847;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3486;
import net.minecraft.class_4097;

public class Brain_Alchemist extends class_4097<class_1309> {
   private int cooldownTimer = 0;
   private int aimTicks = 0;
   private class_1309 cachedTarget = null;
   private FindResult cachedResult = null;
   private static final double THROW_RANGE_SQ = (double)225.0F;
   private static final int DARKNESS_THRESHOLD = 7;
   private static final double MIN_THROW_DIST_SQ = (double)9.0F;
   private static final Set<class_1842> FIRE_POTIONS = new HashSet();
   private static final Set<class_1842> HEAL_POTIONS = new HashSet();
   private static final Set<class_1842> NIGHT_VISION_POTIONS = new HashSet();
   private static final Set<class_1842> SWIFTNESS_POTIONS = new HashSet();
   private static final Set<class_1842> LEAPING_POTIONS = new HashSet();
   private static final Set<class_1842> SLOW_FALLING_POTIONS = new HashSet();
   private static final Set<class_1842> WATER_BREATHING_POTIONS = new HashSet();
   private static final Set<class_1842> STRENGTH_POTIONS = new HashSet();
   private static final Set<class_1842> TURTLE_MASTER_POTIONS = new HashSet();
   private static final Set<class_1842> INVISIBILITY_POTIONS = new HashSet();
   private static final Set<class_1842> LUCK_POTIONS = new HashSet();
   private static final Set<class_1842> HARM_POTIONS = new HashSet();
   private static final Set<class_1842> POISON_POTIONS = new HashSet();
   private static final Set<class_1842> WEAKNESS_POTIONS = new HashSet();
   private static final Set<class_1842> SLOWNESS_POTIONS = new HashSet();
   private static final Set<class_1842> WITHER_POTIONS = new HashSet();

   public Brain_Alchemist() {
      super(ImmutableMap.of());
   }

   private float getHealThreshold() {
      return ((Double)MaidAddonConfig.ALCHEMIST_HEAL_THRESHOLD.get()).floatValue();
   }

   private List<class_1309> getTargets(EntityMaid maid, class_3218 world) {
      List<class_1309> targets = new ArrayList();
      class_1309 owner = maid.method_35057();
      if (owner != null && owner.method_5805() && maid.method_5858(owner) <= (double)225.0F) {
         targets.add(owner);
      }

      if ((Boolean)MaidAddonConfig.ALCHEMIST_POTIONS_SELF.get()) {
         targets.add(maid);
      }

      if ((Boolean)MaidAddonConfig.ALCHEMIST_THROW_NEGATIVE.get()) {
         List<class_1588> enemies = world.method_8390(class_1588.class, maid.method_5829().method_1014((double)10.0F), (e) -> e.method_5805() && !e.method_5722(maid));
         targets.addAll(enemies);
      }

      return targets;
   }

   protected boolean method_18919(class_3218 world, class_1309 entity) {
      if (entity instanceof EntityMaid maid) {
         if (maid.method_24345()) {
            return false;
         } else if (this.cooldownTimer > 0) {
            --this.cooldownTimer;
            return false;
         } else {
            List<class_1309> targets = this.getTargets(maid, world);
            if (targets.isEmpty()) {
               return false;
            } else {
               for(class_1309 target : targets) {
                  FindResult res = this.determinePotionForTarget(world, maid, target);
                  if (res != null && res.found) {
                     this.cachedTarget = target;
                     this.cachedResult = res;
                     this.aimTicks = 0;
                     return true;
                  }
               }

               this.cooldownTimer = 20;
               return false;
            }
         }
      } else {
         return false;
      }
   }

   protected boolean method_18927(class_3218 world, class_1309 entity, long time) {
      return this.cachedTarget != null && this.cachedTarget.method_5805() && this.cachedResult != null;
   }

   protected void method_18920(class_3218 world, class_1309 entity, long time) {
   }

   protected void method_18924(class_3218 world, class_1309 entity, long time) {
      if (entity instanceof EntityMaid maid) {
         if (this.cachedTarget != null && this.cachedResult != null) {
            if (this.cachedTarget instanceof class_1588) {
               class_1309 owner = maid.method_35057();
               double friendlyFireRange = (Double)MaidAddonConfig.ALCHEMIST_FRIENDLY_FIRE_RANGE.get();
               if (owner != null && owner.method_5858(this.cachedTarget) < friendlyFireRange * friendlyFireRange) {
                  this.finish(world, maid, time);
                  return;
               }

               if (maid.method_5858(this.cachedTarget) < (double)9.0F) {
                  class_243 awayVec = maid.method_19538().method_1020(this.cachedTarget.method_19538()).method_1029().method_1021((double)3.0F);
                  maid.method_5942().method_6337(maid.method_23317() + awayVec.field_1352, maid.method_23318(), maid.method_23321() + awayVec.field_1350, 1.2);
                  return;
               }
            }

            maid.method_5988().method_6226(this.cachedTarget, 30.0F, 30.0F);
            int delayConfig = (Integer)MaidAddonConfig.ALCHEMIST_THROW_DELAY.get();
            if (this.aimTicks < delayConfig) {
               ++this.aimTicks;
            } else {
               if (!this.cachedResult.isMainHand) {
                  this.swapToMainHand(maid, this.cachedResult);
               }

               class_1799 handStack = maid.method_6047();
               if (!handStack.method_7960()) {
                  this.performThrow(world, maid, this.cachedTarget, handStack);
                  if (!(Boolean)MaidAddonConfig.ALCHEMIST_INFINITE_POTIONS.get()) {
                     handStack.method_7934(1);
                  }
               }

               this.cooldownTimer = (Integer)MaidAddonConfig.ALCHEMIST_THROW_COOLDOWN.get();
               this.cachedTarget = null;
               this.cachedResult = null;
               this.aimTicks = 0;
            }
         }
      }
   }

   protected void finish(class_3218 world, class_1309 entity, long time) {
      this.cachedTarget = null;
      this.cachedResult = null;
      this.aimTicks = 0;
   }

   private void swapToMainHand(EntityMaid maid, FindResult result) {
      class_1799 currentMain = maid.method_6047();
      class_1799 potionItem = result.stack;
      if (result.isOffHand) {
         maid.method_5673(class_1304.field_6173, potionItem);
         maid.method_5673(class_1304.field_6171, currentMain);
      } else if (result.slot != -1) {
         maid.method_5673(class_1304.field_6173, potionItem);
         maid.getMaidInv().setStackInSlot(result.slot, currentMain);
      }

   }

   private FindResult determinePotionForTarget(class_3218 world, EntityMaid maid, class_1309 target) {
      boolean isEnemy = target instanceof class_1588;
      return isEnemy ? this.determineEnemyPotions(maid, target) : this.determineFriendlyPotions(world, maid, target);
   }

   private FindResult determineFriendlyPotions(class_3218 world, EntityMaid maid, class_1309 target) {
      if (!(Boolean)MaidAddonConfig.ALCHEMIST_THROW_POSITIVE.get()) {
         return null;
      } else {
         if (target.method_6032() < target.method_6063() * this.getHealThreshold()) {
            FindResult res = this.findPotion(maid, HEAL_POTIONS);
            if (res.found) {
               return res;
            }
         }

         if (target.method_5809() && !target.method_6059(class_1294.field_5918)) {
            FindResult res = this.findPotion(maid, FIRE_POTIONS);
            if (res.found) {
               return res;
            }
         }

         boolean inCombat = target.method_6052() != null || target.method_6065() != null;
         if (inCombat) {
            if (!target.method_6059(class_1294.field_5907)) {
               FindResult res = this.findPotion(maid, TURTLE_MASTER_POTIONS);
               if (res.found) {
                  return res;
               }
            }

            if (!target.method_6059(class_1294.field_5910)) {
               FindResult res = this.findPotion(maid, STRENGTH_POTIONS);
               if (res.found) {
                  return res;
               }
            }
         }

         if (target.field_6017 > 3.0F && !target.method_6059(class_1294.field_5906)) {
            FindResult res = this.findPotion(maid, SLOW_FALLING_POTIONS);
            if (res.found) {
               return res;
            }
         }

         if (target.method_5777(class_3486.field_15517) && !target.method_6059(class_1294.field_5923)) {
            FindResult res = this.findPotion(maid, WATER_BREATHING_POTIONS);
            if (res.found) {
               return res;
            }
         }

         if (world.method_22339(target.method_24515()) < 7 && !target.method_6059(class_1294.field_5925)) {
            FindResult res = this.findPotion(maid, NIGHT_VISION_POTIONS);
            if (res.found) {
               return res;
            }
         }

         if (!target.method_6059(class_1294.field_5904)) {
            FindResult res = this.findPotion(maid, SWIFTNESS_POTIONS);
            if (res.found) {
               return res;
            }
         }

         return null;
      }
   }

   private FindResult determineEnemyPotions(EntityMaid maid, class_1309 target) {
      if (!(Boolean)MaidAddonConfig.ALCHEMIST_THROW_NEGATIVE.get()) {
         return null;
      } else {
         class_1310 group = target.method_6046();
         if (group == class_1310.field_6289) {
            if ((Boolean)MaidAddonConfig.ALCHEMIST_HEAL_UNDEAD.get()) {
               FindResult res = this.findPotion(maid, HEAL_POTIONS);
               if (res.found) {
                  return res;
               }
            }
         } else if (group == class_1310.field_6293) {
            FindResult harmRes = this.findPotion(maid, HARM_POTIONS);
            if (harmRes.found) {
               return harmRes;
            }
         } else {
            FindResult defaultHarmRes = this.findPotion(maid, HARM_POTIONS);
            if (defaultHarmRes.found) {
               return defaultHarmRes;
            }

            if (!target.method_6059(class_1294.field_5899)) {
               FindResult res = this.findPotion(maid, POISON_POTIONS);
               if (res.found) {
                  return res;
               }
            }
         }

         if (group != class_1310.field_6289 && !target.method_6059(class_1294.field_5920)) {
            FindResult res = this.findPotion(maid, WITHER_POTIONS);
            if (res.found) {
               return res;
            }
         }

         if (!target.method_6059(class_1294.field_5911)) {
            FindResult res = this.findPotion(maid, WEAKNESS_POTIONS);
            if (res.found) {
               return res;
            }
         }

         if (!target.method_6059(class_1294.field_5909)) {
            FindResult res = this.findPotion(maid, SLOWNESS_POTIONS);
            if (res.found) {
               return res;
            }
         }

         return null;
      }
   }

   private void performThrow(class_3218 world, EntityMaid maid, class_1309 target, class_1799 stack) {
      class_243 spawnPos = maid.method_33571().method_1019(maid.method_5720().method_1021(0.3));
      class_1686 potionEntity = new class_1686(world, maid);
      potionEntity.method_16940(stack);
      potionEntity.method_5814(spawnPos.field_1352, spawnPos.field_1351, spawnPos.field_1350);
      double dx = target.method_23317() - maid.method_23317();
      double dy = target.method_23323(0.33) - spawnPos.field_1351;
      double dz = target.method_23321() - maid.method_23321();
      double dist = Math.sqrt(dx * dx + dz * dz);
      float velocity = 0.75F;
      double arcFactor = dist * 0.15;
      potionEntity.method_7485(dx, dy + arcFactor, dz, velocity, 4.0F);
      world.method_8649(potionEntity);
      world.method_43128((class_1657)null, maid.method_23317(), maid.method_23318(), maid.method_23321(), class_3417.field_14910, class_3419.field_15248, 0.5F, 0.4F / (world.method_8409().method_43057() * 0.4F + 0.8F));
      maid.method_6104(class_1268.field_5808);
   }

   private FindResult findPotion(EntityMaid maid, Set<class_1842> validPotions) {
      if (this.isValidStack(maid.method_6047(), validPotions)) {
         return new FindResult(true, maid.method_6047(), -1, true, false);
      } else if (this.isValidStack(maid.method_6079(), validPotions)) {
         return new FindResult(true, maid.method_6079(), -1, false, true);
      } else {
         if ((Boolean)MaidAddonConfig.ALCHEMIST_USE_BACKPACK.get()) {
            ItemStackHandler inv = maid.getMaidInv();

            for(int i = 0; i < inv.getSlots(); ++i) {
               class_1799 stack = inv.getStackInSlot(i);
               if (this.isValidStack(stack, validPotions)) {
                  return new FindResult(true, stack, i, false, false);
               }
            }
         }

         return new FindResult(false, class_1799.field_8037, -1, false, false);
      }
   }

   private boolean isValidStack(class_1799 stack, Set<class_1842> validPotions) {
      if (stack.method_7960()) {
         return false;
      } else if (!(stack.method_7909() instanceof class_1828) && !(stack.method_7909() instanceof class_1803)) {
         return false;
      } else {
         class_1842 potion = class_1844.method_8063(stack);
         return validPotions.contains(potion);
      }
   }

   static {
      FIRE_POTIONS.add(class_1847.field_8987);
      FIRE_POTIONS.add(class_1847.field_8969);
      HEAL_POTIONS.add(class_1847.field_8963);
      HEAL_POTIONS.add(class_1847.field_8980);
      HEAL_POTIONS.add(class_1847.field_8986);
      HEAL_POTIONS.add(class_1847.field_8992);
      HEAL_POTIONS.add(class_1847.field_9003);
      SLOW_FALLING_POTIONS.add(class_1847.field_8974);
      SLOW_FALLING_POTIONS.add(class_1847.field_8964);
      WATER_BREATHING_POTIONS.add(class_1847.field_8994);
      WATER_BREATHING_POTIONS.add(class_1847.field_9001);
      STRENGTH_POTIONS.add(class_1847.field_8978);
      STRENGTH_POTIONS.add(class_1847.field_8965);
      STRENGTH_POTIONS.add(class_1847.field_8993);
      TURTLE_MASTER_POTIONS.add(class_1847.field_8990);
      TURTLE_MASTER_POTIONS.add(class_1847.field_8988);
      TURTLE_MASTER_POTIONS.add(class_1847.field_8977);
      NIGHT_VISION_POTIONS.add(class_1847.field_8968);
      NIGHT_VISION_POTIONS.add(class_1847.field_8981);
      SWIFTNESS_POTIONS.add(class_1847.field_9005);
      SWIFTNESS_POTIONS.add(class_1847.field_8983);
      SWIFTNESS_POTIONS.add(class_1847.field_8966);
      LEAPING_POTIONS.add(class_1847.field_8979);
      LEAPING_POTIONS.add(class_1847.field_8971);
      LEAPING_POTIONS.add(class_1847.field_8998);
      INVISIBILITY_POTIONS.add(class_1847.field_8997);
      INVISIBILITY_POTIONS.add(class_1847.field_9000);
      LUCK_POTIONS.add(class_1847.field_8995);
      HARM_POTIONS.add(class_1847.field_9004);
      HARM_POTIONS.add(class_1847.field_8973);
      POISON_POTIONS.add(class_1847.field_8982);
      POISON_POTIONS.add(class_1847.field_9002);
      POISON_POTIONS.add(class_1847.field_8972);
      WEAKNESS_POTIONS.add(class_1847.field_8975);
      WEAKNESS_POTIONS.add(class_1847.field_8970);
      SLOWNESS_POTIONS.add(class_1847.field_8996);
      SLOWNESS_POTIONS.add(class_1847.field_8989);
      SLOWNESS_POTIONS.add(class_1847.field_8976);
   }

   private static class FindResult {
      boolean found;
      class_1799 stack;
      int slot;
      boolean isMainHand;
      boolean isOffHand;

      public FindResult(boolean found, class_1799 stack, int slot, boolean isMainHand, boolean isOffHand) {
         this.found = found;
         this.stack = stack;
         this.slot = slot;
         this.isMainHand = isMainHand;
         this.isOffHand = isOffHand;
      }
   }
}
