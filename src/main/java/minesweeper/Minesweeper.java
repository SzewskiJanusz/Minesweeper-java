package minesweeper;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

/**
 * 
 * @author Aleksander Januszewski
 * Date: 03.11.16
 * Functionality
 * - gameplay according to Microsoft's minesweeper
 * - menubar
 * 		- new game
 * 		- exit game
 * - popup window on game over
 * - popup window on game won
 * - timer 
 * 
 * Update
 * Date: 19.03.19
 * 
 * Added unit tests (80% branch test coverage)
 * Refactored some code
 * Added JavaDoc 
 */
public class Minesweeper extends JFrame implements 
									  ActionListener,KeyListener,MouseListener {

	private final int RIGHT_MOUSE_BUTTON = 3;
	private JPanel gamepanel;			// Main game panel
	private JButton[][] buttons;		// Physical mines
	private int[][] mines;				// Logic mines
	private JLabel timer;				// Shows actual time from start
	private Boolean firstClick = true; 	// If true, start timer
	private Thread t;					// Distinct thread for timer
	private boolean cheatUsed;
	
	public static void main(String[] args) {
		Minesweeper m = new Minesweeper();
		m.setVisible(true);
	}
	
	public Minesweeper(){
		// JFrame setup
		super("Java minesweeper");
		setSize(220, 300);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setFocusable(true);
		addKeyListener(this);
		addMouseListener(this);
		// Init timer
		initializeTimer();
		// Init gamepanel
		getContentPane().add(timer, BorderLayout.NORTH);
		gamepanel = new JPanel(new GridLayout(10, 10));
		// Init buttons and add them to panel
		setButtons();
		// Add gamepanel to contentpane of JFrame
		this.getContentPane().add(gamepanel,BorderLayout.CENTER);
		// Prepare mines for game
		setMines();
		setNumbersNearMines();
		
		// Menu bar
		JMenuBar menubar = new JMenuBar();
		// Init JMenu
		JMenu game = new JMenu("Game");
		// New game menu item
		JMenuItem newg = new JMenuItem("New");
		newg.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				  startNewGame();
			}
		});
		game.add(newg);
		// Options menu item
		JMenuItem options = new JMenuItem("Options");
		options.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//not implemented yet
			}
		});
		game.add(options);
		// Best scores menu item
		JMenuItem bestscr = new JMenuItem("Best scores");
		bestscr.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//not implemented yet
			}
		});
		game.add(bestscr);
		game.addSeparator();
		// Exit game menu item
		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		game.add(exit);
		// Add JMenu to JMenuBar
		menubar.add(game);
		setJMenuBar(menubar);
	}
	
	/**
	 * Reset mines, buttons and timer then place new mines.
	 */
	public void startNewGame() {
		clearButtons();
		setMines();
		setNumbersNearMines();
		try{
			t.interrupt();
		}
		catch(NullPointerException npe){}
		firstClick = true;
		timer.setText("0");
		repaint();
	}
	
	/**
	 * For testing purposes
	 * @param x - coords X of mines
	 * @param y - coords Y of mines
	 */
	public void setLogicalMines(int[] x, int[] y){
		setMines(x, y);
		setNumbersNearMines();
	}
	
	private void initializeTimer(){
		timer = new JLabel("0");
		timer.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	/**
	 * Set every button. Add client property of X and Y in grid.
	 */
	private void setButtons(){
		buttons = new JButton[10][10];
		for (int j = 0 ; j < 10 ; j++){
			for (int i = 0 ; i < 10 ; i++){
				buttons[i][j] = new JButton();
				buttons[i][j].addActionListener(this);
				buttons[i][j].addMouseListener(this);
				buttons[i][j].setMargin(new Insets(0,0,0,0));
				buttons[i][j].putClientProperty("x", i);
				buttons[i][j].putClientProperty("y", j);
				gamepanel.add(buttons[i][j]);
			}
		}
	}
	
	/**
	 * Reset all buttons.
	 */
	private void clearButtons(){
		for (int j = 0 ; j < 10 ; j++){
			for (int i = 0 ; i < 10 ; i++){
				buttons[i][j].setEnabled(true);
				buttons[i][j].setText("");
			}
		}
	}
	
	/**
	 * Triggers when clicked on any button in gamepanel.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (firstClick) {		// Starts timer with 1st click
			startTimer();
			firstClick = false;
		}
		if (isJButton(e.getSource())) {
			tryClick(e.getSource());
		}
	}

	public void startTimer() {
		t = new Thread(new Timer());
		t.start();
	}
	
	/**
	 * Converts object to JButton and reads X and Y in grid of this button then
	 * clicks on field with given coords. If all non-mine fields are clicked then
	 * game won.
	 * @param source - e.getSource() from ActionEvent of actionPerformed
	 */
	public void tryClick(Object source) {
		JButton jb_temp = (JButton)source;
		int x = Integer.parseInt(jb_temp.getClientProperty("x").toString());
		int y = Integer.parseInt((String) jb_temp.getClientProperty("y").toString());
		clickOnField(x, y);
		if (ifAllFieldsClicked())
			popupDialogAndExit("You won!");
	}

	private boolean isJButton(Object source) {
		return source.getClass().isInstance(new JButton());
	}
	
	/**
	 * Checks if coordinates point on mine. If yes, then game over. 
	 * If not, set button text to mine's. Additionaly if mine = 0 then start
	 * flood-fill algorithm to show all zeros and numbers nearby. 
	 * @param x - coord X of button in grid
	 * @param y - coord Y of button in grid
	 */
	public void clickOnField(int x,int y){
		 if (mines[x][y] == -1){
			 buttons[x][y].setText( "M" );
			 popupDialogAndExit("Game over!");
		 }
		 else{
		   buttons[x][y].setEnabled(false);
		   buttons[x][y].setText(Integer.toString(mines[x][y]));
		   if (mines[x][y] == 0 )
			   showZeros(x,y);
		 }
	}
	/**
	 * Implementation of flood-fill algorithm to find all zeros and numbers nearby.
	 * Recursive.
	 * @param x - coord X of button in grid
	 * @param y - coord Y of button in grid
	 */
	private void showZeros(int x, int y){
		 if (mines[x][y] == 0)
			 for (int i = x-1; i <= x+1; i++)
				 if (i >= 0 && i < 10)
					 for (int j= y-1; j <= y+1; j++)
						 if (j >= 0 && j < 10 && (i != x || j != y) ) {
								 buttons[i][j].setText(Integer.toString(mines[i][j]));
						         if (buttons[i][j].isEnabled()){
						    	     buttons[i][j].setEnabled(false);
						    	     showZeros(i,j);
						         }
					 }
	}
	/**
	 * Interrupts thread, show dialog and end program
	 * @param text - text to be shown
	 */
	private void popupDialogAndExit(String text){
		t.interrupt();
		JOptionPane.showMessageDialog(this, text);
		this.dispose();
	}
	
	/**
	 * Set all logical mines randomly
	 */
	private void setMines(){
		Random r = new Random();
		int a;
		int b;
		
		mines = new int[10][10];
		
		for (int i = 0 ; i < 10 ; i++){
				a = r.nextInt(10);
				b = r.nextInt(10);
				
				while(mines[a][b] == -1){
					a = r.nextInt(10);
					b = r.nextInt(10);
				}
				
				mines[a][b] = -1;
		}
	}
	
	/**
	 * Set mines with given coordinates. Used for testing.
	 * @param x - x in grid
	 * @param y - y in grid
	 */
	public void setMines(int[] x, int[] y){
		if (x.length != y.length) throw new IllegalArgumentException();
		mines = new int[10][10];
		for (int i = 0 ; i < 10 ; i++)
			if (x[i] >= 0 && x[i] <= 9 && y[i] >= 0 && y[i] <= 9)
				mines[x[i]][y[i]] = -1;
			else
				throw new IllegalArgumentException();
	}
	
	/**
	 * Checks if all fields were clicked
	 * @return - true - all fields clicked, false - not yet
	 */
	public Boolean ifAllFieldsClicked(){
		for (int j = 0 ; j < 10 ; j++)
			for (int i = 0 ; i < 10 ; i++)
				if (buttons[i][j].getText().isEmpty() && 
						mines[i][j] != -1)
					return false;
		
		return true;
	}

	/**
	 * Cheat which shows all mines on board.
	 */
	public void showAllMines(){
		for (int j = 0 ; j < 10 ; j++){
			for (int i = 0 ; i < 10 ; i++){
				if (mines[i][j] == -1){
					buttons[i][j].setText("M");
				}
			}
		}
		cheatUsed = true;
	}
	
	/**
	 * Hide all mines, exposed by cheat.
	 */
	public void hideAllMines(){
		for (int j = 0 ; j < 10 ; j++)
			for (int i = 0 ; i < 10 ; i++)
				if (mines[i][j] == -1)
					buttons[i][j].setText("");
		cheatUsed = false;
	}

	private void setNumbersNearMines(){
		for (int i = 0; i < 10; i++)
			for (int j = 0; j < 10; j++)
			  if (mines[i][j] != -1)
			   mines[i][j] = countMines(i, j);
	}
	
	/**
	 * Count mines which surround recently clicked cell
	 * @param x - x of cell
	 * @param y - y of cell
	 * @return numer of mines nearby free cell
	 */
	private int countMines(int x, int y){
		 int mine=0;
		 for (int i = x-1; i <= x+1; i++)
			 if (i >= 0 && i < 10)
				 for (int j = y-1; j <= y+1; j++)
					 if (j >= 0 && j < 10 && (i != x || j != y))
							 if (mines[i][j] == -1 || mines[i][j] == -2)
								 mine++;
		 return mine;
	}

	@Override
	public void keyTyped(KeyEvent e) {	}
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ALT) 
			if (cheatUsed)
				hideAllMines();
			else
				showAllMines();
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	/**
	 * @author Janusz
	 * Class which counts time in new thread
	 */
	class Timer implements Runnable{
		private int seconds;
		@Override
		public void run() {
			seconds = 0;
			System.out.println("run");
			while(true){
				seconds++;
				try {
					Thread.sleep(1000);
					getTimer().setText(""+seconds);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
		
	}
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		if (e.getButton() == RIGHT_MOUSE_BUTTON) {
			if (isJButton(e.getSource())) {
				JButton jb_temp = (JButton)e.getSource();
				if (jb_temp.isEnabled())
					jb_temp.setText(jb_temp.getText().equals("X") ? "" : "X");	
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }

///////////////////////////////////////////////////////////////////////
	// GETTERS AND SETTERS
	
	public JLabel getTimer() {
		return timer;
	}

	public int[][] getLogicalMines() {
		return mines;
	}

	public JButton[][] getButtons() {
		return buttons;
	}

	public void setButtons(JButton[][] buttons) {
		this.buttons = buttons;
	}

	public Boolean isFirstClick() {
		return firstClick;
	}

	public void setFirstClick(Boolean firstClick) {
		this.firstClick = firstClick;
	}
}
