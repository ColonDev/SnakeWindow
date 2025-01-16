package Game;

import Utilities.Direction;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;

import static Game.Food.WINDOW_SIZE;

public class Snake {
    private static final Utilities.CustomConfig config = Utilities.CustomConfig.getInstance("src/config.properties");
    public static final int MOVE_SPEED = 100;
    private static final int size = config.getIntProperty("size", 20);
    private static final int STEP_SIZE = 20;

    private final LinkedList<JWindow> snakeSegments = new LinkedList<>();
    private final LinkedList<Point> positions = new LinkedList<>();
    private final AtomicReference<Direction> direction;
    private final AtomicReference<Direction> nextDirection;
    private int score = 0;
    private final Rectangle screenBounds;

    public Snake(JFrame controlFrame, Rectangle screenBounds, AtomicReference<Direction> direction, AtomicReference<Direction> nextDirection) {
        this.direction = direction;
        this.nextDirection = nextDirection;
        this.screenBounds = screenBounds;
        createInitialSnake(controlFrame);
    }

    private void createInitialSnake(JFrame controlFrame) {
        int startX = (screenBounds.width / 2) + (10 * STEP_SIZE / 2);
        int startY = screenBounds.height / 2;

        for (int i = 0; i < 10; i++) {
            JWindow window = createSnakeSegment(controlFrame);
            Point position = new Point(startX - i * STEP_SIZE, startY);
            window.setLocation(position);
            window.setVisible(true);

            snakeSegments.add(window);
            positions.add(position);
        }
    }

    private JWindow createSnakeSegment(JFrame controlFrame) {
        JWindow window = new JWindow(controlFrame) {
            private BufferedImage off_screen;

            @Override
            public void update(Graphics g) {
                paint(g);
            }

            @Override
            public void paint(Graphics g) {
                if (off_screen == null) {
                    off_screen = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
                }
                Graphics offG = off_screen.getGraphics();
                paintComponent(offG);
                g.drawImage(off_screen, 0, 0, this);
            }

            private void paintComponent(Graphics g) {
                super.paintComponents(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Get the image URL using the class loader
                URL imageUrl = getClass().getClassLoader().getResource("snake.png");
                if (imageUrl == null) {
                    System.out.println("Image not found.");
                    return;
                }

                // Load the image
                ImageIcon snakeIcon = new ImageIcon(imageUrl);
                Image snakeImage = snakeIcon.getImage();

                // Draw the snake image
                g2d.drawImage(snakeImage, 0, 0, WINDOW_SIZE, WINDOW_SIZE, this);
            }
        };
        window.setSize(WINDOW_SIZE, WINDOW_SIZE);
        window.setBackground(new Color(0, 0, 0, 0));
        return window;
    }

    public void move() {
        // Update direction
        direction.set(nextDirection.get());
        Point newHead = new Point(positions.getFirst());

        // Calculate new head position based on current direction
        switch (direction.get()) {
            case UP -> newHead.y -= STEP_SIZE;
            case DOWN -> newHead.y += STEP_SIZE;
            case LEFT -> newHead.x -= STEP_SIZE;
            case RIGHT -> newHead.x += STEP_SIZE;
        }

        // Add new head position and remove the last position
        positions.addFirst(newHead);
        positions.removeLast();

        // Update segments' positions
        EventQueue.invokeLater(() -> {
            BufferedImage snakeImage = new BufferedImage(screenBounds.width, screenBounds.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = snakeImage.createGraphics();
            g2d.setColor(new Color(0, 0, 0, 0)); // Transparent background

            for (int i = 0; i < snakeSegments.size(); i++) {
                Point pos = positions.get(i);
                snakeSegments.get(i).setLocation(pos.x, pos.y);

                // Paint each segment on the buffered image
                g2d.drawImage(snakeSegments.get(i).getGraphicsConfiguration().createCompatibleImage(WINDOW_SIZE, WINDOW_SIZE), pos.x, pos.y, null);
            }
            g2d.dispose();

            // Paint the buffered image on the screen
            JFrame frame = (JFrame) snakeSegments.getFirst().getOwner();
            frame.getGraphics().drawImage(snakeImage, 0, 0, frame);
        });
    }

    public void grow() {
        // Add a new segment at the position of the last segment
        JWindow newSegment = createSnakeSegment((JFrame) snakeSegments.getFirst().getOwner());
        Point newTail = positions.getLast();
        newSegment.setLocation(newTail.x, newTail.y);
        newSegment.setVisible(true);

        // Add the new segment to the list
        snakeSegments.addLast(newSegment);
        positions.addLast(newTail);

        score++;
    }

    public boolean hasCollidedWithItself() {
        Point head = positions.getFirst();
        for (int i = 1; i < positions.size(); i++) {
            if (positions.get(i).equals(head)) {
                return true;
            }
        }
        return false;
    }

    public boolean isWithinBounds() {
        return screenBounds.contains(positions.getFirst());
    }

    public LinkedList<Point> getPositions() {
        return positions;
    }

    public int getScore() {
        return score;
    }

    public void dispose() {
        snakeSegments.forEach(Window::dispose);
    }
}