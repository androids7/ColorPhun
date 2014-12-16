package scoreHandlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import scoreHandlers.ScoreModels.CPScore;

public class DBHandler extends SQLiteOpenHelper {

    private final String SCOREBOARD_TABLE = "tblScoreBoard";

    public DBHandler(Context context) {
        super(context, "cpScoreBoard", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "CREATE TABLE " + SCOREBOARD_TABLE + " (" +
                "scoreIndex INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "player TEXT, score INTEGER, level INTEGER);";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SCOREBOARD_TABLE);
        onCreate(sqLiteDatabase);
    }

    // clears scores
    public boolean purgeScores() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + SCOREBOARD_TABLE);
        db.close();
        return true;
    }

    public boolean updateScore(CPScore oldScore, CPScore newScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("player", newScore.getPlayer());
        contentValues.put("score", newScore.getScore());
        contentValues.put("level", newScore.getLevel());

        db.update(SCOREBOARD_TABLE, contentValues, "score=" + oldScore.getScore(), null);
        db.close();
        return true;
    }

    // inserts a score in the database
    public boolean insertScore (CPScore score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("player", score.getPlayer());
        contentValues.put("score", score.getScore());
        contentValues.put("level", score.getLevel());

        db.insert(SCOREBOARD_TABLE, null, contentValues);
        db.close();
        return true;
    }

    // returns a boolean indicating where the score exists in DB
    public boolean hasScore(CPScore score) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " +  SCOREBOARD_TABLE +
                                      " WHERE score = " + score.getScore(), null);
        boolean isPresent = cursor.getCount() > 0;
        db.close();
        return isPresent;
    }

    // returns an array list of score records
    public ArrayList<CPScore> getScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CPScore> scoresList = null;
        Cursor cursor =  db.rawQuery("SELECT * FROM " + SCOREBOARD_TABLE +
                                        " ORDER BY score DESC", null);

        if (cursor.moveToFirst()) {
            scoresList = new ArrayList<CPScore>(cursor.getCount());
            do {
                String player = cursor.getString(cursor.getColumnIndex("player"));
                int points = cursor.getInt(cursor.getColumnIndex("score"));
                int level = cursor.getInt(cursor.getColumnIndex("level"));

                CPScore score = new CPScore(player, points, level);
                scoresList.add(score);
            } while (cursor.moveToNext());
        }

        db.close();
        return scoresList;
    }
}