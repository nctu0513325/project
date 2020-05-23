import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class waveform extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root =
        FXMLLoader.load(getClass().getResource("drawform.fxml"));
        
        Scene scene = new Scene(root);
        stage.setTitle("waveform"); // displayed in window's title bar
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}