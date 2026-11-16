package com.commoi.maidaddon.task.sort;

import java.util.Comparator;
import net.minecraft.class_1738;
import net.minecraft.class_1743;
import net.minecraft.class_1744;
import net.minecraft.class_1747;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1786;
import net.minecraft.class_1792;
import net.minecraft.class_1794;
import net.minecraft.class_1799;
import net.minecraft.class_1810;
import net.minecraft.class_1812;
import net.minecraft.class_1820;
import net.minecraft.class_1821;
import net.minecraft.class_1829;
import net.minecraft.class_1831;
import net.minecraft.class_1835;
import net.minecraft.class_2680;
import net.minecraft.class_3481;

public enum SortAlgorithm {
   CATEGORY("Category", Comparator.comparingInt(SortAlgorithm::getCategoryPriority).thenComparing((s) -> s.method_7909().toString())),
   A_Z("A-Z", Comparator.comparing((s) -> s.method_7909().toString())),
   Z_A("Z-A", Comparator.comparing((s) -> s.method_7909().toString()).reversed()),
   ITEM_TYPE("Item Type", Comparator.comparingInt(SortAlgorithm::getItemTypePriority).thenComparing((s) -> s.method_7909().toString()));

   private final String name;
   private final Comparator<class_1799> comparator;

   private SortAlgorithm(String name, Comparator<class_1799> comparator) {
      this.name = name;
      this.comparator = comparator;
   }

   public Comparator<class_1799> getComparator() {
      return this.comparator;
   }

   public String toString() {
      return this.name;
   }

   private static int getCategoryPriority(class_1799 stack) {
      if (stack.method_7960()) {
         return 99;
      } else {
         class_1792 item = stack.method_7909();
         if (item instanceof class_1831) {
            return 1;
         } else {
            return item instanceof class_1747 ? 2 : 10;
         }
      }
   }

   private static int getItemTypePriority(class_1799 stack) {
      if (stack.method_7960()) {
         return 999;
      } else {
         class_1792 item = stack.method_7909();
         if (item instanceof class_1829) {
            return 10;
         } else if (item instanceof class_1810) {
            return 11;
         } else if (item instanceof class_1743) {
            return 12;
         } else if (item instanceof class_1821) {
            return 13;
         } else if (item instanceof class_1794) {
            return 14;
         } else if (!(item instanceof class_1753) && !(item instanceof class_1764)) {
            if (item instanceof class_1835) {
               return 16;
            } else if (!(item instanceof class_1820) && !(item instanceof class_1786)) {
               if (item instanceof class_1738) {
                  return 20;
               } else if (item.method_19263()) {
                  return 30;
               } else if (item instanceof class_1812) {
                  return 40;
               } else if (item instanceof class_1744) {
                  return 41;
               } else if (item instanceof class_1747) {
                  class_1747 blockItem = (class_1747)item;
                  class_2680 state = blockItem.method_7711().method_9564();
                  if (state.method_26164(class_3481.field_15475)) {
                     return 100;
                  } else if (!state.method_26164(class_3481.field_29193) && !state.method_26164(class_3481.field_29195) && !state.method_26164(class_3481.field_28989) && !state.method_26164(class_3481.field_29194) && !state.method_26164(class_3481.field_23062) && !state.method_26164(class_3481.field_28988) && !state.method_26164(class_3481.field_28991) && !state.method_26164(class_3481.field_28990)) {
                     return !state.method_26164(class_3481.field_29822) && !state.method_26164(class_3481.field_15466) ? 200 : 102;
                  } else {
                     return 101;
                  }
               } else {
                  return 500;
               }
            } else {
               return 17;
            }
         } else {
            return 15;
         }
      }
   }

   // $FF: synthetic method
   private static SortAlgorithm[] $values() {
      return new SortAlgorithm[]{CATEGORY, A_Z, Z_A, ITEM_TYPE};
   }
}
