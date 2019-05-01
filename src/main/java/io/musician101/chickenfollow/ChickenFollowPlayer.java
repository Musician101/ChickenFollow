package io.musician101.chickenfollow;

import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class ChickenFollowPlayer extends EntityAIBase {

    private final EntityChicken chicken;
    private EntityPlayer target;

    public ChickenFollowPlayer(EntityChicken chicken) {
        this.chicken = chicken;
        setMutexBits(1);
    }

    private Optional<EntityPlayer> getPriorityTarget() {
        return ChickenFollow.INSTANCE.getTargets().stream().map(chicken.world::getPlayerEntityByName).filter(Objects::nonNull).filter(EntitySelectors.NOT_SPECTATING).filter(ep -> !ep.isCreative()).findFirst();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return getPriorityTarget().map(entityPlayer -> target.getUniqueID().equals(entityPlayer.getUniqueID())).orElse(super.shouldContinueExecuting());
    }

    @Override
    public boolean shouldExecute() {
        Optional<EntityPlayer> target = getPriorityTarget();
        if (target.isPresent()) {
            this.target = target.get();
            chicken.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20);
            return true;
        }

        this.target = null;
        chicken.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16);
        return false;
    }

    @Override
    public void updateTask() {
        if (target == null) {
            chicken.getNavigator().setPath(null, 1D);
            super.updateTask();
            return;
        }

        chicken.getLookHelper().setLookPositionWithEntity(target, (float)(chicken.getHorizontalFaceSpeed() + 20), (float)chicken.getVerticalFaceSpeed());
        chicken.getNavigator().tryMoveToEntityLiving(target, 1D);
    }
}
