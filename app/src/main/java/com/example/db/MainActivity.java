package com.example.db;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {


    EditText registNamaLengkap, registUsername, registPassword, loginUsername, loginPassword;
    TextView txtStatus, txtStatus2;
    Button btnRegister, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registNamaLengkap = findViewById(R.id.editNamaLengkap);
        registUsername = findViewById(R.id.editUsername);
        registPassword = findViewById(R.id.editPassword);

        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);

        txtStatus = findViewById(R.id.txtStatus);
        txtStatus2 = findViewById(R.id.txtStatus2);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query1 = registNamaLengkap.getText().toString();
                String query2 = registUsername.getText().toString();
                String query3 = registPassword.getText().toString();

                Uri.Builder builder = new Uri.Builder().appendQueryParameter("nama_lengkap", query1).appendQueryParameter("username", query2).appendQueryParameter("password", query3);
                new Connection("http://10.0.2.2/pbm/insert.php", builder).execute();
//                txtStatus.setText(status);
                txtStatus2.setVisibility(View.GONE);
                txtStatus.setVisibility(View.VISIBLE);
                reset();
            }

        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query1 = loginUsername.getText().toString();
                String query2 = loginPassword.getText().toString();

                new Connection("http://10.0.2.2/pbm/cekuser.php", new Uri.Builder().appendQueryParameter("username", query1).appendQueryParameter("password", query2)).execute();
                txtStatus.setVisibility(View.GONE);
                txtStatus2.setVisibility(View.VISIBLE);
                reset();
            }
        });
    }

    private void reset(){
        registNamaLengkap.setText("");
        registUsername.setText("");
        registPassword.setText("");
        loginUsername.setText("");
        loginPassword.setText("");
    };



//    Class Connection

    private class Connection extends AsyncTask<String, String, String> {
        private static final int READ_TIMEOUT = 15000;
        private static final int CONNECTION_TIMEUT = 10000;
        Context context;
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        HttpURLConnection conn;
        URL url = null;
        String urlx;
        //        String p1, p2, p3;
        Uri.Builder builder;


        public Connection(String urlx, Uri.Builder builder) {
            this.urlx = urlx;
            this.builder = builder;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pdLoading.setMessage("\tloading...");
//            pdLoading.setCancelable(false);
//            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                url = new URL(urlx);

                System.out.println("try 1");
            } catch (MalformedURLException e) {
//            throw new RuntimeException(e);
                e.printStackTrace();
                System.out.println("gagal 1");
                return e.getMessage();
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

//                Uri.Builder builder = new Uri.Builder().appendQueryParameter("nama", p1).appendQueryParameter("username", p2).appendQueryParameter("password", p3);
                String query = builder.build().getEncodedQuery();
                OutputStream outputStream = conn.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                bufferedWriter.write(query);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                conn.connect();
                System.out.println("try 2");
                System.out.println("yourLink?" + query);

            } catch (IOException e) {
                System.out.println("gagal 2");
                throw new RuntimeException(e);
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code != HttpURLConnection.HTTP_OK) {
                    System.out.println("gagal 3");
                    return ("Connection error");
                } else {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    System.out.println("try 3");
                    return (result.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            pdLoading.dismiss();
            txtStatus.setText(s.toString());
            txtStatus2.setText(s.toString());
        }
    }
}
