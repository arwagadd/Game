package views;

import java.io.IOException;

import model.world.Champion;
import model.world.Cover;

public interface ViewListener {
	
	public void placeChampionPhoto(Champion champion) throws IOException ;
	public void removeChampionPhoto(Champion champion);
	public void greenBackground (Champion champion) ;
	public void removeGreenBackground (Champion champion) ;
	public void generateSound(String filePath);
	public void changeTurnArea() ;
	public void changeChampionArea(Champion c);
	public void changeAbilityArea(Champion c);
	public void removeCoverPhoto(Cover cover) throws IOException ;
	public void changeLeaderAbilityIcon(Champion c) throws IOException ;
	public void changeCoverHp(Cover cover) ;
	public void changeAbilityBoxes();
	public void changePlayerTurnArea();


}
