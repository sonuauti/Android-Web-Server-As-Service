package cis.web.androidwebserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class ServerManager {


    private ServerManager (){ }
    private int port;
    private String ip;
    private Context context;
    private String uuid;
    private Class baseClass;
    private String baseDir;
    private boolean isBackgroundrun;

    private static ServerManager serverManager;

    public static ServerManager getInstance() {
        if (serverManager==null)
        return serverManager = new ServerManager();

        return serverManager;
    }

    public boolean isBackgroundrun() {
        return isBackgroundrun;
    }

    public void setBackgroundrun(boolean backgroundrun) {
        isBackgroundrun = backgroundrun;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getBaseDir() {
        if (baseDir==null)
            return "/tinyweb/";
        return baseDir;
    }

    public void setBaseClass(Class baseClass) {
        this.baseClass = baseClass;
    }

    public Class getBaseClass() {
        return baseClass;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public int getPort() {
        if (port==0)
            return 9000;
        return port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public String getWifiIPAddress(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();

        if(wifiMgr.isWifiEnabled()) {
            int ip = wifiInfo.getIpAddress();
            return Formatter.formatIpAddress(ip);
        }else{
            return null;
        }
    }

    public String getMobileIPAddress() {
        try {
            String ip="";
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            // System.out.println(interfaces);

            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());

                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {

                        if(addr.getAddress().length>0){

                            if (addr.getHostAddress().length()<=15) {
                                ip = addr.getHostAddress();
                            }

                            if (addr.getHostAddress().split("%").length>1){
                                //  Log.d("MOBILE_ADDRESS",addr.getHostAddress().split("%")[0]+" "+addr.getHostAddress().split("%")[1]);
                            }

                        }
                    }
                }
            }
            return ip;
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }

    public static boolean writeSharedPref(SharedPreferences sharedPref, String key, String value){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
        return editor.commit();
    }


    public static void printStack(Exception er){
            printStack(er);
    }

}
