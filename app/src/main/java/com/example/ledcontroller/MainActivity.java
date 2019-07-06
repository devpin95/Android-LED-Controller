package com.example.ledcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;

public class MainActivity extends AppCompatActivity {

    // true state = ON state, false state = OFF state
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

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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

    /**
     *  Sets activity attributes to reflect a color
     * @param color a color as an integer value
     */
    public void setColor(int color) {
        current_color = color;
        //Log.i("info", "int sub cur_color: "+current_color);

        // change color text view to reflect current color
        String hexStr = "#" + Integer.toHexString(current_color);
        int[] rgb = getRGB(current_color);
        String rgbStr = "RGB(" + rgb[0] + ", "
                + rgb[1] + ", "
                + rgb[2] + ")";
        codes.setText(hexStr + "\n" + rgbStr);

        setColorScheme(current_color);

        // set seekbar to current color's brightness value
        setSeekbar(getBrightnessFromColor(current_color));
    }

    /**
     *  Calculates a new color value based on a brightness value
     * @param brightness brightness value as an integer between 0 and 100
     * @return a color value as an integer
     */
    public int modifyColorByBrightness(int brightness) {
        // Convert to HSV
        float[] hsv = new float[3];
        Color.colorToHSV(current_color, hsv);
        hsv[2] = brightness/(float)100;

        return Color.HSVToColor(hsv);
    }

    /**
     *  Extracts brightness from integer color value
     * @param color a color value as an integer
     * @return the brightness value of the color as an integer between 0 and 100
     */
    private int getBrightnessFromColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return (int)(hsv[2] * 100);
    }

    /**
     *  Sets state of activity to OFF state
     *  OFF color scheme, text, and 0 seekbar position
     *  state variable set to false
     */
    private void setOffState() {
        state = false;
        toggle.setText(R.string.OFF);
        setColorScheme(off_color);
        setSeekbar(0);
    }


    protected String getActiveColor() {
        return "FF6EC7";
    }

    /**
     *  Gets RGB values from an integer color value
     * @param color a color as an integer value
     * @return integer array with red, green, and blue values, respectively
     */
    private int[] getRGB(int color) {
        int[] rgb = new int[3];
        rgb[0] = Color.red(color);
        rgb[1] = Color.green(color);
        rgb[2] = Color.blue(color);

        return rgb;
    }

    /**
     *  Changes seekbar to reflect a brightness value
     * @param brightness a brightness value as an integer between 0 and 100
     */
    private void setSeekbar(int brightness) {
        brightnessBar.setProgress(brightness);
        brightness_val.setText(brightness + "%");
    }

    /**
     *  Sets Activity color scheme to reflect a color
     *    changes color of all text and seekbar
     * @param color a color value as and integer
     */
    @TargetApi(16)
    protected void setColorScheme(int color) {
        toggle.setTextColor(color);

        brightnessBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        brightnessBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);

        codes.setTextColor(color);
    }

    /**
     *  Toggles state of Activity ON/OFF (onClick)
     *   ON sets main text to on and current_color color scheme
     *   OFF sets main text to off and off_color color scheme
     * @param view android.view.View required for onClick Event
     */
    protected void toggleLED(View view) {
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
            Log.i("debug", e.toString());
        }
    }


    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            if (fromUser) {
                setColorScheme(modifyColorByBrightness(progress));
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
            // called after the user finishes moving the SeekBar
            if (seekBar.getProgress() == 0) {
                setOffState();

            } else {
                state = true;
                toggle.setText(R.string.ON);
                setColor(modifyColorByBrightness(seekBar.getProgress()));
            }
        }
    };

    /**
     *  Initiates Color Picker Dialog (onClick)
     * @param view android.view.View required for onClick event
     */
    public void onColorPress(View view) {
        ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog(this, ColorPickerDialog.DARK_THEME);

        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {

            @Override
            public void onColorPicked(int color, String hexVal) {
                Log.i("info", "int color: "+color);

                // set new color
                setColor(color);
                Log.i("info", "int cur_color: "+current_color);
                // if activity is in OFF state, reset to OFF state
                if (!state) {
                    setOffState();
                }
            }
        });

        colorPickerDialog.setInitialColor(current_color);
        colorPickerDialog.hideOpacityBar();
        colorPickerDialog.show();
    }

}
