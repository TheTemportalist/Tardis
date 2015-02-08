package com.tardis.common.dimensions

import java.io.{File, FileReader}
import java.util

import com.google.common.io.Files
import com.google.gson._
import com.tardis.common.EntityTardis
import com.temportalist.origin.library.common.utility.Json
import net.minecraft.world.WorldServer
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerConnectionFromClientEvent

/**
 *
 *
 * @author TheTemportalist
 */
object DimManager {

	var registeredDims: util.List[Int] = null

	@SubscribeEvent
	def onSave(event: WorldEvent.Save): Unit = {
		event.world match {
			case server: WorldServer =>
				val dir: File = server.getSaveHandler.getWorldDirectory
				if (dir != null) {
					// TODO put this in the Json Origin helper class
					val archObj: JsonObject = new JsonObject

					val dimArray: JsonArray = new JsonArray
					for (i <- 0 until this.registeredDims.size()) {
						dimArray.add(new JsonPrimitive(this.registeredDims.get(i)))
					}
					archObj.add("TardisDimIDs", dimArray)

					Files.write(
						Json.toReadableString(new Gson().toJson(archObj)).getBytes,
						new File(dir, "tardis.json")
					)

					this.registerDimensions(isRegistering = false)
				}
			case _ =>
		}
	}

	@SubscribeEvent
	def onLoad(event: WorldEvent.Load): Unit = {
		event.world match {
			case server: WorldServer =>
				val dir: File = server.getSaveHandler.getWorldDirectory
				if (dir != null) {
					val file: File = new File(dir, "tardis.json")
					if (!file.exists()) return
					val archObj: JsonObject = new JsonParser().parse(
						new FileReader(file)).getAsJsonObject

					val dimArray: JsonArray = archObj.get("TardisDimIDs").getAsJsonArray
					this.registeredDims = new util.ArrayList[Int]()
					for (i <- 0 until dimArray.size()) {
						this.registeredDims.add(dimArray.get(i).getAsInt)
					}

					this.registerDimensions(isRegistering = true)
				}
			case _ =>
		}
	}

	// TODO what is this for???
	var providerID: Int = 1210950780

	def removeDim(tardis: EntityTardis): Unit = {
		this.registeredDims.remove(tardis.getInteriorDimension())
		DimensionManager.unregisterDimension(tardis.getInteriorDimension())
	}

	def registerDimensions(isRegistering: Boolean): Unit = {
		if (this.registeredDims == null) return
		for (i <- 0 until this.registeredDims.size()) {
			val dimid: Int = this.registeredDims.get(i)
			if (isRegistering) DimensionManager.registerDimension(dimid, this.providerID)
			else DimensionManager.unregisterDimension(dimid)
		}
		this.registeredDims = null
	}

	def registerTardisAndDim(tardis: EntityTardis): Unit = {
		if (FMLCommonHandler.instance().getMinecraftServerInstance == null)
			throw new RuntimeException("Cannot create dimension client-side.")
		val dimid: Int = DimensionManager.getNextFreeDimId
		this.registerDim(dimid)
		tardis.setInteriorDimension(dimid)
	}

	def registerDim(dimid: Int): Unit = {
		if (!this.registeredDims.contains(dimid)) {
			DimensionManager.registerDimension(dimid, this.providerID)
			this.registeredDims.add(dimid)
		}
	}

	@SubscribeEvent
	def serverConnection(event: ServerConnectionFromClientEvent): Unit = {
		//event.manager.sendPacket(new PacketMCDimension())

	}

}
