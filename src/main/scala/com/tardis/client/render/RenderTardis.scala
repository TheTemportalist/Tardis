package com.tardis.client.render

import com.tardis.client.model.ModelTardis
import com.tardis.common.Tardis
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
			org.lwjgl.opengl.GL11.glPushMatrix()
			this.bindEntityTexture(entity)
			GL11.glTranslated(viewX, viewY, viewZ)
			//this.model.render(entity, 0, 0, 0, 0, 0, 0.0625F)
			new ModelTardis().render(entity, 0, 0, 0, 0, 0, 0.0625F)
			org.lwjgl.opengl.GL11.glPopMatrix()
		}
	}

	override def getEntityTexture(entity: Entity): ResourceLocation = {
		new ResourceLocation(Tardis.MODID, "textures/entity/tardis.png")
	}

}
