package com.mario90900.incharacter.chat.commands;

import java.util.ArrayList;
import java.util.List;

import com.mario90900.incharacter.handlers.ConfigHandler;
import com.mario90900.incharacter.helpers.ChatHelper;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EmoteChatCommand extends CommandBase {
	private List<String> aliases;
	
	public EmoteChatCommand(){
		aliases = new ArrayList<String>();
		aliases.add("e");
		aliases.add("em");
		aliases.add("emote");
	}

	@Override
	public String getCommandName() {
		return "emote";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/emote <Action>";
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
			speakingPlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.RED + "/emote <Action...>"));
			return;
		}
		
		String message = "";
		
		for (int i = 0; i < wordsArray.length - 1; i++){ //Push all the words back into a single string message...
			message = message + wordsArray[i] + " ";
		}
		message = message + wordsArray[wordsArray.length - 1]; //... and append the last word to the end.
		
		World world = speakingPlayer.getEntityWorld();
		List<EntityPlayerMP> playerEntities = world.playerEntities;
		String speakerName = ChatHelper.getPlayerName(speakingPlayer);
		
		ChatComponentText chatComponent = new ChatComponentText("* " + speakerName + " " + message);
		
		for (EntityPlayerMP listeningPlayer : playerEntities){
			if (ConfigHandler.looseRestrictionMode){
				listeningPlayer.addChatMessage((IChatComponent) chatComponent);
			} else {
				if ((speakingPlayer.getPlayerCoordinates().getDistanceSquaredToChunkCoordinates(listeningPlayer.getPlayerCoordinates())) <= (ConfigHandler.emoteRange * ConfigHandler.emoteRange)){ //Are the two players within range of one another for this action?
					if (ConfigHandler.muffleSystemEnabled){
						if (speakingPlayer.equals(listeningPlayer)){ //If the Listening Player is the Speaking Player, then just paste the message to them without bothering with a raycast.
							listeningPlayer.addChatMessage((IChatComponent) chatComponent);
						} else {
							if (ChatHelper.isEmoteVisible(world, Vec3.createVectorHelper(speakingPlayer.posX, speakingPlayer.posY + speakingPlayer.getEyeHeight(), speakingPlayer.posZ), Vec3.createVectorHelper(listeningPlayer.posX, listeningPlayer.posY + listeningPlayer.getEyeHeight(), listeningPlayer.posZ))) {
								listeningPlayer.addChatMessage((IChatComponent) chatComponent);
							}
						}
					} else {
						listeningPlayer.addChatMessage((IChatComponent) chatComponent);
					}
				}
			}
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
