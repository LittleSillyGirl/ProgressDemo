package com.mproject.exercisedemo.progressdemo;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mproject.exercisedemo.progressdemo.activity.IndicatorActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_indicator ;
    private Activity mActivity ;
    private EditText et_state ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = MainActivity.this;
        setContentView(R.layout.activity_main);
        bt_indicator = (Button) findViewById(R.id.bt_indicator);
        et_state = (EditText) findViewById(R.id.et_state);
        bt_indicator.setOnClickListener((View.OnClickListener) mActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_indicator:
                int state = Integer.parseInt(et_state.getText().toString());
                Intent intent  = new Intent(mActivity, IndicatorActivity.class);
                intent.putExtra("state", state);
                startActivity(intent);
                break;
        }
    }
}
