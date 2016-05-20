package com.mario90900.incharacter.helpers;

import net.minecraft.util.StatCollector;

public class StringUtil {
	public static String localize(String key) {
		return StatCollector.translateToLocal(key);
	}
}
