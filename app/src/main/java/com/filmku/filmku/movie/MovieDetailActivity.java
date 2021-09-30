package com.filmku.filmku.movie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.filmku.filmku.HomepageActivity;
import com.filmku.filmku.LoginActivity;
import com.filmku.filmku.R;
import com.filmku.filmku.databinding.ActivityMovieDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;

public class MovieDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MOVIE = "movie";
    public static final String EXTRA_ROLE = "role";
    private ActivityMovieDetailBinding binding;
    private MovieModel model;
    private boolean isFavorite = false;

    int total = 0;
    double totalRating = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = getIntent().getParcelableExtra(EXTRA_MOVIE);
        Glide.with(this)
                .load(model.getDp())
                .into(binding.roundedImageView2);

        binding.textView7.setText(model.getTitle());
        binding.textView8.setText(model.getRating());
        binding.releaseDate.setText(model.getReleaseDate());
        binding.genre.setText(model.getGenre());
        binding.synopsis.setText(model.getSynopsis());

        checkRole();

        checkFavoriteMovie();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveRating();
            }
        });

        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFavorite) {
                    setUnfavorite();
                } else {
                    setFavorite();
                }
            }
        });

        // delete film
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDeleteDialog();
            }
        });

        // edit film
        binding.floatingActionButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MovieDetailActivity.this, MovieEditActivity.class);
                intent.putExtra(MovieEditActivity.EXTRA_MOVIE, model);
                startActivity(intent);
            }
        });
    }

    private void checkRole() {
        String uid = FirebaseAuth
                .getInstance()
                .getCurrentUser()
                .getUid();

        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(("" + documentSnapshot.get("role")).equals("admin")) {
                        binding.floatingActionButton.setVisibility(View.VISIBLE);
                        binding.floatingActionButton2.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void showConfirmDeleteDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Menghapus Film")
                .setMessage("Apakah anda yakin ingin menghapus film ini ?")
                .setIcon(R.drawable.ic_baseline_warning_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    deleteFilm();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteFilm() {
        FirebaseFirestore
                .getInstance()
                .collection("movies")
                .document(model.getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MovieDetailActivity.this, "Berhasil menghpus film", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            Toast.makeText(MovieDetailActivity.this, "Gagal menghpus film", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setUnfavorite() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore
                .getInstance()
                .collection("favorite")
                .document(uid + model.getUid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isFavorite = false;
                            binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                            Toast.makeText(MovieDetailActivity.this, "Berhasil menghapus film ini dari daftar favorit", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MovieDetailActivity.this, "Gagal menghapus film ini dari daftar favorit", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkFavoriteMovie() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore
                .getInstance()
                .collection("favorite")
                .document(uid + model.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            isFavorite = true;
                            binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                        } else {
                            isFavorite = false;
                            binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                        }
                    }
                });
    }

    private void setFavorite() {
        saveMovieToFavorite();
    }

    private void saveMovieToFavorite() {
        ProgressDialog mProgressDialog = new ProgressDialog(this);

        mProgressDialog.setMessage("Mohon tunggu hingga proses selesai...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> favorite = new HashMap<>();
        favorite.put("favoriteId", uid + model.getUid());
        favorite.put("uid", uid);
        favorite.put("data", model);

        FirebaseFirestore
                .getInstance()
                .collection("favorite")
                .document(uid + model.getUid())
                .set(favorite)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            isFavorite = true;
                            mProgressDialog.dismiss();
                            binding.favorite.setImageResource(R.drawable.ic_baseline_favorite_24);
                            Toast.makeText(MovieDetailActivity.this, "Berhasil menambahkan film ini ke daftar favorit", Toast.LENGTH_SHORT).show();
                        } else {
                            mProgressDialog.dismiss();
                            Toast.makeText(MovieDetailActivity.this, "Gagal menambahkan film ini ke daftar favorit", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void giveRating() {
        Dialog dialog;
        Button btnSubmitRating, btnDismiss;
        RatingBar ratingBar;
        ProgressBar pb;

        dialog = new Dialog(this);

        dialog.setContentView(R.layout.popup_rating);
        dialog.setCanceledOnTouchOutside(false);


        btnSubmitRating = dialog.findViewById(R.id.submitRating);
        btnDismiss = dialog.findViewById(R.id.dismissBtn);
        ratingBar = dialog.findViewById(R.id.ratingBar);
        pb = dialog.findViewById(R.id.progress_bar);

        btnSubmitRating.setOnClickListener(view -> {
            if(ratingBar.getRating() != 0) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                saveRatingInDatabase(ratingBar.getRating(), pb, uid, dialog);
            } else {
                Toast.makeText(MovieDetailActivity.this, "Minimal 1 Bintang", Toast.LENGTH_SHORT).show();
            }
        });

        btnDismiss.setOnClickListener(view -> dialog.dismiss());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void saveRatingInDatabase(float rating, ProgressBar pb, String uid, Dialog dialog) {

        Map<String, Object> rate = new HashMap<>();
        rate.put("uid", uid);
        rate.put("rating", rating);


        // simpan rating pengguna ke database
        pb.setVisibility(View.VISIBLE);
        FirebaseFirestore
                .getInstance()
                .collection("movies")
                .document(model.getUid())
                .collection("rating")
                .document(uid)
                .set(rate)
                .addOnSuccessListener(unused -> {
                    //todo
                    calculateRating(pb, dialog);
                })
                .addOnFailureListener(e -> {
                    pb.setVisibility(View.GONE);
                    dialog.dismiss();
                    Toast.makeText(MovieDetailActivity.this, "Gagal memberi penilaian", Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("SetTextI18n")
    private void calculateRating(ProgressBar pb, Dialog dialog) {

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("movies")
                    .document(model.getUid())
                    .collection("rating")
                    .get()
                    .addOnCompleteListener(task -> {
                        total = task.getResult().size();

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {


                                // ambil rating jika ada flilm
                                if (document.exists()) {

                                    double rating = Double.parseDouble("" + document.get("rating"));
                                    totalRating = totalRating + rating;
                                }
                            }

                            String rating = String.format("%.1f", (double) totalRating / total ) + " | " + total;
                            binding.textView8.setText(rating);

                            total = 0;
                            totalRating = 0.0;
                            saveRatingToFieldDatabase(rating, pb, dialog);


                        } else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                            pb.setVisibility(View.GONE);
                            dialog.dismiss();
                        }
                    });
        } catch (Exception e) {
            pb.setVisibility(View.GONE);
            dialog.dismiss();
            e.printStackTrace();
        }
    }

    private void saveRatingToFieldDatabase(String rating, ProgressBar pb, Dialog dialog) {

        FirebaseFirestore
                .getInstance()
                .collection("movies")
                .document(model.getUid())
                .update("rating", rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            pb.setVisibility(View.GONE);
                            dialog.dismiss();
                            Toast.makeText(MovieDetailActivity.this, "Berhasil memberi penilaian", Toast.LENGTH_SHORT).show();
                        } else {
                            pb.setVisibility(View.GONE);
                            dialog.dismiss();
                            Toast.makeText(MovieDetailActivity.this, "Gagal memberi penilaian", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}