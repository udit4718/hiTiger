package com.hcs.hitiger.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hcs.hitiger.R;

/**
 * Created by anuj gupta on 4/26/16.
 */
public class MainFragment extends Fragment {
    private static final String POSITION = "POSITION";
    private int position;

    public static Fragment getInstance(int position) {
        Fragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(POSITION);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        TextView textView = (TextView) view.findViewById(R.id.text_fragment);
        textView.setText("Fragment :" + position);
        return view;
    }
}
