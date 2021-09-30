package com.filmku.filmku.movie;

import android.os.Parcel;
import android.os.Parcelable;

public class MovieModel implements Parcelable {

    private String title;
    private String genre;
    private String synopsis;
    private String dp;
    private String uid;
    private String rating;
    private String titleTemp;
    private String releaseDate;

    public MovieModel(){}


    protected MovieModel(Parcel in) {
        title = in.readString();
        genre = in.readString();
        synopsis = in.readString();
        dp = in.readString();
        uid = in.readString();
        rating = in.readString();
        titleTemp = in.readString();
        releaseDate = in.readString();
    }

    public static final Creator<MovieModel> CREATOR = new Creator<MovieModel>() {
        @Override
        public MovieModel createFromParcel(Parcel in) {
            return new MovieModel(in);
        }

        @Override
        public MovieModel[] newArray(int size) {
            return new MovieModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTitleTemp() {
        return titleTemp;
    }

    public void setTitleTemp(String titleTemp) {
        this.titleTemp = titleTemp;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(genre);
        parcel.writeString(synopsis);
        parcel.writeString(dp);
        parcel.writeString(uid);
        parcel.writeString(rating);
        parcel.writeString(titleTemp);
        parcel.writeString(releaseDate);
    }
}
