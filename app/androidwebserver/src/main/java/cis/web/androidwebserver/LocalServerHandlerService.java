package cis.web.androidwebserver;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.util.UniversalTimeScale;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

public class  LocalServerHandlerService extends Service {

    private static String internal_dir_name = "public_html";

    @Override
    public void onCreate() {
        super.onCreate();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ServerManager.getInstance().setUuid(android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));
        //Log.d("uuid",android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID));

        SharedPreferences sharedPreferences=getSharedPreferences("app_config", Context.MODE_PRIVATE);

        //get wifi address
        ServerManager.getInstance().setIp(ServerManager.getInstance().getWifiIPAddress(getApplicationContext()));

        if (ServerManager.getInstance().getIp()!=null) {
            if (ServerManager.getInstance().getIp().equals("0.0.0.0") ||
                    ServerManager.getInstance().getIp().equals("")){
                ServerManager.getInstance().setIp(ServerManager.getInstance().getMobileIPAddress());
            }
        }else{
            ServerManager.getInstance().setIp(ServerManager.getInstance().getMobileIPAddress());
        }

        String port=sharedPreferences.getString("port","");
        if (!port.equals("")){
            ServerManager.getInstance().setPort(Integer.valueOf(port));
        }
        String baseDir=sharedPreferences.getString("baseDir","");
        if (!baseDir.equals("")){
            ServerManager.getInstance().setBaseDir(baseDir);
        }

        ServerManager.getInstance().setBackgroundrun(sharedPreferences.getString("isRunBg","false").equals("true"));

        //Log.d("localPort",ServerInstanceManager.getInstance().getLOCAL_PORT()+"");
        try {
            if (ServerManager.getInstance().getIp() != null) {
                if (!ServerManager.getInstance().getIp().equals("")) {
                    TinyWebServer.startServer(ServerManager.getInstance().getIp(),
                            ServerManager.getInstance().getPort(),
                            ServerManager.getInstance().getBaseDir());
                }
            }
        }catch (Exception er){
            ServerManager.printStack(er);
        }

    }

    public  void startForeGroundService(Context context, Class activity) {
        try {
            Intent notificationIntent;
            String title="Tiny Web Server";
            String message="Keep me running !";

            notificationIntent = new Intent(context,activity);
            //notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
            //notificationIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.lock);

            String channel="";
            if (Build.VERSION.SDK_INT >=26) {

            }else{
                Notification notification = new Notification.Builder(context)
                        .setContentTitle(title)
                        //.setTicker("Waiting for incomming visitor")
                        .setContentText(message)
                        .setSmallIcon(R.drawable.lock)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentIntent(pendingIntent)
                        .setOngoing(true).build();
                startForeground(101, notification);
            }
        }catch (Exception er){
            er.printStackTrace();
            //new Logger(context.getClass().getName()+","+er.getMessage(),context);
        }

    }

    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager) {
        String name = "Tiny Web Server";
        String description = "Tiny web server notifications";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel(name, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        //Toast.makeText(this,"Server started",Toast.LENGTH_SHORT);
        // Log.d("HeartBeat","onStart Called");
        startForeGroundService(LocalServerHandlerService.this,ServerManager.getInstance().getBaseClass());
        return Service.START_STICKY;
    }

    private IBinder binder=new LocalServerHandlerService.LocalBinder();
    @Override
    public IBinder onBind(Intent intent) {
        //TODO for communication return IBinder implementation
        return binder;
    }

    public class LocalBinder extends Binder {
        LocalServerHandlerService getService(){
            return LocalServerHandlerService.this;
        }
    }

    public boolean checkFile(String filepath){
        File directory = this.getDir(internal_dir_name, Context.MODE_PRIVATE);
        File myDir = new File(directory.getPath());
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, filepath);
        if (file.exists()){  return true; }
        return false;
    }

    public static void SaveToInternal(final Context context, final Bitmap bitmap, final String filename) {
        try {
            File directory = context.getDir(internal_dir_name, Context.MODE_PRIVATE);
            if (directory.exists()) {
                File file = new File(directory, filename);
                if (!file.exists()){
                    file.createNewFile();
                }
            }
        } catch (Exception er) {
        }
    }



    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            //sendBroadcast(new Intent("REBOOT_SERVER"));
            System.out.println("on Destroy");
            //TinyWebServer.stopServer();
            //stopForeground(true);
        }catch (Exception er){}
    }

}
