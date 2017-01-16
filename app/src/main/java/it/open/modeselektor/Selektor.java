package it.open.modeselektor;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.view.Menu;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.view.View;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.String;

public class Selektor extends AppCompatActivity {

    final static String Database_Path = "/sdcard/ModeSelektor/";
    final static String ApplyOnBoot_Path = "/sdcard/ModeSelektor/ApplyOnBoot.txt";
    final static String AutoUltraBattery_Path = "/sdcard/ModeSelektor/AutoUltraBattery.txt";
    final static String AutoUltraBatteryPercentage_Path = "/sdcard/ModeSelektor/AutoUltraBatteryPercentage.txt";
    final static String DefaultMode_Path = "/sdcard/ModeSelektor/DefaultMode.txt";
    final static String UltraBatteryAutoEnabled_Path = "/sdcard/ModeSelektor/UltraBatteryAutoEnabled.txt";
    private static final int PERMISSION_REQUEST_CODE = 1;
    String Percentage;
    String Read;
    Switch Apply_on_boot, Auto_Ultra_Battery;
    RadioButton Ultra_Battery, Battery, Balanced, Performance, Ultra_Performance;
    SeekBar Battery_percentage;
    TextView SelectedPercentace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selektor);
        AskRootPermission();
        checkPermissionWrite();
        if (!checkPermissionWrite()){
            requestPermissionWrite();
        }
        checkPermissionRead();
        if (!checkPermissionRead()){
        requestPermissionRead();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
        RadioButton Ultra_Battery = (RadioButton) findViewById(R.id.rdb1);
        RadioButton Battery = (RadioButton) findViewById(R.id.rdb2);
        RadioButton Balanced = (RadioButton) findViewById(R.id.rdb3);
        RadioButton Performance = (RadioButton) findViewById(R.id.rdb4);
        RadioButton Ultra_Performance = (RadioButton) findViewById(R.id.rdb5);
        Switch Apply_on_boot = (Switch) findViewById(R.id.sw);
        Switch Auto_Ultra_Battery = (Switch) findViewById(R.id.sw2);
        SeekBar Battery_percentage = (SeekBar) findViewById(R.id.batterysb);
        TextView SelectedPercentace = (TextView) findViewById(R.id.SelPer);
        TextView UltraBatteryAutoEnable = (TextView) findViewById(R.id.Ubae);
        String[] Directory = {Database_Path};
        for (String directory : Directory ) {
            File file = new File(directory);
            if(file.exists() && file.isDirectory()){
                Read(DefaultMode_Path);
                if (Read.equals("Ultra_Battery")){
                    Ultra_Battery.setChecked(true);
                } else if (Read.equals("Battery")){
                    Battery.setChecked(true);
                } else if (Read.equals("Balanced")){
                    Balanced.setChecked(true);
                } else if (Read.equals("Performance")){
                    Performance.setChecked(true);
                } else if (Read.equals("Ultra_Performance")){
                    Ultra_Performance.setChecked(true);
                }
                Read(ApplyOnBoot_Path);
                if (Read.equals("Y")){
                    Apply_on_boot.setChecked(true);
                }else if (Read.equals("N")){
                    Apply_on_boot.setChecked(false);
                }
                Read(AutoUltraBattery_Path);
                if (Read.equals("Y")){
                    Auto_Ultra_Battery.setChecked(true);
                }else if (Read.equals("N")){
                    Auto_Ultra_Battery.setChecked(false);
                }
                Read(AutoUltraBatteryPercentage_Path);
                SelectedPercentace.setText("Selected percentage: " + Read + "%");
                int Percentage = Integer.parseInt(Read);
                Battery_percentage.setProgress(Percentage);
                Read(UltraBatteryAutoEnabled_Path);
                if (Read.equals("N")){
                    UltraBatteryAutoEnable.setText("");
                }else{
                    UltraBatteryAutoEnable.setText("Ultra Battery enabled the battery is under the selected level");
                }
            }
            }
        }
    }

    //Create menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_selektor, menu);
        Ultra_Battery = (RadioButton) findViewById(R.id.rdb1);
        Battery = (RadioButton) findViewById(R.id.rdb2);
        Balanced = (RadioButton) findViewById(R.id.rdb3);
        Performance = (RadioButton) findViewById(R.id.rdb4);
        Ultra_Performance = (RadioButton) findViewById(R.id.rdb5);
        Apply_on_boot = (Switch) findViewById(R.id.sw);
        Auto_Ultra_Battery = (Switch) findViewById(R.id.sw2);
        Battery_percentage = (SeekBar) findViewById(R.id.batterysb);
        SelectedPercentace = (TextView) findViewById(R.id.SelPer);

        Apply_on_boot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    Write("Y",ApplyOnBoot_Path);
                    Toast.makeText(getApplicationContext(), "Apply on Boot Enabled", Toast.LENGTH_SHORT).show();
                }else{
                    Write("N",ApplyOnBoot_Path);
                }
            }
        });

        Auto_Ultra_Battery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    Write("Y",AutoUltraBattery_Path);
                    Toast.makeText(getApplicationContext(), "Auto Ultra Battery Enabled", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Selektor.this, ModeSelektorService.class);
                    startService(intent);
                }else{
                    Write("N",AutoUltraBattery_Path);
                    Toast.makeText(getApplicationContext(), "Auto Ultra Battery Disabled", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Selektor.this, ModeSelektorService.class);
                    stopService(intent);
                }

            }
        });

        Battery_percentage.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int percentage = 0;
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                percentage = progresValue;
                Percentage = "" + percentage;
                SelectedPercentace.setText("Selected percentage: " + Percentage + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Write(Percentage, AutoUltraBatteryPercentage_Path);
            }
        });
                return true;
        }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        int id=item.getItemId();
        switch (id) {
            case R.id.sources:
                Uri github = Uri.parse("https://github.com/lafonte0/ModeSelektor");
                Intent a = new Intent(Intent.ACTION_VIEW, github);
                startActivity(a);
                return true;
            case R.id.updates:
                Uri sourceforge = Uri.parse("https://sourceforge.net/projects/modeselektor/files/?source=navbar");
                Intent b = new Intent(Intent.ACTION_VIEW, sourceforge);
                startActivity(b);
                return true;
            case R.id.licence:
                Uri licence = Uri.parse("https://raw.githubusercontent.com/lafonte0/ModeSelektor/master/LICENSE");
                Intent c = new Intent(Intent.ACTION_VIEW, licence);
                startActivity(c);
                return true;
            case R.id.reset:
                copyFileOrDir("");
                Toast.makeText(getApplicationContext(), "Stock scripts restored", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRadioButtonClicked(View view){
            if (Ultra_Battery.isChecked()) {
                Apply_on_boot.setChecked(false);
                Write("Ultra_Battery", DefaultMode_Path);
                try {
                    Process a = Runtime.getRuntime().exec("su -c sh /sdcard/ModeSelektor/Ultra_Battery");
                    Toast.makeText(getApplicationContext(), "Ultra Battery Mode Enabled", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.e("value", "sh command error");
                }

            } else if (Battery.isChecked()) {
                Apply_on_boot.setChecked(false);
                Write("Battery", DefaultMode_Path);
                try {
                    Process a = Runtime.getRuntime().exec("su -c sh /sdcard/ModeSelektor/Battery");
                    Toast.makeText(getApplicationContext(),"Battery Mode Enabled", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.e("value", "sh command error");
                }

            } else if (Balanced.isChecked()) {
                Apply_on_boot.setChecked(false);
                Write("Balanced",DefaultMode_Path);
                try {
                    Process a = Runtime.getRuntime().exec("su -c sh /sdcard/ModeSelektor/Balanced");
                    Toast.makeText(getApplicationContext(), "Balanced ModeEnabled", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.e("value", "sh command error");
                }

            } else if (Performance.isChecked()) {
                Apply_on_boot.setChecked(false);
                Write("Performance",DefaultMode_Path);
                try {
                    Process a = Runtime.getRuntime().exec("su -c sh /sdcard/ModeSelektor/Performance");
                    Toast.makeText(getApplicationContext(), "Performance Mode Enabled", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.e("value", "sh command error");
                }

            } else if (Ultra_Performance.isChecked()) {
                Apply_on_boot.setChecked(false);
                Write("Ultra_Performance",DefaultMode_Path);
                try {
                Process a = Runtime.getRuntime().exec("su -c sh /sdcard/ModeSelektor/Ultra_Performance");
                    Toast.makeText(getApplicationContext(), "Ultra Performance Mode Enabled", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    Log.e("value", "sh command error");
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
            Log.e("value", "Read failed.");
        }
        catch (Throwable t) {
            Log.e("value", "Read failed.");
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
            Log.e("value", "Write failed.");
        }
    }

    private void copyFilesToSdCard() {
        copyFileOrDir("");
    }

    private void copyFileOrDir(String path) {
        AssetManager assetManager = this.getAssets();
        String assets[] = null;
        try {
            Log.i("tag", "copyFileOrDir() "+path);
            assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath =  Database_Path + path;
                Log.i("tag", "path="+fullPath);
                File dir = new File(fullPath);
                if (!dir.exists() && !path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                    if (!dir.mkdirs())
                        Log.i("tag", "could not create dir "+fullPath);
                for (int i = 0; i < assets.length; ++i) {
                    String p;
                    if (path.equals(""))
                        p = "";
                    else
                        p = path + "/";

                    if (!path.startsWith("images") && !path.startsWith("sounds") && !path.startsWith("webkit"))
                        copyFileOrDir( p + assets[i]);
                }
            }
        } catch (IOException ex) {
            Log.e("tag", "I/O Exception", ex);
        }
    }

    private void copyFile(String filename) {
        AssetManager assetManager = this.getAssets();

        InputStream in = null;
        OutputStream out = null;
        String newFileName = null;
        try {
            Log.i("tag", "copyFile() "+filename);
            in = assetManager.open(filename);
            if (filename.endsWith(".sh"))
                newFileName = Database_Path + filename.substring(0, filename.length()-4);
            else
                newFileName = Database_Path + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e("tag", "Exception in copyFile() of "+newFileName);
            Log.e("tag", "Exception in copyFile() "+e.toString());
        }
    }

    public void AskRootPermission() {
        Process p;
        try {
            p = Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("value", "NO ROOT PERMISSION");
            Toast.makeText(getApplicationContext(), "Root permission denied, please allow Root permission", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermissionWrite() {
        int result = ContextCompat.checkSelfPermission(Selektor.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    private boolean checkPermissionRead() {
        int result = ContextCompat.checkSelfPermission(Selektor.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    private void requestPermissionWrite() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(Selektor.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Selektor.this, "Read External Storage permission denied, please allow read permission in settings", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Selektor.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void requestPermissionRead() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Selektor.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(Selektor.this, "Read External Storage permission denied, please allow read permission in settings", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Selektor.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    copyFileOrDir("");
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
}