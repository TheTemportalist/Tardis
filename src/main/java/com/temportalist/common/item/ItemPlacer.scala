package com.temportalist.common.item

import com.temportalist.origin.library.common.lib.vec.Vector3O
import com.temportalist.origin.wrapper.common.item.ItemWrapper
import net.minecraft.block.state.IBlockState
import net.minecraft.block.{BlockFence, BlockLiquid}
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util._
import net.minecraft.world.World

/**
 *
 *
 * @author TheTemportalist
 */
class ItemPlacer(id: String, n: String, private var entClass: Class[_ <: Entity])
		extends ItemWrapper(id, n) {

	override def onItemRightClick(
			itemStack: ItemStack, world: World, player: EntityPlayer): ItemStack = {
		if (world.isRemote || itemStack == null) return itemStack

		val mop: MovingObjectPosition = this.getMovingObjectPositionFromPlayer(
			world, player, true
		)

		if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
				world.getBlockState(mop.func_178782_a()).getBlock.isInstanceOf[BlockLiquid]) {
			val useStack: ItemStack = itemStack.copy()
			val offsets: Vector3O = new Vector3O(mop.hitVec) - new Vector3O(mop.func_178782_a())
			if (this.onItemUse(
				useStack, player, world, mop.func_178782_a(), mop.field_178784_b,
				offsets.x_f(), offsets.y_f(), offsets.z_f()
			)) {
				return useStack
			}
		}

		itemStack
	}

	override def onItemUse(stack: ItemStack, playerIn: EntityPlayer, worldIn: World, pos: BlockPos,
			side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
		if (worldIn.isRemote) return true
		if (!playerIn.func_175151_a(pos.offset(side), side, stack)) return false

		val state: IBlockState = worldIn.getBlockState(pos)
		val posVec: Vector3O = new Vector3O(pos)

		/*
		if (state.getBlock == Blocks.mob_spawner) {
			val tile: TileEntity = posVec.getTile(worldIn)
			tile match {
				case spawner: TileEntityMobSpawner =>
					val logic: MobSpawnerBaseLogic = spawner.getSpawnerBaseLogic
					if (this.entName == null) this.fetchEntityName()
					logic.setEntityName(this.entName)
					tile.markDirty()
					// change this to posVec.markForUpdate(worldIn)
					posVec.toBlockCoord(worldIn).markForUpdate()

					if (!playerIn.capabilities.isCreativeMode) stack.stackSize -= 1

					return true
				case _ =>
			}
		}
		*/

		posVec.add(side)

		posVec.add(new Vector3O(0.5, 0, 0.5))
		if (side == EnumFacing.UP && state.getBlock.isInstanceOf[BlockFence])
			posVec.add(0, 0.5, 0)

		val facing: Int = MathHelper.floor_double(((playerIn.rotationYaw * 4F) / 360F) + 0.5D) & 3
		var rotZ: Float = facing * 90
		if (playerIn.isSneaking) rotZ += 180

		val entity: Entity = ItemPlacer.createEntity(this.entClass, worldIn, posVec, rotZ)

		this.playSummonSound(posVec.toBlockCoord(worldIn))

		if (stack.hasDisplayName) entity.setCustomNameTag(stack.getDisplayName)
		if (!playerIn.capabilities.isCreativeMode) stack.stackSize -= 1

		true
	}

	def playSummonSound(pos: BlockPos): Unit = {}

}

object ItemPlacer {

	def createEntity(entClass: Class[_ <: Entity], world: World, pos: Vector3O,
			rotZ: Float): Entity = {
		var entity: Entity = null
		try {
			if (entClass != null) {
				entity = entClass.getConstructor(classOf[World]).newInstance(world)

				entity.setLocationAndAngles(pos.x, pos.y, pos.z, rotZ, 0.0F)
				world.spawnEntityInWorld(entity)
			}
		}
		catch {
			case e: Exception =>
				e.printStackTrace()
		}
		entity
	}

}
