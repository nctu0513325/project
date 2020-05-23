import java.util.Scanner;
import java.io.*;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class DrawWaveform extends Application {

    private ArrayList<Double>[] signal;
 
    public void start (Stage stage){
        

        WavFile.read("C_major.wav");
        WaveformPanel wp = new WaveformPanel(WavFile.getSignal());

    }
    public static void main(String[] args) {
        launch(args);
    }

}

class WaveformPanel extends Application{

    private ArrayList<Double>[] signal;
    Canvas can = new Canvas(250,250);
    GraphicsContext g = can.getGraphicsContext2D();

    public WaveformPanel(ArrayList<Double>[] signal) {
        this.signal = signal;
        setSize(1500, 500);
    }

    int interval = signal[0].size() / getWidth();
    int y_base;
    int max = 100;

    for(int i = 0; i < getWidth(); i++) {
        // for different channel
        for (int j = 0; j < signal.length; j++) {
            if (j % 2 == 0) {
                g.setfill(Color.BLUE);
                y_base = getHeight() / 3;
            } else {
                g.setfill(Color.GREEN);
                y_base = getHeight() * 2 / 3;
            }
            g.strokeLine(i, y_base - (int) (signal[j].get(i * interval) * max), i + 1,
                     y_base - (int) (signal[j].get((i + 1) * interval) * max));

        }
    }
    
}