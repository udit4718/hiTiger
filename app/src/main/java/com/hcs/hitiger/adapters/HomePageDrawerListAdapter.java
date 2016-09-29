package com.hcs.hitiger.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.model.ListModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj gupta on 4/28/16.
 */
public class HomePageDrawerListAdapter extends ArrayAdapter<ListModel> {
    private List<ListModel> listModelList = new ArrayList<>();

    public HomePageDrawerListAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void setListModelList(List<ListModel> listModelList) {
        this.listModelList = listModelList;
    }

    @Override
    public int getCount() {
        return listModelList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.drawer_list_item, parent, false);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.left_image);
        TextView textView = (TextView) convertView.findViewById(R.id.center_text);

        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), listModelList.get(position).getImageId()));
        textView.setText(listModelList.get(position).getData());
        textView.setTypeface(HitigerApplication.SEMI_BOLD);

        return convertView;
    }
}
