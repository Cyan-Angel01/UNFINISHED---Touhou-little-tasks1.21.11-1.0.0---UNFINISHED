package com.commoi.maidaddon.task;

import com.commoi.maidaddon.config.MaidAddonConfig;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MaidConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_1268;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1743;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3481;
import net.minecraft.class_4097;
import net.minecraft.class_7923;

public class Brain_Logging extends class_4097<class_1309> {
   private State currentState;
   private class_2338 treeBasePos;
   private int scanCooldown;
   private float breakProgress;
   private int keepRunningTicks;
   private static final int VERTICAL_RADIUS = 6;
   private static final double MAX_REACH_SQ = (double)9.0F;
   private static final int TASK_TIMEOUT = 3600;

   public Brain_Logging() {
      super(Map.of());
      this.currentState = Brain_Logging.State.SCANNING;
      this.treeBasePos = null;
      this.scanCooldown = 0;
      this.breakProgress = 0.0F;
      this.keepRunningTicks = 0;
   }

   protected boolean method_18919(class_3218 world, class_1309 entity) {
      if (!(entity instanceof EntityMaid maid)) {
         return false;
      } else {
         class_1799 stack = maid.method_6047();
         if (!maid.method_24345() && stack.method_7909() instanceof class_1743 && ((Boolean)MaidAddonConfig.LOGGING_ALLOW_TOOL_BREAK.get() || stack.method_7936() - stack.method_7919() > 1)) {
            if (this.currentState == Brain_Logging.State.SCANNING && this.scanCooldown-- <= 0) {
               this.findNearestTree(world, maid, (Integer)MaidConfig.MAID_WORK_RANGE.get()).ifPresent((pos) -> {
                  List<class_2338> treeLogs = this.findConnectedLogs(world, pos);
                  if (!treeLogs.isEmpty()) {
                     this.treeBasePos = this.findLowestLog(treeLogs);
                     this.currentState = Brain_Logging.State.MOVING;
                     this.breakProgress = 0.0F;
                     this.keepRunningTicks = 0;
                  }

               });
               this.scanCooldown = 20;
            }

            return this.treeBasePos != null;
         } else {
            return false;
         }
      }
   }

   protected void method_18920(class_3218 world, class_1309 entity, long time) {
   }

   protected void method_18924(class_3218 world, class_1309 entity, long time) {
      if (entity instanceof EntityMaid) {
         EntityMaid maid = (EntityMaid)entity;
         ++this.keepRunningTicks;
         switch (this.currentState) {
            case MOVING:
               if (this.treeBasePos == null || !this.isLog(world.method_8320(this.treeBasePos))) {
                  this.finish(world, maid);
                  return;
               }

               if (maid.method_5707(class_243.method_24953(this.treeBasePos)) <= (double)9.0F) {
                  maid.method_5942().method_6340();
                  this.currentState = Brain_Logging.State.CHOPPING;
               } else {
                  maid.method_5942().method_6337((double)this.treeBasePos.method_10263() + (double)0.5F, (double)this.treeBasePos.method_10264(), (double)this.treeBasePos.method_10260() + (double)0.5F, 0.6);
               }
               break;
            case CHOPPING:
               if (this.treeBasePos == null || !this.isLog(world.method_8320(this.treeBasePos))) {
                  this.finish(world, maid);
                  return;
               }

               maid.method_5942().method_6340();
               this.processBreaking(world, maid, this.treeBasePos);
         }

      }
   }

   protected boolean method_18927(class_3218 world, class_1309 entity, long time) {
      return this.treeBasePos != null && this.keepRunningTicks < 3600;
   }

   protected void finish(class_3218 world, class_1309 entity) {
      if (entity instanceof EntityMaid maid) {
         this.resetNavigation(maid, world, false);
      }

   }

   private void processBreaking(class_3218 world, EntityMaid maid, class_2338 currentTarget) {
      maid.method_5988().method_20248((double)currentTarget.method_10263() + (double)0.5F, (double)currentTarget.method_10264() + (double)0.5F, (double)currentTarget.method_10260() + (double)0.5F);
      maid.method_6104(class_1268.field_5808);
      float progressPerTick = this.calculateBreakDelta(maid, world, world.method_8320(currentTarget));
      List<class_2338> treeLogs = this.findConnectedLogs(world, currentTarget);
      this.breakProgress += progressPerTick / (float)Math.max(1, treeLogs.size());
      world.method_8517(maid.method_5628(), currentTarget, (int)(this.breakProgress * 10.0F));
      if (this.breakProgress >= 1.0F) {
         class_1799 axe = maid.method_6047();
         int unbreakingLevel = class_1890.method_8225(class_1893.field_9119, axe);

         for(class_2338 logPos : treeLogs) {
            if (!(Boolean)MaidAddonConfig.LOGGING_ALLOW_TOOL_BREAK.get() && axe.method_7936() - axe.method_7919() <= 1) {
               break;
            }

            world.method_8651(logPos, true, maid);
            if (world.field_9229.method_43048(unbreakingLevel + 1) == 0) {
               axe.method_7956(1, maid, (e) -> e.method_20235(class_1304.field_6173));
            }

            if (axe.method_7960()) {
               break;
            }
         }

         this.resetNavigation(maid, world, true);
      }

   }

