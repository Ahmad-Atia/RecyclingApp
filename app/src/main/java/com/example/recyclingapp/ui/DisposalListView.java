package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.R;
import com.example.recyclingapp.controllers.DisposalController;
import com.example.recyclingapp.models.DisposalPoint;
import com.example.recyclingapp.models.DisposalPointsManager;
import com.example.recyclingapp.utils.NetworkUtils;
import java.util.ArrayList;
import java.util.List;

public class DisposalListView extends Fragment {
    private DisposalAdapter adapter;
    private ProgressBar loadingSpinner;
    private TextView emptyStateTextView;
    private DisposalController disposalController;
    private List<DisposalPoint> pointsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disposal_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.disposalRecyclerView);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        emptyStateTextView = view.findViewById(R.id.emptyStateTextView);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DisposalAdapter(pointsList);
        recyclerView.setAdapter(adapter);

        disposalController = new DisposalController(requireContext());
        loadData();

        return view;
    }

    private void loadData() {
        if (!NetworkUtils.isOnline(requireContext())) {
            Toast.makeText(getContext(), "Keine Internetverbindung. Standorte können nicht geladen werden.", Toast.LENGTH_LONG).show();
            showEmptyState("Keine Internetverbindung.");
            return;
        }
        
        showLoading(true);

        disposalController.fetchDisposalPointsForCurrentUser(new DisposalPointsManager.PointsCallback() {
            @Override
            public void onSuccess(List<DisposalPoint> points) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        if (points != null && !points.isEmpty()) {
                            pointsList.clear();
                            pointsList.addAll(points);
                            adapter.notifyDataSetChanged();
                            emptyStateTextView.setVisibility(View.GONE);
                        } else {
                            showEmptyState("Keine Entsorgungsstellen in der Nähe gefunden.");
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showLoading(false);
                        showEmptyState(error);
                    });
                }
            }
        });
    }

    private void showLoading(boolean loading) {
        if (loadingSpinner != null) {
            loadingSpinner.setVisibility(loading ? View.VISIBLE : View.GONE);
        }
        if (loading) {
            emptyStateTextView.setVisibility(View.GONE);
        }
    }

    private void showEmptyState(String message) {
        if (emptyStateTextView != null) {
            emptyStateTextView.setText(message);
            emptyStateTextView.setVisibility(View.VISIBLE);
        }
        pointsList.clear();
        adapter.notifyDataSetChanged();
    }
}
