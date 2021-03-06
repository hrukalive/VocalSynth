
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.AudioInputStream;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class RecordFrame extends JFrame
{
    
    private RecordThread recordThread;
    private UpdateWaveformThread wavUptThread;

    private MainGUI parent;

    private String state = "Idle";
    private int leftCursor = 0;
    private int rightCursor = 0;
    private ByteArrayOutputStream out;
    private ArrayList<Double> data = new ArrayList<Double>();
    private double[] abs;
    private double[] phs;

    JFileChooser fc;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_analyze;
    private javax.swing.JButton btn_open;
    private javax.swing.JToggleButton btn_record;
    private javax.swing.JLabel label_info;
    private RecordCanvas recordCanvas;
    // End of variables declaration//GEN-END:variables

    public RecordFrame() {
        initComponents();
        fc = new JFileChooser();
        recordCanvas.setParent(this);
    }

    public void setParent(MainGUI parent)
    {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_record = new javax.swing.JToggleButton();
        recordCanvas = new RecordCanvas();
        btn_analyze = new javax.swing.JButton();
        label_info = new javax.swing.JLabel();
        btn_open = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(247, 247, 247));

        btn_record.setText("Record");
        btn_record.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recordActionPerformed(evt);
            }
        });

        recordCanvas.setBackground(new java.awt.Color(243, 243, 243));

        javax.swing.GroupLayout recordCanvasLayout = new javax.swing.GroupLayout(recordCanvas);
        recordCanvas.setLayout(recordCanvasLayout);
        recordCanvasLayout.setHorizontalGroup(
            recordCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 580, Short.MAX_VALUE)
        );
        recordCanvasLayout.setVerticalGroup(
            recordCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        btn_analyze.setText("Analyze");
        btn_analyze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_analyzeActionPerformed(evt);
            }
        });

        label_info.setText("Press 'Record' to start recording.");

        btn_open.setText("Open");
        btn_open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_openActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(recordCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_record, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_open, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_analyze)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(label_info)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(recordCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_record)
                    .addComponent(btn_analyze)
                    .addComponent(label_info)
                    .addComponent(btn_open))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Button functions are implemented here
    private void btn_recordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recordActionPerformed
        if (state.equals("Idle") || state.equals("Recorded"))
        {
            if (btn_record.isSelected())
            {
                btn_record.setText("Stop");
                recordThread = new RecordThread();
                wavUptThread = new UpdateWaveformThread();
                recordThread.start();
                wavUptThread.start();
            }
            else
            {
                btn_record.setText("Record");
                recordThread.exit();
                wavUptThread.exit();
                wavUptThread.interrupt();
                state = "Recorded";
                label_info.setText("Please set the left cursor.");
            }
        }
    }//GEN-LAST:event_btn_recordActionPerformed

    private void btn_openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_openActionPerformed
        if (!state.equals("Idle") && !state.equals("Recorded"))
            return;
        int returnVal = fc.showOpenDialog(RecordFrame.this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            Wave.WavFile wavFile = null;
            try {
                wavFile = Wave.WavFile.openWavFile(file);
            } catch (IOException ex) {
                Logger.getLogger(RecordFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Wave.WavFileException ex) {
                Logger.getLogger(RecordFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            long numFrame = wavFile.getNumFrames();
            short[] shortArr = new short[(int)numFrame];
            try {
                wavFile.readFramesChanel(shortArr, (int)numFrame, 0);
            } catch (IOException ex) {
                Logger.getLogger(RecordFrame.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Wave.WavFileException ex) {
                Logger.getLogger(RecordFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            out = new ByteArrayOutputStream();
            ShortBuffer sBuf = ShortBuffer.wrap(shortArr);
            ByteBuffer byteBuf = ByteBuffer.allocate((int)numFrame * 2);
            for (short s : shortArr) byteBuf.putShort(s);
            byteBuf.compact();
            try {
                out.write(byteBuf.array());
            } catch (IOException ex) {
                Logger.getLogger(RecordFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            ArrayList<Double> data = new ArrayList<Double>();
            for (int i = 0; i < sBuf.capacity(); i++) {
                data.add(sBuf.get(i) / 32768.0);
            }
            recordCanvas.setData(data, 2.0, (int) recordCanvas.getPreferredSize().getHeight() / 2);
            state = "Recorded";
        }
    }//GEN-LAST:event_btn_openActionPerformed

    private void btn_analyzeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_analyzeActionPerformed
        if (state.equals("CutR"))
        {
            int size = data.size()-data.size()%2;
            double[] real = new double[size];
            double[] imag = new double[size];
            for (int i = 0; i < size; i++)
            {
                real[i] = data.get(i) * (0.5 * (1 - Math.cos(2 * Math.PI * i / (size - 1))));
                imag[i] = 0.0;
            }
            Fft.transform(real, imag);
            abs = new double[size];
            phs = new double[size];
            ArrayList<Double> display_part = new ArrayList<Double>();
            double max = 0.0;
            for (int i = 0; i < real.length; i++)
            {
                abs[i] = Math.sqrt(real[i]*real[i]+imag[i]*imag[i]);
                phs[i] = Math.atan2(imag[i], real[i]);
                if (i < 4000/44100.0*(size-1))
                    display_part.add(abs[i]);
                if (abs[i] > max && i < 4000/44100.0*(size-1))
                    max = abs[i];
            }
            recordCanvas.setData(display_part, max, 0);
            
            state = "Analyzed";
            label_info.setText("Please click near the F0.");
        }
    }//GEN-LAST:event_btn_analyzeActionPerformed

    // Process when user click in the canvas if the state is correct
    // In state 'Recorded', allow to set the left cursor of clipping
    // In state 'CutL',     allow to set the right cursor of clipping
    // In state 'Analyzed', allow to find F0 and call parent to update
    public void process(int x,int y)
    {
        if (state.equals("Recorded"))
        {
            state = "CutL";
            leftCursor = (int)((double)x / recordCanvas.getPreferredSize().getWidth() * ByteBuffer.wrap(out.toByteArray()).asShortBuffer().capacity());
            label_info.setText("Please set the right cursor.");
        }
        else if (state.equals("CutL"))
        {
            state = "CutR";
            ShortBuffer sBuf = ByteBuffer.wrap(out.toByteArray()).asShortBuffer();
            rightCursor = (int)((double)x / recordCanvas.getPreferredSize().getWidth() * sBuf.capacity());
            System.out.println(sBuf.capacity());
            System.out.println(leftCursor+" "+rightCursor);
            data.clear();
            for (int i = 0; i < rightCursor - leftCursor; i++)
            {
                data.add(sBuf.get(i + leftCursor) / 32768.0);
            }
            recordCanvas.setData(data, 2.0, (int)recordCanvas.getPreferredSize().getHeight() / 2);
            label_info.setText("Press \'Analyze\' to analyze.");
        }
        else if (state.equals("Analyzed"))
        {
            int size = data.size()-data.size()%2;
          
            double f0p = (int)(((double)x / recordCanvas.getPreferredSize().getWidth() * 4000 ) / 44100.0 * (size - 1));
            int region = (int)(100 / 44100.0 * (size - 1)) / 2;
            
            int f0i = findMax(abs, (int)f0p - region, (int)f0p + region);
            f0p = (f0i / (double)(size - 1) * 44100.0);
            
            label_info.setText("F0: "+String.format("%.2f", f0p) + "Hz Amp: " + abs[f0i]);
            
            ArrayList<Double> amps = new ArrayList<Double>();
            ArrayList<Double> phases = new ArrayList<Double>();
            double max = 0.0;
            for (int i = 1; i*f0p < 15000; i++)
            {
                int fi = findMax(abs, i * f0i - region, i * f0i + region);
                amps.add(abs[fi]);
                if (amps.get(i - 1) > max)
                    max = amps.get(i-1);
                phases.add(phs[fi]);
            }
            for (int i = 0; i < amps.size(); i++)
                amps.set(i, amps.get(i) / max);
            
            parent.setFromRecord((int)Math.round(f0p), amps, phases);
            state = "Idle";
        }
    }
    
    // Helper method to find the index of the max element in the range
    private int findMax(double[] vals, int lo, int hi)
    {
        int max = lo;
        while (max < hi && max < 0)
            max++;
        for (int i = lo + 1; i <= hi; i++)
            if (i >= 0 && i < vals.length)
            {
                if (vals[i] > vals[max])
                    max = i;
            }
        return max;
    }
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(RecordFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RecordFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RecordFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RecordFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RecordFrame().setVisible(true);
            }
        });
    }

    // Thread for updating waveform through recording
    class UpdateWaveformThread extends Thread
    {
        private boolean bExitFlag = false;
        @Override
        public void run()
        {
            ShortBuffer sBuf = ShortBuffer.allocate(0);
            while (!bExitFlag)
            {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    System.out.println("Update thread is interuppted, exit");
                    bExitFlag = true;
                }
                if (out != null)
                {
                    sBuf = ByteBuffer.wrap(out.toByteArray()).asShortBuffer();
                    
                    ArrayList<Double> data = new ArrayList<Double>();
                    for (int i = 0; i < sBuf.capacity(); i++)
                    {
                        data.add(sBuf.get(i) / 32768.0);
                    }
                    recordCanvas.setData(data, 2.0, (int)recordCanvas.getPreferredSize().getHeight() / 2);
                }
            }
        }
        public void exit()
        {
            bExitFlag = true;
        }
    }

    // Thread for continuous recording
    class RecordThread extends Thread
    {
        private boolean bExitThread = false;
        
        @Override
        public void run()
        {
            TargetDataLine line = null;
            AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                return;
            }

            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
            } catch (LineUnavailableException ex) {
                return;
            }
            out  = new ByteArrayOutputStream();
            int numBytesRead;
            byte[] data = new byte[line.getBufferSize() / 5];

            System.out.println("Line started");
            System.out.println("Buffer size: " + data.length);
            
            line.start();
            while (!bExitThread) {
               numBytesRead =  line.read(data, 0, data.length);
               System.out.println("Read " + numBytesRead + " bytes");
               out.write(data, 0, numBytesRead);
            }
            line.close();
            
            
            /* Uncomment these lines will automatically playback 
             * recorded audio after pressing 'Stop'
            info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line2 = null;
            try {
                line2 = (SourceDataLine) AudioSystem.getLine(info);
            } catch (LineUnavailableException ex) {
                Logger.getLogger(RecordFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                line2.open(format);
            } catch (LineUnavailableException ex) {
                Logger.getLogger(RecordFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            line2.start();
            line2.write(out.toByteArray(), 0, out.toByteArray().length);
            line2.drain();
            line2.close();
            */
        }
        
        public void exit()
        {
            bExitThread = true;
        }
    }

}
