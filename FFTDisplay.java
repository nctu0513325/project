import java.util.ArrayList;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FFTDisplay extends Application {
    private static ArrayList<Double>[] signal;

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("fftScene.fxml"));

        Scene scene = new Scene(root);
        stage.setTitle("FFT"); // displayed in window's title bar
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void setSignal(ArrayList<Double>[] input) {
        signal = input;
    }

    public static ArrayList<Double>[] getSignal() {
        return signal;
    }
}