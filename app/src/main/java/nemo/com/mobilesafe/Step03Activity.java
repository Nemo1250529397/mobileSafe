package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by nemo on 16-6-15.
 */
public class Step03Activity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step03_activity);
    }

    public void previous(View view) {
        Intent intent = new Intent(Step03Activity.this, Step02Activity.class);
        startActivity(intent);
        finish();
    }

    public void next(View view) {
        Intent intent = new Intent(Step03Activity.this, Step04Activity.class);
        startActivity(intent);
        finish();
    }
}
