package com.mario90900.incharacter.items;

import com.mario90900.incharacter.reference.UnlocalizedNames;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class InCharacterItems {
	
	public static final Item enderResonator = new EnderResonator();
	
	public static void init(){
		GameRegistry.registerItem(enderResonator, UnlocalizedNames.ENDER_RESONATOR);
	}
}
