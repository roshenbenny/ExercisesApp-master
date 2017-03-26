package danieluk.exercisesapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
  ListDetail- klasa zawierająca informacje szczegółowe o ćwiczeniu,
    posiada przycisk umożliwiający usuwanie danego ćwiczenia,
    informacje wyświetlane się dzięki zastosowaniu klasy Intent,
    komunikacja z klasą LisExercisesFragment

 */
public class ListDetail extends AppCompatActivity {
    String TAG="ListDetails";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_detail);

        Bundle extras = getIntent().getExtras();
        final int id=getIntent().getIntExtra("id",-1);
        String name=getIntent().getStringExtra("name");
        int series=getIntent().getIntExtra("series",-1);
        int reps=getIntent().getIntExtra("reps",-1);
        int weights=getIntent().getIntExtra("weights",-1);
        String date=getIntent().getStringExtra("date");
        String notes=getIntent().getStringExtra("notes");
        Uri myUri = (Uri)extras.get("imageUri");
        TextView txtName=(TextView) findViewById(R.id.detail_name);
        TextView txtSeries=(TextView) findViewById(R.id.detail_series);
        TextView txtReps=(TextView) findViewById(R.id.detail_reps);
        TextView txtWeights=(TextView) findViewById(R.id.detail_weights);
        TextView txtNotes=(TextView) findViewById(R.id.detail_notes);
        TextView txtDate=(TextView) findViewById(R.id.detail_date);
        ImageView imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setImageURI(myUri);



        txtName.setText(name);
        txtDate.setText(date);
        txtSeries.setText(Integer.toString(series));
        txtReps.setText(Integer.toString(reps));
        txtWeights.setText(Integer.toString(weights));
        txtNotes.setText(notes);

        Log.d(TAG,"dostalem id: "+Integer.toString(id));

        Button btnDelete=(Button) findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent();
                intent.putExtra("id",id);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {

        Intent intent=new Intent();
        setResult(RESULT_OK,intent);
        finish();

    }
}
