package d4d.com.subsidio.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import d4d.com.subsidio.R;
import d4d.com.subsidio.comunication.WebSocketComunication;
import d4d.com.subsidio.models.SessionManager;
import d4d.com.subsidio.utils.Utils;


/**
 * Created by Ing. Juan Pablo León on 16/12/2019.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        TextView txt=findViewById(R.id.txt_descripcion_app);
        txt.setText(getResources().getString(R.string.ppl_descripcion)+"\n\n       "+ SessionManager.getNombreOperadora()
                +"\n\n         RUC "+ SessionManager.getIdOperadora());

        Thread.setDefaultUncaughtExceptionHandler(new WebSocketComunication.TopExceptionHandler(this));
        FloatingActionButton map_fab = findViewById(R.id.fab_info_error);
        map_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarErrorAppCorreo();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void enviarErrorAppCorreo(){
        String line;
        String trace="Dispositivo "+ Utils.getManufacturer()+" - "+Utils.obtenerModelo()+"\nLog de Errores APP PROTECTia:\n";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(SettingsActivity.this.openFileInput("stack.trace")));
            while((line = reader.readLine()) != null) {
                trace += line+"\n";
            }
        } catch(FileNotFoundException fnfe) {
            // ...
        } catch(IOException ioe) {
            // ...
        }

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        String subject = Utils.getManufacturer()+" - "+Utils.obtenerModelo()+" - Log de errores";
        String body = trace + "\n";
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"protectiapiloto@gmail.com"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");
        SettingsActivity.this.startActivity(Intent.createChooser(sendIntent, "Title:"));
        SettingsActivity.this.deleteFile("stack.trace");
    }

}
