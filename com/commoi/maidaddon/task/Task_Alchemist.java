package com.commoi.maidaddon.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1803;
import net.minecraft.class_1828;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_7893;
import org.jetbrains.annotations.Nullable;

public class Task_Alchemist implements IMaidTask {
   public static final class_2960 UID = new class_2960("maidaddon", "alchemist");

   public class_2960 getUid() {
      return UID;
   }

   public class_1799 getIcon() {
      return class_1802.field_8436.method_7854();
   }

   public @Nullable class_3414 getAmbientSound(EntityMaid maid) {
      return class_3417.field_14978;
   }

   public List<String> getDescription(EntityMaid maid) {
      List<String> desc = Lists.newArrayList();
      desc.add(class_2561.method_43471("task.maidaddon.alchemist.desc").getString());
      desc.add("");
      desc.add("§6[Extra Condition]");
      boolean hasPotion = this.hasPotion(maid);
      String color = hasPotion ? "§a" : "§c";
      desc.add("§f- " + color + class_2561.method_43471("task.maidaddon.alchemist.condition").getString());
      return desc;
   }

   private boolean hasPotion(EntityMaid maid) {
      return maid.method_6047().method_7909() instanceof class_1828 || maid.method_6047().method_7909() instanceof class_1803;
   }

   public List<Pair<Integer, class_7893<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
      List<Pair<Integer, class_7893<? super EntityMaid>>> tasks = new ArrayList();
      tasks.add(Pair.of(6, new Brain_Alchemist()));
      return tasks;
   }

   public List<Pair<Integer, class_7893<? super EntityMaid>>> createRideBrainTasks(EntityMaid maid) {
      List<Pair<Integer, class_7893<? super EntityMaid>>> tasks = new ArrayList();
      tasks.add(Pair.of(6, new Brain_Alchemist()));
      return tasks;
   }
}
