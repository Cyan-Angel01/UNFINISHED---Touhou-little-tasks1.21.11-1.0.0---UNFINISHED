package com.commoi.maidaddon.task;

import com.commoi.maidaddon.config.MaidAddonConfig;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2609;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_7893;
import org.jetbrains.annotations.Nullable;

public class Task_Fueling implements IMaidTask {
   public class_2960 getUid() {
      return new class_2960("maidaddon", "fueling");
   }

   public class_1799 getIcon() {
      return new class_1799(class_1802.field_8713);
   }

   public @Nullable class_3414 getAmbientSound(EntityMaid maid) {
      return null;
   }

   public List<String> getDescription(EntityMaid maid) {
      List<String> desc = Lists.newArrayList();
      desc.add(class_2561.method_43471("task.maidaddon.fueling.desc").getString());
      desc.add("");
      desc.add("§6[Extra Condition]");
      boolean hasFuel = this.isFuel(maid.method_6047()) || (Boolean)MaidAddonConfig.FUELING_USE_INVENTORY_FUELS.get() && this.hasFuelInInventory(maid);
      String color = hasFuel ? "§a" : "§c";
      desc.add("§f- " + color + class_2561.method_43471("task.maidaddon.fueling.condition").getString());
      return desc;
   }

   private boolean isFuel(class_1799 stack) {
      return class_2609.method_11196().containsKey(stack.method_7909());
   }

   private boolean hasFuelInInventory(EntityMaid maid) {
      for(int i = 0; i < maid.getMaidInv().getSlots(); ++i) {
         if (this.isFuel(maid.getMaidInv().getStackInSlot(i))) {
            return true;
         }
      }

      return false;
   }

   public List<Pair<Integer, class_7893<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
      return Lists.newArrayList(new Pair[]{Pair.of(5, new Brain_Fueling())});
   }
}
