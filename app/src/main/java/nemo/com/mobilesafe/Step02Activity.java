package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by nemo on 16-6-15.
 */
public class Step02Activity extends BaseStepActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step02_activity);
    }

    @Override
    public Intent showNext() {
        return new Intent(Step02Activity.this, Step03Activity.class);
    }

    @Override
    public Intent showPrevious() {
        return new Intent(Step02Activity.this, Step01Activity.class);
    }
}
