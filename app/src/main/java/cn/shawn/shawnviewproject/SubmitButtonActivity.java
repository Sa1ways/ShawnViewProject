package cn.shawn.shawnviewproject;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cn.shawn.view.view.SubmitButton;

public class SubmitButtonActivity extends AppCompatActivity implements SubmitButton.OnSubmitClickListener {

    private SubmitButton submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_button);
        submitButton= (SubmitButton) findViewById(R.id.btn);
        submitButton.setSubmitListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSubmitClick() {
        submitButton.startLoading();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                submitButton.stopLoading();
            }
        }, 5000);
    }

    @Override
    public void onFinish() {

    }


}
