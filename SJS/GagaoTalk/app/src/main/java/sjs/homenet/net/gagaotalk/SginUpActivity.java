package sjs.homenet.net.gagaotalk;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import sjs.homenet.net.gagaotalk.UserInfo;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;


public class SginUpActivity extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private EditText _emailField;
    private EditText _passwordField;
    private EditText _nameField;
    private EditText _phoneNumber;
    private String _fareKey;

    private FirebaseAuth mAuth;
    public  static FirebaseUser _firebaseUser;
    public static DatabaseReference Database;
    public  static  UserInfo  UserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sgin_up);

        _emailField = findViewById(R.id.field_email);
        _passwordField = findViewById(R.id.field_password);
        _nameField = findViewById(R.id.field_name);
        _phoneNumber = findViewById(R.id.field_phone_number);

        Button buttonCreateId = (Button)findViewById(R.id.email_create_account_button);
        //this._fareKey = FirebaseUser.getUid();

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();

        buttonCreateId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_emailField.getText().toString() != null && _passwordField.getText().toString() != null){
                    createAccount(_emailField.getText().toString(), _passwordField.getText().toString());
                }
            }
        });

    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

//        // 유효성 검사
//        if(_nameField.getText().toString() == ""){
//            Toast.makeText(SginUpActivity.this, "이름은 필수값 입니다.", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( SginUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            _firebaseUser = mAuth.getCurrentUser();
                            SaveUserInfo();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                    Toast.LENGTH_SHORT).show();
                            Toast.makeText(SginUpActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        //hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                }).addOnFailureListener(SginUpActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "createUserWithEmail:failure", e);
            }
        });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = _emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            _emailField.setError("이메일을 입력해주세요");
            valid = false;
        } else {
            _emailField.setError(null);
        }

        String password = _passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            _passwordField.setError("비밀번호를 입력해주세요");
            valid = false;
        } else {
            _passwordField.setError(null);
        }

        String name = _nameField.getText().toString();
        if(TextUtils.isEmpty(name)){
            _nameField.setError("이름을 입력해주세요");
            valid = false;
        }
        else{
            _nameField.setError(null);
        }

        return valid;
    }

    private void SaveUserInfo(){

        Database = FirebaseDatabase.getInstance().getReference();

        UserInfo = new UserInfo(_firebaseUser.getUid(),this._nameField.getText().toString(), this._emailField.getText().toString()
                              , this._phoneNumber.getText().toString());

        Database.child("user").child(_firebaseUser.getUid()).setValue(UserInfo);
        //Database.child("user").push().setValue(UserInfo);
        Intent intent = new Intent(SginUpActivity.this, MainListActivity.class);
        SginUpActivity.this.startActivity(intent);
        finish();
    }


}
