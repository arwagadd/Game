package views;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class StageOne extends JFrame {
	
	private JTextField firstName;
	private JTextField secondName;
	private JButton start;
	private JButton doneTyping;
	
	
	public JButton getDoneTyping() {
		return doneTyping;
	}
	public JTextField getFirstName() {
		return firstName;
	}

	public JTextField getSecondName() {
		return secondName;
	}

	public JButton getStart() {
		return start;
	}

	public StageOne () throws IOException {
		
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		int windowWidth = 1280;
		int windowHeight = 720;
		this.setBounds(0,0,1280,720);
		this.setLocationRelativeTo(null);
		this.setLayout(null);
		
		
		JLabel introductionImage = new JLabel();
		introductionImage.setBounds(0,0,windowWidth,windowHeight);

	    BufferedImage img = null;
		img = ImageIO.read(new File("photos/start-view-pic.png"));
		Image dimg = img.getScaledInstance(introductionImage.getWidth(), introductionImage.getHeight(),Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(dimg);
		introductionImage.setIcon(icon);
		
		Font font = new Font("Palatino",Font.ITALIC,20);
		Font fontBold = new Font("Palatino",Font.BOLD,20);
		Font med = new Font("Palatino",font.BOLD,15);
		firstName = new JTextField();
		firstName.setBounds(256, 396, 150, 35);
		firstName.setHorizontalAlignment(JTextField.CENTER);
		firstName.setFont(font);
		
		secondName = new JTextField();
		secondName.setBounds(256, 446, 150, 35);
		secondName.setHorizontalAlignment(JTextField.CENTER);
		secondName.setFont(font);
		
		JTextArea firstArea = new JTextArea("Player One : ");
		firstArea.setBounds(128, 396, 130, 35);
		firstArea.setFont(fontBold);
		firstArea.setEditable(false);
		firstArea.setBackground(null);
		
		JTextArea secondArea = new JTextArea("Player Two : ");
		secondArea.setBounds(128, 446, 130, 35);
		secondArea.setFont(fontBold);
		secondArea.setEditable(false);
		secondArea.setBackground(null);
		
		start = new JButton("Start");
		start.setBounds(128, 504, 110, 35);
		start.setBackground(Color.blue);
		start.setForeground(Color.white);
		start.setFont(fontBold);
		start.setFocusable(false);
		
		doneTyping = new JButton("Done Typing");
		doneTyping.setBounds(256, 504, 150, 35);
		doneTyping.setBackground(Color.blue);
		doneTyping.setForeground(Color.white);
		doneTyping.setFont(med);
		doneTyping.setFocusable(false);
		
		this.getContentPane().setBackground(Color.white);
		
		this.add(firstName);
		this.add(secondName);
		this.add(firstArea);
		this.add(secondArea);
		this.add(start);
		this.add(doneTyping);
		this.add(introductionImage);
		this.setVisible(true);
		this.revalidate();
		this.repaint();
	}
	
	public static void main(String[] args) throws IOException {
		new StageOne();
	}
	
}
