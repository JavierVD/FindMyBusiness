package mx.ita.findmybusiness;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CalificarActivity extends AppCompatActivity {
    TextView tvFeedback;
    RatingBar rbStars;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calificar_producto);

        tvFeedback = findViewById(R.id.tvFeedback);
        rbStars = findViewById(R.id.rbStars);

        rbStars.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
              if(rating==1)
              {
                  tvFeedback.setText("Muy Insatisfecho");
              }
              else if(rating==1)
              {
                  tvFeedback.setText("Insatisfecho");
              }
              else if(rating==2 || rating==3)
              {
                  tvFeedback.setText("Esta bien");
              }
              else if(rating==4)
              {
                  tvFeedback.setText("Satisfecho");
              }
              else if(rating==5)
              {
                  tvFeedback.setText("Muy Satisfecho");
              }
              else
              {

              }
            }
        });
    }
}