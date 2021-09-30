package com.filmku.filmku.favorite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.filmku.filmku.R;
import com.filmku.filmku.databinding.ActivityFavoriteBinding;
import com.filmku.filmku.movie.MovieAdapter;
import com.filmku.filmku.movie.MovieViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class FavoriteActivity extends AppCompatActivity {

    private ActivityFavoriteBinding binding;
    private FavoriteAdapter favoriteAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        initFavoriteRecyclerView();
        initFavoriteViewModel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    private void initFavoriteRecyclerView() {
        binding.rvFavorite.setLayoutManager(new LinearLayoutManager(this));
        favoriteAdapter = new FavoriteAdapter("list");
        binding.rvFavorite.setAdapter(favoriteAdapter);
    }

    private void initFavoriteViewModel() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        binding.progressBar.setVisibility(View.VISIBLE);
        FavoriteViewModel viewModel = new ViewModelProvider(this).get(FavoriteViewModel.class);

        viewModel.setAllFavorite(uid);
        viewModel.getFavoriteList().observe(this, favoriteModels -> {
            if (favoriteModels.size() > 0) {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.GONE);
                favoriteAdapter.setData(favoriteModels);
            } else {
                binding.progressBar.setVisibility(View.GONE);
                binding.noData.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}