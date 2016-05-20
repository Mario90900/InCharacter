package com.mario90900.incharacter.chat.commands;

import java.util.ArrayList;
import java.util.List;

import com.mario90900.incharacter.helpers.StringUtil;
import com.mario90900.incharacter.reference.UnlocalizedNames;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class OOCChatCommand extends CommandBase {
	private List<String> aliases;
	
	public OOCChatCommand(){
		aliases = new ArrayList<String>();
		aliases.add("o");
		aliases.add("ooc");
	}
	
	@Override
	public String getCommandName() {
		return "ooc";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/ooc <Message>";
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
		
		if (wordsArray.length < 1){ //Does this command have any arguments? If there are none, report an error.
			speakingPlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.RED + "/ooc <Message...>"));
			return;
		}
		
		String message = "";
		
		for (int i = 0; i < wordsArray.length - 1; i++){ //Push all the words back into a single string message...
			message = message + wordsArray[i] + " ";
		}
		message = message + wordsArray[wordsArray.length - 1]; //... and append the last word to the end.
		
		List<EntityPlayerMP> playerEntities = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		
		ChatComponentText chatPart1 = new ChatComponentText("[");
		ChatComponentText chatPart2 = new ChatComponentText(StringUtil.localize(UnlocalizedNames.OOC_TAG));
		chatPart2.getChatStyle().setColor(EnumChatFormatting.BLUE);
		ChatComponentText chatPart3 = new ChatComponentText("] <" + speakingPlayer.getCommandSenderName() + "> " + message);
		
		ChatComponentText chatComponent = (ChatComponentText) chatPart1.appendSibling(chatPart2).appendSibling(chatPart3);
		
		for (EntityPlayerMP listeningPlayer : playerEntities){
				listeningPlayer.addChatMessage((IChatComponent) chatComponent);
		}
		
		MinecraftServer.getServer().addChatMessage((IChatComponent) chatComponent);
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
