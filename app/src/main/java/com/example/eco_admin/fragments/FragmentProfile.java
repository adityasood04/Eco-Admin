package com.example.eco_admin.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eco_admin.R;
import com.example.eco_admin.databinding.FragmentProfileBinding;
import com.example.eco_admin.models.NGO;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class FragmentProfile extends Fragment {
    NGO ngo;
    FirebaseFirestore db;
    private FragmentProfileBinding binding;
    private Context context;

    public FragmentProfile() {
        // Required empty public constructor
    }

    public FragmentProfile(Context context) {
        // Required empty public constructor
        this.context = context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (binding == null) {
            binding = FragmentProfileBinding.inflate(inflater);
//            Methods.setStatusBarColor(requireActivity().getColor(R.color.colorStatusBar), (AppCompatActivity) requireActivity());
            loadData();
            loadUI();
            setListeners();
        }
        return binding.getRoot();
    }

    private void setListeners() {

    }

    private void loadUI() {
        binding.tvName.setText(ngo.getName());
        binding.tvAddress.setText(ngo.getAddress());
        binding.tvContactNo.setText(String.valueOf(ngo.getContactNo()));
        binding.tvWebsite.setText(ngo.getWebsite());
        binding.tvEmail.setText(ngo.getEmail());
        binding.tvRegistrationNo.setText(ngo.getRegistrationNo());
    }

    private void loadData() {
        ngo = new Gson().fromJson(context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE).getString("NGO", ""), NGO.class);
    }
}