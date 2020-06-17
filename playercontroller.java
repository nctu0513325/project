import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ScrollPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.security.PublicKey;

import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import java.util.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.ByteBuffer;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javafx.application.Platform;
// import javax.sound.sampled.*;

public class playercontroller {

    @FXML
    private Slider slTime;
    @FXML
    private Button btnStop;
    @FXML
    private Button btnPlay;
    @FXML
    private Slider slVolume;
    @FXML
    private Label lbVolume;
    @FXML
    private Button btnOpen;
    @FXML
    private Label lbCurrentTime;
    @FXML
    private Slider slSpeed;
    @FXML
    private Label lbSpeed;
    @FXML
    private MediaView mView;
    @FXML
    private Pane pane;
    @FXML
    private Canvas waveformCanvas1;
    @FXML
    private Canvas waveformCanvas2;
    @FXML
    private ScrollPane sp1;
    @FXML
    private ScrollPane sp2;
    @FXML
    private Pane sp_pane1;
    @FXML
    private Pane sp_pane2;
    @FXML
    private Button fftbutton;
    @FXML
    private Button btnvedio;
    @FXML
    private Button previewButton;
    @FXML
    private Button saveButton;
    @FXML
    private Slider slto;
    @FXML
    private Slider slfrom;
    @FXML
    private Line Lfromline;
    @FXML
    private Line Ltoline;
    @FXML
    private Line Rfromline;
    @FXML
    private Line Rtoline;
    @FXML
    private Button btnBlockPlay;
    @FXML
    private Button btnCut;

    private Double endTime = new Double(0);
    private Double currentTime = new Double(0);
    private java.io.File file = new java.io.File("init.mp3");
    private Media media = new Media(file.toURI().toString());
    private MediaPlayer mplayer = new MediaPlayer(media);
    FileChooser fileChooser = new FileChooser();

    // wavfile
    // private WavFile wf;
    protected ArrayList<Double>[] signal;
    protected ArrayList<Double>[] signal_modify;
    protected ArrayList<Double>[] signal_temp;
    protected ArrayList<Double>[] signal_cut;
    // some useful signal properties
    // private int sampleRate;
    private double blockstarttime = 0;
    private double blockendtime = 100;

    // play by signal sample flag
    // private boolean platBySampleFlag = false;
    private static Thread td;
    private double pauseTime;
    // private Play player;

    public void start(Stage primarytStage) {

        mView.fitWidthProperty().bind(pane.widthProperty());
        mView.fitHeightProperty().bind(pane.heightProperty());

        mplayer.setOnEndOfMedia(() -> {
            mplayer.stop();
            btnPlay.setText("Play");
        });

    }

    double vol = 50;
    double last_vol = 1;
    double speed = 1;

