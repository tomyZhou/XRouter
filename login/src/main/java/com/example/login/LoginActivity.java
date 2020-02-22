package com.example.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.annotation_lib.XRouterPath;
import com.example.xrouter.XRouter;

@XRouterPath("login/login")
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void jumpToHome(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("username", "zhougang");
        bundle.putString("password", "123456");
        XRouter.getInstance().jump("main/home", bundle);
    }
}
