package com.filmku.filmku.movie;
import android.annotation.SuppressLint;
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
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final ArrayList<MovieModel> listMovie = new ArrayList<>();



    public void setData(ArrayList<MovieModel> items) {
        listMovie.clear();
        listMovie.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movies, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        holder.bind(listMovie.get(position));
    }

    @Override
    public int getItemCount() {
        return listMovie.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout cv;
        ImageView dp;
        TextView title, genre, rating;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            dp = itemView.findViewById(R.id.dp);
            title = itemView.findViewById(R.id.title);
            genre = itemView.findViewById(R.id.genre);
            rating = itemView.findViewById(R.id.rating);
        }

        @SuppressLint("SetTextI18n")
        public void bind(MovieModel model) {
            Glide.with(itemView.getContext())
            .load(model.getDp())
            .into(dp);

            title.setText(model.getTitle());
            genre.setText(model.getGenre());
            rating.setText(model.getRating() + " Penilaian");

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), MovieDetailActivity.class);
                    intent.putExtra(MovieDetailActivity.EXTRA_MOVIE, model);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
