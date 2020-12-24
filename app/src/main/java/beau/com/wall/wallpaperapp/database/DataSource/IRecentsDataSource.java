package beau.com.wall.wallpaperapp.database.DataSource;

import java.util.List;

import beau.com.wall.wallpaperapp.database.Recents;
import io.reactivex.Flowable;

public interface IRecentsDataSource {

    Flowable<List<Recents>> getAllRecents();
    void insertRecents(Recents... recents);
    void updateRecents(Recents... recents);
    void deleteRecents(Recents... recents);
    void deleteAllRecents();



}
