package engine;

import java.awt.List;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.http.WebSocket.Listener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import exceptions.AbilityUseException;
import exceptions.ChampionDisarmedException;
import exceptions.InvalidTargetException;
import exceptions.LeaderAbilityAlreadyUsedException;
import exceptions.LeaderNotCurrentException;
import exceptions.NotEnoughResourcesException;
import exceptions.UnallowedMovementException;
import model.abilities.Ability;
import model.abilities.AreaOfEffect;
import model.abilities.CrowdControlAbility;
import model.abilities.DamagingAbility;
import model.abilities.HealingAbility;
import model.effects.Disarm;
import model.effects.Dodge;
import model.effects.Effect;
import model.effects.EffectType;
import model.effects.Embrace;
import model.effects.PowerUp;
import model.effects.Root;
import model.effects.Shield;
import model.effects.Shock;
import model.effects.Silence;
import model.effects.SpeedUp;
import model.effects.Stun;
import model.world.AntiHero;
import model.world.Champion;
import model.world.Condition;
import model.world.Cover;
import model.world.Damageable;
import model.world.Direction;
import model.world.Hero;
import model.world.Villain;
import views.ViewListener;

public class Game {
	private ArrayList<Champion> availableChampions;
	private ArrayList<Ability> availableAbilities;
	private ArrayList<Cover> gameCovers;
	private Player firstPlayer; 
	private Player secondPlayer;
	private Object[][] board;
	private PriorityQueue turnOrder;
	private boolean firstLeaderAbilityUsed;
	private boolean secondLeaderAbilityUsed;
	private int BOARDWIDTH = 5;
	private int BOARDHEIGHT = 5;
	private ViewListener listener;
	
	
	
	public void setListener(ViewListener listener) {
		this.listener = listener;
	}

	public Game(Player first, Player second) throws IOException {
		
		firstPlayer = first;
		secondPlayer = second;
		availableChampions = new ArrayList<Champion>();
		availableAbilities = new ArrayList<Ability>();
		gameCovers = new ArrayList<Cover>();
		
		board = new Object[BOARDWIDTH][BOARDHEIGHT];
		turnOrder = new PriorityQueue(6);
		loadAbilities("Abilities.csv");
		loadChampions("Champions.csv");
		//placeChampions();
		placeCovers();
		//prepareChampionTurns();
		
	}

