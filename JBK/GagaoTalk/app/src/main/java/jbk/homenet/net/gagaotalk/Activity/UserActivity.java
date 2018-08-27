package jbk.homenet.net.gagaotalk.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jbk.homenet.net.gagaotalk.Class.CommonService;
import jbk.homenet.net.gagaotalk.Class.FirbaseService;
import jbk.homenet.net.gagaotalk.Class.UserInfo;
import jbk.homenet.net.gagaotalk.R;

/**
 * 사용자 정보 Activity
 */
public class UserActivity extends BaseActivity implements
        View.OnClickListener {

    //region == [ Fields ] ==

    /**
     * Uid
     */
    private String uid;

    /**
     * Email
     */
    private String email;

    /**
     * 이름
     */
    private String name;

    /**
     * 연락처
     */
    private String phoneNum;

    /**
     * 상태메세지
     */
    private String stateMsg;

    /**
     * 이름 EditText
     */
    private EditText txtName;

    /**
     * 연락처 EditText
     */
    private EditText txtPhoneNum;

    /**
     * 상태메세지 EditText
     */
    private EditText txtStateMsg;

    //endregion == [ Fields ] ==

    //region == [ Override Methods ] ==

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
        String extraUid = intent.getStringExtra("uid");

        if (extraUid == null || extraUid.equals("")){

            if (CommonService.UserInfo != null)
            {
                //# 기존 정보 조회
                this.uid = CommonService.UserInfo.uid;
                this.email = CommonService.UserInfo.email;

                this.name = CommonService.UserInfo.name;
                this.phoneNum = CommonService.UserInfo.phoneNum;
                this.stateMsg = CommonService.UserInfo.stateMsg;
            } else{
                //# 신규입력
                this.uid = FirbaseService.FirebaseUser.getUid();
            }

        } else{

            //# 다른 사용자 정보 조회
            Button btnSave = findViewById(R.id.btnUserCommit);
            btnSave.setVisibility(View.GONE);

            this.uid = extraUid;
            CommonService.Database = FirebaseDatabase.getInstance().getReference("users").child(extraUid);
            CommonService.Database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserInfo tempUserInfo = dataSnapshot.getValue(UserInfo.class);

                    if (tempUserInfo != null){

                        name = tempUserInfo.name;
                        phoneNum = tempUserInfo.phoneNum;
                        stateMsg = tempUserInfo.stateMsg;

                        txtName = findViewById(R.id.name);
                        txtPhoneNum = findViewById(R.id.phoneNum);
                        txtStateMsg = findViewById(R.id.stateMsg);

                        txtName.setText(name);
                        txtPhoneNum.setText(phoneNum);
                        txtStateMsg.setText(stateMsg);
                    }
                }

                /**
                 *
                 * @param databaseError databaseError
                 */
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

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
        } else if (i == R.id.imgProfile){
            this.SetGallery ();
        }
    }
    //endregion -- onClick() : onClick --

    //endregion == [ Override Methods ] ==

    //region == [ Methods ] ==

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

    //region -- SetGallery() : 프로필사진 설정 --
    /**
     * 프로필사진 설정
     */
    private void  SetGallery(){

    }
    //endregion -- SetGallery() : 프로필사진 설정 --

    //endregion == [ Methods ] ==
}