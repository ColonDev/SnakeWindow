package Game;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Food {
    static final int WINDOW_SIZE = Snake.getSize();

    private static final Color FOOD_COLOR = new Color(220, 20, 60);

    private final List<JWindow> foodWindows;
    private final List<Point> foodPositions;
    private final Rectangle screenBounds;
    private final LinkedList<Point> snakePositions;
    private final Random rand;

    public Food(JFrame controlFrame, Rectangle screenBounds, LinkedList<Point> snakePositions, int numFood) {
        this.screenBounds = screenBounds;
        this.snakePositions = snakePositions;
        this.foodWindows = new LinkedList<>();
        this.foodPositions = new LinkedList<>();
        this.rand = new Random();

        for (int i = 0; i < numFood; i++) {
            JWindow foodWindow = createFoodWindow(controlFrame);
            foodWindows.add(foodWindow);
            spawn(foodWindow);
        }
    }

    private JWindow createFoodWindow(JFrame controlFrame) {
        JWindow window = new JWindow(controlFrame);
        JPanel foodPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Get the image URL using the class loader
                URL imageUrl = getClass().getClassLoader().getResource("apple.png");
                if (imageUrl == null) {
                    System.out.println("Image not found. Using fallback color.");
                    g2d.setColor(FOOD_COLOR);
                    g2d.fillOval(0, 0, WINDOW_SIZE, WINDOW_SIZE);
                    return;
                }

                // Load the image
                ImageIcon appleIcon = new ImageIcon(imageUrl);
                Image appleImage = appleIcon.getImage();

                // Draw the apple image centered in the panel without scaling
                g2d.drawImage(appleImage, 0, 0, WINDOW_SIZE, WINDOW_SIZE, this);
            }
        };
        foodPanel.setOpaque(false);
        window.setContentPane(foodPanel);
        window.setSize(WINDOW_SIZE, WINDOW_SIZE);
        window.setBackground(new Color(0, 0, 0, 0));
        window.setAlwaysOnTop(true);
        window.setVisible(true);

        return window;
    }

    public void spawn(JWindow foodWindow) {
        Point foodPosition;
        do {
            // Generate a random position for the food
            foodPosition = new Point(
                    rand.nextInt((screenBounds.width - WINDOW_SIZE) / WINDOW_SIZE) * WINDOW_SIZE,
                    rand.nextInt((screenBounds.height - WINDOW_SIZE) / WINDOW_SIZE) * WINDOW_SIZE
            );
        } while (snakePositions.contains(foodPosition) || foodPositions.contains(foodPosition));

        // Add the new valid position to the food positions list
        foodPositions.add(foodPosition);

        // Update the food window position
        Point finalFoodPosition = foodPosition;
        EventQueue.invokeLater(() -> {
            foodWindow.setLocation(finalFoodPosition);
            foodWindow.setVisible(true);
            foodWindow.repaint();
        });
    }

    public void removeFood(Point foodPosition) {
        int index = foodPositions.indexOf(foodPosition);
        if (index != -1) {
            JWindow foodWindow = foodWindows.get(index);
            foodWindow.dispose();
            foodWindows.remove(index);
            foodPositions.remove(index);
        }
    }

    public void addNewFood(JFrame controlFrame) {
        JWindow newFoodWindow = createFoodWindow(controlFrame);
        foodWindows.add(newFoodWindow);
        spawn(newFoodWindow);
    }

    public List<Point> getPositions() {
        return foodPositions;
    }

    public void dispose() {
        foodWindows.forEach(Window::dispose);
    }
}