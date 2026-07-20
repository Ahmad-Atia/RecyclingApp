package com.example.recyclingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.PersistentCacheSettings;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configure Firestore Offline Persistence
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(PersistentCacheSettings.newBuilder()
                        .setSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                        .build())
                .build();
        db.setFirestoreSettings(settings);

        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

            boolean isFirstStart = prefs.getBoolean("is_first_start", true);
            long loginTime = prefs.getLong("login_timestamp", 0);
            long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000;
            long currentTime = System.currentTimeMillis();

            NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_graph);

            if (isFirstStart) {
                navGraph.setStartDestination(R.id.onboardingView);
            } else if (mAuth.getCurrentUser() != null && (currentTime - loginTime <= thirtyDaysInMillis)) {
                navGraph.setStartDestination(R.id.dashboardView);
            } else {
                mAuth.signOut();

                navGraph.setStartDestination(R.id.onboardingView);
            }

            navController.setGraph(navGraph);

            NavigationBarView navView = findViewById(R.id.bottom_navigation);

            if (navView != null) {
                NavigationUI.setupWithNavController(navView, navController);

                final NavigationBarView finalNavView = navView;
                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    int id = destination.getId();
                    
                    // Update start destination dynamically to keep the "Home" button working correctly
                    if (id == R.id.dashboardView) {
                        NavGraph graph = controller.getGraph();
                        if (graph.getStartDestinationId() != R.id.dashboardView) {
                            graph.setStartDestination(R.id.dashboardView);
                        }
                    }

                    if (id == R.id.loginView || id == R.id.registerView || id == R.id.onboardingView) {
                        finalNavView.setVisibility(View.GONE);
                    } else {
                        finalNavView.setVisibility(View.VISIBLE);
                    }
                });

                if (navView instanceof BottomNavigationView) {
                    final BottomNavigationView bNav = (BottomNavigationView) navView;
                    navHostFragment.getChildFragmentManager().registerFragmentLifecycleCallbacks(
                            new FragmentManager.FragmentLifecycleCallbacks() {
                                @Override
                                public void onFragmentViewCreated(@NonNull FragmentManager fm, @NonNull Fragment f,
                                                                   @NonNull View v, @Nullable Bundle savedInstanceState) {
                                    v.post(() -> reserviereBottomNavPlatz(v, bNav));
                                }
                            }, false);
                }
            }
        }
    }

    private void reserviereBottomNavPlatz(View fragmentRoot, BottomNavigationView bottomNav) {
        if (bottomNav.getVisibility() != View.VISIBLE) {
            return;
        }
        if (bottomNav.getHeight() == 0) {
            bottomNav.post(() -> reserviereBottomNavPlatz(fragmentRoot, bottomNav));
            return;
        }

        ViewGroup.MarginLayoutParams navParams = (ViewGroup.MarginLayoutParams) bottomNav.getLayoutParams();
        int puffer = (int) (8 * getResources().getDisplayMetrics().density);
        int platzbedarf = bottomNav.getHeight() + navParams.bottomMargin + puffer;

        fragmentRoot.setPadding(
                fragmentRoot.getPaddingLeft(),
                fragmentRoot.getPaddingTop(),
                fragmentRoot.getPaddingRight(),
                fragmentRoot.getPaddingBottom() + platzbedarf);

        if (fragmentRoot instanceof ViewGroup) {
            ((ViewGroup) fragmentRoot).setClipToPadding(false);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            return navController.navigateUp() || super.onSupportNavigateUp();
        }
        return super.onSupportNavigateUp();
    }
}