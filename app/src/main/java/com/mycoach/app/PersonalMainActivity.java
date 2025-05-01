package com.mycoach.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;

public class PersonalMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_main);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                Intent intent = new Intent(PersonalMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        MaterialCardView manageStudentsCard = findViewById(R.id.manageStudentsCard);
        manageStudentsCard.setOnClickListener(v -> {
            Intent intent = new Intent(PersonalMainActivity.this, GerenciarAlunosActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personal_main_menu, menu);
        return true;
    }
}