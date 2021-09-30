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
import com.filmku.filmku.HomepageActivity;
import com.filmku.filmku.R;
import com.filmku.filmku.databinding.ActivityMovieEditBinding;
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

public class MovieEditActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "movie";
    private ActivityMovieEditBinding binding;
    private MovieModel model;
    private String dp;
    private String dateTime;
    private static final int REQUEST_FROM_GALLERY = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_MOVIE);
        binding.titleEt.setText(model.getTitle());
        binding.genreEt.setText(model.getGenre());
        binding.synopsisEt.setText(model.getSynopsis());
        binding.releaseDate.setText(model.getReleaseDate());
        Glide.with(this)
                .load(model.getDp())
                .into(binding.dp);


        binding.uploadMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataMovie();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // KLIK TAMBAH GAMBAR
        binding.imageHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MovieEditActivity.this)
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

    private void getDataMovie() {
        String title = binding.titleEt.getText().toString().trim();
        String genre = binding.genreEt.getText().toString().trim();
        String synopsis = binding.synopsisEt.getText().toString();

        if(title.isEmpty()) {
            Toast.makeText(MovieEditActivity.this, "Judul Film tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (genre.isEmpty()) {
            Toast.makeText(MovieEditActivity.this, "Genre Film tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        } else if (synopsis.isEmpty()) {
            Toast.makeText(MovieEditActivity.this, "Sinopsis Film tidak boleh kosong!", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        // SIMPAN DATA PERALATAN KAMERA KE DATABASE
        Map<String, Object> movie = new HashMap<>();
        movie.put("title", title);
        movie.put("genre", genre);
        movie.put("synopsis", synopsis);
        if(dp != null) {
            movie.put("dp", dp);
        }
        if(dateTime != null) {
            movie.put("releaseDate", dateTime);
        }
        movie.put("titleTemp", title.toLowerCase());

        FirebaseFirestore
                .getInstance()
                .collection("movies")
                .document(model.getUid())
                .update(movie)
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
                .setTitle("Gagal Memperbarui Film")
                .setMessage("Terdapat kesalahan ketika mengunggah Film, silahkan periksa koneksi internet anda, dan coba lagi nanti")
                .setIcon(R.drawable.ic_baseline_clear_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                })
                .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Berhasil Memperbarui Film")
                .setMessage("Film akan segera terbit, anda dapat mengedit atau menghapus Film jika terdapat kesalahan")
                .setIcon(R.drawable.ic_baseline_check_circle_outline_24)
                .setPositiveButton("OKE", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    Intent intent = new Intent(MovieEditActivity.this, HomepageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
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
                                            .into(binding.dp);
                                })
                                .addOnFailureListener(e -> {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(MovieEditActivity.this, "Gagal memperbarui gambar", Toast.LENGTH_SHORT).show();
                                    Log.d("imageDp: ", e.toString());
                                }))
                .addOnFailureListener(e -> {
                    mProgressDialog.dismiss();
                    Toast.makeText(MovieEditActivity.this, "Gagal memperbarui gambar", Toast.LENGTH_SHORT).show();
                    Log.d("imageDp: ", e.toString());
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}