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
    private final double C_2 = 65.41;
    private final double Csharp_2 = 69.30;
    private final double D_2 = 73.42;
    private final double Dsharp_2 = 77.78;
    private final double E_2 = 82.41;
    private final double F_2 = 87.31;
    private final double Fsharp_2 = 92.50;
    private final double G_2 = 98;
    private final double Gsharp_2 = 103.83;
    private final double A_2 = 110;
    private final double Asharp_2 = 116.54;
    private final double B_2 = 123;

    private double C_sum = 0;
    private double Csharp_sum = 0;
    private double D_sum = 0;
    private double Dsharp_sum = 0;
    private double E_sum = 0;
    private double F_sum = 0;
    private double Fsharp_sum = 0;
    private double G_sum = 0;
    private double Gsharp_sum = 0;
    private double A_sum = 0;
    private double Asharp_sum = 0;
    private double B_sum = 0;

    // self constructor
    public FrequencyAnalysisController() {
        super((int) Math.pow(2, 15));
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
                    // System.out.println();
                    break;
                }
                linkedHashMap.put((Integer) e.getKey(), (Double) e.getValue());
                Integer temp = (Integer) e.getKey();
                if (temp >= sampleNum / 2) {
                    temp = sampleNum - temp;
                }
                // System.out.print(temp * WavFile.getSampleRate() / sampleNum + ": " + (Double)
                // e.getValue() + "\t");
                k++;
            }
            // make another map
            getScalePower(list_Data);
            Map<String, Double> scaleMap = new HashMap<String, Double>();
            scaleMap.put("C", C_sum);
            scaleMap.put("C#", Csharp_sum);
            scaleMap.put("D", D_sum);
            scaleMap.put("D#", Dsharp_sum);
            scaleMap.put("E", E_sum);
            scaleMap.put("F", F_sum);
            scaleMap.put("F#", Fsharp_sum);
            scaleMap.put("G", G_sum);
            scaleMap.put("G#", Gsharp_sum);
            scaleMap.put("A", A_sum);
            scaleMap.put("A#", Asharp_sum);
            scaleMap.put("B", B_sum);
            List<Map.Entry<String, Double>> list_scaleData = new ArrayList<Map.Entry<String, Double>>(
                    scaleMap.entrySet());
            Collections.sort(list_scaleData, new Comparator<Map.Entry<String, Double>>() {
                public int compare(Map.Entry<String, Double> entry1, Map.Entry<String, Double> entry2) {
                    if ((Double) entry1.getValue() > (Double) entry2.getValue()) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            k = 0;
            Map<String, Double> linkedScaleHashMap = new LinkedHashMap<>();
            for (Map.Entry e : list_scaleData) {
                if (k == 5) {
                    // System.out.println();
                    break;
                }
                linkedScaleHashMap.put((String) e.getKey(), (Double) e.getValue());
                k++;
            }
            System.out.println(linkedScaleHashMap);

            // System.out.println(C_sum + "\t" + Csharp_sum + "\t" + D_sum + "\t" +
            // Dsharp_sum + "\t" + E_sum + "\t"
            // + F_sum + "\n" + Fsharp_sum + "\t" + G_sum + "\t" + Gsharp_sum + "\t" + A_sum
            // + "\t" + Asharp_sum
            // + "\t" + B_sum);
            System.out.println("============================================================================");
            C_sum = 0;
            Csharp_sum = 0;
            D_sum = 0;
            Dsharp_sum = 0;
            E_sum = 0;
            F_sum = 0;
            Fsharp_sum = 0;
            G_sum = 0;
            Gsharp_sum = 0;
            A_sum = 0;
            Asharp_sum = 0;
            B_sum = 0;
        }

    }

    public void getScalePower(List<Map.Entry<Integer, Double>> input) {
        int index = 0;
        int fre = 0;
        int range = 2;
        for (Map.Entry e : input) {
            index = (int) e.getKey();
            if (index >= sampleNum / range) {
                index = sampleNum - index;
            }
            fre = (int) (index * WavFile.getSampleRate() / sampleNum);
            if ((fre % (int) Math.round(C_2)) <= range || (fre % (int) Math.round(C_2)) >= (C_2 - range)) {
                C_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(Csharp_2)) <= range
                    || (fre % (int) Math.round(Csharp_2)) >= (Csharp_2 - range)) {
                Csharp_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(D_2)) <= range || (fre % (int) Math.round(D_2)) >= (D_2 - range)) {
                D_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(Dsharp_2)) <= range
                    || (fre % (int) Math.round(Dsharp_2)) >= (Dsharp_2 - range)) {
                Dsharp_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(E_2)) <= range || (fre % (int) Math.round(E_2)) >= (E_2 - range)) {
                E_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(F_2)) <= range || (fre % (int) Math.round(F_2)) >= (F_2 - range)) {
                F_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(Fsharp_2)) <= range
                    || (fre % (int) Math.round(F_2)) >= (Fsharp_2 - range)) {
                Fsharp_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(G_2)) <= range || (fre % (int) Math.round(F_2)) >= (G_2 - range)) {
                G_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(Gsharp_2)) <= range
                    || (fre % (int) Math.round(F_2)) >= (Gsharp_2 - range)) {
                Gsharp_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(A_2)) <= range || (fre % (int) Math.round(F_2)) >= (A_2 - range)) {
                A_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(Asharp_2)) <= range
                    || (fre % (int) Math.round(F_2)) >= (Asharp_2 - range)) {
                Asharp_sum += (Double) e.getValue();
            } else if ((fre % (int) Math.round(B_2)) <= range || (fre % (int) Math.round(F_2)) >= (B_2 - range)) {
                B_sum += (Double) e.getValue();
            }
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
        /* f = 2*f for every octive */
        double C_2 = 65.41;
        double Csharp_2 = 69.30;
        double D_2s = 73.42;
        double Dsharp_2 = 77.78;
        double E_2 = 82.41;
        double F_2 = 87.31;
        double Fsharp_2 = 92.50;
        double G_2 = 98;
        double Gsharp_2 = 103.83;
        double A_2 = 110;
        double Asharp_2 = 116.54;
        double B_2 = 123;

        // double E_3 = 164;
        // double F_3 = 174;
        // double Fsharp_3 = 185;
        // double G_3 = 196;
        // double Gsharp_3 = 207.65;
        // double A_3 = 220;
        // double Asharp_3 = 233.08;
        // double B_3 = 247;
        // double C_4 = 261;
        // double Csharp_4 = 277.18;
        // double D_4 = 293;
        // double Dsharp_4 = 311.13;
        // double E_4 = 329;
        // double F_4 = 349.23;
        // double Fsharp_4 = 269.99;
        // double G_4 = 392;
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
        // gc1.strokeLine(0, canvas1.getHeight() - E_2, canvas1.getWidth(),
        // canvas1.getHeight() - E_2);
        // gc1.strokeLine(0, canvas1.getHeight() - F_2, canvas1.getWidth(),
        // canvas1.getHeight() - F_2);
        // gc1.strokeLine(0, canvas1.getHeight() - G_2, canvas1.getWidth(),
        // canvas1.getHeight() - G_2);
        // gc1.strokeLine(0, canvas1.getHeight() - A_3, canvas1.getWidth(),
        // canvas1.getHeight() - A_3);
        // gc1.strokeLine(0, canvas1.getHeight() - B_3, canvas1.getWidth(),
        // canvas1.getHeight() - B_3);
        // gc1.strokeLine(0, canvas1.getHeight() - C_3, canvas1.getWidth(),
        // canvas1.getHeight() - C_3);
        // gc1.strokeLine(0, canvas1.getHeight() - D_3, canvas1.getWidth(),
        // canvas1.getHeight() - D_3);
        // gc1.strokeLine(0, canvas1.getHeight() - F_3, canvas1.getWidth(),
        // canvas1.getHeight() - F_3);
        // gc1.strokeLine(0, canvas1.getHeight() - G_3, canvas1.getWidth(),
        // canvas1.getHeight() - G_3);

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