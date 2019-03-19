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
	public void ShowAllMinesTest(){
		Minesweeper m = new Minesweeper();
		int[] x = {2,5,3,6,7,3,9,1,0,5};
		int[] y = {5,4,7,8,1,2,9,3,2,6};
		m.setLogicalMines(x, y);
		m.showAllMines();
		for(int i = 0 ; i < 10 ; i++)
			assertEquals("M", m.getButtons()[x[i]][y[i]].getText());
	}
	
	@Test
	public void HideAllMinesTest(){
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
	public void NegativeCoordTest(){
		Minesweeper m = new Minesweeper();
		int[] x = {2,5,3,6,7,3,9,-1,0,5};
		int[] y = {5,4,7,8,1,2,9,3,2,6};
		m.setLogicalMines(x, y);
	}
	
	@Test
	public void AllMinesClickedTest(){
		Minesweeper m = new Minesweeper();
		int[] x = {2,5,3,6,7,3,9,1,0,5};
		int[] y = {5,4,7,8,1,2,9,3,2,6};
		m.setLogicalMines(x, y);
		int[] clickX = {9,5,7,1,2,0,2,3,2,3,4,0,3,4,5,3,4,4,5,6,7,8};
		int[] clickY = {0,0,0,2,2,3,3,3,4,4,4,5,5,5,5,6,6,7,7,9,9,9};
		// startup
		m.startTimer();
		m.setFirstClick(false);
		// clicking on field
		for(int i = 0 ; i < clickX.length ; i++)
			m.clickOnField(clickX[i], clickY[i]);
		// check if victory
		boolean didIWin = m.ifAllFieldsClicked();
		// assert true, because u should win
		assertEquals(true, didIWin);
	}
	
	@Test
	public void StartNewGameTest(){
		Minesweeper m = new Minesweeper();
		m.startNewGame();
		assertTrue(m.isFirstClick());
		assertEquals("0" , m.getTimer().getText());
	}

}
