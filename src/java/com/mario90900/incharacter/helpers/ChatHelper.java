package com.mario90900.incharacter.helpers;

import com.mario90900.incharacter.chat.commands.SetICNameChatCommand;
import com.mario90900.incharacter.handlers.ConfigHandler;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ChatHelper {

	/**
	 * Built off of the raytrace code from the World class. Uses the same math to check the hardness of each block between the Speaker and Listener for the purposes of the Muffle System.
	 * 
	 * @param world		A reference to the world, needed to check the blocks.
	 * @param speaker	The Vec3 position for the speaking player.
	 * @param listener	The Vec3 position for the listening player.
	 * @param muteCap	The cap at which the message is simply never sent to the listener. This is used to cut off the calculations early.
	 * @return
	 */
	public static float getMuffleLevel(World world, Vec3 speaker, Vec3 listener, float muteCap){
        float muffleValue = 0.0f;
		
		if (!Double.isNaN(speaker.xCoord) && !Double.isNaN(speaker.yCoord) && !Double.isNaN(speaker.zCoord)) {
            if (!Double.isNaN(listener.xCoord) && !Double.isNaN(listener.yCoord) && !Double.isNaN(listener.zCoord)) {
                int listenerX = MathHelper.floor_double(listener.xCoord);
                int listenerY = MathHelper.floor_double(listener.yCoord);
                int listenerZ = MathHelper.floor_double(listener.zCoord);
                int speakerX = MathHelper.floor_double(speaker.xCoord);
                int speakerY = MathHelper.floor_double(speaker.yCoord);
                int speakerZ = MathHelper.floor_double(speaker.zCoord);
                int counter = ConfigHandler.raycastLimit;

                while (counter-- >= 0) {
                    if (Double.isNaN(speaker.xCoord) || Double.isNaN(speaker.yCoord) || Double.isNaN(speaker.zCoord)) {
                        return muffleValue;
                    }

                    if (speakerX == listenerX && speakerY == listenerY && speakerZ == listenerZ) {
                        return muffleValue;
                    }
                    
                    if (muffleValue >= muteCap) {
                    	return muffleValue;
                    }

                    boolean shouldChangeX = true;
                    boolean shouldChangeY = true;
                    boolean shouldChangeZ = true;
                    double newX = 999.0D;
                    double newY = 999.0D;
                    double newZ = 999.0D;

                    if (listenerX > speakerX) {
                        newX = (double)speakerX + 1.0D;
                    } else if (listenerX < speakerX) {
                        newX = (double)speakerX + 0.0D;
                    } else {
                        shouldChangeX = false;
                    }

                    if (listenerY > speakerY) {
                        newY = (double)speakerY + 1.0D;
                    } else if (listenerY < speakerY) {
                        newY = (double)speakerY + 0.0D;
                    } else {
                        shouldChangeY = false;
                    }

                    if (listenerZ > speakerZ) {
                        newZ = (double)speakerZ + 1.0D;
                    } else if (listenerZ < speakerZ) {
                        newZ = (double)speakerZ + 0.0D;
                    } else {
                        shouldChangeZ = false;
                    }

                    double percentChangeX = 999.0D;
                    double percentChangeY = 999.0D;
                    double percentChangeZ = 999.0D;
                    double differenceX = listener.xCoord - speaker.xCoord;
                    double differenceY = listener.yCoord - speaker.yCoord;
                    double differenceZ = listener.zCoord - speaker.zCoord;

                    if (shouldChangeX) {
                        percentChangeX = (newX - speaker.xCoord) / differenceX;
                    }

                    if (shouldChangeY) {
                        percentChangeY = (newY - speaker.yCoord) / differenceY;
                    }

                    if (shouldChangeZ) {
                        percentChangeZ = (newZ - speaker.zCoord) / differenceZ;
                    }

                    byte whichToChange;

                    if (percentChangeX < percentChangeY && percentChangeX < percentChangeZ) {
                        if (listenerX > speakerX) {
                            whichToChange = 4;
                        } else {
                            whichToChange = 5;
                        }

                        speaker.xCoord = newX;
                        speaker.yCoord += differenceY * percentChangeX;
                        speaker.zCoord += differenceZ * percentChangeX;
                    } else if (percentChangeY < percentChangeZ) {
                        if (listenerY > speakerY) {
                            whichToChange = 0;
                        } else {
                            whichToChange = 1;
                        }

                        speaker.xCoord += differenceX * percentChangeY;
                        speaker.yCoord = newY;
                        speaker.zCoord += differenceZ * percentChangeY;
                    } else {
                        if (listenerZ > speakerZ) {
                            whichToChange = 2;
                        } else {
                            whichToChange = 3;
                        }

                        speaker.xCoord += differenceX * percentChangeZ;
                        speaker.yCoord += differenceY * percentChangeZ;
                        speaker.zCoord = newZ;
                    }

                    Vec3 vec32 = Vec3.createVectorHelper(speaker.xCoord, speaker.yCoord, speaker.zCoord);
                    speakerX = (int)(vec32.xCoord = (double)MathHelper.floor_double(speaker.xCoord));

                    if (whichToChange == 5) {
                        --speakerX;
                        ++vec32.xCoord;
                    }

                    speakerY = (int)(vec32.yCoord = (double)MathHelper.floor_double(speaker.yCoord));

                    if (whichToChange == 1) {
                        --speakerY;
                        ++vec32.yCoord;
                    }

                    speakerZ = (int)(vec32.zCoord = (double)MathHelper.floor_double(speaker.zCoord));

                    if (whichToChange == 3) {
                        --speakerZ;
                        ++vec32.zCoord;
                    }

                    Block block = world.getBlock(speakerX, speakerY, speakerZ);
                    int blockMeta = world.getBlockMetadata(speakerX, speakerY, speakerZ);
                    
                    if ((block != null) && (block.getCollisionBoundingBoxFromPool(world, speakerX, speakerY, speakerZ) != null) && (block.canCollideCheck(blockMeta, true))) {
                    	MovingObjectPosition mop = collisionRayTraceWithSegmentLength(world, block, speakerX, speakerY, speakerZ, speaker, listener);
                    	
                    	if (mop != null){
                    		float hardness = block.getBlockHardness(world, mop.blockX, mop.blockY, mop.blockZ);
                    		
                    		if (hardness < 0){
                    			hardness = 1000;
                    		}
                    		
                    		float multiplier = (Float) mop.hitInfo;
                    		hardness *= multiplier;
                    		
                    		if (block.getMaterial().equals(Material.cloth)) {
                    			hardness *= 6;
                    		} else if (block.getMaterial().equals(Material.craftedSnow) || block.getMaterial().equals(Material.snow)) {
                    			hardness *= 10;
                    		}
                    		
                    		muffleValue += hardness;
                    	}
                    }
                }

                return muffleValue;
            } else {
                return muffleValue;
            }
        } else {
            return muffleValue;
        }
    }
	
	public static boolean isEmoteVisible(World world, Vec3 speaker, Vec3 listener){
		if (!Double.isNaN(speaker.xCoord) && !Double.isNaN(speaker.yCoord) && !Double.isNaN(speaker.zCoord)) {
            if (!Double.isNaN(listener.xCoord) && !Double.isNaN(listener.yCoord) && !Double.isNaN(listener.zCoord)) {
                int listenerX = MathHelper.floor_double(listener.xCoord);
                int listenerY = MathHelper.floor_double(listener.yCoord);
                int listenerZ = MathHelper.floor_double(listener.zCoord);
                int speakerX = MathHelper.floor_double(speaker.xCoord);
                int speakerY = MathHelper.floor_double(speaker.yCoord);
                int speakerZ = MathHelper.floor_double(speaker.zCoord);
                int counter = ConfigHandler.raycastLimit;

                while (counter-- >= 0) {
                    if (Double.isNaN(speaker.xCoord) || Double.isNaN(speaker.yCoord) || Double.isNaN(speaker.zCoord)) {
                        return false;
                    }

                    if (speakerX == listenerX && speakerY == listenerY && speakerZ == listenerZ) {
                        return true;
                    }

                    boolean shouldChangeX = true;
                    boolean shouldChangeY = true;
                    boolean shouldChangeZ = true;
                    double newX = 999.0D;
                    double newY = 999.0D;
                    double newZ = 999.0D;

                    if (listenerX > speakerX) {
                        newX = (double)speakerX + 1.0D;
                    } else if (listenerX < speakerX) {
                        newX = (double)speakerX + 0.0D;
                    } else {
                        shouldChangeX = false;
                    }

                    if (listenerY > speakerY) {
                        newY = (double)speakerY + 1.0D;
                    } else if (listenerY < speakerY) {
                        newY = (double)speakerY + 0.0D;
                    } else {
                        shouldChangeY = false;
                    }

                    if (listenerZ > speakerZ) {
                        newZ = (double)speakerZ + 1.0D;
                    } else if (listenerZ < speakerZ) {
                        newZ = (double)speakerZ + 0.0D;
                    } else {
                        shouldChangeZ = false;
                    }

                    double percentChangeX = 999.0D;
                    double percentChangeY = 999.0D;
                    double percentChangeZ = 999.0D;
                    double differenceX = listener.xCoord - speaker.xCoord;
                    double differenceY = listener.yCoord - speaker.yCoord;
                    double differenceZ = listener.zCoord - speaker.zCoord;

                    if (shouldChangeX) {
                        percentChangeX = (newX - speaker.xCoord) / differenceX;
                    }

                    if (shouldChangeY) {
                        percentChangeY = (newY - speaker.yCoord) / differenceY;
                    }

                    if (shouldChangeZ) {
                        percentChangeZ = (newZ - speaker.zCoord) / differenceZ;
                    }

                    byte whichToChange;

                    if (percentChangeX < percentChangeY && percentChangeX < percentChangeZ) {
                        if (listenerX > speakerX) {
                            whichToChange = 4;
                        } else {
                            whichToChange = 5;
                        }

                        speaker.xCoord = newX;
                        speaker.yCoord += differenceY * percentChangeX;
                        speaker.zCoord += differenceZ * percentChangeX;
                    } else if (percentChangeY < percentChangeZ) {
                        if (listenerY > speakerY) {
                            whichToChange = 0;
                        } else {
                            whichToChange = 1;
                        }

                        speaker.xCoord += differenceX * percentChangeY;
                        speaker.yCoord = newY;
                        speaker.zCoord += differenceZ * percentChangeY;
                    } else {
                        if (listenerZ > speakerZ) {
                            whichToChange = 2;
                        } else {
                            whichToChange = 3;
                        }

                        speaker.xCoord += differenceX * percentChangeZ;
                        speaker.yCoord += differenceY * percentChangeZ;
                        speaker.zCoord = newZ;
                    }

                    Vec3 vec32 = Vec3.createVectorHelper(speaker.xCoord, speaker.yCoord, speaker.zCoord);
                    speakerX = (int)(vec32.xCoord = (double)MathHelper.floor_double(speaker.xCoord));

                    if (whichToChange == 5) {
                        --speakerX;
                        ++vec32.xCoord;
                    }

                    speakerY = (int)(vec32.yCoord = (double)MathHelper.floor_double(speaker.yCoord));

                    if (whichToChange == 1) {
                        --speakerY;
                        ++vec32.yCoord;
                    }

                    speakerZ = (int)(vec32.zCoord = (double)MathHelper.floor_double(speaker.zCoord));

                    if (whichToChange == 3) {
                        --speakerZ;
                        ++vec32.zCoord;
                    }

                    Block block = world.getBlock(speakerX, speakerY, speakerZ);
                    int blockMeta = world.getBlockMetadata(speakerX, speakerY, speakerZ);
                    
                    if ((block != null) && (block.getCollisionBoundingBoxFromPool(world, speakerX, speakerY, speakerZ) != null) && (block.canCollideCheck(blockMeta, false))) {
                    	MovingObjectPosition mop = block.collisionRayTrace(world, speakerX, speakerY, speakerZ, speaker, listener);
                    	
                    	if ((mop != null) && block.isOpaqueCube()) {
                    		return false;
                    	}
                    }
                }

                return false;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
	
	/**
	 * Tweaks the collision raytrace code from the Block class to add the length of the segment to the extra data in the MOP.
	 * 
	 * @param world		Reference to the World.
	 * @param block		The block being acted on.
	 * @param blockX	The X-position of the block.
	 * @param blockY	The Y-position of the block.
	 * @param blockZ	The Z-position of the block.
	 * @param startVec	The start point of the raytrace.
	 * @param endVec	The end point of the raytrace.
	 * @return			Returns the MOP with the length of the segment as the hitInfo variable.
	 */
	public static MovingObjectPosition collisionRayTraceWithSegmentLength(World world, Block block, int blockX, int blockY, int blockZ, Vec3 startVec, Vec3 endVec) {
        block.setBlockBoundsBasedOnState(world, blockX, blockY, blockZ);
        startVec = startVec.addVector((double)(-blockX), (double)(-blockY), (double)(-blockZ));
        endVec = endVec.addVector((double)(-blockX), (double)(-blockY), (double)(-blockZ));
        
        double blockMinX = block.getBlockBoundsMinX();
        double blockMaxX = block.getBlockBoundsMaxX();
        double blockMinY = block.getBlockBoundsMinY();
        double blockMaxY = block.getBlockBoundsMaxY();
        double blockMinZ = block.getBlockBoundsMinZ();
        double blockMaxZ = block.getBlockBoundsMaxZ();
        
        Vec3 minXVec = startVec.getIntermediateWithXValue(endVec, blockMinX);
        Vec3 maxXVec = startVec.getIntermediateWithXValue(endVec, blockMaxX);
        Vec3 minYVec = startVec.getIntermediateWithYValue(endVec, blockMinY);
        Vec3 maxYVec = startVec.getIntermediateWithYValue(endVec, blockMaxY);
        Vec3 minZVec = startVec.getIntermediateWithZValue(endVec, blockMinZ);
        Vec3 maxZVec = startVec.getIntermediateWithZValue(endVec, blockMaxZ);

        if (!isVecInsideYZBounds(minXVec, blockMinY, blockMaxY, blockMinZ, blockMaxZ)) {
            minXVec = null;
        }

        if (!isVecInsideYZBounds(maxXVec, blockMinY, blockMaxY, blockMinZ, blockMaxZ)) {
            maxXVec = null;
        }

        if (!isVecInsideXZBounds(minYVec, blockMinX, blockMaxX, blockMinZ, blockMaxZ)) {
            minYVec = null;
        }

        if (!isVecInsideXZBounds(maxYVec, blockMinX, blockMaxX, blockMinZ, blockMaxZ)) {
            maxYVec = null;
        }

        if (!isVecInsideXYBounds(minZVec, blockMinX, blockMaxX, blockMinY, blockMaxY)) {
            minZVec = null;
        }

        if (!isVecInsideXYBounds(maxZVec, blockMinX, blockMaxX, blockMinY, blockMaxY)) {
            maxZVec = null;
        }

        Vec3 entryHitVec = null;
        Vec3 exitHitVec = null;

        if (minXVec != null) {
            if (entryHitVec == null) {
            entryHitVec = minXVec;
            } /*else {
            	if (startVec.squareDistanceTo(minXVec) < startVec.squareDistanceTo(entryHitVec)) {
            		exitHitVec = entryHitVec;
            		entryHitVec = minXVec;
            	} else {
            		exitHitVec = minXVec;
            	}
            }*/
        }

        if (maxXVec != null) {
        	if (entryHitVec == null) {
        		entryHitVec = maxXVec;
        	} else {
        		if (startVec.squareDistanceTo(maxXVec) < startVec.squareDistanceTo(entryHitVec)) {
        			exitHitVec = entryHitVec;
        			entryHitVec = maxXVec;
        		} else {
        			exitHitVec = maxXVec;
        		}
        	}
        }

        if (minYVec != null) {
        	if (entryHitVec == null) {
        		entryHitVec = minYVec;
        	} else {
        		if (startVec.squareDistanceTo(minYVec) < startVec.squareDistanceTo(entryHitVec)) {
        			exitHitVec = entryHitVec;
        			entryHitVec = minYVec;
        		} else {
        			exitHitVec = minYVec;
        		}
        	}
        }

        if (maxYVec != null) {
        	if (entryHitVec == null) {
        		entryHitVec = maxYVec;
        	} else {
        		if (startVec.squareDistanceTo(maxYVec) < startVec.squareDistanceTo(entryHitVec)) {
        			exitHitVec = entryHitVec;
        			entryHitVec = maxYVec;
        		} else {
        			exitHitVec = maxYVec;
        		}
        	}
        }

        if (minZVec != null) {
        	if (entryHitVec == null) {
        		entryHitVec = minZVec;
        	} else {
        		if (startVec.squareDistanceTo(minZVec) < startVec.squareDistanceTo(entryHitVec)) {
        			exitHitVec = entryHitVec;
        			entryHitVec = minZVec;
        		} else {
        			exitHitVec = minZVec;
        		}
        	}
        }

        if (maxZVec != null) {
        	if (entryHitVec == null) {
        		entryHitVec = maxZVec;
        	} else {
        		if (startVec.squareDistanceTo(maxZVec) < startVec.squareDistanceTo(entryHitVec)) {
        			exitHitVec = entryHitVec;
        			entryHitVec = maxZVec;
        		} else {
        			exitHitVec = maxZVec;
        		}
        	}
        }

        if (entryHitVec == null) {
            return null;
        } else {
            byte b0 = -1;

            if (entryHitVec == minXVec) {
                b0 = 4;
            }

            if (entryHitVec == maxXVec) {
                b0 = 5;
            }

            if (entryHitVec == minYVec) {
                b0 = 0;
            }

            if (entryHitVec == maxYVec) {
                b0 = 1;
            }

            if (entryHitVec == minZVec) {
                b0 = 2;
            }

            if (entryHitVec == maxZVec) {
                b0 = 3;
            }
            
            float length = 0.0f;
            
            if (exitHitVec != null){
            	length = (float) entryHitVec.distanceTo(exitHitVec);
            }
            
            MovingObjectPosition mop = new MovingObjectPosition(blockX, blockY, blockZ, b0, entryHitVec.addVector((double)blockX, (double)blockY, (double)blockZ));
            mop.hitInfo = length;
            
            return mop;
        }
    }
	
	public static String getPlayerName(EntityPlayerMP player){
		if (ConfigHandler.nicknameSystem){
			if (NBTHelper.hasTag(player, SetICNameChatCommand.NICKNAME_TAG)){
				return NBTHelper.getString(player, SetICNameChatCommand.NICKNAME_TAG);
			} else {
				return player.getCommandSenderName();
			}
		} else {
			if (NBTHelper.hasTag(player, SetICNameChatCommand.NICKNAME_TAG)){
				NBTHelper.removeTag(player, SetICNameChatCommand.NICKNAME_TAG);
			}
			
			return player.getCommandSenderName();
		}
	}
	
	//This is all copied over from the Block class and tweaked slightly. Generally names and to allow it to work when not in the Block Class itself.
	
	/**
     * Checks if a vector is within the Y and Z bounds of the block.
     */
    public static boolean isVecInsideYZBounds(Vec3 testVec, double minY, double maxY, double minZ, double maxZ) {
        return testVec == null ? false : testVec.yCoord >= minY && testVec.yCoord <= maxY && testVec.zCoord >= minZ && testVec.zCoord <= maxZ;
    }

    /**
     * Checks if a vector is within the X and Z bounds of the block.
     */
    public static boolean isVecInsideXZBounds(Vec3 testVec, double minX, double maxX, double minZ, double maxZ) {
        return testVec == null ? false : testVec.xCoord >= minX && testVec.xCoord <= maxX && testVec.zCoord >= minZ && testVec.zCoord <= maxZ;
    }

    /**
     * Checks if a vector is within the X and Y bounds of the block.
     */
    public static boolean isVecInsideXYBounds(Vec3 testVec, double minX, double maxX, double minY, double maxY) {
        return testVec == null ? false : testVec.xCoord >= minX && testVec.xCoord <= maxX && testVec.yCoord >= minY && testVec.yCoord <= maxY;
    }
}
