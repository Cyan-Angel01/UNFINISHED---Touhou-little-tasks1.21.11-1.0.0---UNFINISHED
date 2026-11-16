package com.commoi.maidaddon.task;

import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class Task_Water implements IMaidTask {
   public Identifier getUid() {
      return Identifier.of("maidaddon", "water")
   }

   public ItemStack getIcon() {
      return new ItemStack(Items.WATER_BUCKET)
   }

   public @Nullable SoundEvent getAmbientSound(EntityMaid maid) {
      return null;
   }

   public List<String> getDescription(EntityMaid maid) {
      List<String> desc = Lists.newArrayList();
      desc.add(class_2561.method_43471("task.maidaddon.water.desc").getString());
      desc.add("");
      desc.add("§6[Extra Condition]");
    boolean hasWater = hasWaterBucket(maid);
      String color = hasWater ? "§a" : "§c";
      desc.add("§f- " + color + Text.translatable("task.maidaddon.water.condition").getString());
      return desc;
   }

   public List<Pair<Integer, Task<? super EntityMaid>>> createBrainTasks(EntityMaid maid) {
      return Lists.newArrayList(new Pair[]{Pair.of(0, new Brain_Water())});
   }
public static boolean hasWaterBucket(EntityMaid maid) {
if (maid.getMainHandStack().isOf(Items.WATER_BUCKET)) return true;
var inv = maid.getAvailableBackpackInv();
for (int i = 0; i < inv.getSlots(); i++) {
if (inv.getStackInSlot(i).isOf(Items.WATER_BUCKET)) return true;
}
return false;
}
public static boolean hasEmptyBucket(EntityMaid maid) {
if (maid.getMainHandStack().isOf(Items.BUCKET)) return true;
var inv = maid.getAvailableBackpackInv();
for (int i = 0; i < inv.getSlots(); i++) {
if (inv.getStackInSlot(i).isOf(Items.BUCKET)) return true;
}
return false;
}
public static void swapBucket(EntityMaid maid, Item fromItem, Item toItem) {
if (maid.getMainHandStack().isOf(fromItem)) {
maid.setStackInHand(Hand.MAIN_HAND, new ItemStack(toItem));
return;
}
var inv = maid.getAvailableBackpackInv();
for (int i = 0; i < inv.getSlots(); i++) {
if (inv.getStackInSlot(i).isOf(fromItem)) {
inv.setStackInSlot(i, new ItemStack(toItem));
return;
}
}
}

}

//Ayayaaaaaaa Aegis!!
