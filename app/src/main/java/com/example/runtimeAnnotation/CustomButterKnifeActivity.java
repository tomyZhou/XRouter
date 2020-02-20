package com.example.runtimeAnnotation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compiletimeprocessor.Hello;
import com.example.runtimebutterknife.ButterKnife;
import com.example.runtimebutterknife.ContentView;
import com.example.runtimebutterknife.ViewInject;
import com.example.xrouter.R;
import com.example.compiletimeprocessor.Print;

@ContentView(R.layout.activity_custom_butter_knife)
public class CustomButterKnifeActivity extends AppCompatActivity implements View.OnClickListener {

    @Print
    @ViewInject(id = R.id.tv_name)
    private TextView tv_name;

    @Print
    @ViewInject(id = R.id.tv_hello, clickable = true)
    private TextView tv_hello;

    @Hello("hello world!")
    @Print
    @ViewInject(id = R.id.iv_image)
    private ImageView iv_image;

    @ViewInject(id = R.id.bt_click, clickable = true)
    private Button bt_click;

    @Print
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
        tv_hello.setText("bbbbbbbbbb");

    }

    @Print
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_hello:
                Toast.makeText(CustomButterKnifeActivity.this, "点击hello", 0).show();
                break;
            case R.id.bt_click:
                Toast.makeText(CustomButterKnifeActivity.this, "点击按钮", 0).show();
                break;
        }
    }
}
