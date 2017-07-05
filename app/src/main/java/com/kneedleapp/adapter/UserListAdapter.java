package com.kneedleapp.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.bumptech.glide.Glide;
import com.kneedleapp.BaseActivity;
import com.kneedleapp.ProfileActivity;
import com.kneedleapp.R;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.UserDetailsVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.CheckViewHolder> {

    Context context;
    List<UserDetailsVo> list;
    String currentDate;
    BaseActivity baseContext;
    String listType;


    public UserListAdapter(Context context, List<UserDetailsVo> list, String listType) {
        this.context = context;
        this.list = list;
        this.listType = listType;

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
        final UserDetailsVo userDetail = list.get(position);
        holder.txt_name.setText(userDetail.getUsername());
        holder.fullname.setText(userDetail.getFullname());
        holder.job.setText(userDetail.getProfiletype());
        if (!userDetail.getImage().isEmpty()) {
            Glide.with(context).load(userDetail.getImage()).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(holder.img);
        }

        if (!listType.equalsIgnoreCase("BLOCKED_USER")) {
            if(!userDetail.getUserId().equalsIgnoreCase(AppPreferences.getAppPreferences(context).getUserId())) {
                if (userDetail.getStatus().equalsIgnoreCase("1")) {
                    holder.follow.setVisibility(View.GONE);
                    holder.unfollow.setVisibility(View.VISIBLE);
                } else {
                    holder.follow.setVisibility(View.VISIBLE);
                    holder.unfollow.setVisibility(View.GONE);
                }
            }
        } else {
            holder.unblock.setVisibility(View.VISIBLE);
        }

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(context, true)) {
                    followUnfollowUser(userDetail.getUserId(), currentDate, position);
                }
            }
        });

        holder.unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(context, true)) {
                    followUnfollowUser(userDetail.getUserId(), currentDate, position);
                }
            }
        });

        holder.unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isNetworkConnected(context, true)) {
                    unblock(userDetail.getUserId(), position);
                }
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID",userDetail.getUserId())
                        .putExtra("USER_NAME", userDetail.getUsername())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));

            }
        });


        holder.txt_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(getApplicationContext(), ProfileActivity.class)
                        .putExtra("USER_ID",userDetail.getUserId())
                        .putExtra("USER_NAME", userDetail.getUsername())
                        .setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CheckViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name, fullname, job;
        TextView follow, unfollow, unblock;
        ImageView img;


        public CheckViewHolder(final View itemView) {
            super(itemView);
            txt_name = (TextView) itemView.findViewById(R.id.txt_username);
            fullname = (TextView) itemView.findViewById(R.id.txt_fullname);
            job = (TextView) itemView.findViewById(R.id.txt_profile_type);
            follow = (TextView) itemView.findViewById(R.id.btn_follow);
            unfollow = (TextView) itemView.findViewById(R.id.btn_unfollow);
            unblock = (TextView) itemView.findViewById(R.id.btn_unblock);
            img = (ImageView) itemView.findViewById(R.id.img_profile);


            Utils.setTypeface(context, txt_name, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, fullname, Config.CENTURY_GOTHIC_REGULAR);
            Utils.setTypeface(context, job, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, follow, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, unfollow, Config.CENTURY_GOTHIC_BOLD);
            Utils.setTypeface(context, unblock, Config.CENTURY_GOTHIC_BOLD);

        }

    }


    public void followUnfollowUser(final String friendId, final String date, final int position) {
        ((BaseActivity) context).showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.FOLLOW_UNFOLLOW_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) context).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);
                                Config.updateFollower = true;
                                Config.updateFollowing = true;
                                Config.updateProfile = true;
                                if(list.get(position).getStatus().equalsIgnoreCase("1")){
                                    list.get(position).setStatus("0");
                                } else {
                                    list.get(position).setStatus("1");
                                }
                                notifyDataSetChanged();
                            }
                            Toast.makeText(context, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();

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
                        ((BaseActivity) context).dismissProgressDialog();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", AppPreferences.getAppPreferences(context).getUserId());
                params.put("friend_user_id", friendId);
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

    public void unblock(final String userId, final int position) {
        ((BaseActivity) context).showProgessDialog();
        StringRequest block = new StringRequest(Request.Method.POST, Config.BLOCK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ((BaseActivity) context).dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("reponce...::>>", response);
                                list.remove(position);
                                notifyDataSetChanged();
                            }
                            Toast.makeText(context, jObject.getString("status_msg"), Toast.LENGTH_SHORT).show();

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
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("friend_user_id", userId);
                params.put("user_id", AppPreferences.getAppPreferences(context).getStringValue(AppPreferences.USER_ID));

                return params;
            }
        };

        block.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(block);
    }
}
