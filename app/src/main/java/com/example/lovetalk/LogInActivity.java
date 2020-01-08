package com.example.lovetalk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LogInActivity extends AppCompatActivity {
private EditText phone,code;
private Button msend;
private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
String mVerificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);

        UserIsLoggedIn();
        phone = findViewById(R.id.phone_number);
        code = findViewById(R.id.your_code);
        msend = findViewById(R.id.verify);
        msend.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              try{
                  if(mVerificationId!= null){
                      verifyPhoneNumberWithCode();
                  }
                  else{
                      StartPhoneNumberVerification();
                  }

              }
              catch(Exception c){
                  System.out.println("Something Wrong");
              }

          }
      });


      mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
          @Override
          public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
              try{
                  signInWithPhoneAuthCredential(phoneAuthCredential);
              }

             catch(Exception e){
                  System.out.println("Something Wrong");
              }
          }

          @Override
          public void onVerificationFailed(@NonNull FirebaseException e) {

          }

          @Override
          public void onCodeSent(@NonNull String VerificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
              super.onCodeSent(VerificationId, forceResendingToken);
             mVerificationId = VerificationId;
             msend.setText("Verify Code");
          }
      };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                final   FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                   if(user != null){
                    final DatabaseReference mDBrefer = FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid());
                    mDBrefer.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()){
                                Map<String,Object> usermap = new HashMap<>();
                                usermap.put("phone",user.getPhoneNumber());
                                usermap.put("name",user.getPhoneNumber());
                                mDBrefer.updateChildren(usermap);
                            }
                            UserIsLoggedIn();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                   }
               }

            }
        });
    }

    private void UserIsLoggedIn() {
        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if(user!=null){
                startActivity(new Intent(getApplicationContext(),MainPageActivity.class));
                finish();
                return;
            }
        }
        catch(Exception e){
            System.out.println("Something Wrong");
        }

    }

    private void  StartPhoneNumberVerification(){
        try{
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone.getText().toString(),60,TimeUnit.SECONDS,this,mCallBacks);
        }
        catch(Exception e){
            System.out.println("Something Wrong");
        }

    }
    private void verifyPhoneNumberWithCode(){
        try{
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code.getText().toString());
            signInWithPhoneAuthCredential(credential);
        }
        catch(Exception e){
            System.out.println("Something Wrong");
        }

    }
}
