package com.mycoach.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class GerenciarTreinosActivity extends AppCompatActivity {

    private Spinner studentSpinner;
    private TextInputEditText workoutNameEditText;
    private TextInputEditText workoutDescriptionEditText;
    private TextInputEditText workoutDateEditText;
    private Button addWorkoutButton;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private List<Aluno> alunoList;
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_treinos);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        studentSpinner = findViewById(R.id.studentSpinner);
        workoutNameEditText = findViewById(R.id.workoutNameEditText);
        workoutDescriptionEditText = findViewById(R.id.workoutDescriptionEditText);
        workoutDateEditText = findViewById(R.id.workoutDateEditText);
        addWorkoutButton = findViewById(R.id.addWorkoutButton);
        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        loadStudents();

        addWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String workoutName = workoutNameEditText.getText().toString().trim();
                String description = workoutDescriptionEditText.getText().toString().trim();
                String date = workoutDateEditText.getText().toString().trim();
                int selectedPosition = studentSpinner.getSelectedItemPosition();

                if (selectedPosition == 0) {
                    Toast.makeText(GerenciarTreinosActivity.this, "Selecione um aluno", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (workoutName.isEmpty() || description.isEmpty() || date.isEmpty()) {
                    Toast.makeText(GerenciarTreinosActivity.this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                int studentId = alunoList.get(selectedPosition - 1).getId();
                long result = bancoDeDadosHelper.adicionarTreino(studentId, workoutName, description, date);
                if (result != -1) {
                    Toast.makeText(GerenciarTreinosActivity.this, "Treino adicionado com sucesso", Toast.LENGTH_SHORT).show();
                    workoutNameEditText.setText("");
                    workoutDescriptionEditText.setText("");
                    workoutDateEditText.setText("");
                    studentSpinner.setSelection(0);
                } else {
                    Toast.makeText(GerenciarTreinosActivity.this, "Erro ao adicionar treino", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadStudents() {
        alunoList = bancoDeDadosHelper.obterTodosAlunos();
        List<String> studentNames = new ArrayList<>();
        studentNames.add("Selecione um aluno");
        for (Aluno aluno : alunoList) {
            studentNames.add(aluno.getNome());
        }

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, studentNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        studentSpinner.setAdapter(spinnerAdapter);
    }
}