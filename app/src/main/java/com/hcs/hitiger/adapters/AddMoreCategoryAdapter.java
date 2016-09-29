package com.hcs.hitiger.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.model.CategoryUiModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj gupta on 5/2/16.
 */
public class AddMoreCategoryAdapter extends ArrayAdapter<CategoryUiModel> {
    private List<CategoryUiModel> mCategoryList = new ArrayList<>();

    public AddMoreCategoryAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return mCategoryList.size();
    }

    public void setCategoryList(List<CategoryUiModel> categoryList) {
        mCategoryList = categoryList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.category_list, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.center_text);
        ImageView categoryImageView = (ImageView) convertView.findViewById(R.id.left_image);
        RelativeLayout relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relative_layout);

        if (position > 0) {
            categoryImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_whistle));
            categoryImageView.getLayoutParams().width = 40;
            categoryImageView.getLayoutParams().height = 40;
        } else
            categoryImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_location));


        textView.setText(mCategoryList.get(position).getName());
        textView.setTypeface(HitigerApplication.REGULAR);

        relativeLayout.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.white_background_with_border));
        relativeLayout.setPadding(15, 16, 10, 16);

        return convertView;
    }
}
