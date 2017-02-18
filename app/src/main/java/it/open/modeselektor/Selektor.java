package it.open.modeselektor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.view.Menu;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

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
    final static String DefaultMode_Path = "/sdcard/ModeSelektor/Config/DefaultMode.txt";
    final static String GPT_Path = "/sdcard/ModeSelektor/Config/GooglePlayTweaks.txt";
    final static String BPT_Path = "/sdcard/ModeSelektor/Config/BuildPropTweaks.txt";
    final static String Seeder_Path = "/sdcard/ModeSelektor/Config/Seeder.txt";
    final static String Rngd_Path = "/system/xbin/rngd";
    final static String CurrentEntropy_Path = "/proc/sys/kernel/random/entropy_avail";
    final static int Version = 6;
    private Handler EntroHandler;
    String Read, Script, CurrentEntropy;
    Switch GPT, BPT, Apply_on_boot, Seeder;
    RadioButton Battery, Balanced, Performance;
    ProgressBar SeederProg;
    TextView SeederTextV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selektor);
        File file = new File(DatabaseVersion_Path);
        if (!file.exists()){
            copyFileOrDir("");
        } else {
            Read(DatabaseVersion_Path);
            int v = Integer.parseInt(Read);
            int cmp = v > Version ? +1 : v < Version ? -1 : 0;
            if (cmp == -1) {
                copyFileOrDir("");
            }
        }
        RadioButton Battery = (RadioButton) findViewById(R.id.rdb2);
        RadioButton Balanced = (RadioButton) findViewById(R.id.rdb3);
        RadioButton Performance = (RadioButton) findViewById(R.id.rdb4);
        Switch Apply_on_boot = (Switch) findViewById(R.id.sw);
        Switch Google_Play_Tweaks = (Switch) findViewById(R.id.sw3);
        Switch Build_Prop_Tweaks = (Switch) findViewById(R.id.sw4);
        Switch Seeder = (Switch) findViewById(R.id.sw5);
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
                } else {
                    Google_Play_Tweaks.setChecked(false);
                }
                Read(BPT_Path);
                if (Read.equals("Y")){
                    Build_Prop_Tweaks.setChecked(true);
                } else {
                    Build_Prop_Tweaks.setChecked(false);
                }
                Read(Seeder_Path);
                if (Read.equals("Y")){
                    File file2 = new File(Rngd_Path);
                    if(!file2.exists()){
                        Seeder.setChecked(false);
                        Write("N",Seeder_Path);
                    } else {
                        Seeder.setChecked(true);
                    }
                } else {
                    Seeder.setChecked(false);
                        }
                Read(ApplyOnBoot_Path);
                if (Read.equals("Y")){
                    Apply_on_boot.setChecked(true);
                } else {
                    Apply_on_boot.setChecked(false);
                }
            }
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
        Seeder = (Switch) findViewById(R.id.sw5);
        Apply_on_boot = (Switch) findViewById(R.id.sw);
        SeederProg = (ProgressBar) findViewById(R.id.EntroBar);
        SeederTextV = (TextView) findViewById(R.id.Entro);
        EntroHandler = new Handler();
        EntroHandler.post(EntroUpdate);

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

        Seeder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    File file = new File(Rngd_Path);
                    if(!file.exists()){
                        Script = "Seeder_Install";
                        RunScript(Script);
                        Toast.makeText(getApplicationContext(), "Disable the seeder for better performance during the benchmarks", Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), "Remember to uninstall the seeder before uninstall ModeSelektor", Toast.LENGTH_LONG).show();
                    }
                    Script = "Seeder_On";
                    RunScript(Script);
                    Write("Y",Seeder_Path);
                    Toast.makeText(getApplicationContext(), "Seeder Enabled", Toast.LENGTH_SHORT).show();
                } else {
                    Script = "Seeder_Off";
                    RunScript(Script);
                    Toast.makeText(getApplicationContext(), "Seeder Disabled", Toast.LENGTH_SHORT).show();
                    Write("N",Seeder_Path);
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
        return true;
    }

    private Runnable EntroUpdate = new Runnable() {
        public void run() {
            ReadEntropy(CurrentEntropy_Path);
            SeederProg.setProgress(Integer.parseInt(CurrentEntropy));
            SeederTextV.setText(CurrentEntropy + "/4096");
            EntroHandler.postDelayed(this, 1000);
        }
    };

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
            case R.id.SeederUni:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("Uninstall the Seeder");
                builder2.setMessage("This action uninstall the seeder \n" +
                        "Are you sure? ");
                builder2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Script = "Seeder_Uninstall";
                        RunScript(Script);
                        Seeder.setChecked(false);
                        Toast.makeText(getApplicationContext(), "Seeder Uninstalled", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder2.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert2 = builder2.create();
                alert2.show();
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
                builder1.setMessage("ModeSelektor version v2.5.2 \n" +
                        "Author Davide Di Battista 2017-2018 \n" +
                        "Contributor Stefano 99 \n" +
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
            Write(Script,DefaultMode_Path);
            Toast.makeText(getApplicationContext(), Script+" Mode Enabled", Toast.LENGTH_SHORT).show();
        } else if (Performance.isChecked()) {
            Script = "Performance";
            RunScript(Script);
            Write(Script,DefaultMode_Path);
            Toast.makeText(getApplicationContext(), Script+" Mode Enabled", Toast.LENGTH_SHORT).show();
        }
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

    public void ReadEntropy(String Path){
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
                CurrentEntropy = (buf.toString());
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
}