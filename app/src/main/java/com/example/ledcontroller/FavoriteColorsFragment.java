package com.example.ledcontroller;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
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
public class FavoriteColorsFragment extends Fragment {

    private DataManager db;
    private RecyclerView recyclerView;
    private ColorsAdapter colorsAdapter;
    private Vibrator vibe;

    private TextView emptyAlert;

    final ArrayList<LColor> colorList = new ArrayList<>();

    private int REQ_CODE = 1;

    public FavoriteColorsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        View itemView = inflater.inflate(R.layout.fragment_colors_list, container, false);

        emptyAlert = itemView.findViewById(R.id.presetColorsDefault);

        db = new DataManager(getContext());

        Cursor cursor = db.getFavoriteColors();
        while(cursor.moveToNext()) {
            String s = cursor.getString((cursor.getColumnIndex("color")));
            int i = Integer.parseInt(s);
            Log.i("info", "ADDING " + i + " to favorite colors list");
            colorList.add(new LColor(i));
        }

        if ( colorList.size() == 0 ) {
            emptyAlert.setVisibility(View.VISIBLE);
        } else {
            emptyAlert.setVisibility(View.GONE);

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

                    //Log.i("info", Integer.toString(color.getHex()));
                    DialogFragment newFragment = DeleteFavoriteColorDialogFragment.newInstance(color.getHex());
                    newFragment.setTargetFragment(FavoriteColorsFragment.this, REQ_CODE);

                    newFragment.show(getFragmentManager(), "dialog");

                    //Toast.makeText(getContext(), "Delete " + color.getHexString() + "?", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == Activity.RESULT_OK ) {
            int color = 0;
            color = data.getIntExtra("color", color);

            Boolean removed = db.deleteColor(color);

            if ( removed ){
                removeColor(color);
            } else {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void removeColor(int color) {
        int pos = -1;

        for ( int i = 0; i < colorList.size(); ++i ) {
            if ( colorList.get(i).getHex() == color ) {
                pos = i;
                break;
            }
        }

        if ( pos == -1 ) {
            Toast.makeText(getActivity(), "Error removing color", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Color removed", Toast.LENGTH_SHORT).show();
            colorList.remove(pos);
            colorsAdapter.notifyItemRemoved(pos);
            colorsAdapter.notifyItemRangeChanged(pos, colorList.size());

            if ( colorList.size() == 0 ) {
                emptyAlert.setVisibility(View.VISIBLE);
            }
        }
    }
}
