
/* use this class to implement equalizer
first: get the signal form playController
second: do fft after pressing apply, and pass back to playcontroller in order to preview  */
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.*;

public class TenEQController extends FFTImplement {

    @FXML
    private Slider sl31;

    @FXML
    private Slider sl62;

    @FXML
    private Slider sl125;

    @FXML
    private Slider sl250;

    @FXML
    private Slider sl500;

    @FXML
    private Slider sl1k;

    @FXML
    private Slider sl2k;

    @FXML
    private Slider sl4k;

    @FXML
    private Slider sl8k;

    @FXML
    private Slider sl16k;

    @FXML
    private TextField tf31;

    @FXML
    private TextField tf125;

    @FXML
    private TextField tf62;

    @FXML
    private TextField tf250;

    @FXML
    private TextField tf500;

    @FXML
    private TextField tf1k;

    @FXML
    private TextField tf2k;

    @FXML
    private TextField tf4k;

    @FXML
    private TextField tf8k;

    @FXML
    private TextField tf16k;

    private int band31 = 31;
    private int band62 = 62;
    private int band125 = 125;
    private int band250 = 250;
    private int band500 = 500;
    private int band1k = 1000;
    private int band2k = 2000;
    private int band4k = 4000;
    private int band8k = 8000;
    private int band16k = 16000;

    private double dBGain31 = 0;
    private double dBGain62 = 0;
    private double dBGain125 = 0;
    private double dBGain250 = 0;
    private double dBGain500 = 0;
    private double dBGain1k = 0;
    private double dBGain2k = 0;
    private double dBGain4k = 0;
    private double dBGain8k = 0;
    private double dBGain16k = 0;

    private ArrayList<Double>[] signal_modify; // use to modify signal
    private ArrayList<Double>[] signal_EQ_save; // for save origin signal,restore later
    private ArrayList<Double>[] temp;
    private PlayerController PlayerController;

