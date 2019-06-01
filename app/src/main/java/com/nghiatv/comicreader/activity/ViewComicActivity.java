package com.nghiatv.comicreader.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nghiatv.comicreader.R;
import com.nghiatv.comicreader.adapter.MyViewPagerAdapter;
import com.nghiatv.comicreader.model.Chapter;
import com.nghiatv.comicreader.utils.Common;
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer;

public class ViewComicActivity extends AppCompatActivity {
    ViewPager viewPager;
    TextView txtChapterName;
    View btnChapterBack, btnChapterNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comic);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        txtChapterName = (TextView) findViewById(R.id.txtChapterName);
        btnChapterBack = (View) findViewById(R.id.btnChapterBack);
        btnChapterNext = (View) findViewById(R.id.btnChapterNext);

        btnChapterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.chapterIndex == 0) {
                    Toast.makeText(ViewComicActivity.this, "You are reading the first chapter", Toast.LENGTH_SHORT).show();
                } else {
                    Common.chapterIndex--;
                    Common.chapterSelected = Common.chapterList.get(Common.chapterIndex);
                    fetchLink(Common.chapterSelected);
                }
            }
        });

        btnChapterNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.chapterIndex == Common.chapterList.size() - 1) {
                    Toast.makeText(ViewComicActivity.this, "You are reading the last chapter", Toast.LENGTH_SHORT).show();
                } else {
                    Common.chapterIndex++;
                    Common.chapterSelected = Common.chapterList.get(Common.chapterIndex);
                    fetchLink(Common.chapterSelected);
                }
            }
        });
        fetchLink(Common.chapterSelected);
    }

    private void fetchLink(Chapter chapter) {
        if (chapter.Links != null) {
            if (chapter.Links.size() > 0) {
                MyViewPagerAdapter adapter = new MyViewPagerAdapter(getBaseContext(), chapter.Links);
                viewPager.setAdapter(adapter);

                txtChapterName.setText(Common.formatString(Common.chapterSelected.Name));

                //Animation
                BookFlipPageTransformer bookFlipPageTransformer = new BookFlipPageTransformer();
                bookFlipPageTransformer.setScaleAmountPercent(10f);
                viewPager.setPageTransformer(true, bookFlipPageTransformer);
            } else {
                Toast.makeText(this, "No image here", Toast.LENGTH_SHORT).show();
            }
        } else {
            txtChapterName.setText(Common.formatString(Common.chapterSelected.Name));
            viewPager.removeAllViews();
            Toast.makeText(this, "This chapter is translating...", Toast.LENGTH_SHORT).show();
        }
    }

}
