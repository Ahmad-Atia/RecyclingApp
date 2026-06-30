package com.example.recyclingapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.recyclingapp.R;
import com.example.recyclingapp.databinding.FragmentOnboardingBinding;

public class OnboardingView extends Fragment {

    private FragmentOnboardingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);

        binding.btnLoslegen.setOnClickListener(v -> {
            SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("is_first_start", false).apply();

            Navigation.findNavController(v).navigate(R.id.action_onboardingView_to_loginView);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}