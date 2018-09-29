package beau.com.wall.wallpaperapp.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import io.reactivex.annotations.NonNull;

@Entity(tableName = "recents", primaryKeys = {"imageLink","categoryId"})
public class Recents {

    @ColumnInfo(name = "imageLink")
    @NonNull
    private String imageLink;

    @ColumnInfo(name = "categoryId")
    @NonNull
    private String categoryId;

    @ColumnInfo(name = "saveTime")
    private String saveTime;

    public Recents(String imageLink, String categoryId, String saveTime) {
        this.imageLink = imageLink;
        this.categoryId = categoryId;
        this.saveTime = saveTime;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }
}
