package com.example.twofileswriting;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private EditText edit_text_login, edit_text_password;
    private Button btn_log, btnReg;
    private CheckBox checkBox;
    public static final int REQUEST_CODE_PERMISSION_READ_STORAGE = 10;
    public static final int REQUEST_CODE_PERMISSION_WRITE_STORAGE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_PERMISSION_READ_STORAGE
        );
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_CODE_PERMISSION_WRITE_STORAGE
        );
        init();
        registration();
        login();

    }

    private void init() {
        btn_log = findViewById(R.id.btn_log);
        btnReg = findViewById(R.id.btn_pass);
        edit_text_login = findViewById(R.id.edit_text_login);
        edit_text_password = findViewById(R.id.edit_text_password);
        checkBox = findViewById(R.id.checkbox);
    }
    private void registration() {
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {

                    try {
                        FileOutputStream fileOutputStream = openFileOutput("loginPassword", MODE_PRIVATE);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                        BufferedWriter bw = new BufferedWriter(outputStreamWriter);
                        bw.write(edit_text_login.getText().toString());
                        bw.write(edit_text_password.getText().toString());
                        bw.close();
                        Toast.makeText(getApplicationContext(), "Вы зарегистрировались", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_PERMISSION_WRITE_STORAGE);
                    int permissionStatus = ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {


                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                                "loginPassword");
                        try {
                            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                            bw.write(edit_text_login.getText().toString());
                            bw.write(edit_text_password.getText().toString());
                            bw.close();
                            Toast.makeText(getApplicationContext(), "Вы зарегистрировались", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSION_WRITE_STORAGE);
                    }
                }
            }
        });
    }

    private void login() {
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    try {
                        FileInputStream fileInputStream = openFileInput("loginPassword");
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                        BufferedReader login = new BufferedReader(inputStreamReader);
                        String log = login.readLine();
                        String pass = login.readLine();
                        login.close();
                        if (log.equals(edit_text_login.getText().toString()) & pass.equals(edit_text_password.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "Добро пожаловать " + edit_text_login.getText().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Вы ввели неверные данные", Toast.LENGTH_SHORT).show();
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    int permissionStatus = ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {

                        try {
                            LoadFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_CODE_PERMISSION_READ_STORAGE);
                    }
                }
            }
        });
    }




    private void LoadFile() throws IOException {


        if (isExternalStorageWritable()) {

            File logFile = new File(getApplicationContext().getExternalFilesDir(null), "log.txt");
            try {
                FileWriter logWriter = new FileWriter(logFile);
                logWriter.append("App loaded");
                logWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "loginPassword");

            BufferedReader login = new BufferedReader(new FileReader(file));
            String log = login.readLine();
            String pass = login.readLine();
            login.close();
            if (log.equals(edit_text_login.getText().toString()) & pass.equals(edit_text_password.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Добро пожаловать " + edit_text_login.getText().toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Вы ввели неверные данные", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "File Error", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isExternalStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }
}
