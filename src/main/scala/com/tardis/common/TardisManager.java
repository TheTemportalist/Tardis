package com.tardis.common;

import com.temportalist.origin.library.common.lib.LogHelper;
import com.temportalist.origin.library.common.lib.vec.V3O;
import com.temportalist.origin.library.common.utility.Teleport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
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
	private static final HashMap<Integer, V3O> origins = new HashMap<Integer, V3O>();
	private static final ArrayList<Integer> consoles = new ArrayList<Integer>();

	private static final int providerID = 1210950780;

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
			// todo this errors because the providerID is invalid
			DimensionManager.registerDimension(id, TardisManager.providerID);
			tardis.setInteriorDimension(id);
			TardisManager.tardi.put(id, tardis);
			TardisManager.origins.put(id, V3O.ZERO());
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
			pos = TardisManager.origins.get(dim);
		}
		else {
			dim = tardis.getEntityWorld().provider.getDimensionId();
			pos = new V3O(tardis);
			// todo translate pos based on tardis rotation (where tardis doors are frontwards)
		}
		// todo change to encapsulated method in Teleport
		Teleport.toDimension(player, dim);
		Teleport.toPoint(player, pos);

	}

	public static EntityTardis getTardisForDimension(int dimid) {
		return TardisManager.tardi.containsKey(dimid) ? TardisManager.tardi.get(dimid) : null;
	}

	public static boolean hasConsole(int dimid) {
		return TardisManager.tardi.containsKey(dimid) && TardisManager.consoles.contains(dimid);
	}

}
