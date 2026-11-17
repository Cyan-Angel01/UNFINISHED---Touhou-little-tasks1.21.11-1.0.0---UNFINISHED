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
