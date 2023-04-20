package views;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import engine.Game;
import engine.Player;
import engine.PriorityQueue;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.world.Champion;
import model.world.Cover;
import model.world.Damageable;
import model.world.Direction;

public class StageThree extends JFrame implements ViewListener {
	private Player p1;
	private Player p2;
	private JButton[][] locationsOfBoard;
	
	private Champion[] championsArray;
	private JTextArea[] championsTextAreas;
	private JTextArea[] championsAbilityText;
	
	private JPanel locationsPanel;
	private Game model;
	
	private JButton endTurnButton;
	private JButton useLA;
	
	private JLabel playerOneLA;
	private JLabel playerTwoLA;

	private JTextArea turnArea;
	
	private JComboBox<AreaOfEffect> healingBox;
	private JComboBox<AreaOfEffect> damagingBox;
	private JComboBox<AreaOfEffect> crowdBox;
	
	private JComboBox<Direction> directionBox;
	
	private JComboBox<Integer> chooseX;
	private JComboBox<Integer> chooseY;
	
	
	private JButton upAttack;
	private JButton downAttack;
	private JButton leftAttack;
	private JButton rightAttack;
	
	private JTextArea playerTurn;
	
	public StageThree (Game model) throws IOException  {
		p1=model.getFirstPlayer();
		p2=model.getSecondPlayer();
		this.model=model;
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(0,0,1280,720);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.getContentPane().setBackground(new Color(217, 224, 231));
		
		Border border = BorderFactory.createLineBorder(Color.black, 1);
		Font fontBold = new Font("Serif",Font.BOLD,20);
		Font fontMed2 = new Font("Serif",Font.BOLD,17);
		Font fontMed = new Font("Serif",Font.BOLD,15);

		locationsOfBoard = new JButton[5][5];
		
		locationsPanel = new JPanel();
		locationsPanel.setBounds(315, 0, 650, 520);
		locationsPanel.setBackground(Color.white);
		locationsPanel.setLayout(new GridLayout(5,5,2,2));
		this.add(locationsPanel);

		for(int x=0 ; x<5 ; x++) {
			for(int y=0; y<5 ; y++) {
				JButton button = new JButton();
				button.setBackground(null);
				locationsOfBoard[x][y] = button;
				button.setFocusable(false);
				locationsPanel.add(button);
			}
		}
		
		for(Champion champion : p1.getTeam()) {
			placeChampionPhoto(champion);
		}
		for(Champion champion : p2.getTeam()) {
			placeChampionPhoto(champion);
		}
		
		
		for(int i=0 ; i<5 ; i++ ) {
			for(int j=0 ; j<5 ;j++) {
				if(model.getBoard()[i][j] instanceof Cover ) {
					this.placeCoverPhoto((Cover) model.getBoard()[i][j]);
				}
			}
		}
		
		endTurnButton = new JButton("End Turn");
		endTurnButton.setBounds(315, 525, 120, 40);
		endTurnButton.setBackground(new Color(75, 203, 35));
		endTurnButton.setForeground(Color.white);
		endTurnButton.setFont(fontBold);
		endTurnButton.setFocusable(false);
		this.add(endTurnButton);
		
		useLA = new JButton("USE LA");
		useLA.setBounds(315, 570, 120, 40);
		useLA.setBackground(new Color(75, 203, 35));
		useLA.setForeground(Color.white);
		useLA.setFont(fontBold);
		useLA.setFocusable(false);
		this.add(useLA);
		
		
		
		JTextArea turnTitle = new JTextArea("Champions Turns : ");
		turnTitle.setEditable(false);
		turnTitle.setBackground(null);
		turnTitle.setFont(fontBold);
		turnTitle.setBounds(160, 640, 170, 35);
		this.add(turnTitle);
		
		turnArea = new JTextArea();
		turnArea.setEditable(false);
		turnArea.setBackground(Color.white);
		turnArea.setBounds(340, 640, 620, 35);
		turnArea.setBorder(border);
		turnArea.setFont(fontMed2);
		this.add(turnArea);
		
		championsArray = new Champion[6];
		
		for(int i=0 ; i<3 ; i++)
			championsArray[i] = model.getFirstPlayer().getTeam().get(i);
		for(int i=0 ; i<3 ; i++)
			championsArray[i+3] = model.getSecondPlayer().getTeam().get(i);
		
		championsTextAreas = new JTextArea[6];
		championsAbilityText = new JTextArea[6];
		
		JPanel playerOneChampionsData = new JPanel();
		playerOneChampionsData.setBounds(5, 80, 150, 550);
		playerOneChampionsData.setOpaque(true);
		playerOneChampionsData.setLayout(new GridLayout(3,1,5,5));
		playerOneChampionsData.setBackground(null);
		for(int i=0 ; i<3 ; i++) {
			JTextArea championText = new JTextArea();
			championText.setBackground(new Color(133, 213, 249));
			championText.setOpaque(true);
			playerOneChampionsData.add(championText);
			championText.setEditable(false);
			championsTextAreas[i] = championText;
			
			JScrollPane scroller = new JScrollPane(championText);
			JScrollBar bar = new JScrollBar();
			scroller.add(bar);
			playerOneChampionsData.add(scroller, BorderLayout.EAST);
		}
		this.add(playerOneChampionsData);

		JPanel playerOneChampionsAbilities = new JPanel();
		playerOneChampionsAbilities.setBounds(160, 80, 150, 550);
		playerOneChampionsAbilities.setOpaque(true);
		playerOneChampionsAbilities.setLayout(new GridLayout(3,1,5,5));
		playerOneChampionsAbilities.setBackground(null);
		
		for(int i=0 ; i<3 ; i++) {
			JTextArea abilitiesText = new JTextArea();
			abilitiesText.setBackground(Color.red);
			abilitiesText.setBackground(new Color(133, 213, 249));
			abilitiesText.setOpaque(true);
			playerOneChampionsAbilities.add(abilitiesText);
			abilitiesText.setEditable(false);
			championsAbilityText[i] = abilitiesText;
			
			JScrollPane scroller = new JScrollPane(abilitiesText);
			JScrollBar bar = new JScrollBar();
			scroller.add(bar);
			playerOneChampionsAbilities.add(scroller, BorderLayout.EAST);
		}
		this.add(playerOneChampionsAbilities);

		JPanel playerTwoChampionsData = new JPanel();
		playerTwoChampionsData.setBounds(970, 80, 140, 550);
		playerTwoChampionsData.setOpaque(true);
		playerTwoChampionsData.setLayout(new GridLayout(3,1,5,5));
		playerTwoChampionsData.setBackground(null);
		
		for(int i=0 ; i<3 ; i++) {
			JTextArea championText = new JTextArea();
			championText.setBackground(Color.red);
			championText.setBackground(new Color(133, 213, 249));
			championText.setOpaque(true);
			playerTwoChampionsData.add(championText);
			championText.setEditable(false);
			championsTextAreas[i+3] = championText;
			
			JScrollPane scroller = new JScrollPane(championText);
			JScrollBar bar = new JScrollBar();
			scroller.add(bar);
			playerTwoChampionsData.add(scroller, BorderLayout.EAST);
		}
		this.add(playerTwoChampionsData);

		JPanel playerTwoChampionsAbilities = new JPanel();
		playerTwoChampionsAbilities.setBounds(1115, 80, 150, 550);
		playerTwoChampionsAbilities.setOpaque(true);
		playerTwoChampionsAbilities.setLayout(new GridLayout(3,1,5,5));
		playerTwoChampionsAbilities.setBackground(null);
		
		for(int i=0 ; i<3 ; i++) {
			JTextArea abilitiesText = new JTextArea();
			playerTwoChampionsAbilities.add(abilitiesText);
			abilitiesText.setBackground(new Color(133, 213, 249));
			abilitiesText.setEditable(false);
			championsAbilityText[i+3] = abilitiesText;
			
			JScrollPane scroller = new JScrollPane(abilitiesText);
			JScrollBar bar = new JScrollBar();
			scroller.add(bar);
			playerTwoChampionsAbilities.add(scroller, BorderLayout.EAST);
		}
		this.add(playerTwoChampionsAbilities);
		
		for(int i=0 ; i<3 ; i++) {
			if(model.getFirstPlayer().getTeam().get(i) == model.getFirstPlayer().getLeader())
				championsTextAreas[i].setText(model.getFirstPlayer().getTeam().get(i).displayChampionData()+"\n"+"   Leader");
			else
				championsTextAreas[i].setText(model.getFirstPlayer().getTeam().get(i).displayChampionData());
			championsAbilityText[i].setText(model.getFirstPlayer().getTeam().get(i).displayAbilitiesData());
			
			if(model.getSecondPlayer().getTeam().get(i) == model.getSecondPlayer().getLeader())
				championsTextAreas[i+3].setText(model.getSecondPlayer().getTeam().get(i).displayChampionData()+"\n"+"   Leader");
			else
				championsTextAreas[i+3].setText(model.getSecondPlayer().getTeam().get(i).displayChampionData());
			championsAbilityText[i+3].setText(model.getSecondPlayer().getTeam().get(i).displayAbilitiesData());
		}
		
		JTextArea playerOneName = new JTextArea("Team Of "+model.getFirstPlayer().getName());
		playerOneName.setBounds(10, 10, 220, 30);
		playerOneName.setFont(fontBold);
		playerOneName.setEditable(false);
		playerOneName.setBackground(null);
		this.add(playerOneName);
		
		JTextArea playerOneLeaderAbility = new JTextArea("Can "+model.getFirstPlayer().getName()+" Use LA ?");
		playerOneLeaderAbility.setBounds(10, 40, 220, 30);
		playerOneLeaderAbility.setFont(fontBold);
		playerOneLeaderAbility.setEditable(false);
		playerOneLeaderAbility.setBackground(null);
		playerOneLeaderAbility.setForeground(new Color(217, 19, 216));
		this.add(playerOneLeaderAbility);
		
	    BufferedImage image = null;
	    image = ImageIO.read(new File("photos/green-mark.png"));
		Image resizedImage = image.getScaledInstance(40,40,Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(resizedImage);
		
		playerOneLA = new JLabel();
		playerOneLA.setBounds(230, 30, 40, 40);
		playerOneLA.setIcon(icon);
		playerOneLA.setBackground(Color.white);
		playerOneLA.setOpaque(true);
		this.add(playerOneLA);
		
		JTextArea playerTwoName = new JTextArea("Team Of "+model.getSecondPlayer().getName());
		playerTwoName.setBounds(970, 10, 220, 30);
		playerTwoName.setFont(fontBold);
		playerTwoName.setEditable(false);
		playerTwoName.setBackground(null);
		this.add(playerTwoName);
		
		JTextArea playerTwoLeaderAbility = new JTextArea("Can "+model.getSecondPlayer().getName()+" Use LA ?");
		playerTwoLeaderAbility.setBounds(970, 40, 220, 30);
		playerTwoLeaderAbility.setFont(fontBold);
		playerTwoLeaderAbility.setEditable(false);
		playerTwoLeaderAbility.setBackground(null);
		playerTwoLeaderAbility.setForeground(new Color(217, 19, 216));
		this.add(playerTwoLeaderAbility);
		
		playerTwoLA = new JLabel();
		playerTwoLA.setBounds(1190, 30, 40, 40);
		playerTwoLA.setIcon(icon);
		playerTwoLA.setBackground(Color.white);
		playerTwoLA.setOpaque(true);
		this.add(playerTwoLA);
		
		JTextArea attackArea = new JTextArea("Attack");
		attackArea.setBounds(510, 560, 50, 20);
		attackArea.setFont(fontMed2);
		attackArea.setEditable(false);
		attackArea.setBackground(null);
		this.add(attackArea);
		
		upAttack = new JButton("U");
		upAttack.setBounds(510, 530, 60, 25);
		upAttack.setFont(fontMed2);
		upAttack.setBackground(new Color(221, 0, 0));
		upAttack.setFocusable(false);
		this.add(upAttack);
		
		downAttack = new JButton("D");
		downAttack.setBounds(510, 590, 60, 25);
		downAttack.setFont(fontMed2);
		downAttack.setBackground(new Color(221, 0, 0));
		downAttack.setFocusable(false);
		this.add(downAttack);
		
		leftAttack = new JButton("L");
		leftAttack.setBounds(440, 560, 60, 25);
		leftAttack.setFont(fontMed2);
		leftAttack.setBackground(new Color(221, 0, 0));
		leftAttack.setFocusable(false);
		this.add(leftAttack);
		
		rightAttack = new JButton("R");
		rightAttack.setBounds(577, 560, 60, 25);
		rightAttack.setFont(fontMed2);
		rightAttack.setBackground(new Color(221, 0, 0));
		rightAttack.setFocusable(false);
		this.add(rightAttack);
		
		JTextArea healingArea = new JTextArea("Cast Healing");
		healingArea.setBounds(650, 517, 100, 20);
		healingArea.setBackground(null);
		healingArea.setFont(fontMed);
		healingArea.setEditable(false);
		this.add(healingArea);
		
		healingBox = new JComboBox();
		healingBox.setBounds( 650,540,100,20);
		healingBox.setBackground(Color.yellow);
		healingBox.setFocusable(false);
		this.add(healingBox);
		
		JTextArea damagingArea = new JTextArea("Cast Damaging");
		damagingArea.setBounds(753, 517, 100, 20);
		damagingArea.setBackground(null);
		damagingArea.setFont(fontMed);
		damagingArea.setEditable(false);
		this.add(damagingArea);
		
		damagingBox = new JComboBox();
		damagingBox.setBounds(753,540,100,20);
		damagingBox.setBackground(Color.yellow);
		damagingBox.setFocusable(false);
		this.add(damagingBox);
		
		JTextArea crowdControlArea = new JTextArea("   Cast CC");
		crowdControlArea.setBounds(853, 517, 100, 20);
		crowdControlArea.setBackground(null);
		crowdControlArea.setFont(fontMed);
		crowdControlArea.setEditable(false);
		this.add(crowdControlArea);
		
		crowdBox = new JComboBox();
		crowdBox.setBounds(857,540,100,20);
		crowdBox.setBackground(Color.yellow);
		crowdBox.setFocusable(false);
		this.add(crowdBox);
		
		JTextArea directionTitle  = new JTextArea("Direction");
		directionTitle.setBounds( 650,578,100,20);
		directionTitle.setBackground(null);
		directionTitle.setFont(fontMed);
		directionTitle.setEditable(false);
		this.add(directionTitle);
		
		directionBox = new JComboBox();
		directionBox.setBounds( 650,600,100,20);
		directionBox.setBackground(Color.yellow);
		directionBox.setFocusable(false);
		directionBox.addItem(null);
		directionBox.addItem(Direction.UP);
		directionBox.addItem(Direction.DOWN);
		directionBox.addItem(Direction.RIGHT);
		directionBox.addItem(Direction.LEFT);
		this.add(directionBox);
		
		JTextArea xTitle  = new JTextArea("   X");
		xTitle.setBounds( 755,578,50,20);
		xTitle.setBackground(null);
		xTitle.setFont(fontMed);
		xTitle.setEditable(false);
		this.add(xTitle);
		
		chooseX = new JComboBox();
		chooseX.setBounds( 755,600,50,20);
		chooseX.setBackground(Color.yellow);
		chooseX.setFocusable(false);
		chooseX.addItem(null);
		chooseX.addItem(0);
		chooseX.addItem(1);
		chooseX.addItem(2);
		chooseX.addItem(3);
		chooseX.addItem(4);
		this.add(chooseX);

		JTextArea yTitle  = new JTextArea("   Y");
		yTitle.setBounds( 810,578,50,20);
		yTitle.setBackground(null);
		yTitle.setFont(fontMed);
		yTitle.setEditable(false);
		this.add(yTitle);
		
		chooseY = new JComboBox();
		chooseY.setBounds( 810,600,50,20);
		chooseY.setBackground(Color.yellow);
		chooseY.setFocusable(false);
		chooseY.addItem(null);
		chooseY.addItem(0);
		chooseY.addItem(1);
		chooseY.addItem(2);
		chooseY.addItem(3);
		chooseY.addItem(4);
		this.add(chooseY);
		
		playerTurn = new JTextArea();
		playerTurn.setBounds( 990,640,250,35);
		playerTurn.setBackground(Color.white);
		playerTurn.setFont(fontBold);
		playerTurn.setEditable(false);
		this.add(playerTurn);
		
		changePlayerTurnArea();

		this.setVisible(true);
		this.revalidate();
		this.repaint();
	}
	

	public void changePlayerTurnArea() {
		Champion c = model.getCurrentChampion();
		if(model.getFirstPlayer().getTeam().contains(c))
			playerTurn.setText("   "+model.getFirstPlayer().getName()+"'s "+"Turn");
		else if(model.getSecondPlayer().getTeam().contains(c))
			playerTurn.setText("   "+model.getSecondPlayer().getName()+"'s "+"Turn");
	}
	
	public void placeChampionPhoto(Champion champion) throws IOException {
		JButton button = locationsOfBoard[(int) (champion.getLocation().getX()) ][(int) ( champion.getLocation().getY() )];
		
	    BufferedImage image = null;
	    image = ImageIO.read(new File("photos/"+champion.getName()+".png"));
		Image resizedImage = image.getScaledInstance(80,80,Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(resizedImage);
		button.setIcon(icon);
		
		this.revalidate();
		this.repaint();
	}
	
	
	
	public void removeChampionPhoto(Champion champion) {
		JButton button = locationsOfBoard[(int) (champion.getLocation().getX()) ][(int) ( champion.getLocation().getY() )];
		button.setIcon(null);
		this.revalidate();
		this.repaint();
	}
	
	public void greenBackground (Champion champion) {
		JButton button = locationsOfBoard[(int) (champion.getLocation().getX()) ][(int) ( champion.getLocation().getY() )];
		button.setBackground(Color.green);
	}
	
	public void removeGreenBackground (Champion champion) {
		JButton button = locationsOfBoard[(int) (champion.getLocation().getX()) ][(int) ( champion.getLocation().getY() )];
		button.setBackground(null);
	}
	
	public void changeTurnArea() {
		PriorityQueue queue = model.getTurnOrder();
		PriorityQueue temp = new PriorityQueue(6);
		String string = " ";
		
		while(! queue.isEmpty()) {
			Champion c = (Champion) queue.remove();
			temp.insert(c);
			string+=c.getName()+" - ";
		}
		while(! temp.isEmpty()) 
			queue.insert(temp.remove());
		
		turnArea.setText(string);
	}
	
	
	
	public void placeCoverPhoto(Cover cover) throws IOException {
		JButton button = locationsOfBoard[(int) (cover.getLocation().getX()) ][(int) ( cover.getLocation().getY() )];
		
	    BufferedImage image = null;
	    image = ImageIO.read(new File("photos/cover-block.png"));
		Image resizedImage = image.getScaledInstance(80,80,Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(resizedImage);
		
		button.setText(cover.getCurrentHP()+"");
		button.setForeground(Color.white);
		button.setHorizontalTextPosition( SwingConstants.CENTER );
		button.setIcon(icon);
	}
	
	public void changeCoverHp(Cover cover) {
		JButton button = locationsOfBoard[(int) (cover.getLocation().getX()) ][(int) ( cover.getLocation().getY() )];
		button.setText(cover.getCurrentHP()+"");
	}
	
	public void removeCoverPhoto(Cover cover) throws IOException {
		JButton button = locationsOfBoard[(int) (cover.getLocation().getX()) ][(int) ( cover.getLocation().getY() )];
		button.setIcon(null);
		button.setText(null);
	}
	
	public int indexOfChamp(Champion[] arr,Champion c) {
		
		for(int i=0 ; i<6 ; i++) {
			if(championsArray[i]==c)
				return i;
		}
		return -1;
	}
	
	public void changeChampionArea(Champion c) {
		
		
		int i = indexOfChamp(championsArray, c);
		
		String data = c.displayChampionData();
		if(c.getCurrentHP() == 0) {
			championsTextAreas[i].setText("   DEAD");
			championsTextAreas[i].setFont(new Font("palatino",Font.BOLD,30));
			championsTextAreas[i].setForeground(Color.red);
		}
		else {
			if(c == model.getFirstPlayer().getLeader() || c == model.getSecondPlayer().getLeader())
				championsTextAreas[i].setText(data+"\n"+ "   Leader");
			else
				championsTextAreas[i].setText(data);
		}
		
	}
	
	public void changeAbilityArea(Champion c) {
		int i = indexOfChamp(championsArray, c);
		if(c.getCurrentHP() == 0) {
			championsAbilityText[i].setText("   DEAD");
			championsAbilityText[i].setFont(new Font("palatino",Font.BOLD,30));
			championsAbilityText[i].setForeground(Color.red);
		}
		else
			championsAbilityText[i].setText(c.displayAbilitiesData());
	}
	
	public void changeLeaderAbilityIcon(Champion c) throws IOException {
	    BufferedImage image = null;
	    image = ImageIO.read(new File("photos/x-mark.png"));
		Image resizedImage = image.getScaledInstance(40,40,Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(resizedImage);
		
		if(c == model.getFirstPlayer().getLeader())
			playerOneLA.setIcon(icon);
		else if(c == model.getSecondPlayer().getLeader())
			playerTwoLA.setIcon(icon);
		
	}
	public JButton[][] getLocationsOfBoard() {
		return locationsOfBoard;
	}
	
	public void changeAbilityBoxes() {
		try {
			healingBox.removeAllItems();
		}
		catch (Exception e) {
			
		}
		try {
			damagingBox.removeAllItems();
		}
		catch (Exception e) {
			
		}
		try {
			crowdBox.removeAllItems();
		}
		catch (Exception e) {
			
		}
		
		healingBox.addItem(null);
		damagingBox.addItem(null);
		crowdBox.addItem(null);
		
		for(Ability a : model.getCurrentChampion().getAbilities()) {
			if(a instanceof HealingAbility)
				healingBox.addItem(a.getCastArea());
			else if(a instanceof DamagingAbility)
				damagingBox.addItem(a.getCastArea());
			else
				crowdBox.addItem(a.getCastArea());
		}
	}
	
	public JButton getEndTurnButton() {
		return endTurnButton;
	}

	public JTextArea getTurnArea() {
		return turnArea;
	}

	public JButton getUpAttack() {
		return downAttack;
	}

	public JButton getDownAttack() {
		return upAttack;
	}

	public JButton getLeftAttack() {
		return leftAttack;
	}

	public JButton getRightAttack() {
		return rightAttack;
	}
	
	
	public JButton getUseLA() {
		return useLA;
	}

	public JComboBox<AreaOfEffect> getHealingBox() {
		return healingBox;
	}

	public JComboBox<AreaOfEffect> getDamagingBox() {
		return damagingBox;
	}

	public JComboBox<AreaOfEffect> getCrowdBox() {
		return crowdBox;
	}

	public JComboBox<Direction> getDirectionBox() {
		return directionBox;
	}
	
	public JComboBox<Integer> getChooseX() {
		return chooseX;
	}

	public JComboBox<Integer> getChooseY() {
		return chooseY;
	}

	public JTextArea getPlayerTurn() {
		return playerTurn;
	}

	public void generateSound(String filePath) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}
}
