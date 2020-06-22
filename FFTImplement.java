
// FFTImplement
import java.util.*;

public class FFTImplement {
    protected static int sampleNum = (int) Math.pow(2, 16);
    protected static int sampleRate;
    protected static Complex[][] part_signal_arr;
    protected static Complex[][] fft_signal_arr;
    // private static ArrayList<ArrayList<Double>> fft_signal_ArrayList =
    // ArrayList<ArrayList<Double>>();
    protected static ArrayList<double[][]> fft_signal = new ArrayList<double[][]>();
    protected static int index[][]; // WavFileeform

    public static ArrayList<double[][]> signalDoFFT(ArrayList<Double>[] signal) {
        part_signal_arr = new Complex[signal[0].size()][];
        fft_signal_arr = new Complex[signal[0].size()][];

        double duration = ((double) sampleNum / WavFile.getSampleRate());
        // System.out.println("fft every: " + duration + " sec");
        // build new complex array to store sample point

        for (int row = 0; row < signal.length; row++) {
            part_signal_arr[row] = new Complex[sampleNum];
            fft_signal_arr[row] = new Complex[sampleNum];
        }
        int count = 0;
        double time = 0;
        // number of frequency we get in
        int n = 3;
        for (count = 0; count < signal[0].size() - sampleNum; count += sampleNum) {
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
                index[row] = getNLargestFrequencyIndex(fft_signal_arr[row], n);
            }
            time += duration;
            // System.out.printf("at time %.2f:\t", time);
            // initialize arraylist array

            double fre[][] = new double[signal.length][n];
            for (int col = 0; col < n; col++) {
                for (int row = 0; row < signal.length; row++) {
                    if (index[row][col] > sampleNum / 2) {
                        index[row][col] = sampleNum - index[row][col];
                    }
                    fre[row][col] = (double) index[row][col] * (double) WavFile.getSampleRate() / sampleNum;
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

    public static int[] getNLargestFrequencyIndex(Complex[] input, int n) {

        int[] indexArr = new int[n];
        sampleRate = WavFile.getSampleRate();
        ArrayList<Integer> index = new ArrayList<Integer>();
        double a, b;
        // init
        for (int i = 0; i < n; i++) {
            index.add(0);
        }
        double temp, max = 0;
        double fre;
        for (int i = 0; i < input.length; i++) {
            a = Math.pow(input[i].re(), 2) + Math.pow(input[i].im(), 2);

            fre = ((double) i * (double) WavFile.getSampleRate() / sampleNum);
            if (i > input.length / 2) {
                fre = ((double) (input.length - i) * (double) WavFile.getSampleRate() / sampleNum);
            }
            for (int j = 0; j < n; j++) {
                b = Math.pow(input[index.get(j)].re(), 2) + Math.pow(input[index.get(j)].re(), 2);
                // range of fft
                if (a > b && fre > 80 && fre < 300) {
                    // if (a > b) {
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