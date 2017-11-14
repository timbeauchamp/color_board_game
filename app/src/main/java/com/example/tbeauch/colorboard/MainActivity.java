package com.example.tbeauch.colorboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

	private static final int NUMCOLORS = 6 ;
	static Paint[] paints = new Paint[NUMCOLORS + 1];

	private int moves = 0;

	static public int getColorVal(int color)
	{
		int returnVal = 0;
		return returnVal;
	}

	static public Paint getPaint(int colorIndex)
	{
		Paint paint = paints[colorIndex];
		return paint;
	}

	View pickerView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
	    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	    setSupportActionBar(toolbar);
	    updateMovesText();

	    Paint paint;

	    paints[0] = new Paint();
	    paints[0].setColor(Color.rgb(0x66,0x33,0xff));

	    paints[1] = new Paint();
	    paints[1].setColor(Color.rgb(0x33,0xcc,0xff));

	    paints[2] = new Paint();
	    paints[2].setColor(Color.rgb(0x33,0xcc,0x00));

	    paints[3] = new Paint();
	    paints[3].setColor(Color.rgb(0xff,0x33,0x00));

	    paints[4] = new Paint();
	    paints[4].setColor(Color.rgb(0xff,0x99,0x33));

	    paints[5] = new Paint();
	    paints[5].setColor(Color.rgb(0xff,0xff,0x33));

	    paints[6] = new Paint();
	    paints[6].setColor(Color.rgb(0x00,0x00,0x00));

	    View pickerView = findViewById(R.id.colorPickerView);
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
			    ColorGrid gridView = (ColorGrid)findViewById(R.id.ColorGridView);

			    if(gridView.isUndoable())
			    {
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

	        ColorGrid gridView = (ColorGrid)findViewById(R.id.ColorGridView);

	        if(gridView != null)
	        {
		        gridView.resetGrid();
		        moves = 0;
		        updateMovesText();
	        }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateMovesText()
    {
	    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	    toolbar.setSubtitle("Moves: " + moves + " / 22");
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
				showWin();
			}
			else if(moves >= 22)
			{
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
				{
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
		moves = 0;
		alert.show();
	}

}
