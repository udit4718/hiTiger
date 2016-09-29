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
 * Created by anuj gupta on 5/16/16.
 */
public class ListOfCommonAdapter extends ArrayAdapter<ClubUiData> {
    public ListOfCommonAdapter(Context context, int resource, List<ClubUiData> detaiList) {
        super(context, resource, detaiList);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.category_list, parent, false);
        }
        ImageView leftImage = (ImageView) convertView.findViewById(R.id.left_image);
        TextView text = (TextView) convertView.findViewById(R.id.center_text);

        text.setText(getItem(position).getName());
        text.setTypeface(HitigerApplication.SEMI_BOLD);
        text.setTextColor(ContextCompat.getColor(getContext(), R.color.textColorGrey));

        if (getItem(position).getCategory() != null && getItem(position).getCategory().getName().toLowerCase().equals("city")) {
            leftImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_location));
        } else {
            leftImage.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_whistle));
        }
        leftImage.getLayoutParams().width = 40;
        leftImage.getLayoutParams().height = 40;

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ClubActivity.class);
                intent.putExtra(ClubActivity.CLUB_DATA, getItem(position));
                intent.putExtra(ClubActivity.CATEGORY_NAME, getItem(position).getCategory().getName());
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
