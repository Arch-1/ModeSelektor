package it.open.modeselektor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LastLog extends AppCompatActivity {

    final static String LastLog_Path = "/sdcard/ModeSelektor/LastLog.log";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lastlog);
        StringBuilder text = new StringBuilder();
    try {
        BufferedReader br = new BufferedReader(new FileReader(LastLog_Path ));
        String line;
        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
        br.close();
    }
    catch (IOException e) {
    }
        TextView lastlog = (TextView) findViewById(R.id.lastlog);
        lastlog.setText(text.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_lastlog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id=item.getItemId();
        switch (id) {
            case R.id.back:
                Intent lastlog = new Intent(this, Selektor.class);
                startActivity(lastlog);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}




