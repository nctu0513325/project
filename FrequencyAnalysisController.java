import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.*;
import javafx.event.ActionEvent;
import java.util.*;

public class FrequencyAnalysisController extends FFTImplement {
    @FXML
    private Canvas canvas1;
    @FXML
    private Button btnAnalysis;

    /* something necessary for signal provessign */
    protected ArrayList<double[][]> fft_signal = new ArrayList<double[][]>();
    protected int index[][]; // WavFileeform
    private PlayerController PlayerController;

    // self constructor
    public FrequencyAnalysisController() {
        super((int) Math.pow(2, 13));
    }

    // get signal form playerController
    public void passSignal(PlayerController PlayerController, ArrayList<Double>[] input) {
        this.PlayerController = PlayerController;
        // store original signal in signal_EQ_save
        try {
            signal_modify = new ArrayList[input.length];
            for (int channel = 0; channel < input.length; channel++) {
                signal_modify[channel] = new ArrayList(input[channel]);
            }
        } catch (NullPointerException e) {
            System.out.println(e + "ss");
        }
    }

    public void signalAnalysis(ArrayList<Double>[] input) {
        // part_signal_arr = new Complex[WavFile.getNumChannels()][];
        // fft_signal_arr = new Complex[WavFile.getNumChannels()][];

        // double duration = ((double) sampleNum / WavFile.getSampleRate());
        // // System.out.println("fft every: " + duration + " sec");
        // // build new complex array to store sample point

        // for (int row = 0; row < WavFile.getNumChannels(); row++) {
        // part_signal_arr[row] = new Complex[sampleNum];
        // fft_signal_arr[row] = new Complex[sampleNum];
        // }
        // int count = 0;
        // double time = 0;
        // // number of frequency we get in
        // int n = 10;
        // for (count = 0; count < signal[0].size() - sampleNum; count += sampleNum) {
        // for (int col = 0; col < sampleNum; col++) {
        // for (int row = 0; row < WavFile.getNumChannels(); row++) {
        // part_signal_arr[row][col] = new Complex(signal[row].get(count + col), 0);
        // fft_signal_arr[row][col] = new Complex(0, 0);
        // }
        // }

        // for (int row = 0; row < WavFile.getNumChannels(); row++) {
        // fft_signal_arr[row] = FFT.fft(part_signal_arr[row]);
        // }

        // index = new int[WavFile.getNumChannels()][];
        // for (int row = 0; row < WavFile.getNumChannels(); row++) {
        // index[row] = getNLargestFrequencyIndex(fft_signal_arr[row], n);
        // }
        // time += duration;
        // // System.out.printf("at time %.2f:\t", time);
        // // initialize arraylist array

        // double fre[][] = new double[WavFile.getNumChannels()][n];
        // for (int col = 0; col < n; col++) {
        // for (int row = 0; row < WavFile.getNumChannels(); row++) {
        // if (index[row][col] >= sampleNum / 2) {
        // index[row][col] = sampleNum - index[row][col];
        // }
        // // System.out.println((double) index[row][col] * (double)
        // // WavFile.getSampleRate() / sampleNum);
        // fre[row][col] = (double) index[row][col] * (double) WavFile.getSampleRate() /
        // sampleNum;
        // }
        // }
        // fft_signal.add(fre);
        // // System.out.println(fft_signal.size());

        // }
        // // return fft_signal;

        /* try it again plzzzz */
        part_signal_arr = new Complex[WavFile.getNumChannels()][sampleNum];
        fft_signal_arr = new Complex[WavFile.getNumChannels()][];
        for (int count = 0; count < input[0].size() - sampleNum; count += sampleNum) {
            // initialize
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < WavFile.getNumChannels(); row++) {
                    part_signal_arr[row][col] = new Complex(input[row].get(count + col), 0);
                }
            }
            // do fft
            for (int row = 0; row < WavFile.getNumChannels(); row++) {
                fft_signal_arr[row] = FFT.fft(part_signal_arr[row]);
            }
            // make map
            Map<Integer, Double> map = new HashMap<Integer, Double>();
            for (int col = 0; col < sampleNum; col++) {
                map.put(col, Math.abs(fft_signal_arr[0][col].re()));
                // System.out.println(col * WavFile.getSampleRate() / sampleNum + ":\t" +
                // fft_signal_arr[0][col]);
            }
            List<Map.Entry<Integer, Double>> list_Data = new ArrayList<Map.Entry<Integer, Double>>(map.entrySet());
            Collections.sort(list_Data, new Comparator<Map.Entry<Integer, Double>>() {
                public int compare(Map.Entry<Integer, Double> entry1, Map.Entry<Integer, Double> entry2) {
                    if ((Double) entry1.getValue() > (Double) entry2.getValue()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            Map<Integer, Double> linkedHashMap = new LinkedHashMap<>();
            int k = 0;
            for (Map.Entry e : list_Data) {
                if (k == 5) {
                    System.out.println();
                    break;
                }
                linkedHashMap.put((Integer) e.getKey(), (Double) e.getValue());
                Integer temp = (Integer) e.getKey();
                if (temp >= sampleNum / 2) {
                    temp = sampleNum - temp;
                }
                System.out.print(temp * WavFile.getSampleRate() / sampleNum + ": " + (Double) e.getValue() + "\t");
                k++;
            }

            // System.out.println("linkedHashMap : " + linkedHashMap);
        }

    }

    @FXML
    void btnAnalysisClick(ActionEvent event) {
        signalAnalysis(signal_modify);
        drawFT();
    }

    public int[] getNLargestFrequencyIndex(Complex[] input, int n) {

        int[] indexArr = new int[n];
        sampleRate = WavFile.getSampleRate();
        ArrayList<Integer> index = new ArrayList<Integer>();
        double a, b;
        // init
        for (int i = 0; i < n; i++) {
            index.add(1);
        }
        int temp = 0, max = 0;
        double fre;
        for (int i = 1; i < sampleNum / 2; i++) {
            // a = Math.pow(input[i].re(), 2) + Math.pow(input[i].im(), 2);
            a = Math.abs(input[i].re());
            // System.out.println(i + ": " + a);
            fre = ((double) i * (double) WavFile.getSampleRate() / sampleNum);
            if (i >= sampleNum / 2) {
                temp = sampleNum - i;
                fre = ((double) temp * (double) WavFile.getSampleRate() / sampleNum);
            }
            // System.out.println(fre);
            // compare with those inside index array
            for (int j = 0; j < n; j++) {
                // b = Math.pow(input[index.get(j)].re(), 2) +
                // Math.pow(input[index.get(j)].re(), 2);
                b = Math.abs(input[j].re());
                // range of fft
                if (a > b && fre > 80 && fre < 300) {
                    // if (a > b) {
                    index.add(j, temp);
                    // System.out.println(fre);
                    break;
                }
            }
            if (index.size() > n) {
                index.remove(index.size() - 1);
            }
        }
        for (int i = 0; i < n; i++) {
            System.out.print(index.get(i) + "\t");
            if ((i + 1) % n == 0) {
                System.out.println();
            }
            indexArr[i] = index.get(i);
        }
        return indexArr;
    }

    private void drawFT() {
        GraphicsContext gc1 = canvas1.getGraphicsContext2D();
        gc1.clearRect(0, 0, canvas1.getWidth(), canvas1.getHeight());
        double E_2 = 82.41;
        double F_2 = 87.31;
        double Fsharp_2 = 92.50;
        double G_2 = 98;
        double A_2 = 110;
        double B_2 = 123;
        double C_3 = 129;
        double D_3 = 146;
        double E_3 = 164;
        double F_3 = 174;
        double G_3 = 196;
        double A_3 = 220;
        double B_3 = 247;
        double C_4 = 261;
        double D_4 = 293;
        double E_4 = 329;
        double G_4 = 392;
        // C_major
        // gc1.setStroke(Color.GREEN);
        // gc1.strokeLine(0, canvas1.getHeight() - C_note3, canvas1.getWidth(),
        // canvas1.getHeight() - C_note3);
        // gc1.strokeLine(0, canvas1.getHeight() - E_note3, canvas1.getWidth(),
        // canvas1.getHeight() - E_note3);
        // gc1.strokeLine(0, canvas1.getHeight() - G_note3, canvas1.getWidth(),
        // canvas1.getHeight() - G_note3);

        // G
        // gc1.setStroke(Color.RED);
        // gc1.strokeLine(0, canvas1.getHeight() - G_note2, canvas1.getWidth(),
        // canvas1.getHeight() - G_note2);
        // gc1.strokeLine(0, canvas1.getHeight() - B_note2, canvas1.getWidth(),
        // canvas1.getHeight() - B_note2);
        // gc1.strokeLine(0, canvas1.getHeight() - D_note3, canvas1.getWidth(),
        // canvas1.getHeight() - D_note3);
        gc1.setStroke(Color.RED);
        gc1.strokeLine(0, canvas1.getHeight() - E_2, canvas1.getWidth(), canvas1.getHeight() - E_2);
        gc1.strokeLine(0, canvas1.getHeight() - F_2, canvas1.getWidth(), canvas1.getHeight() - F_2);
        gc1.strokeLine(0, canvas1.getHeight() - G_2, canvas1.getWidth(), canvas1.getHeight() - G_2);
        gc1.strokeLine(0, canvas1.getHeight() - A_3, canvas1.getWidth(), canvas1.getHeight() - A_3);
        gc1.strokeLine(0, canvas1.getHeight() - B_3, canvas1.getWidth(), canvas1.getHeight() - B_3);
        gc1.strokeLine(0, canvas1.getHeight() - C_3, canvas1.getWidth(), canvas1.getHeight() - C_3);
        gc1.strokeLine(0, canvas1.getHeight() - D_3, canvas1.getWidth(), canvas1.getHeight() - D_3);
        gc1.strokeLine(0, canvas1.getHeight() - F_3, canvas1.getWidth(), canvas1.getHeight() - F_3);
        gc1.strokeLine(0, canvas1.getHeight() - G_3, canvas1.getWidth(), canvas1.getHeight() - G_3);

        gc1.setFill(Color.BLACK);
        for (int time = 0; time < fft_signal.size(); time++) {
            // channel number
            for (int channel = 0; channel < fft_signal.get(time).length; channel++) {
                for (int freNum = 0; freNum < fft_signal.get(time)[0].length; freNum++) {
                    if (channel % 2 == 0) {
                        gc1.fillRect(time, canvas1.getHeight() - (int) fft_signal.get(time)[channel][freNum], 3, 1);
                    }
                }
            }
        }

    }
}

class compare implements Comparator<Double> {
    public int compare(Double a, Double b) {
        if (a > b) {
            return -1;
        }
        return 1;
    }
}