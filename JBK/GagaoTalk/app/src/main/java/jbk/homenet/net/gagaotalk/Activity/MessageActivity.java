package jbk.homenet.net.gagaotalk.Activity;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import jbk.homenet.net.gagaotalk.Class.ChatingRoomInfo;
import jbk.homenet.net.gagaotalk.Class.CommonService;
import jbk.homenet.net.gagaotalk.Class.FirbaseService;
import jbk.homenet.net.gagaotalk.Class.MessageData;
import jbk.homenet.net.gagaotalk.Class.UserInfo;
import jbk.homenet.net.gagaotalk.R;

/**
 * Message Activity
 */
public class MessageActivity extends BaseActivity implements
        View.OnClickListener {

    //region == [ ViewHolder Class ] ==
    /**
     * 사용자 리스트 ViewHolder
     */
    public static class MessageDataViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUser;
        TextView txtUserNm;
        TextView txtMsg;
        TextView txtTime;

        TextView txtMyMsg;
        TextView txtMyTime;

        TextView txtMyRead;
        TextView txtRead;


        View sendLayout;
        View postLayout;

        String messageId;

        MessageDataViewHolder(View v) {
            super(v);
            imgUser = itemView.findViewById(R.id.imgChatUser);
            txtUserNm = itemView.findViewById(R.id.txtChatUserNm);
            txtMsg= itemView.findViewById(R.id.txtChatMsg);
            txtTime = itemView.findViewById(R.id.txtChatLastTime);

            txtMyMsg = itemView.findViewById(R.id.txtMyChatMsg);
            txtMyTime = itemView.findViewById(R.id.txtMyChatLastTime);

            sendLayout = itemView.findViewById(R.id.sendLayout);
            postLayout = itemView.findViewById(R.id.postLayout);

            txtMyRead = itemView.findViewById(R.id.txtMyChatRead);
            txtRead = itemView.findViewById(R.id.txtChatRead);
        }
    }
    //endregion == [ ViewHolder Class ] ==

    //region == [ Fields ] ==

    /**
     * 채팅방 id
     */
    private String chatringRoomId;

    /**
     * 입력EditText
     */
    private EditText txtMessage;

    /**
     * 메세지 RecyclerView
     */
    private RecyclerView recyclerView;

    /**
     * Date Format
     */
    private final SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("a h:mm", Locale.getDefault());

    private FirebaseRecyclerAdapter<MessageData, MessageActivity.MessageDataViewHolder> mFirebaseAdapter;

    private List<String> userList = new ArrayList<String>();

    //endregion == [ Fields ] ==

    //region == [ Override Methods ] ==

    //region  -- onCreate() : onCreate --
    /**
     * onCreate
     * @param savedInstanceState savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Intent intent = getIntent();
        this.chatringRoomId = intent.getStringExtra("chatingRoomId");

        this.txtMessage = findViewById(R.id.txtMsg);
        this.recyclerView = findViewById(R.id.listChating);

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);


        DatabaseReference chatingRoomInfo = FirebaseDatabase.getInstance().getReference();
        chatingRoomInfo.child("messageData").child(CommonService.UserInfo.uid).child(chatringRoomId);
        chatingRoomInfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String data = Objects.requireNonNull(dataSnapshot.child("chatingRoom").child(CommonService.UserInfo.uid).child(chatringRoomId).child("TargetUser").getValue()).toString();

                assert data != null;
                userList.add(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        CommonService.Database = FirebaseDatabase.getInstance().getReference("messageData").child(this.chatringRoomId);

        CommonService.Database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                MessageData messageData = dataSnapshot.getValue(MessageData.class);

                DatabaseReference lastReadMessageDataBase = FirebaseDatabase.getInstance().getReference();

                //# 마지막 읽은 메세지 iD 저장
                lastReadMessageDataBase.child("chatingRoom").child(CommonService.UserInfo.uid).child(chatringRoomId).child("LastReadMessageId").setValue(messageData.MessageId);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseRecyclerOptions<MessageData> options = new FirebaseRecyclerOptions.Builder<MessageData>().setQuery(CommonService.Database, MessageData.class).build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<MessageData, MessageActivity.MessageDataViewHolder>(options) {
            @NonNull
            @Override
            public MessageActivity.MessageDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(MessageActivity.this)
                        .inflate(R.layout.chat_list_item, parent, false);

                return new MessageActivity.MessageDataViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final MessageActivity.MessageDataViewHolder holder, int position, @NonNull final MessageData model) {

                CommonService.Database = FirebaseDatabase.getInstance().getReference();

                CommonService.Database.child("messageData").child(chatringRoomId).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (model.MessageId != null) {

                            holder.messageId = model.MessageId;

                            if (dataSnapshot.child(model.MessageId) .child("readUser").child(CommonService.UserInfo.uid).getValue() == null){
                                DatabaseReference messageInfo= FirebaseDatabase.getInstance().getReference();
                                messageInfo.child("messageData").child(chatringRoomId).child(model.MessageId).child("readUser").child(CommonService.UserInfo.uid).setValue(true);
                            }

                            if (model.SendUser.equals(CommonService.UserInfo.uid)) {
                                //# 내가보낸 메세지
                                holder.sendLayout.setVisibility(View.VISIBLE);
                                holder.postLayout.setVisibility(View.GONE);

                                holder.txtMyMsg.setText(model.Message);
                                holder.txtMyTime.setText(mSimpleDateFormat.format(model.Time));

                                holder.imgUser.setImageResource(android.R.color.transparent);

                                if (dataSnapshot.child(model.MessageId) .child("readUser").child(userList.get(0)).getValue() != null){
                                    holder.txtMyRead.setVisibility(View.GONE);
                                }
                            } else {
                                //# 받은 메세지
                                holder.sendLayout.setVisibility(View.GONE);
                                holder.postLayout.setVisibility(View.VISIBLE);

                                holder.txtMsg.setText(model.Message);
                                holder.txtTime.setText(mSimpleDateFormat.format(model.Time));

                                if (dataSnapshot.child(model.MessageId) .child("readUser").child(CommonService.UserInfo.uid).getValue() != null){
                                    holder.txtRead.setVisibility(View.GONE);
                                }

                                CommonService.Database = FirebaseDatabase.getInstance().getReference();
                                CommonService.Database.child("users").child(model.SendUser).addValueEventListener(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        UserInfo sendtUserInfo = dataSnapshot.getValue(UserInfo.class);

                                        assert sendtUserInfo != null;
                                        holder.txtUserNm.setText(sendtUserInfo.name);

                                        if (sendtUserInfo.hasImage != null && sendtUserInfo.hasImage) {
                                            StorageReference riversRootRef = FirbaseService.FirebaseStorage.getReference();
                                            StorageReference riversProfileRef = riversRootRef.child("profileImage");
                                            StorageReference riversRef = riversProfileRef.child("profileImage/" + sendtUserInfo.uid);

                                            if (riversRef.getName().equals(sendtUserInfo.uid)) {
                                                // Load the image using Glide
                                                Glide.with(getApplicationContext())
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


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int noticeCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (noticeCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    recyclerView.scrollToPosition(positionStart);
                }
            }
        });

        recyclerView.setAdapter(mFirebaseAdapter);
        mFirebaseAdapter.startListening();

        //# Click Listener 등록
        findViewById(R.id.btnSend).setOnClickListener(this);
    }
    //endregion  -- onCreate() : onCreate --

    //region -- onClick() : onClick --
    /**
     * onClick
     * @param v View
     */
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btnSend) {
            this.SendMessage();
        }
    }
    //endregion -- onClick() : onClick --

    //endregion == [ Override Methods ] ==

    //region == [ Methods ] ==

    //region -- SendMessage() : 메세지 전송 --
    /**
     * 메세지 전송
     */
    private void SendMessage()
    {
        MessageData messageData = new MessageData();
        messageData.SendUser = CommonService.UserInfo.uid;
        messageData.Message = this.txtMessage.getText().toString();
        messageData.Time = System.currentTimeMillis();


        CommonService.Database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messageUid = CommonService.Database.child("chatingRoom").child(CommonService.UserInfo.uid).push();

        messageData.MessageId = messageUid.getKey();

        CommonService.Database.child("messageData").child(this.chatringRoomId).child(messageData.MessageId).setValue(messageData);

        this.txtMessage.setText("");

        recyclerView.scrollToPosition(mFirebaseAdapter.getItemCount() - 1);

//        //# 키보드 닫기
//        ((InputMethodManager) Objects.requireNonNull(this.getSystemService(Context.INPUT_METHOD_SERVICE)))
//                .hideSoftInputFromWindow(this.txtMessage.getWindowToken(), 0);
    }
    //endregion -- SendMessage() : 메세지 전송 --

    //endregion == [ Methods ] ==

}
