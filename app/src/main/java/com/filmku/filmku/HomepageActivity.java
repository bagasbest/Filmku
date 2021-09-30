package com.filmku.filmku;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.filmku.filmku.databinding.ActivityHomepageBinding;
import com.filmku.filmku.favorite.FavoriteActivity;
import com.filmku.filmku.favorite.FavoriteAdapter;
import com.filmku.filmku.favorite.FavoriteViewModel;
import com.filmku.filmku.movie.MovieActivity;
import com.filmku.filmku.movie.MovieAdapter;
import com.filmku.filmku.movie.MovieAddActivity;
import com.filmku.filmku.movie.MovieViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomepageActivity extends AppCompatActivity {

    private ActivityHomepageBinding binding;
    private MovieAdapter adapter;
    private FavoriteAdapter favoriteAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        initRecylerView();
        initViewModel();

        initFavoriteRecyclerView();
        initFavoriteViewModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomepageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // jika admin, maka bisa menambahkan film
        checkRole();

        binding.infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomepageActivity.this, AboutActivity.class));
            }
        });

        binding.addMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomepageActivity.this, MovieAddActivity.class));
            }
        });

        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLogoutDialog();
            }
        });

        binding.view4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomepageActivity.this, MovieActivity.class);
                startActivity(intent);
            }
        });

        binding.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomepageActivity.this, FavoriteActivity.class));
            }
        });

    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Konfirmasi Logout")
                .setMessage("Apakah anda yakin ingin keluar apliaksi ?")
                .setIcon(R.drawable.ic_baseline_exit_to_app_24)
                .setPositiveButton("YA", (dialogInterface, i) -> {
                    // sign out dari firebase autentikasi
                    FirebaseAuth.getInstance().signOut();

                    // go to login activity
                    Intent intent = new Intent(HomepageActivity.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    dialogInterface.dismiss();
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("TIDAK", (dialog, i) -> {
                    dialog.dismiss();
                })
                .show();
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
                        binding.addMovie.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void initRecylerView() {
        binding.rvMovies.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter();
        binding.rvMovies.setAdapter(adapter);
    }

    private void initViewModel() {
        MovieViewModel viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        viewModel.setListMovieLimit();
        viewModel.getMovieList().observe(this, movie -> {
            if (movie.size() > 0) {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.GONE);
                adapter.setData(movie);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initFavoriteRecyclerView() {
        binding.rvFavorite.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        favoriteAdapter = new FavoriteAdapter("limit");
        binding.rvFavorite.setAdapter(favoriteAdapter);
    }

    private void initFavoriteViewModel() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FavoriteViewModel viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        viewModel.setFavoriteLimit(uid);
        viewModel.getFavoriteList().observe(this, favoriteModels -> {
            if (favoriteModels.size() > 0) {
                binding.noDataFav.setVisibility(View.GONE);
                favoriteAdapter.setData(favoriteModels);
            } else {
                binding.noDataFav.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}