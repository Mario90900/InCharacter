package com.mario90900.incharacter.chat.commands;

import java.util.ArrayList;
import java.util.List;

import com.mario90900.incharacter.handlers.ConfigHandler;
import com.mario90900.incharacter.helpers.ChatHelper;
import com.mario90900.incharacter.helpers.NBTHelper;
import com.mario90900.incharacter.helpers.StringUtil;
import com.mario90900.incharacter.items.EnderResonator;
import com.mario90900.incharacter.reference.UnlocalizedNames;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ResonatorChatCommand extends CommandBase {
private List<String> aliases;
	
	public ResonatorChatCommand(){
		aliases = new ArrayList<String>();
		aliases.add("r");
		aliases.add("radio");
	}
	
	@Override
	public String getCommandName() {
		return "resonator";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "/resonator <Message...> Requires a resonator.";
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
			speakingPlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.RED + "/resonator <Message...>"));
			return;
		}
		
		ItemStack[] speakerItems = speakingPlayer.inventory.mainInventory;
		List<Integer> frequencies = new ArrayList<Integer>();
		
		for (ItemStack item : speakerItems) {
			if ((item != null) && (item.getItem() instanceof EnderResonator)) {
				frequencies.add(NBTHelper.getInt(item, EnderResonator.FREQUENCY_KEY));
			}
		}
		
		if (frequencies.size() == 0){
			speakingPlayer.addChatMessage((IChatComponent) new ChatComponentText(EnumChatFormatting.RED + "Aquire an Ender Resonator to talk over one!"));
			return;
		}
		
		String message = "";
		
		for (int i = 0; i < wordsArray.length - 1; i++){ //Push all the words back into a single string message...
			message = message + wordsArray[i] + " ";
		}
		message = message + wordsArray[wordsArray.length - 1]; //... and append the last word to the end.
		
		String speakerName = ChatHelper.getPlayerName(speakingPlayer);
		
		sendWhispers(speakingPlayer, speakerName, message);
		
		List<EntityPlayerMP> playerEntities = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
		
		for (EntityPlayerMP listeningPlayer : playerEntities){
			if (!listeningPlayer.equals(speakingPlayer)){
				ItemStack[] listenerItems = listeningPlayer.inventory.mainInventory;
				List<Integer> listenerFrequencies = new ArrayList<Integer>();
				
				for (ItemStack item : listenerItems) {
					if ((item != null) && (item.getItem() instanceof EnderResonator)) {
						listenerFrequencies.add(NBTHelper.getInt(item, EnderResonator.FREQUENCY_KEY));
					}
				}
				
				if (listenerFrequencies.size() != 0) { //Does this player have atleast one Ender Resonator? If so...
					for (int i : frequencies){
						if (listenerFrequencies.contains(i)) {
							sendWhispers(listeningPlayer, speakerName, message);
						}
					}
				}
			}
		}
		
		MinecraftServer.getServer().addChatMessage((IChatComponent) new ChatComponentText("[" + StringUtil.localize(UnlocalizedNames.RESONATOR_CHARACTER) + "] <" + speakingPlayer.getCommandSenderName() + "> " + message));
	}
	
	protected void sendWhispers(EntityPlayerMP sourcePlayer, String speakingPlayerName, String message){
		World world = sourcePlayer.getEntityWorld();
		List<EntityPlayerMP> playerEntities = world.playerEntities;
		
		ChatComponentText chatComponent = new ChatComponentText("[" + StringUtil.localize(UnlocalizedNames.RESONATOR_CHARACTER) + "] <" + speakingPlayerName + "> " + message);
		ChatComponentText muffleComponent = new ChatComponentText("* " + StringUtil.localize(UnlocalizedNames.YOU_HEAR_STRING) + " " + speakingPlayerName + " " + StringUtil.localize(UnlocalizedNames.RESONATOR_MUFFLE_STRING));
		
		for (EntityPlayerMP listeningPlayer : playerEntities){
			if ((sourcePlayer.getPlayerCoordinates().getDistanceSquaredToChunkCoordinates(listeningPlayer.getPlayerCoordinates())) <= (ConfigHandler.whisperRange * ConfigHandler.whisperRange)){ //Are the two players within range of one another for this action?
				if (ConfigHandler.muffleSystemEnabled){
					if (sourcePlayer.equals(listeningPlayer)){ //If the Listening Player is the Speaking Player, then just paste the message to them without bothering with a raycast.
						listeningPlayer.addChatMessage((IChatComponent) chatComponent);
					} else { //Since the Listening Player is different from the Speaking Player, and the Muffle System is on, so start the Raycast to test the blocks between them.
						float muffleLevel = ChatHelper.getMuffleLevel(world, Vec3.createVectorHelper(sourcePlayer.posX, sourcePlayer.posY + sourcePlayer.getEyeHeight(), sourcePlayer.posZ), Vec3.createVectorHelper(listeningPlayer.posX, listeningPlayer.posY + listeningPlayer.getEyeHeight(), listeningPlayer.posZ), ConfigHandler.whisperMuteCap);
						
						if (muffleLevel < ConfigHandler.whisperMuteCap) { //Is the muffleLevel less then the mute cap? If not, just drop the message for this player. They didnt hear anything.
							if (muffleLevel < ConfigHandler.whisperMuffleCap) { //Is the muffleLevel less then the muffle cap? Then they heard the message loud and clear.
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
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender){
		return true;
	}
	
	@Override
	public List getCommandAliases(){
		return aliases;
	}
}
