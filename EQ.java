import java.util.*;

public class EQ extends FFTImplement {
    private static ArrayList<Double>[] signal_modify;

    // public EQ(ArrayList<Double> input) {
    // // signal_modify = input[0].size
    // }

    public static ArrayList<Double>[] lowPass(ArrayList<Double>[] input) {
        fft_signal_arr = new Complex[input[0].size()][];
        part_signal_arr = new Complex[input[0].size()][];
        // clone signal arraylist to another list (don't change original one)
        for (int row = 0; row < input.length; row++) {
            part_signal_arr[row] = new Complex[sampleNum];
            fft_signal_arr[row] = new Complex[sampleNum];
        }

        signal_modify = new ArrayList[input.length];
        for (int channel = 0; channel < input.length; channel++) {
            signal_modify[channel] = new ArrayList<Double>();
        }

        int count = 0;
        double time = 0;
        // number of frequency we get in
        int n = 3;
        for (count = 0; count < input[0].size() - sampleNum; count += sampleNum) {

            // put arraylist into complex 2d array
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < input.length; row++) {
                    part_signal_arr[row][col] = new Complex(input[row].get(count + col), 0);
                    fft_signal_arr[row][col] = new Complex(0, 0);
                }
            }
            // fft -> filter ->ifft
            // fft
            for (int row = 0; row < input.length; row++) {
                fft_signal_arr[row] = FFT.fft(part_signal_arr[row]);
            }
            // filter
            for (int col_filter = 0; col_filter < sampleNum; col_filter++) {
                for (int row_filter = 0; row_filter < input.length; row_filter++) {
                    int fre = (int) ((double) col_filter * (double) WavFile.getSampleRate() / sampleNum);
                    // System.out.println(fre);
                    if ((fre < 200 && fre > 50)) {
                        // System.out.println("hi");
                        // fft_signal_arr[row_filter][col_filter] =
                        // fft_signal_arr[row_filter][col_filter]
                        // .plus(new Complex(100, 100));
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(1.5);
                    }
                }
            }
            // ifft
            for (int row = 0; row < input.length; row++) {
                part_signal_arr[row] = FFT.ifft(fft_signal_arr[row]);
            }

            // add into signal modify
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < input.length; row++) {
                    signal_modify[row]
                            .add(part_signal_arr[row][col].abs() * Math.signum(part_signal_arr[row][col].re()));
                    // signal_modify[row].add(part_signal_arr[row][col].re());
                }
            }
        }

        return signal_modify;
    }

}