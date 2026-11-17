package com.commoi.maidaddon.task;

import com.commoi.maidaddon.config.MaidAddonConfig;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import java.util.Map;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class Brain_Water extends MultiTickTask<LivingEntity> {
    private int cleanupTimer = 0;
    private boolean waterPlaced = false;
    private boolean isFinished = false;
    private BlockPos placementPos = null;

    public Brain_Water() {
        super(Map.of());
    }

    @Override
    protected boolean shouldRun(ServerWorld world, LivingEntity entity) {
        if (entity instanceof EntityMaid maid) {
            boolean hasWater = Task_Water.hasWaterBucket(maid);
            boolean hasEmpty = Task_Water.hasEmptyBucket(maid);
            if (!hasWater && !hasEmpty) {
                return false;
            }
            return this.getTarget(maid) != null;
        }
        return false;
    }
    @Override
    protected void run(ServerWorld world, LivingEntity entity, long time) {
        this.cleanupTimer = 0;
        this.waterPlaced = false;
        this.isFinished = false;
        this.placementPos = null;
        if (entity instanceof EntityMaid maid) {
            maid.getNavigation().stop();
            if (Task_Water.hasEmptyBucket(maid) && !Task_Water.hasWaterBucket(maid)) {
                Task_Water.swapBucket(maid, Items.BUCKET, Items.WATER_BUCKET);
            }
            this.checkAndPlace(world, maid);
        }
    }

    @Override
    protected void keepRunning(ServerWorld world, LivingEntity entity, long time) {
        if (entity instanceof EntityMaid maid) {
            maid.getBrain().forget(MemoryModuleType.HURT_BY);
            maid.getBrain().forget(MemoryModuleType.HURT_BY_ENTITY);
            maid.getBrain().forget(MemoryModuleType.NEAREST_HOSTILE);
            if (!this.waterPlaced) {
                this.checkAndPlace(world, maid);
                if (this.getTarget(maid) == null) {
                    this.isFinished = true;
                }
            } else {
                LivingEntity target = this.getTarget(maid);
                if (target != null) {
                    try {
                        this.cleanupTimer = MaidAddonConfig.WATER_RETRIEVE_DELAY.get();
                    } catch (Exception e) {
                        this.cleanupTimer = 20;
                    }
                } else if (this.cleanupTimer > 0) {
                    --this.cleanupTimer;
                } else {
                    if (this.placementPos != null) {
                        BlockState state = world.getBlockState(this.placementPos);
                        if (state.isOf(Blocks.WATER)) {
                            world.setBlockState(this.placementPos, Blocks.AIR.getDefaultState());
                        }
                    }
                    Task_Water.swapBucket(maid, Items.BUCKET, Items.WATER_BUCKET);
                    this.isFinished = true;
                }
            }
        }
    }
    
    private LivingEntity getTarget(EntityMaid maid) {
        double minFall = MaidAddonConfig.WATER_MIN_FALL_DISTANCE.get();
        LivingEntity owner = maid.getOwner();
        if (owner == null || !((double) owner.fallDistance > minFall) && !(owner.getVelocity().y < -0.6)) {
            if (owner != null && owner.isTouchingWater()) {
                return owner;
            } else if (!((double) maid.fallDistance > minFall) && !(maid.getVelocity().y < -0.6)) {
                return maid.isTouchingWater() ? maid : null;
            } else {
                return maid;
            }
        } else {
            return owner;
        }
    }

    private void checkAndPlace(ServerWorld world, EntityMaid maid) {
        LivingEntity target = this.getTarget(maid);
        if (target == null) return;
        double minFall = MaidAddonConfig.WATER_MIN_FALL_DISTANCE.get();
        boolean isMLG = (double) target.fallDistance > minFall || target.getVelocity().y < -0.6;
        if (maid.squaredDistanceTo(target) > 100.0) {
            maid.getNavigation().startMovingTo(target, 2.3);
            if (isMLG) return;
        }
