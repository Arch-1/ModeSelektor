package it.open.modeselektor;

        import android.Manifest;
        import android.app.Activity;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.widget.Toast;

        import java.io.IOException;

public class Permission extends Activity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_permission);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                AskPermission();
            }
        }, 2000);
    }


    public void AskPermission(){
        AskRootPermission();
        checkPermissionWrite();
        if (!checkPermissionWrite()){
            requestPermissionWrite();
            checkPermissionRead();
        }
        if (!checkPermissionRead()){
            requestPermissionRead();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Intent selektor= new Intent(this, Selektor.class);
            startActivity(selektor);
            Permission.this.finish();
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
        int result = ContextCompat.checkSelfPermission(Permission.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    private boolean checkPermissionRead() {
        int result = ContextCompat.checkSelfPermission(Permission.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }

    }

    private void requestPermissionWrite() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Permission.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(Permission.this, "Read External Storage permission denied, please allow read permission in settings", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Permission.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void requestPermissionRead() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Permission.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(Permission.this, "Read External Storage permission denied, please allow read permission in settings", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(Permission.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent selektor= new Intent(this, Selektor.class);
                    startActivity(selektor);
                    Permission.this.finish();
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
}
