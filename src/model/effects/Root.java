package model.effects;

import model.world.Champion;
import model.world.Condition;

public class Root extends Effect {

	public Root( int duration) {
		super("Root", duration, EffectType.DEBUFF);
		
	}

	@Override
	public void apply(Champion c) {
		if(c.getCondition()!=Condition.INACTIVE)
			c.setCondition(Condition.ROOTED);

	}

	@Override
	public void remove(Champion c) {
		if(c.ChampionHasEffect("Root")==null) {
			if(c.getCondition()!=Condition.INACTIVE) //inactive > active
				c.setCondition(Condition.ACTIVE);
		}
	}
	

}
