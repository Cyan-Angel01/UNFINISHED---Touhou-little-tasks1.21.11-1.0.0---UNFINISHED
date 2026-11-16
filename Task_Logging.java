package com.commoi.maidaddon.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.class_1743;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_7893;
import org.jetbrains.annotations.Nullable;

public class Task_Logging implements IMaidTask {
   public class_2960 getUid() {
      return new class_2960("maidaddon", "woodcutter");
   }

   public class_1799 getIcon() {
      return new class_1799(class_1802.field_8475);
   }

   public @Nullable class_3414 getAmbientSound(EntityMaid maid) {
      return null;
   }

   public List<String> getDescription(EntityMaid maid) {
      List<String> desc = Lists.newArrayList();
      desc.add(class_2561.method_43471("task.maidaddon.woodcutter.desc").getString());
      desc.add("");
      desc.add("§6[Extra Condition]");
      boolean hasAxe = maid.method_6047().method_7909() instanceof class_1743;
      String axeColor = hasAxe ? "§a" : "§c";
      desc.add("§f- " + axeColor + class_2561.method_43471("task.maidaddon.woodcutter.condition").getString());
      return desc;
   }

   public List<Pair<Integer, class_7893<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
      return Lists.newArrayList(new Pair[]{Pair.of(5, new Brain_Logging())});
   }
}
