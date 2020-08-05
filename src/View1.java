import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Prim's Algorithm Test. Brute force method.
 * 
 * References:
 * https://en.wikipedia.org/wiki/Prim%27s_algorithm
 * 
 * @author Leonardo Ono (ono.leo@gmail.com);
 */
public class View1 extends JPanel implements KeyListener {

    private static final int NUMBER_OF_POINTS = 100;
    private final List<Point> points = new ArrayList<>();
    private final List<Point> tree = new ArrayList<>();
    
    private final Stroke stroke = new BasicStroke(3);
    
    public View1() {
    }
    
    public void start() {
        addKeyListener(this);
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }
    
    private void draw(Graphics2D g) {
        g.clearRect(0, 0, getWidth(), getHeight());

        points.clear();
        tree.clear();

        outer:
        for (int i = 0; i < NUMBER_OF_POINTS; i++) {
            int x = (int) (50 + (getWidth() - 100) * Math.random());
            int y = (int) (50 + (getHeight() - 100) * Math.random());
            Point p = new Point(x, y);
            points.add(p);
        }
        
        g.setColor(Color.RED);
        points.forEach(point -> {
            g.fillOval(point.x - 5, point.y - 5, 10, 10);
        });
        
        g.setColor(Color.BLACK);
        
        tree.add(points.remove(0));
        
        while (!points.isEmpty()) {
            Point a = null;
            Point b = null;
            double minDistance = Double.MAX_VALUE;
            
            for (Point p1 : tree) {
                for (Point p2 : points) {
                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < minDistance) {
                        minDistance = distance;
                        a = p1;
                        b = p2;
                    }
                }
            }
            
            if (a == null || b == null) {
                throw new RuntimeException("erro ?");
            }
            
            points.remove(b);
            tree.add(b);
            
            g.setStroke(stroke);
            g.drawLine(a.x, a.y, b.x, b.y);

            //g.drawLine(a.x, a.y, a.x, b.y);
            //g.drawLine(a.x, b.y, b.x, b.y);
        }
        
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View1 view = new View1();
            view.setPreferredSize(new Dimension(800, 600));
            JFrame frame = new JFrame();
            frame.setTitle("Java 2D Prim's Algorithm Test");
            frame.getContentPane().add(view);
            frame.setResizable(false);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            view.requestFocus();
            view.start();
        });
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
    
}
