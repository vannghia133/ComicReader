package com.nghiatv.comicreader.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.nghiatv.comicreader.R;
import com.nghiatv.comicreader.adapter.MyChapterAdapter;
import com.nghiatv.comicreader.model.Comic;
import com.nghiatv.comicreader.utils.Common;

public class ChapterActivity extends AppCompatActivity {
    RecyclerView recyclerChapter;
    TextView txtNameChapter;
    LinearLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        //Init view
        txtNameChapter = (TextView) findViewById(R.id.txtNameChapter);
        recyclerChapter = (RecyclerView) findViewById(R.id.recyclerChapter);
        recyclerChapter.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerChapter.setLayoutManager(layoutManager);
        recyclerChapter.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Common.comicSelected.Name);

        //Set icon
        toolbar.setNavigationIcon(R.drawable.ic_chevron_left_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchChapter(Common.comicSelected);
    }

    private void fetchChapter(Comic comicSelected) {
        Common.chapterList = comicSelected.Chapters;
        recyclerChapter.setAdapter(new MyChapterAdapter(this, comicSelected.Chapters));
        txtNameChapter.setText(new StringBuilder("CHAPTERS (")
                .append(comicSelected.Chapters.size())
                .append(")"));
    }
}
