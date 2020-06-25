package com.jojo2357.simcityminecraft.entities.sim;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface ISimSaver extends INBTSerializable<CompoundNBT> {
	void setDataToEntity();
}
