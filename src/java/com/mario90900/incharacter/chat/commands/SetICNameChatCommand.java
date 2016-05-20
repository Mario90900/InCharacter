package com.mario90900.incharacter.chat.commands;

import java.util.ArrayList;
import java.util.List;

import com.mario90900.incharacter.helpers.NBTHelper;
import com.mario90900.incharacter.helpers.StringUtil;
import com.mario90900.incharacter.reference.UnlocalizedNames;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class SetICNameChatCommand extends CommandBase {
	public static final String NICKNAME_TAG = "ICNickname";
	private List<String> aliases;
	
	public SetICNameChatCommand(){
		aliases = new ArrayList<String>();
		aliases.add("nick");
		aliases.add("name");
	}
	
	@Override
	public String getCommandName() {
		return "nickname";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/nickname <New Name>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] wordsArray) {
		EntityPlayerMP commandPlayer;
		
		if (sender instanceof EntityPlayerMP){
			commandPlayer = (EntityPlayerMP)sender;
		} else {
			sender.addChatMessage((IChatComponent) new ChatComponentText("You are not a player!"));
			return;
		}
		
		if (wordsArray.length == 0){
			NBTHelper.removeTag(commandPlayer, NICKNAME_TAG);
		} else {
			String nickname = "";
			
			for (int i = 0; i < wordsArray.length - 1; i++){ //Push all the words back into a single string...
				nickname = nickname + wordsArray[i] + " ";
			}
			nickname = nickname + wordsArray[wordsArray.length - 1]; //... and append the last word to the end.
			
			NBTHelper.setString(commandPlayer, NICKNAME_TAG, nickname);
		}
		
		commandPlayer.addChatMessage((IChatComponent) new ChatComponentText(StringUtil.localize(UnlocalizedNames.SUCCESSFUL_NICK_CHANGE_STRING)));
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
