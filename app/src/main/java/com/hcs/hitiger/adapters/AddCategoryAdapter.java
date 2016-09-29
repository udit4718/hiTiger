package com.hcs.hitiger.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.AddCategoryActivity;
import com.hcs.hitiger.model.CategoryData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj gupta on 5/3/16.
 */
public class AddCategoryAdapter extends ArrayAdapter<CategoryData> {
    private static final String TAG = AddCategoryAdapter.class.getSimpleName();
    private List<CategoryData> mDisplayList = new ArrayList<>();
    private List<CategoryData> mFullList;
    private String typeOfCategory;
    private String query;

    public AddCategoryAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void addData(List<CategoryData> list, String typeOfCategory) {

        this.mFullList = list;
        this.mDisplayList = mFullList;
        this.typeOfCategory = typeOfCategory;
    }

    public void search(String query) {
        this.query = query;
        mDisplayList = new ArrayList<>();
        if (query.length() == 0) {
            mDisplayList.addAll(mFullList);
        } else {
            for (int i = 0; i < mFullList.size(); i++) {
                if (mFullList.get(i).getName().toLowerCase().contains(query)) {
                    mDisplayList.add(mFullList.get(i));
                }
            }
        }
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDisplayList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.add_category_list, parent, false);
        }
        TextView categoryText = (TextView) convertView.findViewById(R.id.center_text);
        ImageView categoryImageView = (ImageView) convertView.findViewById(R.id.left_image);
        RelativeLayout categoryLayout = (RelativeLayout) convertView.findViewById(R.id.relative_layout);
        final ImageView rightArrow = (ImageView) convertView.findViewById(R.id.right_image);

        if (typeOfCategory.toLowerCase().equals("city"))
            categoryImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_location));
        else
            categoryImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_whistle));
        setSelectedDrawable(position, rightArrow);

        if (query != null) {
            String data = mDisplayList.get(position).getName();
            Spannable wordtoSpan = new SpannableString(data);
            wordtoSpan.setSpan(new ForegroundColorSpan(Color.DKGRAY), data.toLowerCase().indexOf(query),
                    data.toLowerCase().indexOf(query) + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            wordtoSpan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), data.toLowerCase().indexOf(query),
                    data.toLowerCase().indexOf(query) + query.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            categoryText.setText(wordtoSpan);
        } else {
            categoryText.setText(mDisplayList.get(position).getName());
        }

        categoryText.setTypeface(HitigerApplication.REGULAR);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                mDisplayList.get(position).setAdded(!mDisplayList.get(position).isAdded());
                setSelectedDrawable(position, rightArrow);
                ((AddCategoryActivity) getContext()).changeDoneButton(isNoItemSelected());
            }
        });
        return convertView;
    }
    
    private boolean isNoItemSelected(){
        for (CategoryData categoryData: mFullList) {
            if(categoryData.isAdded()) {
                Log.d(TAG, "isNoItemSelected() : " + true);
                return false;
            }
        }
        return true;
    }

    private void setSelectedDrawable(int position, ImageView rightArrow) {
        if (mDisplayList.get(position).isAdded()) {
            rightArrow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.verified_black));
        } else {
            rightArrow.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fill_circle_24dp));
        }
    }
}
