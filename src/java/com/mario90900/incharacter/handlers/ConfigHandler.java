package com.mario90900.incharacter.handlers;

import java.io.File;

import com.mario90900.incharacter.InCharacter;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
	
	public static Configuration config;
	
	//Loose Restriction Mode control. If true, Loose Restriction Mode is on.
	public static boolean looseRestrictionMode;
	
	//Nickname System. If true is on, false is off.
	public static boolean nicknameSystem;
	
	//Distance Values -- These are the distances from which the respective chat mode can be heard/seen.
	public static float whisperRange;
	public static float talkRange;
	public static float yellRange;
	public static float emoteRange;
	
	//Muffle System -- These values determine the caps for the Muffle System.
	public static boolean muffleSystemEnabled;
	
	public static float whisperMuffleCap;
	public static float whisperMuteCap;
	public static float talkMuffleCap;
	public static float talkMuteCap;
	public static float yellMuffleCap;
	public static float yellMuteCap;
	public static int raycastLimit;
	
	public static void init(File configFile){
		// Create the configuration object from the given configFile
		if(config == null){
			config = new Configuration(configFile);
			loadConfig();
		}
	}
	
	@SubscribeEvent
	public void onConfigurationChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event){
		if(event.modID.equalsIgnoreCase(InCharacter.MODID)){
			// Resync Configs
			loadConfig();
		}
	}
	
	private static void loadConfig(){
		looseRestrictionMode = config.getBoolean("Loose-Restriction Mode", "Modules", false, "Loose-Restriction Mode is a mode where Talks and regular Emotes can be heard/seen throughout the world they were done in, but still not cross dimensionally. Yells are disabled for this reason, as they are redundant in this mode. Finally, another version of emote is enabled, whisper emote, which returns the limited functionality to emoting. This allows for people to still have whispers and emotes that are not world-wide. To talk cross-dimension, Resonators are still required. This does not disable the muffle system for Whisper or WhisperEmote, if you want that disabled, you must also turn it off. [Default: false]");
		nicknameSystem = config.getBoolean("Nicknames", "Modules", true, "This controls if the players are able to set and use a nickname on the server, for all of the IC chat methods. OOC will always be the Minecraft Username. [Default: true]");
		
		whisperRange = config.getFloat("Whisper Range", "Chat Ranges", 16.0f, 0.0f, 256.0f, "The range (in blocks) a whisper can be heard. Must be a float between 0 and 256. A value of 0 will render this kinda useless. [Default: 16.0]");
		talkRange = config.getFloat("Talk Range", "Chat Ranges", 48.0f, 0.0f, 256.0f, "The range (in blocks) a talk can be heard. This is the default chat. Must be a float between 0 and 256. A value of 0 will render this kinda useless. [Default: 48.0]");
		yellRange = config.getFloat("Yell Range", "Chat Ranges", 128.0f, 0.0f, 256.0f, "The range (in blocks) a yell can be heard. Must be a float between 0 and 256. A value of 0 will render this kinda useless. [Default: 128.0]");
		emoteRange = config.getFloat("Emote Range", "Chat Ranges", 128.0f, 0.0f, 256.0f, "The range (in blocks) an emote is able to be seen at. Must be a float between 0 and 256. A value of 0 will render this kinda useless. [Default: 128.0]");
		
		muffleSystemEnabled = config.getBoolean("Muffle System", "Modules", true, "This controls if the Muffle System is enabled or not. True means that the Muffle System is enabled, while False will disable it. [Default: true]");
		
		whisperMuffleCap = config.getFloat("Whisper Muffle Cap", "Muffle System", 1.0f, 0.0f, 256.0f, "The cap at which a whisper gets muffled. This value corresponds to the hardness of the blocks between the pair. Must be a float between 0 and 256. [Default: 1.0]");
		whisperMuteCap = config.getFloat("Whisper Mute Cap", "Muffle System", 2.0f, whisperMuffleCap, 256.0f, "The cap at which a whisper gets muted, the lister does not hear anything. This value corresponds to the hardness of the blocks between the pair. Must be a float between the Muffle Cap and 256. [Default: 2.0]");
		talkMuffleCap = config.getFloat("Talk Muffle Cap", "Muffle System", 8.0f, 0.0f, 256.0f, "The cap at which a talk gets muffled. This value corresponds to the hardness of the blocks between the pair. Must be a float between 0 and 256. [Default: 8.0]");
		talkMuteCap = config.getFloat("Talk Mute Cap", "Muffle System", 12.0f, talkMuffleCap, 256.0f, "The cap at which a talk gets muted, the listener does not hear anything. This value corresponds to the hardness of the blocks between the pair. Must be a float between the Muffle Cap and 256. [Default: 12.0]");
		yellMuffleCap = config.getFloat("Yell Muffle Cap", "Muffle System", 32.0f, 0.0f, 256.0f, "The cap at which a yell gets muffled. This value corresponds to the hardness of the blocks between the pair. Must be a float between 0 and 256. [Default: 32.0]");
		yellMuteCap = config.getFloat("Yell Mute Cap", "Muffle System", 48.0f, yellMuteCap, 256.0f, "The cap at which a yell gets muted, the listener does not hear anything. This value corresponds to the hardness of the blocks between the pair. Must be a float between the Muffle Cap and 256. [Default: 48.0]");
		
		float largestRange = 0.0f;
		
		if (largestRange < yellRange) {
			largestRange = yellRange;
		}
		
		if (largestRange < emoteRange) {
			largestRange = emoteRange;
		}
		
		if (largestRange < talkRange) {
			largestRange = talkRange;
		}
		
		if (largestRange < whisperRange) {
			largestRange = whisperRange;
		}
		
		raycastLimit = MathHelper.ceiling_double_int((Math.sqrt((largestRange * largestRange) / 3)) * 3); //This is used to ensure that the Raycast limit will always be greater then the sphere created from the ranges, and that it still has a limit to how many times it runs incase something goes wrong.
		
		if(config.hasChanged()){
			config.save();
		}
	}
}
