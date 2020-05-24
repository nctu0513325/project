
// FFTImplement
import java.util.*;

public class FFTImplement {
    private static Complex[][] part_signal_arr;
    private static Complex[][] fft_signal_arr;
    // private static ArrayList<ArrayList<Double>> fft_signal_ArrayList =
    // ArrayList<ArrayList<Double>>();
    private static ArrayList<double[][]> fft_signal = new ArrayList<double[][]>();
    private static int index[][]; // WavFileeform

    public static ArrayList<double[][]> signalDoFFT(WavFile wf, ArrayList<Double>[] signal, int sampleNum) {
        part_signal_arr = new Complex[wf.getnumChannel()][];
        fft_signal_arr = new Complex[wf.getnumChannel()][];

        double duration = ((double) sampleNum / wf.getSampleRate());
        // System.out.println("fft every: " + duration + " sec");
        // build new complex array to store sample point

        for (int row = 0; row < signal.length; row++) {
            part_signal_arr[row] = new Complex[sampleNum];
            fft_signal_arr[row] = new Complex[sampleNum];
        }
        int count = 0;
        double time = 0;
        int n = 3; // number of frequency we get in
        for (count = 0; count < signal[0].size() - sampleNum; count += sampleNum / 2) {
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < signal.length; row++) {
                    part_signal_arr[row][col] = new Complex(signal[row].get(count + col), 0);
                    fft_signal_arr[row][col] = new Complex(0, 0);
                }
            }
            for (int row = 0; row < signal.length; row++) {
                fft_signal_arr[row] = FFT.fft(part_signal_arr[row]);
            }

            index = new int[signal.length][];
            for (int row = 0; row < signal.length; row++) {
                index[row] = getNLargestFrequencyIndex(wf, fft_signal_arr[row], n, sampleNum);
            }
            time += duration / 2;
            // System.out.printf("at time %.2f:\t", time);
            // initialize arraylist array

            double fre[][] = new double[signal.length][n];
            for (int col = 0; col < n; col++) {
                for (int row = 0; row < signal.length; row++) {
                    if (index[row][col] > sampleNum / 2) {
                        index[row][col] = sampleNum - index[row][col];
                    }
                    fre[row][col] = (double) index[row][col] * (double) wf.getSampleRate() / sampleNum;
                }
            }
            // sort and output (checking)
            // for (int row = 0; row < signal.length; row++) {
            // Arrays.sort(fre[row]);
            // }
            // for (int row = 0; row < signal.length; row++) {
            // for (int col = 0; col < n; col++) {
            // if (row % 2 == 0) {
            // System.out.printf("%.2f\t", fre[row][col]);
            // }
            // }
            // }
            // System.out.println();
            fft_signal.add(fre);
            // System.out.println(fft_signal.size());

        }
        return fft_signal;
    }

    public static int[] getNLargestFrequencyIndex(WavFile wf, Complex[] input, int n, int sampleNum) {
        int[] indexArr = new int[n];
        ArrayList<Integer> index = new ArrayList<Integer>();
        double a, b;
        // init
        for (int i = 0; i < n; i++) {
            index.add(0);
        }
        double temp, max = 0;
        int fre;
        for (int i = 0; i < input.length; i++) {
            a = Math.pow(input[i].re(), 2) + Math.pow(input[i].im(), 2);
            fre = (int) ((double) i * (double) wf.getSampleRate() / sampleNum);
            for (int j = 0; j < n; j++) {
                b = Math.pow(input[index.get(j)].re(), 2) + Math.pow(input[index.get(j)].re(), 2);
                // range of fft
                if (a > b && fre > 80 && fre < 500) {
                    index.add(j, i);
                    break;
                }
            }
            if (index.size() > n) {
                index.remove(index.size() - 1);
            }
        }
        for (int i = 0; i < n; i++) {
            indexArr[i] = index.get(i);
        }
        return indexArr;
    }
}