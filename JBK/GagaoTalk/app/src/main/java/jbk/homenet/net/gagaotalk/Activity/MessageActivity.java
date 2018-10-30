package jbk.homenet.net.gagaotalk.Activity;



import android.content.Intent;

import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.firebase.iid.FirebaseInstanceId;

import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;


import jbk.homenet.net.gagaotalk.Class.ChatingRoomInfo;
import jbk.homenet.net.gagaotalk.Class.CommonService;
import jbk.homenet.net.gagaotalk.Class.FirbaseService;
import jbk.homenet.net.gagaotalk.Class.FireBaseDataBaseRef;
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

    private UserInfo targetUserInfo;


    RequestQueue queue;


    private FireBaseDataBaseRef _chatingRoomInfo;

    private FireBaseDataBaseRef _messageDataDB;

    private FireBaseDataBaseRef _msgDataDB;
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

        queue = Volley.newRequestQueue(getApplicationContext());

        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        DatabaseReference chatingRoomInfo = FirebaseDatabase.getInstance().getReference().child("chatingRoom").child(CommonService.UserInfo.uid).child(chatringRoomId);

        chatingRoomInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userList.add(dataSnapshot.child("TargetUser").getValue().toString());

                DatabaseReference targetUser = FirebaseDatabase.getInstance().getReference();
                targetUser.child("users").child(userList.get(0)).addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        targetUserInfo = dataSnapshot.getValue(UserInfo.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        chatingRoomInfo.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                String data = Objects.requireNonNull(dataSnapshot.child("chatingRoom").child(CommonService.UserInfo.uid).child(chatringRoomId).child("TargetUser").getValue()).toString();
//
//                assert data != null;
//                userList.add(data);
//
//
//                DatabaseReference targetUser = FirebaseDatabase.getInstance().getReference();
//                targetUser.child("users").child(userList.get(0)).addValueEventListener(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        targetUserInfo = dataSnapshot.getValue(UserInfo.class);
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        DatabaseReference messageDataDB = FirebaseDatabase.getInstance().getReference("messageData").child(this.chatringRoomId);

        _messageDataDB = new FireBaseDataBaseRef(messageDataDB, new ChildEventListener() {
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
//
//
//        messageDataDB.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//                MessageData messageData = dataSnapshot.getValue(MessageData.class);
//
//                DatabaseReference lastReadMessageDataBase = FirebaseDatabase.getInstance().getReference();
//
//                //# 마지막 읽은 메세지 iD 저장
//                lastReadMessageDataBase.child("chatingRoom").child(CommonService.UserInfo.uid).child(chatringRoomId).child("LastReadMessageId").setValue(messageData.MessageId);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        FirebaseRecyclerOptions<MessageData> options = new FirebaseRecyclerOptions.Builder<MessageData>().setQuery(messageDataDB, MessageData.class).build();

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

                DatabaseReference msgDataDB = FirebaseDatabase.getInstance().getReference().child("messageData").child(chatringRoomId);

                _msgDataDB = new FireBaseDataBaseRef(msgDataDB, new ValueEventListener() {

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

                                if (dataSnapshot.child(model.MessageId).child("readUser").child(userList.get(0)).getValue() != null){
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

                                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference();
                                userDB.child("users").child(model.SendUser).addValueEventListener(new ValueEventListener() {

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
//
//                msgDataDB.child("messageData").child(chatringRoomId).addValueEventListener(new ValueEventListener() {
//
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                        if (model.MessageId != null) {
//
//                            holder.messageId = model.MessageId;
//
//                            if (dataSnapshot.child(model.MessageId) .child("readUser").child(CommonService.UserInfo.uid).getValue() == null){
//                                DatabaseReference messageInfo= FirebaseDatabase.getInstance().getReference();
//                                messageInfo.child("messageData").child(chatringRoomId).child(model.MessageId).child("readUser").child(CommonService.UserInfo.uid).setValue(true);
//                            }
//
//                            if (model.SendUser.equals(CommonService.UserInfo.uid)) {
//                                //# 내가보낸 메세지
//                                holder.sendLayout.setVisibility(View.VISIBLE);
//                                holder.postLayout.setVisibility(View.GONE);
//
//                                holder.txtMyMsg.setText(model.Message);
//                                holder.txtMyTime.setText(mSimpleDateFormat.format(model.Time));
//
//                                holder.imgUser.setImageResource(android.R.color.transparent);
//
//                                if (dataSnapshot.child(model.MessageId) .child("readUser").child(userList.get(0)).getValue() != null){
//                                    holder.txtMyRead.setVisibility(View.GONE);
//                                }
//                            } else {
//                                //# 받은 메세지
//                                holder.sendLayout.setVisibility(View.GONE);
//                                holder.postLayout.setVisibility(View.VISIBLE);
//
//                                holder.txtMsg.setText(model.Message);
//                                holder.txtTime.setText(mSimpleDateFormat.format(model.Time));
//
//                                if (dataSnapshot.child(model.MessageId) .child("readUser").child(CommonService.UserInfo.uid).getValue() != null){
//                                    holder.txtRead.setVisibility(View.GONE);
//                                }
//
//                                DatabaseReference userDB = FirebaseDatabase.getInstance().getReference();
//                                userDB.child("users").child(model.SendUser).addValueEventListener(new ValueEventListener() {
//
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                                        UserInfo sendtUserInfo = dataSnapshot.getValue(UserInfo.class);
//
//                                        assert sendtUserInfo != null;
//                                        holder.txtUserNm.setText(sendtUserInfo.name);
//
//                                        if (sendtUserInfo.hasImage != null && sendtUserInfo.hasImage) {
//                                            StorageReference riversRootRef = FirbaseService.FirebaseStorage.getReference();
//                                            StorageReference riversProfileRef = riversRootRef.child("profileImage");
//                                            StorageReference riversRef = riversProfileRef.child("profileImage/" + sendtUserInfo.uid);
//
//                                            if (riversRef.getName().equals(sendtUserInfo.uid)) {
//                                                // Load the image using Glide
//                                                Glide.with(getApplicationContext())
//                                                        .using(new FirebaseImageLoader())
//                                                        .load(riversRef)
//                                                        .into(holder.imgUser);
//                                            }
//                                        } else {
//                                            holder.imgUser.setImageResource(android.R.color.transparent);
//                                        }
//
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                    }
//                                });
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (this._chatingRoomInfo != null)
            this._chatingRoomInfo.detach();
        if (this._messageDataDB != null)
            this._messageDataDB.detach();
        if (this._msgDataDB != null)
            this._msgDataDB.detach();
    }
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

        DatabaseReference chatingRoomDb = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messageUid = chatingRoomDb.child("chatingRoom").child(CommonService.UserInfo.uid).push();

        messageData.MessageId = messageUid.getKey();

        chatingRoomDb.child("messageData").child(this.chatringRoomId).child(messageData.MessageId).setValue(messageData);

        send(this.txtMessage.getText().toString());
        this.txtMessage.setText("");



        recyclerView.scrollToPosition(mFirebaseAdapter.getItemCount() - 1);

//        //# 키보드 닫기
//        ((InputMethodManager) Objects.requireNonNull(this.getSystemService(Context.INPUT_METHOD_SERVICE)))
//                .hideSoftInputFromWindow(this.txtMessage.getWindowToken(), 0);
    }
    //endregion -- SendMessage() : 메세지 전송 --

    //region -- send() : fcm Push --
    /**
     * fcm Push
     * @param input Message
     */
    public void send(String input) {

        JSONObject requestData = new JSONObject();

        try {
            requestData.put("priority", "high");

            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", input);
            dataObj.put("from_user", CommonService.UserInfo.name);
            requestData.put("data", dataObj);

            JSONArray idArray = new JSONArray();
            idArray.put(0, targetUserInfo.tokenId);
            requestData.put("registration_ids", idArray);

        } catch(Exception e) {
            e.printStackTrace();
        }

        sendData(requestData, new SendResponseListener() {
            @Override
            public void onRequestCompleted() {

            }

            @Override
            public void onRequestStarted() {

            }

            @Override
            public void onRequestWithError(VolleyError error) {

            }
        });
    }
    //endregion -- send() : fcm Push --

    //region -- SendResponseListener --
    /**
     * SendResponseListener
     */
    public interface SendResponseListener {
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }
    //endregion -- SendResponseListener --

    //region -- sendData() : SenData fcm Push --
    /**
     * SenData fcm Push
     * @param requestData 전송데이터
     * @param listener SendResponseListener
     */
    public void sendData(JSONObject requestData, final SendResponseListener listener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Authorization", "key=AAAAFmuyltY:APA91bFzzMkOXyqVOH4d-3a6a9OM7bkcA9LHvKMawyssgdwNrIVScRJayRsMNfD-foAuee-Javpy0b3VS6eSorYGwRTtVPnQtBOKtuUWUXK9TiAdVSWKmQS98JGspn7bL3DDfm-bO0B_DNEBuGRGtiSk8mq5METwEw");

                 return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        queue.add(request);
    }
    //endregion -- sendData() : SenData fcm Push --

    //endregion == [ Methods ] ==

}
