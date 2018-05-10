package com.example.dzj.mogemap.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dzj.mogemap.R;
import com.example.dzj.mogemap.activity.FriendsActivity;
import com.example.dzj.mogemap.fragment.ConfirmDeleteDialogFragment;
import com.example.dzj.mogemap.fragment.PKDialogFragment;
import com.example.dzj.mogemap.modle.Mogemap_user;
import com.example.dzj.mogemap.utils.HttpUtil;
import com.example.dzj.mogemap.utils.UserManager;
import com.example.dzj.mogemap.view.RoundImageView;

import java.util.List;

/**
 * Created by dzj on 2018/2/23.
 */

public class FriendsListViewAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater inflater = null;
    private List<Mogemap_user> users;

    public FriendsListViewAdapter(Context context, List<Mogemap_user> users){
        this.context = context;
        this.users = users;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.friends_item, null);
            holder.icon = (RoundImageView)convertView.findViewById(R.id.icon);
            holder.name = (TextView)convertView.findViewById(R.id.name);
            holder.pk = (ImageView)convertView.findViewById(R.id.pk);
            holder.delete = (ImageView)convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        setHeadImage(holder.icon, users.get(position).getHeadurl());
        holder.name.setText(users.get(position).getName());
        holder.pk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PKDialogFragment pkDialogFragment = new PKDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("mPhone", UserManager.getInstance().getUser().getPhone());
                bundle.putString("fPhone", users.get(position).getPhone());
                pkDialogFragment.setArguments(bundle);
                pkDialogFragment.show(((FriendsActivity)context).getFragmentManager(), "PKDialogFragment");
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmDeleteDialogFragment confirmDeleteDialogFragment = new ConfirmDeleteDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putString("mPhone", UserManager.getInstance().getUser().getPhone());
                bundle.putString("fPhone", users.get(position).getPhone());
                bundle.putInt("position", position);
                confirmDeleteDialogFragment.setArguments(bundle);
                confirmDeleteDialogFragment.show(((FriendsActivity)context).getFragmentManager(), "ConfirmDeleteDialogFragment");
            }
        });

        return convertView;
    }
    static class ViewHolder{
        public RoundImageView icon;
        public TextView name;
        public ImageView pk;
        public ImageView delete;
    }
    private void setHeadImage(final RoundImageView view, final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(url != null){
                    final Bitmap bitmap = HttpUtil.getHttpBitmap(url);
                    ((FriendsActivity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setImageBitmap(bitmap);
                        }
                    });

                }
            }
        }).start();
    }
}
