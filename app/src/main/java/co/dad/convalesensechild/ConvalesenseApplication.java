package co.dad.convalesensechild;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Custom Application class.
 */

public class ConvalesenseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/LakkiReddy-Regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }
}
