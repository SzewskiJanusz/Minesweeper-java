package minesweeper_tests;

import static org.junit.Assert.*;
import org.junit.Test;
import minesweeper.Minesweeper;

public class MinesweeperTest {
	
	@Test
	public void InitTimerTest() {
		Minesweeper m = new Minesweeper();
		assertEquals("0", m.getTimer().getText());
	}
	
	@Test
	public void SetLogicalMinesTest() {
		Minesweeper m = new Minesweeper();
		int[] preparedX = {2,5,3,6,7,3,9,1,0,5};
		int[] preparedY = {5,4,7,8,1,2,9,3,2,6};
		m.setLogicalMines(preparedX, preparedY);
		int answer_mines[][] = 
		{
			{0,1,-1,2,1,0,0,0,0,0},
			{0,1,2,-1,2,1,1,0,0,0},
			{0,1,2,2,2,-1,2,1,1,0},
			{0,1,-1,1,1,1,2,-1,1,0},
			{0,1,1,2,1,2,2,2,1,0},
			{0,0,0,1,-1,2,-1,2,1,1},
			{1,1,1,1,1,2,1,2,-1,1},
			{1,-1,1,0,0,0,0,1,1,1},
			{1,1,1,0,0,0,0,0,1,1},
			{0,0,0,0,0,0,0,0,1,-1}
		};	
		int actual_mines[][] = m.getLogicalMines();
		assertArrayEquals(answer_mines, actual_mines);
	}
	
	@Test
	public void ShowAllMinesTest()
	{
		Minesweeper m = new Minesweeper();
		int[] x = {2,5,3,6,7,3,9,1,0,5};
		int[] y = {5,4,7,8,1,2,9,3,2,6};
		m.setLogicalMines(x, y);
		m.showAllMines();
		for(int i = 0 ; i < 10 ; i++)
			assertEquals("M", m.getButtons()[x[i]][y[i]].getText());
	}
	
	@Test
	public void HideAllMinesTest()
	{
		Minesweeper m = new Minesweeper();
		int[] x = {2,5,3,6,7,3,9,1,0,5};
		int[] y = {5,4,7,8,1,2,9,3,2,6};
		m.setLogicalMines(x, y);
		m.showAllMines();
		m.hideAllMines();
		for(int i = 0 ; i < 10 ; i++)
			assertEquals("", m.getButtons()[x[i]][y[i]].getText());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void NegativeCoordTest()
	{
		Minesweeper m = new Minesweeper();
		int[] x = {2,5,3,6,7,3,9,-1,0,5};
		int[] y = {5,4,7,8,1,2,9,3,2,6};
		m.setLogicalMines(x, y);
	}

}
