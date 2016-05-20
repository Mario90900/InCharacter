package com.mario90900.incharacter.items;

import java.util.List;

import com.mario90900.incharacter.InCharacter;
import com.mario90900.incharacter.helpers.NBTHelper;
import com.mario90900.incharacter.helpers.StringUtil;
import com.mario90900.incharacter.reference.UnlocalizedNames;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class EnderResonator extends Item {
	public static final String FREQUENCY_KEY = "frequency";
	
	public EnderResonator(){
		super();
		this.setCreativeTab(CreativeTabs.tabTools);
		this.setUnlocalizedName(UnlocalizedNames.ENDER_RESONATOR);
		this.setMaxStackSize(1);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		int frequency = NBTHelper.getInt(stack, FREQUENCY_KEY);
		
		if (!player.isSneaking()){ //If the player is not sneaking, increment forward.
			frequency++;
			
			if (frequency > 16){
				frequency = 1;
			}
		} else { //If they are sneaking, decrement backward.
			frequency--;
			
			if (frequency < 1){
				frequency = 16;
			}
		}
		
		NBTHelper.setInteger(stack, FREQUENCY_KEY, frequency);
		player.playSound("random.orb", 0.5f, 1.0f);
		
		if (world.isRemote){
			player.addChatMessage((IChatComponent) new ChatComponentText(StringUtil.localize(UnlocalizedNames.ENDER_RESONATOR_CHANGE_MESSAGE) + " " + frequency));
		}
		
		return stack;
	}
	
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		int frequency = NBTHelper.getInt(stack, FREQUENCY_KEY);
		
		list.add(StringUtil.localize(UnlocalizedNames.ENDER_RESONATOR_TOOLTIP) + ": " + frequency);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
		ItemStack stack = new ItemStack(item, 1, 0);
		NBTHelper.setInteger(stack, FREQUENCY_KEY, 1);
		list.add(stack);
	}
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		NBTHelper.setInteger(stack, FREQUENCY_KEY, 1);
	}
	
	@Override
	public boolean isDamageable(){
		return false;
	}
	
	@Override
	public String getUnlocalizedName(){
		return String.format("item.%s%s", InCharacter.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
	}
	
	@Override
    public String getUnlocalizedName(ItemStack itemStack){
        return String.format("item.%s%s", InCharacter.MODID.toLowerCase() + ":", getUnwrappedUnlocalizedName(super.getUnlocalizedName()));
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister){
        itemIcon = iconRegister.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf(".") + 1));
    }
	
	protected String getUnwrappedUnlocalizedName(String unlocalizedName){
        return unlocalizedName.substring(unlocalizedName.indexOf(".") + 1);
    }
}
