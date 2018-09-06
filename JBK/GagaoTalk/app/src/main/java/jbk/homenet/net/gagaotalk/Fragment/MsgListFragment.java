package jbk.homenet.net.gagaotalk.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import jbk.homenet.net.gagaotalk.Class.ChatingRoomInfo;
import jbk.homenet.net.gagaotalk.Class.CommonService;
import jbk.homenet.net.gagaotalk.Class.FirbaseService;
import jbk.homenet.net.gagaotalk.Class.UserInfo;
import jbk.homenet.net.gagaotalk.R;


public class MsgListFragment extends Fragment {

    //region == [ ViewHolder Class ] ==
    /**
     * 사용자 리스트 ViewHolder
     */
    public static class MsgListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser;
        TextView txtUserNm;
        TextView txtLastMsg;

        MsgListViewHolder(View v) {
            super(v);
            imgUser = itemView.findViewById(R.id.imgTargetUser);
            txtUserNm = itemView.findViewById(R.id.txtTargetUserNm);
            txtLastMsg= itemView.findViewById(R.id.txtLastMsg);
        }
    }
    //endregion == [ ViewHolder Class ] ==

    //region == [ Override Methods ] ==

    //region  -- onCreate() : onCreate --
    /**
     * onCreateView
     * @param inflater inflater
     * @param container container
     * @param savedInstanceState savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msglist, container, false);

        Context context = view.getContext();

        RecyclerView recyclerView = view.findViewById(R.id.listMsg);
        // recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        CommonService.Database = FirebaseDatabase.getInstance().getReference("chatingRoom").child(CommonService.UserInfo.uid);

        FirebaseRecyclerOptions<ChatingRoomInfo> options = new FirebaseRecyclerOptions.Builder<ChatingRoomInfo>().setQuery(CommonService.Database, ChatingRoomInfo.class).build();

        FirebaseRecyclerAdapter<ChatingRoomInfo, MsgListFragment.MsgListViewHolder> mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatingRoomInfo, MsgListFragment.MsgListViewHolder>(options) {
            @NonNull
            @Override
            public MsgListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.msg_list_item, parent, false);

                return new MsgListFragment.MsgListViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final MsgListFragment.MsgListViewHolder holder, int position, @NonNull final ChatingRoomInfo model) {

                CommonService.Database = FirebaseDatabase.getInstance().getReference();
                CommonService.Database.child("users").child(model.TargetUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        UserInfo tartgetUserInfo = dataSnapshot.getValue(UserInfo.class);
                        holder.txtUserNm.setText(tartgetUserInfo.name);

                        if (tartgetUserInfo.hasImage != null && tartgetUserInfo.hasImage) {
                            StorageReference riversRootRef = FirbaseService.FirebaseStorage.getReference();
                            StorageReference riversProfileRef = riversRootRef.child("profileImage");
                            StorageReference riversRef = riversProfileRef.child("profileImage/" + tartgetUserInfo.uid);

                            if (riversRef.getName().equals(tartgetUserInfo.uid)) {
                                // Load the image using Glide
                                Glide.with(getActivity())
                                        .using(new FirebaseImageLoader())
                                        .load(riversRef)
                                        .into(holder.imgUser);
                            }
                        } else {
                            holder.imgUser.setImageResource(android.R.color.transparent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        recyclerView.setAdapter(mFirebaseAdapter);
        mFirebaseAdapter.startListening();

        return view;
}

    //endregion -- onCreate() : onCreate --

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
}
