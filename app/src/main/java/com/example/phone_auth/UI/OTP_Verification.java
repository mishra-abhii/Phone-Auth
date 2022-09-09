package com.example.phone_auth.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phone_auth.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTP_Verification extends AppCompatActivity {

    TextView tv;
    EditText dig1, dig2, dig3, dig4, dig5, dig6;
    Button resend_otp, verify_otp;
    ProgressBar pb;

    private String mVerificationId;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        tv = findViewById(R.id.tv);
        dig1 = findViewById(R.id.dig1);
        dig2 = findViewById(R.id.dig2);
        dig3 = findViewById(R.id.dig3);
        dig4 = findViewById(R.id.dig4);
        dig5 = findViewById(R.id.dig5);
        dig6 = findViewById(R.id.dig6);

        resend_otp = findViewById(R.id.resendotp);
        verify_otp = findViewById(R.id.verifyotp);

        pb = findViewById(R.id.pb2);
        pb.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        tv.setText("+91-" + getIntent().getStringExtra("mobileno"));
        mVerificationId = getIntent().getStringExtra("verificationID");

        OTP_Input(dig1, dig2);
        OTP_Input(dig2, dig3);
        OTP_Input(dig3, dig4);
        OTP_Input(dig4, dig5);
        OTP_Input(dig5, dig6);

        //-------------------------------
        verify_otp.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            verify_otp.setVisibility(View.INVISIBLE);

            if (dig1.getText().toString().isEmpty() ||
                    dig2.getText().toString().isEmpty() ||
                    dig3.getText().toString().isEmpty() ||
                    dig4.getText().toString().isEmpty() ||
                    dig5.getText().toString().isEmpty() ||
                    dig6.getText().toString().isEmpty()) {
                Toast.makeText(this, "OTP is not Valid", Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
                verify_otp.setVisibility(View.VISIBLE);
            } else {
                if (mVerificationId != null) {
                    String code = dig1.getText().toString().trim() +
                            dig2.getText().toString().trim() +
                            dig3.getText().toString().trim() +
                            dig4.getText().toString().trim() +
                            dig5.getText().toString().trim() +
                            dig6.getText().toString().trim();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    FirebaseAuth
                            .getInstance()
                            .signInWithCredential(credential)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    pb.setVisibility(View.VISIBLE);
                                    verify_otp.setVisibility(View.INVISIBLE);

                                    SaveData(getIntent().getStringExtra("mobileno"));

                                } else {
                                    pb.setVisibility(View.GONE);
                                    verify_otp.setVisibility(View.VISIBLE);
                                    Toast.makeText(OTP_Verification.this, "OTP is not valid", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        //------------------------
        resend_otp.setOnClickListener(view -> {
            Send_OTP();
            Toast.makeText(OTP_Verification.this, "OTP re-sent Successfully", Toast.LENGTH_SHORT).show();
        });
    }

    private void Send_OTP() {
        pb.setVisibility(View.VISIBLE);
        verify_otp.setVisibility(View.INVISIBLE);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                pb.setVisibility(View.GONE);
                verify_otp.setVisibility(View.VISIBLE);
                Toast.makeText(OTP_Verification.this, "OTP successfully sent", Toast.LENGTH_SHORT).show();
                mVerificationId =verificationId;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pb.setVisibility(View.GONE);
                verify_otp.setVisibility(View.VISIBLE);
                Toast.makeText(OTP_Verification.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+getIntent().getStringExtra("mobileno"))
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void SaveData(String phone_no){
        SharedPreferences sharedPreferences=getSharedPreferences("logindata",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean("logincounter",true);
        editor.putString("pno",phone_no);
        editor.apply();

        Toast.makeText(OTP_Verification.this, "Welcome", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(OTP_Verification.this, MainActivity.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void OTP_Input(EditText et1 , EditText et2) {
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!et1.getText().toString().isEmpty())
                    et2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}