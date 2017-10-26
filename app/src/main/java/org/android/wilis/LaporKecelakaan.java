package org.android.wilis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Random;


/**
 * Created by Rian on 06/10/2017.
 */
public class LaporKecelakaan extends Activity  {

    String id_user, username, email;
    SharedPreferences sharedpreferences;

    public static final String TAG_ID = "id_user";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_EMAIL = "email";
    // end of session

    LocationManager lm;
    LocationListener locationListener;

    // object class LaporKecelakaanAction
    LaporKecelakaanAction laporKecelakaan = new LaporKecelakaanAction();

    // Variable
    EditText alamat, foto, detail;
    Button submit, clear, btnCamera;
    TextView tvlat, tvlng;

    String lat, lng;

    // initiate capt cam
    private ImageView ivImage;
    private ConnectionDetector cd;
    private Boolean upflag = false;
    private Uri selectedImage = null;
    private Bitmap bitmap, bitmapRotate;
    private ProgressDialog pDialog;
    String imagepath = "";
    String fname;
    File file;

    // class for get current location
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if (loc != null){
                lat = String.valueOf(loc.getLatitude());
                tvlat.setText(lat);

                lng = String.valueOf(loc.getLongitude());
                tvlng.setText(lng);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            String statusString = "";
            switch (status){
                case LocationProvider.AVAILABLE:
                    statusString = "available";
                case LocationProvider.OUT_OF_SERVICE:
                    statusString = "out of service";
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    statusString = "temporarily unavailable";
            }

            Toast.makeText(getBaseContext(),
                    provider+ " " +statusString,
                    Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getBaseContext(),
                    "Provider: " +provider+ " enabled",
                    Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getBaseContext(),
                    "Provider: " +provider+ " disabled",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //-use the LocationManager class to obtain location data
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener();

        //        Object initialization
        cd = new ConnectionDetector(LaporKecelakaan.this);

        cd = new ConnectionDetector(getApplicationContext());
        showLayout();

    }

    @Override
    protected void onResume(){
        super.onResume();

        //-request for location update using GPS
        lm.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                //LocationManager.GPS_PROVIDER,
                0,
                0,
                locationListener);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        // session
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        id_user = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);
        email = getIntent().getStringExtra(TAG_EMAIL);

