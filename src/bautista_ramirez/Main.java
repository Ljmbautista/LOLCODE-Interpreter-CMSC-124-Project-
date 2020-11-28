package bautista_ramirez;

import bautista_ramirez.MainStage;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application{
	public static void main(String[] args) {
		launch(args);
	}
	public void start(Stage stage){			
		MainStage Stage = new MainStage();
		Stage.setStage(stage);
	}	
}
