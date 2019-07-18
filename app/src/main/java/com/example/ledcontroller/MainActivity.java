package com.example.ledcontroller;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1;

    // true state = ON state, false state = OFF state
    private boolean state = true;

    private Button toggle;
    private SeekBar brightnessBar;
    private TextView brightness_val;
    private TextView codes;
    private FloatingActionButton addFavoritesButton;

    private LColor currentColor;
    private LColor offColor = new LColor(0xFF555555);

    private DataManager db;

//    private int currentColor;
//    private int off_color = 0xFF555555;

    Vibrator vibe;


    @Override
    @TargetApi(16)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DataManager(this);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        toggle = findViewById(R.id.toggle);
        brightnessBar = findViewById(R.id.brightnessBar);
        brightness_val = findViewById(R.id.brightnessTextView);
        codes = findViewById(R.id.colorCodes);
        addFavoritesButton = findViewById(R.id.addFavoriteButton);

        // set up the fab for adding a color to favorites
        addFavoritesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
            }
        });
        addFavoritesButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent viewFavoritesIntent = new Intent(getApplicationContext(), FavoriteColorListActivity.class);
                startActivityForResult(viewFavoritesIntent, REQUEST_CODE);
                return true;
            }
        });

        // set the onChangeListener for the brightnessBar
        brightnessBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // TODO set color and brightnessBar to last saved?
        // set starting color
        currentColor = new LColor(Integer.parseInt(getActiveColor(), 16) + 0xFF000000);
        setColor(currentColor.getHex());
        //setColor(Integer.parseInt(getActiveColor(), 16) + 0xFF000000);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == Activity.RESULT_OK ) {
            int hex = 0xFF00FF00;
            hex = data.getIntExtra("color", hex);
            Log.i("info", "From Main " + hex);
            setColor(hex);
            currentColor.setColor(hex);
            setSeekbar(currentColor.getBrightness());
            if ( state == false ) {
                toggleLED(toggle);
            }
        }
    }

    /**
     *  Sets activity attributes to reflect a color
     * @param color a color as an integer value
     */
    public void setColor(int color) {
//        currentColor = new LColor(color);
        //Log.i("info", "int sub cur_color: "+currentColor);

        // change color text view to reflect current color

        codes.setText(currentColor.getHexString() + '\n' +
                currentColor.getRGBString() + '\n' +
                currentColor.getHSVString());

        setColorScheme(color);

        // set seekbar to current color's brightness value
        setSeekbar(currentColor.getBrightness());
    }

    /**
     *  Calculates a new color value based on a brightness value
     * @param brightness brightness value as an integer between 0 and 100
     * @return a color value as an integer
     */
//    public int modifyColorByBrightness(int brightness) {
//        // Convert to HSV
//        float[] hsv = new float[3];
//        Color.colorToHSV(currentColor, hsv);
//        hsv[2] = brightness/(float)100;
//
//        return Color.HSVToColor(hsv);
//    }

    /**
     *  Extracts brightness from integer color value
     * @param color a color value as an integer
     * @return the brightness value of the color as an integer between 0 and 100
     */
//    private int getBrightnessFromColor(int color) {
//        float[] hsv = new float[3];
//        Color.colorToHSV(color, hsv);
//        return (int)(hsv[2] * 100);
//    }

    /**
     *  Sets state of activity to OFF state
     *  OFF color scheme, text, and 0 seekbar position
     *  state variable set to false
     */
    private void setOffState() {
        state = false;
        toggle.setText(R.string.OFF);
        setColorScheme(offColor.getHex());
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
//    private int[] getRGB(int color) {
//        int[] rgb = new int[3];
//        rgb[0] = Color.red(color);
//        rgb[1] = Color.green(color);
//        rgb[2] = Color.blue(color);
//
//        return rgb;
//    }


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

        // need two arrays to set the color of the FAB
        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                color,
                color,
                color,
                color
        };
        // get a new colorstatelist from the arrays
        ColorStateList mylist = new ColorStateList(states, colors);

        // set the new color of the fab
        addFavoritesButton.setBackgroundTintList(mylist);
    }

    /**
     *  Toggles state of Activity ON/OFF (onClick)
     *   ON sets main text to on and currentColor color scheme
     *   OFF sets main text to off and off_color color scheme
     * @param view android.view.View required for onClick Event
     */
    protected void toggleLED(View view) {
        state = !state;
        if ( state ) {
            toggle.setText(R.string.ON);
            if (currentColor.getBrightness() == 0) {
                setColor(currentColor.modifyColorByBrightness(100));
            }
            else {
                setColor(currentColor.modifyColorByBrightness(currentColor.getBrightness()));
            }
        } else {
            setOffState();
        }

        try {
            vibe.vibrate(10);
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
                setColorScheme(currentColor.modifyColorByBrightness(progress));
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            /*// called when the user first touches the SeekBar
            int prog = seekBar.getProgress();
            if (prog == 0) {
                setColorScheme(currentColor);
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
                setColor(currentColor.modifyColorByBrightness(seekBar.getProgress()));
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
                currentColor.setColor(color);
                setColor(currentColor.getHex());
                Log.i("info", "int cur_color: "+ currentColor);
                // if activity is in OFF state, reset to OFF state
                if (!state) {
                    setOffState();
                }
            }
        });

        colorPickerDialog.setInitialColor(currentColor.getHex());
        colorPickerDialog.hideOpacityBar();
        colorPickerDialog.show();
    }

}
