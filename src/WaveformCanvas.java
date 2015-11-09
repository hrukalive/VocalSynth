/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dsun18
 */

import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class WaveformCanvas extends JPanel
{
    
    private final int[] amps;
    private final int winWidth = 235;
    private final int winHeight = 114;
    
    public WaveformCanvas()
    {
        setBorder(BorderFactory.createLineBorder(Color.black));
        amps = new int[winWidth];
        for (int i = 0; i < winWidth; i++)
            amps[i] = 0;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(winWidth, winHeight);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for (int i = 0; i < (amps.length - 1); i++)
        {
            Line2D line = new Line2D.Double(i, amps[i] + winHeight / 2, i + 1, amps[i + 1] + winHeight / 2);
            g2d.draw(line);
        }
    }
    
    public void clear()
    {
        for (int i = 0; i < winWidth; i++)
            amps[i] = 0;
        repaint();
    }
    
    private Worker worker;
    private ArrayList<Double> realfreqs;
    private ArrayList<Double> amplitudes;
    private ArrayList<Double> phases;
    public void drawWaveform(ArrayList<Double> realfreqs, ArrayList<Double> amplitudes, ArrayList<Double> phases)
    {
        if (realfreqs.size()!=amplitudes.size() || realfreqs.size()!=phases.size() || amplitudes.size()!=phases.size())
            return;
        if (worker != null)
            worker.cancel(true);
        worker = new Worker();
        this.realfreqs = realfreqs;
        this.amplitudes = amplitudes;
        this.phases = phases;
        worker.execute();
    }
    
    private class Worker extends javax.swing.SwingWorker<Void, Void>
    {
        @Override
        @SuppressWarnings("empty-statement")
        public Void doInBackground()
        {
            double[] damps = new double[winWidth];
            for (int i = 0; i < winWidth; i++)
                damps[i] = 0;
            double step = 1.0 / ((double)realfreqs.get(0) * winWidth);
            double factor = amplitudes.get(0);
            for (int i = 1; i < amplitudes.size(); i++)
            {
                factor += amplitudes.get(i);//Math.sqrt(factor * factor + amplitudes.get(i) * amplitudes.get(i));
                //System.out.println(amplitudes.get(i));
            }
            //System.out.println(factor);
            for (int i = 0; i < realfreqs.size(); i++)
            {
                for (int j = 0; j < amps.length; j++)
                {
                    //System.out.println(amplitudes.get(i)+ " "+realfreqs.get(i));
                    damps[j] += (double)(winHeight / 2) / factor * amplitudes.get(i) * Math.sin(2 * Math.PI * realfreqs.get(i) * step * j + phases.get(i));
                }
            }
            for (int i = 0; i < winWidth; i++)
                amps[i] = (int)Math.round(damps[i]);
            return null;
        }
        
        @Override
        public void done()
        {
            repaint();
        }
    }
}