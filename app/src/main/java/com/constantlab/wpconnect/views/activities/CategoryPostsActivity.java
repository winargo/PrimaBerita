package com.constantlab.wpconnect.views.activities;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.model.Category;
import com.constantlab.wpconnect.views.fragments.PostsFragment;

public class CategoryPostsActivity extends BaseActivity {

    Category category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_posts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            category = bundle.getParcelable(Constants.TAG_FOR_CATEGORY);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, PostsFragment.newInstance(category)).commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
