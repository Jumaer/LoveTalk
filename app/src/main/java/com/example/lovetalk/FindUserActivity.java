package com.example.lovetalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.example.lovetalk.country_founder.CountryToPhonePrefix;
import com.example.lovetalk.user_all_recycle.UserListAdapter;
import com.example.lovetalk.user_all_recycle.UserObject;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {
   private RecyclerView mUserList;
   private RecyclerView.Adapter mUserListAdapter;
   private RecyclerView.LayoutManager mUserLayoutManager;
   ArrayList<UserObject>userList,contactList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);


        contactList= new ArrayList<>();
        userList = new ArrayList<>();
        initializeRecyclerView();
        getContactList();
    }
    private void initializeRecyclerView(){
        mUserList = findViewById(R.id.recycle_UserList);
        mUserList.setNestedScrollingEnabled(false);
        mUserList.setHasFixedSize(true);
        mUserLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        mUserList.setLayoutManager(mUserLayoutManager);
        mUserListAdapter = new UserListAdapter(userList);
        mUserList.setAdapter(mUserListAdapter);
    }
    private void getContactList(){
        String ISOprefix = getCountryISO();
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while (phones.moveToNext()){
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phone =phone.replace("","");
            phone =phone.replace("-","");
            phone =phone.replace("(","");
            phone =phone.replace(")","");



           if(!String.valueOf(phone.charAt(0)).equals("+"))
                   phone = ISOprefix +phone;



            UserObject mContact = new UserObject(name,phone,"");
            contactList.add(mContact);
          //  mUserListAdapter.notifyDataSetChanged();


            getUserDetails(mContact);

        }
    }
    private String getCountryISO(){
        String iso = null;
        TelephonyManager telephonyManager = (TelephonyManager)getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null)
            if(!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso =telephonyManager.getNetworkCountryIso().toString();
        return CountryToPhonePrefix.getPhone(iso);
    }
   private void  getUserDetails(UserObject mContact){
       DatabaseReference muserDB = FirebaseDatabase.getInstance().getReference().child("user");
       Query query = muserDB.orderByChild("phone").equalTo(mContact.getPhone_number());
       query.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   String phone ="",
                   name = "";

                   for(DataSnapshot childsnapshot : dataSnapshot.getChildren()){
                       if(childsnapshot.child("phone").getValue()!= null)
                           phone= childsnapshot.child("phone").getValue().toString();
                       if(childsnapshot.child("name").getValue()!= null)
                           name= childsnapshot.child("name").getValue().toString();
                       UserObject mUser = new UserObject(name,phone,childsnapshot.getKey());
                       if(name.equals(phone))
                           for (UserObject mContactIterator : contactList) {
                               if (mContactIterator.getPhone_number().equals(mUser.getPhone_number())) {
                                   mUser.setName(mContactIterator.getName());
                               }
                           }



                       userList.add(mUser);
                       mUserListAdapter.notifyDataSetChanged();
                       return;
                   }

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });
   }
}
