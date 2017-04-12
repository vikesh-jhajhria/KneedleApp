package com.kneedleapp.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.kneedleapp.adapter.UserListAdapter;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.UserDetailsVo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.kneedleapp.utils.Config.fragmentManager;

public class BlockedUsersFragment extends BaseFragment {
    private static BaseActivity context;

    private FollowerFragment.OnFragmentInteractionListener mListener;
    private UserListAdapter userListAdapter;
    public static RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    private String mUserId;
    private TextView emptyView;

    private ArrayList<UserDetailsVo> blockedUserList = new ArrayList<>();

    public BlockedUsersFragment() {
        // Required empty public constructor
    }

    public static BlockedUsersFragment newInstance(String userId) {
        BlockedUsersFragment fragment = new BlockedUsersFragment();
        Bundle bundle = new Bundle();
        bundle.putString("USER_ID", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mUserId = bundle.getString("USER_ID");
        }
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
        View view = inflater.inflate(R.layout.fragment_user_list, container, false);
        emptyView = (TextView) view.findViewById(R.id.empty_view);
        applyFonts(view);
        Config.LAST_PAGE = "";
        view.findViewById(R.id.img_back).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.txt_title)).setText("Blocked Users");

        context = (BaseActivity) getActivity();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        userListAdapter = new UserListAdapter(getActivity(), blockedUserList, "BLOCKED_USER");
        recyclerView.setAdapter(userListAdapter);

        getBlockedUsers();

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
        Utils.setTypeface(getActivity(), emptyView, Config.CENTURY_GOTHIC_REGULAR);
    }

    @Override
    public void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getView().setFocusableInTouchMode(true);
                getView().requestFocus();
                getView().setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if (i == KeyEvent.KEYCODE_BACK) {
                            fragmentManager.popBackStack();
                            return true;
                        }
                        return false;
                    }
                });
            }
        }, 500);

    }

    private void getBlockedUsers() {
        emptyView.setVisibility(View.GONE);
        context.showProgessDialog();
        StringRequest requestFeed = new StringRequest(Request.Method.POST, Config.GET_BLOCKED_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        context.dismissProgressDialog();
                        try {
                            final JSONObject jObject = new JSONObject(response);
                            if (jObject.getString("status_id").equals("1")) {
                                Log.e("responce....::>>>", response);

                                JSONArray jsonArray = jObject.getJSONArray("blocked_data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    UserDetailsVo userObject = new UserDetailsVo();
                                    userObject.setUserId(jsonObject.getString("user_id"));
                                    userObject.setFullname(jsonObject.getString("fullname"));
                                    userObject.setImage(Config.USER_IMAGE_URL + jsonObject.getString("image"));
                                    userObject.setProfiletype(jsonObject.getString("profiletype"));
                                    userObject.setUsername(jsonObject.getString("username"));
                                    userObject.setGender(jsonObject.getString("gender"));
                                    userObject.setCity(jsonObject.getString("city"));
                                    userObject.setCountry(jsonObject.getString("state"));
                                    userObject.setCountry(jsonObject.getString("country"));
                                    userObject.setBio(jsonObject.getString("bio"));
                                    userObject.setEmail(jsonObject.getString("email"));
                                    userObject.setWebsite(jsonObject.getString("website"));
                                    userObject.setPrivacy(jsonObject.getString("privacy"));
                                    userObject.setZipcode(jsonObject.getString("zipcode"));

                                    blockedUserList.add(userObject);
                                }
                                userListAdapter.notifyDataSetChanged();

                            }
                            if (blockedUserList.size() == 0) {
                                emptyView.setText(jObject.getString("status_msg"));
                                emptyView.setVisibility(View.VISIBLE);
                            } else {
                                emptyView.setVisibility(View.GONE);
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
                params.put("user_id", mUserId);

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
