package com.tardis.client.model

import com.tardis.common.block.BlockTardisDoor
import com.temportalist.origin.library.common.lib.vec.V3O
import com.temportalist.origin.wrapper.client.render.model.ModelWrapper
import net.minecraft.block.BlockDoor
import net.minecraft.block.state.IBlockState
import net.minecraft.client.model.ModelRenderer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing

/**
 *
 *
 * @author TheTemportalist
 */
class ModelDoor() extends ModelWrapper(128, 128) {

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

	override def render(te: TileEntity): Unit = {
		// todo move to WorldHelper
		val state: IBlockState = new V3O (te).getBlockState(te.getWorld)

		te.getBlockType match {
			case _: BlockTardisDoor =>

				val facing: EnumFacing = state.getValue(BlockDoor.FACING).asInstanceOf[EnumFacing]

				val yRot: Float = Math.toRadians(
					facing match {
						case EnumFacing.NORTH => 0
						case EnumFacing.WEST => 90
						case EnumFacing.SOUTH => 180
						case EnumFacing.EAST => 270
						case _ => 0
					}
				).toFloat
				//Math.toRadians((facing.getIndex - 2) * 0).toFloat
				val doorRot: Float = Math.toRadians(
					// todo move this to a constant somewhere
					if (state.getValue(BlockDoor.OPEN).asInstanceOf[Boolean]) 80
					else 0
				).toFloat

				//println (state.getValue(BlockDoor.HALF) + "|" + te.getBlockMetadata)

				state.getValue(BlockDoor.HALF) match {
					case BlockDoor.EnumDoorHalf.LOWER =>
						this.base.rotateAngleY = yRot
						this.b_doorL.rotateAngleY = -doorRot
						this.b_doorR.rotateAngleY = doorRot
						this.base.render(ModelWrapper.f5)
					case BlockDoor.EnumDoorHalf.UPPER =>
						this.top.rotateAngleY = yRot
						this.t_doorL.rotateAngleY = -doorRot
						this.t_doorR.rotateAngleY = doorRot
						this.top.render(ModelWrapper.f5)
					case _ =>
				}

			case _ =>
		}

	}

}
