import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Java Procedural 2D Dungeon Generation Test.
 * 
 * References:
 * https://en.wikipedia.org/wiki/Prim%27s_algorithm
 * 
 * Procedural Generation in Godot: Dungeon Generation (part 2) - 
 * https://www.youtube.com/watch?v=U9B39sDIupc
 * 
 * @author Leonardo Ono (ono.leo@gmail.com);
 */

public class View2 extends JPanel implements KeyListener {

    private final BufferedImage image 
        = new BufferedImage(120, 90, BufferedImage.TYPE_INT_RGB);
    
    private static final int NUMBER_OF_POINTS = 10000;
    private final List<Point> points = new ArrayList<>();
    
    private final List<Point> tree = new ArrayList<>();
    
    private static final int ROOM_MAX_SIZE = 20;
    private static final int ROOM_MIN_SIZE = 10;
    
    // minimum distance between 2 rooms
    private static final int ROOM_MIN_DISTANCE = 2;
    
    private final List<Rectangle> rooms = new ArrayList<>();
    
    public View2() {
    }
    
    public void start() {
        addKeyListener(this);
        
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) image.getGraphics());
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }
    
    private void draw(Graphics2D g) {
        g.clearRect(0, 0, image.getWidth(), image.getHeight());

        Set<Integer> xs = new HashSet<>();
        Set<Integer> ys = new HashSet<>();
        
        points.clear();
        tree.clear();
        rooms.clear();
        
        g.setColor(Color.WHITE);

        outer:
        for (int i = 0; i < NUMBER_OF_POINTS; i++) {
            int x = (int) (10 + (image.getWidth() - 20) * Math.random());
            int y = (int) (10 + (image.getHeight() - 20) * Math.random());
            Point p = new Point(x, y);

            int w = (int) (ROOM_MIN_SIZE 
                    + ((ROOM_MAX_SIZE - ROOM_MIN_SIZE) * Math.random()));
            
            int h = (int) (ROOM_MIN_SIZE 
                    + ((ROOM_MAX_SIZE - ROOM_MIN_SIZE) * Math.random()));
            
            Rectangle ra = new Rectangle(p.x - w / 2, p.y - h / 2, w, h);
            for (Rectangle rb : rooms) {
                if (ra.intersects(rb)) {
                    continue outer;
                }
            }
            
            // ensure each room doesn't touch others
            ra.x += ROOM_MIN_DISTANCE;
            ra.y += ROOM_MIN_DISTANCE;
            ra.width -= 2 * ROOM_MIN_DISTANCE;
            ra.height -= 2 * ROOM_MIN_DISTANCE;

            // ensure path doesn't collide with one of wall corners
            if (xs.contains(ra.x) || xs.contains(ra.x + ra.width / 2) 
                || xs.contains(ra.x + ra.width)
                || ys.contains(ra.y) || ys.contains(ra.y + ra.height / 2) 
                || ys.contains(ra.y + ra.height)) {
                
                continue;
            }

            // save wich xs and ys can't be used for next room
            int d = 0;
            //for (int d = -1; d <= 1; d++) {
                xs.add(ra.x + d);
                xs.add(ra.x + ra.width / 2 + d);
                xs.add(ra.x + ra.width + d);
                ys.add(ra.y + d);
                ys.add(ra.y + ra.height / 2 + d);
                ys.add(ra.y + ra.height + d);
            //}
            
            rooms.add(ra);
            
            g.setColor(Color.BLUE);
            g.draw(ra);
            points.add(p);
        }
        
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
                throw new RuntimeException("error ?");
            }
            
            points.remove(b);
            tree.add(b);

            // draw path between one room to another
            g.setColor(Color.RED);
            g.drawLine(a.x, a.y, a.x, b.y);
            g.drawLine(a.x, b.y, b.x, b.y);
        }
        
        // fill rooms
        g.setPaintMode();
        for (Rectangle room : rooms) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(room.x + 1, room.y + 1
                    , room.width - ROOM_MIN_DISTANCE / 2
                    , room.height - ROOM_MIN_DISTANCE / 2);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            View2 view = new View2();
            view.setPreferredSize(new Dimension(800, 600));
            JFrame frame = new JFrame();
            frame.setTitle("Java Procedural 2D Dungeon Generation Test");
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
