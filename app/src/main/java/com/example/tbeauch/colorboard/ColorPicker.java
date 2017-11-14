package com.example.tbeauch.colorboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * TODO: document your custom view class.
 */
public class ColorPicker extends View
{
	private TextPaint mTextPaint;
	private float mTextWidth;
	private float mTextHeight;
	private float pickerWidth;
	private float pickerHeight;
	private float pickerCellWidth;
	private float pickerCellHeight;
	private float pickerStartX;
	private int numColors = 6;

	public ColorPicker(Context context)
	{
		super(context);
		init(null, 0);
	}

	public ColorPicker(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(attrs, 0);
	}

	public ColorPicker(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle)
	{

		// Set up a default TextPaint object
		mTextPaint = new TextPaint();
		mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Paint.Align.LEFT);

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

		pickerWidth = contentWidth;
		pickerHeight = contentHeight;
		pickerCellHeight = pickerHeight ;
		pickerCellWidth = pickerWidth / numColors;

		Paint cellPaint = new Paint();
		cellPaint.setColor(Color.RED);

		// Draw the ColorGrid
		for(int col = 0; col < numColors; col++)
		{
			cellPaint = MainActivity.getPaint(col);
			RectF rect = new RectF(col*pickerCellWidth,0 ,(col + 1)*pickerCellWidth, pickerCellHeight);
			canvas.drawRoundRect(rect,pickerCellWidth/2, pickerCellWidth/2, cellPaint);
		}

	}



	public int getChosenColor(float x, float y)
	{
		int index;
		index = (int)Math.floor((double) (x / pickerCellWidth));
		return index;
	}
}
