package cn.shawn.shawnviewproject;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import cn.shawn.view.view.ProgressLoadView;

public class ProgressViewActivity extends AppCompatActivity {
    public static final String TAG = ProgressViewActivity.class.getSimpleName();
    private Handler mHandler = new Handler();
    private ProgressLoadView mPg;
    private int mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_view);
        mPg = (ProgressLoadView) findViewById(R.id.pg);
        new MyThread().start();
    }

    class MyThread extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(10);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(mProgress <= 100)
                            mPg.setCurrProgress(mProgress++);
                    }
                });
                mHandler.postDelayed(this,10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
