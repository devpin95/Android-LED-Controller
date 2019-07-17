package com.example.ledcontroller;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PresetColorsFragment extends Fragment {

    private DataManager db;
    private RecyclerView recyclerView;
    private ColorsAdapter colorsAdapter;
    private Vibrator vibe;


    public PresetColorsFragment() {
        // Required empty public constructor
    }


    @Override
    @TargetApi(16)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        View itemView = inflater.inflate(R.layout.fragment_preset_colors, container, false);
        db = new DataManager(getContext());

        Cursor cursor = db.getFavoriteColors();
        final ArrayList<LColor> colorList = new ArrayList<>();
        while(cursor.moveToNext()) {
            String s = cursor.getString((cursor.getColumnIndex("color")));
            int i = Integer.parseInt(s);
            colorList.add(new LColor(i));
        }

        if ( colorList.size() == 0 ) {
            TextView def = itemView.findViewById(R.id.presetColorsDefault);
            def.setText(getString(R.string.colorListEmpty));
        } else {
            TextView def = itemView.findViewById(R.id.presetColorsDefault);
            def.setText("");

            recyclerView = itemView.findViewById(R.id.presetColorRecycler);

            recyclerView.addOnItemTouchListener(new FavoriteColorListTouchListener(getContext(), recyclerView, new FavoriteColorListTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int pos) {
                    LColor color = colorList.get(pos);
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("color", color.getHex());
                    Log.i("info", "From Fragment" + color.getHexString());
                    getActivity().setResult(Activity.RESULT_OK, resultIntent);
                    getActivity().finish();
                }

                @Override
                public void onLongClick(View view, int pos) {
                    LColor color = colorList.get(pos);

                    try {
                        vibe.vibrate(10);
                    } catch (Exception e) {
                        Log.i("debug", e.toString());
                    }

                    Toast.makeText(getContext(), "Delete " + color.getHexString() + "?", Toast.LENGTH_SHORT).show();
                }
            }));

            colorsAdapter = new ColorsAdapter(colorList);

            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(colorsAdapter);
        }

        return itemView;
    }

}
