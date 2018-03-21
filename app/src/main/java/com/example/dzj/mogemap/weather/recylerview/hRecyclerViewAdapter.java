package com.example.dzj.theweather.recylerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dzj.theweather.R;

import java.util.List;

/**
 * Created by dzj on 2017/2/23.
 */

public class hRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,TextView.OnEditorActionListener{
    private List<String>history_city;
    private LayoutInflater mInflater;
    private Context context;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private EditKeyListener meditKeyListener=null;
    @Override
    public void onClick(View v) {
        if(mOnItemClickListener!=null){
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if(meditKeyListener!=null){
            meditKeyListener.onEditorAction(v,actionId,event);
        }
        return false;
    }

    public interface OnRecyclerViewItemClickListener{
        void onItemClick(View view, int tag);
    }
    public interface  SaveEditListener{
        void SaveEdit(int position,String string);
    }
    public interface EditKeyListener{
        boolean onEditorAction(TextView v,int actionId, KeyEvent event);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener){
        this.mOnItemClickListener = listener;
    }
    public void setonEditorActionListener(EditKeyListener listener){
        this.meditKeyListener=listener;
    }
    public enum ITEM_TYPE {
        ITEM1,
        ITEM2
    }
    public hRecyclerViewAdapter(Context context, List<String> history_city){
        this.context=context;
        this.mInflater = LayoutInflater.from(context);
        this.history_city=history_city;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_TYPE.ITEM2.ordinal()) {
            return new Item2ViewHolder(mInflater.inflate(R.layout.seacher_city,parent,false));
        }
        else{
            return new Item1ViewHolder(mInflater.inflate(R.layout.history_city_name,parent,false));
        }
    }
    public int getItemViewType(int positon){
        if(positon==0){
            return ITEM_TYPE.ITEM2.ordinal();
        }else{
            return ITEM_TYPE.ITEM1.ordinal();
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  Item1ViewHolder){
            ((Item1ViewHolder)holder).h_name.setText(history_city.get(position-1));
            ((Item1ViewHolder)holder).h_name.setTag(position);
            ((Item1ViewHolder)holder).h_name.setOnClickListener(this);

        }else if(holder instanceof  Item2ViewHolder){
            ((Item2ViewHolder)holder).seacher_bt.setOnClickListener(this);
            ((Item2ViewHolder)holder).seacher_bt.setTag(position);
            ((Item2ViewHolder)holder).edit.addTextChangedListener(new TextSwitcher((Item2ViewHolder)holder));
            ((Item2ViewHolder)holder).edit.setTag(position);
            ((Item2ViewHolder)holder).edit.setOnEditorActionListener(this);
        }
    }

    @Override
    public int getItemCount() {
        return history_city.size()+1;
    }
    //history
    public static class Item1ViewHolder extends RecyclerView.ViewHolder{
        TextView h_name;
        public Item1ViewHolder(View itemView){
            super(itemView);
            h_name=(TextView)itemView.findViewById(R.id.h_name);
        }
    }
    //search
    public static class Item2ViewHolder extends RecyclerView.ViewHolder{
        EditText edit;
        Button seacher_bt;
        public Item2ViewHolder(View itemView){
            super(itemView);
            edit=(EditText)itemView.findViewById(R.id.edit);
            seacher_bt=(Button)itemView.findViewById(R.id.seacher_bt);
        }
    }
    class TextSwitcher implements TextWatcher{

        private Item2ViewHolder mHolder;

        public TextSwitcher(Item2ViewHolder mHolder){
            this.mHolder=mHolder;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            SaveEditListener listener=(SaveEditListener) context;
            if(s!=null){
                listener.SaveEdit(Integer.parseInt(mHolder.edit.getTag().toString()),s.toString());
            }
        }
    }
}
