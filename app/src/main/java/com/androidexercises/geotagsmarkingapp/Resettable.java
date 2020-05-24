package com.androidexercises.geotagsmarkingapp;

/**
 * Interface that marks a class as resettable, meaning that it's crucial data can be reset through
 * the call of a method without the need to create a new instance of the class form scratch. Used by
 * {@link androidx.fragment.app.Fragment} children.
 */
public interface Resettable {
    /**
     * The class must clean it's cached data collected.
     */
    void reset();
}
