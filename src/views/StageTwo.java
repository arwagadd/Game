package views;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import engine.Player;
import model.world.Champion;

public class StageTwo extends JFrame implements ActionListener {
	
	private Player firstPlayer;
	private Player secondPlayer;
	private ArrayList<Champion> championsList;
	private ArrayList<JButton> championsButtons;
	private JPanel firstPlayerTeamImages;
	private JPanel secondPlayerTeamImages;
	private JComboBox<String> selectPlayerOneLeader;
	private JComboBox<String> selectPlayerTwoLeader;
	private JButton startBattle;
	
	public ArrayList<JButton> getChampionsButtons(){
		return championsButtons;
	}
	
	public StageTwo(Player firstPlayer ,Player secondPlayer , ArrayList<Champion> championsList) {
		this.firstPlayer=firstPlayer;
		this.secondPlayer = secondPlayer;
		this.championsList=championsList;
		championsButtons = new ArrayList<JButton>();
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		int windowWidth = 1280;
		int windowHeight = 720;
		this.setBounds(0,0,1280,720);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		this.getContentPane().setBackground(new Color(217, 224, 231));
		
		Border border = BorderFactory.createLineBorder(Color.black, 1);

		JPanel selectionPanel = new JPanel();
		selectionPanel.setBounds(240, 0, 800, 500);
		selectionPanel.setLayout(new GridLayout(3,5,5,5));
		selectionPanel.setBackground(null);
		this.add(selectionPanel);
		
		for(Champion champion : championsList) {
			JTextArea data = new JTextArea(champion.toString());
			data.setEditable(false);
			data.setBorder(border);
			selectionPanel.add(data);
		}
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBounds(240, 520, 800, 84);
		buttonsPanel.setBackground(null);
		buttonsPanel.setLayout(new GridLayout(3,5,5,5));
		this.add(buttonsPanel);
		
		for(int i=0 ; i<championsList.size() ; i++) {
			JButton button = new JButton(championsList.get(i).getName());
			button.setBackground(new Color(20, 171, 84));
			button.setForeground(Color.white);
			championsButtons.add(button);
			button.addActionListener(this);
			buttonsPanel.add(button);
		}
		
		Font fontBold = new Font("Serif",Font.BOLD,20);
		Font mediumFont = new Font("Serif",Font.BOLD,15);
		
		JTextArea playerOneName = new JTextArea("Team Of "+firstPlayer.getName());
		playerOneName.setBounds(10, 25, 220, 35);
		playerOneName.setFont(fontBold);
		playerOneName.setEditable(false);
		playerOneName.setBackground(null);
		this.add(playerOneName);
		
		JTextArea playerTwoName = new JTextArea("Team Of "+secondPlayer.getName());
		playerTwoName.setBounds(1050, 25, 220, 35);
		playerTwoName.setFont(fontBold);
		playerTwoName.setEditable(false);
		playerTwoName.setBackground(null);
		this.add(playerTwoName);
		
		firstPlayerTeamImages = new JPanel();
		firstPlayerTeamImages.setBounds(20, 70, 200, 450);
		firstPlayerTeamImages.setLayout(new GridLayout(3,1,5,5));
		firstPlayerTeamImages.setBackground(null);
		this.add(firstPlayerTeamImages);
		
		secondPlayerTeamImages = new JPanel();
		secondPlayerTeamImages.setBounds(1050, 70, 200, 450);
		secondPlayerTeamImages.setLayout(new GridLayout(3,1,5,5));
		secondPlayerTeamImages.setBackground(null);
		this.add(secondPlayerTeamImages);
		
		selectPlayerOneLeader = new JComboBox();
		selectPlayerOneLeader.setBounds(20,535,200,30);
		selectPlayerOneLeader.setBackground(new Color(239, 47, 53 ));
		selectPlayerOneLeader.setForeground(Color.white);
		selectPlayerOneLeader.setFont(mediumFont);
		selectPlayerOneLeader.addItem("Select Leader");
		selectPlayerOneLeader.addActionListener(this);
		this.add(selectPlayerOneLeader);
		
		selectPlayerTwoLeader = new JComboBox();
		selectPlayerTwoLeader.setBounds(1050,535,200,30);
		selectPlayerTwoLeader.setBackground(new Color(239, 47, 53 ));
		selectPlayerTwoLeader.setForeground(Color.white);
		selectPlayerTwoLeader.setFont(mediumFont);
		selectPlayerTwoLeader.addItem("Select Leader");
		selectPlayerTwoLeader.addActionListener(this);
		this.add(selectPlayerTwoLeader);
		
		startBattle = new JButton("Start Battle");
		startBattle.setBounds(575, 610, 130, 35);
		startBattle.setFont(mediumFont);
		startBattle.setBackground(new Color(214, 54, 59));
		startBattle.setForeground(Color.white);
		this.add(startBattle);
		
		this.setVisible(true);
		this.revalidate();
		this.repaint();
	}

	public JLabel dropChampionPhoto(Champion c) throws IOException {
		JLabel label = new JLabel();
		
	    BufferedImage image = null;
	    image = ImageIO.read(new File("photos/"+c.getName()+".png"));
		Image resizedImage = image.getScaledInstance(200,146,Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(resizedImage);
		label.setIcon(icon);
		return label;
	}
	
	public Champion getChampionByName(String name , ArrayList<Champion> list) {
		for(Champion champion : list) {
			if(champion.getName().equals(name))
				return champion;
		}
		return null;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
//		try {
//			if( championsButtons.contains(e.getSource()) ) {
//				
//				int index = championsButtons.indexOf( e.getSource() );
//				selectChampion(index);
//
//			}
//			else if(e.getSource() == selectPlayerOneLeader) {
//				String string = (String) selectPlayerOneLeader.getSelectedItem();
//				firstPlayer.setLeader(getChampionByName(string, firstPlayer.getTeam()));
//			}
//			
//			else if(e.getSource() == selectPlayerTwoLeader) {
//				String string = (String) selectPlayerTwoLeader.getSelectedItem();
//				secondPlayer.setLeader(getChampionByName(string, secondPlayer.getTeam()));
//			}
			
//		}
//		catch (Exception exception) {
//			exception.printStackTrace();
//		}
		
	}
	
	public JComboBox<String> getSelectPlayerOneLeader() {
		return selectPlayerOneLeader;
	}

	public JComboBox<String> getSelectPlayerTwoLeader() {
		return selectPlayerTwoLeader;
	}

	public JButton getStartBattle () {
		return startBattle;
	}
	
	public void selectChampion(int index) throws IOException {
		Champion c = championsList.get(index);
		
		if(firstPlayer.getTeam().size()<3) {
			firstPlayer.getTeam().add(c);
			championsButtons.get(index).setVisible(false);
			firstPlayerTeamImages.add(dropChampionPhoto(c));
			selectPlayerOneLeader.addItem(c.getName());
			generateSound("sound-effects/select-champ.wav");
		}
		
		else if(secondPlayer.getTeam().size()<3) {
			secondPlayer.getTeam().add(c);
			championsButtons.get(index).setVisible(false);
			secondPlayerTeamImages.add(dropChampionPhoto(c));
			selectPlayerTwoLeader.addItem(c.getName());
			generateSound("sound-effects/select-champ.wav");
		}
		
	}
	
	public void selectLeader(char whichPlayer , int i) {
		if(whichPlayer == 'f')
			firstPlayer.setLeader(championsList.get(i));
		else if (whichPlayer == 's')
			secondPlayer.setLeader(championsList.get(i));
		
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
	
//	public static void main(String[] args) {
//		new StageTwo();
//	}

}
