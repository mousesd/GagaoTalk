package jsh.homenet.net.gagaotalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import jsh.homenet.net.gagaotalk.Entity.GagaoUserInfo;
import jsh.homenet.net.gagaotalk.Helper.DataBaseHelper;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class LoginActivity extends AppCompatActivity {

    //구글 로그인 버튼
    Button buttonLoginGoogle;

    //페이스북 로그인 버튼
    Button buttonLoginFacebook;

    //파이어베이스인증 객체
    FirebaseAuth mFirebaseAuth;

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        buttonLoginGoogle = findViewById(R.id.buttonLoginGoogle);
        buttonLoginFacebook = findViewById(R.id.buttonLoginFacebook);

        //구글 로그인버튼 클릭
        buttonLoginGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        //페이스북 로그인버튼 클릭
        buttonLoginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try
            {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                GoogleLogin(account);

            } catch (ApiException e) {

            }
        }
    }

    /**
     * 구글 로그인 처리
     * @param acct
     */
    private void GoogleLogin(GoogleSignInAccount acct)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUI();
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w("알림", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    /**
     * 화면변경
     */
    private void updateUI()
    {
        final FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (null != currentUser)
        {
            DataBaseHelper.GetUserInfo(currentUser.getUid(), new IUserInfoCallBack() {
                @Override
                public void onCallback(GagaoUserInfo user) {
                    if (null == user)
                    {
                        //가입 액티비티로
                        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                        intent.putExtra("email", currentUser.getEmail());
                        intent.putExtra("name", currentUser.getDisplayName());
                        intent.putExtra("id", currentUser.getUid());
                        LoginActivity.this.startActivity(intent);
                    }
                    else
                    {
                        //메인 액티비티로
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        LoginActivity.this.startActivity(intent);
                    }
                }
            });
        }
        else
        {
            Log.d("알림", "화면에 그대로...");
        }
    }
}
