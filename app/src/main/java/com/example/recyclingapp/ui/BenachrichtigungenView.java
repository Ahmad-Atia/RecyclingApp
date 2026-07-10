package com.example.recyclingapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.recyclingapp.controllers.NotificationController;
import com.example.recyclingapp.databinding.FragmentBenachrichtigungenBinding;
import com.example.recyclingapp.models.AppNotification;
import com.example.recyclingapp.ui.adapters.NotificationAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class BenachrichtigungenView extends Fragment {

    private FragmentBenachrichtigungenBinding binding;
    private NotificationController notificationController;
    private NotificationAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBenachrichtigungenBinding.inflate(inflater, container, false);
        notificationController = new NotificationController();

        binding.ivBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        setupRecyclerView();
        loadNotifications();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(new ArrayList<>());
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        String uid = FirebaseAuth.getInstance().getUid();
        if (uid != null) {
            notificationController.fetchNotifications(uid, list -> {
                if (isAdded()) {
                    adapter.updateList(list);
                    notificationController.markAllAsRead(uid);
                }
            }, e -> {
                // error
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
