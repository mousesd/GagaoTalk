package jsh.homenet.net.gagaotalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity {

    //로그인 버튼객체
    Button buttonLogin;

    //회원가입 버튼객체
    Button buttonSignUp;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        //로그인 버튼 클릭시 동작
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 화면 호출
            }
        });

        //회원가입 버튼 클릭시 동작
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 화면 호출
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                //LoginActivity.this.startActivity(intent);
                //LoginActivity.this.finish();

                LoginActivity.this.startActivityForResult(intent, Activity.RESULT_OK);
            }
        });


    }
}
