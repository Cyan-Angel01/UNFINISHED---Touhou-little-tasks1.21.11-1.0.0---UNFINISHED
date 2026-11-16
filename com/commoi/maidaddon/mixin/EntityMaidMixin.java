package com.commoi.maidaddon.mixin;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.commoi.maidaddon.task.Task_Water;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityMaid.class})
public abstract class EntityMaidMixin extends TameableEntity {
   protected EntityMaidMixin(EntityType<? extends TameableEntity> entityType, World world) {
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
      if ((hasWater || hasEmpty) && (this.isTouchingWater() || this.getOwner() != null && this.getOwner().isTouchingWater())) {
      this.getBrain().forget(MemoryModuleType.HURT_BY);
      this.getBrain().forget(MemoryModuleType.HURT_BY_ENTITY);
      this.getBrain().forget(MemoryModuleType.NEAREST_HOSTILE);
      }

   }
}
