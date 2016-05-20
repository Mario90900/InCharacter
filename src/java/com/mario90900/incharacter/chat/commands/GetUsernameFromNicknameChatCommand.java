package com.mario90900.incharacter.chat.commands;

import java.util.ArrayList;
import java.util.List;

import com.mario90900.incharacter.helpers.ChatHelper;
import com.mario90900.incharacter.helpers.NBTHelper;
import com.mario90900.incharacter.helpers.StringUtil;
import com.mario90900.incharacter.reference.UnlocalizedNames;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class GetUsernameFromNicknameChatCommand extends CommandBase {
	private List<String> aliases;
	
	public GetUsernameFromNicknameChatCommand(){
		aliases = new ArrayList<String>();
	}
	
	@Override
	public String getCommandName() {
		return "getUsernameFromNickname";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/getUsernameFromNickname <Nickname>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] nicknameWords) {
		if (nicknameWords.length == 0){
			sender.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.RED + "/getUsernameFromNickname <Nickname>"));
			return;
		}
		
		String nickname = "";
		
		for (int i = 0; i < nicknameWords.length - 1; i++){ //Push all the words back into a single string...
			nickname = nickname + nicknameWords[i] + " ";
		}
		nickname = nickname + nicknameWords[nicknameWords.length - 1]; //... and append the last word to the end.
		
		List<EntityPlayerMP> playerEntities = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		List<String> usernames = new ArrayList<String>();
		
		for (EntityPlayerMP testPlayer : playerEntities){
			if (NBTHelper.hasTag(testPlayer, SetICNameChatCommand.NICKNAME_TAG)){
				String testName = ChatHelper.getPlayerName(testPlayer);
				
				if (nickname.equals(testName)){
					usernames.add(testPlayer.getCommandSenderName());
				}
			}
		}
		
		sender.addChatMessage((IChatComponent) new ChatComponentText(StringUtil.localize(UnlocalizedNames.GET_USERNAME_RESPONSE_STRING)));
		
		if (usernames.size() != 0){
			for (String name : usernames){
				sender.addChatMessage((IChatComponent) new ChatComponentText(name));
			}
		}
	}
	
	@Override
	public List getCommandAliases(){
		return aliases;
	}
}
