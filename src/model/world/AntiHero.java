package model.world;

import java.util.ArrayList;

import model.effects.Stun;

public class AntiHero extends Champion {

	public AntiHero(String name, int maxHP, int maxMana, int actions, int speed, int attackRange, int attackDamage) {
		super(name, maxHP, maxMana, actions, speed, attackRange, attackDamage);

	}

	@Override
	public void useLeaderAbility(ArrayList<Champion> targets) throws CloneNotSupportedException {
		Stun s = new Stun(2);
		for(Champion c :targets)
		{
			Stun st = (Stun) s.clone();
			st.apply(c);
			c.getAppliedEffects().add(st);
		}
		
	}
}
