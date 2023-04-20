package model.world;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import model.abilities.Ability;
import model.effects.Effect;

public abstract class Champion implements Comparable,Damageable{
	private String name;
	private int maxHP;
	private int currentHP;
	private int mana;
	private int maxActionPointsPerTurn;
	private int currentActionPoints;
	private int attackRange;
	private int attackDamage;
	private int speed;
	private ArrayList<Ability> abilities;
	private ArrayList<Effect> appliedEffects;
	private Condition condition;
	private Point location;
	

	public Champion(String name, int maxHP, int mana, int actions, int speed, int attackRange, int attackDamage) {
		this.name = name;
		this.maxHP = maxHP;
		this.mana = mana;
		this.currentHP = this.maxHP;
		this.maxActionPointsPerTurn = actions;
		this.speed = speed;
		this.attackRange = attackRange;
		this.attackDamage = attackDamage;
		this.condition = Condition.ACTIVE;
		this.abilities = new ArrayList<Ability>();
		this.appliedEffects = new ArrayList<Effect>();
		this.currentActionPoints=maxActionPointsPerTurn;
	}
	
	public int compareTo(Object o)
	{
		if( this.speed < ((Champion)(o)).getSpeed() )
			return 1;
		else if (this.speed > ((Champion)(o)).getSpeed() )
				return -1;
		return this.name.compareTo(((Champion)(o)).getName());
		
	}
	public abstract void useLeaderAbility(ArrayList<Champion> targets) throws CloneNotSupportedException;

	public int getMaxHP() {
		return maxHP;
	}

	public String getName() {
		return name;
	}

	public void setCurrentHP(int hp) {

		if (hp <= 0) {
			
			currentHP = 0;
			this.condition=condition.KNOCKEDOUT;
			
		} 
		else if (hp > maxHP)
			currentHP = maxHP;
		else
			currentHP = hp;

	}

	
	public int getCurrentHP() {

		return currentHP;
	}

	public ArrayList<Effect> getAppliedEffects() {
		return appliedEffects;
	}

	public int getMana() {
		return mana;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public int getAttackDamage() {
		return attackDamage;
	}

	public void setAttackDamage(int attackDamage) {
		this.attackDamage = attackDamage;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int currentSpeed) {
		if (currentSpeed < 0)
			this.speed = 0;
		else
			this.speed = currentSpeed;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point currentLocation) {
		this.location = currentLocation;
	}

	public int getAttackRange() {
		return attackRange;
	}

	public ArrayList<Ability> getAbilities() {
		return abilities;
	}

	public int getCurrentActionPoints() {
		return currentActionPoints;
	}

	public void setCurrentActionPoints(int currentActionPoints) {
		if(currentActionPoints>maxActionPointsPerTurn)
			currentActionPoints=maxActionPointsPerTurn;
		else 
			if(currentActionPoints<0)
			currentActionPoints=0;
		this.currentActionPoints = currentActionPoints;
	}

	public int getMaxActionPointsPerTurn() {
		return maxActionPointsPerTurn;
	}

	public void setMaxActionPointsPerTurn(int maxActionPointsPerTurn) {
		this.maxActionPointsPerTurn = maxActionPointsPerTurn;
	}
	
	public Effect ChampionHasEffect(String name)
	{
		for(Effect ef : this.appliedEffects)
		{
			if(ef.getName().equals(name))
			{
				return ef;
			}
		}
		return null;
	}


	


	public String toString() {
		String string = "";
		
		string+="   Name :"+name+"\n";
		string+="   Type :"+this.getClass().getName().substring(12)+"\n";
		string+="   Max HP :"+maxHP+"\n";
		string+="   Current HP :"+currentHP+"\n";
		string+="   Mana :"+mana+"\n";
		string+="   Max Points :"+maxActionPointsPerTurn+"\n";
		string+="   Current Points :"+currentActionPoints+"\n";
		string+="   Attack Range :"+attackRange+"\n";
		string+="   Attack Damage :"+attackDamage+"\n";
		string+="   Speed :"+speed+"\n";
		
		return string;
	}
	
	 
	
	public String displayChampionData() {
		String s = "";
		s+= this.toString()+"   Condition: "+condition+"\n"+"   Applied Effects: ";
		for(Effect e : appliedEffects)
			s+="\n"+e.toString();
		return s;
	}
	
	public String displayAbilitiesData() {
		String s = "";
		for(Ability a : abilities)
			s+=a.toString()+"   ==============="+"\n";
		return s;
	}

}
