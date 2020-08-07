package com.uzair.sinchcallingsdk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private AdapterForRecycler adapterForRecycler;
    private List<UserModel> list;
    private String secret = "0X+Q87wpD02YnM5QvPLG5A==";
    private String appKey = "91e0cac3-c151-4b75-8aff-de6f21b9f8f1";
    private static final String ENVIRONMENT = "clientapi.sinch.com";
    private SinchClient sinchClient;
    private Call call;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE},
                    1);
        }


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        list = new ArrayList<>();
        adapterForRecycler = new AdapterForRecycler(list, this);


        getUsersList();



        // Instantiate a SinchClient using the SinchClientBuilder.
        sinchClient = Sinch.getSinchClientBuilder()
                .context(this)
                .userId(firebaseAuth.getCurrentUser().getUid())
                .applicationKey(appKey)
                .applicationSecret(secret)
                .environmentHost(ENVIRONMENT)
                .build();

        sinchClient.checkManifest();
        // Specify the client capabilities.
        sinchClient.setSupportCalling(true);
     //   sinchClient.setSupportManagedPush(true);
// or
       //sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();

        sinchClient.getCallClient().addCallClientListener(new SinchCallClientListener());

        sinchClient.start();



    }



    private void getUsersList()
    {
        try{
            databaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    UserModel userModel = dataSnapshot.getValue(UserModel.class);
                    list.add(userModel);

                    recyclerView.setAdapter(adapterForRecycler);

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
        catch (Exception e)
        {
            Log.d("ErrorInList", "getUsersList: "+e.getMessage());
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private class SinchCallListener implements CallListener {
        @Override
        public void onCallEnded(Call endedCall) {
            call = null;
            endedCall.hangup();
            setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
            Toast.makeText(MainActivity.this, "Call ended", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEstablished(Call establishedCall) {
            Toast.makeText(MainActivity.this, "Call establish", Toast.LENGTH_SHORT).show();
            setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
        }

        @Override
        public void onCallProgressing(Call progressingCall) {
            Toast.makeText(MainActivity.this, "Ringing", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> pushPairs) {

        }
    }


    public void callingUser(UserModel user)
    {

        if(call == null) {
            name = user.getUserName();
            call = sinchClient.getCallClient().callUser(user.getUid());
            call.addCallListener(new SinchCallListener());

            showDialog();
        }


    }

    private void showDialog()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Calling");
        alert.setMessage("Your Friend calling you");
        alert.setPositiveButton("Hang Up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (call != null) {
                    call.hangup();
                    dialog.dismiss();
                }
            }
        });

        alert.show();
    }

    private class SinchCallClientListener implements CallClientListener
    {

        @Override
        public void onIncomingCall(CallClient callClient, Call call)
        {
            Toast.makeText(MainActivity.this, "Show dialog to attend or decline the call", Toast.LENGTH_SHORT).show();

            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setMessage("Received call from "+name);
            alert.setTitle("Ring");
            alert.setPositiveButton("Pick", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    call.answer();

                }
            }).setNeutralButton("Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    call.hangup();
                    dialog.dismiss();

                }
            });

            alert.show();

        }
    }


}
