package org.eneryleen.attackIndicator.indicator;

import org.bukkit.entity.LivingEntity;

public interface IndicatorSpawner {

    void spawnIndicator(LivingEntity entity, double damage);

    void cleanup();
}
