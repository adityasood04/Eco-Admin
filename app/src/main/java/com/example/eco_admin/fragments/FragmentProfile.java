package com.example.eco_admin.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.eco_admin.ActivityLogin;
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
        binding.btnLogout.setOnClickListener(view -> {
            confirmLogout();
        });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out of the app?")
                .setPositiveButton("Logout", (dialogInterface, i) -> logout())
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                .setCancelable(true)
                .show();
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

    private void logout() {
        SharedPreferences.Editor editor = getActivity()
                .getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                .edit();
        editor.remove("NGO");
        editor.apply();
        SharedPreferences.Editor editor2 = getActivity()
                .getSharedPreferences("EVENTS_PREFS", MODE_PRIVATE)
                .edit();
        editor2.putBoolean("IS_EVENT_LIVE", false);
        editor2.remove("LIVE_EVENT_ID");
        editor2.apply();
        Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), ActivityLogin.class));
        getActivity().finish();
    }
}