    /*
     * this function is used to apply user's EQ parameters on signal_modify and pass
     * it back to PlayerController
     */
    @FXML
    void applyButtonPressed(ActionEvent event) throws Exception {
        // signal_modify = EQ.lowPass(signal_modify);
        signal_modify = signal_EQ_save;
        temp = new ArrayList[signal_modify.length];
        for (int channel = 0; channel < signal_modify.length; channel++) {
            temp[channel] = new ArrayList<Double>();
        }

        fft_signal_arr = new Complex[signal_modify[0].size()][];
        part_signal_arr = new Complex[signal_modify[0].size()][];
        // clone signal arraylist to another list (don't change original one)
        for (int row = 0; row < signal_modify.length; row++) {
            part_signal_arr[row] = new Complex[sampleNum];
            fft_signal_arr[row] = new Complex[sampleNum];
        }
        int count = 0;
        double time = 0;
        for (count = 0; count < signal_modify[0].size() - sampleNum; count += sampleNum) {

            // put arraylist into complex 2d array
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < signal_modify.length; row++) {
                    part_signal_arr[row][col] = new Complex(signal_modify[row].get(count + col), 0);
                    fft_signal_arr[row][col] = new Complex(0, 0);
                }
            }
            // fft -> filter ->ifft
            // fft
            for (int row = 0; row < signal_modify.length; row++) {
                fft_signal_arr[row] = FFT.fft(part_signal_arr[row]);
            }
            // filter
            for (int col_filter = 0; col_filter < sampleNum; col_filter++) {
                for (int row_filter = 0; row_filter < signal_modify.length; row_filter++) {
                    double fre = ((double) col_filter * (double) WavFile.getSampleRate() / sampleNum);
                    if (col_filter > sampleNum / 2) {
                        fre = ((double) (sampleNum - col_filter) * (double) WavFile.getSampleRate() / sampleNum);
                    }
                    /* ============================== */
                    /* modify signal by frquency here */
                    /* ============================== */
                    double k = 0;
                    if (fre < band31) {
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(0);
                    } else if ((fre > band31) && (fre < band62)) {
                        k = Math.pow(10, (double) sl31.getValue() / 20);
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band62) && (fre < band125)) {
                        k = Math.pow(10, (double) sl62.getValue() / 20);
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band125) && (fre < band250)) {
                        k = Math.pow(10, (double) sl125.getValue() / 20);
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band250) && (fre < band500)) {
                        k = Math.pow(10, (double) sl250.getValue() / 20);
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band500) && (fre < band1k)) {
                        k = Math.pow(10, (double) sl500.getValue() / 20);
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band1k) && (fre < band2k)) {
                        k = Math.pow(10, (double) sl1k.getValue() / 20);
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band2k) && (fre < band4k)) {
                        k = Math.pow(10, (double) sl2k.getValue() / 20);
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band4k) && (fre < band8k)) {
                        k = Math.pow(10, (double) sl4k.getValue() / 20);
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band8k) && (fre < band16k)) {
                        k = Math.pow(10, (double) sl8k.getValue() / 20);
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    } else if ((fre > band16k)) {
                        k = Math.pow(10, (double) sl16k.getValue() / 20);
                        fft_signal_arr[row_filter][col_filter] = fft_signal_arr[row_filter][col_filter].scale(k);
                    }
                    // System.out.println(k);
                }
            }
            // ifft
            for (int row = 0; row < signal_modify.length; row++) {
                part_signal_arr[row] = FFT.ifft(fft_signal_arr[row]);
            }

            // add into signal modify
            for (int col = 0; col < sampleNum; col++) {
                for (int row = 0; row < signal_modify.length; row++) {
                    temp[row].add(part_signal_arr[row][col].re());
                }
            }

        }
        signal_modify = temp;
        PlayerController.callbackSignal(temp);
    }

    /* this function is used to catch reference that passed from PlayerController */
    public void passSignal(PlayerController PlayerController, ArrayList<Double>[] input) {
        this.signal_modify = input;
        this.PlayerController = PlayerController;
        // store original signal in signal_EQ_save
        try {
            signal_EQ_save = new ArrayList[signal_modify.length];
            for (int channel = 0; channel < signal_modify.length; channel++) {
                signal_EQ_save[channel] = new ArrayList(signal_modify[channel]);
            }
        } catch (NullPointerException e) {
            System.out.println(e);
        }

    }

    /* inititalize */
    public void initialize() {
        // 31Hz band slider and textfield binding
        sl31.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                dBGain31 = newValue.intValue();
                tf31.setText(String.valueOf(dBGain31));
                // tf31.setText(String.format("%.1f", dBGain31));
            }
        });
        // tf31.textProperty().bind(sl31.valueProperty().asString("%.1f"));

        // 62Hz band slider and textfield binding
        sl62.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                dBGain62 = newValue.intValue();
                tf62.setText(String.valueOf(dBGain62));
            }
        });
        // tf62.textProperty().bind(sl62.valueProperty().asString("%.1f"));

        // 125Hz band slider and textfield binding
        sl125.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                dBGain125 = newValue.intValue();
                tf125.setText(String.valueOf(dBGain125));
            }
        });
        // tf125.textProperty().bind(sl125.valueProperty().asString("%.1f"));

        // 250Hz band slider and textfield binding
        sl250.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                dBGain250 = newValue.intValue();
                tf250.setText(String.valueOf(dBGain250));
            }
        });
        // tf250.textProperty().bind(sl250.valueProperty().asString("%.1f"));

        // 500Hz band slider and textfield binding
        sl500.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                dBGain500 = newValue.intValue();
                tf500.setText(String.valueOf(dBGain500));
            }
        });
        // tf500.textProperty().bind(sl500.valueProperty().asString("%.1f"));

        // 1kHz band slider and textfield binding
        sl1k.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                dBGain1k = newValue.intValue();
                tf1k.setText(String.valueOf(dBGain1k));
            }
        });
        // tf1k.textProperty().bind(sl1k.valueProperty().asString("%.1f"));

        // 2kHz band slider and textfield binding
        sl2k.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                dBGain2k = newValue.intValue();
                tf2k.setText(String.valueOf(dBGain2k));
            }
        });
        // tf2k.textProperty().bind(sl2k.valueProperty().asString("%.1f"));

        // 4kHz band slider and textfield binding
        sl4k.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                dBGain4k = newValue.intValue();
                tf4k.setText(String.valueOf(dBGain4k));
            }
        });
        // tf4k.textProperty().bind(sl4k.valueProperty().asString("%.1f"));

        // 8kHz band slider and textfield binding
        sl8k.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                dBGain8k = newValue.intValue();
                tf8k.setText(String.valueOf(dBGain8k));
            }
        });
        // tf8k.textProperty().bind(sl31.valueProperty().asString("%.1f"));

        // 16kHz band slider and textfield binding
        sl16k.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
                dBGain16k = newValue.intValue();
                tf16k.setText(String.valueOf(dBGain16k));
            }
        });
        // tf16k.textProperty().bind(sl16k.valueProperty().asString("%.1f"));
        // get signal data
        // signal_modify = TenEQ.getSignal();

    }

    public void start(Stage primaryStage) {
    }

}