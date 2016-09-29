package com.hcs.hitiger.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.CustomListAboutAdapter;
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

public class AboutYouActivity extends AppCompatActivity implements Progressable {

    @InjectView(R.id.list_about_user)
    ListView aboutUserlistView;

    @InjectView(R.id.toolbar_title)
    TextView title;

    private UserProfileDetail userProfileDetail;
    private Dialog mProgressDialog;

    @OnClick(R.id.backpress_button)
    public void onBackPress() {
        onBackPressed();
    }

    private View.OnClickListener mAddMoreCategoryClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(AboutYouActivity.this, AddMoreCategoryActivity.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_you);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userProfileDetail = UserProfileDetail.getUserProfileDetail();

        ButterKnife.inject(this);
        title.setText("ABOUT YOU");
        setStyles();
        adderFooterInListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadingCategories();
    }

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

    private void loadingCategories() {
        showProgress("", getResources().getString(R.string.loading_categories));
        getApiService().getUsersClub(new UserUniqueIdRequest(userProfileDetail.getFbId(), userProfileDetail.getId(), "in"), new Callback<CategoriesResponse>() {
            @Override
            public void success(CategoriesResponse categoriesResponse, Response response) {
                hideProgress();
                if (categoriesResponse.isSuccesfull()) {
                    setCategoriesList(categoriesResponse.getCategories());
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

    private void setCategoriesList(List<CategoriesApiModel> categoriesList) {
        int count = 0;
        for (CategoriesApiModel categoriesApiModel : categoriesList) {
            if (categoriesApiModel.getClubs().size() != 0) {
                count++;
            }
        }
        if (count == 0) {
            startActivity(new Intent(AboutYouActivity.this, AddMoreCategoryActivity.class));
            finish();
        } else {
            List<CategoryUiModel> categoryUiModelList = CategoryUiModel.getCategoryWithFilteredCLubListList(categoriesList);
            CustomListAboutAdapter customListAboutAdapter = new CustomListAboutAdapter(this, 0, categoryUiModelList);
            aboutUserlistView.setAdapter(customListAboutAdapter);
        }
    }

    private void setStyles() {
        title.setTypeface(HitigerApplication.BOLD);
    }

    private void adderFooterInListView() {
        View view = getLayoutInflater().inflate(R.layout.about_user_footer_view, null);
        TextView addMoreText = (TextView) view.findViewById(R.id.add_more_category_text);
        ImageView addMoreIcon = (ImageView) view.findViewById(R.id.add_more_category_image);
        RelativeLayout addMoreLayout = (RelativeLayout) view.findViewById(R.id.add_more_category_layout);
        addMoreLayout.setOnClickListener(mAddMoreCategoryClickLisner);
        addMoreText.setTypeface(HitigerApplication.SEMI_BOLD);
        aboutUserlistView.addFooterView(view, null, false);
    }
}
