package model.abilities;

import java.util.ArrayList;

import model.world.Damageable;

public  class HealingAbility extends Ability {
	private int healAmount;

	public HealingAbility(String name,int cost, int baseCoolDown, int castRadius, AreaOfEffect area,int required, int healingAmount) {
		super(name,cost, baseCoolDown, castRadius, area,required);
		this.healAmount = healingAmount;
	}

	public int getHealAmount() {
		return healAmount;
	}

	public void setHealAmount(int healAmount) {
		this.healAmount = healAmount;
	}

	public String toString() {
		return "   HEL Ability"+"\n"+super.toString()+"   Heal Amount: "+healAmount+"\n";	
	}
	
	@Override
	public void execute(ArrayList<Damageable> targets) {
		for(Damageable dm : targets)
		{
			dm.setCurrentHP(dm.getCurrentHP()+this.healAmount);
			
		}
		
	}

	

	

}
