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
    private Button saveButton;

    private Double endTime = new Double(0);
    private Double currentTime = new Double(0);
    private java.io.File file = new java.io.File("init.mp3");
    private Media media = new Media(file.toURI().toString());
    private MediaPlayer mplayer = new MediaPlayer(media);
    FileChooser fileChooser = new FileChooser();

    // wavfile
    private WavFile wf;
    private ArrayList<Double>[] signal;
    private ArrayList<Double>[] signal_modify;
    private ArrayList<Double>[] signal_temp;
    // some useful signal properties
    private int sampleRate;
    private int interval;
    private double blockstarttime = 0;
    private double blockendtime = 100;

    public void start(Stage primarytStage) {

        mView.fitWidthProperty().bind(pane.widthProperty());
        mView.fitHeightProperty().bind(pane.heightProperty());

        mplayer.setOnEndOfMedia(() -> {
            mplayer.stop();
            btnPlay.setText("Play");
        });
    }

    int vol = 50;
    double speed = 1;

    public void initialize() {
        slVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                // show changes
                vol = newValue.intValue();
                lbVolume.setText(String.valueOf(vol));

                // modify signal
                double constant = signal[0].size() / signal_modify[0].size();
                signal_temp = new ArrayList[signal_modify.length];
                for (int channel = 0; channel < signal.length; channel++) {
                    signal_temp[channel] = new ArrayList(signal_modify[channel]);
                    for (int x = 0; x < signal_temp[channel].size(); x++) {
                        signal_temp[channel].set(x, signal[channel].get(x * (int) constant) * ((double) vol / 50));
                    }
                }
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
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP4 Video", "*.mp4"),
                new FileChooser.ExtensionFilter("MP3 Music", "*.mp3"),
                new FileChooser.ExtensionFilter("WAV Music", "*.wav"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));

        slfrom.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                double x = newValue.doubleValue();
                blockstarttime = x;
                drawFromTimeLine(waveformCanvas1.getWidth() * (x / 100));
            }
        });

        slto.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                double x = newValue.doubleValue();
                blockendtime = x;
                drawToTimeLine(waveformCanvas1.getWidth() * (x / 100));
            }
        });
    }

    @FXML
    void PlayClick(ActionEvent event) {
        if (btnPlay.getText().equals("Play")) {
            btnPlay.setText("Pause");
            mplayer.play();
        } else {
            btnPlay.setText("Play");
            mplayer.pause();
        }
    }

    @FXML
    void StopClick(final ActionEvent event) {
        mplayer.stop();
        btnPlay.setText("Play");
    }

    @FXML
    void btnOpenClick(ActionEvent event) throws IOException {
        double sp = slSpeed.getValue();
        file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            mplayer.stop();
            btnPlay.setText("Pause");
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
                slTime.setValue(currentTime / endTime * 100);
            });
            slTime.valueProperty().addListener(ov -> {
                if (slTime.isValueChanging()) {
                    mplayer.seek(mplayer.getTotalDuration().multiply(slTime.getValue() / 100));
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

            wf = new WavFile();
            wf.read(file.getAbsolutePath());
            signal = wf.getSignal();
            interval = signal[0].size() / (int) waveformCanvas1.getWidth();
            sampleRate = wf.getSampleRate();
            modifyArrayList();
            drawWaveform(signal);

            mplayer.play();

            // pass to FFTController now
            FFTController.set(wf);

        }
    }

    // timeline canvas
    @FXML
    void sp_pane1MousePressed(MouseEvent event) {
        double x = event.getX();
        // find the time correspond to the x
        double timeClick = (x * interval) / wf.getSampleRate();
        slTime.setValue(timeClick / endTime * 100);
        drawCurrentTimeLine(timeClick);
        mplayer.seek(mplayer.getTotalDuration().multiply(slTime.getValue() / 100));

    }

    @FXML
    void sp_pane2MousePressed(MouseEvent event) {
        double x = event.getX();
        // find the time correspond to the x
        double timeClick = (x * interval) / wf.getSampleRate();
        slTime.setValue(timeClick / endTime * 100);
        drawCurrentTimeLine(timeClick);
        mplayer.seek(mplayer.getTotalDuration().multiply(slTime.getValue() / 100));

    }

    @FXML
    void fftClick(ActionEvent event) throws Exception {
        FFTDisplay fd = new FFTDisplay();
        fd.start(new Stage());
    }

    @FXML
    void btnVedioClick(ActionEvent event) throws Exception {
        vedioplayer vp = new vedioplayer();
        vp.start(new Stage());
    }

    @FXML
    void btnBlockPlayClick(ActionEvent event) {
        mplayer.setStartTime(mplayer.getTotalDuration().multiply(blockstarttime / 100));
        mplayer.play();
        btnPlay.setText("Pause");

        mplayer.setStopTime(mplayer.getTotalDuration().multiply(blockendtime / 100));
    }

    @FXML
    void CutClick(ActionEvent event) {
                
        int start = (int) ((blockstarttime / 100) * signal[0].size());
        int end = (int) ((blockendtime / 100) * signal[0].size());
        int save = start;
        
        WavCut(start, end, save);
        wf.saveAsWav(wf, signal_cut);
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
                    gc1.strokeLine(x, y_base - (int) (input[channel].get(x * interval) * max), x + 1,
                            y_base - (int) (input[channel].get((x + 1) * interval) * max));
                } else if (channel % 2 != 0) {
                    gc2.strokeLine(x, y_base - (int) (input[channel].get(x * interval) * max), x + 1,
                            y_base - (int) (input[channel].get((x + 1) * interval) * max));
                }
            }
        }
    }

    // use to draw current timeline
    private void drawCurrentTimeLine(double time) {
        // static double lastTime;
        int sampleRate = wf.getSampleRate();
        double x = ((double) sampleRate * time) / (double) interval;
        sp_pane1.getChildren().clear();
        sp_pane2.getChildren().clear();
        sp_pane1.getChildren().add(waveformCanvas1);
        sp_pane2.getChildren().add(waveformCanvas2);
        // draw on scroller panel
        Line newTimeline1 = new Line(x, 0, x, sp1.getHeight());
        Line newTimeline2 = new Line(x, 0, x, sp2.getHeight());
        sp_pane1.getChildren().add(newTimeline1);
        sp_pane2.getChildren().add(newTimeline2);

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

    public void modifyArrayList() {
        signal_modify = new ArrayList[signal.length];
        
        for (int channel = 0; channel < signal.length; channel++) {
            signal_cut[channel] = new ArrayList<Double>(signal[channel]);
        }
    }
    
    public void WavCut(int start, int end, int save){
        signal_cut = new ArrayList[signal.length];

        for (int channel = 0; channel < signal.length; channel++) {
            signal_cut[channel] = new ArrayList<Double>();
        }

        for(int channel=0; channel<signal.length; channel++){
            for(int y=0; y<(end-start); y++){
                signal_cut[channel].add(signal[channel].get(save));
                save++;
            }
            save=start;
        }
    }
}
