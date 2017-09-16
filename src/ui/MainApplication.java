package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class MainApplication extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("BaseWindow.fxml"));
		Parent root = loader.load();
		MenuItem saveMI = (MenuItem)loader.getNamespace().get("saveMenuItem");
		MenuItem showMI = (MenuItem)loader.getNamespace().get("showMenuItem");
		MenuItem queryMI = (MenuItem)loader.getNamespace().get("queryMenuItem");
		MenuItem generateMI = (MenuItem)loader.getNamespace().get("generateMenuItem");
		MenuItem pathMI = (MenuItem)loader.getNamespace().get("pathMenuItem");
		MenuItem walkMI = (MenuItem)loader.getNamespace().get("walkMenuItem");
		saveMI.setDisable(true);
		showMI.setDisable(true);
		queryMI.setDisable(true);
		generateMI.setDisable(true);
		pathMI.setDisable(true);
		walkMI.setDisable(true);
		Scene scene = new Scene(root);
		stage.setTitle("软件工程实验一");
		stage.setScene(scene);
		stage.setResizable(false);
		stage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
