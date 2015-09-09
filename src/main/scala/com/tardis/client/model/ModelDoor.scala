package com.tardis.client.model

import com.tardis.common.init.TardisBlocks
import com.temportalist.origin.api.client.render.Model
import com.temportalist.origin.api.common.lib.V3O
import net.minecraft.client.model.ModelRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.util.ForgeDirection

/**
 *
 *
 * @author TheTemportalist
 */
class ModelDoor() extends Model(128, 128) {

	// Lower Half things
	var base: ModelRenderer = null
	var b_box: ModelRenderer = null
	var b_doorL: ModelRenderer = null
	var b_doorR: ModelRenderer = null

	// Upper Half things
	var top: ModelRenderer = null
	var t_box: ModelRenderer = null
	var t_doorL: ModelRenderer = null
	var t_doorR: ModelRenderer = null

	this.init()

	def init() {

		this.base = this.createModel(
			new V3O(0, -8, 0),
			new V3O(8, 0, 8),
			new V3O(16, 1, 16),
			V3O.ZERO, 0, 77
		)

		this.b_box = this.createModel(
			new V3O(0, 1, 0),
			new V3O(8, 0, 7),
			new V3O(16, 15, 15),
			V3O.ZERO, 0, 47
		)
		this.add(this.base, this.b_box)

		this.b_doorR = this.createModel(
			new V3O(7, 8, -7),
			new V3O(7, 7, 0),
			new V3O(7, 15, 1),
			V3O.ZERO, 80, 16
		)
		this.add(this.base, this.b_doorR)

		this.b_doorL = this.createModel(
			new V3O(-7, 8, -7),
			new V3O(0, 7, 0),
			new V3O(7, 15, 1),
			V3O.ZERO, 64, 16
		)
		this.add(this.base, this.b_doorL)

		this.top = this.createModel(
			new V3O(0, 7, 0),
			new V3O(8, 0, 8),
			new V3O(16, 1, 16),
			V3O.ZERO, 0, 0
		)

		this.t_box = this.createModel(
			new V3O(0, -15, 0),
			new V3O(8, 0, 7),
			new V3O(16, 15, 15),
			V3O.ZERO, 0, 17
		)
		this.add(this.top, this.t_box)

		this.t_doorR = this.createModel(
			new V3O(7, -8, -7),
			new V3O(7, 7, 0),
			new V3O(7, 15, 1),
			V3O.ZERO, 80, 0
		)
		this.add(this.top, this.t_doorR)

		this.t_doorL = this.createModel(
			new V3O(-7, -8, -7),
			new V3O(0, 7, 0),
			new V3O(7, 15, 1),
			V3O.ZERO, 64, 0
		)
		this.add(this.top, this.t_doorL)

	}

	override def render(te: TileEntity): Unit = this.render(te.getBlockMetadata)

	def render(meta: Int): Unit = {
		val facing: ForgeDirection = TardisBlocks.tDoor.getFacing(meta)

		val yRot: Float = Math.toRadians(
			facing match {
				case ForgeDirection.NORTH => 0
				case ForgeDirection.WEST => 90
				case ForgeDirection.SOUTH => 180
				case ForgeDirection.EAST => 270
				case _ => 0
			}
		).toFloat
		//Math.toRadians((facing.getIndex - 2) * 0).toFloat
		val doorRot: Float = Math.toRadians(
			// todo move this to a constant somewhere
			if (TardisBlocks.tDoor.isOpen(meta)) 80
			else 0
		).toFloat

		//println (state.getValue(BlockDoor.HALF) + "|" + te.getBlockMetadata)

		TardisBlocks.tDoor.getHalf(meta) match {
			case 0 =>
				this.base.rotateAngleY = yRot
				this.b_doorL.rotateAngleY = -doorRot
				this.b_doorR.rotateAngleY = doorRot
				this.base.render(Model.f5)
			case 1 =>
				this.top.rotateAngleY = yRot
				this.t_doorL.rotateAngleY = -doorRot
				this.t_doorR.rotateAngleY = doorRot
				this.top.render(Model.f5)
			case _ =>
		}
	}

}
