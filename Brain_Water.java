package com.commoi.maidaddon.task;

import com.commoi.maidaddon.config.MaidAddonConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import java.util.Map;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4097;
import net.minecraft.class_4140;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class Brain_Water extends class_4097<class_1309> {
   private int cleanupTimer = 0;
   private boolean waterPlaced = false;
   private boolean isFinished = false;
   private class_2338 placementPos = null;

   public Brain_Water() {
      super(Map.of());
   }

   protected boolean method_18919(class_3218 world, class_1309 entity) {
      if (entity instanceof EntityMaid maid) {
         boolean hasWater = maid.method_6047().method_31574(class_1802.field_8705);
         boolean hasEmpty = maid.method_6047().method_31574(class_1802.field_8550);
         if (!hasWater && !hasEmpty) {
            return false;
         } else {
            return this.getTarget(maid) != null;
         }
      } else {
         return false;
      }
   }

   protected void method_18920(class_3218 world, class_1309 entity, long time) {
      this.cleanupTimer = 0;
      this.waterPlaced = false;
      this.isFinished = false;
      this.placementPos = null;
      if (entity instanceof EntityMaid maid) {
         maid.method_5942().method_6340();
         if (maid.method_6047().method_31574(class_1802.field_8550)) {
            maid.method_6122(class_1268.field_5808, new class_1799(class_1802.field_8705));
         }

         this.checkAndPlace(world, maid);
      }

   }

   protected void method_18924(class_3218 world, class_1309 entity, long time) {
      if (entity instanceof EntityMaid maid) {
         maid.method_18868().method_18875(class_4140.field_18451);
         maid.method_18868().method_18875(class_4140.field_18452);
         maid.method_18868().method_18875(class_4140.field_18453);
         if (!this.waterPlaced) {
            this.checkAndPlace(world, maid);
            if (this.getTarget(maid) == null) {
               this.isFinished = true;
            }
         } else {
            class_1309 target = this.getTarget(maid);
            if (target != null) {
               try {
                  this.cleanupTimer = (Integer)MaidAddonConfig.WATER_RETRIEVE_DELAY.get();
               } catch (Exception var8) {
                  this.cleanupTimer = 20;
               }
            } else if (this.cleanupTimer > 0) {
               --this.cleanupTimer;
            } else {
               if (this.placementPos != null) {
                  class_2680 state = world.method_8320(this.placementPos);
                  if (state.method_27852(class_2246.field_10382)) {
                     world.method_8501(this.placementPos, class_2246.field_10124.method_9564());
                  }
               }

               maid.method_6122(class_1268.field_5808, new class_1799(class_1802.field_8705));
               this.isFinished = true;
            }
         }

      }
   }

   private class_1309 getTarget(EntityMaid maid) {
      double minFall = (Double)MaidAddonConfig.WATER_MIN_FALL_DISTANCE.get();
      class_1309 owner = maid.method_35057();
      if (owner == null || !((double)owner.field_6017 > minFall) && !(owner.method_18798().field_1351 < -0.6)) {
         if (owner != null && owner.method_5809()) {
            return owner;
         } else if (!((double)maid.field_6017 > minFall) && !(maid.method_18798().field_1351 < -0.6)) {
            return maid.method_5809() ? maid : null;
         } else {
            return maid;
         }
      } else {
         return owner;
      }
   }

   private void checkAndPlace(class_3218 world, EntityMaid maid) {
      class_1309 target = this.getTarget(maid);
      if (target != null) {
         double minFall = (Double)MaidAddonConfig.WATER_MIN_FALL_DISTANCE.get();
         boolean isMLG = (double)target.field_6017 > minFall || target.method_18798().field_1351 < -0.6;
         if (maid.method_5858(target) > (double)100.0F) {
            maid.method_5942().method_6335(target, 2.3);
            if (isMLG) {
               return;
            }
         }

         class_2338 targetPos;
         if (isMLG) {
            class_243 start = target.method_5829().method_1005();
            class_243 velocity = target.method_18798();
            class_243 predictionVec = velocity.method_1021((double)6.0F);
            if (predictionVec.method_1027() < 0.1) {
               predictionVec = new class_243((double)0.0F, (double)-10.0F, (double)0.0F);
            } else if (predictionVec.field_1351 > (double)-10.0F) {
               predictionVec = predictionVec.method_1031((double)0.0F, (double)-10.0F, (double)0.0F);
            }

            class_243 end = start.method_1019(predictionVec);
            class_3965 result = world.method_17742(new class_3959(start, end, class_3960.field_17558, class_242.field_1348, target));
            if (result.method_17783() == class_240.field_1333) {
               return;
            }

            double distToGround = start.method_1022(result.method_17784());
            double currentSpeed = Math.abs(velocity.field_1351);
            double safety = (double)(Integer)MaidAddonConfig.WATER_LOOKAHEAD.get();
            if (!(distToGround <= currentSpeed + safety)) {
               return;
            }

            targetPos = result.method_17777().method_10084();
         } else {
            class_243 vel = target.method_18798();
            if (vel.method_37268() > 0.02) {
               class_243 futurePos = target.method_19538().method_1019(vel.method_1021((double)1.5F));
               targetPos = new class_2338((int)futurePos.field_1352, (int)target.method_23318(), (int)futurePos.field_1350);
            } else {
               targetPos = target.method_24515();
            }
         }

         if (maid.method_5649((double)targetPos.method_10263(), (double)targetPos.method_10264(), (double)targetPos.method_10260()) > (double)100.0F) {
            maid.method_5942().method_6337((double)targetPos.method_10263(), (double)targetPos.method_10264(), (double)targetPos.method_10260(), 2.3);
         } else {
            class_2680 state = world.method_8320(targetPos);
            if (state.method_27852(class_2246.field_10382)) {
               this.waterPlaced = true;
               this.placementPos = targetPos;
            } else {
               if (state.method_45474() || state.method_26220(world, targetPos).method_1110()) {
                  maid.method_5942().method_6340();
                  if (!state.method_26215()) {
                     world.method_22352(targetPos, true);
                  }

                  world.method_8501(targetPos, class_2246.field_10382.method_9564());
                  maid.method_6122(class_1268.field_5808, new class_1799(class_1802.field_8705));
                  this.waterPlaced = true;
                  this.placementPos = targetPos;

                  try {
                     this.cleanupTimer = (Integer)MaidAddonConfig.WATER_RETRIEVE_DELAY.get();
                  } catch (Exception var19) {
                     this.cleanupTimer = 20;
                  }
               }

            }
         }
      }
   }

   protected boolean method_18927(class_3218 world, class_1309 entity, long time) {
      if (this.isFinished) {
         return false;
      } else {
         return this.waterPlaced || this.method_18919(world, entity);
      }
   }

   protected void finish(class_1309 entity) {
      if (entity instanceof EntityMaid maid) {
         maid.method_5942().method_6340();
      }

   }
}
