package views;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.swing.JOptionPane;

import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.world.Direction;

public class GameClient {
    Socket clientSocket;
    DataOutputStream writer;
    BufferedReader reader;   
    ActionsReader actionsReader; 
    
    Controller controller;
    
    public GameClient(Controller controller){
        try {
        	this.controller=controller;
        	clientSocket = new Socket("localhost", 6436);
            writer = new DataOutputStream(clientSocket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            actionsReader = new ActionsReader();
            actionsReader.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void writeToServer(String action){
        try {
            writer.writeBytes(action + '\n');
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    
    public void applyAction(String action){
        System.out.println("Applying action:"+action);
        try {
        	if(action.charAt(0) == 'n') {
        		if(action.charAt(1) == '1')
        			controller.getFirstStage().getFirstName().setText(action.substring(2));
        		else if(action.charAt(1) == '2')
        			controller.getFirstStage().getSecondName().setText(action.substring(2));
        	}
        	else if(action.charAt(0)=='s') {
        		controller.getSecondStage().selectChampion(Integer.parseInt(action.substring(1)));
    			controller.generateSound("sound-effects/select-champ.wav");
        	}
        	else if(action.charAt(0) == 'l') {
        		controller.getSecondStage().selectLeader(action.charAt(1), Integer.parseInt(action.substring(2)));
        	}
        	else if(action.equals("endTurn")) { // End Turn Request
        		controller.getModel().endTurn();
        		controller.generateSound("sound-effects/well-done.wav");
        	}
        	else if(action.equals("ula")) { // Use Leader Ability Request
        		controller.getModel().useLeaderAbility();
        		controller.generateSound("sound-effects/ULA3.wav");
        	}
        	else if(action.charAt(0) == 'h' && controller.getThirdStage() != null) {
        		int i = Integer.parseInt(action.charAt(1)+"");
        		int hp = Integer.parseInt(action.substring(2));
        		controller.getModel().getGameCovers().get(i).setCurrentHP(hp);
        		controller.getThirdStage().changeCoverHp(controller.getModel().getGameCovers().get(i));
        	}
        	else if(action.charAt(0) == 'm') { //Move Request
		        if(action.substring(1).equals("LEFT"))
		            controller.getModel().move(Direction.LEFT);
		        else if(action.substring(1).equals("RIGHT"))
		            controller.getModel().move(Direction.RIGHT);
		        else if(action.substring(1).equals("UP"))
		            controller.getModel().move(Direction.UP);
		        else if(action.substring(1).equals("DOWN"))
		            controller.getModel().move(Direction.DOWN);
	        }  
	        else if(action.charAt(0) == 'a') { // Attack Request
	        	 if(action.substring(1).equals("LEFT"))
	 	            controller.getModel().attack(Direction.LEFT);
	 	        else if(action.substring(1).equals("RIGHT"))
	 	            controller.getModel().attack(Direction.RIGHT);
	 	        else if(action.substring(1).equals("UP"))
	 	            controller.getModel().attack(Direction.UP);
	 	        else if(action.substring(1).equals("DOWN"))
	 	            controller.getModel().attack(Direction.DOWN);
	        }
	        else if(action.charAt(0)=='1' || action.charAt(0) == '2' || action.charAt(0) == '3') { // CastAbility Request
	        	Ability castedAbility = null;
	        	if(action.charAt(0) == '1') { // CastAbility( Ability a)
	        		castedAbility = controller.getModel().findAbilityByName(action.substring(1));
	        		controller.getModel().castAbility(castedAbility);
	        	}
	        	else if(action.charAt(0) == '2') { // CastAbility( Ability a , Direction d)
	        		Direction d = null;
	        		switch(action.charAt(1)) {
	        			case 'U' : d= Direction.UP;break; 
	        			case 'D' : d= Direction.DOWN;break;
	        			case 'R' : d= Direction.RIGHT;break;
	        			case 'L' : d= Direction.LEFT;break;
	        		}
	        		castedAbility = controller.getModel().findAbilityByName(action.substring(2));
	        		controller.getModel().castAbility(castedAbility, d);
	        	}
	        	else if(action.charAt(0) == '3') { // CastAbility( Ability a , int x , int y)
	        		castedAbility = controller.getModel().findAbilityByName(action.substring(3));
	        		controller.getModel().castAbility(castedAbility, Integer.parseInt(action.charAt(1)+""), Integer.parseInt(action.charAt(2)+""));
	        	}
	        }
	        else if(action.equals("gameover")) {
	        	controller.getFirstStage().dispose();
	        	controller.getSecondStage().dispose();
	        	controller.getThirdStage().dispose();
	        }
        }
        catch (UnallowedMovementException | NotEnoughResourcesException | IOException | ChampionDisarmedException | InvalidTargetException | LeaderAbilityAlreadyUsedException | LeaderNotCurrentException | CloneNotSupportedException | AbilityUseException e) {
			JOptionPane.showMessageDialog(controller.getThirdStage(), e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		}
        System.out.println("action applied");
    }

    class ActionsReader extends Thread{
        @Override
        public void run() {
            String action;
            while (true){
                try {
                    action = reader.readLine();
                    System.out.println("FROM SERVER: " + action);
                    applyAction(action);
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    
}
