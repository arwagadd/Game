package model.abilities;

import java.util.ArrayList;

import model.effects.Effect;
import model.world.Champion;
import model.world.Cover;
import model.world.Damageable;

public class DamagingAbility extends Ability {
	
	private int damageAmount;
	public DamagingAbility(String name, int cost, int baseCoolDown, int castRadius, AreaOfEffect area,int required,int damageAmount) {
		super(name, cost, baseCoolDown, castRadius, area,required);
		this.damageAmount=damageAmount;
	}
	public int getDamageAmount() {
		return damageAmount;
	}
	public void setDamageAmount(int damageAmount) {
		this.damageAmount = damageAmount;
	}
	
	public String toString() {
		return "   DMG Ability"+"\n"+super.toString()+"   Damage Amount: "+damageAmount+"\n";	
	}
	
	@Override
	public void execute(ArrayList<Damageable> targets) {
		for(Damageable dm : targets) {
		
			if(dm instanceof Cover) {
				dm.setCurrentHP(dm.getCurrentHP()-this.getDamageAmount());
			}
			else {
				Effect ef = ((Champion)dm).ChampionHasEffect("Shield");
				if(ef==null) { 
					dm.setCurrentHP(dm.getCurrentHP()-this.getDamageAmount());
				}
				else { 
					((Champion)dm).getAppliedEffects().remove(ef);
					ef.remove((Champion) dm);		
				}
			}
		}
		
	}
	

}
