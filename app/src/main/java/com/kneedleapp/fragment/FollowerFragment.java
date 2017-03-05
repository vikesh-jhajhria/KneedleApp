package com.kneedleapp.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kneedleapp.R;
import com.kneedleapp.adapter.FollowerAdapter;
import com.kneedleapp.utils.Config;
import com.kneedleapp.utils.Utils;
import com.kneedleapp.vo.SearchResultVO;

import java.util.ArrayList;

import static com.kneedleapp.utils.Config.fragmentManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FollowerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FollowerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowerFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FollowerAdapter followerAdapter;
    private ArrayList<SearchResultVO> List = new ArrayList<>();
    public static RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    public FollowerFragment() {
        // Required empty public constructor
    }

    public static FollowerFragment newInstance() {
        FollowerFragment fragment = new FollowerFragment();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        applyFonts(view);

        view.findViewById(R.id.img_back).setOnClickListener(this);



        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        List = new ArrayList<>();
        for (int i = 1; i < 20; i++) {
            SearchResultVO check = new SearchResultVO();
            check.Name = "ABILE";
            check.fname = "ABILE smith";
            check.job = "photographer";
            check.image = getResources().getDrawable(R.drawable.image);
            List.add(check);
        }

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        followerAdapter = new FollowerAdapter(List, getActivity());
        recyclerView.setAdapter(followerAdapter);
        return view;
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
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
}
