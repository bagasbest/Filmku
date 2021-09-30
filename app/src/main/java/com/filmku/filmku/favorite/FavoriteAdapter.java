package com.filmku.filmku.favorite;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.filmku.filmku.R;
import com.filmku.filmku.movie.MovieDetailActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {


    private final ArrayList<FavoriteModel> listFavorite = new ArrayList<>();

    private final String listOrLimit;
    public FavoriteAdapter(String listOrLimit) {
        this.listOrLimit = listOrLimit;
    }

    public void setData(ArrayList<FavoriteModel> items) {
        listFavorite.clear();
        listFavorite.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view;
        if(listOrLimit.equals("limit")) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_list, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listFavorite.get(position), listOrLimit);
    }

    @Override
    public int getItemCount() {
        return listFavorite.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        ImageView dp, dp2;
        TextView title, genre, rating;


        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.roundedImageView);
            dp2 = itemView.findViewById(R.id.dp);
            title = itemView.findViewById(R.id.title);
            genre = itemView.findViewById(R.id.genre);
            rating = itemView.findViewById(R.id.rating);
        }

        public void bind(FavoriteModel model, String listOrLimit) {

            if(listOrLimit.equals("limit")) {
                Glide.with(itemView.getContext())
                        .load(model.getData().getDp())
                        .into(dp);

                title.setText(model.getData().getTitle());
                genre.setText(model.getData().getGenre());
            } else {
                Glide.with(itemView.getContext())
                        .load(model.getData().getDp())
                        .into(dp2);

                title.setText(model.getData().getTitle());
                genre.setText(model.getData().getGenre());
                rating.setText(model.getData().getRating() + " Penilaian");
            }


            cv.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, model.getData());
                itemView.getContext().startActivity(intent);
            });

        }
    }
}
