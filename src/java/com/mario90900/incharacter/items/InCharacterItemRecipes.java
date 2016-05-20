package com.mario90900.incharacter.items;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class InCharacterItemRecipes {
	
	public static void registerRecipes(){
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(InCharacterItems.enderResonator, 1, 0), new Object[] {"GGR", "GEI", "GGG", 'G', "paneGlass", 'R', "dustRedstone", 'E', new ItemStack(Items.ender_eye, 1), 'I', "ingotIron"}));
	}
}
