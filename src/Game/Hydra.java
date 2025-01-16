package Game;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class Hydra extends JPanel {

    private final BufferedImage canvas;
    private JFrame frame;
    private static final String MESSAGE = "Cut one head, and two shall rise.....";
    private final boolean isShaking;
    private static int closeCount = 0; // Tracks the number of closes

    public Hydra(int width, int height, String name, boolean shake) {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        setPreferredSize(new Dimension(width, height));
        isShaking = shake;
        setupFrame(name);
    }

    public void draw(Graphics2DCallback callback) {
        Random random = new Random();
        Graphics2D g = canvas.createGraphics();
        g.setFont(new Font("Chiller", Font.BOLD, 34));
        g.setColor(new Color(random.nextInt(0,255), 0, 0));
        callback.draw(g);
        g.dispose();
        repaint();
    }

    private void setupFrame(String name) {
        frame = new JFrame(name);
        frame.add(this);
        frame.pack();
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Random initial position
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Random random = new Random();
        int x = random.nextInt(screenSize.width - frame.getWidth());
        int y = random.nextInt(screenSize.height - frame.getHeight());
        frame.setLocation(x, y);

        frame.setVisible(true);

        // Window listener to multiply on close
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                closeCount++;
                SwingUtilities.invokeLater(() -> {
                    // Spawn two new windows that will shake
                    for (int i = 0; i < 2; i++) {
                        Hydra hydra = new Hydra(390, 300, "You can't escape...", true);
                        hydra.fillWithText();
                    }

                });
            }
        });

        if (isShaking) {
            startMovingWindow();
        }
    }

    private void fillWithText() {
        draw(g -> {
            int canvasWidth = getWidth();
            int canvasHeight = getHeight();
            FontMetrics metrics = g.getFontMetrics();
            int textHeight = metrics.getHeight();

            for (int y = 0; y < canvasHeight; y += textHeight) {
                for (int x = 0; x < canvasWidth; x += metrics.stringWidth(MESSAGE)) {
                    g.drawString(MESSAGE, x, y);
                }
            }
        });
    }

    private void startMovingWindow() {
        Timer timer = new Timer(30, e -> {
            if (frame != null) {
                Point currentLocation = frame.getLocation();
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

                // Increase shaking intensity with each close
                int intensity = closeCount; // Base speed increases by 2 per close
                int dx = (Math.random() > 0.5 ? 1 : -1) * (intensity + new Random().nextInt(intensity));
                int dy = (Math.random() > 0.5 ? 1 : -1) * (intensity + new Random().nextInt(intensity));

                int newX = currentLocation.x + dx;
                int newY = currentLocation.y + dy;

                newX = Math.max(0, Math.min(screenSize.width - frame.getWidth(), newX));
                newY = Math.max(0, Math.min(screenSize.height - frame.getHeight(), newY));

                frame.setLocation(newX, newY);
            }
        });

        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }

    public interface Graphics2DCallback {
        void draw(Graphics2D g);
    }

    public static void main(String[] args) {
        new Hydra(100, 100, "uH OH", false);

    }
}
