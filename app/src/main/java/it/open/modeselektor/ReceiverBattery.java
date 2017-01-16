//ModeSelektor v2.0
//Author Davide Di Battista
//2017-2018
//License GNU v3

package it.open.modeselektor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class ReceiverBattery extends BroadcastReceiver {

    final static String AutoUltraBattery_Path = "/sdcard/ModeSelektor/AutoUltraBattery.txt";
    final static String DefaultMode_Path = "/sdcard/ModeSelektor/DefaultMode.txt";
    final static String UltraBatteryAutoEnabled_Path = "/sdcard/ModeSelektor/UltraBatteryAutoEnabled.txt";
    String Read;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
            context.stopService(new Intent(context, ModeSelektorService.class));
            Read(AutoUltraBattery_Path);
            if (Read.equals("Y")){
                ApplyDefault();
                Write("N",UltraBatteryAutoEnabled_Path);
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
            Log.e("value", "sh command error");
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

    public void Write(String Write, String Path) {
        try {
            File file = new File(Path);
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter =
                    new OutputStreamWriter(fOut);
            myOutWriter.append(Write);
            myOutWriter.close();
            fOut.close();
        }catch (Exception e) {
            Log.e("value", "Write error");
        }
    }
}

