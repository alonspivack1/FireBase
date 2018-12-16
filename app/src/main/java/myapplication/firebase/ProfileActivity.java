package myapplication.firebase;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    private Button buttonLogout;
    private TextView tvAnswer;
    private DatabaseReference databaseReference,databaseReference2;
    private EditText editTextName;
    private Button buttonSave;
    private String StringAnswer;
    private EditText etchatusername;
    private DatabaseReference reference,reference2;
    Boolean NewRoom=true;
    Intent intentChat;
    DataSnapshot dataSnapshot2,dataSnapshot3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference2 = FirebaseDatabase.getInstance().getReference();
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        textViewUserEmail = (TextView)findViewById(R.id.textViewUserEmail);
        editTextName = (EditText) findViewById(R.id.editTextDisplayName);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        etchatusername = (EditText)findViewById(R.id.etchatusername);
        tvAnswer = (TextView)findViewById(R.id.tvAnswer);
        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        textViewUserEmail.setText("Welcome: " + user.getEmail());
        buttonLogout.setOnClickListener(this);
        buttonSave.setOnClickListener(this);




        databaseReference.addValueEventListener(new ValueEventListener() {
             @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 dataSnapshot3 = dataSnapshot;
                 Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.child("Users").child(user.getUid()).getValue();
                 StringAnswer = objectMap.toString().substring(10,objectMap.toString().lastIndexOf(","));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot2 = dataSnapshot;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }

        );

    }

    private void saveUserInformation() {
        String name = editTextName.getText().toString().trim();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id",user.getUid());
        hashMap.put("username",name);
        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                task.isComplete();
            }
        });


        Toast.makeText(this, "Information Saved...",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if (view==buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LogInActivity.class));
        }
        if (view == buttonSave) {
            saveUserInformation();
        }

    }

    public void Answer(View view) {

        tvAnswer.setText("Answer: " + StringAnswer);
    }


    public void StartChat(View view) {
        final String chatusername = etchatusername.getText().toString();
        if (etchatusername.getText().toString().length()>0) {
        Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot2.child("Names").child(chatusername).getValue();
        if ((objectMap!=null)&&(chatusername.length()>0))
        {
            intentChat = new Intent(this,Chat.class);
            intentChat.putExtra("room",getChatName(StringAnswer,chatusername));
            intentChat.putExtra("username", StringAnswer);
            intentChat.putExtra("chatusername", chatusername);

                    if (dataSnapshot2.child("Chats").hasChild(getChatName(StringAnswer,chatusername))) {
                        NewRoom = false;

                    }

            if (!NewRoom){
                startActivity(intentChat);
            }
            else {

                reference2 = FirebaseDatabase.getInstance().getReference("Chats");
                HashMap<String, Object> hashMap2 = new HashMap<>();
                hashMap2.put("num", 1);
                reference2.child(getChatName(StringAnswer, chatusername)).child("num").setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete()) {
                            reference = FirebaseDatabase.getInstance().getReference("Chats").child(getChatName(StringAnswer,chatusername));
                            HashMap<String, String> hashMap2 = new HashMap<>();
                            hashMap2.put("sender",StringAnswer);
                            hashMap2.put("receiver",chatusername);
                            hashMap2.put("message","startchat");
                            reference.child(""+1).setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isComplete()){
                                        reference.child("num").child("num").setValue(2);
                                        startActivity(intentChat);
                                    }
                                }});


                        }
                    }
                });
            }
        }
        else {Toast.makeText(this, "NO GOOD", Toast.LENGTH_SHORT).show();}
        }
        else {Toast.makeText(this, "NO GOOD", Toast.LENGTH_SHORT).show();}
    }


    public String getChatName(String a, String b) {
        String c="";
        a.trim();
        b.trim();
        if (a.length()!=b.length()) {
            if (a.length() > b.length()) {
                c = a + b;
            }
            if (b.length() > a.length()) {
                c = b + a;
            }
        }
        else {
            for (int i=0; i<a.length(); i++){
            if (a.charAt(i)>b.charAt(i))
            {
                c = a+b;
                i=a.length();
            }
            else{
                if (b.charAt(i)>a.charAt(i)) {
                    c=b+a;
                    i=a.length();
                }
            }
            }
        }


        return c;
    }



}