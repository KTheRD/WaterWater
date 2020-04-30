package ru.ktrd.waterwater;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.stream.Stream;

import static ru.ktrd.waterwater.Util.*;
import static ru.ktrd.waterwater.WaterWater.MODID;

public class BoxForSource extends Block implements IWaterLoggable {

    public static final String REGISTRYNAME = "boxforsource";

    @ObjectHolder(MODID+":"+REGISTRYNAME)
    public static BoxForSource BOXFORSOURCE;

    @ObjectHolder(MODID+":"+REGISTRYNAME+"_wood")
    public static BoxForSource BOXFORSOURCEWOOD;

    @ObjectHolder(MODID+":"+REGISTRYNAME+"_iron")
    public static BoxForSource BOXFORSOURCEIRON;

    public BoxForSource(Properties properties) {
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

                .with(WATERLOGGED, false)
        );
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
                POWERED_DOWN,

                WATERLOGGED
        );
    }

    ////////////////////////

    @Override
    public IFluidState getFluidState(BlockState blockState) {
        return blockState.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : Fluids.EMPTY.getDefaultState();
    }

    @Override
    public BlockState updatePostPlacement(BlockState p_196271_1_, Direction p_196271_2_, BlockState p_196271_3_, IWorld p_196271_4_, BlockPos p_196271_5_, BlockPos p_196271_6_) {
        if (p_196271_1_.get(WATERLOGGED)) {
            p_196271_4_.getPendingFluidTicks().scheduleTick(p_196271_5_, Fluids.WATER, Fluids.WATER.getTickRate(p_196271_4_));
        }
        return super.updatePostPlacement(p_196271_1_, p_196271_2_, p_196271_3_, p_196271_4_, p_196271_5_, p_196271_6_);
    }

    ////////////////

    @Override
    public void neighborChanged(BlockState blockState, World world, BlockPos blockPos, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
        if (!world.isRemote && (this.material != Material.WOOD)) {
            for (Direction direction : Direction.values()){
                boolean flag = world.getRedstonePower(blockPos.offset(direction),direction) > 0;
                if (flag != blockState.get(getPropertyPoweredFromFaceName(direction.getName()))){
                    if (blockState.get(getPropertyFromFaceName(direction.getOpposite().getName())) != flag){
                        blockState = blockState.with(getPropertyFromFaceName(direction.getOpposite().getName()), flag);
                    }
                    world.setBlockState(blockPos, (BlockState)blockState.with(getPropertyPoweredFromFaceName(direction.getName()), flag), 2);
                    if (blockState.get(WATERLOGGED)) {
                        world.getPendingFluidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
                    }
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext blockItemUseContext) {
        BlockState blockState = this.getDefaultState();
        IFluidState ifluidstate = blockItemUseContext.getWorld().getFluidState(blockItemUseContext.getPos());
        World world = blockItemUseContext.getWorld();
        BlockPos blockPos = blockItemUseContext.getPos();
        if (this.material != Material.WOOD) {
            for (Direction direction : Direction.values()) {
                if (world.getRedstonePower(blockPos.offset(direction), direction) > 0) {
                    blockState = blockState.with(getPropertyFromFaceName(direction.getOpposite().getName()), true).with(getPropertyPoweredFromFaceName(direction.getName()), true);
                }
            }
        }
        return blockState.with(WATERLOGGED, ifluidstate.getFluid() == Fluids.WATER);
    }

    ///////////////

    private static final VoxelShape BASE = Stream.of(
            Block.makeCuboidShape(0, 0, 0, 4, 16, 4),
            Block.makeCuboidShape(12, 0, 0, 16, 16, 4),
            Block.makeCuboidShape(12, 0, 12, 16, 16, 16),
            Block.makeCuboidShape(0, 0, 12, 4, 16, 16),
            Block.makeCuboidShape(0, 0, 4, 4, 4, 12),
            Block.makeCuboidShape(0, 12, 4, 4, 16, 12),
            Block.makeCuboidShape(4, 0, 12, 12, 4, 16),
            Block.makeCuboidShape(4, 12, 12, 12, 16, 16),
            Block.makeCuboidShape(12, 0, 4, 16, 4, 12),
            Block.makeCuboidShape(12, 12, 4, 16, 16, 12),
            Block.makeCuboidShape(4, 0, 0, 12, 4, 4),
            Block.makeCuboidShape(4, 12, 0, 12, 16, 4)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

    private static final VoxelShape PLUG_NORTH =Block.makeCuboidShape(4, 4, 0, 12, 12, 4);
    private static final VoxelShape PLUG_SOUTH =Block.makeCuboidShape(4, 4, 12, 12, 12, 16);
    private static final VoxelShape PLUG_EAST =Block.makeCuboidShape(12, 4, 4, 16, 12, 12);
    private static final VoxelShape PLUG_WEST =Block.makeCuboidShape(0, 4, 4, 4, 12, 12);
    private static final VoxelShape PLUG_UP =Block.makeCuboidShape(4, 12, 4, 12, 16, 12);
    private static final VoxelShape PLUG_DOWN =Block.makeCuboidShape(4, 0, 4, 12, 4, 12);

    @Override
    public VoxelShape getShape(BlockState blockState, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
        VoxelShape shape = BASE;
        if (!blockState.get(NORTH)){
            shape = VoxelShapes.combineAndSimplify(shape,PLUG_NORTH,IBooleanFunction.OR);
        }
        if (!blockState.get(SOUTH)){
            shape = VoxelShapes.combineAndSimplify(shape,PLUG_SOUTH,IBooleanFunction.OR);
        }
        if (!blockState.get(WEST)){
            shape = VoxelShapes.combineAndSimplify(shape,PLUG_WEST,IBooleanFunction.OR);
        }
        if (!blockState.get(EAST)){
            shape = VoxelShapes.combineAndSimplify(shape,PLUG_EAST,IBooleanFunction.OR);
        }
        if (!blockState.get(UP)){
            shape = VoxelShapes.combineAndSimplify(shape,PLUG_UP,IBooleanFunction.OR);
        }
        if (!blockState.get(DOWN)){
            shape = VoxelShapes.combineAndSimplify(shape,PLUG_DOWN,IBooleanFunction.OR);
        }
        return shape;
    }

    @Override
    public ActionResultType onBlockActivated(BlockState blockState, World world, BlockPos blockPos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult blockRayTraceResult) {
        if (this.material == Material.IRON){
            return ActionResultType.PASS;
        }
        Direction face = blockRayTraceResult.getFace();
        BooleanProperty property = getPropertyFromFaceName(face.getName());
        world.setBlockState(blockPos, blockState.cycle(property));
        if (blockState.get(WATERLOGGED)) {
            world.getPendingFluidTicks().scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return ActionResultType.SUCCESS;
    }
}
