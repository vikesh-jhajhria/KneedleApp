package com.kneedleapp.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kneedleapp.BaseActivity;
import com.kneedleapp.R;
import com.kneedleapp.fragment.FollowingFragment;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FollowersVo;
import com.kneedleapp.vo.SearchResultVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FollowerAdapter extends RecyclerView.Adapter<FollowerAdapter.CheckViewHolder> {

    Context context;
    List<FollowersVo> list;
    String currentDate;
    BaseActivity baseContext;



    public FollowerAdapter(List<FollowersVo> list, Context context) {
        this.context = context;
        this.list = list;

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        currentDate = df.format(c.getTime());


    }

    @Override
    public CheckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CheckViewHolder viewHolder;

        viewHolder = new CheckViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_follow_item, parent, false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CheckViewHolder holder, final int position) {
        final FollowersVo checkVo = list.get(position);
        holder.txt_name.setText(checkVo.getFullname().split(" ")[0]);
        holder.fullname.setText(checkVo.getFullname());
        holder.job.setText(checkVo.getProfiletype());

        if(checkVo.getStatus().equalsIgnoreCase("1")){
            holder.follow.setVisibility(View.GONE);
            holder.unfollow.setVisibility(View.VISIBLE);
        }else{
            holder.follow.setVisibility(View.VISIBLE);
            holder.unfollow.setVisibility(View.GONE);
        }

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUnfollowUser(checkVo.getFollowerUserId(),currentDate);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CheckViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name, fullname, job;
        TextView follow, unfollow;
        ImageView img;


        public CheckViewHolder(final View itemView) {
            super(itemView);
            txt_name = (TextView) itemView.findViewById(R.id.txt_username);
            fullname = (TextView) itemView.findViewById(R.id.txt_fullname);
            job = (TextView) itemView.findViewById(R.id.txt_designation);
            follow = (TextView) itemView.findViewById(R.id.btn_follow);
            unfollow = (TextView) itemView.findViewById(R.id.btn_unfollow);
            img = (ImageView) itemView.findViewById(R.id.img_profile);


            Utils.setTypeface(context, txt_name, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, fullname, Config.CENTURY_GOTHIC_REGULAR);
            Utils.setTypeface(context, job, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, follow, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, unfollow, Config.CENTURY_GOTHIC_BOLD);

        }

    }



    public void followUnfollowUser(final String friendId, final String date){
        ((BaseActivity)context).showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.FOLLOW_UNFOLLOW_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity)context).showProgessDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);

                                Toast.makeText(context,jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(context, "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(context, volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                        ((BaseActivity)context).dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", "4");
                params.put("friend_id",friendId );
                params.put("follow_date", date);

                return params;
            }
        };

        requestFeed.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue feedqueue = Volley.newRequestQueue(context);
        feedqueue.add(requestFeed);
    }

}
