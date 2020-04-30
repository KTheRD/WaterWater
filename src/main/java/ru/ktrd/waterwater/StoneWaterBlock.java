package ru.ktrd.waterwater;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;

import static ru.ktrd.waterwater.Util.*;
import static ru.ktrd.waterwater.WaterWater.MODID;


public class StoneWaterBlock extends Block{

    public static final String REGISTRYNAME = "stonewaterblock";

    @ObjectHolder(MODID+":"+REGISTRYNAME)
    public static StoneWaterBlock STONEWATERBLOCK;

    @ObjectHolder(MODID+":"+REGISTRYNAME+"_wood")
    public static StoneWaterBlock STONEWATERBLOCKWOOD;

    @ObjectHolder(MODID+":"+REGISTRYNAME+"_iron")
    public static StoneWaterBlock STONEWATERBLOCKIRON;

    public StoneWaterBlock(Properties properties) {
        super(properties);
        setDefaultState(stateContainer.getBaseState()
                .with(NORTH,false)
                .with(SOUTH,false)
                .with(UP,false)
                .with(DOWN,false)
                .with(WEST,false)
                .with(EAST,false)

                .with(POWERED_NORTH, false)
                .with(POWERED_SOUTH, false)
                .with(POWERED_EAST, false)
                .with(POWERED_WEST, false)
                .with(POWERED_UP, false)
                .with(POWERED_DOWN, false)
        );
    }

    @Override
    public boolean isTransparent(BlockState p_220074_1_) {
        return true;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
        builder.add(
                UP,
                NORTH,
                EAST,
                WEST,
                SOUTH,
                DOWN,

                POWERED_NORTH,
                POWERED_SOUTH,
                POWERED_EAST,
                POWERED_WEST,
                POWERED_UP,
                POWERED_DOWN
        );
    }

    @Override
    public ActionResultType onBlockActivated(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if(this.material == Material.IRON){
            return ActionResultType.PASS;
        }
        BooleanProperty property = getPropertyFromFaceName(blockRayTraceResult.getFace().getName());
        world.setBlockState(blockPos, blockState.cycle(property));
        if (blockState.cycle(property).get(property)) {
            this.checkForMixing(world, blockPos, blockState.cycle(property));
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void neighborChanged(BlockState blockState, World world, BlockPos blockPos, Block blockIn, BlockPos fromPos, boolean p_220069_6_) {
        this.checkForMixing(world, blockPos, blockState);
        if (!world.isRemote && this.material != Material.WOOD) {
            for (Direction direction : Direction.values()){
                boolean flag = world.getRedstonePower(blockPos.offset(direction),direction) > 0;
                if (flag != blockState.get(getPropertyPoweredFromFaceName(direction.getName()))){
                    if (blockState.get(getPropertyFromFaceName(direction.getOpposite().getName())) != flag){
                        blockState = blockState.with(getPropertyFromFaceName(direction.getOpposite().getName()), flag);
                    }
                    world.setBlockState(blockPos, (BlockState)blockState.with(getPropertyPoweredFromFaceName(direction.getName()), flag), 2);
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext blockItemUseContext) {
        BlockState blockState = this.getDefaultState();
        World world = blockItemUseContext.getWorld();
        BlockPos blockPos = blockItemUseContext.getPos();
        if (this.material != Material.WOOD) {
            for (Direction direction : Direction.values()) {
                if (world.getRedstonePower(blockPos.offset(direction), direction) > 0) {
                    blockState = blockState.with(getPropertyFromFaceName(direction.getOpposite().getName()), true).with(getPropertyPoweredFromFaceName(direction.getName()), true);
                }
            }
        }
        return blockState;
    }

    private void checkForMixing(World world, BlockPos blockPos, BlockState blockState) {

        for (Direction direction : Direction.values()) {

            BlockPos posL = blockPos.offset(direction);
            BlockState stateL = world.getBlockState(posL);
            IFluidState fluidStateL = world.getFluidState(posL);

            if (blockState.get(getPropertyFromFaceName(direction.getName()))) {
                if (stateL.getMaterial() == Material.LAVA){
                    if (fluidStateL.isSource()) {
                        world.setBlockState(posL, Blocks.OBSIDIAN.getDefaultState());
                    } else{
                        world.setBlockState(posL, Blocks.COBBLESTONE.getDefaultState());
                    }
                    this.triggerMixEffects(world, posL);
                }

                int haveSource = 0;
                for (Direction dir : Direction.Plane.HORIZONTAL) {
                    BlockPos pos2 = posL.offset(dir);
                    BlockState state2 = world.getBlockState(pos2);
                    IFluidState fluidState2 = world.getFluidState(pos2);
                    if ((fluidState2.isSource() && fluidState2.getFluid().isEquivalentTo(Fluids.WATER)) || (state2.getBlock() instanceof StoneWaterBlock && state2.get(getPropertyFromFaceName(dir.getOpposite().getName())))) {
                        haveSource++;
                    }
                }
                if (haveSource >=2) {
                    boolean flag = stateL.isReplaceable(Fluids.WATER);
                    if (!(!stateL.isAir() && !flag && (!(stateL.getBlock() instanceof ILiquidContainer) || !((ILiquidContainer)stateL.getBlock()).canContainFluid(world,posL,stateL,Fluids.WATER)))) {
                        if (world.dimension.doesWaterVaporize()){
                            int i = posL.getX();
                            int j = posL.getY();
                            int k = posL.getZ();
                            world.playSound(null, posL, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
                            for(int l = 0; l < 8; ++l) {
                                world.addParticle(ParticleTypes.LARGE_SMOKE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D);
                            }
                        } else if (stateL.getBlock() instanceof ILiquidContainer) {
                            ((ILiquidContainer) stateL.getBlock()).receiveFluid(world, posL, stateL, Fluids.WATER.getStillFluidState(false));
                        } else{
                            if (!world.isRemote && flag && !stateL.getMaterial().isLiquid() && !(stateL.getBlock() instanceof AirBlock)) {
                                world.destroyBlock(posL, true);
                            }
                            world.setBlockState(posL, Fluids.WATER.getDefaultState().getBlockState(), 11);
                        }
                    }
                }
            }

        }
    }


    protected void triggerMixEffects(World worldIn, BlockPos pos) {
        double d0 = pos.getX();
        double d1 = pos.getY();
        double d2 = pos.getZ();
        worldIn.playSound(null, pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F,
                2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            worldIn.addOptionalParticle(ParticleTypes.LARGE_SMOKE, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(),
                    0.0D, 0.0D, 0.0D);

        }
    }
}
