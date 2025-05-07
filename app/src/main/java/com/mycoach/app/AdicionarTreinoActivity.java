package com.mycoach.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdicionarTreinoActivity extends AppCompatActivity {

    private TextInputEditText nomeEditText, descricaoEditText, dataEditText;
    private MaterialButton addButton;
    private BancoDeDadosHelper bancoDeDadosHelper;
    private DataFirebase dbfire = new DataFirebase();
    private int alunoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_treino);

        bancoDeDadosHelper = new BancoDeDadosHelper(this);

        alunoId = getIntent().getIntExtra("aluno_id", -1);
        if (alunoId == -1) {
            finish();
            return;
        }

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        nomeEditText = findViewById(R.id.workoutNameEditText);
        descricaoEditText = findViewById(R.id.workoutDescriptionEditText);
        dataEditText = findViewById(R.id.workoutDateEditText);
        addButton = findViewById(R.id.addButton);

        Log.d("IdDoAluno", "Aluno Id: "+alunoId);

        dataEditText.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AdicionarTreinoActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dataEditText.setText(dateFormat.format(selectedDate.getTime()));
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        addButton.setOnClickListener(v -> {
            String nome = nomeEditText.getText().toString().trim();
            String descricao = descricaoEditText.getText().toString().trim();
            String data = dataEditText.getText().toString().trim();

            if (nome.isEmpty() || data.isEmpty()) {
                Toast.makeText(AdicionarTreinoActivity.this, "Por favor, preencha os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean sucesso = bancoDeDadosHelper.adicionarTreino(alunoId, nome, descricao, data);
            if (sucesso) {
                Toast.makeText(AdicionarTreinoActivity.this, "Treino adicionado com sucesso", Toast.LENGTH_SHORT).show();

                // Enviar para o Firebase
                Treino treino = new Treino();
                treino.setData(data);
                treino.setNome(nome);
                treino.setDescricao(descricao);
                treino.setAlunoId(alunoId);
                Log.d("Aluno ID", String.valueOf(alunoId));


                dbfire.sendFirebaseTreino(treino, "treinos", bancoDeDadosHelper);
                dbfire.syncWithFirebaseTreino(bancoDeDadosHelper, "treinos");
                finish();
            } else {
                Toast.makeText(AdicionarTreinoActivity.this, "Erro ao adicionar treino", Toast.LENGTH_SHORT).show();
            }
        });
    }
}