import java.awt.Canvas;
import java.awt.Graphics;

import javax.swing.JFrame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * @class KnightsJourney is the driver class  all parts are brought together.
 */

public class KnightsJourney extends Canvas implements Runnable
{
	public final int HOLY_GRAIL_FOUND = 64; //All squares must be touched only once!
	public final int SLEEP = 300;
	private Knight montyPython;
	private ChessBoard holyGrail;
	MouseHandler placeKnight = new MouseHandler();
	private int numMoves;
	private int currentRow;
	private int currentCol;
	private int numClicks;
	private ChessBoardSquare currentSquare = null;
	private ChessBoardSquare pastSquare = null;
	private Thread runner;  //only in here to make the animation.
	private volatile boolean stop = true;  //volatile variables are stored in main memory
	//all threads are updated when the volatile variable us updated.  
	//This allows multiple threads to run simultaniously and is helpful in stopping all threads simultaniously
	
	
	
	/*
	 * * * * * * * * * * * * * *  
	 *       CONSTRUCTORS      *
	 * * * * * * * * * * * * * *
	 */
	
	public KnightsJourney()
	{
		super();
		setSize(700, 700);
		addMouseListener(placeKnight);
		numMoves = 0;
		montyPython = null;
		holyGrail = new ChessBoard();
		numClicks = 0;
		runner = new Thread(this);
	}
	
	
	
	/*
	 * * * * * * * * * * * * * *  
	 *        PAINT STUFF      *
	 * * * * * * * * * * * * * *
	 */
	
	public void paint(Graphics g)
	{
		holyGrail.paint(g);
	}
	
	public void repaintAll()
	{
		this.paintAll(this.getGraphics());		
	}
	
	public void paintAll(Graphics g)
	{
		holyGrail.paint(g);
		montyPython.paint(g);
	
		if(!runner.isAlive())
		{
			stop = false;
			runner = new Thread(this);
			runner.start();
		}
	}
	
	/*
	 * * * * * * * * * * * * * *  
	 *       THREAD STUFF      *
	 * * * * * * * * * * * * * *
	 */
	
	public void start()
	{
		if(!runner.isAlive())
		{
			stop = false;
			runner = new Thread(this);
			run();
		}

	}
	
	public void stop()
	{
		if(runner.isAlive())
		{
			try
				{
					runner.join(1); //combine runner into main thread after 1 milisecond
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				stop = true;
				System.out.println("Stopping...");
				numMoves = 0;
		}
	}
	
	public void resetGUI()
	{
		if(runner.isAlive())
		{
			stop();
		}
		repaint();
		montyPython = null;
		System.out.println("Resetting GUI...");
	}
	
	//pausing thread.
	public void repaintPause()
	{
		try
		{
		    Thread.sleep((long)SLEEP);
		}
		catch(InterruptedException e)
		{ }

	}

	//Thread starts
	public void run()
	{
		repaintPause();
		while(!stop) //this has a stop condition for all threads.
		{
			if(!huzzah())
			{ 
				onlyAFleshWound();
				//coconuts();
			}
			
			else {
				stop(); //stopping thread
				System.out.println("Solution Found!");
			}
		}
			
	}
	
	/*
	 * * * * * * * * * *
	 *   STRATEGIES    *
	 * * * * * * * * * * 
	 */
	
	/**
	 * Running with a theme, this is for the brute force method.
	 */
	public void coconuts()
	{
		if(montyPython.notDeadYet()) //are there any valid moves left?
		{
			montyPython.bruteForce();
			repaintPause();
			montyPython.paint(this.getGraphics());
			repaintPause();
			montyPython.paintTrail(this.getGraphics(), 
							montyPython.getPrevSquare().getX(), 
							montyPython.getPrevSquare().getY());
			numMoves++;
		}
		
		else {
			System.out.println("No solution found");
			stop(); //stop all threads
		}
	}
	
	/**
	 * This is the huristic algorythim method
	 */
	
	public void onlyAFleshWound()
	{
		if(montyPython.notDeadYet())
		{
			montyPython.hurisitic();
			repaintPause();
			montyPython.paint(this.getGraphics());
			montyPython.paintTrail(this.getGraphics(), 
							montyPython.getPrevSquare().getX(), 
							montyPython.getPrevSquare().getY());
			repaintPause();
			numMoves++;
		}
		
		else {
			System.out.println("No solution found");
			stop();
		}

	}
	
		
	
	private boolean huzzah()
	{
		if(numMoves < HOLY_GRAIL_FOUND)
		{ return false; }
		
		else 
		{ return true; }
	}
	
	private class MouseHandler extends MouseAdapter
	{
		public void mouseClicked(MouseEvent e)
		{
			numClicks++;
			if(numClicks%3 == 1 && validXY(e.getX(), e.getY()) )
			{
				ChessBoardSquare selected = findSquareClicked(e.getX(), e.getY(), holyGrail.getBoard());
				montyPython = new Knight(selected.getRow(), selected.getCol(), selected.getX(), selected.getY(), holyGrail);
				repaintAll();
			}
			
			else if(numClicks%3 == 2)
			{
				stop();
			}
			
			else
			{
				resetGUI();
			}
			
			
		}
		
		
		/**
		 * A little helper method for mouseClicked().
		 * Mostly for my sanity.
		 * @param x is x-coordinate of mouseClick -> e.getX().
		 * @param y is y-coordinate of mouseClick -> e.getY().
		 */
		private boolean validXY(int x, int y)
		{
			boolean topXOK = (x >= (holyGrail.getX() + ChessBoardSquare.MARGIN));
			boolean bottomXOK = (x < (holyGrail.getWidth() - ChessBoardSquare.MARGIN));
			boolean topYOK = (y >= (holyGrail.getY() + ChessBoardSquare.MARGIN));
			boolean bottomYOK = (y < (holyGrail.getHeight() - ChessBoardSquare.MARGIN));
			return (topXOK && bottomXOK) && (topYOK && bottomYOK);
		}
		
		public ChessBoardSquare findSquareClicked(int x, int y, ChessBoardSquare[][] a)
		{
			int row = 0;
			int col = 0;
			ChessBoardSquare temp = a[row][col];
			isInRange(x, y, temp);
			while(!isInRange(x, y, temp) && row < a.length)
			{
				col = 0;
				while(!isInRange(x, y, temp) && col < a[0].length)
				{
					temp = a[row][col];
					col++;
				}
				row++;
			}
			return temp;
		}
	
		private boolean isInRange(int x, int y, ChessBoardSquare a)
		{
			if((a.getY() + a.SIDE) < y  && (a.getY()) < y)
			{
				return false;
			}
			
			else if((a.getX() + a.SIDE) < x && (a.getX()) < x)
			{
					return false;
			}
				
			else { return true; }
		}
	}//end of mouse handler

	public static void main(String[] args)
	{
		JFrame fullTest = new JFrame();
		fullTest.setSize(700, 700);
		fullTest.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		KnightsJourney verySmallRocks = new KnightsJourney();
		fullTest.add(verySmallRocks);
		fullTest.pack();
		fullTest.setVisible(true);
		//AudioFilePlayer player = new AudioFilePlayer();
		//player.play("Buffy The Vampire Slayer.wav");

	}

		
}