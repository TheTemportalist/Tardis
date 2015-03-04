package com.tardis.common

import com.tardis.common.block.{BlockConsole, BlockTardisDoor}
import com.tardis.common.dimensions.TardisManager
import com.tardis.common.item.ItemPlacer
import com.tardis.common.network.{PacketDimensionRegistration, PacketMoveTardis}
import com.tardis.common.tile.{TEConsole, TEDoor}
import com.tardis.server.CommandTardis
import com.temportalist.origin.api.IProxy
import com.temportalist.origin.library.common.Origin
import com.temportalist.origin.library.common.handlers.RegisterHelper
import com.temportalist.origin.wrapper.common.ModWrapper
import com.temportalist.origin.wrapper.common.item.ItemWrapper
import net.minecraft.entity.Entity
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.ForgeChunkManager
import net.minecraftforge.fml.common.event._
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
				// todo this causes lag when spawning a tardis
				//new Thread(new Runnable {
				//	override def run(): Unit = {
						TardisManager.registerTardis(entity.asInstanceOf[EntityTardis])
				//	}
				//}).start()

			}
		}
		Origin.addItemToTab(this.tardis)

		this.register("console", classOf[TEConsole])
		this.console = new BlockConsole("console")
		Origin.addBlockToTab(this.console)

		this.register("door", classOf[TEDoor])
		this.tDoor = new BlockTardisDoor("tardis_door")
		Origin.addBlockToTab(this.tDoor)

		RegisterHelper.registerPacketHandler(this.MODID, classOf[PacketMoveTardis],
			classOf[PacketDimensionRegistration]
		)

		TardisManager.registerProviderType()

		ForgeChunkManager.setForcedChunkLoadingCallback(this, TardisChunkCallback)

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
		TardisManager.registerDimsFromDir(event.getServer.worldServerForDimension(0).
				getSaveHandler.getMapFileFromName("dummy").getParentFile
		)
	}

	@Mod.EventHandler
	def serverStop(event: FMLServerStoppedEvent): Unit = {
		dimensions.TardisManager.unregisterDims()
	}

	// TODO move to blockregister
	def register(id: String, clazz: Class[_ <: TileEntity]): Unit =
		GameRegistry.registerTileEntity(clazz, id)

}
