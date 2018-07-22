package jsh.homenet.net.gagaotalk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import jsh.homenet.net.gagaotalk.Helper.NetworkHelper;

public class BeginningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //일정시간 지연 후 실행
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isNetworkConnected = NetworkHelper.isNetworkConnected(BeginningActivity.this);
                if (!isNetworkConnected)
                {
                    Toast.makeText(BeginningActivity.this, "네트워크 연결상태를 확인 후 다시 실행해주세요.",  Toast.LENGTH_LONG).show();
                }

                else
                {
                    Intent intent = new Intent(BeginningActivity.this, LoginActivity.class);
                    BeginningActivity.this.startActivity(intent);
                }

                BeginningActivity.this.finish();
            }
        }, 2000);
    }
}
