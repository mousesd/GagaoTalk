package jbk.homenet.net.gagaotalk.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jbk.homenet.net.gagaotalk.Activity.MainActivity;
import jbk.homenet.net.gagaotalk.Class.CommonService;
import jbk.homenet.net.gagaotalk.Class.FirbaseService;
import jbk.homenet.net.gagaotalk.R;

public class SettingFragment extends Fragment {

    //region == [ Fields ] ==

    /**
     * Context
     */
    private Context context;

    /**
     * 알림설정 버튼
     */
    private Button btnIsPush;

    //endregion == [ Fields ] ==

    //region == [ Constructor ] ==

    /**
     * 생성자
     */
    public SettingFragment() {
        // Required empty public constructor
    }

    //endregion == [ Constructor ] ==

    //region == [ Override Methods ] ==

    //region -- onCreate() : onCreate --
    /**
     * onCreate
     * @param savedInstanceState savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    //endregion -- onCreate() : onCreate --

    //region -- onCreateView() : onCreateView --
    /**
     * onCreateView
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        this.context = container.getContext();

        Button btnLogOuut = view.findViewById(R.id.btnLogOut);
        this.btnIsPush = view.findViewById(R.id.btnPush);

        this.btnIsPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPush();
            }
        });

        this.SetPushButtontext();

        btnLogOuut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirbaseService.FirebaseAuth.signOut();
                FirebaseAuth.getInstance().signOut();

                FirbaseService.FirebaseUser = null;
                FirbaseService.FirebaseAuth = null;
                CommonService.UserInfo = null;

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                if (getActivity() != null)
                    getActivity().finish();
            }
        });

        return view;
    }
    //endregion -- onCreateView() : onCreateView --

    //region -- onDetach() : onDetach --
    /**
     * onDetach
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }
    //endregion -- onDetach() : onDetach --

    //endregion == [ Override Methods ] ==

    //region == [ Methods ] ==

    //region -- SetPush() : 알림설정 상태 변경 --
    /**
     * 알림설정 상태 변경
     */
    private  void  SetPush()
    {
        CommonService.UserInfo.isPush = !CommonService.UserInfo.isPush;

        DatabaseReference usersaveDB = FirebaseDatabase.getInstance().getReference();
        usersaveDB.child("users").child(CommonService.UserInfo.uid).child("isPush").setValue(CommonService.UserInfo.isPush);

        this.SetPushButtontext();

        Toast.makeText(this.context , this.btnIsPush.getText(), Toast.LENGTH_SHORT).show();
    }
    //endregion -- SetPush() : 알림설정 상태 변경 --

    //region -- SetPushButtontext() : 알림설정 버튼 상태 변경 --
    /**
     * 알림설정 버튼 상태 변경
     */
    private void SetPushButtontext()
    {
        if (CommonService.UserInfo.isPush){
            this.btnIsPush.setText(R.string.pushOn);
        } else {
            this.btnIsPush.setText(R.string.pushOff);
        }
    }
    //endregion -- SetPushButtontext() : 알림설정 버튼 상태 변경 --

    //endregion == [ Methods ] ==
}
