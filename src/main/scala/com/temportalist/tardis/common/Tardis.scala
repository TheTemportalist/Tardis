package com.temportalist.tardis.common

import com.temportalist.origin.library.common.Origin
import com.temportalist.origin.library.common.helpers.RegisterHelper
import com.temportalist.origin.wrapper.common.item.ItemWrapper
import com.temportalist.origin.wrapper.common.{ModWrapper, ProxyWrapper}
import com.temportalist.tardis.common.block.BlockConsole
import com.temportalist.tardis.common.item.ItemPlacer
import com.temportalist.tardis.common.tile.TEConsole
import com.temportalist.tardis.server.CommandTardis
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.fml.common.event._
import net.minecraftforge.fml.common.registry.{GameRegistry, EntityRegistry}
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
	final val clientProxy = "com.temportalist.tardis.client.ProxyClient"
	final val serverProxy = "com.temportalist.tardis.server.ProxyServer"

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: ProxyWrapper = null

	var tardis: ItemWrapper = null
	var console: BlockConsole = null

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

		RegisterHelper.registerPacketHandler(this.MODID, classOf[PacketTardisController],
			classOf[PacketTardisMover]
		)

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

}
