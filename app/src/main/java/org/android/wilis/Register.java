package org.android.wilis;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Rian on 21/10/2017.
 */
public class Register extends AppCompatActivity {

    // initiate
    TextView sudah_terdaftar;
    EditText username, password, password2, nik, nama_lengkap, tempat_lahir, tanggal_lahir, jenis_kelamin ,alamat, pekerjaan, email, agama;
    Button submit, clear;

    // object class LaporKecelakaanAction
    LaporKecelakaanAction register = new LaporKecelakaanAction();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // tambahan agar bisa insert data ke server
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // end of tambahan

        sudah_terdaftar = (TextView) findViewById(R.id.tvsudahdaftar);

        username = (EditText) findViewById(R.id.etusername);
        password = (EditText) findViewById(R.id.etpassword);
        password2 = (EditText) findViewById(R.id.etpassword2);
        nik = (EditText) findViewById(R.id.etnik);
        nama_lengkap = (EditText) findViewById(R.id.etnamalengkap);
        tempat_lahir = (EditText) findViewById(R.id.ettempatlahir);
        tanggal_lahir = (EditText) findViewById(R.id.ettanggallahir);
        jenis_kelamin = (EditText) findViewById(R.id.etjeniskelamin);
        alamat = (EditText) findViewById(R.id.etalamat);
        pekerjaan = (EditText) findViewById(R.id.etpekerjaan);
        email = (EditText) findViewById(R.id.etemail);
        agama = (EditText) findViewById(R.id.etagama);

        submit = (Button) findViewById(R.id.btsubmit);
        clear = (Button) findViewById(R.id.btclear);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tusername = username.getText().toString();
                String tpassword = password.getText().toString();
                String tpassword2 = password2.getText().toString();
                String tnik = nik.getText().toString();
                String tnama_lengkap = nama_lengkap.getText().toString();
                String ttempat_lahir = tempat_lahir.getText().toString();
                String ttanggal_lahir = tanggal_lahir.getText().toString();
                String tjenis_kelamin = jenis_kelamin.getText().toString();
                String talamat = alamat.getText().toString();
                String tpekerjaan = pekerjaan.getText().toString();
                String temail = email.getText().toString();
                String tagama = agama.getText().toString();

                if(tpassword.equals(tpassword2) == true){
                    String laporan = register.insertUser(tusername, tpassword, tnik, tnama_lengkap, ttempat_lahir, ttanggal_lahir, tjenis_kelamin, talamat, tpekerjaan, temail, tagama);
                    Toast.makeText(Register.this, laporan, Toast.LENGTH_SHORT).show();

                    finish();
                    startActivity(getIntent());
                } else {
                    Toast.makeText(getApplicationContext(), "Password tidak cocok", Toast.LENGTH_SHORT).show();
                }


            }
        });

    }
}
