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
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.ClubActivity;
import com.hcs.hitiger.model.ClubUiData;

import java.util.List;

/**
 * Created by anuj gupta on 5/2/16.
 */
public class ListOfCategoryAdapter extends ArrayAdapter<ClubUiData> {
    private List<ClubUiData> mList;
    private String categoryName;

    public ListOfCategoryAdapter(Context context, int resource, List<ClubUiData> list, String categoryName) {
        super(context, resource, list);
        mList = list;
        this.categoryName = categoryName;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.category_list, parent, false);
        }
        ImageView categoryImageView = (ImageView) convertView.findViewById(R.id.left_image);
        TextView categoryNameTextView = (TextView) convertView.findViewById(R.id.center_text);
        categoryNameTextView.setTypeface(HitigerApplication.BOLD);

        if (getItem(position).getCategoryId() == 2) {
            categoryImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_location));
        } else {
            categoryImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_whistle));
        }
        categoryImageView.getLayoutParams().height = 30;
        categoryImageView.getLayoutParams().width = 30;

        categoryNameTextView.setText(mList.get(position).getName());
        categoryNameTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorGrey));

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ClubActivity.class);
                intent.putExtra(ClubActivity.CLUB_DATA, getItem(position));
                intent.putExtra(ClubActivity.CATEGORY_NAME, categoryName);
                getContext().startActivity(intent);
            }
        });
        return convertView;
    }
}
