package com.nghiatv.comicreader.activity;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.design.chip.ChipGroup;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nghiatv.comicreader.R;
import com.nghiatv.comicreader.adapter.MyComicAdapter;
import com.nghiatv.comicreader.model.Comic;
import com.nghiatv.comicreader.utils.Common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterSearchActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_search);

        //Init view
        recyclerFilter = (RecyclerView) findViewById(R.id.recyclerFilterSearch);
        recyclerFilter.setHasFixedSize(true);
        recyclerFilter.setLayoutManager(new GridLayoutManager(this, 2));

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNav);
        bottomNavigationView.inflateMenu(R.menu.main_menu);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.actionFilter:
                        showFilterDialog();
                        break;

                    case R.id.actionSearch:
                        showSearchDialog();
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void showFilterDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Select Category");

        final LayoutInflater inflater = this.getLayoutInflater();
        View filterLayout = inflater.inflate(R.layout.dialog_option, null);

        final AutoCompleteTextView txtCategory = (AutoCompleteTextView) filterLayout.findViewById(R.id.txtCategory);
        final ChipGroup chipGroup = (ChipGroup) filterLayout.findViewById(R.id.chipGroup);

        //Create autocomple
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.select_dialog_item, Common.categories);
        txtCategory.setAdapter(adapter);
        txtCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Clear
                txtCategory.setText("");

                //Crete tag
                Chip chip = (Chip) inflater.inflate(R.layout.item_chip, null, false);
                chip.setText(((TextView) view).getText());
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chipGroup.removeView(view);
                    }
                });
                chipGroup.addView(chip);
            }
        });

        alertDialog.setView(filterLayout);
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("FILTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<String> filterKey = new ArrayList<>();
                StringBuilder filterQuery = new StringBuilder("");
                for (int j = 0; j < chipGroup.getChildCount(); j++) {
                    Chip chip = (Chip) chipGroup.getChildAt(j);
                    filterKey.add(chip.getText().toString());
                }

                //Because in databae, category will be sort from A->Z and split by
                //So we need sort out filter key
                Collections.sort(filterKey);

                //Convert list to string
                for (String key : filterKey) {
                    filterQuery.append(key).append(",");
                }

                //Remove last ","
                filterQuery.setLength(filterQuery.length() - 1);

                //Filter by this query
                recyclerFilter.removeAllViews();
                fetchFilterCategory(filterQuery.toString());
            }
        });

        alertDialog.show();
    }

    private void fetchFilterCategory(String query) {
        List<Comic> comicFilter = new ArrayList<>();
        for (Comic comic : Common.comicList) {
            if (comic.Category != null) {
                if (comic.Category.contains(query)) {
                    comicFilter.add(comic);
                }
            }
        }
        if (comicFilter.size() > 0) {
            recyclerFilter.setAdapter(new MyComicAdapter(getBaseContext(), comicFilter));
        } else {
            Toast.makeText(this, "No result", Toast.LENGTH_SHORT).show();
        }
    }

    private void showSearchDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Search");

        final LayoutInflater inflater = this.getLayoutInflater();
        View searchLayout = inflater.inflate(R.layout.dialog_search, null);

        final EditText edtSearch = (EditText) searchLayout.findViewById(R.id.edtSearch);

        alertDialog.setView(searchLayout);
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setPositiveButton("SEARCH", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recyclerFilter.removeAllViews();
                fetchSearchComic(edtSearch.getText().toString());
            }
        });

        alertDialog.show();
    }

    private void fetchSearchComic(String query) {
        List<Comic> comicSearch = new ArrayList<>();
        for (Comic comic : Common.comicList) {
            if (comic.Name.contains(query)) {
                comicSearch.add(comic);
            }
        }
        if (comicSearch.size() > 0) {
            recyclerFilter.setAdapter(new MyComicAdapter(getBaseContext(), comicSearch));
        } else {
            Toast.makeText(this, "No result", Toast.LENGTH_SHORT).show();
        }
    }
}
