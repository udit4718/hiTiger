package com.hcs.hitiger.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.NonScrollListView;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.AddCategoryActivity;
import com.hcs.hitiger.activities.AddMoreCategoryActivity;
import com.hcs.hitiger.model.CategoryUiModel;
import com.hcs.hitiger.model.ClubUiData;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by anuj gupta on 4/29/16.
 */
public class CustomListAboutAdapter extends ArrayAdapter<CategoryUiModel> {
    public CustomListAboutAdapter(Context context, int resource, List<CategoryUiModel> arrayList) {
        super(context, resource, arrayList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final AboutUserViewHolder aboutUserViewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.about_user_list_layout, parent, false);
            aboutUserViewHolder = new AboutUserViewHolder(convertView);
            convertView.setTag(aboutUserViewHolder);
        } else {
            aboutUserViewHolder = (AboutUserViewHolder) convertView.getTag();
        }
        aboutUserViewHolder.categoryName.setText(getItem(position).getName());

        if (position > 0)
            aboutUserViewHolder.categoryImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_whistle));
        else
            aboutUserViewHolder.categoryImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_location));

        if (getItem(position).getClubs().size() > 0) {
            List<ClubUiData> clubUiDataList = ClubUiData.getList(getItem(position).getClubs());
            ListOfCategoryAdapter listOfCategoryAdapter = new ListOfCategoryAdapter(getContext(), 0, clubUiDataList, getItem(position).getName());
            aboutUserViewHolder.listOfCategory.setAdapter(listOfCategoryAdapter);
        }

        aboutUserViewHolder.addMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddCategoryActivity.class);
                intent.putExtra(AddMoreCategoryActivity.TYPE_OF_CATEGORY, getItem(position));
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }

    static class AboutUserViewHolder {
        @InjectView(R.id.category_image)
        ImageView categoryImage;
        @InjectView(R.id.category_name)
        TextView categoryName;
        @InjectView(R.id.add_more_detail_button)
        ImageView addMoreButton;
        @InjectView(R.id.list_of_category)
        NonScrollListView listOfCategory;

        AboutUserViewHolder(View view) {
            ButterKnife.inject(this, view);
            addMoreButton.setVisibility(View.VISIBLE);
            setStyles();
        }

        private void setStyles() {
            categoryName.setTypeface(HitigerApplication.REGULAR);
        }
    }
}
