package com.commoi.maidaddon.task;

import com.commoi.maidaddon.config.MaidAddonConfig;
import com.commoi.maidaddon.task.sort.SortAlgorithm;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_1263;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2281;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_2595;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3708;
import net.minecraft.class_3719;
import net.minecraft.class_4097;
import net.minecraft.class_7923;

public class Brain_Organizing extends class_4097<EntityMaid> {
   private static final Map<class_2338, Long> sortedContainers = new HashMap();
   private class_2338 targetContainer = null;
   private int processTicks = 0;

   public Brain_Organizing() {
      super(ImmutableMap.of());
   }

   protected boolean shouldRun(class_3218 world, EntityMaid maid) {
      if (maid.method_24345()) {
         return false;
      } else {
         sortedContainers.entrySet().removeIf((entry) -> (Long)entry.getValue() < world.method_8510());
         this.targetContainer = this.findNearbyContainer(world, maid);
         return this.targetContainer != null;
      }
   }

   protected void run(class_3218 world, EntityMaid maid, long time) {
      this.processTicks = 0;
   }

   protected void keepRunning(class_3218 world, EntityMaid maid, long time) {
      if (this.targetContainer != null) {
         if (maid.method_5707(this.targetContainer.method_46558()) > (double)4.0F) {
            maid.method_5942().method_6337((double)this.targetContainer.method_10263(), (double)this.targetContainer.method_10264(), (double)this.targetContainer.method_10260(), 0.7);
         } else {
            maid.method_5942().method_6340();
            ++this.processTicks;
            maid.method_5988().method_20248((double)this.targetContainer.method_10263(), (double)this.targetContainer.method_10264(), (double)this.targetContainer.method_10260());
            if (this.processTicks == 1) {
               this.playAnimation(world, this.targetContainer, true);
            }

            if (this.processTicks == (Integer)MaidAddonConfig.ORGANIZING_ANIMATION_TICKS.get()) {
               this.getInventory(world, this.targetContainer).ifPresent(this::sortInventory);
            }

         }
      }
   }

   protected boolean shouldKeepRunning(class_3218 world, EntityMaid maid, long time) {
      int animationTicks = (Integer)MaidAddonConfig.ORGANIZING_ANIMATION_TICKS.get();
      if (this.targetContainer != null && this.processTicks <= animationTicks) {
         return true;
      } else {
         if (this.targetContainer != null) {
            this.playAnimation(world, this.targetContainer, false);
            long cooldownTime = world.method_8510() + (long)(Integer)MaidAddonConfig.ORGANIZING_SORT_COOLDOWN.get();
            sortedContainers.put(this.targetContainer, cooldownTime);
            this.getOtherHalf(world, this.targetContainer).ifPresent((otherPos) -> sortedContainers.put(otherPos, cooldownTime));
         }

         return false;
      }
   }

   private class_2338 findNearbyContainer(class_3218 world, EntityMaid maid) {
      int range = 8;
      class_2338 maidPos = maid.method_24515();
      long currentTime = world.method_8510();
      List<? extends String> blacklist = (List)MaidAddonConfig.ORGANIZING_BLACKLIST.get();

      for(class_2338 pos : class_2338.method_10097(maidPos.method_10069(-range, -range, -range), maidPos.method_10069(range, range, range))) {
         if (currentTime >= (Long)sortedContainers.getOrDefault(pos, 0L)) {
            class_2586 be = world.method_8321(pos);
            if (be != null) {
               class_2960 blockId = class_7923.field_41175.method_10221(be.method_11010().method_26204());
               if (!blacklist.contains(blockId.toString()) && (be instanceof class_2595 || be instanceof class_3719) && !this.isContainerInUse(world, pos)) {
                  return pos.method_10062();
               }
            }
         }
      }

      return null;
   }

   private boolean isContainerInUse(class_1937 world, class_2338 pos) {
      class_2586 be = world.method_8321(pos);
      if (be instanceof class_2595) {
         return class_2595.method_11048(world, pos) > 0;
      } else {
         return be instanceof class_3719 ? (Boolean)be.method_11010().method_11654(class_3708.field_18006) : false;
      }
   }

   private Optional<class_1263> getInventory(class_1937 world, class_2338 pos) {
      class_2586 be = world.method_8321(pos);
      if (be instanceof class_2595) {
         return Optional.of(class_2281.method_17458((class_2281)be.method_11010().method_26204(), be.method_11010(), world, pos, true));
      } else {
         return be instanceof class_3719 ? Optional.of((class_3719)be) : Optional.empty();
      }
   }

   private Optional<class_2338> getOtherHalf(class_1937 world, class_2338 pos) {
      class_2680 state = world.method_8320(pos);
      return state.method_26204() instanceof class_2281 ? Optional.of(pos.method_10093(class_2281.method_9758(state))) : Optional.empty();
   }

   private void sortInventory(class_1263 inv) {
      List<class_1799> items = new ArrayList();

      for(int i = 0; i < inv.method_5439(); ++i) {
         items.add(inv.method_5438(i));
         inv.method_5447(i, class_1799.field_8037);
      }

      items.sort(((SortAlgorithm)MaidAddonConfig.ORGANIZING_ALGORITHM.get()).getComparator());

      for(class_1799 item : items) {
         if (!item.method_7960()) {
            for(int i = 0; i < inv.method_5439() && !item.method_7960(); ++i) {
               class_1799 existing = inv.method_5438(i);
               if (class_1799.method_31577(existing, item) && existing.method_7947() < existing.method_7914()) {
                  int toTransfer = Math.min(item.method_7947(), existing.method_7914() - existing.method_7947());
                  existing.method_7933(toTransfer);
                  item.method_7934(toTransfer);
               }
            }

            if (!item.method_7960()) {
               for(int i = 0; i < inv.method_5439(); ++i) {
                  if (inv.method_5438(i).method_7960()) {
                     inv.method_5447(i, item.method_7972());
                     item.method_7939(0);
                     break;
                  }
               }
            }
         }
      }

   }

   private void playAnimation(class_3218 world, class_2338 pos, boolean open) {
      class_2586 be = world.method_8321(pos);
      if (be instanceof class_2595) {
         world.method_8427(pos, be.method_11010().method_26204(), 1, open ? 1 : 0);
         world.method_8396((class_1657)null, pos, open ? class_3417.field_14982 : class_3417.field_14823, class_3419.field_15245, 0.5F, world.field_9229.method_43057() * 0.1F + 0.9F);
      } else if (be instanceof class_3719) {
         world.method_8652(pos, (class_2680)be.method_11010().method_11657(class_3708.field_18006, open), 3);
         world.method_8396((class_1657)null, pos, open ? class_3417.field_17604 : class_3417.field_17603, class_3419.field_15245, 0.5F, world.field_9229.method_43057() * 0.1F + 0.9F);
      }

   }
}
