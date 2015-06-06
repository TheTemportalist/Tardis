package com.tardis.client

import java.util

import com.tardis.client.model.ModelTardis
import com.tardis.client.render.{RenderConsole, RenderTardis, RenderTardisDoor}
import com.tardis.common.init.TardisBlocks
import com.tardis.common.tile.{TEConsole, TEDoor}
import com.tardis.common.{EntityTardis, ProxyCommon}
import com.temportalist.origin.api.client.utility.Rendering
import cpw.mods.fml.client.IModGuiFactory
import cpw.mods.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class ProxyClient() extends ProxyCommon with IModGuiFactory {

	override def registerRender(): Unit = {
		Rendering.registerRender(classOf[EntityTardis], new RenderTardis(new ModelTardis()))
		val rtd: RenderTardisDoor = new RenderTardisDoor
		Rendering.registerRender(classOf[TEDoor], rtd)
		Rendering.registerRender(TardisBlocks.tDoor, rtd)
		val rc: RenderConsole = new RenderConsole
		Rendering.registerRender(classOf[TEConsole], rc)
		Rendering.registerRender(TardisBlocks.console, rc)
	}

	override def getClientElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int,
			z: Int, tileEntity: TileEntity): AnyRef = {
		null
	}

	override def initialize(minecraftInstance: Minecraft): Unit = {}

	override def runtimeGuiCategories(): util.Set[RuntimeOptionCategoryElement] = {
		null
	}

	override def getHandlerFor(element: RuntimeOptionCategoryElement): RuntimeOptionGuiHandler = {
		null
	}

	override def mainConfigGuiClass(): Class[_ <: GuiScreen] = {
		null
	}

}
