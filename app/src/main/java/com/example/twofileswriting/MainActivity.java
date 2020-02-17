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
    EditText editTextLogin, editTextPassword;
    Button btnLog, btnReg;
    CheckBox checkBox;
    public static final int REQUEST_CODE_PERMISSION_READ_STORAGE = 10;
    public static final int REQUEST_CODE_PERMISSION_WRITE_STORAGE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int permissionStatus = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        init();
        registration();
        login();



    }

    private void init() {
        btnLog = findViewById(R.id.btnLog);
        btnReg = findViewById(R.id.btnPass);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBox = findViewById(R.id.checkbox);
    }

    private void registration() {
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextLogin.getText().toString().equals("") & !editTextPassword.getText().toString().equals("") & !checkBox.isChecked()) {

                    try {
                        FileOutputStream fileOutputStream = openFileOutput("LoginPassword", MODE_PRIVATE);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                        BufferedWriter bw = new BufferedWriter(outputStreamWriter);
                        bw.write(editTextLogin.getText().toString());
                        bw.write(editTextPassword.getText().toString());
                        bw.close();
                        Toast.makeText(getApplicationContext(), "Вы зарегистрировались", Toast.LENGTH_SHORT).show();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (!editTextLogin.getText().toString().equals("") & !editTextPassword.getText().toString().equals("") & checkBox.isChecked()) {
                    if (isExternalStorageWritable()) {
                        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                                "LoginPassword");
                        try {
                            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
                            bw.write(editTextLogin.getText().toString());
                            bw.write(editTextPassword.getText().toString());
                            bw.close();
                            Toast.makeText(getApplicationContext(), "Вы зарегистрировались", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "File Error", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void login() {
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    try {
                        FileInputStream fileInputStream = openFileInput("LoginPassword");
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                        BufferedReader login = new BufferedReader(inputStreamReader);
                        String log = login.readLine();
                        String pass = login.readLine();
                        login.close();
                        if (log.equals(editTextLogin.getText().toString()) & pass.equals(editTextPassword.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "Добро пожаловать " + editTextLogin.getText().toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_READ_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        LoadFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
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
                    "LoginPassword");

            BufferedReader login = new BufferedReader(new FileReader(file));
            String log = login.readLine();
            String pass = login.readLine();
            login.close();
            if (log.equals(editTextLogin.getText().toString()) & pass.equals(editTextPassword.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Добро пожаловать " + editTextLogin.getText().toString(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Вы ввели неверные данные", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "File Error", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
