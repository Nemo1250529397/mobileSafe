package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import nemo.com.mobilesafe.receiver.SMSReceiver;

/**
 * Created by nemo on 16-6-15.
 */
public class Step01Activity extends BaseStepActivity {


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step01_activity);
    }

    public Intent showPrevious() {
        return null;
    }

    public Intent showNext() {
        return new Intent(Step01Activity.this, Step02Activity.class);
    }
}
