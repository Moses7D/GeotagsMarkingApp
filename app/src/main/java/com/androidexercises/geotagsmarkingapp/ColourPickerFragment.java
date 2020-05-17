package com.androidexercises.geotagsmarkingapp;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class ColourPickerFragment extends Fragment {

    private HSLColorPicker colourPickerView;
    private ImageView imgView;
    private int colour;


    public ColourPickerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_colour_picker, container, false);
        colourPickerView = v.findViewById(R.id.colour_picker);
        imgView = v.findViewById(R.id.colour_picker_img);
        colourPickerView.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                if (Build.VERSION.SDK_INT >= 29)
                    imgView.getBackground().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC));
                else imgView.getBackground().setColorFilter(color, PorterDuff.Mode.SRC);
                colour = color;
            }
        });

        return v;
    }

    public int getColour() {
        return colour;
    }
}