	public void loadAbilities(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Ability a = null;
			AreaOfEffect ar = null;
			switch (content[5]) {
			case "SINGLETARGET":
				ar = AreaOfEffect.SINGLETARGET;
				break;
			case "TEAMTARGET":
				ar = AreaOfEffect.TEAMTARGET;
				break;
			case "SURROUND":
				ar = AreaOfEffect.SURROUND;
				break;
			case "DIRECTIONAL":
				ar = AreaOfEffect.DIRECTIONAL;
				break;
			case "SELFTARGET":
				ar = AreaOfEffect.SELFTARGET;
				break;

			}
			Effect e = null;
			if (content[0].equals("CC")) {
				switch (content[7]) {
				case "Disarm":
					e = new Disarm(Integer.parseInt(content[8]));
					break;
				case "Dodge":
					e = new Dodge(Integer.parseInt(content[8]));
					break;
				case "Embrace":
					e = new Embrace(Integer.parseInt(content[8]));
					break;
				case "PowerUp":
					e = new PowerUp(Integer.parseInt(content[8]));
					break;
				case "Root":
					e = new Root(Integer.parseInt(content[8]));
					break;
				case "Shield":
					e = new Shield(Integer.parseInt(content[8]));
					break;
				case "Shock":
					e = new Shock(Integer.parseInt(content[8]));
					break;
				case "Silence":
					e = new Silence(Integer.parseInt(content[8]));
					break;
				case "SpeedUp":
					e = new SpeedUp(Integer.parseInt(content[8]));
					break;
				case "Stun":
					e = new Stun(Integer.parseInt(content[8]));
					break;
				}
			}
			switch (content[0]) {
			case "CC":
				a = new CrowdControlAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), e);
				break;
			case "DMG":
				a = new DamagingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			case "HEL":
				a = new HealingAbility(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[4]),
						Integer.parseInt(content[3]), ar, Integer.parseInt(content[6]), Integer.parseInt(content[7]));
				break;
			}
			availableAbilities.add(a);
			line = br.readLine();
		}
		br.close();
	}

	public void loadChampions(String filePath) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		while (line != null) {
			String[] content = line.split(",");
			Champion c = null;
			switch (content[0]) {
			case "A":
				c = new AntiHero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;

			case "H":
				c = new Hero(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			case "V":
				c = new Villain(content[1], Integer.parseInt(content[2]), Integer.parseInt(content[3]),
						Integer.parseInt(content[4]), Integer.parseInt(content[5]), Integer.parseInt(content[6]),
						Integer.parseInt(content[7]));
				break;
			}

			c.getAbilities().add(findAbilityByName(content[8]));
			c.getAbilities().add(findAbilityByName(content[9]));
			c.getAbilities().add(findAbilityByName(content[10]));
			availableChampions.add(c);
			line = br.readLine();
		}
		br.close();
	}

	public Ability findAbilityByName(String name) {
		for (Ability a : availableAbilities) {
			if (a.getName().equals(name))
				return a;
		}
		return null;
	}

	public void placeCovers() {
//		int i = 0;
//		while (i < 5) {
//			int x = ((int) (Math.random() * (BOARDWIDTH - 2))) + 1;
//			int y = (int) (Math.random() * BOARDHEIGHT);
//
//			if (board[x][y] == null) {
//				board[x][y] = new Cover(x, y);
//				gameCovers.add((Cover) board[x][y]);
//				i++;
//			}
//		}
		
		board[1][1] = new Cover(1, 1);
		gameCovers.add((Cover) board[1][1]);
		
		board[1][3] = new Cover(1, 3);
		gameCovers.add((Cover) board[1][3]);

		board[3][1]	= new Cover(3, 1);
		gameCovers.add((Cover) board[3][1]);

		board[3][3] = new Cover(3, 3);
		gameCovers.add((Cover) board[3][3]);

		board[2][2] = new Cover(2, 2);
		gameCovers.add((Cover) board[2][2]);

	}

	public void placeChampions() {
		int i = 1;
		for (Champion c : firstPlayer.getTeam()) {
			board[0][i] = c;
			c.setLocation(new Point(0, i));
			i++;
		}
		i = 1;
		for (Champion c : secondPlayer.getTeam()) {
			board[BOARDHEIGHT - 1][i] = c;
			c.setLocation(new Point(BOARDHEIGHT - 1, i));
			i++;
		}
	
	}

	public ArrayList<Champion> getAvailableChampions() {
		return availableChampions;
	}

	public ArrayList<Ability> getAvailableAbilities() {
		return availableAbilities;
	}

	public Player getFirstPlayer() {
		return firstPlayer;
	}

	public Player getSecondPlayer() {
		return secondPlayer;
	}

	public Object[][] getBoard() {
		return board;
	}

	public PriorityQueue getTurnOrder() {
		return turnOrder;
	}

	public boolean isFirstLeaderAbilityUsed() {
		return firstLeaderAbilityUsed;
	}

	public boolean isSecondLeaderAbilityUsed() {
		return secondLeaderAbilityUsed;
	}

	public  int getBoardwidth() {
		return BOARDWIDTH;
	}

	public  int getBoardheight() {
		return BOARDHEIGHT;
	}
	
	
	
	 public ArrayList<Cover> getGameCovers() {
		return gameCovers;
	}

	public Champion getCurrentChampion()
	 {
		 return (Champion)(turnOrder.peekMin());
		
		 
	 }
	 
	 public Player checkGameOver()
	 {
		 boolean FirstTeamDefeated = true;
		 boolean SecondTeamDefeated = true;
		 
		 for(int i=0;i<firstPlayer.getTeam().size();i++)
		 {
			 if(firstPlayer.getTeam().get(i).getCondition()!=Condition.KNOCKEDOUT)
				 FirstTeamDefeated = false;
		 }
		 for(int i=0;i<secondPlayer.getTeam().size();i++)
		 {
			 if(secondPlayer.getTeam().get(i).getCondition()!=Condition.KNOCKEDOUT)
				 SecondTeamDefeated = false;
		 }
		 
		 if(FirstTeamDefeated==true)
			 return secondPlayer;
		 else if(SecondTeamDefeated==true)
			 return firstPlayer;
		 else return null;
			 
	 }
	 
	 public void move(Direction d) throws  UnallowedMovementException, NotEnoughResourcesException, IOException
	 {
		 Champion c = getCurrentChampion();
		 int xprev = (int) c.getLocation().getX();
		 int yprev = (int) c.getLocation().getY();

		 //all possible exceptions:
		 
		 if(c.getCurrentActionPoints()==0)
			 throw new NotEnoughResourcesException("Champion does not have enough action points.");
		 
		 if(c.getCondition()==Condition.ROOTED)
			 throw new UnallowedMovementException("The champion cannot move due to being rooted.");
		 
		 Point newPoint = locateTo(c.getLocation(),d);
		 
		 if(newPoint.getX()<0 || newPoint.getY()<0 || newPoint.getX() >= BOARDHEIGHT || newPoint.getY() >= BOARDWIDTH)
				throw new UnallowedMovementException("Champion cannot move out of the board");
		 
		if(board[(int)newPoint.getX()][(int)newPoint.getY()]!=null)
			throw new UnallowedMovementException("Champion cannot move to a none empty cell");
		
		
		listener.removeChampionPhoto(c);
		listener.removeGreenBackground(c);
		

		c.setLocation(newPoint);
		
        
		listener.placeChampionPhoto(c);
		listener.greenBackground(c);
		
		listener.generateSound("sound-effects/movement.wav");
		
		board[(int) newPoint.getX()][(int) newPoint.getY()] = c;
		board[xprev][yprev]=null;
		c.setCurrentActionPoints(c.getCurrentActionPoints()-1);
		listener.changeChampionArea(c);
    }

	public Point locateTo(Point location, Direction d) {
		int x = (int)location.getX();
		int y = (int)location.getY();
		
		switch(d) 
		{
		case RIGHT : return new Point(x,y+1);
		case LEFT : return new Point(x,y-1);
		case UP : return new Point(x+1,y);
		case DOWN : return new Point(x-1,y);
		}
		
		return null; //will never return null
		
		
	}
	  public void prepareChampionTurnsH(Player X) {
	    	 for(int i=0;i<X.getTeam().size();i++) {
	    		 if(X.getTeam().get(i).getCondition()==Condition.KNOCKEDOUT) {
	    			 continue;
	    		 }
	    		 else {
	    			 turnOrder.insert(X.getTeam().get(i));
	    		 }
	    	 }
	    	 
	    	 
	     }	 
	  public void prepareChampionTurns() {
	    	 
	    	 prepareChampionTurnsH(firstPlayer);
	    	 prepareChampionTurnsH(secondPlayer);
	    	 
	     }
	

	public void endTurn() {
		listener.generateSound("sound-effects/well-done.wav");
		Champion oldChampion = (Champion) turnOrder.remove();
		listener.removeGreenBackground(oldChampion);
		listener.changeTurnArea();
		
		if(turnOrder.isEmpty()) {
			prepareChampionTurns();
		}
		listener.changePlayerTurnArea();

		while(!turnOrder.isEmpty()) {
			Champion c = (Champion) turnOrder.peekMin();
			if(c.getCondition()==Condition.INACTIVE) {
				updateCoolDownofAbility(c);
				updateDurationofEffect(c);
				listener.changeChampionArea(c);
				listener.changeAbilityArea(c);
				turnOrder.remove();
				
				if(turnOrder.isEmpty())
					prepareChampionTurns();
				
				listener.changePlayerTurnArea();
				listener.changeTurnArea();			 

			}
			else {
				listener.greenBackground(c);
				updateCoolDownofAbility(c);
				updateDurationofEffect(c);
				c.setCurrentActionPoints(c.getMaxActionPointsPerTurn());
				listener.changeChampionArea(c);
				listener.changeAbilityArea(c);
				listener.changeAbilityBoxes();
				break;
				
			}
		}
	
		
	}
	
	
	
	public void updateDurationofEffect(Champion c) {
		Iterator<Effect> it = c.getAppliedEffects().iterator();
			while(it.hasNext()) {
				Effect e = it.next();
				e.setDuration(e.getDuration()-1);
				if(e.getDuration()==0) {
					it.remove();  //remove from the Applied Efects of the champ
					e.remove(c);  //call the method of remove found in the Effect subclasses
				}
			}
	}

	public void updateCoolDownofAbility(Champion c) {
		for(Ability ab : c.getAbilities())
		{
			ab.setCurrentCooldown(ab.getCurrentCooldown()-1);
		}
	}

	public void useLeaderAbility() throws LeaderAbilityAlreadyUsedException, LeaderNotCurrentException, CloneNotSupportedException, IOException {
		
		Champion current = getCurrentChampion();
		
		ArrayList<Champion> target = new ArrayList<Champion>();
		
		if( (current == firstPlayer.getLeader() && firstLeaderAbilityUsed ) || (current == secondPlayer.getLeader() && secondLeaderAbilityUsed))
				
			throw new LeaderAbilityAlreadyUsedException("Leader ability has been used already.");
		
		if((current!= firstPlayer.getLeader()) && current!=secondPlayer.getLeader())
			throw new LeaderNotCurrentException("The current champion is not the leader.");
		
		if(current instanceof Hero) {
			if(firstPlayer.getTeam().contains(current) ) {
				target=firstPlayer.getTeam();
			}
			else target=secondPlayer.getTeam();
		}
		
		else if(current instanceof Villain) {
			if(firstPlayer.getTeam().contains(current) ) {
				for(Champion c : secondPlayer.getTeam()) {
					if( c.getCurrentHP()<c.getMaxHP()*0.3)
						target.add(c);
				}
			}
			else {
				for(Champion c : firstPlayer.getTeam()) {
					if( c.getCurrentHP()<c.getMaxHP()*0.3)
						target.add(c);
				}
			}
		}
		
		else if (current instanceof AntiHero){ 	// if the current champion is an antihero
			for(Champion champ : firstPlayer.getTeam())
			{
				if(champ!=firstPlayer.getLeader()) {
					target.add(champ);
				}
					
			}
			
			for(Champion champ : secondPlayer.getTeam())
			{
				if(champ!=secondPlayer.getLeader()) {
					target.add(champ);
				}
					
			}
			
		}
		
		current.useLeaderAbility(target);
		
		if(current==firstPlayer.getLeader()) {	//if the current champ is done using the leader ability
			firstLeaderAbilityUsed = true;
		}
		else {
			secondLeaderAbilityUsed = true;
		}
		
		if(current instanceof Villain) {
			for(Champion champion : target)
				eliminateTheDead(champion);
		}
		
		for(Champion champion :target) {
			listener.changeChampionArea(champion);
		}
		
		listener.changeLeaderAbilityIcon(current);
		listener.generateSound("sound-effects/ULA3.wav");
		
	}
	
	
	public int manhattan(Point start, Point end) {
		return Math.abs(((int)start.getX()-(int)end.getX()))+Math.abs((int)start.getY()-(int)end.getY());
	}
	
     public void castAbility(Ability a) throws NotEnoughResourcesException, AbilityUseException, CloneNotSupportedException, IOException
     {
    	 
    	 Champion c = getCurrentChampion();
    	 
    	 if(c.getCurrentActionPoints()<a.getRequiredActionPoints())
    		 throw new NotEnoughResourcesException("Champion does not have enough action points to cast the ability");
    	 if(c.getMana()<a.getManaCost())
    		 throw new NotEnoughResourcesException("Champion does not have enough mana to cast the ability.");
    	 if(c.ChampionHasEffect("Silence")!=null)
    		 throw new AbilityUseException("Champion is silenced. Cannot cast ability.");
    	 
    	 if(a.getCurrentCooldown()!=0)
    		 throw new AbilityUseException("Ability is in cooldown condition!");
    	 ArrayList<Damageable> targets = new ArrayList<Damageable>();
    	 
    
    		 if(a instanceof DamagingAbility)
    		 {
    			 if((a.getCastArea()== AreaOfEffect.TEAMTARGET))
    				 
    			 {
    				 targets= getEnemyInRange(c,a.getCastRange());
    				 
    				 
    				  
    			 }
    			 
    			 else {
        			 ArrayList<Damageable> Surroundings = getSurrounding(c);
        			 for (Damageable dam : Surroundings) {
        				 if (dam instanceof Cover){
        					 
        					 targets.add(dam);
        				 }
        				 else 
        				 {
        					 if(isEnemy(c,(Champion) dam))
        						 targets.add(dam);
        						 
        				 }
    					
    	      }
				}
    		 }
    		 else if(a instanceof HealingAbility) 
    		 {
    			 if(a.getCastArea()==AreaOfEffect.TEAMTARGET) {
    				 targets=getFriendlyInRange(c, a.getCastRange());
    				 targets.add((Damageable) c);
    			 }
    			 
    			 else if(a.getCastArea()==AreaOfEffect.SELFTARGET) {
        			 targets.add((Damageable) c);
        			 
        		 }
        		 else {
        			 ArrayList<Damageable> Surroundings = getSurrounding(c);
        			 for (Damageable dam : Surroundings) {
        				 if(dam instanceof Champion)
        				 {
        					 if(isAlly(c,(Champion)dam)) {
        						 targets.add(dam);
        					 }
        				 }
        				 
    					
    				}
        		 }
    		 }
    		 
    		 else  { 		//CrowdControlAbility
    			 if(a.getCastArea()==AreaOfEffect.TEAMTARGET) {
    				 if(((CrowdControlAbility) a).getEffect().getType()==EffectType.BUFF){
    					 targets = getFriendlyInRange(c, a.getCastRange());
    					 targets.add((Damageable) c);
    				 }
    			 
    			 else 	//law el effect debuff
    				 targets= getEnemyInRange(c, a.getCastRange());
    		  }
    			 
    			 else if(a.getCastArea()==AreaOfEffect.SELFTARGET)
    			 {
    				 if(((CrowdControlAbility) a).getEffect().getType()==EffectType.BUFF)
    					 targets.add((Damageable) c);
    					 
    			 }
    			 else {   //Surrounding: Range not considered
    				 
    				 ArrayList<Damageable> Surroundings=getSurrounding(c);
    				 EffectType type = ((CrowdControlAbility) a).getEffect().getType();
    				 for (Damageable dam : Surroundings) {
    					if(type==EffectType.BUFF)
    					{
    						if(dam instanceof Champion && isAlly(c,(Champion) dam))
    							targets.add(dam);
    					}
    					else {
    						if(dam instanceof Champion && isEnemy(c,(Champion) dam))
    								targets.add(dam);
    						
    					}
						
					}
    		 		 
    			 }
    		 }
    		 a.execute(targets);
    		 
    		 
    		 c.setMana(c.getMana()-a.getManaCost());
    		 c.setCurrentActionPoints(c.getCurrentActionPoints()-a.getRequiredActionPoints());
    		 a.setCurrentCooldown(a.getBaseCooldown());
    		 
    		 listener.changeChampionArea(c);
    		 listener.changeAbilityArea(c);
    		 
    		 for(Damageable damageable : targets) {
    			 if(damageable instanceof Champion) {
    				 listener.changeChampionArea((Champion) damageable);
    				 listener.changeAbilityArea((Champion) damageable);
    			 }
    			 else
    				 listener.changeCoverHp((Cover) damageable);
    		 }
    		 
    		listener.generateSound("sound-effects/outstanding.wav");
			if(a instanceof DamagingAbility)
				listener.generateSound("sound-effects/damage-cast.wav");
    		 
    		 if(a instanceof DamagingAbility)
    			 eliminateDead(targets);
    		
    }
     
     public void castAbility(Ability a, int x, int y) throws NotEnoughResourcesException, AbilityUseException, InvalidTargetException, CloneNotSupportedException, IOException {
    	 Champion c = getCurrentChampion();
		 Point p = c.getLocation();
		 
	  	 if(a.getCurrentCooldown()!=0)
    		 throw new AbilityUseException("Ability is in a cooldown condition!");
	   	 if(c.ChampionHasEffect("Silence")!=null)
    		 throw new AbilityUseException("Champion is silenced. Cannot cast ability.");
	   	 if(c.getCurrentActionPoints()<a.getRequiredActionPoints())
    		 throw new NotEnoughResourcesException("Champion does not have enough action points to cast the ability");
    	 if(c.getMana()<a.getManaCost())
    		 throw new NotEnoughResourcesException("Champion does not have enough mana to cast the ability.");
    	 if(board[x][y]==null)
    		 throw new InvalidTargetException("Target cell is empty!");
		 
    	 if (manhattan(p,new Point(x,y))> a.getCastRange()) 
    		 throw new AbilityUseException("Cannot cast ability. Target is out of range.");
  
    	 
    	 
    	 Damageable t = (Damageable) board[x][y]; //attacked cell
    	 ArrayList<Damageable> targets = new ArrayList<Damageable>();
    	 
    	 if(a instanceof DamagingAbility) {
    		 if(  (t instanceof Champion && isAlly(c,(Champion)t)))
    			 throw new InvalidTargetException("Cannot cast a damaging ability on friendly champions.");
    		 else 
    			 targets.add(t);
    	 }
    	 
    	 else if(a instanceof HealingAbility) {
    		 if( (t instanceof Champion) && isAlly(c,(Champion)t)) {
    			 targets.add(t);
    		 }
    		 else 
    			 throw new InvalidTargetException("Cannot cast on a cover or an enemy.");
    	 }
    	 else {  //if it is CC
    		 if(  (((CrowdControlAbility) a).getEffect().getType()==EffectType.BUFF)  ) {
    			 if(t instanceof Champion && isAlly(c,(Champion)t))
    				 targets.add(t);
    			 else {
    				 throw new InvalidTargetException("Cannot cast positive effect/BUFF on an enemy or a cover.");
    				 
    			 }
    			 
    			 
    				 
    		 }
    		 else {
    			 if( t instanceof Champion &&(isEnemy(c,(Champion)t))  )
    				 targets.add(t);
    			 else
    				 throw new InvalidTargetException("Cannot cast negative effect/DEBUFF on an ally or a cover.");
    			 
    				 
    		 }
    			 
    		 
    	 }
    	 
    	 a.execute(targets);
		 
		 
		 c.setMana(c.getMana()-a.getManaCost());
		 c.setCurrentActionPoints(c.getCurrentActionPoints()-a.getRequiredActionPoints());
		 a.setCurrentCooldown(a.getBaseCooldown());
		 
   		 listener.changeChampionArea(c);
		 listener.changeAbilityArea(c);
		 
		 for(Damageable damageable : targets) {
			 if(damageable instanceof Champion) {
				 listener.changeChampionArea((Champion) damageable);
				 listener.changeAbilityArea((Champion) damageable);
			 }
			 else
				 listener.changeCoverHp((Cover) damageable);
		 }
		 
 		listener.generateSound("sound-effects/outstanding.wav");
		if(a instanceof DamagingAbility)
			listener.generateSound("sound-effects/damage-cast.wav");
		 
		 if(a instanceof DamagingAbility)
			 eliminateDead(targets);
    	
	
     }
     
     
     public void castAbility(Ability a, Direction d) throws NotEnoughResourcesException, AbilityUseException, CloneNotSupportedException, IOException {
    	 
    	 Champion c = getCurrentChampion();
    	 
    	 if(c.getCurrentActionPoints()<a.getRequiredActionPoints())
    		 throw new NotEnoughResourcesException("Champion does not have enough action points to cast the ability");
    	 if(c.getMana()<a.getManaCost())
    		 throw new NotEnoughResourcesException("Champion does not have enough mana to cast the ability.");
    	 if(c.ChampionHasEffect("Silence")!=null)
    		 throw new AbilityUseException("Champion is silenced. Cannot cast ability.");
    	 if(a.getCurrentCooldown()!=0)
    		 throw new AbilityUseException("Ability is in a coolsown condition!");
    	 ArrayList<Damageable> targets = new ArrayList<Damageable>();
    	 
    
    		 if(a instanceof DamagingAbility ) {
    				ArrayList<Damageable> allCovers = getCoversInRange(c, a.getCastRange(), d);
    				ArrayList<Damageable> allEnemies = EnemiesInRange(c, a.getCastRange(), d);

    				for(Damageable damaged : allCovers)
    					targets.add(damaged);
    				for(Damageable damaged : allEnemies)
    					targets.add(damaged);
              }
    		 
    		 else if( a instanceof HealingAbility) {
 				targets = friendliesInThatDirection(c, a.getCastRange(), d);
    		 }
    		 
    		 else {
    			 if(((CrowdControlAbility) a).getEffect().getType()==EffectType.BUFF) {
    				 targets=friendliesInThatDirection(c,a.getCastRange(),d);
    			 }
    			 else {	//Debuff
    				 targets=EnemiesInRange(c,a.getCastRange(),d);
    			 }
    				 
    	 }
    		 
    		 a.execute(targets);
    		 c.setMana(c.getMana()-a.getManaCost());
    		 c.setCurrentActionPoints(c.getCurrentActionPoints()-a.getRequiredActionPoints());
    		 a.setCurrentCooldown(a.getBaseCooldown());
    		 
       		 listener.changeChampionArea(c);
    		 listener.changeAbilityArea(c);
    		 
    		 for(Damageable damageable : targets) {
    			 if(damageable instanceof Champion) {
    				 listener.changeChampionArea((Champion) damageable);
    				 listener.changeAbilityArea((Champion) damageable);
    			 }
    			 else
    				 listener.changeCoverHp((Cover) damageable);
    		 }
    		 
     		listener.generateSound("sound-effects/outstanding.wav");
 			if(a instanceof DamagingAbility)
 				listener.generateSound("sound-effects/damage-cast.wav");
    		 
    		 if(a instanceof DamagingAbility)
    			 eliminateDead(targets);
    		 
    		
     }
    

	public ArrayList<Damageable> friendliesInThatDirection(Champion c, int range,Direction d) {
	  	 ArrayList<Damageable> allFriendly = new ArrayList<Damageable>();
    	 Point ChampLoc = c.getLocation();
    	 int x =(int) ChampLoc.getX();
    	 int y = (int) ChampLoc.getY();
    	 
    	 if(d==Direction.RIGHT) {
    		 for(int i=y; i<BOARDWIDTH;i++) {
    			 Object a = board[x][i];
    			 if(a != null) {
    				 if(a instanceof Champion) {
    					 
    					 if(isAlly((Champion) a,c) && manhattan(ChampLoc, ((Champion)a).getLocation()) <= range && a!=c) {
    						 allFriendly.add((Damageable) a);
    					 }
    				 }
    			 }
    		 }
    	 }
    	 else if(d==Direction.LEFT)
    	 {
    		 for(int i=y;i>=0;i--) {
    			 Object a = board[x][i];
    			 if(a instanceof Champion) {
					 if(isAlly((Champion) a,c) && manhattan(ChampLoc, ((Champion)a).getLocation()) <= range && a!=c) {
						 allFriendly.add((Damageable) a);
					 }
				 }
    		 }
    	 }
    	 else if(d==Direction.UP)
    	 {
    		 for(int i=x;i<BOARDHEIGHT;i++) {
    			 Object a = board[i][y];
    			 if(a instanceof Champion) {
					 if(isAlly((Champion) a,c) && manhattan(ChampLoc, ((Champion)a).getLocation()) <= range && a!=c) {
						 allFriendly.add((Damageable) a);
					 }
				 }
    		 }
    	 }
    	 else {		//Direction is down
    		 for(int i=x; i>=0;i--) {
    			 Object a = board[i][y];
    			 if(a!=null) {
    				 if(a instanceof Champion) {
    					 
    					 if(isAlly((Champion) a,c) && manhattan(ChampLoc, ((Champion)a).getLocation()) <= range && a!=c) {
    						 allFriendly.add((Damageable) a);
    					 }
    				 }
    			 }
    		 }
    	 }
		return  allFriendly;

	}

	public ArrayList<Damageable> EnemiesInRange(Champion c, int range,Direction d) {
    	 ArrayList<Damageable> allEnemies = new ArrayList<Damageable>();
    	 Point ChampLoc = c.getLocation();
    	 int x =(int) ChampLoc.getX();
    	 int y = (int) ChampLoc.getY();
    	 if(d==Direction.RIGHT)
    	 {
    		 for(int i=y; i<BOARDWIDTH;i++) {
    			 Object a = board[x][i];
    			 if(a != null) {
    				 if(a instanceof Champion) {
    					 
    					 if(isEnemy((Champion) a,c) && manhattan(ChampLoc, ((Champion)a).getLocation()) <= range && a!=c) {
    						 allEnemies.add((Damageable) a);
    					 }
    				 }
    			 }
    		 }
    	 }
    	 else if(d==Direction.LEFT)
    	 {
    		 for(int i=y;i>=0;i--) {
    			 Object a = board[x][i];
    			 if(a instanceof Champion) {
					 if(isEnemy((Champion) a,c) && manhattan(ChampLoc, ((Champion)a).getLocation()) <= range && a!=c) {
						 allEnemies.add((Damageable) a);
					 }
				 }
    		 }
    	 }
    	 else if(d==Direction.UP)
    	 {
    		 for(int i=x;i<BOARDHEIGHT;i++) {
    			 Object a = board[i][y];
    			 if(a instanceof Champion) {
					 if(isEnemy((Champion) a,c) && manhattan(ChampLoc, ((Champion)a).getLocation()) <= range && a!=c) {
						 allEnemies.add((Damageable) a);
					 }
				 }
    		 }
    	 }
    	 else {		//Direction is down
    		 for(int i=x; i>=0;i--) {
    			 Object a = board[i][y];
    			 if(a!=null) {
    				 if(a instanceof Champion) {
    					 if(isEnemy((Champion) a,c) && manhattan(ChampLoc, ((Champion)a).getLocation()) <= range && a!=c) {
    						 allEnemies.add((Damageable) a);
    					 }
    				 }
    			 }
    		 }
    	 }
		return  allEnemies;

    	 
    	 
	
	}
	
	public ArrayList<Damageable> getCoversInRange(Champion c, int range,Direction d){
    	 ArrayList<Damageable> allCovers = new ArrayList<Damageable>();
    	 Point ChampLoc = c.getLocation();
    	 int x =(int) ChampLoc.getX();
    	 int y = (int) ChampLoc.getY();
    	 if(d==Direction.RIGHT) {
    		 for(int i=y; i<BOARDWIDTH;i++) {
    			 Object a = board[x][i];
    			 if(a != null) {
    				 if( a instanceof Cover) {
    					 if( manhattan(ChampLoc, ((Cover)a).getLocation()) <= range ) {
    						 allCovers.add((Damageable) a);
    					 }
    				 }			 
    			 }
    		 } 
    	 }
    	 else if(d==Direction.LEFT) {
    		 for(int i=y;i>=0;i--) {
    			 Object a = board[x][i];
    			 if( a instanceof Cover) {
					 if( manhattan(ChampLoc, ((Cover)a).getLocation()) <= range ) {
						 allCovers.add((Damageable) a);
					 }
				 }
    		 }
    	 }
    	 else if(d==Direction.UP) {
    		 for(int i=x;i<BOARDHEIGHT;i++) {
    			 Object a = board[i][y];
    			 if( a instanceof Cover) {
					 if( manhattan(ChampLoc, ((Cover)a).getLocation()) <= range ) {
						 allCovers.add((Damageable) a);
					 }
				 }
    		 }
    	 }
    	 else {		//Direction is down
    		 for(int i=x; i>=0;i--) {
    			 Object a = board[i][y];
    			 if(a!=null) {
    				 if( a instanceof Cover) {
    					 if( manhattan(ChampLoc, ((Cover)a).getLocation()) <= range ) {
    						 allCovers.add((Damageable) a);
    					 }
    				 }
    			 }
    		 }
    	 }
		return allCovers;
   }
     
     public void eliminateDead(ArrayList<Damageable> targets) throws IOException {
    	 for (Damageable dam : targets) {
    		 eliminateTheDead(dam);
			
		}
     }
     
     
     private void eliminateTheDead(Damageable dead) throws IOException {
		if(dead.getCurrentHP()==0)	{
			if(dead instanceof Champion) {
				if(firstPlayer.getTeam().contains(dead))
					firstPlayer.getTeam().remove(dead);
				else secondPlayer.getTeam().remove(dead);
				
				
			PriorityQueue temp= new PriorityQueue(6);
			while(!turnOrder.isEmpty())
			{
				Champion ch = (Champion) turnOrder.remove();
				if(ch!=dead)
					temp.insert(ch);
				
			}
			while(!temp.isEmpty())	{
				turnOrder.insert(temp.remove());
				
			}
				
			listener.changeTurnArea();
			listener.changeAbilityArea((Champion) dead);
			listener.removeChampionPhoto((Champion)dead);
			listener.generateSound("sound-effects/ultimate-fatality.wav");
			}
			int x = (int) dead.getLocation().getX();
			int y = (int) dead.getLocation().getY();
			board[x][y]=null;
			if(dead instanceof Cover) {
				listener.removeCoverPhoto((Cover)dead);
			}
			}
	}

	private boolean isAlly(Champion c, Champion dam) {
     	if((firstPlayer.getTeam().contains(c)) && firstPlayer.getTeam().contains(dam))
     	{
     		return true;
     	}
    	if((secondPlayer.getTeam().contains(c)) && secondPlayer.getTeam().contains(dam))
    	{
    		return true;
        }
    	return false;
    	
     }
     
     
     	

	private boolean isEnemy(Champion c, Champion dam) {
    	if((firstPlayer.getTeam().contains(c)) && secondPlayer.getTeam().contains(dam))
    	{
    		return true;
    	}
    	if((secondPlayer.getTeam().contains(c)) && firstPlayer.getTeam().contains(dam))
    	{
    		return true;
    	}
    	return false;
		
	}

	public ArrayList<Damageable> getFriendlyInRange(Champion c, int range) {
    	 ArrayList<Champion> allFriendly = new ArrayList<Champion>();
    	 ArrayList<Damageable> list = new ArrayList<Damageable>();
    	 
    	 if(firstPlayer.getTeam().contains(c))
    		 allFriendly = firstPlayer.getTeam();
    	 else 
    		 allFriendly = secondPlayer.getTeam();
    	 for (Champion champ : allFriendly) {
    		 if(manhattan(champ.getLocation(),c.getLocation())<=range && c!=champ)
    			 list.add((Damageable) champ);
    	 }
    	 return list;
    	}
     
     public ArrayList<Damageable> getEnemyInRange(Champion c, int range)
     {
    	 ArrayList<Damageable> list = new ArrayList<Damageable>();
    	 ArrayList<Champion> allEnemy = new ArrayList<Champion>();
    	 
    	 if(firstPlayer.getTeam().contains(c))
    		 allEnemy = secondPlayer.getTeam();
    	 else 
    		 allEnemy = firstPlayer.getTeam();
    	 for (Champion champ : allEnemy) {
    		 if(manhattan(champ.getLocation(),c.getLocation())<=range)
    			 list.add((Damageable) champ);
			
		}
		return list;
    	
     }
     
     public ArrayList<Damageable> getSurrounding(Champion c)
     {
    	 Point p = c.getLocation();
    	 int x = (int) p.getX();
    	 int y = (int) p.getY();
    	 
    	 int Xstart;
    	 int Ystart;
    	 
    	 if(x==0)
    		 Xstart=0;
    	 else
    		 Xstart=x-1;
    	 
    	 if(y==0)
    		 Ystart=0;
    	 else
    		 Ystart=y-1;
    	 
    	 ArrayList<Damageable> list = new ArrayList<Damageable>();
    	 
    	 for(int i = Xstart; i<=x+1 && i<BOARDHEIGHT;i++)
    	 {
    		 for(int j = Ystart; j<=y+1 && j<BOARDWIDTH;j++)
    		 {
    			 Object o = board[i][j];
    			 if(o instanceof Damageable &&o!=c)   //it is damageable and not the champ himself
    			 {
    				 list.add((Damageable) o);
    			 }
    		 }
    	 } return list;
    	 
    	 
    	 
     }
     
     public boolean isExtraDamage(Champion a , Champion b) {
    	 
    	 return ( (a instanceof Hero && b instanceof Villain) ||
    			  (a instanceof Villain && b instanceof Hero) ||
    			  (a instanceof AntiHero && b instanceof Hero) ||
    			  (a instanceof AntiHero && b instanceof Villain)||
    			  (a instanceof Villain && b instanceof AntiHero)||
    			  (a instanceof Hero && b instanceof AntiHero) ); 
     }
     
     public void attack(Direction d) throws ChampionDisarmedException, NotEnoughResourcesException, IOException, InvalidTargetException {
    	 
    	 Champion c = getCurrentChampion();
 		
    	if(c.ChampionHasEffect("Disarm")!=null)
			throw new ChampionDisarmedException("Cannot apply a normal attack, Champion is disarmed!");
		
		if(c.getCurrentActionPoints()<2)
			throw new NotEnoughResourcesException("Champion does not have enough action points to perform the action!");
    	 
		c.setCurrentActionPoints(c.getCurrentActionPoints()-2);
		listener.changeChampionArea(c);
		
		
		ArrayList<Damageable> coversInDirection = getCoversInRange(c,c.getAttackRange(),d);

		ArrayList<Damageable> enemiesInDirection = EnemiesInRange(c,c.getAttackRange(),d);

		Damageable attacked = null;

		
		if(coversInDirection.isEmpty() && enemiesInDirection.isEmpty())
			return;

		if(coversInDirection.isEmpty() && !enemiesInDirection.isEmpty())
			attacked = (Damageable) enemiesInDirection.get(0);

		if(enemiesInDirection.isEmpty() && !coversInDirection.isEmpty())
			attacked = (Damageable) coversInDirection.get(0);

    	if(!enemiesInDirection.isEmpty() && !coversInDirection.isEmpty()) {
    		Cover cover = (Cover) getCoversInRange(c,c.getAttackRange(),d).get(0);
    		Champion enemy = (Champion) EnemiesInRange(c,c.getAttackRange(),d).get(0);
    		
    		if(manhattan(c.getLocation(), cover.getLocation()) < manhattan(c.getLocation(), enemy.getLocation()) )
    			attacked = (Damageable) cover;
    		else
    			attacked = (Damageable) enemy;
    	}

		

    	if(attacked instanceof Champion && ((Champion) attacked).ChampionHasEffect("Shield")!=null) {
    		Effect e = ((Champion) attacked).ChampionHasEffect("Shield");
    		((Champion) attacked).getAppliedEffects().remove(e);
    		e.remove((Champion) attacked);
    		return;
    	}

    	boolean isDodge = false;
    	
    	if(attacked instanceof Champion && ((Champion) attacked).ChampionHasEffect("Dodge")!=null) {
    		Random r = new Random();
    		isDodge = r.nextBoolean();
    	}

    	if(attacked instanceof Cover) {
			attacked.setCurrentHP(attacked.getCurrentHP()-c.getAttackDamage());
			listener.changeCoverHp((Cover)attacked);
			listener.generateSound("sound-effects/hit-cover2.wav");
		}

    	else  { 

    		if(isDodge == false) {
    	
    			if( isExtraDamage(c, (Champion) attacked)) {
    				attacked.setCurrentHP(attacked.getCurrentHP()-(int) (c.getAttackDamage()*1.5));
    			}
    			else
    				attacked.setCurrentHP(attacked.getCurrentHP()-c.getAttackDamage());
    			
    			listener.changeChampionArea((Champion)attacked);
    			listener.generateSound("sound-effects/attack.wav");
    		}
    	
    	}

    	eliminateTheDead(attacked);
    	
     }

    	 
}









