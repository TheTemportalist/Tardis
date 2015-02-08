package com.tardis.common.block

import java.util.Random

import com.tardis.common.Tardis
import com.tardis.common.item.ItemTDoor
import com.tardis.common.tile.TEDoor
import com.temportalist.origin.wrapper.common.block.BlockWrapperTE
import net.minecraft.block.BlockDoor._
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.block.{Block, BlockDoor}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.util.{BlockPos, EnumFacing, EnumWorldBlockLayer}
import net.minecraft.world.{IBlockAccess, World}
import net.minecraftforge.fml.relauncher.{Side, SideOnly}

/**
 *
 *
 * @author TheTemportalist
 */
class BlockTardisDoor(n: String) extends BlockWrapperTE(
	Tardis.MODID, n, classOf[ItemTDoor], classOf[TEDoor]
) {

	this.setDefaultState(
		this.blockState.getBaseState
				.withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.OPEN, false)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
	)

	override def isOpaqueCube: Boolean = false

	override def isFullCube: Boolean = false

	override def onBlockActivated(worldIn: World, pos: BlockPos, stateIn: IBlockState,
			playerIn: EntityPlayer, side: EnumFacing, hitX: Float, hitY: Float,
			hitZ: Float): Boolean = {
		val lPos: BlockPos = this.getLowerPos(pos, stateIn)
		val lState: IBlockState = this.getLowerState(worldIn, pos, stateIn)
		var state: IBlockState = null
		if (lState.getBlock == this) {
			state = lState.cycleProperty(OPEN)
			worldIn.setBlockState(lPos, state, 2)
			worldIn.markBlockRangeForRenderUpdate(lPos, pos)
			worldIn.playAuxSFXAtEntity(
				playerIn,
				if (state.getValue(OPEN).asInstanceOf[Boolean].booleanValue) 1003 else 1006,
				pos, 0
			)
			return true
		}
		false
	}

	override def onNeighborBlockChange(
			worldIn: World, pos: BlockPos, state: IBlockState, neighborBlock: Block
			): Unit = {
		// Find out which part this block is
		state.getValue(HALF) match {
			case EnumDoorHalf.UPPER =>
				// get the pos and state below
				val lPos: BlockPos = pos.down
				val lState: IBlockState = worldIn.getBlockState(lPos)

				// make sure they are not just "gone"
				if (lState.getBlock != this) {
					worldIn.setBlockToAir(pos)
				}
				else if (neighborBlock != this) {
					this.onNeighborBlockChange(worldIn, lPos, lState, neighborBlock)
				}
			case EnumDoorHalf.LOWER =>
				var shouldDrop: Boolean = false
				// get the pos and state above
				val uPos: BlockPos = pos.up
				val uState: IBlockState = worldIn.getBlockState(uPos)

				// If the uppor is just "gone", then drop
				if (uState.getBlock != this) {
					worldIn.setBlockToAir(pos)
					shouldDrop = true
				}

				if (shouldDrop && !worldIn.isRemote) {
					this.dropBlockAsItem(worldIn, pos, state, 0)
				}
			case _ =>
		}
	}

	override def getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item = {
		if (state.getValue(HALF) == EnumDoorHalf.UPPER) null else this.getItem
	}

	override def canPlaceBlockAt(worldIn: World, pos: BlockPos): Boolean = {
		(pos.getY < worldIn.getHeight - 1) && super.canPlaceBlockAt(worldIn, pos) &&
				super.canPlaceBlockAt(worldIn, pos.up())
	}

	override def getMobilityFlag: Int = 1

	override def onBlockHarvested(worldIn: World, pos: BlockPos, state: IBlockState,
			player: EntityPlayer): Unit = {
		val lPos: BlockPos = pos.down
		if (player.capabilities.isCreativeMode &&
				state.getValue(HALF) == EnumDoorHalf.UPPER &&
				worldIn.getBlockState(lPos).getBlock == this) {
			worldIn.setBlockToAir(lPos)
		}
	}

	override def getActualState(stateIn: IBlockState, worldIn: IBlockAccess,
			pos: BlockPos): IBlockState = {
		stateIn.getValue(HALF) match {
			case EnumDoorHalf.UPPER =>
				val oState: IBlockState = worldIn.getBlockState(pos.down)
				if (oState.getBlock == this) {
					return stateIn.withProperty(FACING, oState.getValue(FACING))
							.withProperty(OPEN, oState.getValue(OPEN))
				}
			case _ =>
		}
		stateIn
	}

	override def getStateFromMeta(meta: Int): IBlockState = {
		if ((meta & 8) > 0) {
			this.getDefaultState.withProperty(HALF, EnumDoorHalf.UPPER)
		}
		else {
			this.getDefaultState.
					withProperty(HALF, EnumDoorHalf.LOWER).
					withProperty(FACING, EnumFacing.getHorizontal(meta & 3).rotateYCCW()).
					withProperty(OPEN, java.lang.Boolean.valueOf((meta & 4) > 0))
		}
	}

	@SideOnly(Side.CLIENT)
	override def getBlockLayer: EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT

	override def getMetaFromState(state: IBlockState): Int = {
		val b0: Byte = 0
		var meta: Int = 0
		if (state.getValue(HALF) == EnumDoorHalf.UPPER) {
			meta = b0 | 8
		}
		else {
			meta = b0 | state.getValue(FACING).asInstanceOf[EnumFacing].rotateY.getHorizontalIndex
			if (state.getValue(OPEN).asInstanceOf[Boolean].booleanValue) {
				meta |= 4
			}
		}
		meta
	}

	override def createBlockState(): BlockState = new BlockState(this, HALF, FACING, OPEN)

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	def getLowerPos(pos: BlockPos, state: IBlockState): BlockPos = {
		if (state.getValue(HALF) == EnumDoorHalf.LOWER) pos
		else pos.down()
	}

	def getLowerState(world: World, pos: BlockPos, state: IBlockState): IBlockState = {
		if (state.getValue(HALF) == EnumDoorHalf.LOWER)
			state
		else
			world.getBlockState(pos.down())
	}

}
