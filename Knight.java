import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingUtilities;

import java.awt.image.BufferedImage;
import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Color;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;

/**
 * @class Knight defines Knight behavior, movement in strategy.
 * This is the primary class for operation of the program.
 */
public class Knight extends Canvas {
	/* 
	 * * * * * * * * * * * * * * * *
	 *          CONSTANTS          *
	 * * * * * * * * * * * * * * * *
	*/
	public static final File KNIGHT_PAST = new File("Knight_Corpse.gif");
	public static final File KNIGHT_CURRENT = new File("HellKnight.gif");
	public final int MAX_MOVES = 8;
	
	
	public final int IMAGE_SIZE = ChessBoardSquare.SIDE;
	//{vertical movement, horizontal movement}
	public final int[][] KNIGHT_MOVES = {{2, 1}, {-2, 1}, {2, -1}, {-2, -1},
										 {1, 2}, {-1, 2}, {1, -2}, {-1, -2}};
	/* 
	 * * * * * * * * * * * * * * * *
	 *         CLASS DATA          *
	 * * * * * * * * * * * * * * * *
	*/
	private int[][] moveOptions;
	private ArrayList<ArrayList<Integer>> ties = new ArrayList<ArrayList<Integer>>(MAX_MOVES);
	private int[] selectedMove;
	private BufferedImage iconKnight;
	private BufferedImage knightPath;
	private int x;
	private int y;
	private int row;
	private int col;
	private ChessBoard chess;
	private ChessBoardSquare nextSquare, prevSquare, currSquare, lowestSquare;
	
	private boolean[][] squaresVisited = new boolean[ChessBoard.MAX_ROWS][ChessBoard.MAX_COLUMNS];
	public int[][] moveGrid = new int[ChessBoard.MAX_ROWS][ChessBoard.MAX_COLUMNS];
	private int moveCount;
	private Random randomMove;
	private boolean nextValidMove = true;
	
	/* 
	 * * * * * * * * * * * * * * * *
	 *         CONSTRUCTORS        *
	 * * * * * * * * * * * * * * * *
	*/
	public Knight(int startRow, int startCol, ChessBoard chessBoard)
	{
		super();
		setSize(IMAGE_SIZE, IMAGE_SIZE);
		chess = chessBoard;
		row = startRow;
		col = startCol;
		moveCount = 0;
		prevSquare = null;
		nextSquare = null;
		selectedMove = new int[2];
		
		currSquare =  chess.getBoard()[row][col];
		makeVisitedSquares();
		makeMoveGrid();
		updateMoveGrid(row, col);
		x = (col * IMAGE_SIZE) + ChessBoardSquare.MARGIN;		
		y = (row * IMAGE_SIZE) + ChessBoardSquare.MARGIN;
		randomMove = new Random();
		
		
		try
		{
			iconKnight = ImageIO.read(KNIGHT_CURRENT);
			knightPath = ImageIO.read(KNIGHT_PAST);
		}
		
		catch (IllegalArgumentException ex) 
		{ System.err.println("File is null"); }
		
		catch (IOException ex)
		{ System.err.println("Error during reading"); }
	}
	
	public Knight(int startRow, int startCol, int startX, int startY, ChessBoard chessBoard)
	{
		super();
		setSize(IMAGE_SIZE, IMAGE_SIZE);
		chess = chessBoard;
		row = startRow;
		col = startCol;
		makeVisitedSquares();
		makeMoveGrid();
		updateMoveGrid(row, col);
		x = startX;		
		y = startY;
		prevSquare = null;
		nextSquare = null;
		lowestSquare = null;
		currSquare =  chess.getBoard()[row][col];
		randomMove = new Random();
		selectedMove = new int[2];
	
		try
		{
			iconKnight = ImageIO.read(KNIGHT_CURRENT);
			knightPath = ImageIO.read(KNIGHT_PAST);
		}
		
		catch (IllegalArgumentException ex) 
		{ System.err.println("File is null"); }
		
		catch (IOException ex)
		{ System.err.println("Error during reading"); }
	}


	/* 
	 * * * * * * * * * * * * * * * *
	 *          PAINTING           *
	 * * * * * * * * * * * * * * * *
	*/
	public void paint(Graphics g)
	{
		g.drawImage(iconKnight, x, y, this);
	}
	
	public void paintTrail(Graphics g, int xP, int yP)
	{
		g.drawImage(knightPath, xP, yP, null);
	}
	
