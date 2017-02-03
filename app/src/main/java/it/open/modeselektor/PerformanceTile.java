package it.open.modeselektor;


import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.service.quicksettings.Tile;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RequiresApi(api = Build.VERSION_CODES.N)

public class PerformanceTile extends TileService {

    final static String DefaultMode_Path = "/sdcard/ModeSelektor/Config/DefaultMode.txt";
    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private static final String PREFERENCES_KEY = "com.google.android_quick_settings";
    String Read;

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onClick() {
        super.onClick();
        updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    private void updateTile() {
        Tile tile = this.getQsTile();
        boolean isActive = getServiceStatus();
        Icon newIcon = null;
        int newState;
        newState = Tile.STATE_ACTIVE;
        if (isActive) {
            try {
                Runtime.getRuntime().exec(new String[]{"su","-c","sh /sdcard/ModeSelektor/Scripts/Performance"});
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_perf_off);
            Toast.makeText(getApplicationContext(), "Performance Mode Enabled", Toast.LENGTH_SHORT).show();
        } else {
            Read(DefaultMode_Path);
            try {
                Runtime.getRuntime().exec(new String[]{"su","-c","sh /sdcard/ModeSelektor/Scripts/"+Read});
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getApplicationContext(), Read + "Mode Enabled", Toast.LENGTH_SHORT).show();
            newIcon = Icon.createWithResource(getApplicationContext(), R.drawable.ic_perf_on);
        }
        tile.setIcon(newIcon);
        tile.setState(newState);
        tile.updateTile();
    }

    private boolean getServiceStatus() {

        SharedPreferences prefs =
                getApplicationContext()
                        .getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();

        return isActive;
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
            Log.e("value", "Read failed.");
        }
        catch (Throwable t) {
            Log.e("value", "Read failed.");
        }
    }
}
