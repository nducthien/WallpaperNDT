package beau.com.wall.wallpaperapp.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import beau.com.wall.wallpaperapp.common.Common;
import beau.com.wall.wallpaperapp.Interface.ItemClickListener;
import beau.com.wall.wallpaperapp.activity.ListWallpaper;
import beau.com.wall.wallpaperapp.model.CategoryItem;
import beau.com.wall.wallpaperapp.R;
import beau.com.wall.wallpaperapp.ViewHolder.CategoryViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    //firebase
    FirebaseDatabase database;
    DatabaseReference categoryBackground;

    //firebase adapter
    FirebaseRecyclerOptions<CategoryItem> options;
    FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder> adapter;

    //view
    RecyclerView recyclerView;

    private static CategoryFragment INSTANCE = null;

    public CategoryFragment() {
        database = FirebaseDatabase.getInstance();
        categoryBackground = database.getReference(Common.STR_CATEGORY_BACKGROUND);

        options = new FirebaseRecyclerOptions.Builder<CategoryItem>()
                .setQuery(categoryBackground, CategoryItem.class) // select all
                .build();

        adapter = new FirebaseRecyclerAdapter<CategoryItem, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position, @NonNull final CategoryItem model) {
                Picasso.get()
                        .load(model.getImageLink())
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.background_image, new Callback() {
                            @Override
                            public void onSuccess() {


                            }

                            @Override
                            public void onError(Exception e) {
                                // try again online if cache failed
                                Picasso.get()
                                        .load(model.getImageLink())
                                        .error(R.drawable.ic_terrain_black_24dp)
                                        .into(holder.background_image, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(Exception exception) {
                                                Log.e("ERROR_NDT", "Could fetch image");

                                            }
                                        });

                            }
                        });
                holder.category_name.setText(model.getName());

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        // code late for detail category
                        Common.CATEGORY_ID_SELECTED = adapter.getRef(position).getKey(); // get key of item
                        Common.CATEGORY_SELECTED = model.getName();
                        Intent intent = new Intent(getActivity(), ListWallpaper.class);
                        startActivity(intent);

                    }
                });

            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_category, parent, false);
                return new CategoryViewHolder(itemView);
            }
        };

    }

    /**
     * Because each time user select tab , we show fragment so we will use Singleton Design Pattern
     * to make sure only one instance of fragment is create
     * @return
     */
    public static CategoryFragment getIntance() {
        if (INSTANCE == null)
            INSTANCE = new CategoryFragment();
        return INSTANCE;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        recyclerView = view.findViewById(R.id.rcv_category);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        setCategory();

        return view;
    }

    private void setCategory() {

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        if (adapter != null)
            adapter.stopListening();
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }


}
