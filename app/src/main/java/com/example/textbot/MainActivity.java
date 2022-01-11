package com.example.textbot;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {
    int count = 0;
    String number = "";
    String str = "";
    TextView phone;
    TextView received;
    TextView state;
    String[] first,second,third,fourth;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (permissions[0].equalsIgnoreCase(Manifest.permission.SEND_SMS) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted.
                    Log.d("TAG", "Permission granted");
                } else {
                    // Permission denied.
                    Log.d("TAG", "Permission denied");
                }
            }
        }
    }

    public class Receiver extends BroadcastReceiver{
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.d("TAG","Receiver");
            Bundle bundle = intent.getExtras();
            intent.getExtras();
            Object[] pdus = (Object[])bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[pdus.length];
            for(int i =0;i<messages.length;i++) {
                messages[i]=SmsMessage.createFromPdu((byte[])pdus[i],bundle.getString("format"));
                number = messages[i].getOriginatingAddress();
                str = messages[i].getMessageBody();
                Log.d("TAG", "onReceive: " + messages[i].getMessageBody());
                Log.d("TAG","Sender: "+number);
                phone.setText("Phone: "+number);
                received.setText("Receive: "+str);

                }
            if(count==3){
                state.setText("State: 4");
                Log.d("TAG",str.toLowerCase());
                if(str.toLowerCase().contains("what") || str.toLowerCase().contains("why") || str.toLowerCase().contains("really") || str.toLowerCase().contains("omg") || str.toLowerCase().contains("stop")){
                    sendMessage(fourth[(int)(Math.random()*fourth.length)]);
                    Log.d("TAG","work");
                    count=4;
                }
                else{
                    sendMessage("Can you say something else");
                }
            }
            if(count==2){
                state.setText("State: 3");
                Log.d("TAG",str.toLowerCase());
                if(str.toLowerCase().contains("what") || str.toLowerCase().contains("why") || str.toLowerCase().contains("really") || str.toLowerCase().contains("no")){
                    sendMessage(third[(int)(Math.random()*third.length)]);
                    Log.d("TAG","work");
                    count=3;
                }
                else{
                    sendMessage("Can you say something else");
                }
            }
            if(count==1){
                state.setText("State: 2");
                Log.d("TAG",str.toLowerCase());
                if(str.toLowerCase().contains("good") || str.toLowerCase().contains("great") || str.toLowerCase().contains("bad") || str.toLowerCase().contains("okay")){
                    sendMessage(second[(int)(Math.random()*second.length)]);
                    Log.d("TAG","work");
                    count=2;
                }
                else{
                    sendMessage("Can you please tell me how you are properly");
                }
            }
            if(count==0){
                state.setText("State: 1");
                Log.d("TAG",str.toLowerCase());
                if(str.toLowerCase().contains("hey") || str.toLowerCase().contains("hello") || str.toLowerCase().contains("what's up ") || str.toLowerCase().contains("hi")){
                    sendMessage(first[(int)(Math.random()*first.length)]);
                    Log.d("TAG","work");
                    count=1;
                }
                else{
                    sendMessage("Can you please greet me properly");
                }
            }
        }
    }
    public void sendMessage(final String msg){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG","sendMessage Running " +msg);
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(number,null,msg,null,null);
            }
        },1000);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phone = findViewById(R.id.id_phone);
        received = findViewById(R.id.id_receive);
        state = findViewById(R.id.id_state);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS,Manifest.permission.READ_PHONE_STATE}, 1);
        }
        Receiver receiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(receiver,intentFilter);
        first = new String[]{"Hello how are you ","Hey how are you doing","Hi how are you doing","Whats up",};
        second = new String[]{"Im breaking up with you","We're done im leaving you","Its you not me, we're done","Me and you arent together anymore"};
        third = new String[]{"I dont like you anymore","you aren't good enough for me","you annoy me","i hate everything about you"};
        fourth = new String[]{"Im ghosting you bye","I'm never talking to you again","Im blocking you"};
    }
}
