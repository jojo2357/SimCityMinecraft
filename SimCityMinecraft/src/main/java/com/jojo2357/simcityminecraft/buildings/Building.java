package com.jojo2357.simcityminecraft.buildings;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.util.handler.BuildingTemplate;

import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.registries.ForgeRegistries;

abstract class Building {
	
	private BuildingType building;
	private int occupancy;
	private BuildingTemplate myRecipe;
	private BlockPos thisSpot;
	
	public Building(BuildingType buildingType, int residents, BuildingTemplate template, BlockPos location) {
		this.building = buildingType;
		this.occupancy = residents;
		this.myRecipe = template;
		this.thisSpot = location;
	}
	
	
	
	
	/*private Block[][][] blueprint;
	private char[][][] states;
	private int[] blocksNeeded;
	private String name;
	private String category;
	private int[] dimensions = new int[3];
	private String stringMensions;
	private String[] blocksName;

	public Building(String displayName, String type) {
		if (displayName.contains(".txt"))
			displayName = displayName.substring(0, displayName.length() - 4);
		this.name = displayName;
		this.category = type;
		try {
			File f = new File(Main.getFolder() + File.separator + type + File.separator + displayName + ".txt");

			if (!f.exists()) {
				return;
			}

			FileInputStream fstream = new FileInputStream(
					Main.getFolder() + File.separator + type + File.separator + displayName + ".txt");
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			strLine = br.readLine().toString().toLowerCase().trim(); // dimensions
			this.stringMensions = strLine;
			String[] d = strLine.split("x");
			int[] di = { 0, 0, 0 };
			di[0] = Integer.parseInt(d[0]);
			di[1] = Integer.parseInt(d[1]);
			di[2] = Integer.parseInt(d[2]);
			this.dimensions[0] = di[2];
			this.dimensions[1] = di[1];
			this.dimensions[2] = di[0];
			br.readLine().toString().toLowerCase().trim();
			ArrayList<String> key = new ArrayList<String>();
			ArrayList<Block> corresponding = new ArrayList<Block>();
			ArrayList<Character> keyStates = new ArrayList<Character>();
			this.blueprint = new Block[di[2]][di[1]][di[0]];
			this.states = new char[di[2]][di[1]][di[0]];
			String readIn;
			do {
				readIn = br.readLine().toString().toLowerCase().trim();
				if (readIn.contains("}"))
					break;
				String args[] = readIn.split("=");
				key.add(args[0]);
				String[] namespace = args[1].split(":");
				ResourceLocation blok = (new ResourceLocation(namespace[0], namespace[1]));
				Block bloq = ForgeRegistries.BLOCKS.getValue(blok);
				corresponding.add(bloq);
				keyStates.add(namespace.length == 3 ? namespace[2].charAt(0) : '0');
			} while (true);
			ArrayList<Integer> bloqueCount = new ArrayList<Integer>();
			for (int i = 0; i < key.size(); i++)
				bloqueCount.add(0);
			/*
			 * for (int i = 0; i < key.size(); i++) { blocksName[i] =
			 * corresponding.get(i).getNameTextComponent().getString(); blocksNeeded[i] = 0;
			 * }
			 *
			readIn = br.readLine().toString().toLowerCase().trim();
			String[] levels = readIn.split(";");
			for (int level = 0; level < di[2]; level++) {
				String[] thisLevel = levels[level].split(",");
				for (int column = 0; column < di[1]; column++) {
					for (int row = 0; row < di[0]; row++) {
						bloqueCount.set(key.indexOf("" + thisLevel[column].charAt(row)),
								1 + bloqueCount.get(key.indexOf("" + thisLevel[column].charAt(row))));
						this.blueprint[level][column][row] = corresponding
								.get(key.indexOf("" + thisLevel[column].charAt(row)));
						this.states[level][column][row] = keyStates
								.get(key.indexOf("" + thisLevel[column].charAt(row)));
					}
				}

			}

			for (int i = corresponding.size() - 1; i >= 0; i--) {
				for (int j = i - 1; j >= 0; j--) {
					if (corresponding.get(i).getNameTextComponent().getString()
							.equals(corresponding.get(j).getNameTextComponent().getString())) {
						corresponding.remove(i);
						int holder = bloqueCount.get(i);
						bloqueCount.remove(i);
						bloqueCount.set(j, holder + bloqueCount.get(j));
						break;
					} else if (corresponding.get(i).getNameTextComponent().getString().equals("Air")) {
						bloqueCount.remove(i);
						corresponding.remove(i);
						break;
					}else if (corresponding.get(j).getNameTextComponent().getString().equals("Air")) {
						bloqueCount.remove(j);
						corresponding.remove(j);
						break;
					}
				}
			}
			this.blocksNeeded = new int[corresponding.size()];
			this.blocksName = new String[corresponding.size()];
			for (int p = 0; p < corresponding.size(); p++) {
				this.blocksName[p] = corresponding.get(p).getNameTextComponent().getString();
				this.blocksNeeded[p] = bloqueCount.get(p);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Block[][][] getBlueprint() {
		return this.blueprint;
	}

	public char[][][] getStates() {
		return this.states;
	}

	public String getName() {
		return this.name;
	}

	public String getDimensions() {
		return this.stringMensions;
	}

	public String[] getRecipeNames() {
		return this.blocksName;
	}

	public int[] getRecipeNumbers() {
		return this.blocksNeeded;
	}*/
}
