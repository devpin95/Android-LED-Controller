package com.example.ledcontroller;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Uses API to send color, brightness, and state to database
 */
public class ColorSender extends AsyncTask<URL, Void, Void> {

    private final String API_URL = "http://173.214.162.225:8080/jersey/rest/colorService/set";
    private int color;
    private int brightness;
    private boolean state;


    public ColorSender(int color, int brightness, boolean state) {
        this.color = color;
        this.brightness = brightness;
        this.state = state;
    }

    public ColorSender(boolean state) {
        this.color = -1;
        this.brightness = -1;
        this.state = state;
    }

    public ColorSender(int brightness) {
        this.color = -1;
        this.brightness = brightness;
        this.state = false;
    }


    @Override
    protected Void doInBackground(URL... params) {

        HttpURLConnection connection = null;

        try {
            URL url;
            //send state only
            if (color == -1 && brightness == -1) {
                url = new URL(API_URL + "/state/" + state);
            }
            // send brightness only
            else if (color == -1 && brightness > -1 && !state){
                url = new URL(API_URL + "/brightness/" + brightness);
            }
            // send full color
            else {
                url = new URL(API_URL + "/" + color + "/" + brightness + "/" + state);
            }

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
                        Log.i("info", "ColorSender response: "+builder.toString());
                    }
                } catch (IOException e) {
                    Log.i("info", "ColorSender IO: "+e.getMessage());
                }
                connection.disconnect();

                Log.i("info", "api: "+builder.toString());
            }
            connection.disconnect();

        } catch (Exception e) {
            Log.i("info", "ColorSender Ex: "+e.getMessage());
        }

        return null;
    }

}
