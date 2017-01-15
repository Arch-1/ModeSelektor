package it.open.modeselektor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class ModeSelektorService extends Service {

    final static String AutoUltraBatteryPercentage_Path = "/sdcard/ModeSelektor/AutoUltraBatteryPercentage.txt";
    final static String UltraBatteryAutoEnabled_Path = "/sdcard/ModeSelektor/UltraBatteryAutoEnabled.txt";
    String Read;
    int BatteryLevel;
    int SelectedBattery;

    public BroadcastReceiver batInfoReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent batteryIntent) {
            BatteryLevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            Read(AutoUltraBatteryPercentage_Path);
            SelectedBattery = Integer.parseInt(Read);
            int cmp = SelectedBattery > BatteryLevel ? +1 : SelectedBattery < BatteryLevel ? -1 : 0;
            if (cmp == +1){
                Ultra_Battery();
            } else if (cmp == -1) {
                Write("N",UltraBatteryAutoEnabled_Path);
            }else {
                Ultra_Battery();
            }
        }
    };

   public void Ultra_Battery() {
        try {
            Process a = Runtime.getRuntime().exec("su -c sh /sdcard/ModeSelektor/Ultra_Battery");
            Toast.makeText(getApplicationContext(), "Ultra Battery Mode Enabled", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
        Write("Y",UltraBatteryAutoEnabled_Path);
        stopSelf();
    }

    @Override
    public void onCreate() {
        registerReceiver(batInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(batInfoReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
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
            Toast
                    .makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
                    .show();
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
        }
    }
}

