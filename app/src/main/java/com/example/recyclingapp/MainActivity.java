package com.example.recyclingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

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

            if (bottomNav != null) {
                NavigationUI.setupWithNavController(bottomNav, navController);

                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    if (destination.getId() == R.id.loginView ||
                            destination.getId() == R.id.registerView ||
                            destination.getId() == R.id.onboardingView) {
                        bottomNav.setVisibility(View.GONE);
                    } else {
                        bottomNav.setVisibility(View.VISIBLE);
                    }
                });
            }
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