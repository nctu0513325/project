import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.Pane;
import java.io.*;
import java.awt.*;
import javax.swing.*;


public class playercontroller{

    @FXML private Slider slTime;
    @FXML private Button btnStop;
    @FXML private Button btnPlay;
    @FXML private Slider slVolume;
    @FXML private Label lbVolume;
    @FXML private Button btnOpen;
    @FXML private Label lbCurrentTime;
    @FXML private Slider slSpeed;
    @FXML private Label lbSpeed;
    @FXML private MediaView mView;
    @FXML private Pane pane;
    @FXML private Button soundwave;
    @FXML private AnchorPane root;

    private final Double endTime = new Double(0);
    private final Double currentTime = new Double(0);
    private java.io.File file = new java.io.File("init.mp3");
    private Media media = new Media(file.toURI().toString());
    private MediaPlayer mplayer = new MediaPlayer(media);
    FileChooser fileChooser = new FileChooser();

    int vol = 50;
    double speed = 1;
    private Stage stage;

    public void initialize() {
        mplayer.setOnEndOfMedia(() -> {
            mplayer.stop();
            btnPlay.setText("Play");
        });

        fileChooser.setTitle("Open Media...");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MP4 Video", "*.mp4"),
                new FileChooser.ExtensionFilter("MP3 Music", "*.mp3"),
                new FileChooser.ExtensionFilter("WMV Music", "*.wmv"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        slVolume.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(final ObservableValue<? extends Number> ov, final Number oldValue,
                    final Number newValue) {
                vol = newValue.intValue();
                lbVolume.setText(String.valueOf(vol));
            }
        });

        slSpeed.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(final ObservableValue<? extends Number> ov, final Number oldValue,
                    final Number newValue) {
                speed = newValue.doubleValue();
                speed = Double.parseDouble(String.format("%.2f", speed));
                lbSpeed.setText(String.valueOf(speed));
            }
        });
    }

    @FXML
    void PlayClick(final ActionEvent event) {
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
    void btnOpenClick(final ActionEvent event) {
        final double sp = slSpeed.getValue();
        file = fileChooser.showOpenDialog(new Stage());
        if (file != null) {
            final String name = file.getName();
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
            mplayer.currentTimeProperty().addListener(ov -> {
                currentTime = mplayer.getCurrentTime().toSeconds();
                lbCurrentTime.setText(Seconds2Str(currentTime) + "/" + Seconds2Str(endTime));
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
            mplayer.play();
        }
    }

    @FXML
    void SoundWaveClick(final ActionEvent event) throws IOException {
        final waveform wf = new waveform();
        wf.main();
    }

    private String Seconds2Str(final Double seconds) {
        Integer count = seconds.intValue();
        final Integer Hours = count / 3600;
        count = count % 3600;
        final Integer Minutes = count / 60;
        count = count % 60;
        final String str = Hours.toString() + ":" + Minutes.toString() + ":" + count.toString();
        return str;
    }
}