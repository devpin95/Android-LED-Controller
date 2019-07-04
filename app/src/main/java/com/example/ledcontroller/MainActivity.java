package com.example.ledcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;

public class MainActivity extends AppCompatActivity {

    private boolean state = true;
    private Button toggle;
    private SeekBar brightnessBar;
    private TextView brightness_val;
    private TextView codes;

    private int current_color;
    private int off_color = 0xFF555555;

    Vibrator vibe;

    @Override
    @TargetApi(16)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        toggle = findViewById(R.id.toggle);
        brightnessBar = findViewById(R.id.brightnessBar);
        brightness_val = findViewById(R.id.brightnessTextView);
        codes = findViewById(R.id.colorCodes);

        // set the onChangeListener for the brightnessBar
        brightnessBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // TODO set color and brightnessBar to last saved?
        // set starting color
        setColor(Integer.parseInt(getActiveColor(), 16) + 0xFF000000);

    }


    public void setColor(int value) {
        current_color = value;

        // set color text
        String hex = Integer.toHexString(current_color);
        String rgb = hexToRGB(hex);
        codes.setText("#" + hex + "\n" + rgb);

        setColorScheme(current_color);

        // Extract brightness from color
        float[] hsv = new float[3];
        Color.colorToHSV(current_color, hsv);
        int brightness = (int)(hsv[2] * 100);
        resetSeekbar(brightness);
    }


    public void modifyColorByBrightness(int value) {
        // Convert to HSV
        float[] hsv = new float[3];
        Color.colorToHSV(current_color, hsv);
        hsv[2] = value/(float)100;

        setColor(Color.HSVToColor(hsv));
    }

    private void setOffState() {
        toggle.setText(R.string.OFF);
        setColorScheme(off_color);
        resetSeekbar(0);
    }


    protected String getActiveColor() {
        return "FF6EC7";
    }


    private String hexToRGB(String color) {
        int r =  Integer.valueOf( color.substring( 1, 3 ), 16 );
        int g =  Integer.valueOf( color.substring( 3, 5 ), 16 );
        int b =  Integer.valueOf( color.substring( 5, 7 ), 16 );

        return "RGB(" + r + ", "
                + g + ", "
                + b + ")";
    }


    private void resetSeekbar(int brightness) {
        brightnessBar.setProgress(brightness);
        brightness_val.setText(brightness + "%");
    }

    @TargetApi(16)
    protected void setColorScheme(int color) {
        toggle.setTextColor(color);

        brightnessBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        brightnessBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        codes.setTextColor(color);
    }

    protected void toggleLED(View v) {
        state = !state;
        if ( state ) {
            toggle.setText(R.string.ON);
            setColor(current_color);
        } else {
            setOffState();
        }

        try {
            vibe.vibrate(100);
        }
        catch (Exception e) {
            // do nothing
        }
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            if (progress == 0) {
                setOffState();

            } else {
                toggle.setText(R.string.ON);
                modifyColorByBrightness(progress);
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            /*// called when the user first touches the SeekBar
            int prog = seekBar.getProgress();
            if (prog == 0) {
                setColorScheme(current_color);
                toggle.setText(R.string.ON);
            }*/
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            /*// called after the user finishes moving the SeekBar
            if (brightnessBar.getProgress() == 0) {
                setOffState();

            } else {
                modifyColorByBrightness(seekBar.getProgress());
            }*/
        }
    };


    public void onColorPress(View view) {
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog(this, ColorPickerDialog.DARK_THEME);

        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                setColor(color);
                if (!state) {
                    setOffState();
                }

            }
        });

        colorPickerDialog.setInitialColor(current_color);
        colorPickerDialog.show();
    }

}
