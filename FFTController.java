import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import java.util.*;

public class FFTController {

    @FXML
    private Canvas canvas1;

    private ArrayList<Double>[] signal;
    private ArrayList<double[][]> fft_signal;
    public static WavFile wf = new WavFile();

    private int sampleNum = (int) Math.pow(2, 13);

    public void initialize() {
        signal = wf.getSignal();
        fft_signal = FFTImplement.signalDoFFT(wf, signal, sampleNum);
        drawFT();
    }

    public static void set(WavFile input) {
        wf = input;
    }

    private void drawFT() {
        GraphicsContext gc1 = canvas1.getGraphicsContext2D();
        for (int time = 0; time < fft_signal.size(); time++) {
            // channel number
            for (int channel = 0; channel < fft_signal.get(time).length; channel++) {
                for (int freNum = 0; freNum < fft_signal.get(time)[0].length; freNum++) {
                    if (channel % 2 == 0) {
                        gc1.fillRect(time, canvas1.getHeight() - (int) fft_signal.get(time)[channel][freNum], 2, 1);
                    }
                }
            }
        }

    }

}
