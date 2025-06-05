package io.github.milosz275.mazegame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import java.util.ArrayList;
import java.util.Set;

public class ParametersActivity extends AppCompatActivity {
    private int mazeSize = 20;
    private String selectedMaze = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        SharedPreferences prefs = getSharedPreferences("MazeParams", MODE_PRIVATE);
        mazeSize = prefs.getInt("mazeSize", 20);
        ((EditText) findViewById(R.id.editSize)).setText(String.valueOf(mazeSize));

        Set<String> names = PrefsManager.getMazeNames(this);
        Spinner spinner = findViewById(R.id.spinnerMazes);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                new ArrayList<>(names));
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedMaze = (String) parent.getItemAtPosition(pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedMaze = null;
            }
        });

        findViewById(R.id.btnSaveParams).setOnClickListener(v -> {
            int size = Integer.parseInt(((EditText) findViewById(R.id.editSize)).getText().toString());
            if (size < 10)
                size = 10;
            if (size > 50)
                size = 50;
            SharedPreferences.Editor editor = getSharedPreferences("MazeParams", MODE_PRIVATE).edit();
            editor.putInt("mazeSize", size);
            editor.putString("selectedMaze", selectedMaze);
            editor.apply();
            Toast.makeText(this, "Parameters saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
