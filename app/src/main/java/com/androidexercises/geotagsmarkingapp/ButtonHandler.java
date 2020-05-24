package com.androidexercises.geotagsmarkingapp;


/**
 * Interface that marks a class as a handler of a button, instead of giving a reference to the button needed
 * by a nested {@link androidx.fragment.app.Fragment} within an {@link androidx.fragment.app.FragmentActivity}
 * this activity marks itself as able to manage simple enabling and disabling the button. It can only be used to
 * manage one button (out of many a layout can have), so that this button is supposed to be a "main" button
 * for a series of events.
 */
public interface ButtonHandler {
    /**
     * Disables the button
     */
    void blockButton();

    /**
     * Enables the button
     */
    void unblockButton();
}
