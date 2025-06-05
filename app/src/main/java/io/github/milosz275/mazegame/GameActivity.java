package io.github.milosz275.mazegame;

import android.hardware.SensorEventListener;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import java.util.ArrayList;
import android.graphics.Point;

public class GameActivity extends AppCompatActivity implements SensorEventListener {
    private Maze maze;
    private MazeView mazeView;
    private int playerX = 0, playerY = 0;
    private ArrayList<Point> path = new ArrayList<>();
    private SensorManager sensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mazeView = findViewById(R.id.mazeView);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        if (savedInstanceState != null) {
            // restore state from rotation
            playerX = savedInstanceState.getInt("playerX");
            playerY = savedInstanceState.getInt("playerY");
            String mazeData = savedInstanceState.getString("mazeData");
            maze = Maze.deserialize(mazeData);
            int[] pathX = savedInstanceState.getIntArray("pathX");
            int[] pathY = savedInstanceState.getIntArray("pathY");
            path = new ArrayList<>();
            if (pathX != null && pathY != null) {
                for (int i = 0; i < pathX.length; i++) {
                    path.add(new Point(pathX[i], pathY[i]));
                }
            }
        } else {
            // initial launch: load from SharedPreferences
            SharedPreferences prefs = getSharedPreferences("MazeParams", MODE_PRIVATE);
            String mazeName = prefs.getString("selectedMaze", null);

            if (mazeName == null) {
                Toast.makeText(this, "No maze selected!", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            String mazeData = PrefsManager.loadMaze(this, mazeName);
            maze = Maze.deserialize(mazeData);
            // initialize player at start position
            playerX = 0;
            playerY = 0;
            path = new ArrayList<>();
            path.add(new Point(playerX, playerY));
        }

        mazeView.setMaze(maze, playerX, playerY, path);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("playerX", playerX);
        outState.putInt("playerY", playerY);
        outState.putString("mazeData", maze.serialize());
        // save path as a list of x and y coordinates
        int[] pathX = new int[path.size()];
        int[] pathY = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            pathX[i] = path.get(i).x;
            pathY[i] = path.get(i).y;
        }
        outState.putIntArray("pathX", pathX);
        outState.putIntArray("pathY", pathY);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0], y = event.values[1];
        int dx = 0, dy = 0;

        if (Math.abs(x) > Math.abs(y)) {
            // horizontal movement domination
            if (Math.abs(x) > 2)
                dx = x < 0 ? 1 : -1;
        } else {
            // vertical movement domination
            if (Math.abs(y) > 2)
                dy = y > 0 ? 1 : -1;
        }

        movePlayer(dx, dy);
    }

    private void movePlayer(int dx, int dy) {
        int nx = playerX + dx, ny = playerY + dy;
        if (nx < 0 || nx >= maze.size || ny < 0 || ny >= maze.size)
            return;
        Maze.Cell c = maze.grid[playerX][playerY];
        if (dx == 1 && !c.east) {
            playerX++;
        }
        if (dx == -1 && !c.west) {
            playerX--;
        }
        if (dy == 1 && !c.south) {
            playerY++;
        }
        if (dy == -1 && !c.north) {
            playerY--;
        }
        Point newPos = new Point(playerX, playerY);
        if (path.isEmpty() || !path.get(path.size() - 1).equals(newPos)) {
            path.add(newPos);
        }

        mazeView.setMaze(maze, playerX, playerY, path);

        if (playerX == maze.size - 1 && playerY == maze.size - 1) {
            Toast.makeText(this, "You escaped the maze!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
