package bautista_ramirez;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import bautista_ramirez.MainStage;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainStage {
	private Scene scene;
	private Stage stage;
	private Group root;
	private Canvas canvas;
	private TableView table;
	private Button file_btn;
	private FileChooser fileChooser;
	
	private ArrayList<String> lexemes;					//arraylist of lexeme
	private ArrayList<String> classification;			//arraylist of classification
	
	public final static int WINDOW_WIDTH = 1080;
	public final static int WINDOW_HEIGHT = 720;
	
	public MainStage() {								//constructor for MainStage
		this.root = new Group();
		this.scene = new Scene(root, MainStage.WINDOW_WIDTH,MainStage.WINDOW_HEIGHT);	
		this.canvas = new Canvas(MainStage.WINDOW_WIDTH,MainStage.WINDOW_HEIGHT);
		this.lexemes = new ArrayList<String>();	
		this.classification = new ArrayList<String>();	
		this.file_btn = new Button();
		this.fileChooser = new FileChooser();
		
	}
	
	public void setStage(Stage stage) {							//function for adding elements to stage
		this.stage = stage;
		this.addMouseEventHandler();
		
		this.file_btn.setText("  Add file...  ");
        this.file_btn.setFont(new Font(12));
		this.file_btn.setLayoutX(MainStage.WINDOW_WIDTH*0.5);
		this.file_btn.setLayoutY(MainStage.WINDOW_WIDTH*0.1);

		this.root.getChildren().add(canvas);
		this.root.getChildren().add(file_btn);
		this.stage.setTitle("LOLCODE INTERPRETER");
		this.stage.setScene(this.scene);
		this.stage.show();
	}
	
	public String lexemeChecker(String s) {
		boolean containsComma = false;
		
		if(s.matches("^([a-zA-Z]+,)$")) {						//check if there is comma
			containsComma = true;
			s = s.substring(0,(s.length()-1));
		}
		
		//pattern checker
		if(s.matches("^(HAI)$")) {
			lexemes.add(s);
			classification.add("Code Delimiter");
		}
		else if(s.matches("^(KTHXBYE)$")) {
			lexemes.add(s);
			classification.add("Code Delimiter");
		}
		else if(s.matches("^(I HAS A)$")) {
			lexemes.add(s);
			classification.add("Variable Declaration");
			s = "";
		}
		else if(s.matches("^(ITZ)$")) {
			lexemes.add(s);
			classification.add("Variable Assignment");
			s = "";
		}
		else if(s.matches("^(R)$")) {
			lexemes.add(s);
			classification.add("Assignment Keyword");
			s = "";
		}
		else if(s.matches("^(SUM OF)$")) {
			lexemes.add(s);
			classification.add("Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(DIFF OF)$")) {
			lexemes.add(s);
			classification.add("Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(PRODUKT OF)$")) {
			lexemes.add(s);
			classification.add("Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(QUOSHUNT OF)$")) {
			lexemes.add(s);
			classification.add("Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(MOD OF)$")) {
			lexemes.add(s);
			classification.add("Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(BIGGR OF)$")) {
			lexemes.add(s);
			classification.add("Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(SMALLR OF)$")) {
			lexemes.add(s);
			classification.add("Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(BOTH OF)$")) {
			lexemes.add(s);
			classification.add("Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(EITHER OF)$")) {
			lexemes.add(s);
			classification.add("Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(WON OF)$")) {
			lexemes.add(s);
			classification.add("Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(NOT)$")) {
			lexemes.add(s);
			classification.add("Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(ANY OF)$")) {
			lexemes.add(s);
			classification.add("Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(ALL OF)$")) {
			lexemes.add(s);
			classification.add("Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(BOTH SAEM)$")) {
			lexemes.add(s);
			classification.add("Comparison Operation Keyword");
			s = "";
		}
		else if(s.matches("^(DIFFRINT)$")) {
			lexemes.add(s);
			classification.add("Comparison Operation Keyword");
			s = "";
		}
		else if(s.matches("^(SMOOSH)$")) {
			lexemes.add(s);
			classification.add("Concatenation Operation Keyword");
			s = "";
		}
		else if(s.matches("^(MAEK)$")) {
			lexemes.add(s);
			classification.add("Explicit Type Cast Keyword");
			s = "";
		}
		else if(s.matches("^(A)$")) {
			lexemes.add(s);
			classification.add("A Keyword");					
			s = "";
		}
		else if(s.matches("^(AN)$")) {
			lexemes.add(s);
			classification.add("AN Keyword");					
			s = "";
		}
		else if(s.matches("^(IS NOW A)$")) {
			lexemes.add(s);
			classification.add("Explicit Type Cast Keyword");
			s = "";
		}
		
		else if(s.matches("^(VISIBLE)$")) {
			lexemes.add(s);
			classification.add("Output Keyword");
			s = "";
		}
		else if(s.matches("^(GIMMEH)$")) {
			lexemes.add(s);
			classification.add("Input Keyword");
			s = "";
		}
		else if(s.matches("^(O RLY\\?)$")) {
			lexemes.add(s);
			classification.add("O RLY Keyword");
			s = "";
		}
		else if(s.matches("^(YA RLY)$")) {
			lexemes.add(s);
			classification.add("If-Then Delimiter");
			s = "";
		}
		else if(s.matches("^(MEBBE)$")) {
			lexemes.add(s);
			classification.add("YA RLY Keyword");
			s = "";
		}
		else if(s.matches("^(NO WAI)$")) {
			lexemes.add(s);
			classification.add("NO WAI Keyword");
			s = "";
		}
		else if(s.matches("^(OIC)$")) {
			lexemes.add(s);
			classification.add("If-Then Delimiter");
			s = "";
		}
		else if(s.matches("^(WTF\\?)$")) {
			lexemes.add(s);
			classification.add("Switch-Case Delimiter");
			s = "";
		}
		else if(s.matches("^(OMG)$")) {
			lexemes.add(s);
			classification.add("OMG Keyword");
			s = "";
		}
		else if(s.matches("^(OMGWTF)$")) {
			lexemes.add(s);
			classification.add("OMGWTF Keyword");
			s = "";
		}
		else if(s.matches("^(UPPIN)$")) {
			lexemes.add(s);
			classification.add("UPPIN Keyword");
			s = "";
		}
		else if(s.matches("^(NERFIN)$")) {
			lexemes.add(s);
			classification.add("NERFIN Keyword");
			s = "";
		}
		else if(s.matches("^(UPPIN)$")) {
			lexemes.add(s);
			classification.add("UPPIN Keyword");
			s = "";
		}
		else if(s.matches("^(TIL)$")) {
			lexemes.add(s);
			classification.add("TIL Keyword");
			s = "";
		}
		else if(s.matches("^(GTFO)$")) {
			lexemes.add(s);
			classification.add("GTFO Keyword");
			s = "";
		}
		else if(s.matches("^(WILE)$")) {
			lexemes.add(s);
			classification.add("WILE Keyword");
			s = "";
		}
		else if(s.matches("^(IM OUTTA YR)$")) {
			lexemes.add(s);
			classification.add("IM OUTTA YR Keyword");
			s = "";
		}
		else if(s.matches("^(\\,)$")) {
			lexemes.add(s);
			classification.add("Soft-Line/Command Break");
			s = "";
		}
		else if(s.matches("^([A-Za-z][A-Za-z0-9\\_]*)$") && 
				(classification.get(classification.size()-1) == "Variable Declaration" ||
				classification.get(classification.size()-1) == "Literal" ||
				classification.get(classification.size()-1) == "AN Keyword" ||
				classification.get(classification.size()-1) == "Input Keyword" ||
				(classification.get(classification.size()-1) == "Variable Identifier" &&
				classification.get(classification.size()-2) == "Input Keyword") ||
				classification.get(classification.size()-1) == "Arithmetic Operation Keyword" 
				)) {										//variable identifier
			lexemes.add(s);
			classification.add("Variable Identifier");
			s = "";
		}
		else if(s.matches("^(-?\\d+)$")) {					//numbr
			lexemes.add(s);
			classification.add("Literal");
			s = "";
		}
		else if(s.matches("^(-?\\d*\\.\\d+)$")) {			//numbar
			lexemes.add(s);
			classification.add("Literal");
			s = "";
		}
		else if(s.matches("^(\\\".*\\\")$")) {				//yarn
			lexemes.add("\"");
			classification.add("String Delimiter");
			s = s.substring(1,(s.length()-1));				//remove the ""
			lexemes.add(s);
			classification.add("Literal");
			lexemes.add("\"");
			classification.add("String Delimiter");
			s = "";
		}
		
		//if the string has a comma at the end
		if(containsComma) {
			lexemeChecker(",");
		}
		return s;
	}

	private void addMouseEventHandler() {
		file_btn.setOnMouseClicked(new EventHandler<MouseEvent>(){		//eventhandler for file chooser
			public void handle(MouseEvent e) {
				//file reading implementation
				File file = fileChooser.showOpenDialog(stage);
				
				try {
					if(file != null) {		//if file not empty
						BufferedReader br = new BufferedReader(new FileReader(file));
						String str;
						
						while((str=br.readLine())!=null) {										//read each line of file
							System.out.println("line:"+str);
							
							if(str.contains("\09") || str.contains("\t")) {
								str = str.replaceAll("[\09\t]+", "");
							}
							
							if(!str.contains(" ")) {											//if string can't be split (one word line)
								lexemeChecker(str);
							}else {
								String[] words = str.split(" ");									//split each word by space delimiter  //TOKENIZE
								String s = new String();
							
								s = words[0];
								s = s.replaceAll("[^a-zA-Z0-9\"]", ""); 							//clean
								
								for(int i=1;i<(words.length);i++) {
									System.out.println(words.length + " word:" + words[i]);
									s = lexemeChecker(s);					//check token if lexeme
									
									if(words.length != 1) {					//add next word to the string to compare
										if(s == "") {
											s = words[i];
										}else {
											s = s + " " + words[i];
										}
									}
									if(i == (words.length-1)) s = lexemeChecker(s);					//check last word
									System.out.println("s:"+s);		
								}
								System.out.println();
							}
						}
						//printing of lexemes table
						System.out.println("\n============LEXEMES============ n:" + lexemes.size());
						for(int i=0;i<lexemes.size();i++) {
							System.out.println(lexemes.get(i) + " :: " + classification.get(i));
						}
					}else {
						//print error if no file is selected
						System.out.println("[!] No file selected.");
					}
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});
	}
}
