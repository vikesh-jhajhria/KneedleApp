package com.kneedleapp.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.kneedleapp.adapter.FollowerAdapter;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.FollowersVo;
import com.kneedleapp.vo.SearchResultVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kneedleapp.utils.Config.fragmentManager;


public class FollowerFragment extends BaseFragment {
    private BaseActivity context;

    private OnFragmentInteractionListener mListener;
    private FollowerAdapter followerAdapter;
    private ArrayList<SearchResultVO> List = new ArrayList<>();
    public static RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    private java.util.List<FollowersVo> followersList = new ArrayList<>();

    public FollowerFragment() {
        // Required empty public constructor
    }

    public static FollowerFragment newInstance() {
        FollowerFragment fragment = new FollowerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.img_back:
                fragmentManager.popBackStack();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        applyFonts(view);
        Config.LAST_PAGE = "";
        view.findViewById(R.id.img_back).setOnClickListener(this);

        context = (BaseActivity) getActivity();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        followerAdapter = new FollowerAdapter(followersList, getActivity());
        recyclerView.setAdapter(followerAdapter);

        getFollowers();

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void applyFonts(View view) {
        Utils.setTypeface(getActivity(), (TextView) view.findViewById(R.id.txt_title), Config.CENTURY_GOTHIC_BOLD);
    }

    @Override
    public void onResume() {
        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if (i == KeyEvent.KEYCODE_BACK) {
                    fragmentManager.popBackStackImmediate();

                    return true;
                }
                return false;
            }
        });

    }


    private void getFollowers() {
        context.showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.FOLLOWERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        context.dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);

                                JSONArray jsonArray = jObject.getJSONArray("followers_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    FollowersVo followersItemVo = new FollowersVo();
                                    followersItemVo.setFollowerUserId(jsonObject.getString("user_id"));
                                    followersItemVo.setFullname(jsonObject.getString("fullname"));
                                    followersItemVo.setImage("http://kneedleapp.com/restAPIs/uploads/user_images/" + jsonObject.getString("image"));
                                    followersItemVo.setProfiletype(jsonObject.getString("profiletype"));
                                    followersItemVo.setUsername(jsonObject.getString("username"));
                                    followersItemVo.setGender(jsonObject.getString("gender"));
                                    followersItemVo.setCity(jsonObject.getString("city"));
                                    followersItemVo.setCountry(jsonObject.getString("state"));
                                    followersItemVo.setCountry(jsonObject.getString("country"));
                                    followersItemVo.setBio(jsonObject.getString("bio"));
                                    followersItemVo.setDob(jsonObject.getString("dob"));
                                    followersItemVo.setEmail(jsonObject.getString("email"));
                                    followersItemVo.setWebsite(jsonObject.getString("website"));
                                    followersItemVo.setPrivacy(jsonObject.getString("privacy"));
                                    followersItemVo.setZipcode(jsonObject.getString("zipcode"));
                                    followersItemVo.setStatus(jsonObject.getString("status"));

                                    followersList.add(followersItemVo);
                                }
                                followerAdapter.notifyDataSetChanged();

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
                params.put("user_id", "4");

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
