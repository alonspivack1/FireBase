package myapplication.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Chat extends AppCompatActivity {

    Intent intent;
    DatabaseReference reference;
    String room,username,chatusername;
    FirebaseAuth mAuth;
     DatabaseReference databaseReference;
    int MessageNum;
     String Messagenumber = "";
    DataSnapshot dataSnapshot2,dataSnapshot3;
     FirebaseAuth firebaseAuth;
    EditText etMessageText;
    Map<String, Object> objectMap2,objectMap3;
    SimpleAdapter adapter;
    ListView lvchat;
    Boolean NewChat=true;
    HashMap<String,String> item;
    String[][] Message=   new String[1000][2];
    int FLAGINT=0;

    ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        lvchat = (ListView)findViewById(R.id.lvchat);
        lvchat.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        lvchat.setStackFromBottom(true);

        adapter = new SimpleAdapter(this, list,
                R.layout.twolines,
                new String[] { "line1","line2" },
                new int[] {R.id.line_a, R.id.line_b});





         intent= getIntent();
         username = intent.getStringExtra("username");
         chatusername = intent.getStringExtra("chatusername");
         room = intent.getStringExtra("room");
        mAuth = FirebaseAuth.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        etMessageText = (EditText)findViewById(R.id.etMessageText);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                dataSnapshot3 = dataSnapshot;
                dataSnapshot2 = dataSnapshot;
                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.child("Chats").child(room).child("num").getValue();
                Messagenumber = objectMap.toString().substring(5,objectMap.toString().length()-1);
                MessageNum = Integer.parseInt(Messagenumber);
                FLAGINT++;

            if (FLAGINT%2!=0){
                try
                {
                    Thread.sleep(400);
                    RefreshChat();
                }
                catch(InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }});
    }

   private void sendMessage(String sender, String receiver, String message) {
                   etMessageText.setText("");
                   reference = FirebaseDatabase.getInstance().getReference("Chats").child(room);
                   HashMap<String, String> hashMap2 = new HashMap<>();
                   hashMap2.put("sender",sender);
                   hashMap2.put("receiver",receiver);
                   hashMap2.put("message",message);
                   reference.child(""+MessageNum).setValue(hashMap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if (task.isComplete()){
                               MessageNum++;
                               reference.child("num").child("num").setValue(MessageNum);
                               lvchat.setSelection(MessageNum);

                           }
                       }});
                    }


    public void SendMessage(View view) {
        if (!etMessageText.getText().toString().equals("")){
        sendMessage(username,chatusername,etMessageText.getText().toString());

    }



    }
    public void RefreshChat() {
        if (NewChat){
            NewChat=false;
        for (int i=0; i<MessageNum-1; i++)
        {
            objectMap2 = (HashMap<String, Object>) dataSnapshot2.child("Chats").child(room).child(String.valueOf(i+1)).getValue();
            Message[i][0]=(objectMap2.toString().substring(9,(objectMap2.toString().length()-21-username.length()-chatusername.length())));
            objectMap3 = (HashMap<String, Object>) dataSnapshot3.child("Chats").child(room).child(String.valueOf(i+1)).getValue();
            if (objectMap3.toString().substring(29+Message[i][0].length()+chatusername.length(),objectMap3.toString().length()-1).equals(username)) {
                Message[i][1] = "you";
            }
            else{
                Message[i][1] = chatusername;
            }
            if (i==MessageNum-2)
            {
                for(int j=0;j<MessageNum-1;j++){
                    item = new HashMap<String,String>();
                    item.put( "line1", Message[j][0]);
                    item.put( "line2", Message[j][1]);
                    list.add( item );
                }
                lvchat.setAdapter(adapter);
                lvchat.setSelection(MessageNum);
            }
        }


        }
        else {
            for (int i=MessageNum-2; i<MessageNum-1; i++)
            {
                objectMap2 = (HashMap<String, Object>) dataSnapshot2.child("Chats").child(room).child(String.valueOf(i+1)).getValue();
                Message[i][0]=(objectMap2.toString().substring(9,(objectMap2.toString().length()-21-username.length()-chatusername.length())));
                objectMap3 = (HashMap<String, Object>) dataSnapshot3.child("Chats").child(room).child(String.valueOf(i+1)).getValue();
                if (objectMap3.toString().substring(29+Message[i][0].length()+chatusername.length(),objectMap3.toString().length()-1).equals(username)) {
                    Message[i][1] = "you";
                }
                else{
                    Message[i][1] = chatusername;
                }

            }

            item = new HashMap<String,String>();
            item.put( "line1", Message[MessageNum-2][0]);
            item.put( "line2", Message[MessageNum-2][1]);
            list.add( item) ;
            }
            lvchat.setSelection(MessageNum);
            }


}









