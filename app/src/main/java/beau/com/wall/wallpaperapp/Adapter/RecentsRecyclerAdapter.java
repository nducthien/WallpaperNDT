package beau.com.wall.wallpaperapp.Adapter;

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

import beau.com.wall.wallpaperapp.Common.Common;
import beau.com.wall.wallpaperapp.Database.Recents;
import beau.com.wall.wallpaperapp.Interface.ItemClickListener;
import beau.com.wall.wallpaperapp.ListWallpaper;
import beau.com.wall.wallpaperapp.Model.WallpaperItem;
import beau.com.wall.wallpaperapp.R;
import beau.com.wall.wallpaperapp.ViewHolder.ListWallpaperViewHolder;
import beau.com.wall.wallpaperapp.ViewWallpaper;

public class RecentsRecyclerAdapter extends RecyclerView.Adapter<ListWallpaperViewHolder>{

    private Context context;
    private List<Recents> recents;

    public RecentsRecyclerAdapter(Context context, List<Recents> recents) {
        this.context = context;
        this.recents = recents;
    }

    @Override
    public ListWallpaperViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_wallpaper_item, parent, false);
        int height = parent.getMeasuredHeight()/2;
        itemView.setMinimumHeight(height);
        return new ListWallpaperViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListWallpaperViewHolder holder, final int position) {
        Picasso.with(context)
                .load(recents.get(position).getImageLink())
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(holder.wallpaper, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(context)
                                .load(recents.get(position).getImageLink())
                                .error(R.drawable.ic_terrain_black_24dp)
//                                        .networkPolicy(NetworkPolicy.OFFLINE)
                                .into(holder.wallpaper, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.e("NDT DEV", "Cound't not featch image");

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
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recents.size();
    }
}
