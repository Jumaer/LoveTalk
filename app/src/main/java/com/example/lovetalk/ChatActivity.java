package com.example.lovetalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lovetalk.chat.MediaAdapter;
import com.example.lovetalk.chat.MessageAdapter;
import com.example.lovetalk.chat.ChatObject;
import com.example.lovetalk.chat.MessageObject;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mChat,mMedia;
    private RecyclerView.Adapter mChatAdapter,mMediaAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager,mMediaLayoutManager;
    ArrayList<MessageObject> messageList;


    String chatID;
    DatabaseReference mChatdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatID = getIntent().getExtras().getString("chatID");
        mChatdb =  FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);
        Button  msend = findViewById(R.id.send);
        Button maddMedia = findViewById(R.id.add_media);
        msend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        maddMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        initializeMessage();
        initializeMedia();
        getChatMessages();
    }
    private void getChatMessages(){
     mChatdb.addChildEventListener(new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

             if(dataSnapshot.exists()){
                 String text ="",
                         creatorID ="";
                 if(dataSnapshot.child("text").getValue() != null)
                     text = dataSnapshot.child("text").getValue().toString();
                 if(dataSnapshot.child("creator").getValue() != null)
                     creatorID = dataSnapshot.child("creator").getValue().toString();


                 MessageObject mMessage = new MessageObject(dataSnapshot.getKey(),creatorID,text);
                 messageList.add(mMessage);
                 mChatLayoutManager.scrollToPosition(messageList.size()-1);
                 mChatAdapter.notifyDataSetChanged();
             }






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
    }
    EditText mMessage;



    int PICK_IMAGE_INTENT = 1;
    ArrayList<String>mediaUriList = new ArrayList<>();

    private void initializeMedia(){
        mediaUriList = new ArrayList<>();
        mMedia = findViewById(R.id.media_list);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(true);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mChatAdapter = new MediaAdapter(mediaUriList,getApplicationContext());
        mMedia.setAdapter(mMediaAdapter);
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent .putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture(s)"),PICK_IMAGE_INTENT);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_OK){
            if(requestCode== PICK_IMAGE_INTENT){
                if(data.getClipData()==null){
                    mediaUriList.add(data.getData().toString());
                }
                else {
                    for (int i = 0;i<data.getClipData().getItemCount();i++){
                        mediaUriList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }

                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }
    int totalMediaUploaded = 0;
    ArrayList<String>mediaIDlist= new ArrayList<>();
    private void sendMessage(){
         mMessage = findViewById(R.id.message_write);
        if(!mMessage.getText().toString().isEmpty()){
            String messageID= mChatdb.push().getKey();
            final DatabaseReference newMessageDB = mChatdb.child(messageID);

           final Map newMessageMap = new HashMap<>();

            newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());

            if(!mMessage.getText().toString().isEmpty())
                     newMessageMap.put("text",mMessage.getText().toString());

        //    newMessageDB.updateChildren(newMessageMap);


            if(!mediaUriList.isEmpty()){
                  for(String mediaUri : mediaUriList){
                      String mediaId= newMessageDB.child("media").push().getKey();
                      mediaIDlist.add(mediaId);
                      final StorageReference filePath = FirebaseStorage.getInstance().getReference().
                              child("chat").child(chatID).child(mediaId).child(mediaId);

                      UploadTask uploadTask = filePath.putFile(Uri.parse(mediaUri));
                      uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                          @Override
                          public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                               newMessageMap.put("/media/"+ mediaIDlist.get( totalMediaUploaded) + "/",uri.toString());
                               totalMediaUploaded++;
                               if(totalMediaUploaded== mediaUriList.size()){
                                       updateReferenceWithNewMessae(newMessageDB,newMessageMap);
                               }
                                }
                            });
                          }
                      });
                  }
            }
            else {
                if(!mMessage.getText().toString().isEmpty())
                    updateReferenceWithNewMessae(newMessageDB,newMessageMap);

            }
        }

    }
    private  void updateReferenceWithNewMessae(DatabaseReference newMessageDB , Map newMessageMap){
        newMessageDB.updateChildren(newMessageMap);
        mMessage.setText(null);
        mediaUriList.clear();
        mediaIDlist.clear();
        mMediaAdapter.notifyDataSetChanged();
    }
    private void initializeMessage(){
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.recycle_message);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(true);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }


}
