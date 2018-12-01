package com.baronkiko.launcherhijack;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;



public class AppAdapter extends BaseAdapter
{
    private Context mContext;
    private List<ResolveInfo> mListAppInfo;
    private PackageManager mPackManager;

    public AppAdapter(Context c, List<ResolveInfo> list, PackageManager pm) {
        mContext = c;
        mListAppInfo = list;
        mPackManager = pm;
    }

    @Override
    public int getCount() {
        return mListAppInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mListAppInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // get the selected entry
        ResolveInfo entry = mListAppInfo.get(position);

        // reference to convertView
        View v = convertView;

        // inflate new layout if null
        if(v == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            v = inflater.inflate(com.baronkiko.launcherhijack.R.layout.layout_appinfo, null);
        }

        // load controls from layout resources
        ImageView ivAppIcon = (ImageView)v.findViewById(com.baronkiko.launcherhijack.R.id.ivIcon);
        TextView tvAppName = (TextView)v.findViewById(com.baronkiko.launcherhijack.R.id.tvName);
        TextView tvPkgName = (TextView)v.findViewById(com.baronkiko.launcherhijack.R.id.tvPack);

        Log.v("Test", entry.activityInfo.processName) ;

        // set data to display
        ivAppIcon.setImageDrawable(entry.loadIcon(mPackManager));
        tvAppName.setText(entry.loadLabel(mPackManager));
        tvPkgName.setText(entry.activityInfo.processName);

        // return view
        return v;
    }




}
