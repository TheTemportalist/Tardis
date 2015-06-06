package com.tardis.common.dimensions

import java.io.File
import java.util
import java.util.List

import com.tardis.common.EntityTardis
import com.temportalist.origin.api.common.lib.vec.V3O
import com.temportalist.origin.api.common.utility.{WorldHelper, Teleport, Scala}
import com.temportalist.origin.library.common.utility.WorldHelper
import net.minecraft.entity.player.{EntityPlayerMP, EntityPlayer}
import net.minecraft.server.MinecraftServer
import net.minecraft.util.MathHelper
import net.minecraft.world.storage.MapStorage
import net.minecraft.world.{World, WorldProvider}
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.common.util.ForgeDirection

/**
 *
 *
 * @author TheTemportalist
 */
object TardisManager {

	private final val filePrefix: String = "TardisDim"
	final var registeredDims: util.List[Int] = null
	// todo config option
	final var providerID: Int = 543165347
	private final val providerClass: Class[_ <: WorldProvider] = classOf[WorldProviderInyard]

	def registerProviderType(): Unit = {
		DimensionManager.registerProviderType(this.providerID, this.providerClass, true)
	}

	def getDimensionList(dir: File): List[Int] = {
		val dims: List[Int] = new util.ArrayList[Int]()
		for (file: File <- dir.listFiles()) {
			if (file.getName.startsWith(this.filePrefix) && file.getName.endsWith(".dat")) {
				try {
					val fileName: String = file.getName
					dims.add(Integer.parseInt(fileName.substring(
						filePrefix.length, fileName.length - 4
					)))
				}
				catch {
					case e: Exception => e.printStackTrace()
				}
			}
		}
		dims
	}

	def registerDimsFromDir(dir: File): Unit = {
		this.registeredDims = this.getDimensionList(dir)
		Scala.foreach(this.registeredDims, (index: Int, dimid: Int) => {
			if (!DimensionManager.isDimensionRegistered(dimid)) {
				DimensionManager.registerDimension(dimid, this.providerID)
				println("TardisManager: registered dim " + dimid)
			}
		})
	}

	def unregisterDims(): Unit = {
		Scala.foreach(this.registeredDims, (index: Int, dimid: Int) => {
			DimensionManager.unregisterDimension(dimid)
		})
		this.registeredDims = null
	}

	def getDimName(dimID: Int): String = this.filePrefix + dimID

	def getDimData(world: World): InyardData = this.getDimData(
		world.provider.dimensionId,
		!world.isRemote
	)

	def getDimData(dimID: Int, isServer: Boolean): InyardData = {
		if (DimensionManager.getProviderType(dimID) == this.providerID) {
			val dimName: String = this.getDimName(dimID)
			val storage: MapStorage = this.getStorage(isServer)
			if (storage != null) {
				var data: InyardData = storage.loadData(
					classOf[InyardData], dimName
				).asInstanceOf[InyardData]
				if (data == null) {
					data = new InyardData(dimName)
					storage.setData(dimName, data)

					data.setDim(dimID)
					data.setDoorPos(V3O.ZERO + V3O.UP)

					data.markDirty()
				}
				return data
			}
			else {
				println("Storage was null on " + (if (isServer) "server" else "client"))
			}
		}
		else {
			println("[Tardis] Dim " + dimID + " not of tardis provider type " + this.providerID)
		}
		null
	}

	def getStorage(isServer: Boolean): MapStorage = {
		/*
		if (isServer)
		else {
			println("Need to create a client storage, returning null for client side storage fetch")
			null
		}
		*/
		DimensionManager.getWorld(0).perWorldStorage
	}

	def createDimension(): InyardData = {
		val dimID: Int = DimensionManager.getNextFreeDimId
		if (WorldHelper.isClient())
			throw new RuntimeException("Cannot create dimensions client-side")
		this.registeredDims.add(dimID)
		DimensionManager.registerDimension(dimID, this.providerID)
		//PacketHandler.sendToClients(Tardis.MODID, new PacketDimensionRegistration(dimID))
		this.getDimData(dimID, isServer = false)
	}

	def registerTardis(tardis: EntityTardis): Unit = {
		val data: InyardData = this.createDimension()
		if (data != null) {
			//println ("registered tardis @ " + tardis.worldObj.provider.getDimensionId + " | " + tardis.getUniqueID)
			tardis.setInteriorDimension(data.getDim())
			data.setTardis(tardis)
			data.markDirty()
		}
		else {
			println("Data was null and could not assign tardis details")
		}
	}

	def getTardis(world: World): EntityTardis = this.getTardis(
		world.provider.dimensionId,
		!world.isRemote
	)

	def getTardis(dimID: Int, isServer: Boolean): EntityTardis = {
		this.getDimData(dimID, isServer).getTardis()
	}

	def movePlayerIntoTardis(player: EntityPlayer, tardis: EntityTardis): Unit = {
		val dimID: Int = tardis.getInteriorDimension()
		val data: InyardData = this.getDimData(dimID, player.getEntityWorld.isRemote)
		if (data == null) {
			println("null data")
			return
		}

		val spawn: V3O = data.getSpawnPoint(0) + V3O.CENTER.suppressedYAxis()
		//Teleport.toDimensionPoint(player, spawn, dimID)

		if (WorldHelper.isServer() && !player.isDead) {
			println("teleport")
			MinecraftServer.getServer.getConfigurationManager.transferPlayerToDimension(
			player.asInstanceOf[EntityPlayerMP], dimID, new TeleporterCo
			)
		}

	}

	def movePlayerOutOfTardis(player: EntityPlayer): Unit = {
		val tardis: EntityTardis = this.getTardis(player.getEntityWorld)
		if (tardis == null) {
			println("null tardis")
			return
		}

		// todo use that helper function mentioned in ItemPlacer
		val facing: Int = MathHelper.floor_double(((tardis.rotationYaw * 4F) / 360F) + 0.5D) & 3
		val convertedFacing =
			facing match {
				case 0 => 3
				case 1 => 4
				case 2 => 2
				case 3 => 5
				case _ => 2
			}
		val direction: V3O = new V3O(ForgeDirection.getOrientation(convertedFacing)) * 1.5
		val point: V3O = new V3O(tardis) + direction
		Teleport.toDimension(player, tardis.worldObj.provider.dimensionId)
		Teleport.toPoint(player, point)
	}

}
