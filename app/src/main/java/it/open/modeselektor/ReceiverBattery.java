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

    final static String DefaultMode_Path = "/sdcard/ModeSelektor/Config/DefaultMode.txt";
    final static String BatteryAutoEnabled_Path = "/sdcard/ModeSelektor/Config/BatteryAutoEnabled.txt";
    String Read;
    private Context contest;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals(Intent.ACTION_POWER_CONNECTED)) {
            context.stopService(new Intent(context, ModeSelektorService.class));
            Read(BatteryAutoEnabled_Path);
            if (Read.equals("Y")){
                ApplyDefault();
                Write("N",BatteryAutoEnabled_Path);
                Read(DefaultMode_Path);
                Toast.makeText(context, Read + " Mode Enabled", Toast.LENGTH_SHORT).show();
            }
        } else if(action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                context.startService(new Intent(context, ModeSelektorService.class));
        }
    }

    public void ApplyDefault(){
        Read(DefaultMode_Path);
        try {
            Runtime.getRuntime().exec(new String[]{"su", "-c", "sh ~/sdcard/ModeSelektor/Scripts/" + Read});
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

