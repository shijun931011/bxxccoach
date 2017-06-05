package com.jgkj.bxxccoach.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jgkj.bxxccoach.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BxxcFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BxxcFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BxxcFragment extends Fragment {



    public BxxcFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bxxc, container, false);
    }







}
