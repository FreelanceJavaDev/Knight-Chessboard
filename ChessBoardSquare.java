import java.awt.LinearGradientPaint;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.GradientPaint;

public class ChessBoardSquare {
	public static final int SIDE = 80;
	public static final int MARGIN = 20;
	
	private final int START_WHITE_VALUE = 230;
	private final int START_BLACK_VALUE = 32;
	private final int COLOR_CHANGE = 2;
	private String squareID;
	
	private int row;
	private int col;
	private LinearGradientPaint gradient;
	private Rectangle2D.Double square;
	private boolean isWhite;
	private int baseX, baseY;
	private int x;
	private int y;

	
	public ChessBoardSquare(int row, int col, String id, boolean isWhite, Graphics2D g2D)
	{
		this.row = row;
		this.col = col;
		float[] change = {0.5f, 1f};
		baseX = SIDE * col;
		baseY = SIDE * row;
		x = baseX + MARGIN;
		y = baseY + MARGIN;
		gradient = new LinearGradientPaint(baseX, baseY, 
						baseX + SIDE, baseY + SIDE, change, 
						getGradientColors(row, col, isWhite));
		squareID = id;
		square = new Rectangle2D.Double(x, y, SIDE, SIDE);
		g2D.setPaint(gradient);
		g2D.fill(square);
	}
	
	private Color[] getGradientColors(int row, int col, boolean isWhite)
	{
		Color[] squareColors = new Color[2];
		int rgbVal = 0;
		if(isWhite)
		{
			rgbVal = START_WHITE_VALUE - (COLOR_CHANGE*row + COLOR_CHANGE*col);
			squareColors[0] = new Color(rgbVal, rgbVal, rgbVal);
			squareColors[1] = new Color(rgbVal+COLOR_CHANGE, rgbVal+COLOR_CHANGE, rgbVal+COLOR_CHANGE);
		}
		
		else {
			rgbVal = START_BLACK_VALUE - (COLOR_CHANGE*row + COLOR_CHANGE*col);
			squareColors[0] = new Color(rgbVal, rgbVal, rgbVal);
			squareColors[1] = new Color(rgbVal+COLOR_CHANGE, rgbVal+COLOR_CHANGE, rgbVal+COLOR_CHANGE);
		}
		
		return squareColors;
	}
	
	/* 
	 * * * * * * * * * * * * * *
	 *   GETTERS AND SETTERS   *
	 * * * * * * * * * * * * * *
	 */
	
	public int getX()
	{
		return this.x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getRow()
	{
		return row;
	}
	
	public int getCol()
	{
		return col;
	}
	
	public String getSquareID()
	{
		return squareID;
	}
	
	
}