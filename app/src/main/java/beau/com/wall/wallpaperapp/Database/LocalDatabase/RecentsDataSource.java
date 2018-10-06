package beau.com.wall.wallpaperapp.Database.LocalDatabase;

import java.util.List;

import beau.com.wall.wallpaperapp.Database.DataSource.IRecentsDataSource;
import beau.com.wall.wallpaperapp.Database.Recents;
import io.reactivex.Flowable;

public class RecentsDataSource implements IRecentsDataSource {

    private RecentsDAO recentsDAO;
    private static RecentsDataSource instance;

    private RecentsDataSource(RecentsDAO recentsDAO) {
        this.recentsDAO = recentsDAO;
    }

    public static RecentsDataSource getInstance(RecentsDAO recentsDAO){
        if (instance == null)
            instance = new RecentsDataSource(recentsDAO);
        return instance;
    }

    @Override
    public Flowable<List<Recents>> getAllRecents() {
        return recentsDAO.getAllRecents();
    }

    @Override
    public void insertRecents(Recents... recents) {
        recentsDAO.insertRecents(recents);
    }

    @Override
    public void updateRecents(Recents... recents) {
        recentsDAO.updateRecents(recents);
    }

    @Override
    public void deleteRecents(Recents... recents) {
        recentsDAO.deleteRecents(recents);
    }

    @Override
    public void deleteAllRecents() {
        recentsDAO.deleteAllRecents();
    }
}
