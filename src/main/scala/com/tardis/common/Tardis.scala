package com.tardis.common

import java.io.{File, FileReader}

import com.google.common.io.Files
import com.google.gson._
import com.tardis.common.block.{BlockConsole, BlockTardisDoor}
import com.tardis.common.dimensions.{DimManager, InyardProvider}
import com.tardis.common.item.ItemPlacer
import com.tardis.common.tile.{TEConsole, TEDoor}
import com.tardis.server.CommandTardis
import com.temportalist.origin.api.IProxy
import com.temportalist.origin.library.common.Origin
import com.temportalist.origin.library.common.handlers.RegisterHelper
import com.temportalist.origin.library.common.utility.{Json, Scala}
import com.temportalist.origin.wrapper.common.ModWrapper
import com.temportalist.origin.wrapper.common.item.ItemWrapper
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.WorldServer
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.world.WorldEvent
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.{EntityRegistry, GameRegistry}
import net.minecraftforge.fml.common.{Mod, SidedProxy}

/**
 *
 *
 * @author TheTemportalist
 */
@Mod(modid = Tardis.MODID, name = Tardis.NAME, version = Tardis.VERSION,
	guiFactory = Tardis.clientProxy, modLanguage = "scala",
	dependencies = "required-after:origin@[4.0,);"
)
object Tardis extends ModWrapper {

	final val MODID = "tardis"
	final val NAME = "Tardis"
	final val VERSION = "@MOD_VERSION@"
	final val clientProxy = "com.tardis.client.ProxyClient"
	final val serverProxy = "com.tardis.server.ProxyServer"

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: IProxy = null

	var tardis: ItemWrapper = null
	var console: BlockConsole = null
	var tDoor: BlockTardisDoor = null

	@Mod.EventHandler
	def pre(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this.MODID, this.NAME, event, this.proxy)

		RegisterHelper.registerExtendedPlayer(
			"tardis", classOf[PlayerTardis], deathPersistance = false
		)

		// Entity Tardis: allow tracking due to high speed travel
		EntityRegistry.registerModEntity(
			classOf[EntityTardis], "tardis", 0, this, 80, 3, true
		)
		// Item Tardis
		this.tardis = new ItemPlacer(this.MODID, "tardis", classOf[EntityTardis]) {
			override def preSpawn(entity: Entity): Unit = {
				TardisManager.registerTardis(entity.asInstanceOf[EntityTardis])
			}
		}
		Origin.addItemToTab(this.tardis)

		this.register("console", classOf[TEConsole])
		this.console = new BlockConsole("console")
		Origin.addBlockToTab(this.console)

		this.register("door", classOf[TEDoor])
		this.tDoor = new BlockTardisDoor("tardis_door")
		Origin.addBlockToTab(this.tDoor)

		RegisterHelper.registerPacketHandler(this.MODID, classOf[PacketTardisController],
			classOf[PacketTardisMover]
		)
		RegisterHelper.registerHandler(DimManager)

		DimensionManager.registerProviderType(TardisManager.providerID, classOf[InyardProvider], true)

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = {
		super.initialize(event, this.proxy)

	}

	@Mod.EventHandler
	def post(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event)
	}

	@Mod.EventHandler
	def serverStart(event: FMLServerStartingEvent): Unit = {
		event.registerServerCommand(new CommandTardis())
	}

	@SubscribeEvent
	def onSave(event: WorldEvent.Save): Unit = {
		event.world match {
			case server: WorldServer =>
				val dir: File = server.getSaveHandler.getWorldDirectory
				if (dir != null) {
					// TODO put this in the Json Origin helper class
					val archObj: JsonObject = new JsonObject

					val dimArray: JsonArray = new JsonArray
					Scala.foreach(TardisManager.registeredDims, (index: Int, value: Integer) => {
						dimArray.add(new JsonPrimitive(value))
					})

					archObj.add("TardisDimIDs", dimArray)

					Files.write(
						Json.toReadableString(new Gson().toJson(archObj)).getBytes,
						new File(dir, "tardis.json")
					)

					TardisManager.registerDimensions(isRegistering = false)
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
					Scala.foreach(dimArray.iterator(), (index: Int, element: JsonElement) => {
						val did = TardisManager.registeredDims.add(element.getAsInt)
					})

					TardisManager.registerDimensions(isRegistering = true)
				}
			case _ =>
		}
	}

	// TODO move to blockregister
	def register(id: String, clazz: Class[_ <: TileEntity]): Unit =
		GameRegistry.registerTileEntity(clazz, id)

}
