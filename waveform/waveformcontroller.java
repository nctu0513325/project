import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

public class waveformcontroller {

    @FXML private ScrollPane panel;
    @FXML private Label lbFrom;
    @FXML private Label lbTo;
    @FXML private Button btnPlay;
    @FXML private Button btnStop;
    
    WaveformPanel wp =new WaveformPanel(WavFile.getSignal());
    panel.add(wp,BorderLayout.CENTER);


    @FXML
    void PlayClick(ActionEvent event) {

    }

    @FXML
    void StopClick(ActionEvent event) {

    }

    @FXML
    void initialize() {
        
    }
}
