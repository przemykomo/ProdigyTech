package lykrast.prodigytech.common.block;

import lykrast.prodigytech.common.capability.CapabilityHotAir;
import lykrast.prodigytech.common.gui.ProdigyTechGuiHandler;
import lykrast.prodigytech.common.item.ItemBlockMachineHotAir;
import lykrast.prodigytech.common.tileentity.TileSolderer;
import lykrast.prodigytech.common.util.TemperatureHelper;
import lykrast.prodigytech.core.ProdigyTech;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSolderer extends BlockGeneric implements ITileEntityProvider, ICustomItemBlock {
	public static final PropertyBool ACTIVE = PropertyBool.create("active");
	
	public BlockSolderer(float hardness, float resistance, int harvestLevel) {
		super(Material.IRON, SoundType.METAL, hardness, resistance, "pickaxe", harvestLevel);
        this.setLightOpacity(0);
		setDefaultState(blockState.getBaseState().withProperty(ACTIVE, Boolean.valueOf(false)));
	}

	public static void setState(boolean active, World worldIn, BlockPos pos) {
		worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(ACTIVE, Boolean.valueOf(active)), 3);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileSolderer();
	}

	private TileSolderer getTileEntity(IBlockAccess world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileSolderer) return (TileSolderer)tile;
		else return null;
	}

	/**
     * Called when the given entity walks on this Block
     */
    @Override
	public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        TemperatureHelper.hotAirDamage(entityIn, getTileEntity(worldIn, pos).getCapability(CapabilityHotAir.HOT_AIR, EnumFacing.UP));

        super.onEntityWalk(worldIn, pos, entityIn);
    }

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
        	TileSolderer tile = getTileEntity(worldIn,pos);

            if (tile != null)
            {
                playerIn.openGui(ProdigyTech.instance, ProdigyTechGuiHandler.SOLDERER, worldIn, pos.getX(), pos.getY(), pos.getZ());
                playerIn.openContainer.detectAndSendChanges();
            }

            return true;
        }
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    @Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
    	TileSolderer tile = getTileEntity(worldIn, pos);

        if (tile != null)
        {
            InventoryHelper.dropInventoryItems(worldIn, pos, tile);
        }
        
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        return getTileEntity(worldIn, pos).getComparatorOutput();
    }

	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(ACTIVE, Boolean.valueOf((meta & 1) > 0));
	}

	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
	    if (((Boolean)state.getValue(ACTIVE)).booleanValue()) return 1;
	    else return 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this, ACTIVE);
	}

    @Override
	public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    @Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

	@Override
	public ItemBlock getItemBlock() {
		return new ItemBlockMachineHotAir(this, 125, 75);
	}

}
