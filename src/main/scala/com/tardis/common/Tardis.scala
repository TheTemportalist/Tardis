package com.tardis.common

import com.tardis.common.block.{BlockConsole, BlockTardisDoor}
import com.tardis.common.dimensions.DimManager
import com.tardis.common.item.ItemPlacer
import com.tardis.common.tile.{TEConsole, TEDoor}
import com.tardis.server.CommandTardis
import com.temportalist.origin.library.common.Origin
import com.temportalist.origin.library.common.helpers.RegisterHelper
import com.temportalist.origin.wrapper.common.item.ItemWrapper
import com.temportalist.origin.wrapper.common.{ModWrapper, ProxyWrapper}
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.event.entity.EntityJoinWorldEvent
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
	var proxy: ProxyWrapper = null

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
		this.tardis = new ItemPlacer(this.MODID, "tardis", classOf[EntityTardis])
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

	// TODO move to blockregister
	def register(id: String, clazz: Class[_ <: TileEntity]): Unit =
		GameRegistry.registerTileEntity(clazz, id)

	@SubscribeEvent
	def onJoinWorld(event: EntityJoinWorldEvent): Unit = {
		event.entity match {
			case tardis: EntityTardis =>
				TardisManager.registerTardis(tardis)
			case _ =>
		}
	}

}
