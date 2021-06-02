package mx.ita.findmybusiness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;


public class bugsActivity extends AppCompatActivity {

    EditText para, asunto, mensaje;
    Button enviar;
    String sPara, sAsunto, sMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugs);

        para = (EditText) findViewById(R.id.ePara);
        asunto = (EditText) findViewById(R.id.eAsunto);
        mensaje = (EditText) findViewById(R.id.eMensaje);
        para.setText("findmybusiness@gmail.com");
        enviar = (Button) findViewById(R.id.btnEnviar);

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sPara = para.getText().toString();
                sAsunto = asunto.getText().toString();
                sMensaje = mensaje.getText().toString();
                sPara = "findmybusiness@gmail.com";

                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{ sPara});
                email.putExtra(Intent.EXTRA_SUBJECT, sAsunto);
                email.putExtra(Intent.EXTRA_TEXT, sMensaje);

                //necesita esto para solicitar solo al cliente de correo electrónico
                email.setType("message/rfc822");

                startActivity(Intent.createChooser(email, "Elija el cliente de correo electrónico :"));
            }
        });
    }
}