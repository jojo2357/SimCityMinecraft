package com.jojo2357.simcityminecraft.util.handler;

import java.util.ArrayList;

import net.minecraft.world.gen.feature.template.Template;

public class BuildingTemplate extends Template{
	private ArrayList<String> recipeNames = new ArrayList<String>();
	private ArrayList<Integer> recipeNumbers = new ArrayList<Integer>();
	private final Template template;
	private String name;
	private String dimensions = "";
	
	public BuildingTemplate(Template template, String name) {
		this.template = template;
		this.name = name;
		for (int i = 0; i < template.getSize().toString().length(); i++)
			if (Character.isDigit(template.getSize().toString().charAt(i))) this.dimensions = this.dimensions + template.getSize().toString().charAt(i);
			else if (i > 0 ? Character.isDigit(template.getSize().toString().charAt(i - 1)) : false) this.dimensions += "x";
		this.dimensions = this.dimensions.substring(0, this.dimensions.length() - 1);
		this.loadRecipe();
	}

	public void loadRecipe() {
		for (BlockInfo blok : this.template.blocks.get(0)) {
			String blokName = blok.state.getBlock().asItem().toString();
			if (this.recipeNames.contains(blokName)) {
				this.recipeNumbers.set(recipeNames.indexOf(blokName), this.recipeNumbers.get(recipeNames.indexOf(blokName)) + 1);
			}else {
				this.recipeNames.add(blokName);
				this.recipeNumbers.add(1);
			}
		}
	}

	public ArrayList<Integer> getRecipeNumbers() {
		return this.recipeNumbers;
	}

	public ArrayList<String> getRecipeNames() {
		return this.recipeNames;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDimensions() {
		return this.dimensions;
	}

	public Template getTemplate() {
		return this.template;
	}
}
