package com.tardis.common;

import com.tardis.common.dimensions.InyardData;
import com.temportalist.origin.library.common.lib.LogHelper;
import com.temportalist.origin.library.common.lib.vec.V3O;
import com.temportalist.origin.library.common.utility.Teleport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This is the class that will handle interactions between the following: ~ Tardis -> Dimension (interior) ~ Console -> Dimension ~ Dimension -> Tardis
 *
 * @author TheTemportalist XCompWiz
 */
@Deprecated
public class TardisManager1 {

	public static final List<Integer> registeredDims = new ArrayList<Integer>();
	private static final HashMap<Integer, EntityTardis> tardi = new HashMap<Integer, EntityTardis>();
	private static final HashMap<Integer, String> dimNames = new HashMap<Integer, String>();
	private static final HashMap<Integer, V3O> doors = new HashMap<Integer, V3O>();

	public static final int providerID = 1210950780;

	/*
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
	*/

	public static void registerDimensions(boolean isRegistering) {
		/*
		for (Integer id : TardisManager.registeredDims) {
			if (isRegistering) DimensionManager.registerDimension(id, TardisManager.providerID);
			else DimensionManager.unregisterDimension(id);
		}
		*/
	}

	public static void openInterface(EntityPlayer player) {
		PlayerTardis.open(TardisManager1.getTardisForDimension(
				player.getEntityWorld().provider.getDimensionId()
		), player);
	}

	public static void registerTardis(EntityTardis tardis) {
		if (FMLCommonHandler.instance().getMinecraftServerInstance() == null)
			throw new RuntimeException("Cannot create dimension client-side.");
		int id = DimensionManager.getNextFreeDimId();
		if (TardisManager1.tardi.containsKey(id))
			LogHelper.info(Tardis.MODID(), "ERROR: id " + id + " already exists");
		else {
			TardisManager1.registeredDims.add(id);
			DimensionManager.registerDimension(id, TardisManager1.providerID);
			tardis.setInteriorDimension(id);
			TardisManager1.tardi.put(id, tardis);
			TardisManager1.dimNames.put(id, "TARDISDim" + id);
			System.out.println("Registered: " + TardisManager1.dimNames.get(id));
			TardisManager1.doors.put(id,
					V3O.ZERO().$plus(V3O.UP()).$plus(new V3O(.5, 0, .5))
			);

			WorldServer world = TardisManager1.getWorld(id);


			TardisManager1.setData(id).markDirty();

		}
	}

	//TODO finish this method based on starting point in dimension

	public static void leaveDimension(EntityPlayer player) {
		TardisManager1.movePlayerThroughDoor(player, TardisManager1.getTardisForDimension(
				player.getEntityWorld().provider.getDimensionId()
		), false);
	}

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

			WorldSavedData data = TardisManager1.load(dim);
			System.out.println(TardisManager1.doors.toString());

			//WorldServer dimWorld = DimensionManager.getWorld(dim);
			V3O doorPos = TardisManager1.doors.get(dim); // todo this isnt saved and therefore cleared on load
			//IBlockState doorState = dimWorld.getBlockState(doorPos.toBlockPos());
			//EnumFacing facing = (EnumFacing)doorState.getValue(BlockDoor.FACING);
			if (doorPos == null) return;
			pos = doorPos.$plus(EnumFacing.NORTH);

		}
		else {
			dim = tardis.getEntityWorld().provider.getDimensionId();
			pos = new V3O(tardis).$plus(V3O.NORTH()); // todo this is arbitrary, see below
			// todo translate pos based on tardis rotation (where tardis doors are frontwards)
		}
		// todo change to encapsulated method in Teleport
		/*
		if (player instanceof EntityPlayerMP && player.getEntityWorld() instanceof WorldServer) {
			((EntityPlayerMP)player).mcServer.getConfigurationManager().transferPlayerToDimension(
					(EntityPlayerMP)player, dim,
					new TeleporterCore((WorldServer) player.getEntityWorld())
			);
		}
		Teleport.toPoint(player, pos);
		*/
		if (player instanceof EntityPlayerMP && Teleport.toDimension(player, dim)) {
			Teleport.toPoint(player, pos);
		}

	}

	public static String getDimName(int dimid) {
		return TardisManager1.dimNames.get(dimid);
	}

	public static void setDimName(int dimid, String name) {
		TardisManager1.dimNames.put(dimid, name);
	}

	public static EntityTardis getTardisForDimension(int dimid) {
		return TardisManager1.tardi.containsKey(dimid) ? TardisManager1.tardi.get(dimid) : null;
	}

	public static void setTardis(int dimid, EntityTardis tardis) {
		TardisManager1.tardi.put(dimid, tardis);
	}

	public static V3O getDoorPos(int dimid) {
		return TardisManager1.doors.get(dimid);
	}

	public static void setDoorPos(int dimid, V3O pos) {
		TardisManager1.doors.put(dimid, pos);
	}

	public static WorldServer getWorld(int dimid) {
		return MinecraftServer.getServer().worldServerForDimension(dimid);
	}

	public static WorldSavedData setData(int dimid) {
		String key = TardisManager1.getDimName(dimid);
		InyardData data = new InyardData(key);
		data.setDim(dimid);
		TardisManager1.getWorld(dimid).getMapStorage().setData(key, data);
		return data;
	}

	public static WorldSavedData load(int dimid) {
		String name = TardisManager1.getDimName(dimid);
		WorldServer world = TardisManager1.getWorld(dimid);
		WorldSavedData data = world.getMapStorage().loadData(
				InyardData.class, name
		);
		if (data == null) {
			System.out.println("Data was null");
			data = new InyardData(name);
			world.getMapStorage().setData(name, data);
		}
		return data;
	}

	/*
	public static boolean hasConsole(int dimid) {
		return TardisManager.tardi.containsKey(dimid) && TardisManager.consoles.contains(dimid);
	}
	*/

}
