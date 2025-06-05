package io.github.milosz275.mazegame;

import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import androidx.annotation.NonNull;

public class Maze {
    public final int size;
    public final Cell[][] grid;

    public Maze(int size) {
        this.size = size;
        grid = new Cell[size][size];
        for (int x = 0; x < size; x++)
            for (int y = 0; y < size; y++)
                grid[x][y] = new Cell(x, y);
        generate();

        grid[0][0].north = false; // entrance gap
        grid[size - 1][size - 1].south = false; // exit gap
    }

    private void generate() {
        Stack<Cell> stack = new Stack<>();
        Cell start = grid[0][0];
        start.visited = true;
        stack.push(start);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            List<Cell> neighbors = new ArrayList<>();
            int x = current.x, y = current.y;
            // collect unvisited neighbors
            if (x > 0 && !grid[x - 1][y].visited)
                neighbors.add(grid[x - 1][y]);
            if (y > 0 && !grid[x][y - 1].visited)
                neighbors.add(grid[x][y - 1]);
            if (x < size - 1 && !grid[x + 1][y].visited)
                neighbors.add(grid[x + 1][y]);
            if (y < size - 1 && !grid[x][y + 1].visited)
                neighbors.add(grid[x][y + 1]);
            if (!neighbors.isEmpty()) {
                Cell next = neighbors.get(new Random().nextInt(neighbors.size()));
                removeWall(current, next);
                next.visited = true;
                stack.push(next);
            } else {
                stack.pop();
            }
        }
    }

    private void removeWall(Cell a, Cell b) {
        if (a.x == b.x) {
            if (a.y < b.y) {
                a.south = false;
                b.north = false;
            } else {
                a.north = false;
                b.south = false;
            }
        } else if (a.y == b.y) {
            if (a.x < b.x) {
                a.east = false;
                b.west = false;
            } else {
                a.west = false;
                b.east = false;
            }
        }
    }

    public String serialize() {
        StringBuilder sb = new StringBuilder();
        sb.append(size).append(";");
        for (int y = 0; y < size; y++)
            for (int x = 0; x < size; x++)
                sb.append(grid[x][y].toString()).append(",");
        return sb.toString();
    }

    public static Maze deserialize(String data) {
        String[] parts = data.split(";");
        int size = Integer.parseInt(parts[0]);
        Maze maze = new Maze(size);
        String[] cells = parts[1].split(",");
        int i = 0;
        for (int y = 0; y < size; y++)
            for (int x = 0; x < size; x++)
                maze.grid[x][y] = Cell.fromString(cells[i++], x, y);
        return maze;
    }

    public static class Cell {
        public final int x, y;
        public boolean north = true, south = true, east = true, west = true, visited = false;

        public Cell(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @NonNull
        public String toString() {
            return (north ? "1" : "0") + (south ? "1" : "0") + (east ? "1" : "0") + (west ? "1" : "0");
        }

        public static Cell fromString(String s, int x, int y) {
            Cell c = new Cell(x, y);
            c.north = s.charAt(0) == '1';
            c.south = s.charAt(1) == '1';
            c.east = s.charAt(2) == '1';
            c.west = s.charAt(3) == '1';
            return c;
        }
    }
}
