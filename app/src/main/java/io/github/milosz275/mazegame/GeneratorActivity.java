package io.github.milosz275.mazegame;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.res.Configuration;

public class GeneratorActivity extends AppCompatActivity {
    private int mazeSize = 20;
    private TextView txtSize;
    private Maze maze;
    private ImageView imgMaze;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generator);
        txtSize = findViewById(R.id.txtSize);
        imgMaze = findViewById(R.id.imgMaze);

        SharedPreferences prefs = getSharedPreferences("MazeParams", MODE_PRIVATE);
        mazeSize = prefs.getInt("mazeSize", 20);
        ((TextView) findViewById(R.id.txtSize)).setText("Size: " + mazeSize);

        findViewById(R.id.btnGenerate).setOnClickListener(v -> {
            maze = new Maze(mazeSize);

            maze.grid[0][0].south = false; // entrance at top-left
            maze.grid[maze.size - 1][maze.size - 1].north = false; // exit at bottom-right

            imgMaze.setImageBitmap(drawMaze(maze));
        });

        findViewById(R.id.btnParameters)
                .setOnClickListener(v -> startActivity(new Intent(this, ParametersActivity.class)));

        findViewById(R.id.btnSave).setOnClickListener(v -> {
            String name = ((EditText) findViewById(R.id.editMazeName)).getText().toString();
            if (maze != null && !name.isEmpty()) {
                PrefsManager.saveMaze(this, name, maze.serialize());

                SharedPreferences.Editor editor = getSharedPreferences("MazeParams", MODE_PRIVATE).edit();
                editor.putString("selectedMaze", name);
                editor.apply();

                Toast.makeText(this, "Maze saved and selected!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMazeSizeDisplay();
    }

    private Bitmap drawMaze(Maze maze) {
        int cell = 20;
        int size = maze.size * cell;
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);
        Paint wall = new Paint();
        wall.setColor(isDarkTheme() ? Color.WHITE : Color.BLACK);
        wall.setStrokeWidth(4);

        for (int x = 0; x < maze.size; x++) {
            for (int y = 0; y < maze.size; y++) {
                int left = x * cell, top = y * cell, right = left + cell, bottom = top + cell;
                Maze.Cell c = maze.grid[x][y];
                if (c.north)
                    canvas.drawLine(left, top, right, top, wall);
                if (c.south)
                    canvas.drawLine(left, bottom, right, bottom, wall);
                if (c.west)
                    canvas.drawLine(left, top, left, bottom, wall);
                if (c.east)
                    canvas.drawLine(right, top, right, bottom, wall);
            }
        }
        return bmp;
    }

    private void updateMazeSizeDisplay() {
        SharedPreferences prefs = getSharedPreferences("MazeParams", MODE_PRIVATE);
        mazeSize = prefs.getInt("mazeSize", 20);
        txtSize.setText("Size: " + mazeSize);
    }

    private boolean isDarkTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
}
