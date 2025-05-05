package com.mycoach.app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import android.widget.TextView;

public class DetalhesTreinoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_treino);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        TextView nomeTextView = findViewById(R.id.detalheTreinoNome);
        TextView dataTextView = findViewById(R.id.detalheTreinoData);
        TextView descricaoTextView = findViewById(R.id.detalheTreinoDescricao);

        int treinoId = getIntent().getIntExtra("treino_id", -1);
        String treinoNome = getIntent().getStringExtra("treino_nome");
        String treinoDescricao = getIntent().getStringExtra("treino_descricao");
        String treinoData = getIntent().getStringExtra("treino_data");

        if (treinoId != -1 && treinoNome != null && treinoDescricao != null && treinoData != null) {
            nomeTextView.setText(treinoNome);
            dataTextView.setText(treinoData);
            descricaoTextView.setText(treinoDescricao);
        }
    }
}