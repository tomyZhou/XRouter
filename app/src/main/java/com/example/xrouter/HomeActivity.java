package com.example.xrouter;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.annotation_lib.XRouterPath;

@XRouterPath("main/home")
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Bundle bundle = getIntent().getBundleExtra("bundle");
        String userName = bundle.getString("username");
        String password = bundle.getString("password");

        Toast.makeText(this, userName + ":" + password, 0).show();

    }
}
