package jbk.homenet.net.gagaotalk.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;


import jbk.homenet.net.gagaotalk.Class.CommonService;
import jbk.homenet.net.gagaotalk.Class.FirbaseService;
import jbk.homenet.net.gagaotalk.Class.UserInfo;
import jbk.homenet.net.gagaotalk.R;

/**
 * MainActivity  StartActivity
 */
public class MainActivity  extends BaseActivity
{
    //region == [ Fields ] ==
    //endregion == [ Fields ] ==

    //region == [ Constructors ] ==
    //endregion == [ Constructors ] ==

    //region == [ Override Methods ] ==

    //region  -- onCreate() : onCreate --

    /**
     * onCreate
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar myActionBar = getSupportActionBar(); // to get activity actionbar

        if (myActionBar != null)
            myActionBar.hide();

        FirbaseService.FirebaseAuth = FirebaseAuth.getInstance();
        FirbaseService.FirebaseStorage = FirebaseStorage.getInstance();
    }

    //endregion  -- onCreate() : onCreate --

    //region  -- onStart() : onStart --

    /**
     * onStart
     */
    @Override
    public void onStart() {
        super.onStart();

        FirbaseService.FirebaseUser = FirbaseService.FirebaseAuth.getCurrentUser();

        if (FirbaseService.FirebaseUser == null ||  FirbaseService.FirebaseUser.getUid().equals("") ){
            //# 인증정보 없는 경우 인증 Activity 호출
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
        } else {
            //# 인증정보가 있는 경우 사용자 설정 체크
            CommonService.Database = FirebaseDatabase.getInstance().getReference("users").child(FirbaseService.FirebaseUser.getUid());

            CommonService.Database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    CommonService.UserInfo = dataSnapshot.getValue(UserInfo.class);

                    if (CommonService.UserInfo == null) {
                        //# 사용자 정보 없으면 사용자 설정 Activity
                        Intent intent = new Intent(MainActivity.this, UserActivity.class);

                        startActivity(intent);
                        finish();
                    }
                    else
                    {
                        //# Main Start
                        Intent intent = new Intent(MainActivity.this, MainFrameActivity.class);

                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
    //endregion  -- onStart() : onStart --

    //endregion == [ Override Methods ] ==

    //region == [ Methods ] ==
    //endregion == [ Methods ] ==

}