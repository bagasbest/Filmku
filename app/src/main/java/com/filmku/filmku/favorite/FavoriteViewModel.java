package com.filmku.filmku.favorite;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.filmku.filmku.movie.MovieModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class FavoriteViewModel extends ViewModel {

    private final MutableLiveData<ArrayList<FavoriteModel>> listFavorite = new MutableLiveData<>();
    final ArrayList<FavoriteModel> favoriteModelArrayList = new ArrayList<>();

    private static final String TAG = FavoriteViewModel.class.getSimpleName();

    public void setFavoriteLimit(String uid) {
        favoriteModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("favorite")
                    .whereEqualTo("uid", uid)
                    .limit(5)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                FavoriteModel model = new FavoriteModel();

                                model.setFavoriteId("" + document.get("favoriteId"));
                                model.setUid("" + document.get("uid"));
                                model.setData(document.toObject(FavoriteModel.class).data);

                                favoriteModelArrayList.add(model);
                            }
                            listFavorite.postValue(favoriteModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void setAllFavorite(String uid) {
        favoriteModelArrayList.clear();

        try {
            FirebaseFirestore
                    .getInstance()
                    .collection("favorite")
                    .whereEqualTo("uid", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                FavoriteModel model = new FavoriteModel();

                                model.setFavoriteId("" + document.get("favoriteId"));
                                model.setUid("" + document.get("uid"));
                                model.setData(document.toObject(FavoriteModel.class).data);


                                favoriteModelArrayList.add(model);
                            }
                            listFavorite.postValue(favoriteModelArrayList);
                        } else {
                            Log.e(TAG, task.toString());
                        }
                    });
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public LiveData<ArrayList<FavoriteModel>> getFavoriteList() {
        return listFavorite;
    }

}