	/* 
	 * * * * * * * * * * * * * * * *
	 *          MOVE GRID          *
	 * * * * * * * * * * * * * * * *
	*/
	
	public void makeVisitedSquares()
	{
		for(int rows = 0; rows < squaresVisited.length; rows++)
		{
			for(int cols = 0; cols < squaresVisited[0].length; cols++)
			{
				squaresVisited[rows][cols] = false;	
			}
		}	
	}
	
	
	/**
	 * Find valid moves that can be moved to that location.
	 */
	
	public void makeMoveGrid()
	{
		int possibleMoves = 0;
		for(int rows = 0; rows < moveGrid.length; rows++)
		{
			for(int cols = 0; cols < moveGrid[0].length; cols++)
			{
				possibleMoves = 0;
				for(int m = 0; m < KNIGHT_MOVES.length; m++)
				{
					if(isValid(KNIGHT_MOVES[m][0], KNIGHT_MOVES[m][1], rows, cols))
					{
						possibleMoves++;
					}
				}
				
				moveGrid[rows][cols] = possibleMoves;
			}
		}
	}
	
	public void printMoveGrid()
	{
		for(int rows = 0; rows < moveGrid.length; rows++)
		{
			for(int cols = 0; cols < moveGrid[0].length; cols++)
			{
				System.out.print("[" + moveGrid[rows][cols] + "]");
			}
			System.out.println();
		}
	}
	
	public void updateMoveGrid(int currRow, int currCol)
	{
		int count = 0;
		int end = KNIGHT_MOVES.length;
		moveGrid[currRow][currCol] = 0;
		while(count < end)
		{
			if(isValid(KNIGHT_MOVES[count][0], KNIGHT_MOVES[count][1], currRow, currCol))
			{
				int gridValue = moveGrid[(currRow + KNIGHT_MOVES[count][0])][(currCol + KNIGHT_MOVES[count][1])];
				if(gridValue > 0)
				{
					moveGrid[(currRow + KNIGHT_MOVES[count][0])][(currCol + KNIGHT_MOVES[count][1])]--;	
				}
				
			}
			count++;
		}
	}
	
	/* 
	 * * * * * * * * * * * * * * * *
	 *         STRATEGIES          *
	 * * * * * * * * * * * * * * * *
	*/
	
	public void hurisitic()
	{
		if(notDeadYet())
		{
			findLowestSquare();
			moveKnight(selectedMove[0], selectedMove[1]);
		}
		
	}
	
	public void bruteForce()
	{
		
		int count = 0;
		if(nextValidMove)
		{
			count = getValidMoveNum();
			if(count > 0)
			{
				nextValidMove = true;
				makeMoveList(count);
				int moveSelection = this.randomMove.nextInt(count);
				moveKnight(moveOptions[moveSelection][0], moveOptions[moveSelection][1]);
			}
			else {
				nextValidMove = false;
			}
		}
	}
	
	
	public int getValidMoveNum()
	{
		int count = 0;
		for(int i = 0; i < KNIGHT_MOVES.length; i++)
		{
			if(isValid(KNIGHT_MOVES[i][0], KNIGHT_MOVES[i][1], row, col))
			{
				count++;
			}
		}
		
		return count;
	}
	
	
	public void makeMoveList(int size)
	{
		moveOptions = new int[size][2];
		int index = 0;
		int i = 0;
		while(index < size)
		{
			if(isValid(KNIGHT_MOVES[i][0], KNIGHT_MOVES[i][1], row, col))
			{
				moveOptions[index][0] = KNIGHT_MOVES[i][0];
				moveOptions[index][1] = KNIGHT_MOVES[i][1];
				index++;
			}
			i++;
		}
	}
	
