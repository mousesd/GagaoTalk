package jbk.homenet.net.gagaotalk.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;

import jbk.homenet.net.gagaotalk.Class.CommonService;
import jbk.homenet.net.gagaotalk.Class.UserInfo;
import jbk.homenet.net.gagaotalk.R;


public class UserActivity extends BaseActivity implements
        View.OnClickListener {


    private String uid;
    private String email;

    private String name;
    private String phoneNum;
    private String stateMsg;

    private EditText txtName;
    private EditText txtPhoneNum;
    private EditText txtStateMsg;

    //region  -- onCreate() : onCreate --

    /**
     * onCreate
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();

        this.uid = intent.getStringExtra("uid");
        this.email = intent.getStringExtra("email");

        this.name = intent.getStringExtra("name");
        this.phoneNum = intent.getStringExtra("phoneNum");
        this.stateMsg = intent.getStringExtra("stateMsg");

        this.txtName = findViewById(R.id.name);
        this.txtPhoneNum = findViewById(R.id.phoneNum);
        this.txtStateMsg = findViewById(R.id.stateMsg);

        this.txtName.setText(this.name);
        this.txtPhoneNum.setText(this.phoneNum);
        this.txtStateMsg.setText(this.stateMsg);

        findViewById(R.id.btnUserCommit).setOnClickListener(this);
    }

    //endregion

    //region -- onClick() : onClick --
    /**
     * onClick
     * @param v View
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnUserCommit) {
            this.SaveUserInfo();
        }
    }
    //endregion -- onClick() : onClick --

    //region -- SaveUserInfo() : 사용자 정보 저장 --
    /**
     * 사용자 정보 저장
     */
    private void SaveUserInfo()
    {
        //# 유효성 체크
        if(this.txtName.getText() != null && this.txtName.getText().toString().equals("")){
            Toast.makeText(this, "이름은 필수 값입니다.", Toast.LENGTH_LONG).show();
            return;
        }

        CommonService.Database = FirebaseDatabase.getInstance().getReference();

        //String uid, String name, String email, String stateMsg, String phoneNum
        CommonService.UserInfo = new UserInfo(this.uid, this.txtName.getText().toString(),this.email, this.txtStateMsg.getText().toString(), this.txtPhoneNum.getText().toString());

        CommonService.Database.child("users").child(this.uid).setValue(CommonService.UserInfo);

        Intent intent = new Intent(UserActivity.this, MainFrameActivity.class);
        startActivity(intent);
        finish();
    }
    //endregion -- SaveUserInfo() : 사용자 정보 저장 --
}