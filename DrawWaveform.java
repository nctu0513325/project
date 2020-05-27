import java.util.Scanner;
import java.io.*;
import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;

public class DrawWaveform extends JFrame {
    private ArrayList<Double>[] signal;
    // private WavFile wav = new WavFile("C_major.wav");
    // private WaveformPanel wp = new WaveformPanel(wav.getSignal());

    public DrawWaveform() throws IOException {
        WavFile.read("C_major.wav");
        WaveformPanel wp = new WaveformPanel(WavFile.getSignal());
        add(wp, BorderLayout.CENTER);
        // System.out.println(signal[0].size());
    }

    public static void main(String[] args) throws IOException {
        DrawWaveform f = new DrawWaveform();
        f.setSize(1500, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}

class WaveformPanel extends JPanel {
    private ArrayList<Double>[] signal;

    public WaveformPanel(ArrayList<Double>[] signal) {
        this.signal = signal;
        setSize(1500, 500);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int interval = signal[0].size() / getWidth();
        int y_base;
        int max = 100;
        for (int i = 0; i < getWidth(); i++) {
            // for different channel
            for (int j = 0; j < signal.length; j++) {
                if (j % 2 == 0) {
                    g.setColor(Color.BLUE);
                    y_base = getHeight() / 3;
                } else {
                    g.setColor(Color.GREEN);
                    y_base = getHeight() * 2 / 3;
                }
                g.drawLine(i, y_base - (int) (signal[j].get(i * interval) * max), i + 1,
                        y_base - (int) (signal[j].get((i + 1) * interval) * max));

            }
        }
    }
}