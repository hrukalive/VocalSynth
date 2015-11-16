import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
//import java.awt.event.MouseMotionListener;
//import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;

public class RecordCanvas extends JPanel {
    
    private int winWidth = 582;
    private int winHeight = 302;
    private RecordFrame parent = null;
    private ArrayList<Double> data;
    private double max;
    private int offset;
    public RecordCanvas() {
        setBorder(BorderFactory.createLineBorder(Color.black));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                parent.process(e.getX(),e.getY());
            }
        });
        
        /*addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                parent.process(e.getX(),e.getY());
            }
        });*/
    }
    public void setParent(RecordFrame parent)
    {
        this.parent = parent;
    }
    public void setData(ArrayList<Double> data, double max, int offset)
    {
        this.data = data;
        this.max = max;
        this.offset = offset;
        repaint();
    }
    public void setData(double[] data, double max, int offset, boolean negate)
    {
        this.data = new ArrayList<Double>();
        for (int i = 0; i < data.length; i++)
        {
            this.data.add((negate?-data[i]:data[i]));
        }
        this.max = max;
        this.offset = offset;
        repaint();
    }
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(winWidth, winHeight);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data != null)
        {
            Graphics2D g2d = (Graphics2D) g;
            double increment = (double)winWidth / data.size();
            for (int i = 0; i < data.size() - 1; i++)
            {
                Line2D line = new Line2D.Double((int)(i * increment), winHeight - (data.get(i)/max * winHeight + offset),
                                                (int)((i + 1)*increment), winHeight - (data.get(i+1)/max * winHeight + offset));
                g2d.draw(line);
            }
        }
    }
}
