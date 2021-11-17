package cis.web.server;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cis.web.androidwebserver.LocalServerHandlerService;
import cis.web.androidwebserver.ServerManager;
import cis.web.androidwebserver.TinyWebServer;

public class MainActivity extends AppCompatActivity {

    private EditText serverDir;
    private EditText port;
    private TextView message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        port=findViewById(R.id.portNum);
        serverDir=findViewById(R.id.serverDir);
        CheckBox isService = findViewById(R.id.runasbgcheck);

        message=findViewById(R.id.message);
        message.setVisibility(View.INVISIBLE);

        CheckBox autorestart=findViewById(R.id.restartcheck);
        CheckBox remoteConfigcheck=findViewById(R.id.remoteConfigcheck);

        isService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ServerManager.getInstance().setBackgroundrun(isChecked);
            }
        });

        if (Build.VERSION.SDK_INT>=23) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4444);
            }
        }

        ServerManager.getInstance().setContext(getApplicationContext());
        ServerManager.getInstance().setIp(ServerManager.getInstance().getWifiIPAddress(getApplicationContext()));

        Button start_stopbtn=findViewById(R.id.start_stopbtn);
        start_stopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    init((Button) v);
                }else{
                    Toast.makeText(getApplicationContext(),"File write permission not granted",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==4444){

        }
    }

    private void init(Button view){

            final int portNum=Integer.valueOf(port.getText().toString());

            if (portNum<79){
                Toast.makeText(getApplicationContext(),"Invalid port number, must be greater than 80",Toast.LENGTH_SHORT).show();
                return;
            }

            ServerManager.getInstance().setPort(portNum);

            if(TinyWebServer.isStart){
                TinyWebServer.stopServer();
            }

            String dir = serverDir.getText().toString().trim();

            if (TinyWebServer.startServer(ServerManager.getInstance().getIp(),
                    ServerManager.getInstance().getPort(),
                    dir,ServerManager.getInstance().isBackgroundrun(),
                    this)) {

                view.setText("Running");
                message.setText("Server started http://" + ServerManager.getInstance().getIp() + ":" + ServerManager.getInstance().getPort());
                message.setVisibility(View.VISIBLE);

            }

    }

}
