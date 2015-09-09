package com.tardis.client.render

import com.tardis.client.model.ModelTardis
import com.tardis.common.{EntityTardis, Tardis}
import com.tardis.lookingglass.LookingGlass
import com.temportalist.origin.api.client.utility.{TessRenderer, Rendering}
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.entity.RenderEntity
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
class RenderTardis(model: ModelBase) extends RenderEntity() {

	this.shadowSize = 0.0F

	override def doRender(entity: Entity, viewX: Double, viewY: Double, viewZ: Double,
			f1: Float, f2: Float): Unit = {
		{
			Rendering.Gl.push()
			this.bindEntityTexture(entity)
			GL11.glTranslated(viewX, viewY, viewZ)
			//this.model.render(entity, 0, 0, 0, 0, 0, 0.0625F)
			new ModelTardis().render(entity, 0, 0, 0, 0, 0, 0.0625F)

			Rendering.Gl.push()
			entity match {
				case tardis: EntityTardis =>
					//println("tardis ent")
					val worldView = tardis.getWorldView
					if (worldView == null)
						LookingGlass.addWindow(tardis)
					if (worldView != null && worldView.isReady) {
						/*
						worldView.getCamera.setPitch(180)
						worldView.getCamera.setYaw(0)
						worldView.getCamera.setLocation(
							0,
							10,
							0
						)
						*/
						//worldView.getCamera.setPitch(180)
						//println(worldView.getCamera.getPitch)
						Rendering.Gl.enable(GL11.GL_ALPHA_TEST, isOn = false)
						GL11.glBindTexture(GL11.GL_TEXTURE_2D, worldView.getTexture)
						TessRenderer.startQuads()
						/*
						TessRenderer.addVertex(-0.5, 0, 0.45, 0, 1)
						TessRenderer.addVertex(+0.5, 0, 0.45, 1, 1)
						TessRenderer.addVertex(+0.5, 2, 0.45, 1, 0)
						TessRenderer.addVertex(-0.5, 2, 0.45, 0, 0)
						*/
						TessRenderer.addVertex(0, 0, 2, 1, 0)
						TessRenderer.addVertex(2, 0, 2, 1, 1)
						TessRenderer.addVertex(2, 4, 2, 0, 1)
						TessRenderer.addVertex(0, 4, 2, 0, 0)

						TessRenderer.addVertex(0, 0, 3, 0, 1)
						TessRenderer.addVertex(4, 0, 3, 0, 0)
						TessRenderer.addVertex(4, 2, 3, 1, 0)
						TessRenderer.addVertex(0, 2, 3, 1, 1)

						TessRenderer.draw()
						Rendering.Gl.enable(GL11.GL_ALPHA_TEST, isOn = true)
					}
				case _ =>
			}
			Rendering.Gl.pop()

			Rendering.Gl.pop()

		}
	}

	override def getEntityTexture(entity: Entity): ResourceLocation = {
		new ResourceLocation(Tardis.MODID, "textures/entity/tardis.png")
	}

}
