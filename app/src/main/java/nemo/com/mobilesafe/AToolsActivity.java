package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by nemo on 16-6-30.
 */
public class AToolsActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atools_activity);
    }

    public void startNumberLocationQuery(View view) {
        Intent intent = new Intent(AToolsActivity.this, TelephoneNumberQueryActivity.class);
        startActivity(intent);
    }
}
