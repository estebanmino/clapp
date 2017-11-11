package com.construapp.construapp.main;


import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Section;

import java.util.List;


/**
 * Created by JOSE on 18-09-2017.
 */

public class SectionsAdapter extends BaseAdapter {
    private final Context context;
    private final List<Section> sectionModelList;

    private TextView sectionName;
    private TextView sectionDescription;

    public SectionsAdapter(Context context, List<Section> sectionModelList) {
        this.context = context;
        this.sectionModelList = sectionModelList;
    }

    @Override
    public int getCount() {
        if(sectionModelList != null) {
            return sectionModelList.size();
        }

        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return sectionModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //firebaseFirebase.init();

        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.section_list_item,null);
        }

        sectionName = convertView.findViewById(R.id.textview_fullname);
        sectionDescription = convertView.findViewById(R.id.section_description);

        final String elementName = sectionModelList.get(position).getName();
        final String elementDescription = sectionModelList.get(position).getDescription();

        sectionName.setText(elementName);
        sectionDescription.setText(elementDescription);


        return convertView;
    }

}
