package com.hcs.hitiger.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by anuj gupta on 5/8/16.
 */
public class AdditionalInformationAdapter extends ArrayAdapter<String> {
    private List<String> list = new ArrayList<>();

    public AdditionalInformationAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        AdditionalListViewHolder additionalListViewHolder;
        if (convertView == null) {
            LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
            convertView = layoutInflater.inflate(R.layout.additional_information_list, parent, false);
            additionalListViewHolder = new AdditionalListViewHolder(convertView);
            convertView.setTag(additionalListViewHolder);
        } else {
            additionalListViewHolder = (AdditionalListViewHolder) convertView.getTag();
        }

        additionalListViewHolder.additionalInformationTextView.setText(list.get(position));
        additionalListViewHolder.numberTextView.setText(position + 1 + ".");

        return convertView;
    }

    static class AdditionalListViewHolder {
        @InjectView(R.id.number)
        TextView numberTextView;
        @InjectView(R.id.additional_information)
        TextView additionalInformationTextView;
        @InjectView(R.id.cancel_button)
        ImageView cancelButton;

        AdditionalListViewHolder(View view) {
            ButterKnife.inject(this, view);
            setFonts();
        }

        private void setFonts() {
            numberTextView.setTypeface(HitigerApplication.SEMI_BOLD);
            additionalInformationTextView.setTypeface(HitigerApplication.REGULAR);
        }
    }
}
