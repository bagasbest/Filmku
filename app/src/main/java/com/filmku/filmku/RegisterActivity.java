package com.filmku.filmku;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.filmku.filmku.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this)
                .load(R.drawable.logo)
                .into(binding.imageView);

        // kembali kel halaman login
        binding.goToLogin.setOnClickListener(view -> onBackPressed());

        // registrasi pengguna
        binding.registerBtn.setOnClickListener(view -> getUserInput());

    }

    private void getUserInput() {
        String username = binding.usernameEt.getText().toString().trim();
        String email = binding.emailEt.getText().toString().trim();
        String password = binding.passwordEt.getText().toString().trim();

        if(username.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Username tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if(email.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Email tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if(!email.contains("@") || !email.contains(".")) {
            Toast.makeText(RegisterActivity.this, "Format email tidak sesuai!", Toast.LENGTH_SHORT).show();
            return;
        } else if(password.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Kata sandi tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        saveBio(username, email, password);
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        showFailureDialog();
                    }
                });
    }

    private void saveBio(String username, String email, String password) {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> register = new HashMap<>();
        register.put("username", username);
        register.put("email", email);
        register.put("password", password);
        register.put("uid", uid);
        register.put("role", "user");


        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .set(register)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        binding.progressBar.setVisibility(View.GONE);
                        showSuccessDialog();
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        showFailureDialog();
                    }
                });

    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal melakukan registrasi")
                .setMessage("Silahkan mendaftar kembali dengan informasi yang benar")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil melakukan registrasi")
                .setMessage("Silahkan login")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}