package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by nemo on 16-6-15.
 */
public class Step03Activity extends BaseStepActivity implements View.OnClickListener {
    private EditText etSafeNum = null;
    private Button btSelCon = null;

    private static final int REQUESTCODE = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step03_activity);

        etSafeNum = (EditText) findViewById(R.id.et_step03_safenum);
        btSelCon = (Button) findViewById(R.id.btn_step03_selcon);

        btSelCon.setOnClickListener(this);
    }

    @Override
    public Intent showNext() {
        return new Intent(Step03Activity.this, Step04Activity.class);
    }

    @Override
    public Intent showPrevious() {
        return new Intent(Step03Activity.this, Step02Activity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_step03_selcon: {
                Intent intent = new Intent(this, SelectContactActivity.class);
                startActivityForResult(intent, REQUESTCODE);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUESTCODE) {
            if(100 == resultCode) {
                String name = data.getStringExtra("Name");
                String telephoneNumber = data.getStringExtra("telephoneNumber");

                etSafeNum.setText(telephoneNumber);
            }
        }
    }
}
