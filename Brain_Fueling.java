package com.commoi.maidaddon.task;

import com.commoi.maidaddon.config.MaidAddonConfig;
import com.commoi.maidaddon.task.fuel.FuelingAlgorithm;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.class_1268;
import net.minecraft.class_1277;
import net.minecraft.class_1799;
import net.minecraft.class_1863;
import net.minecraft.class_2338;
import net.minecraft.class_2363;
import net.minecraft.class_2586;
import net.minecraft.class_2609;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3861;
import net.minecraft.class_3956;
import net.minecraft.class_4097;

public class Brain_Fueling extends class_4097<EntityMaid> {
   private static final Map<class_2338, Long> fueledFurnaces = new HashMap();
   private class_2338 targetFurnace = null;
   private int taskCooldown = 0;
   private class_1799 bestFuel;

   public Brain_Fueling() {
      super(Map.of());
      this.bestFuel = class_1799.field_8037;
   }

   protected boolean shouldRun(class_3218 world, EntityMaid maid) {
      if (!maid.method_24345() && this.hasFuel(maid)) {
         fueledFurnaces.entrySet().removeIf((entry) -> (Long)entry.getValue() < world.method_8510());
         this.targetFurnace = this.findNearbyFurnace(world, maid);
         return this.targetFurnace != null;
      } else {
         return false;
      }
   }

   protected void run(class_3218 world, EntityMaid maid, long time) {
      this.taskCooldown = 60;
      this.bestFuel = class_1799.field_8037;
   }

   protected void keepRunning(class_3218 world, EntityMaid maid, long time) {
      if (this.targetFurnace == null) {
         this.taskCooldown = 0;
      } else {
         --this.taskCooldown;
         maid.method_5988().method_20248((double)this.targetFurnace.method_10263(), (double)this.targetFurnace.method_10264(), (double)this.targetFurnace.method_10260());
         if (maid.method_5707(this.targetFurnace.method_46558()) > (double)6.0F) {
            maid.method_5942().method_6337((double)this.targetFurnace.method_10263(), (double)this.targetFurnace.method_10264(), (double)this.targetFurnace.method_10260(), 0.7);
         } else {
            maid.method_5942().method_6340();
            if (this.taskCooldown <= 40 && this.bestFuel.method_7960()) {
               class_2586 be = world.method_8321(this.targetFurnace);
               if (be instanceof class_2609) {
                  class_2609 furnace = (class_2609)be;
                  this.findBestFuel(maid, furnace).ifPresent((fuel) -> this.bestFuel = fuel);
               }
            }

            if (!this.bestFuel.method_7960() && this.taskCooldown <= 20) {
               if (maid.method_6047().method_7909() != this.bestFuel.method_7909()) {
                  for(int i = 0; i < maid.getMaidInv().getSlots(); ++i) {
                     if (maid.getMaidInv().getStackInSlot(i).method_7909() == this.bestFuel.method_7909()) {
                        class_1799 currentMainHand = maid.method_6047().method_7972();
                        maid.method_6122(class_1268.field_5808, maid.getMaidInv().getStackInSlot(i));
                        maid.getMaidInv().setStackInSlot(i, currentMainHand);
                        break;
                     }
                  }
               }

               class_2586 be = world.method_8321(this.targetFurnace);
               if (be instanceof class_2609) {
                  class_2609 furnace = (class_2609)be;
                  class_1799 fuelToInsert = maid.method_6047().method_7971(this.bestFuel.method_7947());
                  furnace.method_5447(1, fuelToInsert);
               }

               this.taskCooldown = 0;
            }

         }
      }
   }

   protected boolean shouldKeepRunning(class_3218 world, EntityMaid maid, long time) {
      if (this.targetFurnace != null && this.taskCooldown > 0) {
         return true;
      } else {
         if (this.targetFurnace != null) {
            fueledFurnaces.put(this.targetFurnace, world.method_8510() + (long)(Integer)MaidAddonConfig.FUELING_FURNACE_COOLDOWN.get());
         }

         return false;
      }
   }

