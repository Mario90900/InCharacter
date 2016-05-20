package com.mario90900.incharacter.chat;

import net.minecraft.command.server.CommandEmote;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.CommandEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class CommandEventIntercepter {
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onMe(CommandEvent e){
		if (e.command instanceof CommandEmote){ //Inform the sender that this command has been disabled.
			e.sender.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.RED + "This command has been disabled by the In Character mod. Please use /emote for emoting!"));
			e.setCanceled(true);
		} else if (e.command instanceof CommandMessage){
			e.sender.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.RED + "This command has been disabled by the In Character mod."));
			e.setCanceled(true);
		}
	}
}
