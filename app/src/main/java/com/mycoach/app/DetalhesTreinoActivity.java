package com.mycoach.app;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import android.widget.TextView;

import java.util.List;

public class DetalhesTreinoActivity extends AppCompatActivity {

    private RecyclerView exerciciosRecyclerView;
    private ExercicioAdapter exercicioAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_treino);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        TextView nomeTextView = findViewById(R.id.detalheTreinoNome);
        TextView diaSemanaTextView = findViewById(R.id.detalheTreinoDiaSemana);
        TextView observacaoTextView = findViewById(R.id.detalheTreinoObservacao);

        int treinoId = getIntent().getIntExtra("treino_id", -1);
        int alunoId = getIntent().getIntExtra("aluno_id", -1);
        String treinoNome = getIntent().getStringExtra("treino_nome");
        String treinoObservacao = getIntent().getStringExtra("treino_observacao");
        String treinoDiaSemana = getIntent().getStringExtra("treino_dia_semana");

        Log.d("DetalhesTreinoActivity", "Dados recebidos - treinoId: " + treinoId + ", alunoId: " + alunoId + ", nome: " + treinoNome + ", dia: " + treinoDiaSemana);

        if (treinoId != -1 && alunoId != -1 && treinoNome != null && treinoDiaSemana != null) {
            nomeTextView.setText(treinoNome);
            diaSemanaTextView.setText("Dia: " + treinoDiaSemana);
            observacaoTextView.setText(treinoObservacao != null ? "Observação: " + treinoObservacao : "Sem observação");

            exerciciosRecyclerView = findViewById(R.id.exerciciosRecyclerView);
            exerciciosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            exercicioAdapter = new ExercicioAdapter();
            exerciciosRecyclerView.setAdapter(exercicioAdapter);

            BancoDeDadosHelper db = new BancoDeDadosHelper(this);
            List<Treino> treinos = db.obterTreinosPorAlunoId(alunoId);
            Treino treinoExibido = null;
            for (Treino treino : treinos) {
                if (treino.getId() == treinoId) {
                    treinoExibido = treino;
                    break;
                }
            }

            if (treinoExibido != null) {
                Log.d("DetalhesTreinoActivity", "Treino encontrado - ID: " + treinoExibido.getId() + ", Exercícios: " + treinoExibido.getExercicios().size());
                for (Exercicio ex : treinoExibido.getExercicios()) {
                    Log.d("DetalhesTreinoActivity", "Exercício - Nome: " + ex.getNome() + ", Séries: " + ex.getSeries().size());
                }
                exercicioAdapter.setExercicios(treinoExibido.getExercicios());
            } else {
                Log.d("DetalhesTreinoActivity", "Treino não encontrado para ID: " + treinoId);
                nomeTextView.setText("Treino não encontrado");
            }
        } else {
            Log.d("DetalhesTreinoActivity", "Dados insuficientes para exibir o treino");
            finish();
        }
    }
}