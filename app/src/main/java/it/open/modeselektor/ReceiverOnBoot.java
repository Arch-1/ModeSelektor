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

public class ReceiverOnBoot extends BroadcastReceiver {

    final static String ApplyOnBoot_Path = "/sdcard/ModeSelektor/Config/ApplyOnBoot.txt";
    final static String DefaultMode_Path = "/sdcard/ModeSelektor/Config/DefaultMode.txt";
    final static String AutoBattery_Path = "/sdcard/ModeSelektor/Config/AutoBattery.txt";
    final static String BatteryAutoEnabled_Path = "/sdcard/ModeSelektor/Config/BatteryAutoEnabled.txt";
    final static String GPT_Path = "/sdcard/ModeSelektor/Config/GooglePlayTweaks.txt";
    final static String BPT_Path = "/sdcard/ModeSelektor/Config/BuildPropTweaks.txt";
    String Read;

    @Override
    public void onReceive(Context context, Intent intent) {
        Read(ApplyOnBoot_Path);
        if (Read.equals("Y")){
            Read(DefaultMode_Path);
            try {
                Runtime.getRuntime().exec(new String[]{"su", "-c", "sh ~/sdcard/ModeSelektor/Scripts/" + Read});
                Write("N",BatteryAutoEnabled_Path);
                Toast.makeText(context, Read + " Mode Enabled", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("value", "sh command error");
            }
            Read(GPT_Path);
            if (Read.equals("Y")){
                try {
                    Runtime.getRuntime().exec(new String[]{"su", "-c", "sh ~/sdcard/ModeSelektor/Scripts/Google_Play_Tweaks"});
                    Toast.makeText(context,  "Google Play Tweaks Enabled", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("value", "sh command error");
                    }
            }
            Read(BPT_Path);
            if (Read.equals("Y")){
                try {
                    Runtime.getRuntime().exec(new String[]{"su", "-c", "sh ~/sdcard/ModeSelektor/Scripts/Build_Prop_Tweaks"});
                    Toast.makeText(context,  "Build.prop Tweaks Enabled", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("value", "sh command error");
                }
            }
            Read(AutoBattery_Path);
            if (Read.equals("Y")){
                context.startService(new Intent(context, ModeSelektorService.class));
            } else {
                context.stopService(new Intent(context, ModeSelektorService.class));
            }
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
            Log.e("value", "Read error");
        }
        catch (Throwable t) {
            Log.e("value", "Read error");
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