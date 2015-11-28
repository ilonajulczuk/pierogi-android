package ninja.justyna.pierogi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class UploadActivity extends AppCompatActivity {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    private String filePath = null;
    private ImageView imgPreview;
    private Button btnUpload;
    private EditText mEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        btnUpload = (Button) findViewById(R.id.btnUpload);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");

        // boolean flag to identify the media type, image or video
        boolean isImage = i.getBooleanExtra("isImage", true);

        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                RequestParams params = new RequestParams();

                mEdit   = (EditText)findViewById(R.id.editText);

                params.put("name", mEdit.getText().toString());
                try {
                    params.put("image", new File(filePath));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                AsyncHttpClient client = new AsyncHttpClient();
                client.addHeader("Authorization","Token 7a6749b6725072b41050b004e0ca6d126ac025e8");
                client.post("http://172.27.133.4:8000/food/", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(getApplicationContext(),
                                "uploaded successfully", Toast.LENGTH_LONG)
                                .show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        try {
                            String message = "failure at upload" + Integer.toString(statusCode) + new String(responseBody, "UTF-8");
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG)
                                    .show();
                            Log.w(TAG, message);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            // options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);
        }
    }
}