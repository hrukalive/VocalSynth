import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.util.ArrayList;

public class PhasePaintCanvas extends JPanel
{

    public int[] amps;
    private ArrayList<Integer> freqs = new ArrayList<>();
    private ArrayList<Double> phases = new ArrayList<Double>();
    public final int winWidth = 400;
    public final int winHeight = 100;
    private int oldX = 0;
    private int maxFreq = 10000;

    private final int[] amps1 = new int[winWidth];
    private final int[] amps2 = new int[winWidth];
    private boolean haveSet1 = false;
    private boolean haveSet2 = false;
    
    private MainGUI parent;

    public PhasePaintCanvas() {
        setBorder(BorderFactory.createLineBorder(Color.black));
        amps = new int[winWidth];
        for (int i = 0; i < winWidth; i++)
            amps[i] = winHeight / 2;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                oldX = e.getX();
                paintPoint(e.getX(),e.getY());
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                paintPoint(e.getX(),e.getY());
            }
        });
        
    }
    
    public void setParent(MainGUI parent, ArrayList<Integer> freqs)
    {
        this.parent = parent;
        this.freqs = freqs;
    }
    
    
    public ArrayList<Double> getPhases()
    {
        return phases;
    }

    // Drawing are implemented here
    private void paintPoint(int x, int y) {
        if (x < 0 && x > -10)
            x = 0;
        else if (x >= winWidth && x < winWidth + 10)
            x = winWidth - 1;
        if (y < 0)
            y = 0;
        if (y > winHeight)
            y = winHeight;
        if (x >= 0 && x < winWidth)
        {
            amps[x] = y;
            if (oldX < x)
            {
                for (int i = oldX; i < x; i++)
                    amps[i] = (int) (amps[oldX] + (double) (i - oldX) / (x - oldX) * (amps[x] - amps[oldX]));
            } else
            {
                for (int i = x; i < oldX; i++)
                    amps[i] = (int) (amps[x] + (double) (i - x) / (oldX - x) * (amps[oldX] - amps[x]));
            }
            repaint();
            oldX = x;
            recalc();
            parent.requestUpdateWaveform();
        }
    }

    // Rescale the canvas when the range of shown freqency changes
    public void changeFreqRange(int freq)
    {
        if (freq < this.maxFreq)
        {
            double scaler = (double)freq / this.maxFreq;
            this.maxFreq = freq;
            for (int i = winWidth - 1; i >= 0; i--)
            {
                int index = (int)Math.round(i * scaler);
                amps[i] = amps[index];
                amps1[i] = amps1[index];
                amps2[i] = amps2[index];
            }
            repaint();
        }
        else if (freq > this.maxFreq)
        {
            double multiplier = (double)freq / this.maxFreq;
            this.maxFreq = freq;
            for (int i = 0; i < (int)(winWidth * (1 / multiplier)); i++)
            {
                amps[i] = amps[(int)Math.round(i * multiplier)];
                amps1[i] = amps1[(int)Math.round(i * multiplier)];
                amps2[i] = amps2[(int)Math.round(i * multiplier)];
            }
            for (int j = (int)(winWidth * (1 / multiplier)); j < winWidth; j++)
            {
                amps[j] = winHeight / 2;
                amps1[j] = winHeight / 2;
                amps2[j] = winHeight / 2;
            }
            repaint();
        }
        recalc();
        parent.requestUpdateWaveform();
    }
    
    /*public void interpolate(ArrayList<Double> read_amps, double T)
    {
        double[] damps = new double[winWidth];
        for (int i = 0; i < amps.length; i++)
        {
            damps[i] = 0;
            for (int j = 1; j <= read_amps.size(); j++)
            {
                damps[i] += read_amps.get(j - 1) * sinc((i - j * T) / (double)T);
            }
            amps[i] = (int)(winHeight/2 - damps[i]/Math.PI*(winHeight/2));
            //amps[i] = (int)(sinc((i-T)/(double)T)*winHeight);
        }
    }*/
    
    // Interpolation with sample points
    // Known problem: several times of saving and loading can cause distortion
    public void interpolate(ArrayList<Double> read_amps, double maxFreq, double f0)
    {
        double T = f0;
        double[] damps = new double[(int)maxFreq];
        
        for (int i = 0; i < damps.length; i++)
        {
            damps[i] = 0;
            for (int j = 1; j <= read_amps.size(); j++)
            {
                damps[i] += read_amps.get(j - 1) * sinc((i - j * T) / T);
            }
            damps[i] = (int)(winHeight/2 - damps[i]/Math.PI*(winHeight/2));
        }
        
        for (int i = 0; i < amps.length; i++)
        {
            //amps[i] = (int)(winHeight/2 - damps[i*damps.length/amps.length]/Math.PI*(winHeight/2));
            amps[i] = (int)damps[i*damps.length / amps.length];
        }
    }
    
    // Sinc function used by interpolation
    private double sinc(double x)
    {
        if (Math.abs(x) < 1e-16)
            return 1;
        return Math.sin(Math.PI * x) / (Math.PI * x);
    }
    
    // Blend features are implemented here
    public void addSet1()
    {
        for (int i = 0; i < amps.length; i++)
        {
            amps1[i] = amps[i];
        }
        haveSet1 = true;
    }
    public void addSet2()
    {
        for (int i = 0; i < amps.length; i++)
        {
            amps2[i] = amps[i];
        }
        haveSet2 = true;
    }
    public void blend(double ratio)
    {
        if (haveSet1 && haveSet2)
        {
            for (int i = 0; i < amps.length; i++)
                amps[i] = (int)((1-ratio)*amps1[i]+ratio*amps2[i]);
            repaint();
            recalc();
            parent.requestUpdateWaveform();
        }
    }

    // Clear the frame
    public void clear()
    {
        for (int i = 0; i < winWidth; i++)
            amps[i] = winHeight / 2;
        repaint();
        recalc();
        parent.requestUpdateWaveform();
    }

    // Recalculate amplitudes of each frequencies
    public void recalc()
    {
        phases.clear();
        for (Integer i : freqs)
            phases.add((double)(winHeight / 2 - amps[i]) / (double)(winHeight / 2) * Math.PI);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(winWidth, winHeight);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Line2D l = new Line2D.Double(0, winHeight / 2, winWidth, winHeight / 2);
        g2d.draw(l);
        for (int i = 0; i < (amps.length - 1); i++)
        {
            Line2D line = new Line2D.Double(i, amps[i], i + 1, amps[i + 1]);
            g2d.draw(line);
        }
        for (Integer i : freqs)
        {
            Line2D line = new Line2D.Double(i, amps[i], i, winHeight / 2);
            g2d.draw(line);
        }
    }
}