import java.awt.Container;
import java.awt.Canvas;

import java.awt.Color;
import java.awt.GradientPaint;

import java.awt.Graphics;
import java.awt.Graphics2D;



/**
 * @class ChessBoard makes a chessboard.
 */
public class ChessBoard extends Canvas {
	
	public static final String[] ROWS = {"1", "2", "3", "4", "5", "6", "7", "8"}; //y
	public static final String[] COLUMNS = {"A", "B", "C", "D", "E", "F", "G", "H"}; //X
	public static final int MAX_ROWS = ROWS.length;
	public static final int MAX_COLUMNS = COLUMNS.length;
	
	private ChessBoardSquare[][] board = new ChessBoardSquare[ROWS.length][COLUMNS.length];
	
	public ChessBoard()
	{
		super();
		setSize(680, 680);
	}
	
	public void paint(Graphics g)
	{
		Graphics2D g2d = (Graphics2D) g;
		int count = 0;
		String location;
		boolean squareColor;
		for(int row = 0; row < ROWS.length; row++)
		{
			for(int col = 0; col < COLUMNS.length; col++)
			{
				location = ROWS[row] + COLUMNS[col];
				squareColor  = (count%2==0);
				board[row][col] = new ChessBoardSquare(row, col, location, squareColor, g2d);
				count++;
			}
			count++;
			
		}
	}
	
	public int getRow(ChessBoardSquare a)
	{
		return a.getRow();
		
	}
	
	public int getCol(ChessBoardSquare a)
	{
		return a.getCol();
	}
	
	public int getSquareY(ChessBoardSquare a)
	{
		return a.getY();
		
	}
	
	public int getSquareX(ChessBoardSquare a)
	{
		return a.getX();
	}
	
	public ChessBoardSquare[][] getBoard()
	{
		return this.board;
	}
	
	public ChessBoardSquare getBoardSquare(int row, int col)
	{
		return this.board[row][col];
	}
	
	public int getSquareSize()
	{
		return getBoard()[0][0].SIDE;
	}
	
}


