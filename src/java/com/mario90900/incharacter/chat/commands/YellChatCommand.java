package com.mario90900.incharacter.chat.commands;

import java.util.ArrayList;
import java.util.List;

import com.mario90900.incharacter.handlers.ConfigHandler;
import com.mario90900.incharacter.helpers.ChatHelper;
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
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class YellChatCommand extends CommandBase {
	private List<String> aliases;
	
	public YellChatCommand(){
		aliases = new ArrayList<String>();
		aliases.add("y");
		aliases.add("yell");
	}
	
	@Override
	public String getCommandName() {
		return "yell";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/yell <Message>";
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
			speakingPlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.RED + "/yell <Message...>"));
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
		
		ChatComponentText chatPart1 = new ChatComponentText("[" + StringUtil.localize(UnlocalizedNames.YELL_CHARACTER) + "] <" + speakerName + "> ");
		ChatComponentText chatPart2 = new ChatComponentText(message);
		chatPart2.getChatStyle().setBold(true);
		ChatComponentText chatComponent = (ChatComponentText) chatPart1.appendSibling(chatPart2);
		
		ChatComponentText muffleComponent = new ChatComponentText("* " + StringUtil.localize(UnlocalizedNames.YOU_HEAR_STRING) + " " + speakerName + " " + StringUtil.localize(UnlocalizedNames.YELL_MUFFLE_STRING));
		
		for (EntityPlayerMP listeningPlayer : playerEntities){
			if ((speakingPlayer.getPlayerCoordinates().getDistanceSquaredToChunkCoordinates(listeningPlayer.getPlayerCoordinates())) <= (ConfigHandler.yellRange * ConfigHandler.yellRange)){ //Are the two players within range of one another for this action?
				if (ConfigHandler.muffleSystemEnabled){
					if (speakingPlayer.equals(listeningPlayer)){ //If the Listening Player is the Speaking Player, then just paste the message to them without bothering with a raycast.
						listeningPlayer.addChatMessage((IChatComponent) chatComponent);
					} else { //Since the Listening Player is different from the Speaking Player, and the Muffle System is on, so start the Raycast to test the blocks between them.
						float muffleLevel = ChatHelper.getMuffleLevel(world, Vec3.createVectorHelper(speakingPlayer.posX, speakingPlayer.posY + speakingPlayer.getEyeHeight(), speakingPlayer.posZ), Vec3.createVectorHelper(listeningPlayer.posX, listeningPlayer.posY + listeningPlayer.getEyeHeight(), listeningPlayer.posZ), ConfigHandler.yellMuteCap);
						
						if (muffleLevel < ConfigHandler.yellMuteCap) { //Is the muffleLevel less then the mute cap? If not, just drop the message for this player. They didnt hear anything.
							if (muffleLevel < ConfigHandler.yellMuffleCap) { //Is the muffleLevel less then the muffle cap? Then they heard the message loud and clear.
								listeningPlayer.addChatMessage((IChatComponent) chatComponent);
							} else { //If not, they heard something, but it got muffled. Let them know they heard something, but not what it was.
								listeningPlayer.addChatMessage((IChatComponent) muffleComponent);
							}
						}
					}
				} else { //Muffle System is disabled, just send the message and thats that.
					listeningPlayer.addChatMessage((IChatComponent) chatComponent);
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