	public void findLowestSquare()
	{
		int movesToSquare = MAX_MOVES;
		int foo = getValidMoveNum();
		makeMoveList(getValidMoveNum());
		if(nextValidMove)
		{
			if(foo > 0)
			{
				ArrayList<Integer> temp = new ArrayList<Integer>(2);
				temp.add(0, MAX_MOVES);
				temp.add(1, MAX_MOVES);
				ties = new ArrayList<ArrayList<Integer>>(10);
				ties.add(temp);
				int gridMoves;
				for(int moves = 0; moves < moveOptions.length; moves++)
				{
					gridMoves = moveGrid[row + moveOptions[moves][0]][col + moveOptions[moves][1]];
					if(gridMoves < movesToSquare)
					{
						movesToSquare = gridMoves;
						ties.get(0).set(0, Integer.valueOf(moveOptions[moves][0]));
						ties.get(0).set(1, Integer.valueOf(moveOptions[moves][1]));
					}
					
					else if(movesToSquare == gridMoves)
					{
						temp = new ArrayList<Integer>(2);
						temp.add(0, Integer.valueOf(moveOptions[moves][0]));
						temp.add(1, Integer.valueOf(moveOptions[moves][1]));
						ties.add(temp);
					}
				}//for
				
				ties.trimToSize();
				if(ties.size() > 1)
				{ tieBreakerNextMove(); }
				else {
					selectedMove[0] = ties.get(0).get(0).intValue();
					selectedMove[1] = ties.get(0).get(1).intValue();
				}
			}// if
			
			else {
				nextValidMove = false;
			}
		
		}
	}
	
	
	public void tieBreakerRandom()
	{
		int selection = randomMove.nextInt(ties.size());
		selectedMove[0] = ties.get(selection).get(0).intValue();
		selectedMove[1] = ties.get(selection).get(1).intValue();
		
		
	}
	
	
	public void tieBreakerNextMove()
	{
		int validMoves = ties.size();
		int nextSquareMoves = MAX_MOVES;
		selectedMove[0] = ties.get(0).get(0);
		selectedMove[1] = ties.get(0).get(1);
		int compareMoves;
		int dRow, dCol;
		
		for(int moves = 0; moves < validMoves; moves++)
		{
			dRow = ties.get(moves).get(0).intValue();
			dCol = ties.get(moves).get(1).intValue();
			compareMoves = moveGrid[row+dRow][col+dCol];
			if(compareMoves < nextSquareMoves)
			{
				nextSquareMoves = compareMoves;
				selectedMove[0] = dRow;
				selectedMove[1] = dCol;
			}
			
			else if(compareMoves == nextSquareMoves)
			{
				selectedMove[0] = dRow;
				selectedMove[1] = dCol;
			}
		}
		
	}
	
	
	/* 
	 * * * * * * * * * * * * * * * *
	 *     MOVE DECISION UTILS     *
	 * * * * * * * * * * * * * * * *
	*/
	public boolean isValid(int dRow, int dCol, int currRow, int currCol)
	{
		try {
			int check = moveGrid[(currRow + dRow)][(currCol + dCol)];
		}
		catch(IndexOutOfBoundsException e)
		{return false;}
		
		return hasNextSquareVisited(dRow, dCol, currRow, currCol);
	}
	
	public boolean hasNextSquareVisited(int dRow, int dCol, int currRow, int currCol)
	{
		boolean check;
		try {
			check = squaresVisited[(currRow + dRow)][(currCol + dCol)];
		}
		catch(IndexOutOfBoundsException e)
		{return false;}

		return !check; 
		
		
	}
	
	/* 
	 * * * * * * * * * * * * * * * *
	 *         GET AND SETS        *
	 * * * * * * * * * * * * * * * *
	*/
	
	public BufferedImage getKnight()
	{
		return iconKnight;
	}
	
	public BufferedImage getKnightPath()
	{
		return knightPath;
	}
	
	public ChessBoardSquare getCurrentSquare()
	{
		return currSquare;
	}
	
	public ChessBoardSquare getPrevSquare()
	{
		return prevSquare;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getRow()
	{
		return row;
	}
	
	public void setX(int x)
	{
	 	this.x = x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	public int getCol()
	{
		return col;
	}
	
	public void setCol(int col)
	{
	 	this.col = col;
	}
	
	public void setRow(int row)
	{
		this.row = row;
	}
	
	//Thread Stuff
	public boolean notDeadYet()
	{
		return nextValidMove;
	}
	
	
	/**
	 * moves knight
	 * @param dX if negative -> left, positive -> right 
	 * @param dY if negative -> up, positive -> down
	 */
	public void moveKnight(int dRow, int dCol)
	{
		if(isValid(dRow, dCol, row, col))
		{
			currSquare = chess.getBoard()[row][col];
			prevSquare = currSquare;
			squaresVisited[row][col] = true;
			setCol(col + dCol);
			setRow(row + dRow);
			updateMoveGrid(row, col);
			
			currSquare = chess.getBoard()[row][col];
			setX(currSquare.getX());
			setY(currSquare.getY());
		}
	}
	
}