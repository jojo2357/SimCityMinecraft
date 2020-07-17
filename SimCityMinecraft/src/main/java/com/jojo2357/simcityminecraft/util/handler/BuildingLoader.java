package com.jojo2357.simcityminecraft.util.handler;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.jojo2357.simcityminecraft.Main;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.SimpleReloadableResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

public class BuildingLoader {
	public ArrayList<BuildingTemplate> residentialTemplates = new ArrayList<BuildingTemplate>();
	public ArrayList<BuildingTemplate> commercialTemplates = new ArrayList<BuildingTemplate>();
	public ArrayList<BuildingTemplate> industrialTemplates = new ArrayList<BuildingTemplate>();
	public ArrayList<ArrayList<BuildingTemplate>> allTemplates = new ArrayList<ArrayList<BuildingTemplate>>();

	private final String folder = "structures";

	public boolean buildingsLoaded = false;

	public void LoadEm(World worldIn) throws Exception {
		long timeIn = System.currentTimeMillis();
		this.LoadAll(worldIn);
		buildingsLoaded = true;
		System.out.println("Buildings loaded in " + (System.currentTimeMillis() - timeIn) + "ms");

	}

	public void resetAndReload(World worldIn) {
		residentialTemplates = new ArrayList<BuildingTemplate>();
		commercialTemplates = new ArrayList<BuildingTemplate>();
		industrialTemplates = new ArrayList<BuildingTemplate>();		
		allTemplates = new ArrayList<ArrayList<BuildingTemplate>>();
		try {
			this.LoadAll(worldIn);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public BuildingLoader() {
		this.buildingsLoaded = false;
		this.residentialTemplates = new ArrayList<BuildingTemplate>();
		this.commercialTemplates = new ArrayList<BuildingTemplate>();
		this.industrialTemplates = new ArrayList<BuildingTemplate>();
	}

	public void LoadAll(World worldIn) {
		if (worldIn.isRemote)
			return;
		try {
			File folder = new File(Main.getOtherFolder() + File.separator + "residential");
			File[] maybe = folder.listFiles();
			//if (maybe != null)
			for (File uh : maybe) {
				ResourceLocation resc = new ResourceLocation(Main.MOD_ID,
						uh.getName().substring(0, uh.getName().length() - 4));
				BuildingTemplate template = loadBuilding(worldIn, resc);
				if (template == null) continue;
				template.setAuthor("jojo2357");
				this.residentialTemplates.add(template);
			}
			folder = new File(Main.getOtherFolder() + File.separator + "commercial");
			maybe = folder.listFiles();
			for (File uh : maybe) {
				ResourceLocation resc = new ResourceLocation(Main.MOD_ID,
						uh.getName().substring(0, uh.getName().length() - 4));
				BuildingTemplate template = loadBuilding(worldIn, resc);
				if (template == null) continue;
				template.setAuthor("jojo2357");
				this.commercialTemplates.add(template);
			}
			folder = new File(Main.getOtherFolder() + File.separator + "industrial");
			maybe = folder.listFiles();
			for (File uh : maybe) {
				ResourceLocation resc = new ResourceLocation(Main.MOD_ID,
						uh.getName().substring(0, uh.getName().length() - 4));
				BuildingTemplate template = loadBuilding(worldIn, resc);
				if (template == null) continue;
				template.setAuthor("jojo2357");
				this.industrialTemplates.add(template);
			}
			this.allTemplates.add(this.residentialTemplates);
			this.allTemplates.add(this.commercialTemplates);
			this.allTemplates.add(this.industrialTemplates);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public BuildingTemplate loadBuilding(World world, ResourceLocation rescIn) {
		if (world.isRemote())
			return null;
		ServerWorld serverWorld = (ServerWorld) world;
		TemplateManager templateManager = serverWorld.getStructureTemplateManager();
		BuildingTemplate template = null;
		try {
			if (templateManager.getTemplate(rescIn) != null)
			template = new BuildingTemplate(templateManager.getTemplate(rescIn), rescIn.getPath().replace('_', ' '));
		} catch (ResourceLocationException e) {
			e.printStackTrace();
			System.out.println("Fail");
		}
		return template;
	}
}
