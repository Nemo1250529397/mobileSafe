package nemo.com.mobilesafe;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by nemo on 16-6-19.
 */
public abstract class BaseStepActivity extends Activity {

    protected SharedPreferences sp = null;
    protected GestureDetector gestureDetector = null;
    public static final String CONFIG = "config";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences(CONFIG, MODE_PRIVATE);

        gestureDetector = new GestureDetector(getApplicationContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(Math.abs(velocityX) < 200) {
                    Toast.makeText(getApplicationContext(), "滑动速度太慢，滑动无效！", Toast.LENGTH_SHORT).show();
                }

                if(Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
                    Toast.makeText(getApplicationContext(), "滑动方向错误，滑动无效！", Toast.LENGTH_SHORT).show();
                }

                if((e2.getRawX() - e1.getRawX()) > 200) {
                    onShowPrevious();
                }

                if((e1.getRawX() - e2.getRawX()) > 200) {
                    onShowNext();
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public abstract Intent showNext();
    public abstract Intent showPrevious();

    public void next(View view) {
        onShowNext();
    }

    public void previous(View view) {
        onShowPrevious();
    }

    public void onShowNext() {
        Intent intent = showNext();
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
    }

    public void onShowPrevious() {
        Intent intent = showPrevious();
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);

        return super.onTouchEvent(event);
    }
}
