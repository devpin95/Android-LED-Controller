package com.example.ledcontroller;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ColorViewHolder> {
    private List<LColor> colorsList;

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        public TextView color;
        public Drawable bg;

        public ColorViewHolder(View view) {
            super(view);
            color = view.findViewById(R.id.colorValue);
            bg = color.getBackground();
        }
    }

    public ColorsAdapter(List<LColor> colorsList) {
        this.colorsList = colorsList;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.color_list_row, parent, false);

        return new ColorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        LColor color = colorsList.get(position);
        holder.color.setText(" ");
        GradientDrawable shape = (GradientDrawable) holder.bg.mutate();
        shape.setColor(color.getHex());
        //holder.color.setBackgroundColor(color.getHex());
    }

    @Override
    public int getItemCount() {
        return colorsList.size();
    }
}
