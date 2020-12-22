package com.example.dekkotask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemClickListener {

    SearchView searchView;
    String[] column_number = {"2","3","4"};

    protected static RecyclerViewAdapter adapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    protected static ArrayList<String> data = new ArrayList<>();
    private int numberOfCols;
    private String themeString = "cats";  //by default show images of kittens

    protected static boolean hasMore = true,mLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //recyclerView
        recyclerView = findViewById(R.id.gridRecyclerView);
        int numberOfCols = 2;

        layoutManager = new GridLayoutManager(this, numberOfCols);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new RecyclerViewAdapter(this, data);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        if(hasMore && !mLoading) {
            //api call
            Loadmore.load(themeString);
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(hasMore && !mLoading) {
                    //api call
                    Loadmore.load(themeString);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu,menu);

        MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchViewMenuItem.getActionView();

        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchView.setIconified(true);
                searchView.clearFocus();
                searchView.onActionViewCollapsed();

                //Do your search
                if(!query.isEmpty()) {
                    themeString = query;

                    data.clear();
                    hasMore=true;
                    mLoading=false;

                    if(hasMore && !mLoading) {
                        //api call
                        Loadmore.load(themeString);
                    }
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.more) {

            new AlertDialog.Builder(this)
                    .setTitle(R.string.select_grid_col)
                    .setItems(column_number, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            numberOfCols = Integer.parseInt(column_number[which]);

                            DisplayMetrics displayMetrics = MainActivity.this.getResources().getDisplayMetrics();
                            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
                            int scalingFactor = (int) (dpWidth/numberOfCols);

                            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                                @Override
                                public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                                    super.onDraw(c, parent, state);
                                }
                            });

                            recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, numberOfCols));
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if(!searchView.isIconified()) {
            searchView.setIconified(true);
            searchView.clearFocus();
        }
        else
            super.onBackPressed();
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}