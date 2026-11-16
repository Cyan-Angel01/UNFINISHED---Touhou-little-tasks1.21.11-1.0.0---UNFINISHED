package com.commoi.maidaddon.task;

import com.commoi.maidaddon.config.MaidAddonConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1810;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2586;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3419;
import net.minecraft.class_3481;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_4097;
import net.minecraft.class_7923;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class Brain_Mining extends class_4097<class_1309> {
   private class_2338 targetOre = null;
   private class_2338 lastTarget = null;
   private class_2338 currentObstruction = null;
   private int scanCooldown = 0;
   private float breakProgress = 0.0F;
   private boolean swingToggle = false;
   private int veinSize = 1;
   private static final double MAX_REACH_SQ = (double)16.0F;

   public Brain_Mining() {
      super(Map.of());
   }

   protected boolean method_18919(class_3218 world, class_1309 entity) {
      if (entity instanceof EntityMaid maid) {
         class_1799 stack = maid.method_6047();
         if (!maid.method_24345() && stack.method_7909() instanceof class_1810) {
            if (!(Boolean)MaidAddonConfig.MINING_ALLOW_TOOL_BREAK.get() && stack.method_7936() - stack.method_7919() <= 1) {
               return false;
            } else if (this.targetOre != null) {
               return true;
            } else if (this.scanCooldown-- > 0) {
               return false;
            } else {
               this.scanCooldown = 20;
               int range = (Integer)MaidAddonConfig.MINING_SCAN_RANGE.get();
               this.findNearestOre(world, maid, range).ifPresent((pos) -> this.targetOre = pos);
               return this.targetOre != null;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void method_18920(class_3218 world, class_1309 entity, long time) {
      if (this.targetOre != null && !this.targetOre.equals(this.lastTarget)) {
         this.veinSize = this.countVein(world, this.targetOre);
         this.breakProgress = 0.0F;
         this.currentObstruction = null;
         this.scanCooldown = 0;
         this.lastTarget = this.targetOre;
      }

   }

   protected void method_18924(class_3218 world, class_1309 entity, long time) {
      if (entity instanceof EntityMaid maid) {
         if (this.targetOre == null) {
            this.finish(world, maid, time);
         } else {
            int mineRange = (Integer)MaidAddonConfig.MINING_SCAN_RANGE.get();
            double searchRangeSq = (double)(mineRange * mineRange);
            if (maid.method_5707(class_243.method_24953(this.targetOre)) > searchRangeSq + (double)64.0F) {
               this.targetOre = null;
            } else if (world.method_8320(this.targetOre).method_26215()) {
               this.targetOre = null;
            } else {
               this.currentObstruction = this.findObstruction(world, maid, this.targetOre);
               if (this.currentObstruction != null) {
                  this.processBreaking(world, maid, this.currentObstruction, false);
               } else {
                  this.processBreaking(world, maid, this.targetOre, true);
               }

            }
         }
      }
   }

   protected boolean method_18927(class_3218 world, class_1309 entity, long time) {
      if (!(entity instanceof EntityMaid maid)) {
         return false;
      } else {
         return (this.method_18919(world, entity) || this.targetOre != null) && !maid.method_24345();
      }
   }

   protected void finish(class_3218 world, class_1309 entity, long time) {
      if (entity instanceof EntityMaid maid) {
         this.resetNavigation(maid, world);
      }

   }

   private void processBreaking(class_3218 world, EntityMaid maid, class_2338 pos, boolean isVein) {
      class_1799 stack = maid.method_6047();
      if (!(Boolean)MaidAddonConfig.MINING_ALLOW_TOOL_BREAK.get() && stack.method_7936() - stack.method_7919() <= 1) {
         this.finish(world, maid, 0L);
      } else if (maid.method_5707(class_243.method_24953(pos)) > (double)16.0F) {
         maid.method_5942().method_6337((double)pos.method_10263() + (double)0.5F, (double)pos.method_10264(), (double)pos.method_10260() + (double)0.5F, 0.6);
      } else {
         maid.method_5942().method_6340();
         maid.method_5988().method_20248((double)pos.method_10263() + (double)0.5F, (double)pos.method_10264() + (double)0.5F, (double)pos.method_10260() + (double)0.5F);
         if (!maid.field_6252 && !this.swingToggle) {
            maid.method_6104(class_1268.field_5808);
            this.swingToggle = true;
         } else if (!maid.field_6252) {
            this.swingToggle = false;
         }

         float breakDelta = this.calculateBreakDelta(maid, world, world.method_8320(pos));
         this.breakProgress += breakDelta / (isVein ? (float)Math.max(1, this.veinSize) : 1.0F);
         world.method_8517(maid.method_5628(), pos, (int)(this.breakProgress * 10.0F));
         if (this.breakProgress >= 1.0F) {
            this.breakProgress = 0.0F;
            if (isVein) {
               this.breakVein(world, maid, pos);
               this.resetNavigation(maid, world);
            } else {
               world.method_8651(pos, true, maid);
               stack.method_7956(1, maid, (e) -> e.method_20235(class_1304.field_6173));
               if (this.currentObstruction != null) {
                  world.method_8517(maid.method_5628(), this.currentObstruction, -1);
                  this.currentObstruction = null;
               }
            }
         }

      }
   }

   private void breakVein(class_3218 world, EntityMaid maid, class_2338 startPos) {
      List<class_2338> toBreak = this.scanVein(world, startPos);
      class_1799 tool = maid.method_6047();
      int unbreaking = class_1890.method_8225(class_1893.field_9119, tool);

      for(class_2338 pos : toBreak) {
         if (!(Boolean)MaidAddonConfig.MINING_ALLOW_TOOL_BREAK.get() && tool.method_7936() - tool.method_7919() <= 1 || tool.method_7960()) {
            break;
         }

         class_2680 state = world.method_8320(pos);

         for(class_1799 drop : class_2248.method_9609(state, world, pos, (class_2586)null, maid, tool)) {
            class_1542 itemEntity = new class_1542(world, (double)pos.method_10263() + (double)0.5F, (double)pos.method_10264() + (double)0.5F, (double)pos.method_10260() + (double)0.5F, drop);
            itemEntity.method_6988();
            world.method_8649(itemEntity);
         }

         world.method_8652(pos, class_2246.field_10124.method_9564(), 3);
         world.method_8396((class_1657)null, pos, state.method_26231().method_10595(), class_3419.field_15245, 1.0F, 1.0F);
         if (world.field_9229.method_43048(unbreaking + 1) == 0) {
            tool.method_7956(1, maid, (e) -> e.method_20235(class_1304.field_6173));
         }
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

         if (maid.method_6059(class_1294.field_5917)) {
            speed *= 1.0F + (float)(maid.method_6112(class_1294.field_5917).method_5578() + 1) * 0.2F;
         }

         return speed / hardness / (stack.method_7951(state) ? 30.0F : 100.0F);
      }
   }

   private class_2338 findObstruction(class_3218 world, EntityMaid maid, class_2338 target) {
      class_3965 result = world.method_17742(new class_3959(maid.method_33571(), class_243.method_24953(target), class_3960.field_17558, class_242.field_1348, maid));
      return result.method_17783() == class_240.field_1332 && !result.method_17777().equals(target) ? result.method_17777() : null;
   }

   private void resetNavigation(EntityMaid maid, class_3218 world) {
      if (this.targetOre != null) {
         world.method_8517(maid.method_5628(), this.targetOre, -1);
      }

      if (this.currentObstruction != null) {
         world.method_8517(maid.method_5628(), this.currentObstruction, -1);
      }

      this.targetOre = null;
      this.currentObstruction = null;
      this.breakProgress = 0.0F;
      this.swingToggle = false;
      this.veinSize = 1;
      this.lastTarget = null;
      maid.method_5942().method_6340();
   }

   private int countVein(class_3218 world, class_2338 startPos) {
      return this.scanVein(world, startPos).size();
   }

   private List<class_2338> scanVein(class_3218 world, class_2338 startPos) {
      class_2680 startState = world.method_8320(startPos);
      Queue<class_2338> queue = new LinkedList();
      Set<class_2338> visited = new HashSet();
      List<class_2338> result = new ArrayList();
      queue.add(startPos);
      visited.add(startPos);
      int limit = (Integer)MaidAddonConfig.MINING_MAX_VEIN_SIZE.get();

      while(!queue.isEmpty() && result.size() < limit) {
         class_2338 current = (class_2338)queue.poll();
         result.add(current);

         for(int x = -1; x <= 1; ++x) {
            for(int y = -1; y <= 1; ++y) {
               for(int z = -1; z <= 1; ++z) {
                  if (x != 0 || y != 0 || z != 0) {
                     class_2338 neighbor = current.method_10069(x, y, z);
                     if (!visited.contains(neighbor) && this.isSameOreType(world.method_8320(neighbor), startState)) {
                        visited.add(neighbor);
                        queue.add(neighbor);
                     }
                  }
               }
            }
         }
      }

      return result;
   }

   private Optional<class_2338> findNearestOre(class_3218 world, EntityMaid maid, int range) {
      class_2338 center = maid.method_24515();
      List<class_2338> potentialOres = new ArrayList();

      for(class_2338 pos : class_2338.method_10097(center.method_10069(-range, -range, -range), center.method_10069(range, range, range))) {
         if (this.isOre(world.method_8320(pos))) {
            potentialOres.add(pos.method_10062());
         }
      }

      return potentialOres.stream().filter((posx) -> {
         if ((Boolean)MaidAddonConfig.MINING_XRAY_MODE.get()) {
            return true;
         } else {
            class_3965 result = world.method_17742(new class_3959(maid.method_33571(), class_243.method_24953(posx), class_3960.field_17558, class_242.field_1348, maid));
            return result.method_17783() == class_240.field_1332 && result.method_17777().equals(posx);
         }
      }).filter((posx) -> !(Boolean)MaidAddonConfig.MINING_CHECK_TOOL_TIER.get() || maid.method_6047().method_7951(world.method_8320(posx))).min(Comparator.comparingDouble((posx) -> posx.method_10262(center)));
   }

   private boolean isOre(class_2680 state) {
      class_2960 id = class_7923.field_41175.method_10221(state.method_26204());
      if (((List)MaidAddonConfig.MINING_WHITELIST.get()).contains(id.toString())) {
         return true;
      } else if (((List)MaidAddonConfig.MINING_BLACKLIST.get()).contains(id.toString())) {
         return false;
      } else if (!state.method_26164(class_3481.field_25806) && !state.method_26164(class_3481.field_29822) && !state.method_26164(class_3481.field_15475)) {
         return state.method_26164(class_3481.field_29193) || state.method_26164(class_3481.field_28988) || state.method_26164(class_3481.field_29195) || state.method_26164(class_3481.field_23062) || state.method_26164(class_3481.field_28990) || state.method_26164(class_3481.field_29194) || state.method_26164(class_3481.field_28991) || state.method_26164(class_3481.field_28989);
      } else {
         return false;
      }
   }

   private boolean isSameOreType(class_2680 state1, class_2680 state2) {
      if (state1.method_26164(class_3481.field_29193) && state2.method_26164(class_3481.field_29193)) {
         return true;
      } else if (state1.method_26164(class_3481.field_28988) && state2.method_26164(class_3481.field_28988)) {
         return true;
      } else if (state1.method_26164(class_3481.field_29195) && state2.method_26164(class_3481.field_29195)) {
         return true;
      } else if (state1.method_26164(class_3481.field_23062) && state2.method_26164(class_3481.field_23062)) {
         return true;
      } else if (state1.method_26164(class_3481.field_28990) && state2.method_26164(class_3481.field_28990)) {
         return true;
      } else if (state1.method_26164(class_3481.field_29194) && state2.method_26164(class_3481.field_29194)) {
         return true;
      } else if (state1.method_26164(class_3481.field_28991) && state2.method_26164(class_3481.field_28991)) {
         return true;
      } else if (state1.method_26164(class_3481.field_28989) && state2.method_26164(class_3481.field_28989)) {
         return true;
      } else {
         return state1.method_26204() == state2.method_26204();
      }
   }
}
