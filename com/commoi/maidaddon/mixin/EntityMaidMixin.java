package com.commoi.maidaddon.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityMaid.class})
public abstract class EntityMaidMixin extends class_1321 {
   protected EntityMaidMixin(class_1299<? extends class_1321> entityType, class_1937 world) {
      super(entityType, world);
   }

   @Inject(
      method = {"tick"},
      at = {@At("HEAD")}
   )
   private void onTick(CallbackInfo ci) {
      EntityMaid maid = (EntityMaid)this;
      boolean hasWater = Task_Water.hasWaterBucket(maid);
      boolean hasEmpty = Task_Water.hasEmptyBucket(maid);
      if ((hasWater || hasEmpty) && (this.method_5809() || this.method_35057() != null && this.method_35057().method_5809())) {
         this.field_18321.method_18875(class_4140.field_18451);
         this.field_18321.method_18875(class_4140.field_18452);
         this.field_18321.method_18875(class_4140.field_18453);
      }

   }
}