    public void initialize() {
        // player = new Play();
        slVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                // show changes
                vol = (double) newValue.intValue() / 50;
                if (vol == 0) {
                    vol = 1;
                }
                lbVolume.setText(String.valueOf(vol));

                // modify signal
                double constant = signal[0].size() / signal_modify[0].size();
                signal_temp = new ArrayList[signal_modify.length];
                for (int channel = 0; channel < signal.length; channel++) {
                    signal_temp[channel] = new ArrayList(signal_modify[channel]);
                    for (int x = 0; x < signal_temp[channel].size(); x++) {
                        // use original signal to modify sound
                        signal_temp[channel].set(x, signal_modify[channel].get(x * (int) constant) * (vol / last_vol));
                    }
                }
                last_vol = vol;
                drawWaveform(signal_temp);
                signal_modify = signal_temp;
            }
        });

        slSpeed.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                speed = newValue.doubleValue();
                speed = Double.parseDouble(String.format("%.2f", speed));
                lbSpeed.setText(String.valueOf(speed));

                // modify signal
                // signal_temp = new ArrayList[signal_modify.length];
                // for (int channel = 0; channel < signal.length; channel++) {
                // signal_temp[channel] = new ArrayList(signal_modify[channel]);
                // }
            }
        });

        fileChooser.setTitle("Open Media...");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("WAV Music", "*.wav"),
                new FileChooser.ExtensionFilter("MP3 Music", "*.mp3"),
                new FileChooser.ExtensionFilter("MP4 Video", "*.mp4"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        slfrom.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                double x = newValue.doubleValue();
                // System.out.println("start: " + x);
                blockstarttime = x;
                drawFromTimeLine(waveformCanvas1.getWidth() * (x / 100));
            }
        });

        slto.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                double x = newValue.doubleValue();
                // System.out.println("end: " + x);
                blockendtime = x;
                drawToTimeLine(waveformCanvas1.getWidth() * (x / 100));
            }
        });

    }

    @FXML
    void PlayClick(ActionEvent event) {
        if (btnPlay.getText().equals("Play")) {
            btnPlay.setText("Pause");
            // playBySample(signal_modify, 0, signal_modify[0].size() /
            // WavFile.getSampleRate());
            // mplayer.play();
            // player.play();
            playBySample(signal_modify, pauseTime, signal_modify[0].size() / WavFile.getSampleRate());
        } else {
            btnPlay.setText("Play");
            td.stop();
            // mplayer.pause();
        }
    }

    @FXML
    void StopClick(final ActionEvent event) {
        // mplayer.stop();
        // player.stop();
        pauseTime = 0;
        td.stop();
        btnPlay.setText("Play");
        drawCurrentTimeLine(0);
        signal_modify = EQ.lowPass(signal);
        drawWaveform(signal_modify);
    }

    @FXML
    void btnOpenClick(ActionEvent event) throws IOException {
        double sp = slSpeed.getValue();
        file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            mplayer.stop();
            // btnPlay.setText("Pause");
            media = new Media(file.toURI().toString());
            mplayer = new MediaPlayer(media);
            mView.setMediaPlayer(mplayer);
            mplayer.setOnReady(() -> {
                endTime = mplayer.getStopTime().toSeconds();
            });
            mplayer.setOnEndOfMedia(() -> {
                mplayer.stop();
                mplayer.seek(Duration.ZERO);
                btnPlay.setText("Play");
            });

            mplayer.setOnStopped(() -> {
                mplayer.setStopTime(mplayer.getMedia().getDuration());
                mplayer.setStartTime(Duration.ZERO);
            });

            mplayer.setOnPaused(() -> {
                mplayer.setStopTime(mplayer.getMedia().getDuration());
                mplayer.setStartTime(mplayer.getCurrentTime());
            });

            mplayer.currentTimeProperty().addListener(ov -> {
                currentTime = mplayer.getCurrentTime().toSeconds();
                lbCurrentTime.setText(Seconds2Str(currentTime) + "/" + Seconds2Str(endTime));
                // draw current time line
                drawCurrentTimeLine(currentTime);
                // slTime.setValue(currentTime / endTime * 100);
            });
            slTime.valueProperty().addListener(ov -> {
                if (slTime.isValueChanging()) {
                    // mplayer.seek(mplayer.getTotalDuration().multiply(slTime.getValue() / 100));
                    pauseTime = signal_modify[0].size() * slTime.getValue() / (100 * WavFile.getSampleRate());
                    // mplayer.seek(Duration
                    // .seconds(signal_modify[0].size() * slTime.getValue() / (100 *
                    // WavFile.getSampleRate())));
                    // System.out.print("ddd");
                }
            });
            mplayer.volumeProperty().bind(slVolume.valueProperty().divide(100));
            mplayer.setRate(1);
            slSpeed.valueProperty().addListener(ov -> {
                if (slSpeed.isValueChanging()) {
                    mplayer.setRate(slSpeed.getValue());
                }
            });

            // read wav file and draw waveform
            // save in signal arraylist(for original soundtrack) and signal_modify
            // arraylist(for modify)

            // wf = new WavFile();
            WavFile.read(file.getAbsolutePath());
            signal = WavFile.getSignal();
            // sampleRate = WavFile.getSampleRate();
            modifyArrayList();
            drawWaveform(signal);

            // mplayer.play();

            // pass to FFTController now
            // FFTController.set(wf);

        }
    }

    @FXML
    void previewButtonClick(ActionEvent event) {
        Thread t = new Thread();
        if (previewButton.getText().equals("preview")) {
            previewButton.setText("stop");
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    // playBySample(signal_modify, 0, 10);
                }
            });
            t.start();
        } else {
            previewButton.setText("preview");
            t.interrupt();
        }
    }

    // timeline canvas
    @FXML
    void sp_paneMousePressed(MouseEvent event) {
        int interval;
        double x, timeClick;
        if (btnPlay.getText().equals("Play")) {
            interval = signal_modify[0].size() / (int) waveformCanvas1.getWidth();
            x = event.getX();
            // find the time correspond to the x
            timeClick = (x * interval) / WavFile.getSampleRate();
            pauseTime = timeClick;
            drawCurrentTimeLine(timeClick);
        } else {
            td.stop();
            interval = signal_modify[0].size() / (int) waveformCanvas1.getWidth();
            x = event.getX();
            // find the time correspond to the x
            timeClick = (x * interval) / WavFile.getSampleRate();
            pauseTime = timeClick;
            drawCurrentTimeLine(timeClick);
            playBySample(signal_modify, pauseTime, signal_modify[0].size() / WavFile.getSampleRate());
        }

        // System.out.println(timeClick + "\t" + slTime.getValue() + "\t" +
        // Duration.seconds(timeClick));
        // mplayer.seek(Duration.seconds(timeClick));
        // slTime.setValue(timeClick);
        // mplayer.seek(mplayer.getTotalDuration().multiply(slTime.getValue() / 100));

    }

    @FXML
    void fftClick(ActionEvent event) throws Exception {
        FFTDisplay fd = new FFTDisplay();
        fd.setSignal(signal_modify);
        fd.start(new Stage());
    }

    @FXML
    void btnVedioClick(ActionEvent event) throws Exception {
        vedioplayer vp = new vedioplayer();
        vp.start(new Stage());
    }

    @FXML

    void saveButtonClick(ActionEvent event) {

        WavFile.saveAsWav(signal_modify);

    }

    @FXML
    void btnBlockPlayClick(ActionEvent event) {
        // mplayer.setStartTime(mplayer.getTotalDuration().multiply(blockstarttime /
        // 100));
        // more accurate(?)
        td.stop();
        double start = (signal[0].size() * blockstarttime / 100) / WavFile.getSampleRate();
        double end = (signal[0].size() * blockendtime / 100) / WavFile.getSampleRate();
        // mplayer.setStartTime(Duration.seconds(start));
        // mplayer.play();
        btnPlay.setText("Pause");
        playBySample(signal_modify, start, end);

    }

    @FXML
    void CutClick(ActionEvent event) {

        // int start = (int) ((blockstarttime / 100) * signal[0].size());
        double start = (signal[0].size() * blockstarttime / 100) / WavFile.getSampleRate();
        // int end = (int) ((blockendtime / 100) * signal[0].size());
        double end = (signal[0].size() * blockendtime / 100) / WavFile.getSampleRate();
        WavCut(start, end);
        WavFile.saveAsWav(signal_cut);

    }

    private String Seconds2Str(Double seconds) {
        Integer count = seconds.intValue();
        final Integer Hours = count / 3600;
        count = count % 3600;
        Integer Minutes = count / 60;
        count = count % 60;
        String str = Hours.toString() + ":" + Minutes.toString() + ":" + count.toString();
        return str;
    }

    // use to draw waveform
    private void drawWaveform(ArrayList<Double>[] input) {
        // clean canvas
        int interval_temp = input[0].size() / (int) waveformCanvas1.getWidth();
        GraphicsContext gc1 = waveformCanvas1.getGraphicsContext2D();
        GraphicsContext gc2 = waveformCanvas2.getGraphicsContext2D();
        gc1.clearRect(0, 0, waveformCanvas1.getWidth(), waveformCanvas1.getHeight());
        gc2.clearRect(0, 0, waveformCanvas2.getWidth(), waveformCanvas2.getHeight());
        int max = 100;
        int y_base = (int) waveformCanvas1.getHeight() / 2;
        gc1.strokeLine(0, y_base, waveformCanvas1.getWidth(), y_base);
        gc2.strokeLine(0, y_base, waveformCanvas2.getWidth(), y_base);
        for (int x = 0; x < waveformCanvas1.getWidth(); x++) {
            for (int channel = 0; channel < input.length; channel++) {
                if (channel % 2 == 0) {
                    gc1.strokeLine(x, y_base - (int) (input[channel].get(x * interval_temp) * max), x + 1,
                            y_base - (int) (input[channel].get((x + 1) * interval_temp) * max));
                } else if (channel % 2 != 0) {
                    gc2.strokeLine(x, y_base - (int) (input[channel].get(x * interval_temp) * max), x + 1,
                            y_base - (int) (input[channel].get((x + 1) * interval_temp) * max));
                }
            }
        }
    }

    // use to draw current timeline
    public synchronized void drawCurrentTimeLine(double time) {
        // static double lastTime;

        int sampleRate = WavFile.getSampleRate();
        int interval = signal_modify[0].size() / (int) waveformCanvas1.getWidth();
        double x = ((double) sampleRate * time) / (double) interval;
        sp_pane1.getChildren().clear();
        sp_pane2.getChildren().clear();
        sp_pane1.getChildren().add(waveformCanvas1);
        sp_pane2.getChildren().add(waveformCanvas2);
        sp_pane1.getChildren().add(Lfromline);
        sp_pane1.getChildren().add(Ltoline);
        sp_pane2.getChildren().add(Rfromline);
        sp_pane2.getChildren().add(Rtoline);
        // draw on scroller panel
        Line newTimeline1 = new Line(x, 0, x, sp1.getHeight());
        Line newTimeline2 = new Line(x, 0, x, sp2.getHeight());
        sp_pane1.getChildren().add(newTimeline1);
        sp_pane2.getChildren().add(newTimeline2);

        // set slider label
        lbCurrentTime.setText(
                Seconds2Str(time) + "/" + Seconds2Str((double) signal_modify[0].size() / WavFile.getSampleRate()));
        slTime.setValue(100 * time * WavFile.getSampleRate() / signal_modify[0].size());
    }

    private void drawFromTimeLine(double time) {
        Lfromline.setVisible(true);
        Lfromline.setStartX(time);
        Lfromline.setStartY(0);
        Lfromline.setEndX(time);
        Lfromline.setEndY(sp_pane1.getHeight() + 3);

        Rfromline.setVisible(true);
        Rfromline.setStartX(time);
        Rfromline.setStartY(0);
        Rfromline.setEndX(time);
        Rfromline.setEndY(sp_pane2.getHeight() + 3);

    }

    public void modifyArrayList() {
        signal_modify = new ArrayList[signal.length];
        for (int channel = 0; channel < signal.length; channel++) {
            signal_modify[channel] = new ArrayList(signal[channel]);
        }
    }

    private void drawToTimeLine(double time) {
        Ltoline.setVisible(true);
        Ltoline.setStartX(time);
        Ltoline.setStartY(0);
        Ltoline.setEndX(time);
        Ltoline.setEndY(sp_pane1.getHeight() + 3);

        Rtoline.setVisible(true);
        Rtoline.setStartX(time);
        Rtoline.setStartY(0);
        Rtoline.setEndX(time);
        Rtoline.setEndY(sp_pane2.getHeight() + 3);
    }

    public void tempArrayList() {
        signal_modify = new ArrayList[signal.length];

        for (int channel = 0; channel < signal.length; channel++) {
            signal_cut[channel] = new ArrayList<Double>(signal[channel]);
        }
    }

    public void WavCut(double start, double end) {
        signal_cut = new ArrayList[signal.length];
        int startPos = (int) start * WavFile.getSampleRate();
        int endPos = (int) end * WavFile.getSampleRate();
        for (int channel = 0; channel < signal.length; channel++) {
            signal_cut[channel] = new ArrayList<Double>();
            for (int x = startPos; x <= endPos; x++) {
                signal_cut[channel].add(signal[channel].get(x));
            }
        }
    }

    public void playBySample(ArrayList<Double>[] input, double startTime, double endTime) {
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
                        }

                        index = 0;
                        soundLine.write(buffer, 0, bufferSize);
                        // double temp = x;
                        pauseTime = (double) x / WavFile.getSampleRate();
                        Platform.runLater(() -> {
                            drawCurrentTimeLine(pauseTime);
                            if (pauseTime >= endTime) {
                                btnPlay.setText("Play");
                                drawCurrentTimeLine(endTime);
                            }
                        });
                    }
                } catch (LineUnavailableException e) {
                    System.out.println(e.getMessage());
                }

            }

        });
        td.start();
    }

}