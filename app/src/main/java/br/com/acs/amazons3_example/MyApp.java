package br.com.acs.amazons3_example;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by andersonacs on 16/09/15.
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}