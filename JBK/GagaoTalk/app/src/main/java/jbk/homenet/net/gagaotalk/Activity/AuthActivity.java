package jbk.homenet.net.gagaotalk.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jbk.homenet.net.gagaotalk.Class.CommonService;
import jbk.homenet.net.gagaotalk.Class.FirbaseService;
import jbk.homenet.net.gagaotalk.Class.UserInfo;
import jbk.homenet.net.gagaotalk.R;

/**
 * AuthActivity - 인증 Activity
 */
public class AuthActivity  extends BaseActivity implements
        View.OnClickListener {

    //region == [ Fields ] ==

    /**
     * Debug Tag
     */
    private static final String TAG = "EmailPassword";

    /**
     * 상태 TextView
     */
    private TextView mStatusTextView;

    /**
     * 상태Detail TextView
     */
    private TextView mDetailTextView;

    /**
     * Email TextView
     */
    private EditText mEmailField;

    /**
     * Password TextView
     */
    private EditText mPasswordField;

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
        setContentView(R.layout.activity_auth);

        // Views
        mStatusTextView = findViewById(R.id.status);
        mDetailTextView = findViewById(R.id.detail);
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.email_create_account_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.verify_email_button).setOnClickListener(this);
    }
    //endregion

    //region -- onStart() : onStart --
    /**
     * onStart
     */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirbaseService.FirebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }
    //endregion -- onStart() : onStart --

    //region -- onClick() : onClick --
    /**
     * onClick
     * @param v View
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_create_account_button) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        } else if (i == R.id.verify_email_button) {
            sendEmailVerification();
        }
    }
    //endregion -- onClick() : onClick --

    //endregion == [ View Override Methods ] ==

    //region == [ Methods ] ==

    //region -- createAccount(String, String) : 인증 생성 --
    /**
     * 인증 생성
     * @param email E mail
     * @param password password
     */
    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        if (validateForm()) {
            return;
        }

        showProgressDialog();

        FirbaseService.FirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = FirbaseService.FirebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else if  (task.getException() != null &&  task.getException().getMessage().equals("The email address is already in use by another account.")){
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AuthActivity.this, "이미 등록되어 있는 메일입니다.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog();
                    }
                });
    }
    //endregion  -- createAccount() : 인증 생성 --

    //region -- signIn (String, String) : 로그인 --
    /**
     * 로그인
     * @param email E mail
     * @param password password
     */
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (validateForm()) {
            return;
        }

        showProgressDialog();

        // [START sign_in_with_email]
        FirbaseService.FirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = FirbaseService. FirebaseAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }

                        hideProgressDialog();
                    }
                });
    }
    //endregion -- signIn (String, String) : 로그인 --

    //region -- signOut() : 로그아웃 --
    /**
     * 로그아웃
     */
    private void signOut() {
        FirbaseService.FirebaseAuth.signOut();
        updateUI(null);
    }
    //endregion -- signOut() : 로그아웃 --

    //region -- ???? sendEmailVerification() --
    /**
     *  ???????????????????????????
     */
    private void sendEmailVerification() {
        // Disable button
        findViewById(R.id.verify_email_button).setEnabled(false);

        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = FirbaseService.FirebaseAuth.getCurrentUser();

        if (user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Re-enable button
                            findViewById(R.id.verify_email_button).setEnabled(true);

                            if (task.isSuccessful()) {
                                Toast.makeText(AuthActivity.this,
                                        "Verification email sent to " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "sendEmailVerification", task.getException());
                                Toast.makeText(AuthActivity.this,
                                        "Failed to send verification email.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                });
        }
    }
    //endregion -- ???? sendEmailVerification() --

    //region -- checkEmail(String) : 이메일 유효여부 확인 --
    /**
     * 이메일 유효여부 확인
     * @param email 이메일 유효여부
     * @return 이메일 유효여부
     */
    private boolean checkEmail(String email){
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    //endregion -- checkEmail(String) : 이메일 유효여부 확인 --

    //region -- validateForm() : 입력값 유효여부 확인 --
    /**
     * 입력값 유효여부 확인
     * @return 입력값 유효여부
     */
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else if (!checkEmail(email)){
            mEmailField.setError("올바른 이메일 형식이 아닙니다.");
            valid = false;
        }
        else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else if (password.length() < 6){
            mPasswordField.setError("6자리 이상 비밀번호를 입력하세요.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return !valid;
    }
    //endregion -- validateForm() : 입력값 유효여부 확인 --

    //region -- updateUI(FirebaseUser) : 로그인 여부에 따른 UI 변경 --
    /**
     * 로그인 여부에 따른 UI 변경
     * @param user FirebaseUser정보
     */
    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {

            final String uid = user.getUid();
            final String email = user.getEmail();

            CommonService.Database = FirebaseDatabase.getInstance().getReference("users").child(uid);

            CommonService.Database.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserInfo tempUserInfo = dataSnapshot.getValue(UserInfo.class);

                    if (tempUserInfo == null) {

                        Intent intent = new Intent(AuthActivity.this, UserActivity.class);

                        intent.putExtra("uid", uid);
//                        intent.putExtra("email", email);

                        startActivity(intent);
                        finish();

                    } else {

                        CommonService.UserInfo = tempUserInfo;
                        Intent intent = new Intent(AuthActivity.this, MainFrameActivity.class);
                        startActivity(intent);
                        finish();
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

        } else {
            mStatusTextView.setText(R.string.signed_out);
            mDetailTextView.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }
    //endregion -- updateUI(FirebaseUser) : 로그인 여부에 따른 UI 변경 --

    //endregion == [ Methods ] ==
}