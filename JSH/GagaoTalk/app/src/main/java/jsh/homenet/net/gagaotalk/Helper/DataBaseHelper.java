package jsh.homenet.net.gagaotalk.Helper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import jsh.homenet.net.gagaotalk.Entity.GagaoUserInfo;
import jsh.homenet.net.gagaotalk.IUserInfoCallBack;

public class DataBaseHelper {
    public static void GetUserInfo(final String uid, final IUserInfoCallBack callBack)
    {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {

                    //중복된 아이디가 있으면 메세지 처리하고 종료
                    if (messageSnapshot.getKey().toString().equals(uid))
                    {
                        callBack.onCallback(messageSnapshot.getValue(GagaoUserInfo.class));
                        return;
                    }
                }

                callBack.onCallback(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callBack.onCallback(null);
            }
        });
    }
}