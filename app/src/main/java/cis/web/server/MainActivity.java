package cis.web.server;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import cis.web.androidwebserver.TinyWebServer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText port=findViewById(R.id.portNum);
        EditText serverDir=findViewById(R.id.serverDir);
        CheckBox isService = findViewById(R.id.runasbgcheck);
        CheckBox autorestart=findViewById(R.id.restartcheck);
        CheckBox remoteConfigcheck=findViewById(R.id.remoteConfigcheck);

        int portNum=Integer.valueOf(port.getText().toString());
        if (portNum<79){
            Toast.makeText(getApplicationContext(),"Invalid port number, must be greater than 80",Toast.LENGTH_SHORT).show();
            TinyWebServer.stopServer();
            return;
        }
        
        if(TinyWebServer.isStart){
            TinyWebServer.stopServer();
        }

        TinyWebServer.startServer("localhost",portNum, "/Users/cis/Desktop/web/public_html");

    }
}
