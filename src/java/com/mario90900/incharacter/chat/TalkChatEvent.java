package com.mario90900.incharacter.chat;

import java.util.List;

import com.mario90900.incharacter.handlers.ConfigHandler;
import com.mario90900.incharacter.helpers.ChatHelper;
import com.mario90900.incharacter.helpers.LogHelper;
import com.mario90900.incharacter.helpers.StringUtil;
import com.mario90900.incharacter.reference.UnlocalizedNames;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class TalkChatEvent {
	
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void onTalk(ServerChatEvent e){
		EntityPlayerMP speakingPlayer = e.player;
		String message = e.message;
		
		World world = speakingPlayer.getEntityWorld();
		List<EntityPlayerMP> playerEntities = world.playerEntities;
		String speakerName = ChatHelper.getPlayerName(speakingPlayer);
		
		ChatComponentText chatComponent = new ChatComponentText("[" + StringUtil.localize(UnlocalizedNames.TALK_CHARACTER) + "] <" + speakerName + "> " + message);
		ChatComponentText muffleComponent = new ChatComponentText("* " + StringUtil.localize(UnlocalizedNames.YOU_HEAR_STRING) + " " + speakerName + " " + StringUtil.localize(UnlocalizedNames.TALK_MUFFLE_STRING));
		
		for (EntityPlayerMP listeningPlayer : playerEntities){
			if (ConfigHandler.looseRestrictionMode){ //If LRM is on, simply just send the messages to each player in turn.
				listeningPlayer.addChatMessage((IChatComponent) chatComponent);
			} else {
				if ((speakingPlayer.getPlayerCoordinates().getDistanceSquaredToChunkCoordinates(listeningPlayer.getPlayerCoordinates())) <= (ConfigHandler.talkRange * ConfigHandler.talkRange)){ //Are the two players within range of one another for this action?
					if (ConfigHandler.muffleSystemEnabled){
						if (speakingPlayer.equals(listeningPlayer)){ //If the Listening Player is the Speaking Player, then just paste the message to them without bothering with a raycast.
							listeningPlayer.addChatMessage((IChatComponent) chatComponent);
						} else { //Since the Listening Player is different from the Speaking Player, and the Muffle System is on, so start the Raycast to test the blocks between them.
							float muffleLevel = ChatHelper.getMuffleLevel(world, Vec3.createVectorHelper(speakingPlayer.posX, speakingPlayer.posY + speakingPlayer.getEyeHeight(), speakingPlayer.posZ), Vec3.createVectorHelper(listeningPlayer.posX, listeningPlayer.posY + listeningPlayer.getEyeHeight(), listeningPlayer.posZ), ConfigHandler.talkMuteCap);
							
							if (muffleLevel < ConfigHandler.talkMuteCap) { //Is the muffleLevel less then the mute cap? If not, just drop the message for this player. They didnt hear anything.
								if (muffleLevel < ConfigHandler.talkMuffleCap) { //Is the muffleLevel less then the muffle cap? Then they heard the message loud and clear.
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
		}
		
		MinecraftServer.getServer().addChatMessage((IChatComponent) chatComponent);
		
		e.setCanceled(true); //Prevent the event from ever reaching vanilla Minecraft code. This disables the regular chat methods.
	}
}
