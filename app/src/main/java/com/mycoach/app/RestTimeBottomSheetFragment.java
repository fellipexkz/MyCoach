package com.mycoach.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class RestTimeBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_POSITION = "position";
    private static final String ARG_EXERCISE_NAME = "exercise_name";
    private int adapterPosition;
    private String exerciseName;
    private ExercicioFormAdapter adapter;
    private List<String> restTimeOptions;
    private int selectedPosition = 12;

    public static RestTimeBottomSheetFragment newInstance(int position, String exerciseName, ExercicioFormAdapter adapter) {
        RestTimeBottomSheetFragment fragment = new RestTimeBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putString(ARG_EXERCISE_NAME, exerciseName);
        fragment.setArguments(args);
        fragment.setAdapter(adapter);
        return fragment;
    }

    private void setAdapter(ExercicioFormAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            adapterPosition = getArguments().getInt(ARG_POSITION);
            exerciseName = getArguments().getString(ARG_EXERCISE_NAME);
        }
        restTimeOptions = getRestTimeOptions();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rest_time_bottom_sheet, container, false);

        TextView subtitleTextView = view.findViewById(R.id.subtitleTextView);
        WheelPicker wheelPicker = view.findViewById(R.id.restTimeWheelPicker);
        MaterialButton confirmButton = view.findViewById(R.id.confirmButton);

        subtitleTextView.setText(exerciseName != null ? exerciseName : "Exercício " + (adapterPosition + 1));

        wheelPicker.setData(restTimeOptions);
        wheelPicker.setSelectedItemPosition(selectedPosition);

        wheelPicker.setOnItemSelectedListener((picker, data, position) -> selectedPosition = position);

        confirmButton.setOnClickListener(v -> {
            if (adapter != null && adapterPosition != RecyclerView.NO_POSITION) {
                String selectedTime = restTimeOptions.get(selectedPosition);
                adapter.updateRestTime(adapterPosition, selectedTime);
            }
            dismiss();
        });

        return view;
    }

    private List<String> getRestTimeOptions() {
        List<String> options = new ArrayList<>();
        options.add("Desativado");
        for (int seconds = 5; seconds <= 55; seconds += 5) {
            options.add(seconds + "s");
        }
        for (int totalSeconds = 60; totalSeconds <= 300; totalSeconds += 5) {
            int minutes = totalSeconds / 60;
            int remainingSeconds = totalSeconds % 60;
            options.add(minutes + "min " + remainingSeconds + "s");
        }
        return options;
    }
}