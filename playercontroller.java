import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
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

public class playercontroller {

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


    private Double endTime = new Double(0);
    private Double currentTime = new Double(0);
    private java.io.File file = new java.io.File("init.mp3");
    private Media media = new Media(file.toURI().toString());
    private MediaPlayer mplayer = new MediaPlayer(media);
    FileChooser fileChooser = new FileChooser();

    int vol=50;
    double speed=1;
    public void initialize(){

        mplayer.setOnEndOfMedia(() -> { 
            mplayer.stop();
            btnPlay.setText("Play");
        });
        
        fileChooser.setTitle("Open Media...");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("MP4 Video", "*.mp4"),
            new FileChooser.ExtensionFilter("MP3 Music", "*.mp3"),
            new FileChooser.ExtensionFilter("WMV Music", "*.wmv"),
            new FileChooser.ExtensionFilter("All Files", "*.*")
            );
        slVolume.valueProperty().addListener(
            new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> ov,Number oldValue, Number newValue) {
                    vol = newValue.intValue();
                    lbVolume.setText(String.valueOf(vol));
                }
            }
        );

        slSpeed.valueProperty().addListener(
            new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> ov,Number oldValue, Number newValue) {
                    speed = newValue.doubleValue();
                    speed=Double.parseDouble(String.format("%.2f",speed));
                    lbSpeed.setText(String.valueOf(speed));
                }
            }
        );
    }    

    @FXML
    void PlayClick(ActionEvent event) {
        if (btnPlay.getText().equals("Play")){
            btnPlay.setText("Pause");
            mplayer.play();
        }
        else{
            btnPlay.setText("Play");
            mplayer.pause();
        }
    }

    @FXML
    void StopClick(ActionEvent event) {
        mplayer.stop();
        btnPlay.setText("Play");
    }

    @FXML
    void btnOpenClick(ActionEvent event) {
        double sp=slSpeed.getValue();
        file = fileChooser.showOpenDialog(new Stage()); 
        if (file != null){
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
            mplayer.currentTimeProperty().addListener(ov->{
            currentTime = mplayer.getCurrentTime().toSeconds();
            lbCurrentTime.setText(Seconds2Str(currentTime)+"/"+Seconds2Str(endTime));
            slTime.setValue(currentTime/endTime*100);
            });
            slTime.valueProperty().addListener(ov->{
                if (slTime.isValueChanging()){
                    mplayer.seek(mplayer.getTotalDuration().multiply(slTime.getValue()/100));
                }
            });
            mplayer.volumeProperty().bind(slVolume.valueProperty().divide(100)); 
            mplayer.setRate(1);
            slSpeed.valueProperty().addListener(ov->{
                if (slSpeed.isValueChanging()){
                    mplayer.setRate(slSpeed.getValue());
                }
            });
            mplayer.play();
        }
    }

    

    private String Seconds2Str(Double seconds){
        Integer count = seconds.intValue();
        Integer Hours = count / 3600;
        count = count % 3600;
        Integer Minutes = count /60;
        count = count % 60;
        String str = Hours.toString()+":"+Minutes.toString()+":"+count.toString();
        return str;
    }

}
