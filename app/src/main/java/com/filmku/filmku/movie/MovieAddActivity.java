package com.filmku.filmku.movie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.filmku.filmku.R;
import com.filmku.filmku.databinding.ActivityMovieAddBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MovieAddActivity extends AppCompatActivity {

    private ActivityMovieAddBinding binding;
    private String dp;
    private String dateTime;
    private static final int REQUEST_FROM_GALLERY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.uploadMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadMovie();
            }
        });

        // KLIK TAMBAH GAMBAR
        binding.imageHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MovieAddActivity.this)
                        .galleryOnly()
                        .compress(1024)
                        .start(REQUEST_FROM_GALLERY);
            }
        });

        binding.releaseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                releaseDate();
            }
        });

    }

    private void releaseDate() {
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker().build();
        datePicker.show(getSupportFragmentManager(), datePicker.toString());
        datePicker.addOnPositiveButtonClickListener(selection -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            dateTime = sdf.format(new Date(Long.parseLong(selection.toString())));
            binding.releaseDate.setText(dateTime);
        });
    }

    private void uploadMovie() {
        String title = binding.titleEt.getText().toString().trim();
        String genre = binding.genreEt.getText().toString().trim();
        String synopsis = binding.synopsisEt.getText().toString();

        if(title.isEmpty()) {
            Toast.makeText(MovieAddActivity.this, "Judul Film tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (genre.isEmpty()) {
            Toast.makeText(MovieAddActivity.this, "Genre Film tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (synopsis.isEmpty()) {
            Toast.makeText(MovieAddActivity.this, "Sinopsis Film tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (dp == null) {
            Toast.makeText(MovieAddActivity.this, "Gambar Film tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (dateTime == null) {
            Toast.makeText(MovieAddActivity.this, "Tanggal Release Film tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        String uid = String.valueOf(System.currentTimeMillis());

        // SIMPAN DATA PERALATAN KAMERA KE DATABASE
        Map<String, Object> movie = new HashMap<>();
        movie.put("title", title);
        movie.put("genre", genre);
        movie.put("synopsis", synopsis);
        movie.put("dp", dp);
        movie.put("releaseDate", dateTime);
        movie.put("uid", uid);
        movie.put("rating", "0,0 | 0");
        movie.put("titleTemp", title.toLowerCase());

        FirebaseFirestore
                .getInstance()
                .collection("movies")
                .document(uid)
                .set(movie)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            binding.progressBar.setVisibility(View.GONE);
                            showSuccessDialog();
                        } else {
                            binding.progressBar.setVisibility(View.GONE);
                            showFailureDialog();
                        }
                    }
                });
    }

    private void showFailureDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal Mengunggah Film")
                .setMessage("Terdapat kesalahan ketika mengunggah Film, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Mengunggah Film")
                .setMessage("Film akan segera terbit, anda dapat mengedit atau menghapus Film jika terdapat kesalahan")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    onBackPressed();
                })
                .show();
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_FROM_GALLERY) {
                uploadMovieDp(data.getData());
            }
        }
    }

    private void uploadMovieDp(Uri data) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
        String imageFileName = "movie/data_" + System.currentTimeMillis() + ".png";

        mStorageRef.child(imageFileName).putFile(data)
                .addOnSuccessListener(taskSnapshot ->
                        mStorageRef.child(imageFileName).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    mProgressDialog.dismiss();
                                    dp = uri.toString();
                                    binding.imageHint.setVisibility(View.GONE);
                                    Glide
                                            .with(this)
                                            .load(dp)
                                            .into(binding.ArticleDp);
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(MovieAddActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(MovieAddActivity.this, "Gagal mengunggah gambar", Toast.LENGTH_SHORT).show();
                    Log.d("imageDp: ", e.toString());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}