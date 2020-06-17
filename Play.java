import java.nio.ByteBuffer;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javafx.application.Platform;

import java.util.*;

public class Play extends playercontroller {
    private int pauseTime = 0;
    private Thread td;

    public Play() {
        System.out.println(signal_modify[0].size());
        // System.out.println("ffff");
    }

    public void initialize() {

    }

    public void play() {
        System.out.println(signal_modify[0].size());
        playBySample(signal_modify, pauseTime, signal_modify[0].size() / WavFile.getSampleRate());
    }

    public void pause() {
        td.stop();
    }

    public void stop() {
        pauseTime = 0;
        drawCurrentTimeLine(0);
        td.stop();
    }

    public void blockPlay(double start, double end) {
        playBySample(signal_modify, start, end);
    }

    public void playBySample(ArrayList<Double>[] input, double startTime, double endTime) {
        // if (platBySampleFlag == true) {
        td = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    int bufferSize = 2200;
                    byte[] data_write;
                    AudioFormat audioFormat = new AudioFormat(WavFile.getSampleRate(), WavFile.getBitsPerSample(),
                            WavFile.getNumChannels(), true, true);
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
                    soundLine.open(audioFormat, bufferSize);
                    soundLine.start();
                    // byte counter = 0;
                    int index = 0;
                    double start = WavFile.getSampleRate() * startTime;
                    double end = WavFile.getSampleRate() * endTime;
                    int x = (int) start;
                    byte[] buffer = new byte[bufferSize];
                    int normalizeConstant = (int) Math.pow(2, WavFile.getBitsPerSample() - 1);
                    // drawCurrentTimeLine(startTime);

                    while (x < end) {
                        while (index < bufferSize) {
                            for (int channel = 0; channel < WavFile.getNumChannels(); channel++) {
                                int temp = (int) (input[channel].get(x) * (double) normalizeConstant);
                                data_write = ByteBuffer.allocate(4).putInt(temp).array();
                                buffer[index] = data_write[2];
                                buffer[index + 1] = data_write[3];
                                index += WavFile.getNumChannels();
                            }
                            x++;
                            // use Platform.runLater() to update UI during Thread
                            // x%10000 in case of delay too much
                            if (x % 10000 == 0) {
                                final int temp = x;
                                pauseTime = temp / WavFile.getSampleRate();
                                Platform.runLater(() -> {
                                    drawCurrentTimeLine(temp / WavFile.getSampleRate());
                                });
                            }
                        }

                        index = 0;
                        soundLine.write(buffer, 0, bufferSize);
                    }
                } catch (LineUnavailableException e) {
                    System.out.println(e.getMessage());
                }

            }
        });
        td.start();
    }
}