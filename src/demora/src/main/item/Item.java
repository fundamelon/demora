package main.item;
import main.entity.Entity;

public abstract class Item implements Entity{
	
	//unique items get an ID
	//otherwise ID is zero for generics
	public final int ID;
	
	public Item(int nID) {
		ID = nID;
	}
	
	public int getID() {return ID;}
	
	public abstract void destroy();

}
