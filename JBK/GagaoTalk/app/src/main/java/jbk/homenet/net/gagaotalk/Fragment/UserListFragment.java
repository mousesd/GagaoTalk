package jbk.homenet.net.gagaotalk.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import jbk.homenet.net.gagaotalk.Adapter.MsgListRecyclerViewAdapter;
import jbk.homenet.net.gagaotalk.Class.UserInfo;
import jbk.homenet.net.gagaotalk.Fragment.dummy.DummyContent;
import jbk.homenet.net.gagaotalk.R;

public class UserListFragment extends Fragment {

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<UserInfo, MessageViewHolder> mFirebaseAdapter;
    private LinearLayoutManager mLayoutManager;

    private RecyclerView recyclerView;

    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser;
        TextView txtUserNm;
        TextView txtStateMsg;

        public MessageViewHolder(View v) {
            super(v);
            imgUser = itemView.findViewById(R.id.imgUser);
            txtUserNm = itemView.findViewById(R.id.txtUserNm);
            txtStateMsg = itemView.findViewById(R.id.txtStateMsg);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);

// Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            mLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(mLayoutManager);

            // New child entries
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("users");
            FirebaseRecyclerOptions<UserInfo> options = new FirebaseRecyclerOptions.Builder<UserInfo>().setQuery(mFirebaseDatabaseReference , UserInfo.class).build();

            mFirebaseAdapter = new FirebaseRecyclerAdapter<UserInfo, MessageViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull UserInfo model) {
                    holder.txtUserNm.setText(model.name);
                    holder.txtStateMsg.setText(model.stateMsg);
//                    holder.txtUserNm.setText(model.name);

//                    if (message.getPhotoUrl() == null) {
//                        IconicsDrawable iconicsDrawable = new IconicsDrawable(ChatActivity.this, Ionicons.Icon.ion_ios_contact_outline).sizeDp(36).color(message.getColor());
//                        viewHolder.messengerImageView.setImageDrawable(iconicsDrawable);
//                    } else {
//                        Glide.with(ChatActivity.this)
//                                .load(message.getPhotoUrl())
//                                .into(viewHolder.messengerImageView);
//                    }
                }

                @Override
                public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.user_list_item, parent, false);

                    return new MessageViewHolder(view);
                }


            };

            recyclerView.setAdapter(mFirebaseAdapter);

        }

        return view;
    }




    @Override
    public void onDetach() {
        super.onDetach();
    }


}
