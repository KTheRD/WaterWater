package ru.ktrd.waterwater;

import net.minecraft.state.BooleanProperty;
import net.minecraft.state.properties.BlockStateProperties;

public class Util {

    private Util() {
        throw new AssertionError();
    }

    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final BooleanProperty POWERED_NORTH = BooleanProperty.create("powered_north");
    public static final BooleanProperty POWERED_SOUTH = BooleanProperty.create("powered_south");
    public static final BooleanProperty POWERED_EAST = BooleanProperty.create("powered_east");
    public static final BooleanProperty POWERED_WEST = BooleanProperty.create("powered_west");
    public static final BooleanProperty POWERED_UP = BooleanProperty.create("powered_up");
    public static final BooleanProperty POWERED_DOWN = BooleanProperty.create("powered_down");

    public static BooleanProperty getPropertyFromFaceName(String FaceName){
        switch (FaceName){
            case ("up"):
                return UP;
            case ("down"):
                return DOWN;
            case ("north"):
                return NORTH;
            case ("south"):
                return SOUTH;
            case ("east"):
                return EAST;
            case ("west"):
                return WEST;
        }
        return NORTH;
    }

    public static BooleanProperty getPropertyPoweredFromFaceName(String FaceName){
        switch (FaceName){
            case ("up"):
                return POWERED_UP;
            case ("down"):
                return POWERED_DOWN;
            case ("north"):
                return POWERED_NORTH;
            case ("south"):
                return POWERED_SOUTH;
            case ("east"):
                return POWERED_EAST;
            case ("west"):
                return POWERED_WEST;
        }
        return POWERED_NORTH;
    }
}