package com.example.glpicking;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    private ExampleGLView view;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.main);
        view = (ExampleGLView) findViewById(R.id.glView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        view.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        view.onResume();
    }
}