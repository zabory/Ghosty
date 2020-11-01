package Controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;

import DataStructures.Ghost;
import DataStructures.Payload;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class dataFetchingController {
	
	final String BASE_URL = "https://phasmophobia.fandom.com/wiki/";

	@FXML
	private ProgressBar taskProgress;

	@FXML
	private ProgressBar overallProgress;

	@FXML
	private Label currentTaskLabel;

	Payload pl;

	Stage arg0;

	public void setApp(Stage arg0) {
		this.arg0 = arg0;
	}

	public void fetchData() {
		new Thread(() -> {

			Payload payload = new Payload();

			LinkedList<String> HTMLLines = new LinkedList<String>();

			URL oracle;
			try {
				oracle = new URL(BASE_URL + "Ghosts");

				updateTask("Retrieving ghost names");

				BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null)
					HTMLLines.add(inputLine);
				in.close();
				boolean ghostLooking = false;
				// This loop is for getting the ghost names
				for (String x : HTMLLines) {

					if (x.contains(
							"<li class=\"toclevel-1 tocsection-10\"><a href=\"#Types_of_Ghosts\"><span class=\"tocnumber\">2</span> <span class=\"toctext\">Types of Ghosts</span></a>")) {
						ghostLooking = true;
					} else if (x.contains(
							"<li class=\"toclevel-1 tocsection-23\"><a href=\"#Identification\"><span class=\"tocnumber\">3</span> <span class=\"toctext\">Identification</span></a></li>")) {
						ghostLooking = false;
					}

					if (ghostLooking && x.contains("<li class=\"toclevel-2 ")) {
						String y = x;
						y = y.substring(y.indexOf("<span class=\"toctext\">")).replace("<span class=\"toctext\">", "")
								.replace("</span></a></li>", "");
						payload.addGhostName(y);
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//lets get the ghost info
			updateTask("Retrieving ghost info");
			//use this for progress stuff
			int total = payload.getGhosts().size();
			int currentCount = 0;
			//goto each page and scrub for data
			for(String ghost : payload.getGhosts()) {
				updateTask("Scrubbing data for ghost: " + ghost);
				
				payload.addGhostData(createGhost(ghost));
				
				currentCount++;
				updateTaskBar(total, currentCount);
			}
			
			updateTotalTaskBar(2, 1);
			//<Item name, Item details
			HashMap<String, String> allItems = new HashMap<String, String>();
			
			//get the item names
			HTMLLines = new LinkedList<String>();
			LinkedList<String> itemNames = new LinkedList<String>();
			try {
				
				
				oracle = new URL(BASE_URL + "Category:Equipment");
				updateTask("Retrieving equipment names");

				BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					HTMLLines.add(inputLine);
				}
				in.close();
				
				for(String x : HTMLLines) {
					if(x.contains(" class=\"category-page__member-link\" title=\"") && !x.contains("Category:") && !x.contains("Equipment")) {
						itemNames.add(x.substring(x.indexOf(" class=\"category-page__member-link\" title=\"") + " class=\"category-page__member-link\" title=\"".length(), x.indexOf("\">")));
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//get the item names
			HTMLLines = new LinkedList<String>();
			try {
				
				
				oracle = new URL(BASE_URL + "Category:Van_Equipment");
				updateTask("Retrieving van equipment names");

				BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					HTMLLines.add(inputLine);
				}
				in.close();
				
				for(String x : HTMLLines) {
					if(x.contains(" class=\"category-page__member-link\" title=\"") && !x.contains("Category:") && !x.contains("Equipment")) {
						itemNames.add(x.substring(x.indexOf(" class=\"category-page__member-link\" title=\"") + " class=\"category-page__member-link\" title=\"".length(), x.indexOf("\">")));
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//get the item names
			HTMLLines = new LinkedList<String>();
			try {
				
				
				oracle = new URL(BASE_URL + "Category:Item");
				updateTask("Retrieving item names");

				BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					HTMLLines.add(inputLine);
				}
				in.close();
				
				for(String x : HTMLLines) {
					if(x.contains(" class=\"category-page__member-link\" title=\"") && !x.contains("Category:") && !x.contains("Equipment")) {
						itemNames.add(x.substring(x.indexOf(" class=\"category-page__member-link\" title=\"") + " class=\"category-page__member-link\" title=\"".length(), x.indexOf("\">")));
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			itemNames.add("Hunt");
			
			int totalEquip = itemNames.size();
			int currentEquip = 0;
			//get the item descriptions
			for(String x : itemNames) {
				updateTask("Scrubbing data for item: " + x);
				allItems.put(x, getItemDetails(x));
				updateTaskBar(totalEquip, currentEquip++);
			}
			
			payload.setItemEntries(allItems);
			pl = payload;
			
			updateTotalTaskBar(2, 2);
			
			switchScene();

		}).start();
	}
	
	private String getItemDetails(String itemName) {
		String details = "";
		
		//read in the page
		URL oracle;
		try {
			oracle = new URL(BASE_URL + itemName.replace(" ", "_"));
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

			LinkedList<String> HTMLLines = new LinkedList<String>();
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				HTMLLines.add(inputLine);
			}
			in.close();
			
			//lets go through this data
			//</p><p>
			//<table style="width:100%; margin-top:1em; border:1px solid #999; font-size:90%; text-align:center;">
			//0, no more looking
			//1, looking for start
			//2, currently reading and looking for end
			int lookingForData = 1;
			
			for(String x : HTMLLines) {
				if(x.contains("</p><p>") && lookingForData == 1) {
					lookingForData = 2;
				}else if(lookingForData == 2 && (x.contains("<table style=\"width:100%; margin-top:1em; border:1px solid #999; font-size:90%; text-align:center;\">") || x.contains("Comparison") || x.contains("Objectives</a> requiring") || x.contains("title=\"Daily Challenges\"") || (x.contains("<span class=\"mw-headline\" id=\"Photo_Objective\">") || (x.contains("<h2><span class=\"mw-headline\" id=\"Possible_Ghosts\">") || x.contains("<!-- "))))) {
					lookingForData = 0;
				}
				
				if(lookingForData == 2) {
					details += x + "\n";
				}
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		return formatLine(details).replace("â€”", "-").replace("Â", "");
	}
	
	/**
	 * Scrub the wiki for the ghost and create a ghost object from it
	 * @param name
	 * @return
	 */
	private Ghost createGhost(String name) {
		
		String strength = "";
		String weakness = "";
		String description = "";
		LinkedList<String> evidence = new LinkedList<String>();
		
		
		
		try {
			//read in the page
			URL oracle = new URL(BASE_URL + name);
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));

			LinkedList<String> HTMLLines = new LinkedList<String>();
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				HTMLLines.add(inputLine);
			}
			in.close();
			
			boolean isReadingESW = false;
			int isReadingDetails = 1;
			
			LinkedList<String> finalDetails = new LinkedList<String>();
			//read through the lines for evidence, strengths, and weaknesses, and details
			for(String line : HTMLLines) {
				if(line.contains("<h2><span class=\"mw-headline\" id=\"Evidence\">")) {
					isReadingESW = true;
				}else if(line.equals("</td></tr></tbody></table>")) {
					isReadingESW = false;
				}
				
				
				
				if((isReadingDetails == 1) && (line.contains("<blockquote>") || line.contains("<div class=\"mw-parser-output\"><p>") || line.contains("<p>"))) {
					isReadingDetails = 2;
				}else if(line.contains("<th style=\"padding:0.2em 0.5em;\" nowrap=\"nowrap\" class=\"color1\">") || line.contains("<h2><span class=\"mw-headline\" id=\"References\">") || line.contains("<table style=\"width:100%; margin-top:1em; border:1px solid #999; font-size:90%; text-align:center;\">")) {
					//(!line.contains("<p>") && !line.contains("</p>")) && (!line.contains("<td>") && !line.contains("</td>")) && (!line.contains("<tr>") && !line.contains("</tr>"))
					isReadingDetails = 0;
				}
				
				//get the evidence and strengths and weaknesses
				if(isReadingESW && (line.contains("<td><a href=\"/wiki/") || line.contains("<th><a href=\"/wiki/"))) {
					evidence.add(line.substring(line.replace("</a>", "").lastIndexOf(">") + 1).replace("</a>", ""));
				}else if(line.contains("Strengths:")) {
					strength = formatTextLine(line);
				}else if(line.contains("Weaknesses:")) {
					weakness = formatTextLine(line);
				}
				
				
				
				if(isReadingDetails == 2) {
					finalDetails.add(formatLine(line));
				}
			}
			
			//format description. All lines in final details
			for(String x : finalDetails) {
				description += x + "\n";
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		return new Ghost(evidence, name, strength, weakness, description);
	}

	/**
	 * Updates the task bar
	 * @param total
	 * @param current
	 */
	private void updateTaskBar(int total, int current) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				taskProgress.setProgress((double) current / (double) total);

			}
		});

	}
	
	/**
	 * Updates the total task bar
	 * @param total
	 * @param current
	 */
	private void updateTotalTaskBar(int total, int current) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {

				overallProgress.setProgress((double) current / (double) total);

			}
		});

	}

	@FXML
	void initialize() {
		
	}
	/**
	 * Update the task label
	 * @param task
	 */
	private void updateTask(String task) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				
				currentTaskLabel.setText(task);
				
			}
		});
	}
	
	/**
	 * Remove anything from the line in-between <>
	 * @param line
	 * @return
	 */
	private String formatLine(String line) {
		String formatedLine = "";
		
		for(int i = 0; i < line.length(); i++) {
			char x = line.charAt(i);
			if(x == '<' && ((line.charAt(i + 1) == 'a' || (line.charAt(i + 1) == '/' && line.charAt(i + 1) == 'a')))) {
				if(line.indexOf('>', i) != -1) {
					i = line.indexOf('>', i);
					continue;
				}else {
					break;
				}
			}
			
			formatedLine += x;
			
		}
		return formatedLine.replace("<span class=\"mw-editsection\"><span class=\"mw-editsection-bracket\">[</span>edit</a><span class=\"mw-editsection-divider\"> | </span><svg class=\"wds-icon wds-icon-tiny section-edit-pencil-icon\"><use xlink:href=\"#wds-icons-pencil-tiny\"></use></svg>edit source</a><span class=\"mw-editsection-bracket\">]</span>", "");
	}
	
	private String formatTextLine(String line) {String formatedLine = "";
	for(int i = 0; i < line.length(); i++) {
		char x = line.charAt(i);
		if(x == '<') {
			i = line.indexOf('>', i);
			continue;
		}
		
		formatedLine += x;
		
	}
	return formatedLine.replace("<span class=\"mw-editsection\"><span class=\"mw-editsection-bracket\">[</span>edit</a><span class=\"mw-editsection-divider\"> | </span><svg class=\"wds-icon wds-icon-tiny section-edit-pencil-icon\"><use xlink:href=\"#wds-icons-pencil-tiny\"></use></svg>edit source</a><span class=\"mw-editsection-bracket\">]</span>", "");
		
	}
	
	/**
	 * Switch to the main program once we are done
	 */
	private void switchScene() {
		
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				FXMLLoader mainLoader = new FXMLLoader();
				AnchorPane mainPane;
				try {
					mainPane = mainLoader.load(getClass().getResource("/assets/resources/controller.fxml").openStream());
					razmatazController mc = mainLoader.getController();
					Scene mainScene = new Scene(mainPane);

					mc.loadData(pl);
					arg0.hide();

					Stage newStage = new Stage();
					newStage.setScene(mainScene);
					newStage.show();
					newStage.setTitle("Ghosty");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

	}

}
