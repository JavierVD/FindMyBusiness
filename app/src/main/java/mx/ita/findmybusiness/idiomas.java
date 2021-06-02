package mx.ita.findmybusiness;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class idiomas extends AppCompatActivity {

    Button espa, ingles;
    Resources resources;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_idiomas);
        espa = findViewById(R.id.btnEspañol);
        ingles = findViewById(R.id.btnIngles);
        espa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = LocaleHelper.setLocale(idiomas.this, "es");
                resources = context.getResources();
            }
        });
        ingles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context = LocaleHelper.setLocale(idiomas.this, "en-rUS");
                resources = context.getResources();
            }
        });
    }
}