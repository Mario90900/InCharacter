package com.mario90900.incharacter.chat.commands;

import java.util.List;

import com.mario90900.incharacter.handlers.ConfigHandler;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class ICHelpChatCommand extends CommandBase {
	private List<String> aliases;
	
	public ICHelpChatCommand(){
		
	}
	
	@Override
	public String getCommandName() {
		return "ichelp";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/ichelp";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] wordsArray) {
		EntityPlayerMP speakingPlayer;
		
		if (sender instanceof EntityPlayerMP){
			speakingPlayer = (EntityPlayerMP)sender;
		} else {
			sender.addChatMessage((IChatComponent) new ChatComponentText("You are not a player!"));
			return;
		}
		
		ChatComponentText line1 = new ChatComponentText("-- Showing help for In Character chat commands --");
		line1.getChatStyle().setColor(EnumChatFormatting.GREEN);
		
		ChatComponentText line2 = new ChatComponentText("Simply typing a message like normal allows you to talk normally!");
		ChatComponentText lineLooseRestrict = new ChatComponentText("Loose Restriction Mode is currently on. This means talking and regular emotes are world-wide, but not cross dimension.");
		ChatComponentText lineWhisper = new ChatComponentText("/whisper (/w) <Message> ** Speaks your message in a whisper.");
		ChatComponentText lineYell = new ChatComponentText("/yell (/y) <Message> ** Yells your message loudly!");
		ChatComponentText lineResonator = new ChatComponentText("/resonator (/r, /radio) <Message> ** Requires a Resonator in your inventory. Speak into your resonator. This can be heard cross dimension!");
		ChatComponentText lineEmote = new ChatComponentText("/emote (/e, /em) <Action> ** Perform an emote.");
		ChatComponentText lineWhisperEmote = new ChatComponentText("/whisperemote (/we, /wem, /wemote) <Action> ** Only available in Loose Resctriction mode. Performs your emote, but with a limited range.");
		ChatComponentText lineOOC = new ChatComponentText("/ooc (/o) <Message> ** Speak out of character and to everyone on the server.");
		ChatComponentText lineNickname = new ChatComponentText("/nickname (/nick, /name) <Name> ** Set your In Character nickname to be used for messages and emotes, but not OOC! Running the command without any input clears your name.");
		
		speakingPlayer.addChatMessage((IChatComponent) line1);
		speakingPlayer.addChatMessage((IChatComponent) line2);
		
		if (ConfigHandler.looseRestrictionMode){
			speakingPlayer.addChatMessage((IChatComponent) lineLooseRestrict);
		}
		
		speakingPlayer.addChatMessage((IChatComponent) lineWhisper);
		
		if (!ConfigHandler.looseRestrictionMode){
			speakingPlayer.addChatMessage((IChatComponent) lineYell);
		}
		
		speakingPlayer.addChatMessage((IChatComponent) lineResonator);
		speakingPlayer.addChatMessage((IChatComponent) lineEmote);
		
		if (ConfigHandler.looseRestrictionMode){
			speakingPlayer.addChatMessage((IChatComponent) lineWhisperEmote);
		}
		
		speakingPlayer.addChatMessage((IChatComponent) lineOOC);
		
		if (ConfigHandler.nicknameSystem){
			speakingPlayer.addChatMessage((IChatComponent) lineNickname);
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender){
		return true;
	}
	
	@Override
	public List getCommandAliases(){
		return aliases;
	}
}
