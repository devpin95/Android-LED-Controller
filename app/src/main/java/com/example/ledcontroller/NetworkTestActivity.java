package com.example.ledcontroller;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NetworkTestActivity extends AppCompatActivity {

    private TextView wifiView;
    private TextView apiView;
    private TextView errView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_test);

        // get layout attributes
        wifiView = findViewById(R.id.wifiView);
        apiView = findViewById(R.id.apiView);
        errView = findViewById(R.id.errView);

        // start api retrieval tasks
        // active check
        new DataGrabber(0).execute();
        // error check
        new DataGrabber(1).execute();

        // get wifi SSID
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = manager.getConnectionInfo();
        wifiView.setText(wifiInfo.getSSID());

    }

    public void quitTest(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    /**
     * Async Task that pings api endpoints for response check and error messages
     */
    public class DataGrabber extends AsyncTask<URL, Void, String> {

        private final String API_URL = "http://173.214.162.225:8080/jersey/rest/colorService";
        int errorFlag;

        public DataGrabber(int err) {
            errorFlag = err;
        }

        @Override
        protected String doInBackground(URL... urls) {
            HttpURLConnection connection = null;
            URL url;

            if (errorFlag == 1) {
                try {
                    url = new URL(API_URL + "/err");
                    connection = (HttpURLConnection) url.openConnection();
                    int response = connection.getResponseCode();
                    // return err messages
                    if (response == HttpURLConnection.HTTP_OK) {
                        StringBuilder builder = new StringBuilder();
                        try {
                            Scanner input = new Scanner(
                                    new InputStreamReader(connection.getInputStream()));
                            String line;
                            while (input.hasNext()) {
                                line = input.nextLine();
                                builder.append(line);
                                Log.i("info", "netTest response: " + builder.toString());
                            }
                        } catch (IOException e) {
                            Log.i("info", "netTest IO: " + e.getMessage());
                        }
                        connection.disconnect();

                        Log.i("info", "netTest response: " + builder.toString());

                        return builder.toString();
                    }
                    connection.disconnect();
                } catch (Exception e) {
                    Log.i("info", "netTest exception: " + e.getMessage());
                }
            } else {
                try {
                    url = new URL(API_URL + "/color");
                    connection = (HttpURLConnection) url.openConnection();
                    int response = connection.getResponseCode();
                    // return connection report
                    if (response == HttpURLConnection.HTTP_OK) {
                        return "Active";
                    } else {
                        return "Inactive";
                    }
                } catch (Exception e) {
                    Log.i("info", "netTest exception: " + e.getMessage());
                }

            }

            return "none";
        }

        @Override
        protected void onPostExecute(String s) {
            // set text views to reflect results
            if (errorFlag == 1) {
                errView.setText(s);
            } else {
                apiView.setText(s);
            }
        }
    }
}
