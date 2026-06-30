package com.example.recyclingapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.recyclingapp.databinding.FragmentEmailVerificationBinding;
import com.example.recyclingapp.R;

public class EmailVerificationView extends Fragment {

    private FragmentEmailVerificationBinding binding;
    private FirebaseAuth auth;
    private Handler checkEmailHandler;
    private Runnable checkEmailRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEmailVerificationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        }

        auth = FirebaseAuth.getInstance();

        SharedPreferences prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        String email = prefs.getString("registered_email", "deine E-Mail-Adresse");

        binding.tvMessage.setText("Eine Bestätigungs-E-Mail wurde an " + email + " gesendet.\n\nBitte klicke auf den Link in der E-Mail, um deinen Account freizuschalten.");

        checkEmailHandler = new Handler(Looper.getMainLooper());
        checkEmailRunnable = new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    user.reload().addOnCompleteListener(task -> {
                        if (!isAdded() || getContext() == null || binding == null) {
                            return;
                        }

                        if (task.isSuccessful()) {
                            FirebaseUser freshUser = FirebaseAuth.getInstance().getCurrentUser();

                            if (freshUser != null && freshUser.isEmailVerified()) {
                                stopCheckingEmail();

                                SharedPreferences localPrefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
                                String name = localPrefs.getString("registered_name", "Neuer User");
                                String userId = freshUser.getUid();

                                java.util.Map<String, Object> userData = new java.util.HashMap<>();
                                userData.put("name", name);
                                userData.put("address", "");
                                userData.put("level", 1);
                                userData.put("co2", 0.0);

                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(userId)
                                        .set(userData)
                                        .addOnSuccessListener(aVoid -> {
                                            if (isAdded() && getContext() != null) {
                                                Toast.makeText(getContext(), "Konto erfolgreich verifiziert!", Toast.LENGTH_SHORT).show();
                                                Navigation.findNavController(view).navigate(R.id.action_emailVerificationView_to_dashboardView);
                                            }
                                        })
                                        .addOnFailureListener(dbError -> {
                                            if (isAdded() && getContext() != null) {
                                                Toast.makeText(getContext(), "Firestore-Fehler: " + dbError.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                                Navigation.findNavController(view).navigate(R.id.action_emailVerificationView_to_dashboardView);
                                            }
                                        });
                                return;
                            }
                        }
                        checkEmailHandler.postDelayed(checkEmailRunnable, 3000);
                    });
                }
            }
        };

        checkEmailHandler.post(checkEmailRunnable);
    }

    private void stopCheckingEmail() {
        if (checkEmailHandler != null && checkEmailRunnable != null) {
            checkEmailHandler.removeCallbacks(checkEmailRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopCheckingEmail();

        if (getActivity() != null) {
            View bottomNav = getActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.VISIBLE);
            }
        }

        binding = null;
    }
}
