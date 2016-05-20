package com.mario90900.incharacter;

import com.mario90900.incharacter.chat.CommandEventIntercepter;
import com.mario90900.incharacter.chat.TalkChatEvent;
import com.mario90900.incharacter.chat.commands.EmoteChatCommand;
import com.mario90900.incharacter.chat.commands.GetUsernameFromNicknameChatCommand;
import com.mario90900.incharacter.chat.commands.ICHelpChatCommand;
import com.mario90900.incharacter.chat.commands.OOCChatCommand;
import com.mario90900.incharacter.chat.commands.ResonatorChatCommand;
import com.mario90900.incharacter.chat.commands.SetICNameChatCommand;
import com.mario90900.incharacter.chat.commands.WhisperChatCommand;
import com.mario90900.incharacter.chat.commands.WhisperEmoteChatCommand;
import com.mario90900.incharacter.chat.commands.YellChatCommand;
import com.mario90900.incharacter.handlers.ConfigHandler;
import com.mario90900.incharacter.items.InCharacterItemRecipes;
import com.mario90900.incharacter.items.InCharacterItems;
import com.mario90900.incharacter.proxy.IProxy;

import net.minecraft.command.ICommand;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Mod(modid = InCharacter.MODID, name = InCharacter.MODNAME, version = InCharacter.VERSION)
public class InCharacter {
	public static final String MODID = "incharacter";
	public static final String MODNAME = "In Character";
	public static final String VERSION = "1.0";
	
	@Instance
	public static InCharacter instance = new InCharacter();
	
	@SidedProxy(clientSide = "com.mario90900.incharacter.proxy.ClientProxy", serverSide = "com.mario90900.incharacter.proxy.ServerProxy")
	public static IProxy proxy;
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent e){
		ConfigHandler.init(e.getSuggestedConfigurationFile());
		FMLCommonHandler.instance().bus().register(new ConfigHandler());
		
		InCharacterItems.init();
    }
        
    @EventHandler
    public void init(FMLInitializationEvent e){
    	MinecraftForge.EVENT_BUS.register(new TalkChatEvent()); //Register the basic "Talk" level of chatting.
    	MinecraftForge.EVENT_BUS.register(new CommandEventIntercepter()); //Register the intercepter to drop vanilla /me commands
    	
    	InCharacterItemRecipes.registerRecipes();
    }
        
    @EventHandler
    public void postInit(FMLPostInitializationEvent e){
    	
    }
    
    @EventHandler
    public void serverLoad(FMLServerStartingEvent e){
    	e.registerServerCommand((ICommand) new WhisperChatCommand());
    	
    	if (!ConfigHandler.looseRestrictionMode){
    		e.registerServerCommand((ICommand) new YellChatCommand());
    	}
    	
    	e.registerServerCommand((ICommand) new EmoteChatCommand());
    	e.registerServerCommand((ICommand) new OOCChatCommand());
    	e.registerServerCommand((ICommand) new ResonatorChatCommand());
    	
    	if (ConfigHandler.looseRestrictionMode){
    		e.registerServerCommand((ICommand) new WhisperEmoteChatCommand());
    	}
    	
    	if (ConfigHandler.nicknameSystem){
    		e.registerServerCommand((ICommand) new SetICNameChatCommand());
    		e.registerServerCommand((ICommand) new GetUsernameFromNicknameChatCommand());
    	}
    	
    	e.registerServerCommand((ICommand) new ICHelpChatCommand());
    }
}
