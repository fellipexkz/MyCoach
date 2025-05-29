package com.mycoach.app;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class RestTimeBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_POSITION = "position";
    private static final String ARG_EXERCISE_NAME = "exercise_name";
    private static final String ARG_CURRENT_REST_TIME = "current_rest_time";

    private int adapterPosition;
    private String exerciseName;
    private ExercicioFormAdapter adapter;
    private List<String> restTimeOptions;
    private int selectedPosition = 0;

    public static RestTimeBottomSheetFragment newInstance(int position, String exerciseName, String currentRestTime, ExercicioFormAdapter adapter) {
        RestTimeBottomSheetFragment fragment = new RestTimeBottomSheetFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putString(ARG_EXERCISE_NAME, exerciseName);
        args.putString(ARG_CURRENT_REST_TIME, currentRestTime);
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
        String currentRestTimeValue = null;

        if (getArguments() != null) {
            adapterPosition = getArguments().getInt(ARG_POSITION);
            exerciseName = getArguments().getString(ARG_EXERCISE_NAME);
            currentRestTimeValue = getArguments().getString(ARG_CURRENT_REST_TIME);
        }
        restTimeOptions = getRestTimeOptions();

        int initialIndex = -1;

        if (currentRestTimeValue != null && !currentRestTimeValue.isEmpty()) {
            initialIndex = restTimeOptions.indexOf(currentRestTimeValue);
            Log.d("RestTimePicker", "onCreate - Attempting to use currentRestTimeValue: '" + currentRestTimeValue + "', found at index: " + initialIndex);
        }

        if (initialIndex == -1) {
            String hardcodedDefaultRestTime = "1min 0s";
            initialIndex = restTimeOptions.indexOf(hardcodedDefaultRestTime);
            Log.d("RestTimePicker", "onCreate - currentRestTimeValue not used. Attempting hardcoded default: '" + hardcodedDefaultRestTime + "', found at index: " + initialIndex);
        }

        if (initialIndex == -1) {
            selectedPosition = 0;
            Log.w("RestTimePicker", "onCreate - No valid initial index found (neither current nor hardcoded default). Defaulting to 0.");
        } else {
            selectedPosition = initialIndex;
        }

        Log.d("RestTimePicker", "onCreate - Final initial selectedPosition in fragment: " + selectedPosition);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rest_time_bottom_sheet, container, false);

        TextView subtitleTextView = view.findViewById(R.id.subtitleTextView);
        final WheelPicker wheelPicker = view.findViewById(R.id.restTimeWheelPicker);
        MaterialButton confirmButton = view.findViewById(R.id.confirmButton);

        String subtitleText;
        if (exerciseName != null && !exerciseName.isEmpty()) {
            subtitleText = exerciseName;
        } else {
            subtitleText = getString(R.string.exercise_fallback_format, adapterPosition + 1);
        }
        subtitleTextView.setText(subtitleText);

        wheelPicker.setData(restTimeOptions);

        wheelPicker.setOnItemSelectedListener((picker, data, positionFromEvent) -> {
            int actualCurrentPosition = picker.getCurrentItemPosition();
            Log.d("RestTimePicker", "Listener - positionFromEvent: " + positionFromEvent + ", actualCurrentPosition: " + actualCurrentPosition + ", data: " + data);
            this.selectedPosition = actualCurrentPosition;
        });

        wheelPicker.post(() -> {
            int intendedInitialPosition = this.selectedPosition;
            Log.d("RestTimePicker", "Inside post - Attempting to set picker to intendedInitialPosition: " + intendedInitialPosition);

            if (intendedInitialPosition >= 0 && intendedInitialPosition < restTimeOptions.size()) {
                wheelPicker.setSelectedItemPosition(intendedInitialPosition);
                int reportedPositionAfterSet = wheelPicker.getCurrentItemPosition();
                Log.d("RestTimePicker", "Inside post - After setSelectedItemPosition, picker.getCurrentItemPosition() reports: " + reportedPositionAfterSet + ". Fragment's selectedPosition remains: " + this.selectedPosition);

            } else {
                Log.e("RestTimePicker", "Inside post - intendedInitialPosition is out of bounds: " + intendedInitialPosition + ". Setting picker to 0.");
                wheelPicker.setSelectedItemPosition(0);
                this.selectedPosition = 0;
            }
        });

        confirmButton.setOnClickListener(v -> {
            Log.d("RestTimePicker", "Confirm button clicked. Using selectedPosition: " + this.selectedPosition);
            if (adapter != null && adapterPosition != RecyclerView.NO_POSITION) {
                if (this.selectedPosition >= 0 && this.selectedPosition < restTimeOptions.size()) {
                    String selectedTime = restTimeOptions.get(this.selectedPosition);
                    adapter.updateRestTime(adapterPosition, selectedTime);
                } else {
                    Log.e("RestTimePicker", "Confirm button - Invalid selectedPosition: " + this.selectedPosition);
                }
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            try (TypedArray a = dialog.getContext().getTheme().obtainStyledAttributes(new int[]{com.google.android.material.R.attr.colorSurface})) {
                int surfaceColor = a.getColor(0, ContextCompat.getColor(dialog.getContext(), R.color.surface));
                dialog.getWindow().setNavigationBarColor(surfaceColor);
            }
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (getActivity() != null && getActivity().getWindow() != null) {
            int backgroundColor = ContextCompat.getColor(getActivity(), R.color.background);
            getActivity().getWindow().setNavigationBarColor(backgroundColor);
        }
    }

    private List<String> getRestTimeOptions() {
        List<String> options = new ArrayList<>();
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