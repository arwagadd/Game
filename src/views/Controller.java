package views;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.management.loading.PrivateClassLoader;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.text.StyledEditorKit.ForegroundAction;
import javax.tools.DocumentationTool.Location;

import engine.Game;
import engine.Player;
import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.DamagingAbility;
import model.world.Champion;
import model.world.Cover;
import model.world.Direction;
import views.GameServer.Client;

public class Controller implements ActionListener{
	
	private StageOne firstStage;
	private StageTwo secondStage;
	private StageThree thirdStage;
	
	private Game model;
	private ArrayList<JButton> allButtons;
	

	
	private GameClient client;
	
	public Controller() throws IOException {
		client = new GameClient(this);
		
		firstStage = new StageOne();
		firstStage.getStart().addActionListener(this);
		firstStage.getDoneTyping().addActionListener(this);
		generateSound("sound-effects/game-starts.wav");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if(e.getSource() == firstStage.getDoneTyping()) {
				String playerOneName = firstStage.getFirstName().getText();
				String playerTwoName = firstStage.getSecondName().getText();

				if(playerOneName != null) {
					client.writeToServer("n1"+playerOneName);
				}
				if(playerTwoName != null) {
					client.writeToServer("n2"+playerTwoName);
				}
			}
			else if( e.getSource() == firstStage.getStart() ){
				if(firstStage.getFirstName().getText().equals("") || firstStage.getSecondName().getText().equals("")) {
					JOptionPane.showMessageDialog(firstStage, "Please enter both players names ","Error",JOptionPane.ERROR_MESSAGE);
				}
				else {
					firstStage.setVisible(false);
					Player firstPlayer = new Player(firstStage.getFirstName().getText());
					Player secondPlayer = new Player(firstStage.getSecondName().getText());
					model = new Game(firstPlayer, secondPlayer);
					System.out.println("model "+model);
					ArrayList<Champion> championsList= model.getAvailableChampions();

					secondStage = new StageTwo(firstPlayer, secondPlayer, championsList);
					
					ArrayList<JButton> secondStageButtons = secondStage.getChampionsButtons();
					for(int i=0 ; i<secondStageButtons.size() ; i++) {
						secondStageButtons.get(i).addActionListener(this);
					}
					secondStage.getSelectPlayerOneLeader().addActionListener(this);
					secondStage.getSelectPlayerTwoLeader().addActionListener(this);

					secondStage.getStartBattle().addActionListener(this);
					//generateSound("sound-effects/game-starts.wav");
				}
			}
			else if( secondStage.getChampionsButtons().contains(e.getSource()) ) {
				
				int index = secondStage.getChampionsButtons().indexOf( e.getSource() );
				secondStage.selectChampion(index);
				client.writeToServer("s"+index);
			}
			else if(e.getSource() == secondStage.getSelectPlayerOneLeader()) {
				
				String championName = (String) secondStage.getSelectPlayerOneLeader().getSelectedItem();
				Champion selected = secondStage.getChampionByName(championName, model.getAvailableChampions());
				int index = model.getAvailableChampions().indexOf(selected);
				secondStage.selectLeader('f', index);
				client.writeToServer("lf"+index);
			}
			
			else if(e.getSource() == secondStage.getSelectPlayerTwoLeader()) {
				String championName = (String) secondStage.getSelectPlayerTwoLeader().getSelectedItem();
				Champion selected = secondStage.getChampionByName(championName, model.getAvailableChampions());
				int index = model.getAvailableChampions().indexOf(selected);
				secondStage.selectLeader('s', index);
				client.writeToServer("ls"+index);

			}
			else if(e.getSource() == secondStage.getStartBattle()) {
				if(model.getFirstPlayer().getLeader()==null || model.getSecondPlayer().getLeader()==null)
					JOptionPane.showMessageDialog(firstStage, "Please choose the leader of each player","Error",JOptionPane.ERROR_MESSAGE);
				else if(model.getFirstPlayer().getTeam().size()!=3 || model.getSecondPlayer().getTeam().size()!=3)
					JOptionPane.showMessageDialog(firstStage, "Each player has to choose 3 champions","Error",JOptionPane.ERROR_MESSAGE);
				else {
					secondStage.setVisible(false);
					model.placeChampions();
					model.prepareChampionTurns();
					thirdStage = new StageThree(model);
					allButtons = new ArrayList<JButton>();

					thirdStage.greenBackground(model.getCurrentChampion());
					thirdStage.changeTurnArea();
					
				
					thirdStage.getEndTurnButton().addActionListener(this);
					thirdStage.getUpAttack().addActionListener(this);
					thirdStage.getDownAttack().addActionListener(this);
					thirdStage.getLeftAttack().addActionListener(this);
					thirdStage.getRightAttack().addActionListener(this);
					thirdStage.getUseLA().addActionListener(this);
					
					thirdStage.getHealingBox().addActionListener(this);
					thirdStage.getDamagingBox().addActionListener(this);
					thirdStage.getCrowdBox().addActionListener(this);
					thirdStage.getDirectionBox().addActionListener(this);
					thirdStage.getChooseX().addActionListener(this);
					thirdStage.getChooseY().addActionListener(this);

					model.setListener(thirdStage);
					
					JButton[][] buttonsList = thirdStage.getLocationsOfBoard();
					
					for(int i=0 ; i<5 ; i++) {
						for(int j=0 ; j<5 ; j++) { 
							//buttonsList[i][j].addKeyListener(this);
							buttonsList[i][j].addActionListener(this);
							allButtons.add(buttonsList[i][j]);
						}
					}
					
					thirdStage.changeAbilityBoxes();
					
					ArrayList<Cover> gameCovers = model.getGameCovers();
					for(Cover cover : gameCovers) 
						client.writeToServer("h"+gameCovers.indexOf(cover)+""+cover.getCurrentHP()); // reflect cover hp request
				}
				
				
			}
			
			else if(e.getSource() == thirdStage.getEndTurnButton()) {
				model.endTurn();
				client.writeToServer("endTurn");
				return;
			}
			
			else if(allButtons.contains(e.getSource())) {
				JButton btn = (JButton) e.getSource();
				Point clicked = clickedPoint(thirdStage.getLocationsOfBoard(), btn);
				if(model.manhattan(model.getCurrentChampion().getLocation(), clicked) > 1)
					return;
				int cX = (int) model.getCurrentChampion().getLocation().getX();
				int cY = (int) model.getCurrentChampion().getLocation().getY();
				int bX = (int) clicked.getX();
				int bY = (int) clicked.getY();
				
				if(bX<cX && bY==cY) {
					model.move(Direction.DOWN);
					client.writeToServer("mDOWN");
				}
				else if(bX>cX && bY == cY) {
					model.move(Direction.UP);
					client.writeToServer("mUP");
				}
				else if(bX==cX && bY < cY) {
					model.move(Direction.LEFT);
					client.writeToServer("mLEFT");
				}
				else if(bX==cX && bY > cY) {
					model.move(Direction.RIGHT);
					client.writeToServer("mRIGHT");
				}
					
			}
			
			else if(e.getSource() == thirdStage.getUpAttack()) {
				model.attack(Direction.UP);
				client.writeToServer("aUP");
			}
			
			else if(e.getSource() == thirdStage.getDownAttack()) {
				model.attack(Direction.DOWN);
				client.writeToServer("aDOWN");
			}
			
			else if(e.getSource() == thirdStage.getLeftAttack()) {
				model.attack(Direction.LEFT);
				client.writeToServer("aLEFT");
			}
			
			else if(e.getSource() == thirdStage.getRightAttack()) {
				model.attack(Direction.RIGHT);
				client.writeToServer("aRIGHT");
			}
			
			else if(e.getSource() == thirdStage.getUseLA()) {
				model.useLeaderAbility();
				client.writeToServer("ula");
			}
			
			else if(e.getSource() == thirdStage.getHealingBox() || e.getSource()==thirdStage.getDamagingBox() || e.getSource()==thirdStage.getCrowdBox()) {
				JComboBox<AreaOfEffect> box;
				
				if(e.getSource()==thirdStage.getHealingBox())
					box = thirdStage.getHealingBox();
				else if(e.getSource() == thirdStage.getDamagingBox())
					box = thirdStage.getDamagingBox();
				else
					box = thirdStage.getCrowdBox();
				
				AreaOfEffect areaOfEffect = (AreaOfEffect) box.getSelectedItem();
				Ability castedAbility = null;
				
				for (Ability a : model.getCurrentChampion().getAbilities()) {
					if(a.getCastArea() == areaOfEffect)
						castedAbility = a ;
				}
				
				if(areaOfEffect == AreaOfEffect.SELFTARGET || areaOfEffect == AreaOfEffect.SURROUND || areaOfEffect == AreaOfEffect.TEAMTARGET) {
					model.castAbility(castedAbility);
					client.writeToServer("1"+castedAbility.getName());
				}
				
				else if(areaOfEffect == AreaOfEffect.DIRECTIONAL) {
					
					Direction direction = (Direction) thirdStage.getDirectionBox().getSelectedItem();
					
					if(direction == null) {
						generateSound("sound-effects/buzzer.wav");
						JOptionPane.showMessageDialog(thirdStage, "Please select direction before casting the ability","Error",JOptionPane.ERROR_MESSAGE);
						return;
					}
					if(direction == Direction.UP)
						direction = Direction.DOWN;
					else if(direction == Direction.DOWN)
						direction = Direction.UP;
					
					model.castAbility(castedAbility, direction);
					client.writeToServer("2"+direction.toString().charAt(0)+castedAbility.getName());
				}
				
				else if(areaOfEffect == AreaOfEffect.SINGLETARGET) {
					Integer x = (Integer) thirdStage.getChooseX().getSelectedItem();
					Integer y = (Integer) thirdStage.getChooseY().getSelectedItem();
					if(x == null || y == null) {
						generateSound("sound-effects/buzzer.wav");
						JOptionPane.showMessageDialog(thirdStage, "Please select target coordinates before casting the ability","Error",JOptionPane.ERROR_MESSAGE);
						return;
					}
					model.castAbility(castedAbility, 4-x, y);
					int xServer = 4-x;
					int yServer = y;
					client.writeToServer("3"+xServer+yServer+castedAbility.getName());
				}
				
			}
			
			
			if(e.getSource() != firstStage.getDoneTyping()) {
				Player player = model.checkGameOver();
				if(player != null && e.getSource() != firstStage.getStart() && thirdStage != null) {
					generateSound("sound-effects/victory.wav");
					JOptionPane.showMessageDialog(firstStage, player.getName()+" wins the battle","Game Over",JOptionPane.ERROR_MESSAGE);
					thirdStage.dispose();
					secondStage.dispose();
					firstStage.dispose();
					client.writeToServer("gameover");
				}
			}
		}
		catch (IOException | ChampionDisarmedException | NotEnoughResourcesException | LeaderAbilityAlreadyUsedException | LeaderNotCurrentException | CloneNotSupportedException | InvalidTargetException | AbilityUseException | UnallowedMovementException exception) {
			generateSound("sound-effects/buzzer.wav");
			JOptionPane.showMessageDialog(firstStage, exception.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
		
		
		
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
	
	public static Point clickedPoint(JButton[][] arr,JButton btn) {
		for(int i=0 ; i<5 ; i++) {
			for(int j=0 ; j<5 ; j++) {
				if(arr[i][j] == btn)
					return new Point(i,j);
			}
		}
		return null;
	}
	public static void main(String[] args) throws IOException {
		Controller controller = new Controller();

		
	}





	public StageOne getFirstStage() {
		return firstStage;
	}

	public StageTwo getSecondStage() {
		return secondStage;
	}

	public StageThree getThirdStage() {
		return thirdStage;
	}

	public Game getModel() {
		return model;
	}


	


	
	
	

}
