package com.example.ledcontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import org.w3c.dom.Text;

public class DeleteFavoriteColorDialogFragment extends DialogFragment {
    public static DeleteFavoriteColorDialogFragment newInstance(int color) {
        Log.i("info", Integer.toString(color));
        DeleteFavoriteColorDialogFragment frag = new DeleteFavoriteColorDialogFragment();
        Bundle args = new Bundle();
        args.putInt("color", color);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //int color = getArguments().getInt("color");

        Bundle args = getArguments();
        final int color = args.getInt("color");

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.delete_color_dialog, null);

        TextView c = v.findViewById(R.id.colorToDelete);
        Drawable bg = c.getBackground();
        GradientDrawable shape = (GradientDrawable) bg.mutate();
        shape.setColor(color);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.deleteColorAlert))
                .setView(v)
                .setPositiveButton(R.string.deleteContinue, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // delete the color
                        notifyToTarget(Activity.RESULT_OK, color);
                    }
                })
                .setNegativeButton(R.string.deleteCancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancel
                        notifyToTarget(Activity.RESULT_CANCELED, color);
                        dismiss();
                    }
                });

        return builder.create();
    }

    private void notifyToTarget(int code, int color) {
        Fragment targetFragment = getTargetFragment();
        if ( targetFragment != null ) {
            Intent intent = new Intent();
            intent.putExtra("color", color);
            targetFragment.onActivityResult(getTargetRequestCode(), code, intent);
        }
    }
}
