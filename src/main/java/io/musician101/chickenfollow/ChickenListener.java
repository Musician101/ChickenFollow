package io.musician101.chickenfollow;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChickenListener {

    @SubscribeEvent
    public void onSpawn(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityChicken)) {
            return;
        }

        EntityChicken chicken = (EntityChicken) entity;
        chicken.targetTasks.addTask(0, new ChickenFollowPlayer(chicken));
    }
}
