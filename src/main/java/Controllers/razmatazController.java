package Controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

import DataStructures.Ghost;
import DataStructures.Payload;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class razmatazController {
	
	final String BASE_URL = "https://phasmophobia.fandom.com/wiki/";
	
    @FXML
    private VBox evidencePane;
	
	@FXML
	private VBox resultsPane;

	@FXML
	private VBox lookoutFor;
	
    @FXML
    private ComboBox<String> selectedItem;
    
    @FXML
    private WebView web;
    
    LinkedList<CheckBox> evidences;
	LinkedList<Ghost> allGhosts;
	HashMap<String, String> items;

	public razmatazController() {
		allGhosts = new LinkedList<Ghost>();
		items = new HashMap<String, String>();
		evidences = new LinkedList<CheckBox>();
	}
	
	@FXML
	void initialize() {
		web.getEngine().loadContent("Pick a topic to view", "text/html");
	}
	
	@FXML
	void refreshItem(ActionEvent event) {
		web.getEngine().loadContent("", "text/html");
		if(items.containsKey(selectedItem.getValue())) {
			web.getEngine().loadContent(items.get(selectedItem.getValue()), "text/html");
		}
	}

	@FXML
	void reset(ActionEvent event) {
		// reset all the check boxes
		for(CheckBox x : evidences) {
			x.setSelected(false);
		}

		// reset the output pane
		if (resultsPane.getChildren().size() > 0) {
			resultsPane.getChildren().remove(0, resultsPane.getChildren().size());
		}

		resultsPane.getChildren().add(new Label("We need more evidence..."));

		if (lookoutFor.getChildren().size() > 0) {
			lookoutFor.getChildren().remove(0, lookoutFor.getChildren().size());
		}
	}

	@FXML
	void resetOutput(ActionEvent event) {
		// reset output
		if (resultsPane.getChildren().size() > 0) {
			resultsPane.getChildren().remove(0, resultsPane.getChildren().size());
		}

		if (lookoutFor.getChildren().size() > 0) {
			lookoutFor.getChildren().remove(0, lookoutFor.getChildren().size());
		}
		
		boolean oneSelected = true;
		
		for(CheckBox x : evidences) {
			oneSelected = x.isSelected();
			if(!oneSelected) {
				break;
			}
		}

		if (oneSelected) {
			// none are selected
			resultsPane.getChildren().add(new Label("We need more evidence..."));
		} else {
			// if any are selected
			LinkedList<String> selected = new LinkedList<String>();
			for(CheckBox x : evidences) {
				if(x.isSelected()) {
					selected.add(x.getText());
				}
			}

			Set<String> remainingEvidence = new HashSet<String>();
			LinkedList<Ghost> possibleGhosts = new LinkedList<Ghost>();

			// find out what ghosts it could be, and what remaining evidence we have left to
			// gather
			for (Ghost x : allGhosts) {
				if (x.isViableOutcome(selected)) {
					possibleGhosts.add(x);
					for (String y : x.remainingEvidence(selected)) {
						remainingEvidence.add(y);
					}
				}
			}

			resultsPane.getChildren().add(new Label("Possible Ghosts"));
			resultsPane.getChildren().add(new Label("=========="));

			// print out the list of ghosts
			for (Ghost x : possibleGhosts) {
				resultsPane.getChildren().add(new Label(x.getName()));
				lookoutFor.getChildren().add(new Label(x.getName()));
				
				lookoutFor.getChildren().addAll(formatLine( x.getStrength()));
				lookoutFor.getChildren().addAll(formatLine(x.getWeakness()));
				lookoutFor.getChildren().add(new Label());
			}

			resultsPane.getChildren().add(new Label(""));
			resultsPane.getChildren().add(new Label("Evidence to gather"));
			resultsPane.getChildren().add(new Label("============"));
			
			// print out the evidence, and the types of ghosts it could be related to that
			// evidence
			for (String x : remainingEvidence) {
				resultsPane.getChildren().add(new Label(x));
				LinkedList<String> testClues = new LinkedList<String>();
				for(String y : selected) {
					testClues.add(y);
				}
				
				testClues.add(x);
				
				// get the ghosts that could have this as their stuff
				for (Ghost y : possibleGhosts) {
					if (y.isViableOutcome(testClues)) {
						resultsPane.getChildren().add(new Label("\t" + y.getName()));
					}
				}
				
			}
			
		}
	}
	
	private LinkedList<Label> formatLine(String line) {
		LinkedList<Label> labels = new LinkedList<Label>();
		int spot = 0;
		for(int i = 0; i < line.length(); i++) {
			if((i + 1) % 40 == 0) {
				if((i != 0) && (line.charAt(i - 1) == ' ') || line.charAt(i) == ' ') {
					labels.add(new Label(line.substring(spot, i).trim()));
					spot = i;
				}else {
					labels.add(new Label(line.substring(spot, i).trim() + "-"));
					spot = i;
				}
			}
		}
		labels.add(new Label(line.substring(spot, line.length())));
		return labels;
	}
	

	
	public void loadData(Payload payload) {
		
		for(String x : payload.getEvidences()) {
			CheckBox current = new CheckBox(x);
			current.setFocusTraversable(false);
			
			current.setOnAction(e -> resetOutput(e));
			
			evidencePane.getChildren().add(current);
			evidences.add(current);
		}
		
		
		PriorityQueue<String> comboBoxChoices = new PriorityQueue<String>();
		
		for(Ghost g : payload.getGhostData()) {
			comboBoxChoices.add(g.getName());
			items.put(g.getName(), g.getDescription());
			allGhosts.add(g);
		}
		
		for(String x : payload.getItemEntries().keySet()) {
			items.put(x, payload.getItemEntries().get(x));
			comboBoxChoices.add(x);
		}
		
		ObservableList<String> sortedBoxChoices = FXCollections.observableArrayList();
		
		while(comboBoxChoices.size() > 0) {
			sortedBoxChoices.add(comboBoxChoices.poll());
		}
		
		selectedItem.setItems(sortedBoxChoices);
	}

}
