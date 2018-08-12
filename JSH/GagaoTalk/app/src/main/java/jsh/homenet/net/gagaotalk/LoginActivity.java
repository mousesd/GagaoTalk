package jsh.homenet.net.gagaotalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import jsh.homenet.net.gagaotalk.Entity.GagaoUserInfo;
import jsh.homenet.net.gagaotalk.Helper.DataBaseHelper;

public class LoginActivity extends AppCompatActivity {

    EditText loginIdentity;
    EditText loginPassword;

    //로그인 버튼
    Button buttonLogin;

    //구글 로그인 버튼
    Button buttonLoginGoogle;

    //페이스북 로그인 버튼
    Button buttonLoginFacebook;

    //회원가입 버튼
    Button buttonSignUp;

    //파이어베이스인증 객체
    FirebaseAuth firebaseAuth;

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);

        setContentView(R.layout.login);

        loginIdentity = findViewById(R.id.loginIdentity);
        loginPassword = findViewById(R.id.loginPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLoginGoogle = findViewById(R.id.buttonLoginGoogle);
        buttonLoginFacebook = findViewById(R.id.buttonLoginFacebook);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        firebaseAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("알림", "Facebook onSuccess()");

                FacebookLogin(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("알림", "Facebook onCancel()");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("알림", "Facebook onError()");
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //로그인 버튼 클릭
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //빈 값 확인
                final String identity = loginIdentity.getText().toString().trim();
                final String password = loginPassword.getText().toString().trim();
                if (identity.length() == 0)
                {
                    Toast.makeText(LoginActivity.this, "아이디를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.length() == 0)
                {
                    Toast.makeText(LoginActivity.this, "패스워드를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                DataBaseHelper.GetUserInfo(identity, new IUserInfoCallBack() {
                    @Override
                    public void onCallback(final GagaoUserInfo user) {
                        if (null == user || !user.getPassword().equals(password))
                        {
                            Toast.makeText(LoginActivity.this, "아이디 또는 패스워드가 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        else
                        {
                            firebaseAuth.signInWithEmailAndPassword(user.getEmail(), user.getPassword())
                                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                updateUI(user);
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
        });

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
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email"));
            }
        });

        //회원가입버튼 클릭
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //회원가입 액티비티로
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUI(null);
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

        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 구글 로그인 처리
     * @param acct
     */
    private void GoogleLogin(GoogleSignInAccount acct)
    {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUI(null);
                        }
                        else
                        {
                            // If sign in fails, display a message to the user.
                            Log.w("알림", "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    private void FacebookLogin(AccessToken token)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updateUI(null);
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
    private void updateUI(GagaoUserInfo gagaoUserInfo)
    {
        if (null == gagaoUserInfo)
        {
            final FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (null != currentUser)
            {
                DataBaseHelper.GetUserInfo(currentUser.getUid(), new IUserInfoCallBack() {
                    @Override
                    public void onCallback(GagaoUserInfo user) {
                        if (null == user)
                        {
                            //회원가입 액티비티로
                            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                            intent.putExtra("email", currentUser.getEmail());
                            intent.putExtra("name", currentUser.getDisplayName());
                            intent.putExtra("uid", currentUser.getUid());
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
        else
        {
            //메인 액티비티로
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            LoginActivity.this.startActivity(intent);
        }
    }
}
