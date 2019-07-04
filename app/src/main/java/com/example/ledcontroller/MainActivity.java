package com.example.ledcontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;

public class MainActivity extends AppCompatActivity {

    private boolean state = true;
    private Button toggle;
    private SeekBar brightness;
    private TextView brightness_val;
    private TextView codes;

    private int current_color;
    private int current_brightness = 100;
    private int off_color = 0xFF555555;

    Vibrator vibe;

    @Override
    @TargetApi(16)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        toggle = findViewById(R.id.toggle);
        brightness = findViewById(R.id.brightnessBar);
        brightness_val = findViewById(R.id.brightnessTextView);
        codes = findViewById(R.id.colorCodes);

        // set the onChangeListener for the brightness bar
        brightness.setOnSeekBarChangeListener(seekBarChangeListener);

        // set all color codes
        current_color = Integer.parseInt(getActiveColor(), 16) + 0xFF000000;
        String rgb = hexToRGB(Integer.toHexString(current_color));
        codes.setText("#" + Integer.toHexString(current_color) + "\n" + rgb );

        setAllColors(current_color);
        brightness_val.setText(Integer.toString(current_brightness) + "%");
    }

    protected String getActiveColor() {
        return "FF6EC7";
    }

    private String hexToRGB(String color) {
        int r =  Integer.valueOf( color.substring( 1, 3 ), 16 );
        int g =  Integer.valueOf( color.substring( 3, 5 ), 16 );
        int b =  Integer.valueOf( color.substring( 5, 7 ), 16 );

        return "RGB(" + Integer.toString(r) + ", "
                + Integer.toString(g) + ", "
                + Integer.toString(b) + ")";
    }

    @TargetApi(16)
    protected void setAllColors(int color) {
        toggle.setTextColor(color);

        brightness.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        brightness.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        codes.setTextColor(color);
    }

    protected void toggleLED(View v) {
        state = !state;
        if ( state ) {
            toggle.setText(getResources().getString(R.string.ON));
            if ( current_brightness == 0) current_brightness = 100;
            brightness.setProgress(current_brightness);
            brightness_val.setText(Integer.toString(current_brightness) + "%");
            setAllColors(current_color);
        } else {
            toggle.setText(getResources().getString(R.string.OFF));
            brightness.setProgress(0);
            brightness_val.setText("0%");
            setAllColors(off_color);
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
//            int prog = brightness.getProgress();
//            Toast.makeText(getApplicationContext(), Integer.toString(prog) + "%", Toast.LENGTH_SHORT)
//                    .show();

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
            int prog = seekBar.getProgress();
            if (prog == 0) {
                setAllColors(current_color);
                toggle.setText(getResources().getString(R.string.ON));
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
            current_brightness = seekBar.getProgress();
            brightness_val.setText(Integer.toString(current_brightness) + "%");
            if (current_brightness == 0) {
                toggle.setText(getResources().getString(R.string.OFF));
                setAllColors(off_color);
            } else {
                toggle.setText(getResources().getString(R.string.ON));
                setAllColors(current_color);
            }
        }
    };


    public void onColorPress(android.view.View view) {
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog(this, ColorPickerDialog.DARK_THEME);

        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
            @Override
            public void onColorPicked(int color, String hexVal) {
                current_color = color;
                setAllColors(current_color);

            }
        });

        colorPickerDialog.show();
    }

}
