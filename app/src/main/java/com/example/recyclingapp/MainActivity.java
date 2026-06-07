package com.example.recyclingapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.example.recyclingapp.R;
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
            long loginTime = prefs.getLong("login_timestamp", 0);
            long thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000;

            if (mAuth.getCurrentUser() != null && (System.currentTimeMillis() - loginTime <= thirtyDaysInMillis)) {
                NavOptions navOptions = new NavOptions.Builder()
                        .setPopUpTo(navController.getGraph().getStartDestinationId(), true)
                        .build();

                navController.navigate(R.id.dashboardView, null, navOptions);
            } else {
                mAuth.signOut();
            }

            if (bottomNav != null) {
                NavigationUI.setupWithNavController(bottomNav, navController);

                navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                    if (destination.getId() == R.id.loginView || destination.getId() == R.id.registerView) {
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
