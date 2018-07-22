package jsh.homenet.net.gagaotalk;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jsh.homenet.net.gagaotalk.Entity.UserInfo;

public class SignUpActivity extends AppCompatActivity {

    Button buttonSign;
    Button buttonIdentityCheck;

    EditText txtIdentity;
    EditText txtName;
    EditText txtPassword;
    EditText txtPhone;
    EditText txtEmail;

    boolean isCheckIdentity = false;    //아이디 중복확인을 했는지 여부

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        //액션바 설정
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        txtIdentity = findViewById(R.id.inputIdentity);
        txtName = findViewById(R.id.inputName);
        txtPassword = findViewById(R.id.inputPassword);
        txtPhone = findViewById(R.id.inputPhone);
        txtEmail = findViewById(R.id.inputEmail);
        buttonSign = findViewById(R.id.sign);
        buttonIdentityCheck = findViewById(R.id.identityCheck);

        txtIdentity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isCheckIdentity)
                    isCheckIdentity = false;    //변경이 일어났을 때는
            }
        });

        //아이디 중복체크
        buttonIdentityCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String identity = txtIdentity.getText().toString().trim();

                if (identity.length() == 0)
                {
                    Toast.makeText(SignUpActivity.this, "아이디를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    final FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference ref = database.getReference("Users");

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                                //UserInfo userInfo = messageSnapshot.getValue(UserInfo.class);

                                //중복된 아이디가 있으면 메세지 처리하고 종료
                                if (messageSnapshot.getKey().toString().equals(identity))
                                {
                                    Toast.makeText(SignUpActivity.this, "존재하는 아이디입니다.", Toast.LENGTH_LONG).show();
                                    return;
                                }
                            }

                            isCheckIdentity = true;
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("알림", "The read failed: " + databaseError.getCode());
                        }
                    });
                }
            }
        });

        //인증하기
        buttonSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //빈 값 확인
                final String identity = txtIdentity.getText().toString().trim();
                final String name = txtName.getText().toString().trim();
                final String password = txtPassword.getText().toString().trim();
                final String phone = txtPhone.getText().toString().trim();
                final String email = txtEmail.getText().toString().trim();

                if (identity.length() == 0)
                {
                    Toast.makeText(SignUpActivity.this, "아이디를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (name.length() == 0)
                {
                    Toast.makeText(SignUpActivity.this, "이름을 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.length() == 0)
                {
                    Toast.makeText(SignUpActivity.this, "패스워드를 입력하세요.", Toast.LENGTH_LONG).show();
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

                //아이디 중복체크 여부 확인
                if (!isCheckIdentity)
                {
                    Toast.makeText(SignUpActivity.this, "아이디 중복체크를 먼저 진행하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                UserInfo user = new UserInfo(identity);
                user.setEmail(email);
                user.setName(name);
                user.setPassword(password);
                user.setPhone(phone);

                //가입!!!
                sign(user);
            }
        });
    }

    private boolean validation(UserInfo user)
    {
        return true;
    }

    private void sign(UserInfo user)
    {
        final String userId = user.getIdentity();
        mDatabase.child("Users").child(userId).setValue(user);
//        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Log.d("알림", "성공");
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.d("알림", "실패");
//                    }
//                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.homeAsUp:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
