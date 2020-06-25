package com.jojo2357.simcityminecraft.entities.sim;

import net.minecraft.nbt.CompoundNBT;

@SuppressWarnings("unused")
public class PerSimData implements ISimSaver{

	private boolean hasHome = false;
	private Jobs job = Jobs.UNEMPLOYED;
	private boolean isWorking;
	private boolean hasJob;
	private String name = "Jimbo obmiJ";
	private Sim mySim;
	
	public PerSimData(Sim sim) {
		this.mySim = sim;
	}
	
	@Override
	public CompoundNBT serializeNBT() {
        CompoundNBT compound = new CompoundNBT();
        ListNBT chunkPressuresNBT = new ListNBT();
        for (Map.Entry<ChunkPos, Object2IntMap<PressureType>> pressureEntry : worldPressureMap.entrySet()) {
            CompoundNBT pressureCompound = new CompoundNBT();
            Object2IntMap<PressureType> pressureMap = getAllPressureInChunk(pressureEntry.getKey());
            pressureCompound.putLong("chunkPos", pressureEntry.getKey().asLong());
            for (PressureType type : pressureMap.keySet()) {
                pressureCompound.putInt(type.getRegistryName().getPath() + "Pressure", pressureMap.get(type));
            }
            chunkPressuresNBT.add(pressureCompound);
        }
        compound.put("chunkPressureNBT", chunkPressuresNBT);
        return compound;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
        NBTHelper.fromListNBT(nbt, "chunkPressureNBT", this::pressureMapParsing);
	}

	@Override
	public void setDataToEntity() {
		// TODO Auto-generated method stub
		
	}

}
