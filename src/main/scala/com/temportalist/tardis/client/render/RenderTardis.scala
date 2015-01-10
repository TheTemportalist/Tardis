package com.temportalist.tardis.client.render

import com.temportalist.tardis.client.model.ModelTardis
import com.temportalist.tardis.common.Tardis
import net.minecraft.client.Minecraft
import net.minecraft.client.model.ModelBase
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.entity.RenderEntity
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation

/**
 *
 *
 * @author TheTemportalist
 */
class RenderTardis(model: ModelBase) extends RenderEntity(Minecraft.getMinecraft.getRenderManager) {

	this.shadowSize = 0.0F

	override def doRender(entity: Entity, viewX: Double, viewY: Double, viewZ: Double,
			f1: Float, f2: Float): Unit = {
		GlStateManager.pushMatrix()
		this.bindEntityTexture(entity)
		GlStateManager.translate(viewX, viewY, viewZ)


		//this.model.render(entity, 0, 0, 0, 0, 0, 0.0625F)
		new ModelTardis().render(entity, 0, 0, 0, 0, 0, 0.0625F)

		GlStateManager.popMatrix()
	}

	override def getEntityTexture(entity: Entity): ResourceLocation = {
		new ResourceLocation(Tardis.MODID, "textures/entity/tardis.png")
	}

}
