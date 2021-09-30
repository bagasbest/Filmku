package com.filmku.filmku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.filmku.filmku.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = FirebaseAuth.getInstance().getCurrentUser();

        // auto login jika sudah login sebelumnya
        autoLogin();

        Glide.with(this)
                .load(R.drawable.logo)
                .into(binding.imageView);

        // ke halaman registrasi
        binding.goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // login user
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

    }

    private void autoLogin() {
        if(user!= null) {
            startActivity(new Intent(LoginActivity.this, HomepageActivity.class));
            finish();
        }
    }

    private void loginUser() {
        String email = binding.emailEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();

        if(email.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (password.isEmpty()){
            Toast.makeText(LoginActivity.this, "Password tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Login
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth
                .getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(LoginActivity.this, HomepageActivity.class));
                            finish();
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });
    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal Login")
                .setMessage("Silahkan cek data dan koneksi internet anda dengan benar, dan lakukan login kembali")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}