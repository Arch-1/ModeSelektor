package it.open.modeselektor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
    final static String DatabaseVersion_Path = "/sdcard/ModeSelektor/DatabaseVersion.txt";
    final static String Config_Path = "/sdcard/ModeSelektor/Config";
    final static String ApplyOnBoot_Path = "/sdcard/ModeSelektor/Config/ApplyOnBoot.txt";
    final static String AutoBattery_Path = "/sdcard/ModeSelektor/Config/AutoBattery.txt";
    final static String AutoBatteryPercentage_Path = "/sdcard/ModeSelektor/Config/AutoBatteryPercentage.txt";
    final static String DefaultMode_Path = "/sdcard/ModeSelektor/Config/DefaultMode.txt";
    final static String BatteryAutoEnabled_Path = "/sdcard/ModeSelektor/Config/BatteryAutoEnabled.txt";
    final static String GPT_Path = "/sdcard/ModeSelektor/Config/GooglePlayTweaks.txt";
    final static String BPT_Path = "/sdcard/ModeSelektor/Config/BuildPropTweaks.txt";
    final static int Version = 4;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String Percentage;
    String Read;
    String Script;
    Switch GPT, BPT, Apply_on_boot, Auto_Battery;
    RadioButton Battery, Balanced, Performance;
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
            File file = new File(DatabaseVersion_Path);
            if(file.exists()){
                Read(DatabaseVersion_Path);
                int v = Integer.parseInt(Read);
                int cmp = v > Version ? +1 : v < Version ? -1 : 0;
                if (cmp == -1) {
                    copyFileOrDir("");
                } else {
                    RadioButton Battery = (RadioButton) findViewById(R.id.rdb2);
                    RadioButton Balanced = (RadioButton) findViewById(R.id.rdb3);
                    RadioButton Performance = (RadioButton) findViewById(R.id.rdb4);
                    Switch Apply_on_boot = (Switch) findViewById(R.id.sw);
                    Switch Auto_Battery = (Switch) findViewById(R.id.sw2);
                    Switch Google_Play_Tweaks = (Switch) findViewById(R.id.sw3);
                    Switch Build_Prop_Tweaks = (Switch) findViewById(R.id.sw4);
                    SeekBar Battery_percentage = (SeekBar) findViewById(R.id.batterysb);
                    TextView SelectedPercentace = (TextView) findViewById(R.id.SelPer);
                    String[] Directory = {Config_Path};
                    for (String directory : Directory ) {
                        File file3 = new File(directory);
                        if(file3.exists() && file3.isDirectory()){
                            Read(DefaultMode_Path);
                        switch (Read) {
                            case "Battery":
                                Battery.setChecked(true);
                            break;
                            case "Balanced":
                                Balanced.setChecked(true);
                            break;
                            case "Performance":
                                Performance.setChecked(true);
                            break;
                        }
                        Read(GPT_Path);
                        if (Read.equals("Y")){
                                Google_Play_Tweaks.setChecked(true);
                        }else {
                                Google_Play_Tweaks.setChecked(false);
                        }
                        Read(BPT_Path);
                        if (Read.equals("Y")){
                            Build_Prop_Tweaks.setChecked(true);
                        }else {
                            Build_Prop_Tweaks.setChecked(false);
                        }
                        Read(ApplyOnBoot_Path);
                        if (Read.equals("Y")){
                            Apply_on_boot.setChecked(true);
                        }else {
                            Apply_on_boot.setChecked(false);
                        }
                        Read(AutoBattery_Path);
                        if (Read.equals("Y")){
                            Auto_Battery.setChecked(true);
                        }else {
                            Auto_Battery.setChecked(false);
                        }
                        Read(AutoBatteryPercentage_Path);
                        SelectedPercentace.setText("Selected percentage: " + Read + "%");
                        int Percentage = Integer.parseInt(Read);
                        Battery_percentage.setProgress(Percentage);
                        BatteryAllert();
                        }
                    }
                }
            }
        }
    }

    public void BatteryAllert(){
        TextView UltraBatteryAutoEnable = (TextView) findViewById(R.id.Ubae);
        Read(BatteryAutoEnabled_Path);
        if (Read.equals("N")){
            UltraBatteryAutoEnable.setText("");
        }else{
            UltraBatteryAutoEnable.setText("Battery mode enabled, the battery is under the selected level");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_selektor, menu);
        Battery = (RadioButton) findViewById(R.id.rdb2);
        Balanced = (RadioButton) findViewById(R.id.rdb3);
        Performance = (RadioButton) findViewById(R.id.rdb4);
        GPT = (Switch) findViewById(R.id.sw3);
        BPT = (Switch) findViewById(R.id.sw4);
        Apply_on_boot = (Switch) findViewById(R.id.sw);
        Auto_Battery = (Switch) findViewById(R.id.sw2);
        Battery_percentage = (SeekBar) findViewById(R.id.batterysb);
        SelectedPercentace = (TextView) findViewById(R.id.SelPer);

        GPT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    Write("Y",GPT_Path);
                    Script = "Google_Play_Tweaks";
                    RunScript(Script);
                    Toast.makeText(getApplicationContext(), "Google Play Tweaks Enabled", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(Selektor.this);
                    builder.setTitle("Restore Google Play Services");
                    builder.setMessage("This action need reboot");
                    builder.setPositiveButton("REBOOT NOW", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Write("N",GPT_Path);
                            try {
                                Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Rebooting system", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("REBOOT LATER", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Write("N",GPT_Path);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        BPT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    Write("Y",BPT_Path);
                    Script = "Build_Prop_Tweaks";
                    RunScript(Script);
                    Toast.makeText(getApplicationContext(), "Build.prop Tweaks Enabled", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(Selektor.this);
                    builder.setTitle("Restore Build.prop");
                    builder.setMessage("This action need reboot");
                    builder.setPositiveButton("REBOOT NOW", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Write("N",BPT_Path);
                            try {
                                Runtime.getRuntime().exec(new String[]{"su", "-c", "reboot"});
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Rebooting system", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("REBOOT LATER", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Write("N",BPT_Path);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        Apply_on_boot.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    Write("Y",ApplyOnBoot_Path);
                    Toast.makeText(getApplicationContext(), "Apply on Boot Enabled", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Apply on Boot Disabled", Toast.LENGTH_SHORT).show();
                    Write("N",ApplyOnBoot_Path);
                }
            }
        });

        Auto_Battery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    Write("Y",AutoBattery_Path);
                    Toast.makeText(getApplicationContext(), "Auto Battery Enabled", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Selektor.this, ModeSelektorService.class);
                    startService(intent);
                }else{
                    Write("N",AutoBattery_Path);
                    Toast.makeText(getApplicationContext(), "Auto Battery Disabled", Toast.LENGTH_SHORT).show();
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
                Write(Percentage, AutoBatteryPercentage_Path);
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
            case R.id.reload:
                Intent refresh = new Intent(this, Selektor.class);
                startActivity(refresh);
                this.finish();
                return true;
            case R.id.lastlog:
                Intent lastlog = new Intent(this, LastLog.class);
                startActivity(lastlog);
                return true;
            case R.id.reset:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Reset Scripts");
                builder.setMessage("This action will overwrite all scripts in \n" +
                        "/sdcard/ModeSelektor directory \n" +
                        "do it only after a update or are if you have edited incorrectly the scripts.\n" +
                        "Are you sure? ");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        copyFileOrDir("");
                        Toast.makeText(getApplicationContext(), "Scripts are now restored", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.about:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("About");
                builder1.setMessage("ModeSelektor version v2.4 \n" +
                        "Author Davide Di Battista 2017-2018 \n" +
                        "GNU  General Public License version 3");
                builder1.setPositiveButton("CLOSE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert1 = builder1.create();
                alert1.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onRadioButtonClicked(View view){
        if (Battery.isChecked()) {
            Script = "Battery";
            RunScript(Script);
            Write(Script,DefaultMode_Path);
            Toast.makeText(getApplicationContext(), Script+" Mode Enabled", Toast.LENGTH_SHORT).show();
        } else if (Balanced.isChecked()) {
            Script = "Balanced";
            RunScript(Script);
            Write("N",BatteryAutoEnabled_Path);
            BatteryAllert();
            Write(Script,DefaultMode_Path);
            Toast.makeText(getApplicationContext(), Script+" Mode Enabled", Toast.LENGTH_SHORT).show();
        } else if (Performance.isChecked()) {
            Script = "Performance";
            RunScript(Script);
            Write(Script,DefaultMode_Path);
            Toast.makeText(getApplicationContext(), Script+" Mode Enabled", Toast.LENGTH_SHORT).show();
        }
        Write("N",BatteryAutoEnabled_Path);
        BatteryAllert();
    }

    public void RunScript(String script){
        try {
            Runtime.getRuntime().exec(new String[]{"su","-c","sh ~/sdcard/ModeSelektor/Scripts/"+script});
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
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