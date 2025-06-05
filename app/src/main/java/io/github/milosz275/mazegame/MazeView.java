package io.github.milosz275.mazegame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.List;
import android.graphics.Point;
import android.content.res.Configuration;
import androidx.annotation.NonNull;

public class MazeView extends View {
    private Maze maze;
    private int playerX, playerY;
    private List<Point> path;

    public MazeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMaze(Maze maze, int x, int y, List<Point> path) {
        this.maze = maze;
        this.playerX = x;
        this.playerY = y;
        this.path = path;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (maze == null)
            return;
        int cell = Math.min(getWidth(), getHeight()) / maze.size;
        Paint wall = new Paint();
        wall.setColor(isDarkTheme() ? Color.WHITE : Color.BLACK);
        wall.setStrokeWidth(4);
        Paint player = new Paint();
        player.setColor(Color.RED);
        Paint trace = new Paint();
        trace.setColor(Color.BLUE);
        trace.setStrokeWidth(2);
        for (int x = 0; x < maze.size; x++)
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

        // draw path
        if (path != null && path.size() > 1) {
            for (int i = 1; i < path.size(); i++) {
                Point p1 = path.get(i - 1), p2 = path.get(i);
                canvas.drawLine((p1.x + 0.5f) * cell, (p1.y + 0.5f) * cell, (p2.x + 0.5f) * cell, (p2.y + 0.5f) * cell,
                        trace);
            }
        }

        // draw exit cell
        Paint exit = new Paint();
        exit.setColor(Color.GREEN);
        canvas.drawRect(
                (maze.size - 1) * cell,
                (maze.size - 1) * cell,
                maze.size * cell,
                maze.size * cell,
                exit);

        // draw player
        canvas.drawCircle((playerX + 0.5f) * cell, (playerY + 0.5f) * cell, cell / 3, player);
    }

    private boolean isDarkTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }
}
