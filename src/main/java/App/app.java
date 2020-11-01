package App;

import java.io.IOException;

import Controllers.dataFetchingController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class app extends Application{
	
	@Override
	public void start(Stage arg0) throws IOException, InterruptedException {
			
		FXMLLoader dataFetchingLoader = new FXMLLoader();
		AnchorPane dataFetchingPane = dataFetchingLoader.load(getClass().getResource("/assets/resources/fetching.fxml").openStream());
		dataFetchingController dfc = dataFetchingLoader.getController();
		
		dfc.setApp(arg0);
		
		Scene dataFetchingScene = new Scene(dataFetchingPane);
		
		arg0.setScene(dataFetchingScene);
		
		arg0.show();
		arg0.setResizable(false);
		arg0.setTitle("Razmataz ghost finder-outer");
		
		dfc.fetchData();
	}
	

	
	public static void main(String[] args) {
		launch(args);
	}

}
