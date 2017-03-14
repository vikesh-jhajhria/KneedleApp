package com.kneedleapp.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.kneedleapp.adapter.ProfileListAdapter;
import com.kneedleapp.utils.AppPreferences;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FollowersVo;
import com.kneedleapp.vo.ListVo;
import com.kneedleapp.vo.UserDetailsVo;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.kneedleapp.utils.Config.fragmentManager;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ProfileListAdapter profileListAdapter;
    private ArrayList<ListVo> List = new ArrayList<ListVo>();
    public static RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private ImageView listBtn, gridBtn;
    private static BaseActivity context;
    private View view;
    private String mUserId, mUserName, mUserTitle;

    private TextView num_of_posts, num_of_followers, num_of_following, address, designation;
    private CircleImageView userImgView;
    private AppPreferences mPrefernce;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mUserName = bundle.getString("USERNAME");
            mUserId = bundle.getString("USERID");
            mUserTitle = bundle.getString("USERTITLE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_profile, container, false);
        applyFonts(view);


        view.findViewById(R.id.ll_followers).setOnClickListener(this);
        view.findViewById(R.id.ll_following).setOnClickListener(this);
        ((RelativeLayout) getActivity().findViewById(R.id.rl_toolbar)).setVisibility(View.GONE);
        context = (BaseActivity) getActivity();
        mPrefernce = AppPreferences.getAppPreferences(context);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        List = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            ListVo check = new ListVo();
            check.ProjectName = "Android";
            check.image = getResources().getDrawable(R.drawable.image);
            List.add(check);
        }
        listBtn = (ImageView) view.findViewById(R.id.img_list);
        gridBtn = (ImageView) view.findViewById(R.id.img_grid);
        profileListAdapter = new ProfileListAdapter(List, getContext(), "grid");
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(profileListAdapter);

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gridBtn.setVisibility(View.GONE);
                listBtn.setVisibility(View.VISIBLE);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                profileListAdapter = new ProfileListAdapter(List, getContext(), "LIST");
                recyclerView.setAdapter(profileListAdapter);
            }
        });
        gridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listBtn.setVisibility(View.GONE);
                gridBtn.setVisibility(View.VISIBLE);
                StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(gridLayoutManager);
                profileListAdapter = new ProfileListAdapter(List, getContext(), "GRID");
                recyclerView.setAdapter(profileListAdapter);
            }
        });


        view.findViewById(R.id.txt_btn_edit).setOnClickListener(this);
        view.findViewById(R.id.img_back).setOnClickListener(this);
        view.findViewById(R.id.img_chat).setOnClickListener(this);

        num_of_posts = (TextView) view.findViewById(R.id.txt_post_count);
        num_of_followers = (TextView) view.findViewById(R.id.txt_follower_count);
        num_of_following = (TextView) view.findViewById(R.id.txt_following_count);
        address = (TextView) view.findViewById(R.id.txt_address);
        designation = (TextView) view.findViewById(R.id.txt_designation);
        userImgView = (CircleImageView) view.findViewById(R.id.user_img);

        if (mUserTitle != null) {
            ((TextView) view.findViewById(R.id.txt_username)).setText(mUserTitle);
        } else {
            ((TextView) view.findViewById(R.id.txt_username)).setText(mPrefernce.getStringValue(AppPreferences.USER_NAME));
        }

        getUserDetails();


        return view;
    }

    private void applyFonts(View view) {
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_username), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_post), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_post_count), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_follower), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_follower_count), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_following), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_following_count), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_btn_edit), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_btn_follow), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_btn_following), Config.CENTURY_GOTHIC_REGULAR);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_designation), Config.CENTURY_GOTHIC_BOLD);
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_address), Config.CENTURY_GOTHIC_REGULAR);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.txt_btn_edit:
                Fragment fragment = new EditProfileFragment();
                fragmentManager.beginTransaction().add(R.id.main_frame, fragment).addToBackStack(null).commit();

                /*Fragment fragment = new UserFollowRequest();
                getFragmentManager().beginTransaction().add(R.id.main_frame,fragment).addToBackStack(null).commit();
*/

                break;
            case R.id.img_back:
                break;
            case R.id.img_chat:
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View view1 = LayoutInflater.from(getContext()).inflate(R.layout.unfollow_popup, null);
                builder.setCancelable(false);
                builder.setView(view1);

                String text = "UNFOLLOW  ABIELKUNST?";
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colourRed)), 0, 8, 0);
                spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.textColorPrimary)), 9, text.length(), 0);
                ((TextView) view1.findViewById(R.id.txt_unfollow)).setText(spannableString);
                Utils.setTypeface(getActivity(), ((TextView) view1.findViewById(R.id.txt_unfollow)), Config.CENTURY_GOTHIC_BOLD);
                Utils.setTypeface(getActivity(), ((TextView) view1.findViewById(R.id.txt_cancel)), Config.CENTURY_GOTHIC_BOLD);
                Utils.setTypeface(getActivity(), ((TextView) view1.findViewById(R.id.txt_confirm)), Config.CENTURY_GOTHIC_BOLD);
                final AlertDialog alertDialog = builder.create();

                ((TextView) view1.findViewById(R.id.txt_cancel)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                ((TextView) view1.findViewById(R.id.txt_confirm)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();

                break;
            case R.id.ll_followers:
                FollowerFragment followerFragment = new FollowerFragment();
                fragmentManager.beginTransaction().add(R.id.main_frame, followerFragment)
                        .addToBackStack(null).commit();

                break;
            case R.id.ll_following:
                FollowingFragment followingFragment = new FollowingFragment();
                fragmentManager.beginTransaction().add(R.id.main_frame, followingFragment)
                        .addToBackStack(null).commit();
                break;


        }
    }

    public void getUserDetails() {
        context.showProgessDialog("Please wait...");
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.USER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        context.dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);

                                JSONObject userDataJsonObject = jObject.getJSONObject("user_data");
                                num_of_posts.setText(userDataJsonObject.getString("posts"));
                                num_of_following.setText(userDataJsonObject.getString("following"));
                                num_of_followers.setText(userDataJsonObject.getString("followers"));
                                if (!userDataJsonObject.getString("image").isEmpty()) {
                                    Picasso.with(context).load(Config.USER_IMAGE_URL + userDataJsonObject.getString("image")).placeholder(R.drawable.default_feed).error(R.drawable.default_feed).into(userImgView);
                                }

                                address.setText(userDataJsonObject.getString("city") + "," + userDataJsonObject.getString("state"));
                                designation.setText(userDataJsonObject.getString("profiletype"));
                            } else {
                                Toast.makeText(getContext(), "no data available", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show();
                        Log.d("error", volleyError.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();


                if (mUserId != null && mUserName != null) {
                    params.put("user_id", getArguments().getString("USERID"));
                    params.put("username", getArguments().getString("USERNAME"));
                    Log.e("aman", "aman");

                } else {
                    params.put("user_id", mPrefernce.getStringValue(AppPreferences.USER_ID));
                    params.put("username", mPrefernce.getStringValue(AppPreferences.USER_NAME));
                    Log.e("aman", "sharma");
                }


                return params;
            }
        };

        requestFeed.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue feedqueue = Volley.newRequestQueue(getContext());
        feedqueue.add(requestFeed);
    }
}







