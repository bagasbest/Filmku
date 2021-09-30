package com.filmku.filmku.movie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.filmku.filmku.databinding.ActivityMovieBinding;

public class MovieActivity extends AppCompatActivity {

    private ActivityMovieBinding binding;
    private MovieAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        initRecylerView();
        initViewModel("all");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        realtimeSearch();

    }

    private void realtimeSearch() {
        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!editable.toString().isEmpty()) {
                    initRecylerView();
                    initViewModel(editable.toString());
                } else {
                    initRecylerView();
                    initViewModel("all");
                }
            }
        });
    }

    private void initRecylerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MovieAdapter();
        binding.recyclerView.setAdapter(adapter);
    }

    private void initViewModel(String query) {
        MovieViewModel viewModel = new ViewModelProvider(this).get(MovieViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);
        if(query.equals("all")) {
            viewModel.setAllMovie();
        } else {
            viewModel.setMovieByQuery(query);
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}