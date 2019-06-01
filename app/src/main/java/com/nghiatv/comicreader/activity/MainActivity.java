package com.nghiatv.comicreader.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nghiatv.comicreader.IBannerLoadDone;
import com.nghiatv.comicreader.IComicLoadDone;
import com.nghiatv.comicreader.R;
import com.nghiatv.comicreader.adapter.MyComicAdapter;
import com.nghiatv.comicreader.adapter.MySliderAdapter;
import com.nghiatv.comicreader.model.Comic;
import com.nghiatv.comicreader.service.PicassoLoadingService;
import com.nghiatv.comicreader.utils.Common;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import ss.com.bannerslider.Slider;

public class MainActivity extends AppCompatActivity implements IBannerLoadDone, IComicLoadDone {
    Slider slider;
    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView recyclerComic;
    TextView txtComic;
    ImageView btnFilterSearch;

    //Database
    DatabaseReference banners, comics;

    //Listener
    IBannerLoadDone bannerListener;
    IComicLoadDone comicListener;

    android.app.AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init database
        banners = FirebaseDatabase.getInstance().getReference(Common.BANNERS);
        comics = FirebaseDatabase.getInstance().getReference(Common.COMICS);

        //Init listener
        bannerListener = this;
        comicListener = this;

        btnFilterSearch = (ImageView) findViewById(R.id.btnShowFilterSearch);
        btnFilterSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, FilterSearchActivity.class));
            }
        });

        slider = (Slider) findViewById(R.id.slider);
        Slider.init(new PicassoLoadingService());

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadBanner();
                loadComic();
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadBanner();
                loadComic();
            }
        });

        recyclerComic = (RecyclerView) findViewById(R.id.recyclerComic);
        recyclerComic.setHasFixedSize(true);
        recyclerComic.setLayoutManager(new GridLayoutManager(this, 2));

        txtComic = (TextView) findViewById(R.id.txtComic);
    }

    private void loadBanner() {
        banners.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> bannerList = new ArrayList<>();
                for (DataSnapshot bannerSnapshot : dataSnapshot.getChildren()) {
                    String image = bannerSnapshot.getValue(String.class);
                    bannerList.add(image);
                }

                //Call listener
                bannerListener.onBannerLoadDoneListener(bannerList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComic() {
        //Show dialog
        alertDialog = new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .setMessage("Please wait...")
                .build();

        if (!swipeRefreshLayout.isRefreshing()) {
            alertDialog.show();
        }

        comics.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Comic> comicList = new ArrayList<>();
                for (DataSnapshot comicSnapshot : dataSnapshot.getChildren()) {
                    Comic comic = comicSnapshot.getValue(Comic.class);
                    comicList.add(comic);
                }

                //Call listener
                comicListener.onComicLoadDoneListener(comicList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBannerLoadDoneListener(List<String> banners) {
        slider.setAdapter(new MySliderAdapter(banners));
    }

    @Override
    public void onComicLoadDoneListener(List<Comic> comicList) {
        Common.comicList = comicList;

        recyclerComic.setAdapter(new MyComicAdapter(getBaseContext(), comicList));

        txtComic.setText(new StringBuilder("NEW COMIC (")
                .append(comicList.size())
                .append(")"));

        if (!swipeRefreshLayout.isRefreshing()) {
            alertDialog.dismiss();
        }
    }
}
