import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;
import javax.swing.JFileChooser;

public class MainGUI extends JFrame
{
    private ArrayList<Integer> freqs = new ArrayList<Integer>();
    private ArrayList<Double> realfreqs = new ArrayList<Double>();

    private ArrayList<Double> amplitudes;
    private ArrayList<Double> phases;

    // Variable for scale feature
    private int scaleSelected = 0;
    private int typeSelected = 0;
    private int keyindex = 0;
    private int keyoffset = 0;
    private double volume = 1.0;

    JFileChooser fc;

    // Table of frequencies
    private final int[] CHROMATIC = {
        93,98,104,110,117,124,
        131,139,147,156,165,175,185,196,208,220,233,247,
        262,277,294,311,330,349,370,392,415,440,466,494,
        523,554,587,622,659,698,740,784,831,880,932,988,
        1047,1109,1175,1245,1319,1397,1480,1568,1661,1760,1865,1976,
        2093,2218,2349,2489,2637,2794
    };

    // Tables of indices for different patterns
    private final int[] CHROMATIC_PATTERN = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59};
    private final int[][][] SCALE = 
    {
        {
            {3,5,7,8,10,12,14,15,17,19,20,22,24,26,27,29,31,32,34,36,38,39,41,43,44,46,48,50,51}, // A
            {4,6,8,9,11,13,15,16,18,20,21,23,25,27,28,30,32,33,35,37,39,40,42,44,45,47,49,51,52}, // Bb
            {5,7,9,10,12,14,16,17,19,21,22,24,26,28,29,31,33,34,36,38,40,41,43,45,46,48,50,52,53}, //B
            {6,8,10,11,13,15,17,18,20,22,23,25,27,29,30,32,34,35,37,39,41,42,44,46,47,49,51,53,54}, //C
            {7,9,11,12,14,16,18,19,21,23,24,26,28,30,31,33,35,36,38,40,42,43,45,47,48,50,52,54,55}, //Db
            {8,10,12,13,15,17,19,20,22,24,25,27,29,31,32,34,36,37,39,41,43,44,46,48,49,51,53,55,56},//D
            {9,11,13,14,16,18,20,21,23,25,26,28,30,32,33,35,37,38,40,42,44,45,47,49,50,52,54,56,57},//Eb
            {10,12,14,15,17,19,21,22,24,26,27,29,31,33,34,36,38,39,41,43,45,46,48,50,51,53,55,57,58},//E
            {11,13,15,16,18,20,22,23,25,27,28,30,32,34,35,37,39,40,42,44,46,47,49,51,52,54,56,58,59},//F
            {0,2,4,5,7,9,11,12,14,16,17,19,21,23,24,26,28,29,31,33,35,36,38,40,41,43,45,47,48},     //Gb
            {1,3,5,6,8,10,12,13,15,17,18,20,22,24,25,27,29,30,32,34,36,37,39,41,42,44,46,48,49},     //G
            {2,4,6,7,9,11,13,14,16,18,19,21,23,25,26,28,30,31,33,35,37,38,40,42,43,45,47,49,50}      //Ab
        },
        {
            {3,5,6,8,10,11,13,15,17,18,20,22,23,25,27,29,30,32,34,35,37,39,41,42,44,46,47,49,51},
            {4,6,7,9,11,12,14,16,18,19,21,23,24,26,28,30,31,33,35,36,38,40,42,43,45,47,48,50,52},
            {5,7,8,10,12,13,15,17,19,20,22,24,25,27,29,31,32,34,36,37,39,41,43,44,46,48,49,51,53},
            {6,8,9,11,13,14,16,18,20,21,23,25,26,28,30,32,33,35,37,38,40,42,44,45,47,49,50,52,54},
            {7,9,10,12,14,15,17,19,21,22,24,26,27,29,31,33,34,36,38,39,41,43,45,46,48,50,51,53,55},
            {8,10,11,13,15,16,18,20,22,23,25,27,28,30,32,34,35,37,39,40,42,44,46,47,49,51,52,54,56},
            {9,11,12,14,16,17,19,21,23,24,26,28,29,31,33,35,36,38,40,41,43,45,47,48,50,52,53,55,57},
            {10,12,13,15,17,18,20,22,24,25,27,29,30,32,34,36,37,39,41,42,44,46,48,49,51,53,54,56,58},
            {11,13,14,16,18,19,21,23,25,26,28,30,31,33,35,37,38,40,42,43,45,47,49,50,52,54,55,57,59},
            {0,2,3,5,7,8,10,12,14,15,17,19,20,22,24,26,27,29,31,32,34,36,38,39,41,43,44,46,48},
            {1,3,4,6,8,9,11,13,15,16,18,20,21,23,25,27,28,30,32,33,35,37,39,40,42,44,45,47,49},
            {2,4,5,7,9,10,12,14,16,17,19,21,22,24,26,28,29,31,33,34,36,38,40,41,43,45,46,48,50}
        },
        {
            {3,5,6,8,10,11,14,15,17,18,20,22,23,26,27,29,30,32,34,35,38,39,41,42,44,46,47,50,51},
            {4,6,7,9,11,12,15,16,18,19,21,23,24,27,28,30,31,33,35,36,39,40,42,43,45,47,48,51,52},
            {5,7,8,10,12,13,16,17,19,20,22,24,25,28,29,31,32,34,36,37,40,41,43,44,46,48,49,52,53},
            {6,8,9,11,13,14,17,18,20,21,23,25,26,29,30,32,33,35,37,38,41,42,44,45,47,49,50,53,54},
            {7,9,10,12,14,15,18,19,21,22,24,26,27,30,31,33,34,36,38,39,42,43,45,46,48,50,51,54,55},
            {8,10,11,13,15,16,19,20,22,23,25,27,28,31,32,34,35,37,39,40,43,44,46,47,49,51,52,55,56},
            {9,11,12,14,16,17,20,21,23,24,26,28,29,32,33,35,36,38,40,41,44,45,47,48,50,52,53,56,57},
            {10,12,13,15,17,18,21,22,24,25,27,29,30,33,34,36,37,39,41,42,45,46,48,49,51,53,54,57,58},
            {11,13,14,16,18,19,22,23,25,26,28,30,31,34,35,37,38,40,42,43,46,47,49,50,52,54,55,58,59},
            {0,2,3,5,7,8,11,12,14,15,17,19,20,23,24,26,27,29,31,32,35,36,38,39,41,43,44,47,48},
            {1,3,4,6,8,9,12,13,15,16,18,20,21,24,25,27,28,30,32,33,36,37,39,40,42,44,45,48,49},
            {2,4,5,7,9,10,13,14,16,17,19,21,22,25,26,28,29,31,33,34,37,38,40,41,43,45,46,49,50}
        }
    };

    private SoundThread soundThread;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_addSet1;
    private javax.swing.JButton btn_addSet2;
    private javax.swing.JButton btn_freqClear;
    private javax.swing.JButton btn_load;
    private javax.swing.JButton btn_phaseClear;
    private javax.swing.JToggleButton btn_play;
    private javax.swing.JButton btn_record;
    private javax.swing.JButton btn_save;
    private javax.swing.JComboBox<String> combo_scale;
    private javax.swing.JComboBox<String> combo_type;
    private javax.swing.JPanel freeFreqChooser;
    private FreqPaintCanvas freqPaintCanvas;
    private javax.swing.JPanel freqPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel label_freq;
    private PhasePaintCanvas phasePaintCanvas;
    private javax.swing.JPanel phasePanel;
    private javax.swing.JPanel scaleFreqChooser;
    private javax.swing.JSlider slider_blend;
    private javax.swing.JSlider slider_freq;
    private javax.swing.JSlider slider_key;
    private javax.swing.JSlider slider_voiceFreq;
    private javax.swing.JSlider slider_volume;
    private javax.swing.JTabbedPane tab_freqchooser;
    private javax.swing.JTextField txt_maxFreq;
    private javax.swing.JTextField txt_offset;
    private javax.swing.JTextField txt_voiceFreq;
    private WaveformCanvas waveformCanvas;
    // End of variables declaration//GEN-END:variables

    public MainGUI() {
        initComponents();
        fc = new JFileChooser();
        freqPaintCanvas.setParent(this, freqs);
        phasePaintCanvas.setParent(this, freqs);
        
        amplitudes = freqPaintCanvas.getAmplitudes();
        phases = phasePaintCanvas.getPhases();
        generateFreqLine(Double.parseDouble(txt_voiceFreq.getText()), Integer.parseInt(txt_maxFreq.getText()));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        freqPanel = new javax.swing.JPanel();
        freqPaintCanvas = new FreqPaintCanvas();
        slider_freq = new javax.swing.JSlider();
        txt_maxFreq = new javax.swing.JTextField();
        btn_addSet1 = new javax.swing.JButton();
        btn_addSet2 = new javax.swing.JButton();
        btn_freqClear = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        phasePanel = new javax.swing.JPanel();
        phasePaintCanvas = new PhasePaintCanvas();
        btn_phaseClear = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        waveformCanvas = new WaveformCanvas();
        tab_freqchooser = new javax.swing.JTabbedPane();
        freeFreqChooser = new javax.swing.JPanel();
        slider_voiceFreq = new javax.swing.JSlider();
        jLabel5 = new javax.swing.JLabel();
        txt_voiceFreq = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        scaleFreqChooser = new javax.swing.JPanel();
        combo_scale = new javax.swing.JComboBox<>();
        combo_type = new javax.swing.JComboBox<>();
        slider_key = new javax.swing.JSlider();
        label_freq = new javax.swing.JLabel();
        txt_offset = new javax.swing.JTextField();
        btn_load = new javax.swing.JButton();
        btn_save = new javax.swing.JButton();
        btn_record = new javax.swing.JButton();
        btn_play = new javax.swing.JToggleButton();
        slider_volume = new javax.swing.JSlider();
        slider_blend = new javax.swing.JSlider();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(244, 244, 244));

        freqPanel.setBackground(new java.awt.Color(243, 243, 243));
        freqPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout freqPaintCanvasLayout = new javax.swing.GroupLayout(freqPaintCanvas);
        freqPaintCanvas.setLayout(freqPaintCanvasLayout);
        freqPaintCanvasLayout.setHorizontalGroup(
            freqPaintCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        freqPaintCanvasLayout.setVerticalGroup(
            freqPaintCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 248, Short.MAX_VALUE)
        );

        slider_freq.setMaximum(20000);
        slider_freq.setMinimum(6000);
        slider_freq.setValue(10000);
        slider_freq.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_freqStateChanged(evt);
            }
        });

        txt_maxFreq.setText("10000");
        txt_maxFreq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_maxFreqKeyPressed(evt);
            }
        });

        btn_addSet1.setText("1st");
        btn_addSet1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addSet1ActionPerformed(evt);
            }
        });

        btn_addSet2.setText("2nd");
        btn_addSet2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_addSet2ActionPerformed(evt);
            }
        });

        btn_freqClear.setText("Clear");
        btn_freqClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_freqClearActionPerformed(evt);
            }
        });

        jLabel1.setText("Hz");

        jLabel2.setText("20Hz");

        jLabel3.setText("0dB");

        jLabel4.setText("-90dB");

        javax.swing.GroupLayout freqPanelLayout = new javax.swing.GroupLayout(freqPanel);
        freqPanel.setLayout(freqPanelLayout);
        freqPanelLayout.setHorizontalGroup(
            freqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(freqPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(freqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(freqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(freqPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(83, 83, 83)
                        .addComponent(slider_freq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addComponent(txt_maxFreq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addComponent(freqPaintCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(freqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(freqPanelLayout.createSequentialGroup()
                        .addGroup(freqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_freqClear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_addSet1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btn_addSet2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        freqPanelLayout.setVerticalGroup(
            freqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(freqPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(freqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, freqPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4))
                    .addGroup(freqPanelLayout.createSequentialGroup()
                        .addComponent(btn_addSet1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_addSet2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_freqClear))
                    .addComponent(freqPaintCanvas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(freqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(freqPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(freqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(slider_freq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(freqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_maxFreq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(freqPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(6, 6, 6))
        );

        phasePanel.setBackground(new java.awt.Color(244, 244, 244));
        phasePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout phasePaintCanvasLayout = new javax.swing.GroupLayout(phasePaintCanvas);
        phasePaintCanvas.setLayout(phasePaintCanvasLayout);
        phasePaintCanvasLayout.setHorizontalGroup(
            phasePaintCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 398, Short.MAX_VALUE)
        );
        phasePaintCanvasLayout.setVerticalGroup(
            phasePaintCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 98, Short.MAX_VALUE)
        );

        btn_phaseClear.setText("Clear");
        btn_phaseClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_phaseClearActionPerformed(evt);
            }
        });

        jLabel7.setText("π");

        jLabel8.setText("0");

        jLabel9.setText("-π");

        javax.swing.GroupLayout phasePanelLayout = new javax.swing.GroupLayout(phasePanel);
        phasePanel.setLayout(phasePanelLayout);
        phasePanelLayout.setHorizontalGroup(
            phasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(phasePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(phasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(phasePaintCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_phaseClear)
                .addContainerGap())
        );
        phasePanelLayout.setVerticalGroup(
            phasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, phasePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(phasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_phaseClear)
                    .addGroup(phasePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(phasePaintCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(phasePanelLayout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addGap(26, 26, 26)
                            .addComponent(jLabel8)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9))))
                .addContainerGap())
        );

        waveformCanvas.setBackground(new java.awt.Color(244, 244, 244));

        javax.swing.GroupLayout waveformCanvasLayout = new javax.swing.GroupLayout(waveformCanvas);
        waveformCanvas.setLayout(waveformCanvasLayout);
        waveformCanvasLayout.setHorizontalGroup(
            waveformCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        waveformCanvasLayout.setVerticalGroup(
            waveformCanvasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 112, Short.MAX_VALUE)
        );

        slider_voiceFreq.setMaximum(1320);
        slider_voiceFreq.setMinimum(110);
        slider_voiceFreq.setValue(220);
        slider_voiceFreq.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_voiceFreqStateChanged(evt);
            }
        });

        jLabel5.setText("Frequency:");

        txt_voiceFreq.setText("220");
        txt_voiceFreq.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_voiceFreqKeyPressed(evt);
            }
        });

        jLabel6.setText("Hz");

        javax.swing.GroupLayout freeFreqChooserLayout = new javax.swing.GroupLayout(freeFreqChooser);
        freeFreqChooser.setLayout(freeFreqChooserLayout);
        freeFreqChooserLayout.setHorizontalGroup(
            freeFreqChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(freeFreqChooserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(freeFreqChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(slider_voiceFreq, javax.swing.GroupLayout.DEFAULT_SIZE, 210, Short.MAX_VALUE)
                    .addGroup(freeFreqChooserLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_voiceFreq, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        freeFreqChooserLayout.setVerticalGroup(
            freeFreqChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, freeFreqChooserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(freeFreqChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txt_voiceFreq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(slider_voiceFreq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tab_freqchooser.addTab("Freqency", freeFreqChooser);

        combo_scale.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "A", "Bb", "B", "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "Chromatic" }));
        combo_scale.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_scaleActionPerformed(evt);
            }
        });

        combo_type.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Major", "Natural Minor", "Harmonic Minor" }));
        combo_type.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combo_typeActionPerformed(evt);
            }
        });

        slider_key.setMajorTickSpacing(3);
        slider_key.setMaximum(10);
        slider_key.setMinorTickSpacing(1);
        slider_key.setValue(0);
        slider_key.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_keyStateChanged(evt);
            }
        });

        label_freq.setText("220Hz");

        txt_offset.setText("0");
        txt_offset.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_offsetKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout scaleFreqChooserLayout = new javax.swing.GroupLayout(scaleFreqChooser);
        scaleFreqChooser.setLayout(scaleFreqChooserLayout);
        scaleFreqChooserLayout.setHorizontalGroup(
            scaleFreqChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scaleFreqChooserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scaleFreqChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(scaleFreqChooserLayout.createSequentialGroup()
                        .addComponent(slider_key, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(label_freq))
                    .addGroup(scaleFreqChooserLayout.createSequentialGroup()
                        .addComponent(combo_scale, 0, 77, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(combo_type, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_offset, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        scaleFreqChooserLayout.setVerticalGroup(
            scaleFreqChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(scaleFreqChooserLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(scaleFreqChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(combo_scale, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(combo_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_offset, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(scaleFreqChooserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(slider_key, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_freq, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tab_freqchooser.addTab("Scale", scaleFreqChooser);

        btn_load.setText("Load Preset");
        btn_load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_loadActionPerformed(evt);
            }
        });

        btn_save.setText("Save Preset");
        btn_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveActionPerformed(evt);
            }
        });

        btn_record.setText("Record and Analyze");
        btn_record.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_recordActionPerformed(evt);
            }
        });

        btn_play.setText("Play");
        btn_play.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_playActionPerformed(evt);
            }
        });

        slider_volume.setValue(100);
        slider_volume.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_volumeStateChanged(evt);
            }
        });

        slider_blend.setValue(0);
        slider_blend.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                slider_blendStateChanged(evt);
            }
        });

        jLabel10.setText("Blend: ");

        jLabel11.setText("Volume:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(waveformCanvas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tab_freqchooser)
                    .addComponent(btn_play, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btn_load, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(btn_record, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btn_save, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(slider_volume, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(slider_blend, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(phasePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(freqPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btn_load)
                            .addComponent(btn_save))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_record)
                        .addGap(26, 26, 26)
                        .addComponent(tab_freqchooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(slider_volume, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(slider_blend, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_play, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(freqPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(waveformCanvas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phasePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Events are processed
    private void btn_loadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_loadActionPerformed
        int returnVal = fc.showOpenDialog(MainGUI.this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            Scanner fileIn = null;
            try {
                fileIn = new Scanner(file);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            double f0 = 0;
            if (fileIn != null && fileIn.hasNextDouble())
                f0 = fileIn.nextDouble();
            
            int num = 0;
            if (fileIn != null && fileIn.hasNextInt())
                num = fileIn.nextInt();
            
            ArrayList<Double> amps_read = new ArrayList<Double>();
            ArrayList<Double> phases_read = new ArrayList<Double>();
            int i;
            for (i = 0; i < num && fileIn != null && fileIn.hasNextDouble(); i++)
                amps_read.add(Math.pow(10.0, fileIn.nextDouble() / 20.0));
            for (;i < num; i++)
                amps_read.add(0.0);
            
            for (i = 0; i < num && fileIn != null && fileIn.hasNextDouble(); i++)
                phases_read.add(fileIn.nextDouble());
            for (; i < num; i++)
                phases_read.add(0.0);
            
            txt_voiceFreq.setText("" + f0);
            txt_maxFreq.setText("" + (int)(f0 * (num + 1) - f0 / 2));
            slider_voiceFreq.setValue((int)Double.parseDouble(txt_voiceFreq.getText()));
            slider_freq.setValue(Integer.parseInt(txt_maxFreq.getText()));
            
            int maxFreq = (int)Double.parseDouble(txt_maxFreq.getText());
            
            /*freqs.clear();
            for (int j = 1; j * f0 <= (double)maxFreq; j++)
            {
                freqs.add((int)Math.round((j * f0) / (double)maxFreq * (freqPaintCanvas.getPreferredSize().getWidth() - 1)));
            }
            
            freqPaintCanvas.interpolate(amps_read, 
                    Math.round(2 * f0 / (double) maxFreq * (freqPaintCanvas.getPreferredSize().getWidth() - 1)) 
                         - Math.round(f0 / (double) maxFreq * (freqPaintCanvas.getPreferredSize().getWidth() - 1)));
            phasePaintCanvas.interpolate(phases_read, 
                    Math.round(2 * f0 / (double) maxFreq * (phasePaintCanvas.getPreferredSize().getWidth() - 1)) 
                         - Math.round(f0 / (double) maxFreq * (phasePaintCanvas.getPreferredSize().getWidth() - 1)));*/
            freqPaintCanvas.interpolate(amps_read, maxFreq, f0);
            phasePaintCanvas.interpolate(phases_read, maxFreq, f0);
            generateFreqLine(f0, maxFreq);

        }
    }//GEN-LAST:event_btn_loadActionPerformed

    private void btn_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveActionPerformed
        int returnVal = fc.showSaveDialog(MainGUI.this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION)
        {
            File file = fc.getSelectedFile();
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(file));
            } catch (IOException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                out.write(txt_voiceFreq.getText());
                out.write(" " + amplitudes.size() + " ");
                for (double a : amplitudes)
                {
                    if (a > 3.1623e-5)
                        a = 20*Math.log10(a);
                    else
                        a = -90.0;
                    out.write(a + " ");
                }
                for (double p : phases)
                {
                    out.write(p + " ");
                }
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(MainGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
    }//GEN-LAST:event_btn_saveActionPerformed

    private void btn_recordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_recordActionPerformed
        RecordFrame recordFrame = new RecordFrame();
        recordFrame.setVisible(true);
        recordFrame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        recordFrame.setParent(this);
    }//GEN-LAST:event_btn_recordActionPerformed

    private void txt_voiceFreqKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_voiceFreqKeyPressed
        if (evt.getKeyCode() == 10)
        {
            slider_voiceFreq.setValue((int)Double.parseDouble(txt_voiceFreq.getText()));
            generateFreqLine(Double.parseDouble(txt_voiceFreq.getText()), Integer.parseInt(txt_maxFreq.getText()));
        }
    }//GEN-LAST:event_txt_voiceFreqKeyPressed

    private void slider_voiceFreqStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_voiceFreqStateChanged
        if (slider_voiceFreq.isFocusOwner())
        {
            txt_voiceFreq.setText("" + slider_voiceFreq.getValue());
            generateFreqLine(Double.parseDouble(txt_voiceFreq.getText()), Integer.parseInt(txt_maxFreq.getText()));
        }
    }//GEN-LAST:event_slider_voiceFreqStateChanged

    private void combo_scaleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_scaleActionPerformed
        scaleSelected = combo_scale.getSelectedIndex();
        slider_key.setValue(0);
        if (scaleSelected == 12)
        {
            slider_key.setMaximum(15);
            combo_type.setEnabled(false);
        }
        else
        {
            slider_key.setMaximum(10);
            combo_type.setEnabled(true);
        }
        applyScale();
    }//GEN-LAST:event_combo_scaleActionPerformed

    private void combo_typeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combo_typeActionPerformed
        typeSelected = combo_type.getSelectedIndex();
        slider_key.setValue(0);
        applyScale();
    }//GEN-LAST:event_combo_typeActionPerformed

    private void slider_keyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_keyStateChanged
        keyindex = slider_key.getValue();
        applyScale();
    }//GEN-LAST:event_slider_keyStateChanged

    private void txt_offsetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_offsetKeyPressed
        if (evt.getKeyCode() == 10)
        {
            try
            {
                keyoffset = Integer.parseInt(txt_offset.getText());
                applyScale();
            }
            catch(Exception e)
            {
            }
        }
    }//GEN-LAST:event_txt_offsetKeyPressed

    private void slider_volumeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_volumeStateChanged
        volume = slider_volume.getValue() / 100.0;
    }//GEN-LAST:event_slider_volumeStateChanged

    private void slider_blendStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_blendStateChanged
        freqPaintCanvas.blend(slider_blend.getValue() / 100.0);
        phasePaintCanvas.blend(slider_blend.getValue() / 100.0);
    }//GEN-LAST:event_slider_blendStateChanged

    private void btn_playActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_playActionPerformed
        
        if (btn_play.isSelected())
        {
            soundThread = new SoundThread();
            soundThread.start();
        }
        else
        {
            soundThread.exit();
        }
    }//GEN-LAST:event_btn_playActionPerformed

    private void btn_addSet1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addSet1ActionPerformed
        freqPaintCanvas.addSet1();
        phasePaintCanvas.addSet1();
    }//GEN-LAST:event_btn_addSet1ActionPerformed

    private void btn_addSet2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_addSet2ActionPerformed
        freqPaintCanvas.addSet2();
        phasePaintCanvas.addSet2();
    }//GEN-LAST:event_btn_addSet2ActionPerformed

    private void btn_freqClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_freqClearActionPerformed
        freqPaintCanvas.clear();
        freqPaintCanvas.recalc();
        requestUpdateWaveform();
    }//GEN-LAST:event_btn_freqClearActionPerformed

    private void txt_maxFreqKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_maxFreqKeyPressed
        if (evt.getKeyCode() == 10)
        {
            slider_freq.setValue(Integer.parseInt(txt_maxFreq.getText()));
            freqPaintCanvas.changeFreqRange(Integer.parseInt(txt_maxFreq.getText()));
            generateFreqLine(Double.parseDouble(txt_voiceFreq.getText()), Integer.parseInt(txt_maxFreq.getText()));
        }
    }//GEN-LAST:event_txt_maxFreqKeyPressed

    private void slider_freqStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_slider_freqStateChanged
        if (slider_freq.isFocusOwner())
        {
            txt_maxFreq.setText("" + slider_freq.getValue());
            freqPaintCanvas.changeFreqRange(slider_freq.getValue());
            generateFreqLine(Double.parseDouble(txt_voiceFreq.getText()), Integer.parseInt(txt_maxFreq.getText()));
        }
    }//GEN-LAST:event_slider_freqStateChanged

    private void btn_phaseClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_phaseClearActionPerformed
        phasePaintCanvas.clear();
        phasePaintCanvas.recalc();
        requestUpdateWaveform();
    }//GEN-LAST:event_btn_phaseClearActionPerformed

    // Callback for record canvas
    public void setFromRecord(int f0, ArrayList<Double> amps, ArrayList<Double> phss)
    {
        txt_voiceFreq.setText("" + f0);
        txt_maxFreq.setText("15500");
        slider_voiceFreq.setValue((int)Double.parseDouble(txt_voiceFreq.getText()));
        slider_freq.setValue(Integer.parseInt(txt_maxFreq.getText()));
            
        int maxFreq = (int)Double.parseDouble(txt_maxFreq.getText());
        
        freqPaintCanvas.interpolate(amps, maxFreq, f0);
        phasePaintCanvas.interpolate(phss, maxFreq, f0);
        generateFreqLine(f0, maxFreq);
    }

    // Helper method for set the freqency from the configuration
    private void applyScale()
    {
        int realIndex = 0;
        if (scaleSelected == 12)
            realIndex = CHROMATIC_PATTERN[keyindex];
        else
            realIndex = SCALE[typeSelected][scaleSelected][keyindex];
        realIndex += keyoffset;
        if (realIndex >= 0 && realIndex < CHROMATIC.length)
        {
            txt_voiceFreq.setText("" + CHROMATIC[realIndex]);
            slider_voiceFreq.setValue((int)Double.parseDouble(txt_voiceFreq.getText()));
            label_freq.setText(CHROMATIC[realIndex] + "Hz");
            generateFreqLine(Double.parseDouble(txt_voiceFreq.getText()), Integer.parseInt(txt_maxFreq.getText()));
        }
    }

    // Callback method that will force waveform canvas to refresh
    public void requestUpdateWaveform()
    {
        waveformCanvas.drawWaveform(realfreqs, amplitudes, phases);
    }
    
    // Callback/Helper method that will recalculate the position of
    // frequency lines and force freqCanvas and phaseCanvas to 
    // recalculate the 'amplitudes' followed by refreshing the waveform
    private void generateFreqLine(double F0, int maxFreq)
    {
        freqs.clear();
        realfreqs.clear();
        for (int i = 1; i * F0 <= (double)maxFreq; i++)
        {
            freqs.add((int)Math.round((i * F0) / (double)maxFreq * (freqPaintCanvas.getPreferredSize().getWidth() - 1)));
            realfreqs.add(i*F0);
        }
        
        freqPaintCanvas.repaint();
        phasePaintCanvas.repaint();
        freqPaintCanvas.recalc();
        phasePaintCanvas.recalc();
        requestUpdateWaveform();
    }
    
    public static void main(String args[]) {
        /* Set the System default look and feel */
       //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            System.out.println(javax.swing.UIManager.getSystemLookAndFeelClassName());
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainGUI().setVisible(true);
            }
        });
    }
    
    // Thread for playing sound in background
    class SoundThread extends Thread {

        final static public int SAMPLING_RATE = 44100;
        final static public int SAMPLE_SIZE = 2; //Sample size in bytes

        final static public double BUFFER_DURATION = 0.050; //About a 50ms buffer

        // Size in bytes of sine wave samples we'll create on each loop pass         
        final static public int SINE_PACKET_SIZE = (int) (BUFFER_DURATION * SAMPLING_RATE * SAMPLE_SIZE);

        SourceDataLine line;
        public double fFreq;
        public boolean bExitThread = false;

        //Get the number of queued samples in the SourceDataLine buffer
        private int getLineSampleCount() {
            return line.getBufferSize() - line.available();
        }

        public void run() {
            //Position through the sine wave as a percentage (i.e. 0-1 is 0-2*PI)
            double fCyclePosition = 0;
            try {
                AudioFormat format = new AudioFormat(44100, 16, 1, true, true);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format, SINE_PACKET_SIZE * 2);

                if (!AudioSystem.isLineSupported(info)) {
                    throw new LineUnavailableException();
                }

                line = (SourceDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
            } catch (LineUnavailableException e) {
                System.out.println("Line of that type is not available");
                e.printStackTrace();
                System.exit(-1);
            }

            System.out.println("Requested line buffer size = " + SINE_PACKET_SIZE * 2);
            System.out.println("Actual line buffer size = " + line.getBufferSize());

            ByteBuffer cBuf = ByteBuffer.allocate(SINE_PACKET_SIZE);

            while (bExitThread == false)
            {
                try
                {
                    try
                    {
                        fFreq = Double.parseDouble(txt_voiceFreq.getText());
                    }
                    catch(Exception e)
                    {
                        fFreq = slider_voiceFreq.getValue();
                    }
                    int maxFreq = (int)Double.parseDouble(txt_maxFreq.getText());

                    double fCycleInc = fFreq / SAMPLING_RATE; //Fraction of cycle between samples

                    cBuf.clear();

                    double factor = amplitudes.get(0);
                    for (int i = 1; i < amplitudes.size(); i++)
                        factor += amplitudes.get(i);

                    for (int i = 0; i < SINE_PACKET_SIZE / SAMPLE_SIZE; i++)
                    {
                        double dSampleVal = 0;

                        for (int j = 1; j * fFreq <= (double)maxFreq; j++)
                        {
                            double temp = getAmplitude(j * fFreq, maxFreq);
                            if (temp < 1e-16)
                                continue;
                            dSampleVal += 1.0 / factor * temp * Math.sin(2 * Math.PI * j * fCyclePosition + getPhase(j * fFreq, maxFreq));
                        }
                        cBuf.putShort((short) (Short.MAX_VALUE * volume * dSampleVal));

                        fCyclePosition += fCycleInc;
                        if (fCyclePosition > 1)
                            fCyclePosition -= 1;
                    }
                    line.write(cBuf.array(), 0, cBuf.position());
                    try
                    {
                        while (getLineSampleCount() > SINE_PACKET_SIZE)
                        {
                            Thread.sleep(1);
                        }
                    } catch (InterruptedException e)
                    {
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    break;
                }
            }

            line.drain();
            line.close();
        }

        // Methods for some conversion between dB and amplitudes, index and phases
        public double getAmplitude(double freq, int maxFreq)
        {
            int i = (int)Math.round(freq / (double)maxFreq * (freqPaintCanvas.getPreferredSize().getWidth() - 1));
            if (freqPaintCanvas.amps[i] < freqPaintCanvas.winHeight)
                return Math.pow(10.0, (freqPaintCanvas.amps[i] / (double)freqPaintCanvas.winHeight * (-90.0)) / 20.0);
            else
                return 0.0;
        }
        public double getPhase(double freq, int maxFreq)
        {
            int i = (int)Math.round(freq / (double)maxFreq * (phasePaintCanvas.getPreferredSize().getWidth() - 1));
            return (double)(phasePaintCanvas.winHeight / 2 - phasePaintCanvas.amps[i]) / (double)(phasePaintCanvas.winHeight / 2) * Math.PI;
        }

        public void exit()
        {
            bExitThread = true;
        }
    }
}
