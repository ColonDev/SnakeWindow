package Utilities;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public boolean canChangeTo(Direction newDir) {
        return !((this == UP && newDir == DOWN) ||
                (this == DOWN && newDir == UP) ||
                (this == LEFT && newDir == RIGHT) ||
                (this == RIGHT && newDir == LEFT));
    }
}