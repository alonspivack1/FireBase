package myapplication.firebase;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class Main extends Activity
{
    Button SMSbtn;
    Button Codebtn;
    EditText etPhoneNo;
    EditText etCode;
    TextView tvCode,tvPhoneNo;
    Random rn;
    int codeInt;
    String codeString;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent2 = new Intent(Main.this,LogInActivity.class);
        startActivity(intent2);

        SMSbtn = (Button) findViewById(R.id.btnSendSMS);
        Codebtn = (Button) findViewById(R.id.btnSendCode);
        etPhoneNo = (EditText) findViewById(R.id.etPhoneNo);
        etCode = (EditText) findViewById(R.id.etCode);
        rn = (Random) new Random();
        tvCode = (TextView) findViewById(R.id.tvCode);
        tvPhoneNo = (TextView) findViewById(R.id.tvPhoneNo);
        tvCode.setVisibility(View.GONE);
        etCode.setVisibility(View.GONE);

    }
    private void sendSMS(String phoneNumber, String message)
    {

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public void SendSMS(View view) {
        String smsNum = etPhoneNo.getText().toString();
        if (smsNum.length()>0){
            codeInt=rn.nextInt(9000)+1000;
            codeString="" + codeInt;
            sendSMS(smsNum, codeString);
            etPhoneNo.setVisibility(View.GONE);
            tvPhoneNo.setVisibility(View.GONE);
            etCode.setVisibility(View.VISIBLE);
            tvCode.setVisibility(View.VISIBLE);
        }
        else{
            Toast.makeText(getBaseContext(),
                    "Please enter phone number.",
                    Toast.LENGTH_SHORT).show();}
    }

    public void SendCode(View view) {
            if (codeString.equals(etCode.getText().toString())) {
                Toast.makeText(getBaseContext(), "Good", Toast.LENGTH_LONG).show();
                /*Intent intent = new Intent(Main.this,SignUpActivity.class);
                startActivity(intent);*/
            }
            else {
                Toast.makeText(getBaseContext(), "NO GOOD", Toast.LENGTH_LONG).show();
            }
        }
    }

