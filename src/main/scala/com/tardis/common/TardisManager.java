package com.tardis.common;

import com.temportalist.origin.library.common.lib.LogHelper;
import com.temportalist.origin.library.common.lib.TeleporterCore;
import com.temportalist.origin.library.common.lib.vec.V3O;
import com.temportalist.origin.library.common.utility.Teleport;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the class that will handle interactions between the following: ~ Tardis -> Dimension (interior) ~ Console -> Dimension ~ Dimension -> Tardis
 *
 * @author TheTemportalist XCompWiz
 */
public class TardisManager {

	private static final HashMap<Integer, EntityTardis> tardi = new HashMap<Integer, EntityTardis>();
	private static final HashMap<Integer, V3O> doors = new HashMap<Integer, V3O>();
	private static final ArrayList<Integer> consoles = new ArrayList<Integer>();

	public static final int providerID = 1210950780;

	public static void registerConsole(World world, boolean place) {
		int dim = world.provider.getDimensionId();
		if (TardisManager.tardi.containsKey(dim)) {
			if (place) {
				TardisManager.consoles.add(dim);
			}
			else {
				TardisManager.consoles.remove(TardisManager.consoles.indexOf(dim));
			}
		}
	}

	public static void openInterface(EntityPlayer player) {
		PlayerTardis.open(TardisManager.getTardisForDimension(
				player.getEntityWorld().provider.getDimensionId()
		), player);
	}

	public static void registerTardis(EntityTardis tardis) {
		if (FMLCommonHandler.instance().getMinecraftServerInstance() == null)
			throw new RuntimeException("Cannot create dimension client-side.");
		int id = DimensionManager.getNextFreeDimId();
		if (TardisManager.tardi.containsKey(id))
			LogHelper.info(Tardis.MODID(), "ERROR: id " + id + " already exists");
		else {
			DimensionManager.registerDimension(id, TardisManager.providerID);
			tardis.setInteriorDimension(id);
			TardisManager.tardi.put(id, tardis);
			TardisManager.doors.put(id, V3O.ZERO());
		}
	}

	//TODO finish this method based on starting point in dimension

	/**
	 * Moves player through the passed tardis' door (transfer player to tardis interior)
	 *
	 * @param player The player passing into the interior
	 * @param tardis The tardis that is the transfer unit
	 * @param into   True if going into interior, false if coming out
	 */
	public static void movePlayerThroughDoor(EntityPlayer player, EntityTardis tardis,
			boolean into) {
		V3O pos;
		int dim;
		if (into) {
			dim = tardis.getInteriorDimension();
			WorldServer dimWorld = DimensionManager.getWorld(dim);
			V3O doorPos = TardisManager.doors.get(dim); // todo this isnt saved and therefore cleared on load
			IBlockState doorState = dimWorld.getBlockState(doorPos.toBlockPos());
			EnumFacing facing = (EnumFacing)doorState.getValue(BlockDoor.FACING);
			pos = doorPos.$plus(facing);
		}
		else {
			dim = tardis.getEntityWorld().provider.getDimensionId();
			pos = new V3O(tardis);
			// todo translate pos based on tardis rotation (where tardis doors are frontwards)
		}
		// todo change to encapsulated method in Teleport
		if (player instanceof EntityPlayerMP && player.getEntityWorld() instanceof WorldServer) {
			((EntityPlayerMP)player).mcServer.getConfigurationManager().transferPlayerToDimension(
					(EntityPlayerMP)player, dim,
					new TeleporterCore((WorldServer) player.getEntityWorld())
			);
		}
		Teleport.toPoint(player, pos);

	}

	public static EntityTardis getTardisForDimension(int dimid) {
		return TardisManager.tardi.containsKey(dimid) ? TardisManager.tardi.get(dimid) : null;
	}

	public static boolean hasConsole(int dimid) {
		return TardisManager.tardi.containsKey(dimid) && TardisManager.consoles.contains(dimid);
	}

}
