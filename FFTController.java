import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import java.util.*;

public class FFTController {

    @FXML
    private Canvas canvas1;

    private ArrayList<Double>[] signal;
    private ArrayList<double[][]> fft_signal;
    // public static WavFile wf = new WavFile();

    private int sampleNum = (int) Math.pow(2, 13);

    public void initialize() {
        signal = WavFile.getSignal();
        fft_signal = FFTImplement.signalDoFFT(signal, sampleNum);
        drawFT();
    }

    public static void set(WavFile input) {
        // wf = input;
    }

    private void drawFT() {
        GraphicsContext gc1 = canvas1.getGraphicsContext2D();
        int E_note2 = 82;
        int F_note2 = 87;
        int G_note2 = 98;
        int A_note2 = 110;
        int B_note2 = 123;
        int C_note3 = 129;
        int D_note3 = 146;
        int E_note3 = 164;
        int F_note3 = 174;
        int G_note3 = 196;
        int A_note3 = 220;
        int B_note3 = 247;
        int C_note4 = 261;
        int D_note4 = 293;
        int E_note4 = 329;
        int G_note4 = 392;
        // C_major
        gc1.setStroke(Color.GREEN);
        gc1.strokeLine(0, canvas1.getHeight() - C_note3, canvas1.getWidth(), canvas1.getHeight() - C_note3);
        gc1.strokeLine(0, canvas1.getHeight() - E_note3, canvas1.getWidth(), canvas1.getHeight() - E_note3);
        gc1.strokeLine(0, canvas1.getHeight() - G_note3, canvas1.getWidth(), canvas1.getHeight() - G_note3);

        // G
        gc1.setStroke(Color.RED);
        gc1.strokeLine(0, canvas1.getHeight() - G_note2, canvas1.getWidth(), canvas1.getHeight() - G_note2);
        gc1.strokeLine(0, canvas1.getHeight() - B_note2, canvas1.getWidth(), canvas1.getHeight() - B_note2);
        gc1.strokeLine(0, canvas1.getHeight() - D_note3, canvas1.getWidth(), canvas1.getHeight() - D_note3);

        // g.drawLine(0, getHeight() - E_note4, getWidth(), getHeight() - E_note4);
        gc1.setFill(Color.BLACK);
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
