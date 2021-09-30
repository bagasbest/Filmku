package com.filmku.filmku.favorite;

import android.os.Parcel;
import android.os.Parcelable;

import com.filmku.filmku.movie.MovieModel;

import java.util.ArrayList;
import java.util.Map;

public class FavoriteModel implements Parcelable {

    private String favoriteId;
    private String uid;
    public MovieModel data;


    public FavoriteModel() {}

    protected FavoriteModel(Parcel in) {
        favoriteId = in.readString();
        uid = in.readString();
        data = in.readParcelable(MovieModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(favoriteId);
        dest.writeString(uid);
        dest.writeParcelable(data, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FavoriteModel> CREATOR = new Creator<FavoriteModel>() {
        @Override
        public FavoriteModel createFromParcel(Parcel in) {
            return new FavoriteModel(in);
        }

        @Override
        public FavoriteModel[] newArray(int size) {
            return new FavoriteModel[size];
        }
    };

    public String getFavoriteId() {
        return favoriteId;
    }

    public void setFavoriteId(String favoriteId) {
        this.favoriteId = favoriteId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public MovieModel getData() {
        return data;
    }

    public void setData(MovieModel data) {
        this.data = data;
    }
}
