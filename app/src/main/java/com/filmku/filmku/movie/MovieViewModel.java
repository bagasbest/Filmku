package com.filmku.filmku.movie;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class MovieViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<MovieModel>> listMovie = new MutableLiveData<>();
    final ArrayList<MovieModel> movieModelArrayList = new ArrayList<>();

    private static final String TAG = MovieViewModel.class.getSimpleName();

    public void setAllMovie() {
        movieModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("movies")
                    .orderBy("title")
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MovieModel model = new MovieModel();
                                model.setDp(""+document.get("dp"));
                                model.setGenre(""+document.get("genre"));
                                model.setReleaseDate(""+document.get("releaseDate"));
                                model.setSynopsis(""+document.get("synopsis"));
                                model.setTitle(""+document.get("title"));
                                model.setTitleTemp(""+document.get("titleTemp"));
                                model.setUid(""+document.get("uid"));
                                model.setRating(""+document.get("rating"));

                                movieModelArrayList.add(model);
                            }
                            listMovie.postValue(movieModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void setMovieByQuery(String query) {
        movieModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("movies")
                    .whereGreaterThanOrEqualTo("titleTemp", query)
                    .whereLessThanOrEqualTo("titleTemp", query + '\uf8ff')
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                MovieModel model = new MovieModel();
                                model.setDp(""+document.get("dp"));
                                model.setGenre(""+document.get("genre"));
                                model.setReleaseDate(""+document.get("releaseDate"));
                                model.setSynopsis(""+document.get("synopsis"));
                                model.setTitle(""+document.get("title"));
                                model.setTitleTemp(""+document.get("titleTemp"));
                                model.setUid(""+document.get("uid"));
                                model.setRating(""+document.get("rating"));

                                movieModelArrayList.add(model);
                            }
                            listMovie.postValue(movieModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }


    public void setListMovieLimit() {
        movieModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("movies")
                    .orderBy("rating", Query.Direction.DESCENDING)
                    .limit(5)
                    .get()
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                MovieModel model = new MovieModel();
                                model.setDp(""+document.get("dp"));
                                model.setGenre(""+document.get("genre"));
                                model.setReleaseDate(""+document.get("releaseDate"));
                                model.setSynopsis(""+document.get("synopsis"));
                                model.setTitle(""+document.get("title"));
                                model.setTitleTemp(""+document.get("titleTemp"));
                                model.setUid(""+document.get("uid"));
                                model.setRating(""+document.get("rating"));

                                movieModelArrayList.add(model);
                            }
                            listMovie.postValue(movieModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public LiveData<ArrayList<MovieModel>> getMovieList() {
        return listMovie;
    }

}