        Intent intent = new Intent(LaporKecelakaan.this, MainActivity.class);
        intent.putExtra(TAG_ID, id_user);
        intent.putExtra(TAG_USERNAME, username);
        intent.putExtra(TAG_EMAIL, email);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //--remove the location listener--
        lm.removeUpdates(locationListener);
    }

    private void showLayout() {
        setContentView(R.layout.layout);



        // tambahan agar bisa insert data ke server
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // end of tambahan

        alamat = (EditText) findViewById(R.id.etalamat);
        detail = (EditText) findViewById(R.id.etdetail);

        submit = (Button) findViewById(R.id.btsubmit);
        clear = (Button) findViewById(R.id.btclear);

        tvlat = (TextView) findViewById(R.id.tvlat);
        tvlng = (TextView) findViewById(R.id.tvlng);

        ivImage = (ImageView) findViewById(R.id.ivImage);

        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraintent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraintent, 101);
            }
        });

        submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
              tambahKecelaakaan();
            }
        });

        clear.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                showLayout();
            }
        });

    }


    private void tambahKecelaakaan() {

        // get datetime
        Date currentTime = Calendar.getInstance().getTime();
        // convert to dateTime format
        SimpleDateFormat date24Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //
        if (cd.isConnectingToInternet()) {
            if (!upflag) {
                Toast.makeText(getApplicationContext(), "Image Not Captured..!", Toast.LENGTH_LONG).show();
            } else {
                saveFile(bitmapRotate, file);
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection !", Toast.LENGTH_LONG).show();
        }

        // session
        sharedpreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);

        id_user = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);

        String etalamat = alamat.getText().toString();
        //String etfoto = foto.getText().toString();
        String etfoto = fname;
        String etdetail = detail.getText().toString();

        String waktupelaporan  = date24Format.format(currentTime);

        System.out.println("ID User : " +id_user+ " Alamat : " +etalamat+ " Foto : " +etfoto+ " Tanggal Waktu : " +waktupelaporan+ " Detail : " +etdetail+ " Lat : " +lat+ " Lng : " +lng);

        // pengecekkan form tidak boleh kosong
        if ((etalamat).equals("") || (etfoto).equals("") || (etdetail).equals("") || (lat).equals("") || (lng).equals("")){

            Toast.makeText(LaporKecelakaan.this, "Data kecelakaan tidak boleh kosong", Toast.LENGTH_SHORT).show();
        } else {

            String laporan = laporKecelakaan.insertKecelakaan(id_user, etalamat, etfoto, waktupelaporan, etdetail, lat, lng);
            Toast.makeText(LaporKecelakaan.this, laporan, Toast.LENGTH_SHORT).show();

            finish();
            startActivity(getIntent());
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            switch (requestCode) {
                case 101:
                    if (resultCode == Activity.RESULT_OK) {
                        if (data != null) {
                            selectedImage = data.getData(); // the uri of the image taken
                            if (String.valueOf((Bitmap) data.getExtras().get("data")).equals("null")) {
                                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                            } else {
                                bitmap = (Bitmap) data.getExtras().get("data");
                            }
                            if (Float.valueOf(getImageOrientation()) >= 0) {
                                bitmapRotate = rotateImage(bitmap, Float.valueOf(getImageOrientation()));
                            } else {
                                bitmapRotate = bitmap;
                                bitmap.recycle();
                            }

                            ivImage.setVisibility(View.VISIBLE);
                            ivImage.setImageBitmap(bitmapRotate);

//                            Saving image to mobile internal memory for sometime
                            String root = getApplicationContext().getFilesDir().toString();
                            File myDir = new File(root + "/androidlift");
                            myDir.mkdirs();

                            Random generator = new Random();
                            int n = 10000;
                            n = generator.nextInt(n);

//                            Give the file name that u want
                            fname = "laka" + n + ".png";

                            imagepath = root + "/androidlift/" + fname;
                            file = new File(myDir, fname);
                            upflag = true;
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Bitmap retVal;

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        retVal = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return retVal;
    }

    //    In some mobiles image will get rotate so to correting that this code will help us
    private int getImageOrientation() {
        final String[] imageColumns = {MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                imageColumns, null, null, imageOrderBy);

        if (cursor.moveToFirst()) {
            int orientation = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION));
            System.out.println("orientation===" + orientation);
            cursor.close();
            return orientation;
        } else {
            return 0;
        }
    }

    //    Saving file to the mobile internal memory
    private void saveFile(Bitmap sourceUri, File destination) {
        if (destination.exists()) destination.delete();
        try {
            FileOutputStream out = new FileOutputStream(destination);
            sourceUri.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            if (cd.isConnectingToInternet()) {
                new DoFileUpload().execute();
            } else {
                Toast.makeText(LaporKecelakaan.this, "No Internet Connection..", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DoFileUpload extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            pDialog = new ProgressDialog(LaporKecelakaan.this);
            pDialog.setMessage("wait uploading Image..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                // Set your file path here
                FileInputStream fstrm = new FileInputStream(imagepath);
                // Set your server page url (and the file title/description)
                HttpFileUpload hfu = new HttpFileUpload("http://192.168.1.101/ar2/file_upload.php", "ftitle", "fdescription", fname);
                upflag = hfu.Send_Now(fstrm);
            } catch (FileNotFoundException e) {
                // Error: File not found
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            if (upflag) {
                Toast.makeText(getApplicationContext(), "Uploading Complete", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Unfortunately file is not Uploaded..", Toast.LENGTH_LONG).show();
            }
        }
    }

}
