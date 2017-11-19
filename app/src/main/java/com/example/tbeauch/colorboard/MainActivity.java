package com.example.tbeauch.colorboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity
{

	public static int mNumColors = 7 ;
	public static int mMaxMoves = 22;
	public static int mNumRowsCols = 8;
	private boolean mUsedUndo = false;
	public boolean mShowUndo = false;
	public boolean mShouldRandomize = false;

	public static Random mRand = new Random(System.currentTimeMillis());

	static Paint[] paints;

	private int moves = 0;

	static public int getColorVal(int color)
	{
		int returnVal = 0;
		return returnVal;
	}

	private void storeResults(final boolean win)
	{
		final GameHistory gh = GameHistory.getInstance(getApplicationContext());

		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params)
			{
				gh.addEntry(mNumColors,mNumRowsCols, moves,mUsedUndo,win);
				return gh.dumpResults();
			}

			@Override
			protected void onPostExecute(String result)
			{
					Log.d("RESULTS", result);
			}
		}.execute();

	}

	private void resetGame()
	{
		if(mShouldRandomize)
		{
			mNumRowsCols = 5 + MainActivity.mRand.nextInt(8);
			mNumColors = 1 + (mNumRowsCols + 1) / 2;
		}
		else
		{
			mNumRowsCols = 10;
			mNumColors = 6;
		}

		createColors();
		setMaxMoves();
		View pickerView = findViewById(R.id.colorPickerView);
		ColorGrid gridView = (ColorGrid) findViewById(R.id.ColorGridView);
		pickerView.invalidate();
		gridView.resetGrid();
		mUsedUndo = false;
		moves = 0;
		updateMovesText();
	}

	static public Paint getPaint(int colorIndex)
	{
		Paint paint;
		try
		{
			paint = paints[colorIndex];
		}
		catch (IndexOutOfBoundsException e)
		{
			paint = paints[mNumColors - 1];
		}

		return paint;
	}

	View pickerView = null;

	private void setMaxMoves()
	{
		mMaxMoves = mNumColors < 6 ? mNumColors * 3 + 1 : mNumColors + 16;
	}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	    setSupportActionBar(toolbar);
	    final ColorGrid gridView = (ColorGrid)findViewById(R.id.ColorGridView);
		resetGame();
	    gridView.resetGrid();


	    View pickerView = findViewById(R.id.colorPickerView);

	    pickerView.invalidate();
        pickerView.setOnTouchListener(new View.OnTouchListener()
        {
	        @Override
	        public boolean onTouch(View v, MotionEvent event)
	        {
		        float x = event.getX();
		        float y = event.getY();

		        ColorPicker picker = (ColorPicker)v;

		        int color = picker.getChosenColor(x,y);

		        updateGrid(color);
		        return false;
	        }
        });

	    Button undoButton = (Button)findViewById(R.id.btnUndo);
	    undoButton.setOnClickListener(new View.OnClickListener()
	    {
		    @Override
		    public void onClick(View v)
		    {
			    if(gridView.isUndoable())
			    {
			    	mUsedUndo = true;
				    int stackSize = gridView.Undo();
				    moves--;
				    updateMovesText();
				    if (moves != (stackSize - 1))
				    {

					    CharSequence text = "Undo stack doesn't match move count!";
					    int duration = Toast.LENGTH_SHORT;

					    Toast.makeText(MainActivity.this, text, duration).show();
				    }
			    }
		    }
	    });
	    undoButton.setVisibility(mShowUndo ? View.VISIBLE : View.INVISIBLE);

    }

    private void createColors()
    {
	    Paint paint;

	    int[] colors = new int[]
			    {       Color.rgb(0x66,0x33,0xff),
					    Color.rgb(0x33,0xcc,0xff),
					    Color.rgb(0x33,0xcc,0x00),
					    Color.rgb(0xff,0x33,0x00),
					    Color.rgb(0xff,0x99,0x33),
					    Color.rgb(0xff,0xff,0x33),
					    Color.rgb(0x00,0x66,0x33),
					    Color.rgb(0xc0,0xc0,0xc0),
					    Color.rgb(0xff,0xc0,0xcb),
					    Color.rgb(0x00,0x00,0x00)
			    };

	    if(mNumColors > colors.length)
	    {
		    mNumColors = colors.length;
	    }

	    paints = new Paint[mNumColors];

	    for(int i = 0; i < mNumColors; i++ )
	    {
		    paints[i] = new Paint();
		    paints[i].setColor(colors[i]);
	    }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean returnVal = false;
	    ColorGrid gridView = (ColorGrid) findViewById(R.id.ColorGridView);

        switch (id)
        {
	        case R.id.action_settings:

		        returnVal = true;
		        break;

	        case R.id.action_reset:
		        resetGame();
		        returnVal = true;
		        break;

	        case R.id.action_showUndo:
		        Button undoButton = (Button) findViewById(R.id.btnUndo);
		        if (item.isChecked())
		        {
			        item.setChecked(false);
			        mShowUndo = false;
		        }
		        else
		        {
			        item.setChecked(true);
			        mShowUndo = true;
		        }

		        undoButton.setVisibility(mShowUndo ? View.VISIBLE : View.INVISIBLE);

		        returnVal = true;
		        break;

	        case R.id.action_randomize:
		        if (item.isChecked())
		        {
			        item.setChecked(false);
			        mShouldRandomize = false;
		        }
		        else
		        {
			        item.setChecked(true);
			        mShouldRandomize = true;
		        }

		        resetGame();

		        returnVal = true;
		        break;

	        case R.id.action_about:
		        showAbout();
		        returnVal = true;
		        break;

	        default:
		        returnVal = super.onOptionsItemSelected(item);
		        break;
        }
        return returnVal;
    }

    private void updateMovesText()
    {
	    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	    toolbar.setSubtitle("Moves: " + moves + " / " + mMaxMoves);
    }

	private void updateGrid(int color)
	{
		ColorGrid gridView = (ColorGrid)findViewById(R.id.ColorGridView);

		if(gridView == null)
		{
			return;
		}

		if (gridView.executeChange(color) > 0)
		{
			moves++;

			if(gridView.isSolid())
			{
				storeResults(true);
				showWin();
			}
			else if(moves >= mMaxMoves)
			{
				storeResults(false);
				showLose();
			}
			updateMovesText();
		}

	}

	private void showLose()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Bzzzzzzz, Too many moves! \nYou Lose.");
		moves = 0;
		alert.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				ColorGrid gridView = (ColorGrid)findViewById(R.id.ColorGridView);

				if(gridView != null)
				{   resetGame();
					gridView.resetGrid();
				}
			}
		});

		alert.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				ColorGrid gridView = (ColorGrid)findViewById(R.id.ColorGridView);

				if(gridView != null)
				{
					gridView.resetGrid();
				}
			}
		});


		alert.show();

	}

	private void showWin()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Congratulations, \nonly " + moves + " moves!");

		alert.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
				ColorGrid gridView = (ColorGrid)findViewById(R.id.ColorGridView);

				if(gridView != null)
				{
					resetGame();
					gridView.resetGrid();
				}
			}
		});

		alert.setOnCancelListener(new DialogInterface.OnCancelListener()
		{
			@Override
			public void onCancel(DialogInterface dialog)
			{
				ColorGrid gridView = (ColorGrid)findViewById(R.id.ColorGridView);

               if(gridView != null)
               {
	               resetGame();
                   gridView.resetGrid();
               }
           }
       });
		moves = 0;
		alert.show();
	}

	private void showAbout()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(R.string.about_color_board_title);

		alert.setPositiveButton(R.string.about_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton)
			{
			}
		});

		alert.show();
	}

}
