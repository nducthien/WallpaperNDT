package beau.com.wall.wallpaperapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import beau.com.wall.wallpaperapp.common.Common;
import beau.com.wall.wallpaperapp.database.DataSource.RecentRepository;
import beau.com.wall.wallpaperapp.database.LocalDatabase.LocalDatabase;
import beau.com.wall.wallpaperapp.database.LocalDatabase.RecentsDataSource;
import beau.com.wall.wallpaperapp.database.Recents;
import beau.com.wall.wallpaperapp.helper.SaveImageHelper;
import beau.com.wall.wallpaperapp.model.WallpaperItem;
import beau.com.wall.wallpaperapp.R;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ViewWallpaper extends AppCompatActivity {

    private FloatingActionButton fab_wallpaper;
    private FloatingActionButton fab_download;
    private CoordinatorLayout rootLayout;
    public ImageView imageView;

    // Room Database
    CompositeDisposable compositeDisposable;
    RecentRepository recentRepository;

    FloatingActionMenu mainFloating;
    com.github.clans.fab.FloatingActionButton fbShare;

    // Facebook
    CallbackManager callbackManager;
    ShareDialog shareDialog;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Common.PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog alertDialog = new SpotsDialog(ViewWallpaper.this);
                    alertDialog.show();
                    alertDialog.setMessage("Please waiting....");

                    String fileName = UUID.randomUUID().toString() + ".png";
                    Picasso.get()
                            .load(Common.select_background.getImageLink())
                            .into(new SaveImageHelper(getBaseContext(),
                                    alertDialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName,
                                    "NDT Wallpaper Image"));

                } else {
                    Toast.makeText(this, "You need accept this permission to download image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    private final Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
            try {
                wallpaperManager.setBitmap(bitmap);
                Snackbar.make(rootLayout, "Wallpaper was set", Snackbar.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    private final Target facebookConvertBitmap = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            SharePhoto sharePhoto = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if (ShareDialog.canShow(SharePhotoContent.class)) {
                SharePhotoContent photoContent = new SharePhotoContent.Builder()
                        .addPhoto(sharePhoto)
                        .build();
                shareDialog.show(photoContent);
            }

        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_wallpaper);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init Facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        // init Room Database
        compositeDisposable = new CompositeDisposable();
        LocalDatabase database = LocalDatabase.getInstance(this);
        recentRepository = RecentRepository.getInstance(RecentsDataSource.getInstance(database.recentsDAO()));


        //init
        rootLayout = findViewById(R.id.rootLayout);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsingAppBar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);

        collapsingToolbarLayout.setTitle(Common.CATEGORY_SELECTED);

        imageView = findViewById(R.id.imgThumb);
        Picasso.get()
                .load(Common.select_background.getImageLink())
                .into(imageView);

        mainFloating = findViewById(R.id.menu);
        fbShare = findViewById(R.id.fb_share);
        fbShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create callback
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                    @Override
                    public void onSuccess(Sharer.Result result) {
                        Toast.makeText(ViewWallpaper.this, "Share successful !", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(ViewWallpaper.this, "Share cancelled !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(ViewWallpaper.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

                // we will fetch photo from link and convert to bitmap
                Picasso.get()
                        .load(Common.select_background.getImageLink())
                        .into(facebookConvertBitmap);
            }
        });

        // add to recent
        addToRecent();

        fab_wallpaper = findViewById(R.id.fab_wallpaper);
        fab_wallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Picasso.get()
                        .load(Common.select_background.getImageLink())
                        .into(target);
            }
        });

        fab_download = findViewById(R.id.fab_dowload);
        fab_download.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View view) {
                // don't forget request runtime permission to download img
                if (ActivityCompat.checkSelfPermission(ViewWallpaper.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Common.PERMISSION_REQUEST_CODE);
                } else {
                    AlertDialog alertDialog = new SpotsDialog(ViewWallpaper.this);
                    alertDialog.show();
                    alertDialog.setMessage("Please waiting....");

                    String fileName = UUID.randomUUID().toString() + ".png";
                    Picasso.get()
                            .load(Common.select_background.getImageLink())
                            .into(new SaveImageHelper(getBaseContext(),
                                    alertDialog,
                                    getApplicationContext().getContentResolver(),
                                    fileName,
                                    "Wallpaper Image"));
                }
            }
        });

        increaseViewCount();
    }

    private void increaseViewCount() {
        FirebaseDatabase.getInstance()
                .getReference(Common.STR_WALLPAPER)
                .child(Common.select_background_key)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild("viewCount")) {
                            WallpaperItem wallpaperItem = dataSnapshot.getValue(WallpaperItem.class);
                            long count = wallpaperItem.getViewCount() + 1;

                            //update
                            Map<String, Object> update_view = new HashMap<>();
                            update_view.put("viewCount", count);

                            FirebaseDatabase.getInstance()
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.select_background_key)
                                    .updateChildren(update_view)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ViewWallpaper.this, "Can't update count", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else { // if view count is not set (default)
                            Map<String, Object> update_view = new HashMap<>();
                            update_view.put("viewCount", Long.valueOf(1));

                            FirebaseDatabase.getInstance()
                                    .getReference(Common.STR_WALLPAPER)
                                    .child(Common.select_background_key)
                                    .updateChildren(update_view)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ViewWallpaper.this, "Can't set default view count", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void addToRecent() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Object>() {
            // Ctr I
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                Recents recents = new Recents(Common.select_background.getImageLink(),
                        Common.select_background.getCategoryId(),
                        String.valueOf(System.currentTimeMillis()),
                        Common.select_background_key);
                recentRepository.insertRecents(recents);
                emitter.onComplete();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e("ERROR", throwable.getMessage());
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {

                    }
                });

        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        Picasso.get().cancelRequest(target);
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish(); // close activity when click back button
        return super.onOptionsItemSelected(item);
    }
}
