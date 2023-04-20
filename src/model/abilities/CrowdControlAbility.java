package model.abilities;

import java.util.ArrayList;

import model.effects.Effect;
import model.world.Champion;
import model.world.Damageable;

public class CrowdControlAbility extends Ability {
	private Effect effect;

	public CrowdControlAbility(String name, int cost, int baseCoolDown, int castRadius, AreaOfEffect area, int required,
			Effect effect) {
		super(name, cost, baseCoolDown, castRadius, area, required);
		this.effect = effect;

	}

	public Effect getEffect() {
		return effect;
	}

	public String toString() {
		return "   CC Ability"+"\n"+super.toString()+effect.toString();
	}
	
	@Override
	public void execute(ArrayList<Damageable> targets) throws CloneNotSupportedException {
		Effect effect = this.getEffect();
		for(Damageable dm : targets) {
			Effect ef=(Effect) effect.clone();
			ef.apply((Champion)dm);
			((Champion)(dm)).getAppliedEffects().add(ef);
		}
	}

}
