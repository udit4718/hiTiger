package com.hcs.hitiger.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.adapters.AddCategoryAdapter;
import com.hcs.hitiger.api.ApiService;
import com.hcs.hitiger.api.model.categories.CategoryRequest;
import com.hcs.hitiger.api.model.categories.CategoryResponse;
import com.hcs.hitiger.api.model.club.CreateCategoryResponse;
import com.hcs.hitiger.api.model.club.CreateClub;
import com.hcs.hitiger.api.model.club.CreateClubRequest;
import com.hcs.hitiger.api.model.user.ClubApiModel;
import com.hcs.hitiger.model.CategoryData;
import com.hcs.hitiger.model.CategoryUiModel;
import com.hcs.hitiger.model.Progressable;
import com.hcs.hitiger.model.UserProfileDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddCategoryActivity extends AppCompatActivity implements Progressable {
    @InjectView(R.id.add_category_title)
    TextView title;
    @InjectView(R.id.add_category_list)
    ListView addCategoryList;
    @InjectView(R.id.enter_category)
    EditText searchCategory;
    @InjectView(R.id.done_button)
    TextView doneButton;
    @InjectView(R.id.empty_view)
    TextView emptyTextView;

    private AddCategoryAdapter addCategoryAdapter;
    private CategoryUiModel typeOfCategory;
    private UserProfileDetail userProfileDetail;
    private ProgressDialog mProgressDialog;
    private List<Long> categoryIds;
    private List<CategoryData> mSelectedCategoryDataList;
    private List<CategoryData> mCategoryDataList;
    private List<Long> mPreviousIds;

    private TextWatcher mEditTextChangeLisner = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.toString() != null && addCategoryAdapter != null) {
                addCategoryAdapter.search(s.toString().toLowerCase());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener mDoneCliclLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateCategoryClub();
        }
    };

    private TextView.OnEditorActionListener msearchActionLisner = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                addCategoryAdapter.search("");
                if(!(searchCategory.getText().toString().trim().equals(""))){
                    createClub();
                }else{
                    searchCategory.setText("");
                }
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        }
    };

    private void createClub() {
        showProgress("", getResources().getString(R.string.creating_club));
        getApiService().createClub(new CreateClubRequest(userProfileDetail.getFbId(),userProfileDetail.getId(), new CreateClub(searchCategory.getText().toString(),
                "image_url", typeOfCategory.getId())), new Callback<CreateCategoryResponse>() {
            @Override
            public void success(CreateCategoryResponse createCategoryResponse, Response response) {
                hideProgress();
                searchCategory.setText("");
                if (createCategoryResponse.isSuccesfull()) {
                    getCategoryClubs();
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

    private void updateCategoryClub() {
        showProgress("", getResources().getString(R.string.adding_categories));
        getApiService().updateUserCatClubs(new CategoryRequest(userProfileDetail.getFbId(), userProfileDetail.getId(), typeOfCategory.getId(), categoryIds),
                new Callback<CategoryResponse>() {
                    @Override
                    public void success(CategoryResponse categoryResponse, Response response) {
                        hideProgress();
                        if (categoryResponse.isSuccesfull()) {
                            Toast.makeText(AddCategoryActivity.this, "Successful", Toast.LENGTH_LONG).show();
                            onBackPressed();
                        } else {
                            HitigerApplication.getInstance().showServerError();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideProgress();
                        HitigerApplication.getInstance().showNetworkErrorMessage();
                    }
                });
    }

    @OnClick(R.id.backpress_button)
    public void backpress() {
        onBackPressed();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        userProfileDetail = UserProfileDetail.getUserProfileDetail();
        typeOfCategory = getIntent().getParcelableExtra(AddMoreCategoryActivity.TYPE_OF_CATEGORY);

        ButterKnife.inject(this);

        title.setText("ADD " + typeOfCategory.getName());
        setFonts();

        mSelectedCategoryDataList = new ArrayList<>();
        getCategoryClubs();

        searchCategory.setHint("Enter " + typeOfCategory.getName() + " Name");
        searchCategory.addTextChangedListener(mEditTextChangeLisner);
        searchCategory.setOnEditorActionListener(msearchActionLisner);
        searchCategory.setImeOptions(EditorInfo.IME_ACTION_DONE);
    }

    
    private void getCategoryClubs() {
        showProgress("", getResources().getString(R.string.loading_categories));
        getApiService().getCategoryClubs(new CategoryRequest(userProfileDetail.getFbId(), userProfileDetail.getId(), typeOfCategory.getId()), new Callback<CategoryResponse>() {
            @Override
            public void success(CategoryResponse categoryResponse, Response response) {
                hideProgress();
                if (categoryResponse.isSuccesfull()) {
                    if (categoryResponse.getClubs().size() == 0) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setTypeface(HitigerApplication.SEMI_BOLD);
                    }
                    setUiCategoryDetails(categoryResponse.getClubs());
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

    private void setDataInAdapter() {
        if (addCategoryAdapter == null)
            addCategoryAdapter = new AddCategoryAdapter(this, 0);
        addCategoryList.setAdapter(addCategoryAdapter);
        addCategoryAdapter.addData(mCategoryDataList, typeOfCategory.getName());
        addCategoryAdapter.notifyDataSetChanged();
    }

    private void setFonts() {
        title.setTypeface(HitigerApplication.BOLD);
        searchCategory.setTypeface(HitigerApplication.REGULAR);
        doneButton.setTypeface(HitigerApplication.BOLD);
    }

    private boolean isNewCategorydAdded() {
        for (CategoryData categoryData : mSelectedCategoryDataList) {
            if (!mPreviousIds.contains(categoryData.getId())) {
                return true;
            }
        }
        return false;
    }


    public void changeDoneButton(boolean zeroSelected) {
        categoryIds = new ArrayList<>();
        mSelectedCategoryDataList = new ArrayList<>();
        for (CategoryData categoryData : mCategoryDataList)
            if (categoryData.isAdded()) {
                mSelectedCategoryDataList.add(categoryData);
                categoryIds.add(categoryData.getId());
            }

        if ((mSelectedCategoryDataList.size() != mPreviousIds.size() || isNewCategorydAdded()) && !zeroSelected) {
            doneButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.enable_button_background));
            doneButton.setOnClickListener(mDoneCliclLisner);
        } else {
            doneButton.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.disable_button_background));
            doneButton.setOnClickListener(null);
            doneButton.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    //code to short list of venue
    Comparator<CategoryData> sortAddedVenue = new Comparator<CategoryData>() {
        @Override
        public int compare(CategoryData lhs, CategoryData rhs) {
            return lhs.isAdded()?-1:1;
        }
    };
    public void setUiCategoryDetails(List<ClubApiModel> clubApiModelList) {
        mCategoryDataList = CategoryData.getCategories(clubApiModelList);
        Collections.sort(mCategoryDataList, sortAddedVenue);
        mPreviousIds = new ArrayList<>();
        for (CategoryData categoryData : mCategoryDataList) {
            if (categoryData.isAdded())
                mPreviousIds.add(categoryData.getId());
        }
        setDataInAdapter();
    }
    
}
