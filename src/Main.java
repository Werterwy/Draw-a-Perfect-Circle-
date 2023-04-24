import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class CircleDrawingGame extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final double IDEAL_CIRCLE_RADIUS = 150;
    private static final int CIRCLE_SIZE = 25;
    private static final double MINIMUM_DISTANCE = 5;
    private static final double MAXIMUM_SPEED = 100;
    private static final int REFRESH_RATE = 10;
    private static final double ACCURACY_THRESHOLD = 80;

    private ArrayList<Point> points = new ArrayList<Point>();
    private Point currentPoint = null;
    private Point lastPoint = null;
    private boolean isDrawing = false;
    private int circleX;
    private int circleY;
    private int circleRadius;
    private Color circleColor;
    private double accuracyPercentage = 0;
    private double accuracyThreshold = ACCURACY_THRESHOLD;
    private int radiusX;
    private int radiusY;

    public CircleDrawingGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        circleX = WIDTH / 2;
        circleY = HEIGHT / 2;

        circleRadius = (int) IDEAL_CIRCLE_RADIUS;
        circleColor = Color.BLACK;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    currentPoint = e.getPoint();
                    lastPoint = currentPoint;
                    isDrawing = true;
                    points.clear();
                    accuracyPercentage = 0;
                    circleRadius = 0;
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    isDrawing = false;
                    currentPoint = null;
                    lastPoint = null;
                    checkAccuracy();
                    repaint();
                }
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Point point = e.getPoint();
                    double distance = getDistance(point, lastPoint);
                    double time = getTime(point, lastPoint);
                    if (distance > MINIMUM_DISTANCE && time < MAXIMUM_SPEED) {
                        points.add(point);
                        lastPoint = point;
                        checkAccuracy();
                        repaint();
                    }
                }
            }
        });
        JFrame frame = new JFrame("Circle Drawing Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private double getDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    private double getTime(Point p1, Point p2) {
        long t1 = new Date().getTime();
        long t2 = new Date().getTime();
        return (t1 - t2) / 1000.0;
    }

    private double getAvgRadius() {
        double totalRadius = 0;
        for (Point p : points) {
            totalRadius += getDistance(p, new Point(circleX, circleY));
        }
        return totalRadius / points.size();
    }

    private void checkAccuracy() {
        double avgRadius = getAvgRadius();
        accuracyPercentage = 100 - Math.abs(avgRadius - IDEAL_CIRCLE_RADIUS) / IDEAL_CIRCLE_RADIUS * 360;
        if (accuracyPercentage < accuracyThreshold) {
            circleColor = Color.RED;
        } else {
            circleColor = Color.BLACK;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(circleColor);
        g.fillOval(circleX - CIRCLE_SIZE/2, circleY - CIRCLE_SIZE/2, CIRCLE_SIZE, CIRCLE_SIZE);
        g.drawOval(circleX - circleRadius, circleY - circleRadius, circleRadius * 2, circleRadius * 2);
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            g.fillOval(p.x - 2, p.y - 2, 4, 4);
            if (i > 0) {
                Point lastP = points.get(i - 1);
                g.drawLine(lastP.x, lastP.y, p.x, p.y);
            }
        }
        g.drawString(String.format("Accuracy: %.2f%%", accuracyPercentage), 10, 20);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CircleDrawingGame());
    }
}

