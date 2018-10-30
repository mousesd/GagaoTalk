package jbk.homenet.net.gagaotalk.Activity;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Iterator;

import jbk.homenet.net.gagaotalk.Class.ChatingRoomInfo;
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
     * GALLERY_CODE
     */
    private final int GALLERY_CODE = 1112;

    private final int REQUEST_PERMISSION_CODE =  2000;

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
     * 이미지 등록 여부
     */
    private Boolean hasImage;

    /**
     * Push 알림 여부
     */
    private Boolean isPush = true;

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

    /**
     * 프로필사진 ImageView
     */
    private ImageView imgProfile;

    /**
     * 이미지경로
     */
    private Uri selectUri;

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

        this.txtName = findViewById(R.id.name);
        this.txtPhoneNum = findViewById(R.id.phoneNum);
        this.txtStateMsg = findViewById(R.id.stateMsg);
        this.imgProfile = findViewById(R.id.imgProfile);


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
                this.hasImage = CommonService.UserInfo.hasImage;

                this.isPush = CommonService.UserInfo.isPush;

            } else{
                //# 신규입력
                this.uid = FirbaseService.FirebaseAuth.getUid();
            }

            findViewById(R.id.imgProfile).setOnClickListener(this);

        } else{

            //# 다른 사용자 정보 조회
            Button btnSave = findViewById(R.id.btnUserCommit);
            btnSave.setVisibility(View.GONE);

            Button btnChat= findViewById(R.id.btnChating);
            Button btnCall = findViewById(R.id.btnCall);
            btnChat.setVisibility(View.VISIBLE);
            btnCall.setVisibility(View.VISIBLE);

            this.txtName.setEnabled(false);
            this.txtStateMsg.setEnabled(false);
            this.txtPhoneNum.setEnabled(false);

            this.uid = extraUid;
            DatabaseReference userDB = FirebaseDatabase.getInstance().getReference("users").child(extraUid);
            userDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    UserInfo tempUserInfo = dataSnapshot.getValue(UserInfo.class);

                    if (tempUserInfo != null){

                        name = tempUserInfo.name;
                        phoneNum = tempUserInfo.phoneNum;
                        stateMsg = tempUserInfo.stateMsg;
                        hasImage = tempUserInfo.hasImage;

                        txtName.setText(name);
                        txtPhoneNum.setText(phoneNum);
                        txtStateMsg.setText(stateMsg);

                        if (hasImage != null && hasImage) {

                            // Reference to an image file in Firebase Storage
                            StorageReference riversRootRef = FirbaseService.FirebaseStorage.getReference();
                            StorageReference riversProfileRef = riversRootRef.child("profileImage");
                            StorageReference riversRef = riversProfileRef.child("profileImage/" + uid);

                            if (riversRef.getName().equals(uid)) {

                                // Load the image using Glide
                                Glide.with(UserActivity.this)
                                        .using(new FirebaseImageLoader())
                                        .load(riversRef)
                                        .into(imgProfile);
                            }
                        }

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


        this.txtName.setText(this.name);
        this.txtPhoneNum.setText(this.phoneNum);
        this.txtStateMsg.setText(this.stateMsg);

        if (hasImage != null && hasImage) {

            // Reference to an image file in Firebase Storage
            StorageReference riversRootRef = FirbaseService.FirebaseStorage.getReference();
            StorageReference riversProfileRef = riversRootRef.child("profileImage");
            StorageReference riversRef = riversProfileRef.child("profileImage/" + uid);

            if (riversRef.getName().equals(uid)) {

                // Load the image using Glide
                Glide.with(this)
                        .using(new FirebaseImageLoader())
                        .load(riversRef)
                        .into(imgProfile);
            }
        }

        //# Click Listener 등록
        findViewById(R.id.btnUserCommit).setOnClickListener(this);
        findViewById(R.id.btnChating).setOnClickListener(this);
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
            this.GetGallery ();
        } else if (i== R.id.btnChating){
            this.SetChatingRoom();
        }
    }
    //endregion -- onClick() : onClick --

    //region -- onActivityResult() : onActivityResultk --
    /**
     * onActivityResultk
     * @param requestCode 요청코드
     * @param resultCode 결과코드
     * @param data 데이터
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            switch (requestCode) {

                case GALLERY_CODE:
                    SetPicture(data.getData()); //갤러리에서 가져오기
                    selectUri = data.getData();
                    break;

                default:
                    break;
            }

        }
    }
    //endregion -- onActivityResult() : onActivityResult --

    //region -- onRequestPermissionsResult() : onRequestPermissionsResult --
    /**
     * onRequestPermissionsResult
     * @param requestCode 요청코드
     * @param permissions 권한정보
     * @param grantResults 권한결과
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){

            case REQUEST_PERMISSION_CODE:

                if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    //동의 했을 경우
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_CODE);
                }else{
                    //거부했을 경우
                    Toast toast=Toast.makeText(this,"기능 사용을 위한 권한 동의가 필요합니다.", Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;
        }
    }
    //endregion -- onRequestPermissionsResult() : onRequestPermissionsResult --

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

        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference();

        //String uid, String name, String email, String stateMsg, String phoneNum
        CommonService.UserInfo = new UserInfo(this.uid, this.txtName.getText().toString(),this.email, this.txtStateMsg.getText().toString(), this.txtPhoneNum.getText().toString(), this.isPush );

//        //# 사용자 정보 저장
//        CommonService.Database.child("users").child(this.uid).setValue(CommonService.UserInfo);

        //# 이미지 저장
        if (this.imgProfile.getTag() != null && !this.imgProfile.getTag().toString().equals("")){
            StorageReference riversRootRef = FirbaseService.FirebaseStorage.getReference();
            StorageReference riversProfileRef = riversRootRef.child("profileImage");
            StorageReference riversRef = riversProfileRef.child("profileImage/" + this.uid );

            riversRef.delete();

            UploadTask uploadTask =  riversRef.putFile(selectUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    //# 실패
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //# 사용자 정보 저장
                    CommonService.UserInfo.hasImage = true;

                    DatabaseReference usersaveDB = FirebaseDatabase.getInstance().getReference();
                    usersaveDB.child("users").child(uid).setValue(CommonService.UserInfo);
                }
            });
        } else {
            //# 사용자 정보 저장
            CommonService.UserInfo.hasImage = false;

            DatabaseReference usersaveDB = FirebaseDatabase.getInstance().getReference();
            usersaveDB.child("users").child(this.uid).setValue(CommonService.UserInfo);
        }

        Intent intent = new Intent(UserActivity.this, MainFrameActivity.class);
        startActivity(intent);
        finish();
    }
    //endregion -- SaveUserInfo() : 사용자 정보 저장 --

    //region -- SetGallery() : 프로필사진 설정 --
    /**
     * 프로필사진 설정
     */
    private void  GetGallery(){

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_PERMISSION_CODE);
    }
    //endregion -- SetGallery() : 프로필사진 설정 --


    //region -- SetPicture() : 이미지 설정 --
    /**
     * 이미지 설정
     * @param imgUri 이미지 경로
     */
    private void SetPicture(Uri imgUri) {

//            String imagePath = getRealPathFromURI(imgUri); // path 경로
//            ExifInterface exif = null;
//            try {
//                exif = new ExifInterface(imagePath);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//            int exifDegree = exifOrientationToDegrees(exifOrientation);
//
//            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);//경로를 통해 비트맵으로 전환
            imgProfile.setImageURI(imgUri);
            //setImageBitmap(rotate(bitmap, exifDegree));//이미지 뷰에 비트맵 넣기
    }
    //endregion -- SetPicture() : 이미지 설정 --

    private void SetChatingRoom(){

        final DatabaseReference chatingRoomDB = FirebaseDatabase.getInstance().getReference();

        chatingRoomDB.child("chatingRoom").child(CommonService.UserInfo.uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while (child.hasNext()) {
                    ChatingRoomInfo chatingRoomInfo = child.next().getValue(ChatingRoomInfo.class);

                    if (chatingRoomInfo != null) {
                        if (chatingRoomInfo.TargetUser.equals(uid)) {
                            String chatingRoomId = chatingRoomInfo.ChatingRoomId;

                            Intent intent = new Intent(UserActivity.this, MessageActivity.class);
                            intent.putExtra("chatingRoomId", chatingRoomId);

                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                }


                ChatingRoomInfo chatingRoomInfo = new ChatingRoomInfo();


                DatabaseReference chatingRoomUid = chatingRoomDB.child("chatingRoom").child(CommonService.UserInfo.uid).push();
                chatingRoomInfo.ChatingRoomId = chatingRoomUid.getKey();
                chatingRoomInfo.TargetUser = uid;

                //# 내 채팅 목록추가
                chatingRoomDB.child("chatingRoom").child(CommonService.UserInfo.uid).child(chatingRoomInfo.ChatingRoomId).setValue(chatingRoomInfo);

                //# 상대 채팅 목록 추가
                chatingRoomInfo.TargetUser = CommonService.UserInfo.uid;
                chatingRoomDB.child("chatingRoom").child(uid).child(chatingRoomInfo.ChatingRoomId).setValue(chatingRoomInfo);

                Intent intent = new Intent(UserActivity.this, MessageActivity.class);
                intent.putExtra("chatingRoomId", chatingRoomInfo.ChatingRoomId);

                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getRealPathFromURI(Uri contentUri) {
        int column_index=0;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if(cursor.moveToFirst()){
            column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        }

        return cursor.getString(column_index);
    }

    private int exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private Bitmap rotate(Bitmap src, float degree) {
        // Matrix 객체 생성
        Matrix matrix = new Matrix();
        // 회전 각도 셋팅
        matrix.postRotate(degree);

        // 이미지와 Matrix 를 셋팅해서 Bitmap 객체 생성
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                src.getHeight(), matrix, true);
    }

    //endregion == [ Methods ] ==
}