package jsh.homenet.net.gagaotalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.flags.impl.DataUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.common.StringUtils;

import jsh.homenet.net.gagaotalk.Entity.GagaoUserInfo;

public class SignUpActivity extends AppCompatActivity {

    Button buttonSign;
    Button buttonIdentityCheck;

    EditText txtIdentity;
    EditText txtName;
    EditText txtPassword;
    EditText txtPhone;
    EditText txtEmail;
    EditText txtMessage;

    //파이어베이스인증 객체
    FirebaseAuth firebaseAuth;

    boolean isCheckIdentity = false;    //아이디 중복확인을 했는지 여부

    private DatabaseReference mDatabase;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        txtIdentity = findViewById(R.id.inputIdentity);
        txtPassword = findViewById(R.id.inputPassword);
        txtName = findViewById(R.id.inputName);
        txtPhone = findViewById(R.id.inputPhone);
        txtEmail = findViewById(R.id.inputEmail);
        txtMessage = findViewById(R.id.inputMessage);
        buttonSign = findViewById(R.id.sign);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null)
        {
            if (bundle.containsKey("uid"))
            {
                uid = bundle.getString("uid");
            }

            txtName.setText(bundle.getString("name"));
            txtEmail.setText(bundle.getString("email"));
        }

        mDatabase = FirebaseDatabase.getInstance().getReference();

        //가입하기
        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //빈 값 확인
                final String identity = txtIdentity.getText().toString().trim();
                final String password = txtPassword.getText().toString().trim();
                final String name = txtName.getText().toString().trim();
                final String phone = txtPhone.getText().toString().trim();
                final String email = txtEmail.getText().toString().trim();
                final String message = txtMessage.getText().toString().trim();

                if (identity.length() == 0)
                {
                    Toast.makeText(SignUpActivity.this, "아이디를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.length() < 6)
                {
                    Toast.makeText(SignUpActivity.this, "패스워드는 최소 6자이상 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (name.length() == 0)
                {
                    Toast.makeText(SignUpActivity.this, "이름을 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (phone.length() == 0)
                {
                    Toast.makeText(SignUpActivity.this, "전화번호를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (email.length() == 0)
                {
                    Toast.makeText(SignUpActivity.this, "이메일을 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                //Uid정보가 없는 경우 이메일+패스워드로 추가
                if (TextUtils.isEmpty(uid))
                {
                    //uid 획득
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        uid = firebaseAuth.getCurrentUser().getUid();

                                        GagaoUserInfo gagaoUser = new GagaoUserInfo(uid);
                                        gagaoUser.setIdentity(identity);
                                        gagaoUser.setPassword(password);
                                        gagaoUser.setEmail(email);
                                        gagaoUser.setName(name);
                                        gagaoUser.setPhone(phone);
                                        gagaoUser.setMessage(message);

                                        mDatabase.child("Users").child(identity).setValue(gagaoUser, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();

                                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                                SignUpActivity.this.startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            })
                            .addOnFailureListener(SignUpActivity.this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //The given password is invalid. [ Password should be at least 6 characters ]
                                    Log.d("알림", e.getMessage());
                                }
                            });
                }
                else
                {
                    GagaoUserInfo gagaoUser = new GagaoUserInfo(uid);
                    gagaoUser.setIdentity(identity);
                    gagaoUser.setPassword(password);
                    gagaoUser.setEmail(email);
                    gagaoUser.setName(name);
                    gagaoUser.setPhone(phone);
                    gagaoUser.setMessage(message);

                    mDatabase.child("Users").child(identity).setValue(gagaoUser, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            SignUpActivity.this.startActivity(intent);
                        }
                    });
                }
            }
        });
    }
}
