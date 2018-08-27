package jbk.homenet.net.gagaotalk.Fragment;

import android.content.Context;
import android.content.Intent;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.firebase.database.FirebaseDatabase;


import jbk.homenet.net.gagaotalk.Activity.UserActivity;
import jbk.homenet.net.gagaotalk.Class.CommonService;
import jbk.homenet.net.gagaotalk.Class.UserInfo;
import jbk.homenet.net.gagaotalk.R;

/**
 * 친구목록 Fragment
 */
public class UserListFragment extends Fragment {

    //region == [ ViewHolder Class ] ==
    /**
     * 사용자 리스트 ViewHolder
     */
    public static class UserListViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser;
        TextView txtUserNm;
        TextView txtStateMsg;

        UserListViewHolder (View v) {
            super(v);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtUserNm = itemView.findViewById(R.id.txtUserNm);
            txtStateMsg = itemView.findViewById(R.id.txtStateMsg);
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
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

        ImageView MyImgUser = view.findViewById(R.id.imgMyUser);
        TextView MyUserNm = view.findViewById(R.id.txtMyUserNm);
        TextView MyStateMsg = view.findViewById(R.id.txtMyStateMsg);

        MyUserNm.setText(CommonService.UserInfo.name);
        MyStateMsg.setText(CommonService.UserInfo.stateMsg);

        View MmLayout = view.findViewById(R.id.MyLayout);

        MmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserActivity.class);
                startActivity(intent);
            }
        });

        Context context = view.getContext();

        RecyclerView recyclerView = view.findViewById(R.id.listUser);
        // recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);

        CommonService.Database = FirebaseDatabase.getInstance().getReference("users");

        FirebaseRecyclerOptions<UserInfo> options = new FirebaseRecyclerOptions.Builder<UserInfo>().setQuery(CommonService.Database, UserInfo.class).build();

        FirebaseRecyclerAdapter<UserInfo, UserListViewHolder > mFirebaseAdapter = new FirebaseRecyclerAdapter<UserInfo, UserListViewHolder >(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserListViewHolder  holder, int position, @NonNull final UserInfo model) {

                if (CommonService.UserInfo.uid.equals(model.uid)) {
                    //# 자신은 제외
                    holder.itemView.setVisibility(View.GONE);
                    holder.txtStateMsg.setVisibility(View.GONE);
                    holder.txtUserNm.setVisibility(View.GONE);
                    holder.imgUser.setVisibility(View.GONE);
                } else {
                    holder.txtUserNm.setText(model.name);
                    holder.txtStateMsg.setText(model.stateMsg);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), UserActivity.class);
                            intent.putExtra("uid", model.uid);
                            startActivity(intent);
                        }
                    });
                }
            }

            @NonNull
            @Override
            public UserListViewHolder  onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_list_item, parent, false);

                return new UserListViewHolder (view);
            }
        };


//            mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//                @Override
//                public void onItemRangeInserted(int positionStart, int itemCount) {
//                    super.onItemRangeInserted(positionStart, itemCount);
//                    int noticeCount = mFirebaseAdapter.getItemCount();
//                    int lastVisiblePosition =
//                            mLayoutManager.findLastCompletelyVisibleItemPosition();
//                    // If the recycler view is initially being loaded or the
//                    // user is at the bottom of the list, scroll to the bottom
//                    // of the list to show the newly added message.
//                    if (lastVisiblePosition == -1 ||
//                            (positionStart >= (noticeCount - 1) &&
//                                    lastVisiblePosition == (positionStart - 1))) {
//                        recyclerView.scrollToPosition(positionStart);
//                    }
//                }
//            });

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
