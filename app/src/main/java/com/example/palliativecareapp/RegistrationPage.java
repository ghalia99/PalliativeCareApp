package com.example.palliativecareapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.palliativecareapp.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class RegistrationPage extends AppCompatActivity {

    private EditText etFirstName, etMiddleName, etLastName, etAddress, etEmail,
            etMobileNo, etPassword, etConfirmPassword;
    private DatePicker dpBirthDate;
    private Button btnBirthDate;
    private Button btnRegister, btnLogin, btnResetPassword;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;
    private FirebaseAuth mAuth;
    private RadioGroup rgRole;

    private boolean isDatePickerVisible = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        etFirstName = findViewById(R.id.et_first_name);
        etMiddleName = findViewById(R.id.et_middle_name);
        etLastName = findViewById(R.id.et_last_name);
        dpBirthDate = findViewById(R.id.dp_birth_date);
        btnBirthDate = findViewById(R.id.btn_birth_date);
        etAddress = findViewById(R.id.et_address);
        etEmail = findViewById(R.id.et_email);
        etMobileNo = findViewById(R.id.et_mobile_no);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin = findViewById(R.id.btn_login);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        progressBar = findViewById(R.id.progress_bar);
        rgRole = findViewById(R.id.rg_role);

        dpBirthDate.setVisibility(View.GONE);


        dpBirthDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // If the DatePicker loses focus, hide it
                if (!hasFocus) {
                    dpBirthDate.setVisibility(View.GONE);
                }
            }
        });

        btnBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dpBirthDate.getVisibility() == View.GONE) {
                    dpBirthDate.setVisibility(View.VISIBLE);
                } else {
                    dpBirthDate.setVisibility(View.GONE);
                }
                dpBirthDate.requestFocus();

            }

        });



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString().trim();
                String middleName = etMiddleName.getText().toString().trim();
                String lastName = etLastName.getText().toString().trim();
                String address = etAddress.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String mobileNo = etMobileNo.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                int roleId = rgRole.getCheckedRadioButtonId();

                // Validate input fields
                if (TextUtils.isEmpty(firstName)) {
                    etFirstName.setError("First name is required");
                    etFirstName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {
                    etLastName.setError("Last name is required");
                    etLastName.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    etEmail.setError("Email is required");
                    etEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    etPassword.setError("Password must be at least 6 characters");
                    etPassword.requestFocus();
                    return;
                }

                if (!confirmPassword.equals(password)) {
                    etConfirmPassword.setError("Passwords do not match");
                    etConfirmPassword.requestFocus();
                    return;
                }

                // Get selected birth date
                int day = dpBirthDate.getDayOfMonth();
                int month = dpBirthDate.getMonth();
                int year = dpBirthDate.getYear();

                // Validate birth date
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                long selectedDateInMillis = calendar.getTimeInMillis();
                long currentDateInMillis = Calendar.getInstance().getTimeInMillis();
                long eighteenYearsInMillis = 18L * 365L * 24L *
                        60L * 60L * 1000L; // 18 years in milliseconds

                if (selectedDateInMillis > currentDateInMillis - eighteenYearsInMillis) {
                    Toast.makeText(RegistrationPage.this, "You must be 18 years old to register",
                            Toast.LENGTH_SHORT).show();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);

                String dateOfBirth = String.valueOf(currentDateInMillis);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("firstName", firstName);
                editor.putString("middleName", middleName);
                editor.putString("lastName", lastName);
                editor.putString("address", address);
                editor.putString("email", email);
                editor.putString("mobileNo", mobileNo);
                editor.putString("password", password);
                editor.putString("dateOfBirth",dateOfBirth);


                //   To retrieve the data later, you can use the SharedPreferences object again:
                //SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                // String name = sharedPreferences.getString("name", "");
                //String email = sharedPreferences.getString("email", "");
                //String password = sharedPreferences.getString("password", "");

                // Register user with email and password
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    sendEmailVerification(user);
                                } else {
                                    Toast.makeText(RegistrationPage.this, "Registration failed: " +
                                            task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                if (roleId == R.id.rb_patient) {
                    editor.putString("user", "patient");

                    Intent intent = new Intent(RegistrationPage.this, PatientActivity.class);
                    startActivity(intent);
                } else if (roleId == R.id.rb_doctor) {
                    editor.putString("user", "doctor");

                    Intent intent = new Intent(RegistrationPage.this, DoctorActivity.class);
                    startActivity(intent);
                }
                editor.apply();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistrationPage.this, LoginActivity.class));
            }
        });


    }

    private void sendEmailVerification(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegistrationPage.this, "Registration successful. " +
                                            "Please check your email for verification instructions",
                                    Toast.LENGTH_LONG).show();
                            mAuth.signOut();
                        } else {
                            Toast.makeText(RegistrationPage.this, "Failed to send verification email. " +
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

}
