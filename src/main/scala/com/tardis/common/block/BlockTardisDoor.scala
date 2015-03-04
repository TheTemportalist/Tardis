package com.tardis.common.block

import java.util
import java.util.Random

import com.tardis.common.item.ItemTDoor
import com.tardis.common.tile.TEDoor
import com.tardis.common.{EntityTardis, Tardis}
import com.temportalist.origin.library.common.utility.Generic
import com.temportalist.origin.wrapper.common.block.BlockWrapperTE
import net.minecraft.block.BlockDoor._
import net.minecraft.block.state.{BlockState, IBlockState}
import net.minecraft.block.{Block, BlockDoor}
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.util._
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
		if (!playerIn.isSneaking) {
			worldIn.setBlockState(pos, stateIn.cycleProperty(OPEN), 3)
			val otherPos: BlockPos =
				if (stateIn.getValue(HALF) == EnumDoorHalf.LOWER) pos.up()
				else pos.down()
			val otherState: IBlockState = worldIn.getBlockState(otherPos)
			worldIn.setBlockState(otherPos, otherState.cycleProperty(OPEN), 3)
			worldIn.markBlockRangeForRenderUpdate(otherPos, pos)
			worldIn.playAuxSFXAtEntity(
				playerIn,
				if (stateIn.getValue(OPEN).asInstanceOf[Boolean].booleanValue) 1003 else 1006,
				pos, 0
			)
			return true
		}
		else {
			//TardisManager1.leaveDimension(playerIn)
			return true
		}
		//return false
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

	override def getMetaFromState(state: IBlockState): Int = {
		var meta: Int = 0

		// get the facing. 0-3. SWNE
		val facing: Int = state.getValue(FACING).asInstanceOf[EnumFacing].rotateY()
				.getHorizontalIndex
		// separate in tierms of 4 (meta 0, 4, 8, 12)
		meta |= facing * 4
		// if we are on the top, add 2
		if (state.getValue(HALF) == EnumDoorHalf.UPPER)
			meta += 2
		// if we are open, add 1
		if (state.getValue(OPEN).asInstanceOf[Boolean].booleanValue())
			meta += 1

		meta
	}

	override def getStateFromMeta(meta: Int): IBlockState = {
		val facing: Int = meta / 4
		val remain: Int = meta - (facing * 4)
		val isUpper: Boolean = remain > 1
		val isOpen: Boolean = remain % 2 != 0
		this.getDefaultState.
				withProperty(FACING, EnumFacing.getHorizontal(facing)).
				withProperty(HALF, if (isUpper) EnumDoorHalf.UPPER else EnumDoorHalf.LOWER).
				withProperty(OPEN, Boolean.box(isOpen))
	}

	@SideOnly(Side.CLIENT)
	override def getBlockLayer: EnumWorldBlockLayer = EnumWorldBlockLayer.CUTOUT

	override def createBlockState(): BlockState = new BlockState(this, HALF, FACING, OPEN)

	override def addCollisionBoxesToList(worldIn: World, pos: BlockPos, state: IBlockState,
			mask: AxisAlignedBB, list: util.List[_], collidingEntity: Entity): Unit = {

		// todo optimize this

		val pixel: Double = 0.0625D
		val iPixel: Double = 1 - pixel
		val west: AxisAlignedBB = AxisAlignedBB.fromBounds(0, pixel, 0, pixel, 1, 1)
		val east: AxisAlignedBB = AxisAlignedBB.fromBounds(iPixel - pixel, pixel, pixel, iPixel, 1, iPixel)
		val north: AxisAlignedBB = AxisAlignedBB.fromBounds(0, pixel, 0, iPixel, 1, pixel)
		val south: AxisAlignedBB = AxisAlignedBB.fromBounds(0, pixel, iPixel, iPixel, 1, 1)
		val aabbs: Map[EnumDoorHalf, Array[AxisAlignedBB]] = Map[EnumDoorHalf, Array[AxisAlignedBB]] (
			EnumDoorHalf.LOWER -> Array[AxisAlignedBB](
				AxisAlignedBB.fromBounds(0, 0, 0, 1, pixel, 1),
				west, north, south, east
			),
			EnumDoorHalf.UPPER -> Array[AxisAlignedBB](
				AxisAlignedBB.fromBounds(0, iPixel, 0, 1, 1, 1),
				west, north, south, east
			)
		)
		val removed: Map[EnumFacing, Int] = Map[EnumFacing, Int](
			EnumFacing.NORTH -> 2,
			EnumFacing.EAST -> 4,
			EnumFacing.SOUTH -> 3,
			EnumFacing.WEST -> 1
		)

		// not tardis colliding && is open
		val half: EnumDoorHalf = state.getValue(HALF).asInstanceOf[EnumDoorHalf]
		val isOpen = state.getValue(OPEN).asInstanceOf[Boolean].booleanValue() &&
				!collidingEntity.isInstanceOf[EntityTardis]
		val facing: EnumFacing = state.getValue(FACING).asInstanceOf[EnumFacing]
		val index: Int = if (isOpen && removed.contains(facing)) removed.get(facing).get else -1
		for (i <- 0 until aabbs(half).length) {
			var aabb: AxisAlignedBB = aabbs(half)(i)
			aabb = AxisAlignedBB.fromBounds(
				pos.getX + aabb.minX,
				pos.getY + aabb.minY,
				pos.getZ + aabb.minZ,
				pos.getX + aabb.maxX,
				pos.getY + aabb.maxY,
				pos.getZ + aabb.maxZ
			)
			if (i != index && mask.intersectsWith(aabb)) Generic.addToList(list, aabb)
		}
	}

	/*
	override def onEntityCollidedWithBlock(
			worldIn: World, pos: BlockPos, entity: Entity): Unit = {
		if (!entity.isSneaking && !worldIn.isRemote && entity.isInstanceOf[EntityPlayer]) {
			TardisManager.leaveDimension(entity.asInstanceOf[EntityPlayer])
		}
	}
	*/

}
