package com.openfermenter.colorboard;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.util.List;

/**
 * Created by tbeauch on 11/17/17.
 */

public class GameHistory
{
	private static GameHistory instance = null;

	private GameHistoryDB db;

	public String lastAggregate = "n/a";

	protected GameHistory(Context context)
	{
		// Exists only to defeat instantiation.
		db = Room.databaseBuilder(context,
				GameHistoryDB.class, "game-results").build();
	}

	public static GameHistory getInstance(Context context)
	{
		if(instance == null)
		{
			instance = new GameHistory(context);
		}
		return instance;
	}

	public void addEntry(int colors, int rowcols, int numMoves, boolean usedUndo, boolean win)
	{
		ResultDao rd = db.resultDao();
		GameResults gameResults = new GameResults();
		gameResults.setNum_colors(colors);
		gameResults.setRowcols(rowcols);
		gameResults.setNum_moves(numMoves);
		gameResults.setUsed_undo(usedUndo);
		gameResults.setWin(win);
		rd.insertAll(gameResults);
								}

	public String dumpResults()
	{
		StringBuffer sb = new StringBuffer();
		ResultDao rd = db.resultDao();

		List<GameResults> results = rd.getAll();

		sb.append( GameResults.header());
		for (GameResults gr:results)
		{
			sb.append(gr.toString());
		}
		return sb.toString();
	}

	public String dumpAggregate()
	{
		StringBuffer sb = new StringBuffer();
		ResultDao rd = db.resultDao();

		List<BreakDown> breakDowns = rd.getBreakdown();

		sb.append( BreakDown.header());
		for (BreakDown bd: breakDowns)
		{
			sb.append(bd.toString());
		}
		lastAggregate = sb.toString();
		return lastAggregate;
	}
}

@Database(entities = {GameResults.class}, version = 1, exportSchema = false)
abstract class GameHistoryDB extends RoomDatabase
{

	public abstract ResultDao resultDao();
}

@Entity
class GameResults
{
	@PrimaryKey(autoGenerate = true)
	private int gid; // Game ID

	@ColumnInfo(name = "num_colors")
	private int num_colors;

	@ColumnInfo(name = "rowcols")
	private int rowcols;

	@ColumnInfo(name = "num_moves")
	private int num_moves;

	@ColumnInfo(name = "used_undo")
	private Boolean used_undo;

	@ColumnInfo(name = "win")
	private Boolean win;

	public static String header()
	{
		return "gid\trows\tcolors\tundo?\tmoves\twin\n";
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(gid);
		sb.append("\t");
		sb.append(rowcols);
		sb.append("\t");
		sb.append(num_colors);
		sb.append("\t");
		sb.append(used_undo);
		sb.append("\t");
		sb.append(num_moves);
		sb.append("\t");
		sb.append(win);
		sb.append("\n");
		return sb.toString();
	}

	public int getGid()
	{
		return gid;
	}

	public void setGid(int gid)
	{
		this.gid = gid;
	}

	public int getNum_colors()
	{
		return num_colors;
	}

	public void setNum_colors(int num_colors)
	{
		this.num_colors = num_colors;
	}

	public int getRowcols()
	{
		return rowcols;
	}

	public void setRowcols(int rowcols)
	{
		this.rowcols = rowcols;
	}

	public int getNum_moves()
	{
		return num_moves;
	}

	public void setNum_moves(int num_moves)
	{
		this.num_moves = num_moves;
	}

	public Boolean getUsed_undo()
	{
		return used_undo;
	}

	public void setUsed_undo(Boolean used_undo)
	{
		this.used_undo = used_undo;
	}

	public Boolean getWin()
	{
		return win;
	}

	public void setWin(Boolean win)
	{
		this.win = win;
	}
}

@Dao
interface ResultDao {
	@Query("SELECT * FROM GameResults")
	List<GameResults> getAll();

	@Query("SELECT * FROM GameResults WHERE gid IN (:gameIds)")
	List<GameResults> loadAllByIds(int[] gameIds);

	@Query("SELECT * FROM GameResults WHERE num_colors LIKE :num_colors AND "
			+ "num_moves LIKE :num_moves LIMIT 1")
	GameResults findByComplexity(String num_colors, String num_moves);

	@Query("SELECT  win, rowcols, num_colors, count(num_moves) as count, min(num_moves) as min, " +
			"max(num_moves) as max, avg(num_moves) as avg from GameResults group by win, rowcols")
	List<BreakDown> getBreakdown();

	@Insert
	void insertAll(GameResults... result);

	@Delete
	void delete(GameResults result);
}

class BreakDown
{
	boolean win;
	int rowcols;
	int num_colors;
	@ColumnInfo(name="count")
	int count;
	@ColumnInfo(name="min")
	int min;
	@ColumnInfo(name="max")
	int max;
	@ColumnInfo(name="avg")
	int avg;

	public static String header()
	{
		return "win\trows\tcolors\tcount\tmin\tmax\tavg\n";
	}
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(win);
		sb.append("\t");
		sb.append(rowcols);
		sb.append("\t");
		sb.append(num_colors);
		sb.append("\t");
		sb.append(count);
		sb.append("\t");
		sb.append(min);
		sb.append("\t");
		sb.append(max);
		sb.append("\t");
		sb.append(avg);
		sb.append("\n");
		return sb.toString();
	}

}
