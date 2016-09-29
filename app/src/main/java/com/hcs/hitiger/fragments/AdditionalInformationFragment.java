package com.hcs.hitiger.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hcs.hitiger.GetOpportunityInterface;
import com.hcs.hitiger.HitigerApplication;
import com.hcs.hitiger.R;
import com.hcs.hitiger.activities.CreateOpportynityActivity;
import com.hcs.hitiger.adapters.AdditionalInformationAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AdditionalInformationFragment extends android.app.Fragment {
    @InjectView(R.id.additional_list)
    ListView additionalListView;
    @InjectView(R.id.number)
    TextView additionalNumberTextView;
    @InjectView(R.id.add_to_list)
    ImageView addTolistButtonView;
    @InjectView(R.id.additional_data_text)
    EditText additionalEditTextView;
    @InjectView(R.id.empty_view)
    ScrollView emptyView;

    private AdditionalInformationAdapter additionalInformationAdapter;
    private List<String> additionalList = new ArrayList<>();

    private View.OnClickListener mAddTolistClickLisner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!additionalEditTextView.getText().toString().isEmpty()) {
                additionalList.add(additionalEditTextView.getText().toString());
                addToListChanges(additionalList);
                getOpportunityInterface.getOpportunity().setListOfAdditionalInformation(additionalList);
            }
        }
    };

    private void addToListChanges(List<String> additionalList) {
        additionalInformationAdapter.clear();
        additionalInformationAdapter.setList(additionalList);
        additionalInformationAdapter.notifyDataSetChanged();
        setChangesInLayout();
    }

    private AdapterView.OnItemClickListener mItemClickLisner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            ImageView cancelButton = (ImageView) view.findViewById(R.id.cancel_button);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    additionalList.remove(position);
                    additionalInformationAdapter.setList(additionalList);
                    additionalInformationAdapter.notifyDataSetChanged();
                    setChangesInLayout();
                }
            });
        }
    };
    private GetOpportunityInterface getOpportunityInterface;

    public void setChangesInLayout() {
        if (additionalInformationAdapter.getCount() < 1) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
        additionalEditTextView.setText(null);
        additionalNumberTextView.setText(additionalInformationAdapter.getCount() + 1 + ".");
    }

    public static AdditionalInformationFragment getInstance() {
        AdditionalInformationFragment fragment = new AdditionalInformationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_additional_information, container, false);

        ButterKnife.inject(this, view);

        setFonts();

        ((CreateOpportynityActivity) getActivity()).setCreateButton();

        additionalInformationAdapter = new AdditionalInformationAdapter(getActivity(), 0);
        additionalListView.setAdapter(additionalInformationAdapter);
        additionalListView.setOnItemClickListener(mItemClickLisner);
        addTolistButtonView.setOnClickListener(mAddTolistClickLisner);

        return view;
    }

    private void setFonts() {
        additionalNumberTextView.setTypeface(HitigerApplication.BOLD);
        additionalEditTextView.setTypeface(HitigerApplication.REGULAR_ITALIC);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getOpportunityInterface = (GetOpportunityInterface) getActivity();
        if (getOpportunityInterface.getOpportunity().getListOfAdditionalInformation().size() > 0) {
            addToListChanges(getOpportunityInterface.getOpportunity().getListOfAdditionalInformation());
        }
    }
}
