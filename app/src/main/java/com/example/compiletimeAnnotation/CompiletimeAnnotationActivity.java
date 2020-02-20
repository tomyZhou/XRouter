package com.example.compiletimeAnnotation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.compiletimebutterknife.BindView;
import com.example.compiletimebutterknife.ButterKnife;
import com.example.compiletimebutterknife.ContentView;
import com.example.compiletimebutterknife.OnClick;
import com.example.xrouter.R;

@ContentView(R.layout.activity_custom_butter_knife)
public class CompiletimeAnnotationActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.tv_name)
    TextView tv_hello;

    @BindView(R.id.iv_image)
    ImageView iv_image;

    @BindView(R.id.bt_click)
    Button bt_click;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        ButterKnife.bind(this);

        tv_hello.setText("我的ButterKnife终于成功了！！！");
    }

    @OnClick({R.id.tv_hello, R.id.iv_image, R.id.bt_click})
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_hello:
                Toast.makeText(CompiletimeAnnotationActivity.this, "helloClicked", 0).show();
                break;
            case R.id.iv_image:
                Toast.makeText(CompiletimeAnnotationActivity.this, "imageClicked", 0).show();

                break;
            case R.id.bt_click:
                Toast.makeText(CompiletimeAnnotationActivity.this, "buttonCliced", 0).show();

                break;
        }
    }


    public void setContentView() {
        setContentView(R.layout.activity_custom_butter_knife);
    }


}
