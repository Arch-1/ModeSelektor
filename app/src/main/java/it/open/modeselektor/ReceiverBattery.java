package it.open.modeselektor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class ReceiverBattery extends BroadcastReceiver {

    final static String AutoUltraBattery_Path = "/sdcard/ModeSelektor/AutoUltraBattery.txt";
    final static String DefaultMode_Path = "/sdcard/ModeSelektor/DefaultMode.txt";
    String Read;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
            context.stopService(new Intent(context, ModeSelektorService.class));
            Read(AutoUltraBattery_Path);
            if (Read.equals("Y")){
                ApplyDefault();
        }
        } else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                context.startService(new Intent(context, ModeSelektorService.class));
        }
    }

    public void ApplyDefault(){
        Read(DefaultMode_Path);
        try {
            Process a = Runtime.getRuntime().exec("su -c sh /sdcard/ModeSelektor/" + Read);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void Read(String Path){
        try {
            File file = new File(Path);
            InputStream in = new FileInputStream(file);
            if (in != null) {
                InputStreamReader tmp=new InputStreamReader(in);
                BufferedReader reader=new BufferedReader(tmp);
                String str;
                StringBuilder buf=new StringBuilder();
                while ((str = reader.readLine()) != null) {
                    buf.append(str);
                }
                in.close();
                Read = (buf.toString());
            }
        }
        catch (java.io.FileNotFoundException e) {
        }
        catch (Throwable t) {
        }
    }
}

