package DataStructures;

import java.util.LinkedList;

public class Ghost {

	String name;
	String strength;
	String weakness;
	String description;
	LinkedList<String> evidence;
	
	public Ghost(LinkedList<String> evidence, String name, String strength, String weakness, String description) {
		this(evidence, name, strength, weakness);
		this.description = description;
	}
	
	public Ghost(LinkedList<String> evidence, String name, String strength, String weakness) {
		this(evidence, name);
		this.strength = strength;
		this.weakness = weakness;
	}
	
	public Ghost(LinkedList<String> evidence, String name) {
		this(evidence);
		this.name = name;
	}
	
	public Ghost(LinkedList<String> evidence) {
		this.evidence = evidence;
	}
	
	public boolean isViableOutcome(LinkedList<String> currentEvidence) {
		for(String x : currentEvidence) {
			if(!evidence.contains(x)) {
				return false;
			}
		}
		return true;
	}
	
	public LinkedList<String> remainingEvidence(LinkedList<String> currentEvidence) {
		LinkedList<String> re = new LinkedList<String>();
		for(String x : evidence) {
			re.add(x);
		}
		
		for(String x : currentEvidence) {
			re.remove(x);
		}
		
		return re;
	}
	
	public LinkedList<String> getEvidences(){
		return evidence;
	}
	
	public String getName() {
		return name;
	}

	public String getStrength() {
		return strength;
	}

	public String getWeakness() {
		return weakness;
	}
	
	public String getDescription() {
		return description;
	}
	
}
