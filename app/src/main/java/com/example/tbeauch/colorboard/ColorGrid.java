package com.example.tbeauch.colorboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

/**
 * TODO: document your custom view class.
 */
public class ColorGrid extends View
{
	private TextPaint mTextPaint;
	private float mTextWidth;
	private float mTextHeight;

	private int gridWidth;
	private int gridHeight;

	private float cellHeight;
	private float cellWidth;

	private ArrayList<ArrayList<Cell>> cells;

	private UndoMgr undoMgr = new UndoMgr();


	public ColorGrid(Context context)
	{
		super(context);
		init(null, 0);
	}

	public ColorGrid(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
	}

	public ColorGrid(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void init(AttributeSet attrs, int defStyle)
	{
		gridWidth = this.getWidth();
		gridHeight = this.getHeight();
		cellHeight = gridHeight / MainActivity.mNumRowsCols;
		cellWidth = gridWidth / MainActivity.mNumRowsCols;

		initializeCells();

		undoMgr.clear();
		saveStateForUndo();

		// Set up a default TextPaint object
		mTextPaint = new TextPaint();
		mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Paint.Align.LEFT);

	}

	private void initializeCells()
	{
		cells = new ArrayList<ArrayList<Cell>>();
		// Draw the ColorGrid
		ArrayList<Cell> rowOfCells;
		Cell cell;

		int colorVal;

		for(int row = 0; row < MainActivity.mNumRowsCols; row++)
		{
			rowOfCells = new ArrayList<Cell>();
			cells.add(rowOfCells);

			for(int col = 0; col < MainActivity.mNumRowsCols; col++)
			{
				colorVal = MainActivity.mRand.nextInt(MainActivity.mNumColors);

				cell = new Cell(colorVal);

				rowOfCells.add( cell);
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		// TODO: consider storing these as member variables to reduce
		// allocations per draw cycle.
		int paddingLeft = getPaddingLeft();
		int paddingTop = getPaddingTop();
		int paddingRight = getPaddingRight();
		int paddingBottom = getPaddingBottom();

		int contentWidth = getWidth() - paddingLeft - paddingRight;
		int contentHeight = getHeight() - paddingTop - paddingBottom;

		gridWidth = contentWidth;
		gridHeight = contentHeight;
		cellHeight = gridHeight / MainActivity.mNumRowsCols;
		cellWidth = gridWidth / MainActivity.mNumRowsCols;

		Paint cellPaint = new Paint();
		cellPaint.setColor(Color.RED);

		Cell cell;

		// Draw the ColorGrid
		for(int row = 0; row < MainActivity.mNumRowsCols; row++)
		{
			for(int col = 0; col < MainActivity.mNumRowsCols; col++)
			{
				cell = cells.get(row).get(col) ;

				cellPaint = MainActivity.getPaint( cell.colorVal);

				RectF rect = new RectF(col*cellWidth,row*cellHeight,(col + 1)*cellWidth, (row + 1)*cellHeight);
				canvas.drawRect(rect, cellPaint);
			}
		}
	}


	public int executeChange(int newColor)
	{
		Cell cell;
		cell = cells.get(0).get(0);
		if(cell.colorVal == newColor)
		{
			return 0;
		}

		saveStateForUndo();

		cell.nextColorVal = newColor;
		flagAdjacentCells(0,0,cell.colorVal, newColor);
		int updateCount = updateAllCells(0,0);
		invalidate();

		return updateCount;
	}

	private int flagAdjacentCells(int row, int col, int origColor, int newColor)
	{
		Cell cell = cells.get(row).get(col);

		if(cell.visited )
		{
			return 0;
		}
		cell.visited = true;

		int flagged = 0;
		if(row < (MainActivity.mNumRowsCols - 1))
		{
			cell = cells.get(row + 1).get(col);
			if(cell.colorVal == origColor)
			{
				if(cell.nextColorVal != newColor)
				{
					cell.nextColorVal = newColor;
					flagged++;
				}
				flagged += flagAdjacentCells(row + 1, col, origColor, newColor);
			}
			else
			{
				cell.nextColorVal = cell.colorVal;
			}
		}

		if(col < (MainActivity.mNumRowsCols - 1))
		{
			cell = cells.get(row).get(col + 1);
			if(cell.colorVal == origColor)
			{
				if(cell.nextColorVal != newColor)
				{
					cell.nextColorVal = newColor;
					flagged++;

				}
				flagged += flagAdjacentCells(row, col + 1, origColor, newColor);
			}
			else
			{
				cell.nextColorVal = cell.colorVal;
			}
		}

		if(row > 0)
		{
			cell = cells.get(row - 1).get(col);
			if(cell.colorVal == origColor)
			{
				if(cell.nextColorVal != newColor)
				{
					cell.nextColorVal = newColor;
					flagged++;
				}
				flagged += flagAdjacentCells(row - 1, col, origColor, newColor);
			}
			else
			{
				cell.nextColorVal = cell.colorVal;
			}
		}

		if(col > 0)
		{
			cell = cells.get(row).get(col - 1);
			if(cell.colorVal == origColor)
			{
				if(cell.nextColorVal != newColor)
				{
					cell.nextColorVal = newColor;
					flagged++;
				}
				flagged += flagAdjacentCells(row, col - 1, origColor, newColor);
			}
			else
			{
				cell.nextColorVal = cell.colorVal;
			}
		}
		return flagged;
	}

	private int updateAllCells(int row, int col)
	{
		int updated = 0;
		Cell cell;
		cell = cells.get(row).get(col);

		cell.visited = false;
		if(cell.colorVal != cell.nextColorVal)
		{
			cell.colorVal = cell.nextColorVal;
			updated++;
		}

		if(row < (MainActivity.mNumRowsCols - 1))
		{
			updated += updateAllCells(row + 1, col);
		}

		if(col < (MainActivity.mNumRowsCols - 1))
		{
				updated += updateAllCells(row, col + 1);
		}
		return updated;
	}

	public void resetGrid()
	{
		int colorVal;
		Cell cell;
		undoMgr.clear();

		initializeCells();
		invalidate();
		undoMgr.push(cells);
	}

	public boolean isSolid()
	{
		int homeColor = cells.get(0).get(0).colorVal;
		for(ArrayList<Cell> row: cells)
		{
			for(Cell cell : row)
			{
				if(cell.colorVal != homeColor)
				{
					return false;
				}
			}
		}
		return true;
	}

	public void saveStateForUndo()
	{
		undoMgr.push(cells);
	}

	public boolean isUndoable()
	{
		return (undoMgr.sizeOf() > 1) ? true : false;
	}

	public int Undo()
	{
		if(undoMgr.sizeOf() > 0)
		{
			cells = undoMgr.pop();
			invalidate();
		}
		return undoMgr.sizeOf();
	}

	private class Cell
	{
		int colorVal;
		int nextColorVal;
		boolean visited = false;

		public Cell(int colorVal)
		{
			this.colorVal = colorVal;
			this.nextColorVal = colorVal;
		}

//		public Cell clone()
//		{
//			Cell clonedCell = new Cell(colorVal);
//			clonedCell.nextColorVal = nextColorVal;
//			clonedCell.visited = visited;
//		}

		public Cell(Cell orig)
		{
			this.colorVal = orig.colorVal;
			this.nextColorVal = orig.nextColorVal;
			this.visited = orig.visited;
		}
	}

	private class UndoMgr
	{
		private Stack<ArrayList<ArrayList<Cell>>> undoStack = new Stack<ArrayList<ArrayList<Cell>>>() ;

		public void push(ArrayList<ArrayList<Cell>> state)
		{
			Cell tempCell;
			ArrayList<Cell> newRow;
			ArrayList<ArrayList<Cell>> newState = new ArrayList<ArrayList<Cell>>();
			for(ArrayList<Cell> row: state)
			{
				newRow = new ArrayList<Cell>();
				for(Cell origCell: row)
				{
					tempCell = new Cell(origCell);
					newRow.add(tempCell);
				}
				newState.add(newRow);
			}
			undoStack.push(newState);
		}

		public ArrayList<ArrayList<Cell>> pop()
		{
			return undoStack.pop();
		}

		public int sizeOf()
		{
			return undoStack.size();
		}

		public void clear()
		{
			undoStack.clear();
		}
	}
}
