package mx.ita.findmybusiness;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

public class configuracion extends AppCompatActivity {

    Button btnBugs;
    Button btnIdiomas;
    Switch modo;
    SharedPreferences.Editor editor;
    String mode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        setSupportActionBar(findViewById(R.id.toolbarsettings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnBugs = findViewById(R.id.btnReportarBug);
        btnIdiomas = findViewById(R.id.btnIdioma);
        SharedPreferences sharedPreferences = this.getSharedPreferences("Modo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        mode = sharedPreferences.getString("is", "off");
        btnBugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(configuracion.this,bugsActivity.class);
                startActivity(i);
            }
        });

        btnIdiomas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent o = new Intent(configuracion.this, idiomas.class);
                startActivity(o);
            }
        });

        modo = findViewById(R.id.switchmodo);
        if(mode.equals("on"))
            modo.setChecked(true);
        else
            modo.setChecked(false);
        Log.i("MODO",mode);
        modo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mode.equals("on")){
                    editor.putString("is","off");
                    editor.commit();
                    Log.i("Valorant",""+mode);
                }else{
                    editor.putString("is","on");
                    editor.commit();
                    Log.i("Valor",""+mode);
                }
            }
        });

    }
}