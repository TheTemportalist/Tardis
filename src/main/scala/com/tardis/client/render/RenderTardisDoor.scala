package com.tardis.client.render

import com.tardis.client.model.ModelDoor
import com.tardis.common.Tardis
import com.tardis.common.init.TardisBlocks
import com.tardis.common.tile.TEDoor
import com.temportalist.origin.api.client.render.TERenderItem
import cpw.mods.fml.relauncher.{Side, SideOnly}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.util.ForgeDirection
import org.lwjgl.opengl.GL11

/**
 *
 *
 * @author TheTemportalist
 */
@SideOnly(Side.CLIENT)
class RenderTardisDoor() extends TERenderItem(
	new ResourceLocation(Tardis.MODID, "textures/blocks/tardis_door2.png")
) {

	val dummy: TEDoor = new TEDoor

	override protected def render(tileEntity: TileEntity, renderPartialTicks: Float,
			f5: Float): Unit = {

		val model: ModelDoor = new ModelDoor
		if (tileEntity.getWorldObj != null)
			model.render(tileEntity)
		else {
			GL11.glScaled(0.75, 0.75, 0.75)
			val meta: Int = TardisBlocks.tDoor.setFacing(0, ForgeDirection.EAST)
			model.render(TardisBlocks.tDoor.setHalf(meta, false))
			GL11.glTranslated(0, 1, 0)
			model.render(TardisBlocks.tDoor.setHalf(meta, true))
		}

	}

	override def getRenderingTileItem(): TileEntity = this.dummy

}
