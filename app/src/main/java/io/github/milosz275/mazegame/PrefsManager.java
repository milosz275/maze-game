package io.github.milosz275.mazegame;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Set;

public class PrefsManager {
    private static final String PREFS_NAME = "MazePrefs";

    public static void saveMaze(Context ctx, String name, String data) {
        SharedPreferences.Editor editor = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(name, data);
        editor.apply();
    }

    public static String loadMaze(Context ctx, String name) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(name, null);
    }

    public static Set<String> getMazeNames(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getAll().keySet();
    }
}
