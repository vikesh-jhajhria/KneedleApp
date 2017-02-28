package com.kneedleapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.vo.ListVo;

import java.util.ArrayList;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.CheckViewHolder> {

    Context context;
    ArrayList<ListVo> list;
    String check;


    public ListAdapter(ArrayList<ListVo> list, Context context, String check) {
        this.context = context;
        this.list = list;
        this.check=check;
    }

    @Override
    public CheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckViewHolder viewHolder;
        if(check.trim().equalsIgnoreCase("list"))
        { Log.e("hello","list");
            viewHolder=new CheckViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recyleview, parent, false));
        }
        else{
            Log.e("hello","hello");
            viewHolder=new CheckViewHolder(LayoutInflater.from(context).inflate(R.layout.item_stagger, parent, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CheckViewHolder holder, final int position) {
       final ListVo checkVo = list.get(position);
        holder.txt_name.setText(checkVo.ProjectName);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CheckViewHolder extends RecyclerView.ViewHolder {

        private TextView txt_name;
        ImageView img;



        public CheckViewHolder(final View itemView) {
            super(itemView);
            txt_name = (TextView) itemView.findViewById(R.id.txtview);
            img = (ImageView) itemView.findViewById(R.id.img);




        }

    }

}
