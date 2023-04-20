package model.effects;

import model.world.Champion;

public class Embrace extends Effect {
	

	public Embrace(int duration) {
		super("Embrace", duration, EffectType.BUFF);
	}

	@Override
	public void apply(Champion c) {
		//System.out.println("old current "+c.getCurrentHP()+" max "+c.getMaxHP() + " amount "+(int)(c.getMaxHP()*0.2));
		c.setCurrentHP( c.getCurrentHP()+(int)(c.getMaxHP()*0.2) );
		//System.out.println("new current "+c.getCurrentHP()+" max "+c.getMaxHP());

		if(c.getCurrentHP()==501)
			c.setCurrentHP(500);
		
		c.setMana((int) (c.getMana()*1.2));
		c.setSpeed((int) (c.getSpeed()*1.2));
		c.setAttackDamage((int) (c.getAttackDamage()*1.2));
		
	}

	@Override
	public void remove(Champion c) {
		c.setSpeed( (int)(c.getSpeed()/1.2) );
		c.setAttackDamage( (int)(c.getAttackDamage()/1.2) );
		
	}

}
