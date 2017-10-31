package com.construapp.construapp.sidebar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.construapp.construapp.R;
import com.construapp.construapp.models.Lesson;

import java.util.List;
import java.util.Map;

/**
 * Created by ESTEBANFML on 18-10-2017.
 */

public class SidebarAdapter extends BaseAdapter {

    private final Context context;
    private final Map<String,String> projectsMap;
    private String[] mKeys;

    private TextView projectName;


    public SidebarAdapter(Context context,Map<String,String> projectsMap) {
        this.context = context;
        this.projectsMap = projectsMap;
        this.mKeys = projectsMap.keySet().toArray(new String[projectsMap.size()]);
    }

    @Override
    public int getCount() {
        if(projectsMap != null) {
            return projectsMap.size();
        }
        else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return projectsMap.get(mKeys[position]);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater =
                    (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.drawer_list_item,null);
        }

        projectName = convertView.findViewById(R.id.project_name);

        Map.Entry<String,String> entry=projectsMap.entrySet().iterator().next();

        String key = mKeys[position];
        String value = getItem(position).toString();

        projectName.setText(key);


        return convertView;
    }

}
