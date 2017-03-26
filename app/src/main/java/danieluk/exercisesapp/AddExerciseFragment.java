package danieluk.exercisesapp;



import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * AddExerciseFragment - fragment odpowiedzialny za dodawanie ćwiczeń
 *
 *
 */

public class AddExerciseFragment extends Fragment {
    private static final String LOG_TAG = AddExerciseFragment.class.getSimpleName();

    private ExercisesDbAdapter dbHelper;
    private TextInputLayout inputLayoutName,inputLayoutSeries,inputLayoutReps,inputLayoutWeights;
    private EditText inputName,inputSeries,inputReps,inputWeights,inputNotes,inputDate;
    private Button btnEnter,upload;
    private ImageView mImageView;
    private static final int PICK_IMAGE_REQUEST = 0;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    private Uri imageUri;
    private ContentResolver contentResolver;


    public AddExerciseFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_exercise_fragment, container, false);

        inputLayoutName=(TextInputLayout) view.findViewById(R.id.input_layout_name);
        inputLayoutSeries=(TextInputLayout) view.findViewById(R.id.input_layout_series);
        inputLayoutReps=(TextInputLayout) view.findViewById(R.id.input_layout_reps);
        inputLayoutWeights=(TextInputLayout) view.findViewById(R.id.input_layout_weights);
        inputName=(EditText) view.findViewById(R.id.input_name);
        inputSeries=(EditText) view.findViewById(R.id.input_series);
        inputReps=(EditText) view.findViewById(R.id.input_reps);
        inputWeights=(EditText) view.findViewById(R.id.input_weights);
        inputNotes=(EditText) view.findViewById(R.id.input_notes);
        inputDate=(EditText) view.findViewById(R.id.input_date);
        ImageView inputimage = (ImageView) view.findViewById(R.id.image);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateNow=sdf.format(new Date());
        inputDate.setText(dateNow);
        myCalendar = Calendar.getInstance();

        // ustawianie daty na podstawie kalendarza
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(); // ustawianie aktualnej wartości pola
            }

        };

        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnEnter=(Button) view.findViewById(R.id.btn_enter);
        upload=(Button) view.findViewById(R.id.uploadButton);
        mImageView=(ImageView) view.findViewById(R.id.image);
        final ImageView mImageView = (ImageView) view.findViewById(R.id.image);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
            }
        });
        ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mImageView.setImageBitmap(getBitmapFromUri(imageUri));
            }
        });
        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
                Intent intent = new Intent(getActivity(),ListDetail.class);
                Log.e("erro",imageUri.toString());
                intent.putExtra("imageUri", imageUri);
                startActivity(intent);
            }
        });
        return view;
    }
    // ustawienie wprowadzonej daty, na te wybrana z kalendarza
    private void updateLabel(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        inputDate.setText(sdf.format(myCalendar.getTime()));
    }

    // submitForm - sprawdzenie, czy wymagane pola są uzupełnione
    // i ewentualne dodanie do bazy danych
    private void submitForm() {
        if (!validateName()) {
            return;
        }

        if (!validateSeries()) {
            return;
        }
        if (!validateReps()) {
            return;
        }

        if (!validateWeights()) {
            return;
        }
        dbHelper = new ExercisesDbAdapter(getActivity());
        dbHelper.open();
        String inputNameStr=inputName.getText().toString();
        int inputSeriesInt= Integer.parseInt(inputSeries.getText().toString());
        int inputRepsInt=Integer.parseInt(inputReps.getText().toString());
        int inputWeightsInt=Integer.parseInt(inputWeights.getText().toString());
        String inputNotesStr=inputNotes.getText().toString();
        String inputDateStr=inputDate.getText().toString();
        String img=imageUri.toString();
        dbHelper.createExercise(inputNameStr,inputSeriesInt,inputRepsInt,inputWeightsInt,inputNotesStr,inputDateStr,img);
        dbHelper.close();

        clearInputs();
        requestFocus(inputName);
        Toast.makeText(getActivity().getApplicationContext(), "Exercise added!", Toast.LENGTH_SHORT).show();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (resultData != null) {
                imageUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + imageUri.toString());
                mImageView.setImageBitmap(getBitmapFromUri(imageUri));
                getBitmapFromUri(imageUri);
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        InputStream input = null;
        try {
            input = getActivity().getContentResolver().openInputStream(uri);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = getActivity().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(inputName);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateSeries() {
        String series = inputSeries.getText().toString().trim();

        if (series.isEmpty() ) {
            inputLayoutSeries.setError(getString(R.string.err_msg_series));
            requestFocus(inputSeries);
            return false;
        } else {
            inputLayoutSeries.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateReps() {
        String reps = inputReps.getText().toString().trim();

        if (reps.isEmpty() ) {
            inputLayoutReps.setError(getString(R.string.err_msg_reps));
            requestFocus(inputReps);
            return false;
        } else {
            inputLayoutReps.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateWeights() {
        String weights = inputWeights.getText().toString().trim();

        if (weights.isEmpty() ) {
            inputLayoutWeights.setError(getString(R.string.err_msg_weights));
            requestFocus(inputWeights);
            return false;
        } else {
            inputLayoutWeights.setErrorEnabled(false);
        }

        return true;
    }

    //wybór pola, na które focus
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    //czyszczenie pól po ich uzupełnieniu
    private void clearInputs(){
        inputName.setText("");
        inputSeries.setText("");
        inputReps.setText("");
        inputWeights.setText("");
        inputNotes.setText("");

    }


}
