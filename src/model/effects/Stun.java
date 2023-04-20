package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Stun extends Effect {

	public Stun(int duration) {
		super("Stun", duration, EffectType.DEBUFF);
	}

	@Override
	public void apply(Champion c) {
		c.setCondition(Condition.INACTIVE);
		
	}

	@Override
	public void remove(Champion c) {
		if(c.ChampionHasEffect("Stun") == null) {
			if(c.getCondition()!=Condition.ROOTED)
				c.setCondition(Condition.ACTIVE);
		}
		
	}


}
