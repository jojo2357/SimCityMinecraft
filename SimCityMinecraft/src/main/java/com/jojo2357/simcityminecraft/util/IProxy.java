package com.jojo2357.simcityminecraft.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {
    World getClientWorld();

    PlayerEntity getClientPlayer();
}
