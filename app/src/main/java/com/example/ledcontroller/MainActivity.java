package com.example.ledcontroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
    private LColor offColor = new LColor((int)Long.parseLong("FF555555", 16));
    private LColor defaultColor = new LColor(255);

    private DataManager db;

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
                Boolean added = db.insertColor( currentColor.getHex() );

                if ( added ) {
                    Toast.makeText(getApplicationContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set the onChangeListener for the brightnessBar
        brightnessBar.setOnSeekBarChangeListener(seekBarChangeListener);

        // set default starting color
        currentColor = defaultColor;

        // Retrieve and set color and state from database
        //new ColorGrabber().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve and set color and state from database
        new ColorGrabber().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( resultCode == Activity.RESULT_OK ) {
            int hex = 0xFF00FF00;
            hex = data.getIntExtra("color", hex);
            Log.i("info", "From Main " + hex);
            currentColor = new LColor(hex);

            updateActivityColor();

            if ( !state ) {
                toggleLED(toggle);
            }

            // send color to database
            new ColorSender(currentColor.getAlphalessHex(), currentColor.getBrightness(), state).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.favoritesMenuItem:
                Intent viewFavoritesIntent = new Intent(getApplicationContext(), FavoriteColorListActivity.class);
                startActivityForResult(viewFavoritesIntent, REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     *  Updates activity attributes to reflect the current color
     */
    public void updateActivityColor() {
        //new ColorSender(currentColor.getHex(), currentColor.getBrightness(), state).execute();

        // change color text view to reflect current color
        codes.setText(currentColor.getHexString() + '\n' +
                currentColor.getRgbString() + '\n' +
                currentColor.getHsvString());

        setColorScheme(currentColor.getColor());

        // set seekbar to current color's brightness value
        setSeekbar(currentColor.getBrightness());
    }

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
    public void toggleLED(View view) {
        state = !state;
        if ( state ) {
            toggle.setText(R.string.ON);
            if (currentColor.getBrightness() == 0) {
                currentColor.setBrightness(100);
                updateActivityColor();
                new ColorSender(currentColor.getBrightness()).execute();
            }
            else {
                updateActivityColor();
            }
        } else {
            setOffState();
        }

        // send state to database
        new ColorSender(state).execute();

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
                currentColor.setBrightness(progress);
                setColorScheme(currentColor.getColor());
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
                currentColor.setBrightness(seekBar.getProgress());
                updateActivityColor();

            }

            // save result in database
            new ColorSender(currentColor.getBrightness()).execute();
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
                currentColor = new LColor(color);
                updateActivityColor();
                Log.i("info", "int cur_color: "+ currentColor);

                // if activity is in OFF state, reset to OFF state
                if (!state) {
                    setOffState();
                }

                // send color to database
                new ColorSender(currentColor.getAlphalessHex(), currentColor.getBrightness(), state).execute();
            }
        });

        colorPickerDialog.setInitialColor(currentColor.getHex());
        colorPickerDialog.hideOpacityBar();
        colorPickerDialog.show();
    }


    //***************************** Network tasks *******************************

    public class ColorGrabber extends AsyncTask<URL, Void, String> {

        private final String API_URL = "http://173.214.162.225:8080/jersey/rest/colorService/color";

        @Override
        protected String doInBackground(URL... params) {

            HttpURLConnection connection = null;

            try {
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();
                    try {
                        Scanner input = new Scanner(
                                new InputStreamReader(connection.getInputStream()));
                        String line;
                        while (input.hasNext()) {
                            line = input.nextLine();
                            builder.append(line);
                            Log.i("info", "api: " + builder.toString());
                        }
                    } catch (IOException e) {
                        Log.i("info", "TRY: " + e.getMessage());
                    }
                    connection.disconnect();

                    Log.i("info", "api: " + builder.toString());

                    return builder.toString();
                }
                connection.disconnect();

            } catch (Exception e) {
                Log.i("info", "TRY1: " + e.getMessage());
            }

            return null;

        }

        @Override
        protected void onPostExecute(String colorString) {

            try {
                // XML parsing
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(colorString)));
                doc.getDocumentElement().normalize();
                NodeList list = doc.getElementsByTagName("color");
                for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        // set color to retrieved color object
                        long colorLong = Long.parseLong(element.getElementsByTagName("colorInt")
                                .item(0).getTextContent())
                                +0xFF000000;
                        currentColor = new LColor((int)colorLong,
                                Integer.parseInt(element.getElementsByTagName("brightness")
                                        .item(0).getTextContent()));

                        // set state to retrieved state
                        state = Boolean.parseBoolean(element.getElementsByTagName("status").item(0).getTextContent());
                    }

                    Log.i("info", "received color: " + currentColor.getHexString());
                }

                // apply color and state to activity
                updateActivityColor();
                if (!state) {
                    setOffState();
                }

            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }

        }

    }

}