   private class_2338 findNearbyFurnace(class_3218 world, EntityMaid maid) {
      int range = 8;
      class_2338 maidPos = maid.method_24515();
      long currentTime = world.method_8510();
      List<? extends String> itemBlacklist = (List)MaidAddonConfig.FUELING_ITEM_BLACKLIST.get();

      for(class_2338 pos : class_2338.method_10097(maidPos.method_10069(-range, -range, -range), maidPos.method_10069(range, range, range))) {
         if (currentTime >= (Long)fueledFurnaces.getOrDefault(pos, 0L)) {
            class_2586 be = world.method_8321(pos);
            if (be instanceof class_2609) {
               class_2609 furnace = (class_2609)be;
               class_1799 input = furnace.method_5438(0);
               if (!input.method_7960() && !itemBlacklist.contains(input.method_7909().toString())) {
                  class_2680 state = world.method_8320(pos);
                  if (furnace.method_5438(1).method_7960() && !(Boolean)state.method_11654(class_2363.field_11105)) {
                     return pos.method_10062();
                  }
               }
            }
         }
      }

      return null;
   }

   private Optional<class_1799> findBestFuel(EntityMaid maid, class_2609 furnace) {
      List<class_1799> fuels = new ArrayList();
      if (this.isFuel(maid.method_6047())) {
         fuels.add(maid.method_6047());
      }

      if ((Boolean)MaidAddonConfig.FUELING_USE_INVENTORY_FUELS.get()) {
         List<? extends String> blacklist = (List)MaidAddonConfig.FUELING_BLACKLIST.get();

         for(int i = 0; i < maid.getMaidInv().getSlots(); ++i) {
            class_1799 stack = maid.getMaidInv().getStackInSlot(i);
            if (this.isFuel(stack) && !blacklist.contains(stack.method_7909().toString())) {
               fuels.add(stack);
            }
         }
      }

      if (fuels.isEmpty()) {
         return Optional.empty();
      } else {
         FuelingAlgorithm algorithm = (FuelingAlgorithm)MaidAddonConfig.FUELING_ALGORITHM.get();
         class_1863 recipeManager = furnace.method_10997().method_8433();
         Optional<class_3861> recipe = recipeManager.method_8132(class_3956.field_17546, new class_1277(new class_1799[]{furnace.method_5438(0)}), furnace.method_10997());
         int itemsToSmelt = furnace.method_5438(0).method_7947();
         switch (algorithm) {
            case BEST_FUEL:
               return fuels.stream().max(Comparator.comparingInt(this::getFuelTime));
            case WORST_FUEL:
               return fuels.stream().min(Comparator.comparingInt(this::getFuelTime));
            case COST_EFFICIENCY:
               if (recipe.isEmpty()) {
                  return Optional.empty();
               }

               int cookTimePerItem = ((class_3861)recipe.get()).method_8167();
               int requiredBurnTime = cookTimePerItem * itemsToSmelt;
               return fuels.stream().min(Comparator.comparingInt((fuel) -> {
                  int fuelTime = this.getFuelTime(fuel);
                  if (fuelTime == 0) {
                     return Integer.MAX_VALUE;
                  } else {
                     int needed = (int)Math.ceil((double)requiredBurnTime / (double)fuelTime);
                     int waste = needed * fuelTime - requiredBurnTime;
                     return (Boolean)MaidAddonConfig.FUELING_STRICTLY_NO_WASTE.get() && waste != 0 ? Integer.MAX_VALUE : waste;
                  }
               })).map((fuel) -> {
                  int fuelTime = this.getFuelTime(fuel);
                  if (fuelTime == 0) {
                     return class_1799.field_8037;
                  } else {
                     int needed = (int)Math.ceil((double)requiredBurnTime / (double)fuelTime);
                     class_1799 result = fuel.method_7972();
                     result.method_7939(needed);
                     return result;
                  }
               });
            default:
               return Optional.empty();
         }
      }
   }

   private boolean hasFuel(EntityMaid maid) {
      if (this.isFuel(maid.method_6047())) {
         return true;
      } else {
         if ((Boolean)MaidAddonConfig.FUELING_USE_INVENTORY_FUELS.get()) {
            for(int i = 0; i < maid.getMaidInv().getSlots(); ++i) {
               if (this.isFuel(maid.getMaidInv().getStackInSlot(i))) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean isFuel(class_1799 stack) {
      return this.getFuelTime(stack) > 0;
   }

   private int getFuelTime(class_1799 stack) {
      return (Integer)class_2609.method_11196().getOrDefault(stack.method_7909(), 0);
   }
}
