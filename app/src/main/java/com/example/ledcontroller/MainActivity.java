package com.example.ledcontroller;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    @Override
    @TargetApi(16)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int start_color = getActiveColor();

//        ActionBar bar = this.getSupportActionBar()
//        bar.setBackgroundDrawable(new ColorDrawable(start_color));

        SeekBar seekBar = findViewById(R.id.brightnessBar);
        seekBar.getProgressDrawable().setColorFilter(start_color, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(start_color, PorterDuff.Mode.SRC_IN);
    }

    protected int getActiveColor() {
        return 0xFFFF6EC7;
    }
}
