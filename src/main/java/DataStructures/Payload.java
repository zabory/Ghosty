package DataStructures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Payload {
	//list of the ghost names
	LinkedList<String> ghosts;
	//list of any entry and its data to be put in the info section
	HashMap<String, String> itemEntries;
	//list of the ghost and its data, specifically the name, and what its clues are
	LinkedList<Ghost> ghostData;
	//list of all the possible evidences
	HashSet<String> evidences;
	
	public Payload() {
		ghosts = new LinkedList<String>();
		itemEntries = new HashMap<String, String>();
		ghostData = new LinkedList<Ghost>();
		evidences = new HashSet<String>();
	}
	
	public LinkedList<String> getGhosts() {
		return ghosts;
	}
	public void setGhosts(LinkedList<String> ghosts) {
		this.ghosts = ghosts;
	}
	public HashMap<String, String> getItemEntries() {
		return itemEntries;
	}
	public void setItemEntries(HashMap<String, String> itemEntries) {
		this.itemEntries = itemEntries;
	}
	public LinkedList<Ghost> getGhostData() {
		return ghostData;
	}
	public void setGhostData(LinkedList<Ghost> ghostData) {
		this.ghostData = ghostData;
	}
	
	public void addGhostName(String name) {
		ghosts.add(name);
	}
	
	public void addGhostData(Ghost ghost) {
		ghostData.add(ghost);
		for(String x : ghost.getEvidences()) {
			evidences.add(x);
		}
	}
	
	public void addItemData(String name, String data) {
		itemEntries.put(name, data);
	}
	
	public void addEvidence(String evidence) {
		evidences.add(evidence);
	}
	
	public HashSet<String> getEvidences(){
		return evidences;
	}
	
	public String toString() {
		return "Ghosts:" + ghosts.size() + "\nItems:" + itemEntries.size();
	}
	

}
