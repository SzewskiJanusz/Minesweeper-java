import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

/**
 * 
 * @author Aleksander Januszewski
 * Date: 03.11.16
 *
 */

public class Minesweeper extends JFrame implements 
									  ActionListener,KeyListener,MouseListener {

	private JPanel gamepanel;
	
	private JButton[][] buttons;
	
	private int[][] mines;

	
	public static void main(String[] args) {
		Minesweeper m = new Minesweeper();
		m.setVisible(true);
	}
	
	
	public Minesweeper(){
		super("Java minesweeper");
		this.setSize(220, 300);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.addKeyListener(this);
		this.addMouseListener(this);
		
		buttons = new JButton[10][10];
		gamepanel = new JPanel();
		gamepanel.setLayout(new GridLayout(10, 10));
		setButtons();
		this.getContentPane().add(gamepanel,BorderLayout.CENTER);
		setMines();
		setNumbersNearMines();
		
	}
	
	//Set visual buttons
	private void setButtons(){
		for (int j = 0 ; j < 10 ; j++){
			for (int i = 0 ; i < 10 ; i++){
				buttons[i][j] = new JButton();
				buttons[i][j].addActionListener(this);
				buttons[i][j].addMouseListener(this);
				buttons[i][j].setMargin(new Insets(0,0,0,0));
				buttons[i][j].setFocusable(false);
				gamepanel.add(buttons[i][j]);
			}
		}
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
		for (int j = 0 ; j < 10 ; j++){
			for (int i = 0 ; i < 10 ; i++){
				if (e.getSource() == buttons[i][j]){
					clickOnField(i,j);
					if (ifAllFieldsClicked()){
						JOptionPane.showMessageDialog(this, "You won!");
						this.dispose();
					}
				}
			}
		}
	}
	
	// End if clicked on unknown mine
	private void clickOnField(int x,int y){
		 if (mines[x][y] != -2 && mines[x][y] != -3){
			 if (mines[x][y] == -1){
				 buttons[x][y].setText( "M" );
				 JOptionPane.showMessageDialog(this, "Game over!");
				 this.dispose();
			 }
			 else{
			   buttons[x][y].setEnabled(false);
			   buttons[x][y].setText(Integer.toString(mines[x][y]));
			   if (mines[x][y] == 0 )
				   showZeros(x,y);
			 }
		 }
	}
	//Implementation of Flood fill algorithm to find nearby zeros
	private void showZeros(int x, int y){
		 if (mines[x][y] == 0)
			 for (int i= x-1; i<= x+1; i++)
				 if (i>=0 && i<10)
					 for (int j=y-1; j<=y+1; j++)
						 if (j >= 0 && j < 10 && (i != x || j != y) ) {
								 buttons[i][j].setText(Integer.toString(mines[i][j]));
							       if (buttons[i][j].isEnabled()){
							    	   buttons[i][j].setEnabled(false);
							    	   showZeros(i,j);
							       }
							 }
		}
	
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
	
	private Boolean ifAllFieldsClicked(){
		for (int j = 0 ; j < 10 ; j++)
			for (int i = 0 ; i < 10 ; i++)
				if (buttons[i][j].getText().isEmpty())
					return false;
		
		return true;
	}
	//(CHEAT) Shows all mines on board
	private void showAllMines(){
		for (int j = 0 ; j < 10 ; j++){
			for (int i = 0 ; i < 10 ; i++){
				if (mines[i][j] == -1){
					buttons[i][j].setText("M");
				}
			}
		}
	}
	
	private void hideAllMines(){
		for (int j = 0 ; j < 10 ; j++)
			for (int i = 0 ; i < 10 ; i++)
				if (mines[i][j] == -1)
					buttons[i][j].setText("");
	}

	private void setNumbersNearMines(){
		for (int i=0; i < 10; i++)
			for (int j = 0; j < 10; j++)
			  if (mines[i][j]!=-1)
			   mines[i][j]=countMines(i,j);
	}
	
	//Count mines which surround recently clicked cell
	private int countMines(int x, int y){
		 int mine=0;
		 for (int i= x-1; i<=x+1; i++)
			 if (i >= 0 && i < 10)
				 for (int j=y-1; j<=y+1; j++)
					 if (j >= 0 && j < 10 && (i != x || j != y))
							 if (mines[i][j] == -1 || mines[i][j] == -2)
								 mine++;
		 return mine;
		}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ALT) 
			showAllMines();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ALT){
			System.out.println("hide");
			hideAllMines();
		}
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == 3){
			for (int j = 0 ; j < 10 ; j++)
				for (int i = 0 ; i < 10 ; i++)
					if (e.getSource() == buttons[i][j])
						if (buttons[i][j].getText().equals("X"))
							buttons[i][j].setText("");
						else
							buttons[i][j].setText("X");
		}
	}

	
	@Override
	public void mousePressed(MouseEvent e) {
		
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


}
