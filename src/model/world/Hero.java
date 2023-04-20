package model.world;

import java.util.ArrayList;
import java.util.Iterator;

import model.effects.Effect;
import model.effects.EffectType;
import model.effects.Embrace;

public class Hero extends Champion {

	public Hero(String name, int maxHP, int maxMana, int actions, int speed, int attackRange, int attackDamage) {
		super(name, maxHP, maxMana, actions, speed, attackRange, attackDamage);

	}

	@Override
	public void useLeaderAbility(ArrayList<Champion> targets) throws CloneNotSupportedException {
		Embrace em = new Embrace(2);
		Iterator<Effect> i; 

		for(Champion c : targets) {
			i = c.getAppliedEffects().iterator();
			while(i.hasNext()) {
				Effect e = i.next();
				if(e.getType()==EffectType.DEBUFF) {
					i.remove();
					e.remove(c);
				}
			}
			Embrace embrace = (Embrace) em.clone();
			c.getAppliedEffects().add(embrace);
			embrace.apply(c);

		}
		
	}
	

	
}
