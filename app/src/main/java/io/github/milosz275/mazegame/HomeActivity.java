package io.github.milosz275.mazegame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.btnGenerator)
                .setOnClickListener(v -> startActivity(new Intent(this, GeneratorActivity.class)));
        findViewById(R.id.btnParameters)
                .setOnClickListener(v -> startActivity(new Intent(this, ParametersActivity.class)));
        findViewById(R.id.btnGame).setOnClickListener(v -> startActivity(new Intent(this, GameActivity.class)));
    }
}