   private float calculateBreakDelta(EntityMaid maid, class_1937 world, class_2680 state) {
      float hardness = state.method_26214(world, maid.method_24515());
      if (hardness == -1.0F) {
         return 0.0F;
      } else {
         class_1799 stack = maid.method_6047();
         float speed = stack.method_7924(state);
         if (speed > 1.0F) {
            int efficiency = class_1890.method_8225(class_1893.field_9131, stack);
            if (efficiency > 0) {
               speed += (float)(efficiency * efficiency + 1);
            }
         }

         return speed / hardness / 30.0F;
      }
   }

   private void tryPlaceSapling(class_3218 world, EntityMaid maid) {
      if ((Boolean)MaidAddonConfig.LOGGING_AUTO_REPLANT.get() && this.treeBasePos != null) {
         for(int i = 0; i < maid.getMaidInv().getSlots(); ++i) {
            class_1799 stack = maid.getMaidInv().getStackInSlot(i);
            class_1792 var6 = stack.method_7909();
            if (var6 instanceof class_1747) {
               class_1747 blockItem = (class_1747)var6;
               class_2680 saplingState = blockItem.method_7711().method_9564();
               if (saplingState.method_26164(class_3481.field_15462) && world.method_8320(this.treeBasePos).method_26215() && saplingState.method_26184(world, this.treeBasePos)) {
                  world.method_8501(this.treeBasePos, saplingState);
                  stack.method_7934(1);
                  maid.method_6104(class_1268.field_5808);
                  return;
               }
            }
         }

      }
   }

   private boolean isLog(class_2680 state) {
      class_2960 id = class_7923.field_41175.method_10221(state.method_26204());
      return ((List)MaidAddonConfig.LOGGING_WHITELIST.get()).contains(id.toString()) || state.method_26164(class_3481.field_15475) && !((List)MaidAddonConfig.LOGGING_BLACKLIST.get()).contains(id.toString());
   }

   private boolean isValidLog(class_3218 world, class_2338 pos, class_2680 state) {
      if (!this.isLog(state)) {
         return false;
      } else if ((Boolean)MaidAddonConfig.LOGGING_CHOP_PLACED.get()) {
         return true;
      } else {
         return ((List)MaidAddonConfig.LOGGING_WHITELIST.get()).contains(class_7923.field_41175.method_10221(state.method_26204()).toString()) || this.hasConnectedLeaves(world, pos);
      }
   }

   private boolean hasConnectedLeaves(class_3218 world, class_2338 logPos) {
      for(int y = 0; y <= 6; ++y) {
         for(int x = -2; x <= 2; ++x) {
            for(int z = -2; z <= 2; ++z) {
               if (world.method_8320(logPos.method_10069(x, y, z)).method_26164(class_3481.field_15503)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private List<class_2338> findConnectedLogs(class_3218 world, class_2338 startPos) {
      List<class_2338> logs = new ArrayList();
      List<class_2338> checkList = new ArrayList();
      checkList.add(startPos);

      while(!checkList.isEmpty() && logs.size() < 50) {
         class_2338 current = (class_2338)checkList.remove(0);
         if (!logs.contains(current) && this.isLog(world.method_8320(current))) {
            logs.add(current);

            for(class_2350 dir : class_2350.values()) {
               checkList.add(current.method_10093(dir));
            }
         }
      }

      return logs;
   }

   private class_2338 findLowestLog(List<class_2338> logs) {
      return (class_2338)logs.stream().min(Comparator.comparingInt(class_2382::method_10264)).orElse((Object)null);
   }

   private Optional<class_2338> findNearestTree(class_3218 world, EntityMaid maid, int range) {
      class_2338 maidPos = maid.method_24515();
      return class_2338.method_25997(maidPos, range, 6, (pos) -> this.isValidLog(world, pos, world.method_8320(pos)) && (world.method_8320(pos.method_10074()).method_26164(class_3481.field_29822) || this.isLog(world.method_8320(pos.method_10074()))));
   }

   private void resetNavigation(EntityMaid maid, class_3218 world, boolean planted) {
      if (planted) {
         this.tryPlaceSapling(world, maid);
      }

      if (this.treeBasePos != null) {
         world.method_8517(maid.method_5628(), this.treeBasePos, -1);
      }

      this.treeBasePos = null;
      this.breakProgress = 0.0F;
      this.keepRunningTicks = 0;
      this.currentState = Brain_Logging.State.SCANNING;
      maid.method_5942().method_6340();
   }

   private static enum State {
      SCANNING,
      MOVING,
      CHOPPING;

      // $FF: synthetic method
      private static State[] $values() {
         return new State[]{SCANNING, MOVING, CHOPPING};
      }
   }
}
