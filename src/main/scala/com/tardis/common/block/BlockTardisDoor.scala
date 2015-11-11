package com.tardis.common.block

import java.util

import com.tardis.common.dimensions.TardisManager
import com.tardis.common.item.ItemTDoor
import com.tardis.common.tile.TEDoor
import com.tardis.common.{EntityTardis, Tardis}
import com.temportalist.origin.api.common.block.BlockTile
import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.utility.Generic
import net.minecraft.block.Block
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.world.World
import net.minecraftforge.common.util.ForgeDirection

/**
 *
 *
 * @author TheTemportalist
 */
class BlockTardisDoor(n: String) extends BlockTile(
	Tardis.MODID, n, classOf[ItemTDoor], classOf[TEDoor]
) {

	override def isOpaqueCube: Boolean = false

	override def onBlockActivated(worldIn: World, x: Int, y: Int, z: Int, player: EntityPlayer,
			side: Int, subX: Float, subY: Float, subZ: Float): Boolean = {
		if (player.isSneaking) {
			TardisManager.movePlayerOutOfTardis(player)
		}
		else {
			this.cycleOpen_Tall(worldIn, new V3O(x, y, z))
			// if these sounds are inverted, then move above the cycleOpen_Tall or swap the ints
			worldIn.playAuxSFXAtEntity(
				player,
				if (this.isOpen(worldIn.getBlockMetadata(x, y, z))) 1003 else 1006,
				x, y, z, 0
			)
			TardisManager.getDimData(worldIn).getTardis.setDoorOpen(
				this.isOpen(new V3O(x, y, z).getBlockMeta(worldIn))
			)
		}
		true
	}

	def cycleOpen_Tall(world: World, pos: V3O): Unit = {
		if (world == null) return
		val min: Int = -this.getHalf(pos.getBlockMeta(world))
		for (h: Int <- min to 1 + min) this.cycleOpen(world, pos + new V3O(0, h, 0))
	}

	def cycleOpen(world: World, pos: V3O): Unit = {
		if (world == null) return
		pos.setBlockMeta(world, this.cycleOpen(pos.getBlockMeta(world)), 3)
	}

	def cycleOpen(meta: Int): Int = if ((meta & 1) > 0) meta & ~1 else meta | 1

	def getMeta(dir: ForgeDirection, open: Boolean, half: Boolean): Unit = {
		(if (open) 1 else 0) | ((if (half) 1 else 0) << 1) |
				((dir.ordinal() - 2) << 2)
	}

	def isOpen(meta: Int): Boolean = (meta & 1) > 0

	def getHalf(meta: Int): Int = (meta & 2) / 2

	def setHalf(meta: Int, isUpper: Boolean): Int =
		if ((meta & 2) > 0) {
			if (!isUpper) meta & ~2
			else meta
		}
		else {
			if (isUpper) meta | 2
			else meta
		}

	def getFacing(meta: Int): ForgeDirection = ForgeDirection.getOrientation(
		(meta >> 2) + 2
	)

	def setFacing(meta: Int, dir: ForgeDirection): Int = {
		val facing: Int = (meta >> 2) + 2
		val unset: Int = meta & ~((facing - 2) << 2)
		unset | ((dir.ordinal() - 2) << 2)
	}

	override def onNeighborBlockChange(worldIn: World, x: Int, y: Int, z: Int,
			neighbor: Block): Unit = {
		val meta: Int = worldIn.getBlockMetadata(x, y, z)
		this.getHalf(meta) match {
			case 0 => // lower
				if (new V3O(x, y, z).up().getBlock(worldIn) != this) {
					worldIn.setBlockToAir(x, y, z)
					this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(this))
				}
			case 1 => // upper
				if (new V3O(x, y, z).down().getBlock(worldIn) != this) {
					worldIn.setBlockToAir(x, y, z)
				}
			case _ =>
		}
	}

	override def canPlaceBlockAt(world: World, x: Int, y: Int, z: Int): Boolean = {
		y < world.getHeight - 1 && super.canPlaceBlockAt(world, x, y, z) &&
				super.canPlaceBlockAt(world, x, y + 1, z)
	}

	override def getMobilityFlag: Int = 1

	/*
	override def onBlockHarvested(world: World, x: Int, y: Int, z: Int, meta: Int,
			player: EntityPlayer): Unit = {

	}

	override def onBlockHarvested(worldIn: World, pos: BlockPos, state: IBlockState,
			player: EntityPlayer): Unit = {
		val lPos: BlockPos = pos.down
		if (player.capabilities.isCreativeMode &&
				state.getValue(HALF) == EnumDoorHalf.UPPER &&
				worldIn.getBlockState(lPos).getBlock == this) {
			worldIn.setBlockToAir(lPos)
		}
	}
	*/

	override def addCollisionBoxesToList(world: World, x: Int, y: Int, z: Int, mask: AxisAlignedBB,
			list: util.List[_], entity: Entity): Unit = {
		// todo this needs to be optimized
		val pixel: Double = 0.0625D
		val iPixel: Double = 1 - pixel
		val west: AxisAlignedBB = AxisAlignedBB.getBoundingBox(
			0, pixel, 0, pixel, 1, 1)
		val east: AxisAlignedBB = AxisAlignedBB.getBoundingBox(
			iPixel - pixel, pixel, pixel, iPixel, 1, iPixel)
		val north: AxisAlignedBB = AxisAlignedBB.getBoundingBox(
			0, pixel, 0, iPixel, 1, pixel)
		val south: AxisAlignedBB = AxisAlignedBB.getBoundingBox(
			0, pixel, iPixel, iPixel, 1, 1)
		val aabbs: Map[Int, Array[AxisAlignedBB]] = Map[Int, Array[AxisAlignedBB]](
			0 -> Array[AxisAlignedBB](
				AxisAlignedBB.getBoundingBox(0, 0, 0, 1, pixel, 1),
				west, north, south, east
			),
			1 -> Array[AxisAlignedBB](
				AxisAlignedBB.getBoundingBox(0, iPixel, 0, 1, 1, 1),
				west, north, south, east
			)
		)
		val removed: Map[ForgeDirection, Int] = Map[ForgeDirection, Int](
			ForgeDirection.NORTH -> 2,
			ForgeDirection.EAST -> 4,
			ForgeDirection.SOUTH -> 3,
			ForgeDirection.WEST -> 1
		)

		val meta: Int = world.getBlockMetadata(x, y, z)
		val half: Int = this.getHalf(meta)
		val isOpen: Boolean = this.isOpen(meta) && !entity.isInstanceOf[EntityTardis]
		val facing: ForgeDirection = this.getFacing(meta)
		val index: Int = if (isOpen && removed.contains(facing)) removed.get(facing).get else -1
		for (i <- 0 until aabbs(half).length) {
			var aabb: AxisAlignedBB = aabbs(half)(i)
			aabb = AxisAlignedBB.getBoundingBox(
				x + aabb.minX,
				y + aabb.minY,
				z + aabb.minZ,
				x + aabb.maxX,
				y + aabb.maxY,
				z + aabb.maxZ
			)
			if (i != index && mask.intersectsWith(aabb)) Generic.addToList(list, aabb)
		}
	}

	/*
	override def addCollisionBoxesToList(worldIn: World, pos: BlockPos, state: IBlockState,
			mask: AxisAlignedBB, list: util.List[_], collidingEntity: Entity): Unit = {

		// todo optimize this

		val pixel: Double = 0.0625D
		val iPixel: Double = 1 - pixel
		val west: AxisAlignedBB = AxisAlignedBB.fromBounds(0, pixel, 0, pixel, 1, 1)
		val east: AxisAlignedBB = AxisAlignedBB
				.fromBounds(iPixel - pixel, pixel, pixel, iPixel, 1, iPixel)
		val north: AxisAlignedBB = AxisAlignedBB.fromBounds(0, pixel, 0, iPixel, 1, pixel)
		val south: AxisAlignedBB = AxisAlignedBB.fromBounds(0, pixel, iPixel, iPixel, 1, 1)
		val aabbs: Map[EnumDoorHalf, Array[AxisAlignedBB]] = Map[EnumDoorHalf, Array[AxisAlignedBB]](
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
	*/

	override def onEntityCollidedWithBlock(world: World, x: Int, y: Int, z: Int,
			entity: Entity): Unit = {
		println("collide")
		if (this.isOpen(world.getBlockMetadata(x, y, z)))
		entity match {
			case player: EntityPlayer =>
				if (!world.isRemote) TardisManager.movePlayerOutOfTardis(player)
			case _ =>
		}
	}

}
