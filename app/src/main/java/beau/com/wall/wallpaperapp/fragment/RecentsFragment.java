package beau.com.wall.wallpaperapp.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import beau.com.wall.wallpaperapp.adapter.RecentsRecyclerAdapter;
import beau.com.wall.wallpaperapp.database.DataSource.RecentRepository;
import beau.com.wall.wallpaperapp.database.LocalDatabase.LocalDatabase;
import beau.com.wall.wallpaperapp.database.LocalDatabase.RecentsDataSource;
import beau.com.wall.wallpaperapp.database.Recents;
import beau.com.wall.wallpaperapp.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class RecentsFragment extends Fragment {


    public static RecentsFragment INSTANCE = null;

    RecyclerView rcv_recents;
    Context context;

    List<Recents> recentsList;
    RecentsRecyclerAdapter adapter;

    // Room Database
    CompositeDisposable compositeDisposable;
    RecentRepository recentRepository;

    @SuppressLint("ValidFragment")
    public RecentsFragment(Context context) {
        // Required empty public constructor
        this.context = context;

        // init Room Database
        compositeDisposable = new CompositeDisposable();
        LocalDatabase database = LocalDatabase.getInstance(context);
        recentRepository = RecentRepository.getInstance(RecentsDataSource.getInstance(database.recentsDAO()));
    }

    public static RecentsFragment getInstance(Context context) {
        if (INSTANCE == null)
            INSTANCE = new RecentsFragment(context);
        return INSTANCE;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recents, container, false);
        rcv_recents = view.findViewById(R.id.rcv_recent);
        rcv_recents.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        rcv_recents.setLayoutManager(gridLayoutManager);
        recentsList = new ArrayList<>();
        adapter = new RecentsRecyclerAdapter(context, recentsList);
        rcv_recents.setAdapter(adapter);
        
        loadRecents();

        return view;
    }

    private void loadRecents() {
       Disposable disposable = recentRepository.getAllRecents()
               .observeOn(AndroidSchedulers.mainThread())
               .subscribeOn(Schedulers.io())
               .subscribe(new Consumer<List<Recents>>() {
                   @Override
                   public void accept(List<Recents> recents) throws Exception {
                       onGetAllRecentsSuccess(recents);
                   }
               }, new Consumer<Throwable>() {
                   @Override
                   public void accept(Throwable throwable) throws Exception {
                       Log.d("ERROR", throwable.getMessage());
                   }
               });
       compositeDisposable.add(disposable);
    }

    private void onGetAllRecentsSuccess(List<Recents> recents) {
        recentsList.clear();
        recentsList.addAll(recents);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        super.onDestroyView();
    }
}
