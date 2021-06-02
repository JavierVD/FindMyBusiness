package mx.ita.findmybusiness;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class contactoActivity<Share> extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);


        ImageView btnFace = findViewById(R.id.btnFace);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String facebookId = "fb://page/104912231810037"; //*Los signos < y > no van!
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookId)));
            }


        });


        ImageView btnGoogle = findViewById(R.id.btnGoogle);
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "FindMyBusiness@gmail.com"));
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Escriba aqui el asunto ");
                    intent.putExtra(Intent.EXTRA_TEXT, "ahora el texto");
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(contactoActivity.this, "Sorry...You don't have any mail app", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

        });

        ImageView btnInsta = findViewById(R.id.btnInsta);
        btnInsta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://instagram.com/_u/rubii.resendiz");
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/xxx")));
                }
            }
        });
        ImageView btnum1 = findViewById(R.id.btnum1);
        btnum1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:4494596585"));
                startActivity(intent);




            }
        });
        ImageView btnnum2 = findViewById(R.id.btnnum2);
        btnnum2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:4492158116"));
                startActivity(intent);




            }
        });



    }}
