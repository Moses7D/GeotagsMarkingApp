package com.androidexercises.geotagsmarkingapp;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;


/**
 * A simple {@link Fragment} subclass, to pick the colour of the marker (optional operation).
 * Since only the hue of the marker can be changed, a simplified version of the com.madrapps.pikolo project found
 * <a href=https://github.com/jaumebalust/Pikolo>here</a> where the HSL picker has only the hue circle
 * and not the saturation and brightness options.
 */
public class ColourPickerFragment extends Fragment implements Resettable{

    private HSLColorPicker colourPickerView;
    //It is used to output the chosen color.
    private ImageView imgView;
    public float hue;

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
            /**
             * When a colour is selected the colour of the central image (a circle) is changed
             * and the chosen hue is saved.
             */
            public void onColorSelected(int color) {
                if (Build.VERSION.SDK_INT >= 29)
                    imgView.getBackground().setColorFilter(new BlendModeColorFilter(color, BlendMode.SRC));
                else imgView.getBackground().setColorFilter(color, PorterDuff.Mode.SRC);
                Log.i("color", "color returned: " + color);
                hue = getHue(color);
                Log.i("color", "huy returned: " + hue);
            }
        });
        return v;
    }

    /**
     * Splits a colour int into the hue, saturation and brightness channels.
     * @param colour the int value of the colour
     * @return the hue channel
     */
    private float getHue(int colour) {
        float[] hsv = new float[3];
        Color.colorToHSV(colour, hsv);
        return hsv[0];
    }

    @Override
    public void reset() {
        hue=0.0f;
    }
}
