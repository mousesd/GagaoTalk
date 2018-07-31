package jsh.homenet.net.gagaotalk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    boolean isCheckIdentity = false;    //아이디 중복확인을 했는지 여부

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        txtIdentity = findViewById(R.id.inputIdentity);
        txtName = findViewById(R.id.inputName);
        txtPhone = findViewById(R.id.inputPhone);
        txtEmail = findViewById(R.id.inputEmail);
        txtMessage = findViewById(R.id.inputMessage);
        buttonSign = findViewById(R.id.sign);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle != null)
        {
            txtIdentity.setText(bundle.getString("id"));
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
                final String name = txtName.getText().toString().trim();
                final String phone = txtPhone.getText().toString().trim();
                final String email = txtEmail.getText().toString().trim();
                final String message = txtMessage.getText().toString().trim();

                if (phone.length() == 0)
                {
                    Toast.makeText(SignUpActivity.this, "전화번호를 입력하세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                GagaoUserInfo gagaoUser = new GagaoUserInfo(identity);
                gagaoUser.setEmail(email);
                gagaoUser.setName(name);
                gagaoUser.setPhone(phone);
                gagaoUser.setMessage(message);

                mDatabase.child("Users").child(identity).setValue(gagaoUser, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        SignUpActivity.this.startActivity(intent);
                    }
                });
            }
        });
    }
}
