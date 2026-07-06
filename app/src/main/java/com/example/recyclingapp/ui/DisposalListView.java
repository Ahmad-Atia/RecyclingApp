package com.example.recyclingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recyclingapp.R;
import com.example.recyclingapp.controllers.ScanController;
import com.example.recyclingapp.models.DisposalPoint;
import com.example.recyclingapp.models.DisposalPointsManager;
import java.util.ArrayList;
import java.util.List;

public class DisposalListView extends Fragment {
    private DisposalAdapter adapter;
    private ProgressBar loadingSpinner;
    private ScanController scanController;
    private List<DisposalPoint> pointsList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disposal_list, container, false);
        
        RecyclerView recyclerView = view.findViewById(R.id.disposalRecyclerView);
        loadingSpinner = view.findViewById(R.id.loadingSpinner);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DisposalAdapter(pointsList);
        recyclerView.setAdapter(adapter);
        
        scanController = new ScanController();
        loadData();
        
        return view;
    }

    private void loadData() {
        loadingSpinner.setVisibility(View.VISIBLE);
        // Using Dortmund coordinates for testing
        scanController.getDisposalPoints(51.5136, 7.4653, new DisposalPointsManager.PointsCallback() {
            @Override
            public void onSuccess(List<DisposalPoint> points) {
                if (getContext() == null || getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
                    if (points != null) {
                        pointsList.clear();
                        pointsList.addAll(points);
                        if (adapter != null) adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getContext() == null || getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (loadingSpinner != null) loadingSpinner.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
