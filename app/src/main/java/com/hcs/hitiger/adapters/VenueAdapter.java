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
import com.hcs.hitiger.fragments.AddressFragment;
import com.hcs.hitiger.model.AddressData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anuj gupta on 5/4/16.
 */
public class VenueAdapter extends ArrayAdapter<AddressData> {
    private List<AddressData> mDataList = new ArrayList<>();
    private boolean isForAddress;

    public VenueAdapter(Context context, int resounce) {
        super(context, resounce);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(R.layout.venue_list, parent, false);
        }
        TextView venueAddress = (TextView) convertView.findViewById(R.id.venue_address);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.address_icon_image);

        if (isForAddress) {
            imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.fill_circle_24dp));
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(AddressFragment.ADDRESS, mDataList.get(position));
                    ((Activity) getContext()).setResult(AddressFragment.REQUSET_CODE, intent);
                    ((Activity) getContext()).finish();
                }
            });
        }
        venueAddress.setText(mDataList.get(position).getAddress());
        venueAddress.setTypeface(HitigerApplication.SEMI_BOLD);
        return convertView;
    }

    public void addData(List<AddressData> list, boolean isForAddress) {
        this.mDataList = list;
        this.isForAddress = isForAddress;
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }
}
