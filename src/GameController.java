import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import Game.Food;
import Game.Snake;

import Utilities.Direction;

public class GameController {

    private static final Utilities.CustomConfig config = Utilities.CustomConfig.getInstance("src/config.properties");
    private final Snake snake;
    private final Food food;
    private final JFrame controlFrame;
    private final AtomicBoolean isRunning;
    private final AtomicReference<Direction> direction;
    private final AtomicReference<Direction> nextDirection;
    private static final int NUM_FOOD = config.getIntProperty("numFood", 1);

    public GameController() {
        isRunning = new AtomicBoolean(false);
        direction = new AtomicReference<>(Direction.RIGHT);
        nextDirection = new AtomicReference<>(Direction.RIGHT);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle screenBounds = new Rectangle(0, 0, gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());

        controlFrame = setupControlFrame();
        snake = new Snake(controlFrame, screenBounds, direction, nextDirection);

        food = new Food(controlFrame, screenBounds, snake.getPositions(), NUM_FOOD);

        setupControls();
        startGameLoop();
    }

    private JFrame setupControlFrame() {
        JFrame frame = new JFrame("Snake");
        ImageIcon icon = new ImageIcon("resources/icon.png");
        frame.setIconImage(icon.getImage());
        frame.setUndecorated(true);
        frame.setSize(1, 1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.requestFocus();
        return frame;
    }

    private void setupControls() {
        controlFrame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> updateDirection(Direction.UP);
                    case KeyEvent.VK_DOWN -> updateDirection(Direction.DOWN);
                    case KeyEvent.VK_LEFT -> updateDirection(Direction.LEFT);
                    case KeyEvent.VK_RIGHT -> updateDirection(Direction.RIGHT);
                    case KeyEvent.VK_SPACE -> isRunning.set(!isRunning.get());
                    case KeyEvent.VK_ESCAPE -> endGame();
                }
            }
        });

        controlFrame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                Timer focusTimer = new Timer(100, _ -> controlFrame.requestFocus());
                focusTimer.setRepeats(false);
                focusTimer.start();
            }
        });

        Timer focusTimer = new Timer(500, _ -> {
            if (!controlFrame.isFocused()) {
                controlFrame.requestFocus();
            }
        });
        focusTimer.start();
    }

    private void updateDirection(Direction newDirection) {
        Direction currentDir = direction.get();
        if (currentDir.canChangeTo(newDirection)) {
            nextDirection.set(newDirection);
        }
    }

    private void startGameLoop() {
        Timer timer = new Timer(Snake.MOVE_SPEED, _ -> {
            if (isRunning.get()) {
                EventQueue.invokeLater(() -> {
                    snake.move();
                    List<Point> foodPositions = food.getPositions();
                    List<Point> snakePositions = new LinkedList<>(snake.getPositions());
                    boolean foodCollected = false;
                    for (Point foodPosition : foodPositions) {
                        for (Point snakePosition : snakePositions) {
                            if (snakePosition.equals(foodPosition)) {
                                foodCollected = true;
                                break;
                            }
                        }
                        if (foodCollected) {
                            snake.grow();
                            food.respawnFood(foodPosition, snake.getPositions());
                            break;
                        }
                    }
                    if (snake.hasCollidedWithItself() || !snake.isWithinBounds()) {
                        gameOver();
                    }
                });
            }
        });
        timer.setCoalesce(true);
        timer.start();
    }

    private void gameOver() {
        isRunning.set(false);
        JOptionPane.showMessageDialog(controlFrame, "Game Over! Score: " + snake.getScore());
        endGame();
    }

    private void endGame() {
        isRunning.set(false);
        snake.dispose();
        food.dispose();
        controlFrame.dispose();
        System.exit(0);
    }
}