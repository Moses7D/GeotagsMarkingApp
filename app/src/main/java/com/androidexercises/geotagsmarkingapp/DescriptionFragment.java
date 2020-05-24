package com.androidexercises.geotagsmarkingapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.model.Marker;

/**
 * A simple {@link Fragment} subclass, to input the title and description of the marker(optional operation).
 * The title and description are used in the {@link Marker#setTitle(String)}
 * and {@link Marker#setSnippet(String)} fields respectively.
 */
public class DescriptionFragment extends Fragment implements Resettable{

    public String title;
    public String description;
    private EditText titleEditText,descEditText;

    public DescriptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_description, container, false);
        titleEditText = ((EditText) v.findViewById(R.id.desc_ttl));
        //Sets the Listeners for the title edit text, to save the input of the user
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveTitle(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        descEditText = ((EditText) v.findViewById(R.id.desc_txt));
        //Sets the Listeners for the description edit text, to save the input of the user
        descEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveDescription(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return v;
    }

    /**
     * Saves the input of the user
     * @param s the data passed from the listener
     */
    public void saveTitle(CharSequence s) {
        Log.i("text event", "title event");
        title = s.toString();
        Log.i("text event", "title: " + title);
    }

    /**
     * Saves the input of the user
     * @param s the data passed from the listener
     */
    public void saveDescription(CharSequence s) {
        Log.i("text event", "description event");
        description = s.toString();
        Log.i("text event", "description: " + description);
    }

    @Override
    public void reset() {
        title = null;
        titleEditText.getText().clear();
        description = null;
        descEditText.getText().clear();
    }
}
