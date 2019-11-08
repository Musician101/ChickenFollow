package io.musician101.chickenfollow;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.EntityPredicates;

public class ChickenFollowPlayer extends Goal {

    private final ChickenEntity chicken;
    private PlayerEntity target;

    public ChickenFollowPlayer(ChickenEntity chicken) {
        this.chicken = chicken;
        setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    private Optional<? extends PlayerEntity> getPriorityTarget() {
        return ChickenFollow.instance().getTargets().stream().map(name -> chicken.world.getPlayers().stream().filter(player -> name.equals(player.getName().getUnformattedComponentText())).findFirst().orElse(null)).filter(Objects::nonNull).filter(EntityPredicates.NOT_SPECTATING).filter(ep -> !ep.isCreative()).findFirst();
    }

    @Override
    public boolean shouldContinueExecuting() {
        return getPriorityTarget().map(PlayerEntity -> target.getUniqueID().equals(PlayerEntity.getUniqueID())).orElse(super.shouldContinueExecuting());
    }

    @Override
    public boolean shouldExecute() {
        Optional<? extends PlayerEntity> target = getPriorityTarget();
        if (target.isPresent()) {
            this.target = target.get();
            chicken.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20);
            return true;
        }

        this.target = null;
        chicken.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(16);
        return false;
    }

    @Override
    public void tick() {
        if (target == null) {
            chicken.getNavigator().setPath(null, 1D);
            super.tick();
            return;
        }

        chicken.getLookController().setLookPositionWithEntity(target, (float)(chicken.getHorizontalFaceSpeed() + 20), (float)chicken.getVerticalFaceSpeed());
        chicken.getNavigator().tryMoveToEntityLiving(target, 1D);
    }
}
