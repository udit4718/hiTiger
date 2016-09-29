package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.AddMoreCategoryAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.club.CategoriesApiModel;
import com.hcs.hitiger.api.model.club.CategoriesResponse;
import com.hcs.hitiger.api.model.user.UserUniqueIdRequest;
import com.hcs.hitiger.model.CategoryUiModel;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddMoreCategoryActivity extends AppCompatActivity implements Progressable {

    public static final String TYPE_OF_CATEGORY = "TYPE_OF_CATEGORY";

    @InjectView(R.id.add_more_category_title)
    TextView title;
    @InjectView(R.id.add_more_category_list)
    ListView addMoreCategoryListView;

    private UserProfileDetail userProfileDetail;
    private ProgressDialog mProgressDialog;
    private List<CategoryUiModel> categoryUiModelList;

    private AdapterView.OnItemClickListener mItemClickLisner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(AddMoreCategoryActivity.this, AddCategoryActivity.class);
            intent.putExtra(TYPE_OF_CATEGORY, categoryUiModelList.get(position));
            startActivity(intent);
        }
    };

    @OnClick(R.id.add_more_backpress)
    public void backButton() {
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_more_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userProfileDetail = UserProfileDetail.getUserProfileDetail();

        ButterKnife.inject(this);
        setFonts();

        loadUserClubs();
    }

    @Override
    public void showProgress(String title, String message) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = ProgressDialog.show(this, title, message, true);
    }

    @Override
    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    private void addDataInList(List<CategoriesApiModel> categoriesApiModelList) {
        categoryUiModelList = CategoryUiModel.getCategoryList(categoriesApiModelList);
        AddMoreCategoryAdapter addMoreCategoryAdapter = new AddMoreCategoryAdapter(this, 0);
        addMoreCategoryListView.setAdapter(addMoreCategoryAdapter);
        addMoreCategoryAdapter.setCategoryList(categoryUiModelList);
        addMoreCategoryAdapter.notifyDataSetChanged();
        addMoreCategoryListView.setOnItemClickListener(mItemClickLisner);
    }

    private void loadUserClubs() {
        showProgress("", getResources().getString(R.string.loading_categories));
        getApiService().getCategories(new UserUniqueIdRequest(userProfileDetail.getFbId()), new Callback<CategoriesResponse>() {
            @Override
            public void success(CategoriesResponse categoriesResponse, Response response) {
                hideProgress();
                if (categoriesResponse.isSuccesfull()) {
                    addDataInList(categoriesResponse.getCategories());
                } else
                    HitigerApplication.getInstance().showServerError();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                HitigerApplication.getInstance().showNetworkErrorMessage();
            }
        });
    }

    private ApiService getApiService() {
        return HitigerApplication.getInstance().getRestClient().getApiService();
    }

    private void setFonts() {
        title.setTypeface(HitigerApplication.BOLD);
    }
}
