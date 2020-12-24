package beau.com.wall.wallpaperapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import beau.com.wall.wallpaperapp.common.Common;
import beau.com.wall.wallpaperapp.database.Recents;
import beau.com.wall.wallpaperapp.Interface.ItemClickListener;
import beau.com.wall.wallpaperapp.model.WallpaperItem;
import beau.com.wall.wallpaperapp.R;
import beau.com.wall.wallpaperapp.ViewHolder.ListWallpaperViewHolder;
import beau.com.wall.wallpaperapp.activity.ViewWallpaper;

public class RecentsRecyclerAdapter extends RecyclerView.Adapter<ListWallpaperViewHolder> {

    private final Context context;
    private List<Recents> recents;

    public RecentsRecyclerAdapter(Context context, List<Recents> recents) {
        this.context = context;
        this.recents = recents;
    }

    @Override
    public ListWallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallpaper, parent, false);
        int height = parent.getMeasuredHeight() / 2;
        itemView.setMinimumHeight(height);
        return new ListWallpaperViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListWallpaperViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        Picasso.get()
                .load(recents.get(position).getImageLink())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.wallpaper, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception exception) {
                        Picasso.get()
                                .load(recents.get(position).getImageLink())
                                .error(R.drawable.ic_terrain_black_24dp)
//                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                .into(holder.wallpaper, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Log.e("NDT DEV", "Could not fetch image");

                                    }
                                });

                    }
                });

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(context, ViewWallpaper.class);
                WallpaperItem wallpaperItem = new WallpaperItem();
                wallpaperItem.setCategoryId(recents.get(position).getCategoryId());
                wallpaperItem.setImageLink(recents.get(position).getImageLink());
                Common.select_background = wallpaperItem;
                Common.select_background_key = recents.get(position).getKey();
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recents.size();
    }
}
