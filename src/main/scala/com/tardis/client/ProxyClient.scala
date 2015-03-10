package com.tardis.client

import java.util

import com.tardis.client.model.ModelTardis
import com.tardis.client.render.{RenderTardis, RenderTardisDoor}
import com.tardis.common.tile.TEDoor
import com.tardis.common.{EntityTardis, PlayerTardis, ProxyCommon}
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.settings.KeyBinding
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.fml.client.IModGuiFactory
import net.minecraftforge.fml.client.IModGuiFactory.{RuntimeOptionCategoryElement, RuntimeOptionGuiHandler}
import net.minecraftforge.fml.client.registry.{ClientRegistry, RenderingRegistry}
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent

/**
 *
 *
 * @author TheTemportalist
 */
class ProxyClient() extends ProxyCommon with IModGuiFactory {

	override def registerRender(): Unit = {
		RenderingRegistry.registerEntityRenderingHandler(classOf[EntityTardis],
			new RenderTardis(new ModelTardis())
		)
		ClientRegistry.bindTileEntitySpecialRenderer(classOf[TEDoor], new RenderTardisDoor())
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

	@SubscribeEvent
	def keyEvent(event: InputEvent.KeyInputEvent): Unit = {
		//this.removeKeys()
	}

	@SubscribeEvent
	def mouseEvent(event: InputEvent.MouseInputEvent): Unit = {
		//this.removeKeys()
	}

	private def removeKeys(): Unit = {
		val player: EntityPlayerSP = Minecraft.getMinecraft.thePlayer
		val pt: PlayerTardis = PlayerTardis.get(player)
		if (pt.hasTardisToControl()) KeyBinding.unPressAllKeys()
	}

}
