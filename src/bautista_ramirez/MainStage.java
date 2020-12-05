package bautista_ramirez;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeMap;

import bautista_ramirez.MainStage;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainStage {
	private Scene scene;
	private Stage stage;
	private Group root;
	private Canvas canvas;
	private Button file_btn;
	private Button execute_btn;
	private FileChooser fileChooser;
	
	private ArrayList<String> lexemes;								//arraylist of lexemes
	private ArrayList<String> classification;						//arraylist of classification
	private ArrayList<ArrayList<String>> lexemesByLine;				//arraylist of lexemes
	private ArrayList<ArrayList<String>> classificationByLine;		//arraylist of classification
	private ArrayList<Integer> line_numByLine;						//arraylist of lexemes line number
	private ArrayList<String> identifiers;							//arraylist of identifiers
	private ArrayList<String> values;								//arraylist of value of identifiers
	public TableView<Lexeme> lexemeTable = new TableView();
	public TableView<Identifier> symbolTable = new TableView();
	private TextArea code;
	private TextArea output;
	
	public final static int WINDOW_WIDTH = 1080;
	public final static int WINDOW_HEIGHT = 720;
	public boolean hasSyntaxError = false;											//flag for syntax error
	public boolean hasMultiLineComment = false;										//flag for multiline comment
	public static ArrayList<String> lexemeLine = new ArrayList<String>();
	public static ArrayList<String> classificationLine = new ArrayList<String>();
	public static File file = new File("");
	
	public MainStage() {															//constructor for MainStage
		this.root = new Group();
		this.scene = new Scene(root, MainStage.WINDOW_WIDTH,MainStage.WINDOW_HEIGHT,Color.rgb(97,134,133));
		this.canvas = new Canvas(MainStage.WINDOW_WIDTH,MainStage.WINDOW_HEIGHT);
		this.lexemes = new ArrayList<String>();	
		this.classification = new ArrayList<String>();
		this.lexemesByLine = new ArrayList<ArrayList<String>>();	
		this.classificationByLine = new ArrayList<ArrayList<String>>();	
		this.line_numByLine = new ArrayList<Integer>();
		this.identifiers = new ArrayList<String>();	
		this.values = new ArrayList<String>();	
		this.file_btn = new Button();
		this.execute_btn = new Button();
		this.fileChooser = new FileChooser();
		this.code = new TextArea();
		this.output = new TextArea();
	}
	
	public void setStage(Stage stage) {								//function for adding elements to stage
		this.stage = stage;
		this.addMouseEventHandler();
		
		this.file_btn.setText("  SELECT FILE  ");
        this.file_btn.setFont(new Font(12));
		this.file_btn.setLayoutX(MainStage.WINDOW_WIDTH*0.01);
		this.file_btn.setLayoutY(MainStage.WINDOW_WIDTH*0.03);
		this.file_btn.setStyle("-fx-background-color: #d5f4e6; ");
		
		this.execute_btn.setText("          EXECUTE          ");
        this.execute_btn.setFont(new Font(12));
		this.execute_btn.setLayoutX(MainStage.WINDOW_WIDTH*0.48);
		this.execute_btn.setLayoutY(MainStage.WINDOW_HEIGHT*0.47);
		this.execute_btn.setStyle("-fx-background-color: #d5f4e6; ");
		
		lexemeTable.setLayoutX(MainStage.WINDOW_WIDTH*0.4);
		lexemeTable.setLayoutY(MainStage.WINDOW_HEIGHT*0.05);
		lexemeTable.setMinWidth(WINDOW_WIDTH*0.275);
		lexemeTable.setMaxHeight(WINDOW_HEIGHT*0.4);
		
		symbolTable.setLayoutX(MainStage.WINDOW_WIDTH*0.7);
		symbolTable.setLayoutY(MainStage.WINDOW_HEIGHT*0.05);
		symbolTable.setMinWidth(WINDOW_WIDTH*0.275);
		symbolTable.setMaxHeight(WINDOW_HEIGHT*0.4);
		
		TableColumn lexCol = new TableColumn("Lexeme");
		TableColumn classCol = new TableColumn("Classification");
		lexCol.setCellValueFactory(new PropertyValueFactory<>("lexeme"));
		lexCol.setMinWidth(150);
		classCol.setCellValueFactory(new PropertyValueFactory<>("classification"));
		classCol.setMinWidth(150);
		lexemeTable.getColumns().addAll(lexCol,classCol);
		
		TableColumn idCol = new TableColumn("Identifier");
		TableColumn valCol = new TableColumn("Value");
		idCol.setCellValueFactory(new PropertyValueFactory<>("identifier"));
		idCol.setMinWidth(150);
		valCol.setCellValueFactory(new PropertyValueFactory<>("value"));
		valCol.setMinWidth(150);
		symbolTable.getColumns().addAll(idCol,valCol);
		
		fileChooser.setInitialDirectory(new File("./lolcode-samples/"));
		
		this.code.setLayoutX(MainStage.WINDOW_WIDTH*0.01);
		this.code.setLayoutY(MainStage.WINDOW_HEIGHT*0.10);
		this.code.setMaxWidth(WINDOW_WIDTH*0.36);
		this.code.setMinHeight(MainStage.WINDOW_HEIGHT*0.35);

		this.output.setLayoutX(MainStage.WINDOW_WIDTH*0.01);
		this.output.setLayoutY(MainStage.WINDOW_HEIGHT*0.525);
		this.output.setMinWidth(WINDOW_WIDTH*0.975);
		this.output.setMinHeight(MainStage.WINDOW_HEIGHT*0.45);
		
		this.root.getChildren().addAll(canvas,file_btn,code,lexemeTable,symbolTable,output,execute_btn);
		this.stage.setTitle("LOLCODE INTERPRETER");
		this.stage.setScene(this.scene);
		this.stage.show();
	}	
	public void addLexeme(String s, String c) {												//function for adding lexeme
		lexemes.add(s);
		classification.add(c);
		lexemeLine.add(s);
		classificationLine.add(c);
	}
	
	public String lexemeChecker(String s) {
		boolean containsComma = false;
//		System.out.println("checking s:"+s);
		//check if there is comma on the current string
		if(s.matches("^([a-zA-Z]+,)$")) {						
			containsComma = true;
			s = s.substring(0,(s.length()-1));
		}
		
		//pattern checker
		if(s.matches("^(HAI)$")) {
			addLexeme(s,"Code Delimiter");
			s="";
		}
		else if(s.matches("^(KTHXBYE)$")) {
			addLexeme(s,"Code Delimiter");
			s = "";
		}
		else if(s.matches("^(I HAS A)$")) {					
			addLexeme(s,"Variable Declaration");
			s = "";
		}
		else if(s.matches("^(ITZ)$")) {
			addLexeme(s,"Variable Assignment");
			s = "";
		}
		else if(s.matches("^(R)$")) {
			addLexeme(s,"Assignment Keyword");
			s = "";
		}
		else if(s.matches("^(SUM OF)$")) {
			addLexeme(s,"Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(DIFF OF)$")) {
			addLexeme(s,"Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(PRODUKT OF)$")) {
			addLexeme(s,"Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(QUOSHUNT OF)$")) {
			addLexeme(s,"Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(MOD OF)$")) {
			addLexeme(s,"Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(BIGGR OF)$")) {
			addLexeme(s,"Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(SMALLR OF)$")) {
			addLexeme(s,"Arithmetic Operation Keyword");
			s = "";
		}
		else if(s.matches("^(BOTH OF)$")) {
			addLexeme(s,"Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(EITHER OF)$")) {
			addLexeme(s,"Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(WON OF)$")) {
			addLexeme(s,"Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(NOT)$")) {
			addLexeme(s,"Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(ANY OF)$")) {
			addLexeme(s,"Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(ALL OF)$")) {
			addLexeme(s,"Boolean Operation Keyword");
			s = "";
		}
		else if(s.matches("^(BOTH SAEM)$")) {
			addLexeme(s,"Comparison Operation Keyword");
			s = "";
		}
		else if(s.matches("^(DIFFRINT)$")) {
			addLexeme(s,"Comparison Operation Keyword");
			s = "";
		}
		else if(s.matches("^(SMOOSH)$")) {
			addLexeme(s,"Concatenation Operation Keyword");
			s = "";
		}
		else if(s.matches("^(MAEK)$")) {
			addLexeme(s,"Explicit Type Cast Keyword");
			s = "";
		}
		else if(s.matches("^(A)$")) {
			addLexeme(s,"A Keyword");
			s = "";
		}
		else if(s.matches("^(AN)$")) {
			addLexeme(s,"AN Keyword");
			s = "";
		}
		else if(s.matches("^(IS NOW A)$")) {
			addLexeme(s,"Explicit Type Cast Keyword");
			s = "";
		}
		
		else if(s.matches("^(VISIBLE)$")) {
			addLexeme(s,"Output Keyword");
			s = "";
		}
		else if(s.matches("^(GIMMEH)$")) {
			addLexeme(s,"Input Keyword");
			s = "";
		}
		else if(s.matches("^(O RLY\\?)$")) {
			addLexeme(s,"O RLY Keyword");
			s = "";
		}
		else if(s.matches("^(YA RLY)$")) {
			addLexeme(s,"YA RLY Keyword");
			s = "";
		}
		else if(s.matches("^(MEBBE)$")) {
			addLexeme(s,"MEBBE Keyword");
			s = "";
		}
		else if(s.matches("^(NO WAI)$")) {
			addLexeme(s,"NO WAI Keyword");
			s = "";
		}
		else if(s.matches("^(OIC)$")) {
			addLexeme(s,"If-Then Delimiter");
			s = "";
		}
		else if(s.matches("^(WTF\\?)$")) {
			addLexeme(s,"Switch-Case Delimiter");
			s = "";
		}
		else if(s.matches("^(OMG)$")) {
			addLexeme(s,"OMG Keyword");
			s = "";
		}
		else if(s.matches("^(OMGWTF)$")) {
			addLexeme(s,"OMGWTF Keyword");
			s = "";
		}
		else if(s.matches("^(UPPIN)$")) {
			addLexeme(s,"UPPIN Keyword");
			s = "";
		}
		else if(s.matches("^(NERFIN)$")) {
			addLexeme(s,"NERFIN Keyword");
			s = "";
		}
		else if(s.matches("^(TIL)$")) {
			addLexeme(s,"TIL Keyword");
			s = "";
		}
		else if(s.matches("^(GTFO)$")) {
			addLexeme(s,"GTFO Keyword");
			s = "";
		}
		else if(s.matches("^(WILE)$")) {
			addLexeme(s,"WILE Keyword");
			s = "";
		}
		else if(s.matches("^(IM OUTTA YR)$")) {
			addLexeme(s,"IM OUTTA YR Keyword");
			s = "";
		}
		else if(s.matches("^(\\,)$")) {
			addLexeme(s,"Soft-Line/Command Break");
			s = "";
		}
		else if(s.matches("^(WIN)$")) {
			addLexeme(s,"Literal");
			s = "";
		}
		else if(s.matches("^(FAIL)$")) {
			addLexeme(s,"Literal");
			s = "";
		}
		else if(s.matches("^(IT)$")) {
			addLexeme(s,"Implicit Variable");
			s="";
		}
		else if (s.matches("BTW")) {														//ignore comments
			s = "";
		}
		else if(s.matches("^(TLDR)$")) {
			hasMultiLineComment = false;
			s="";
		}
		else if(s.matches("^(OBTW)$")) {
			hasMultiLineComment = true;
			s="";
		}
		else if(s.matches("^(NUMBR)$")) {
			addLexeme(s,"NUMBR Keyword");
			s="";
		}
		else if(s.matches("^([A-Za-z][A-Za-z0-9\\_]*)$")){									//variable identifier
			if(classification.get(classification.size()-1) == "Variable Declaration") {		//variable declaration (I HAS A)
				addLexeme(s,"Variable Identifier");
				s = "";
			}
			else if(lexemes.contains(s)) {													//variables used on other operations should be declared
				addLexeme(s,"Variable Identifier");
				s = "";
			}
			//if varident not in list of declared variables, error
		}
		else if(s.matches("^(-?\\d+)$")) {														//numbr/integer
			if(!lexemes.isEmpty()) {
				if(lexemes.get(lexemes.size()-1).matches("HAI")) {
					addLexeme(s,"LOLCode Version");
				}else {
					addLexeme(s,"Literal");
				}
			}
			s = "";
		}
		else if(s.matches("^(-?\\d*\\.\\d+)$")) {												//numbar/float
			if(!lexemes.isEmpty()) {
				System.out.println(lexemes.get(lexemes.size()-1));
				if(lexemes.get(lexemes.size()-1).matches("HAI")) {
					addLexeme(s,"LOLCode Version");
				}else {
					addLexeme(s,"Literal");
				}
			}
			s = "";
		}
		else if(s.matches("^(\\\".*\\\")$")) {													//yarn/string
			addLexeme("\"","String Delimiter");
			s = s.substring(1,(s.length()-1));													//remove the ""
			addLexeme(s,"String Literal");
			addLexeme("\"","String Delimiter");
			s = "";
		}
		else if(s.matches("^(HOW IZ I)$")) {													//function delimeter
			addLexeme(s,"HOW IZ I Keyword");
			s = "";
		}
		else if(s.matches("^([A-Za-z][A-Za-z0-9\\_]*)$") &&				
				(classification.get(classification.size()-1) == "HOW IZ I Keyword")
				){																				//function identifier(bonus)
			addLexeme(s,"Function Identifier");
			s = "";
		}
		else if(s.matches("^(IF U SAY SO)$")) {													//function delimeter
			addLexeme(s,"IF U SAY SO Keyword");
			s = "";
		}	
		//if the string has a comma
		if(containsComma) {
			lexemeChecker(",");
		}
		return s;
	}	
	public void hasHAI() {																	//function for checking if has opening code delimiter
		//the code must be start with HAI
		if(lexemes.size() != 0 && lexemes.get(0).matches("HAI") == false) 
			hasSyntaxError = true;
	}
	public void hasKTHXBYE(){																//function for checking if has closing code delimiter
		//the code must be delimited by KTHXBYE
		
		if(lexemes.get(lexemes.size()-1).matches("KTHXBYE") == false) {
			hasSyntaxError = true;
			System.out.println("pasok");
		}
			
	}	
	private void addLiteralSymbol(String identifier, String value) {						
		identifiers.add(identifier);
		values.add(value);
	}
	private boolean isComment(String str) {													//checker if comment
		//check if string is a comment
		if(str.matches("^(OBTW)$")) hasMultiLineComment = true;								//flag for multi-line comment
		if(str.matches("^(BTW)$") || str.matches("^(OBTW)$") || str.matches("^(TLDR)$")){
			return true;
		}else return false;
	}	
	private void setLexemeTable() {															//function for adding elements to Lexeme TableView
		//printing of lexemes lexemeTable
		ObservableList<Lexeme> lexTable = FXCollections.observableArrayList();
		//System.out.println("\n============LEXEMES============ n:" + lexemes.size());
		for(int i=0;i<lexemes.size();i++) {
			lexTable.add(new Lexeme(lexemes.get(i), classification.get(i)));
			//System.out.println(lexemes.get(i) + " :: " + classification.get(i));
		}
		lexemeTable.setItems(lexTable);		//add to tableview content	
//		System.out.println("lexeme count: "+lexemes.size());
//		
//		System.out.println("lexemebyline: "+lexemesByLine.size());
//		for (ArrayList<String> arr : lexemesByLine) {
//			System.out.println(arr.toString());
//		}
//		for (ArrayList<String> arr : classificationByLine) {
//			System.out.println(arr.toString());
//		}
	}	
	private void setSourceCode(String program) {											//function for adding elements to source code text area
		code.setText(program);
	}	
	private void setTerminal(String out) {													//function for adding elements to execute text area
		output.setText(out);
		System.out.println(out);
	}	
	private boolean isOneWord(String str) {
		if(!str.contains(" ")) return true;
		else return false;
	}	
	private String removeTabs(String str) {
		if(str.contains("\\x{09}") || str.contains("\t")) {									//remove tabs from the program
			str = str.replaceAll("[\\x{09}\t]+", "");
		}
		return str;
	}	
	private boolean isStringEmpty(String s) {												//checker if string = ""
		if(s.matches("")== true) return true;
		else return false;
	}	
	private void clearExecuteBtn() {														//function for clearing execute button
		//clear for next click
		identifiers.clear();
		values.clear();
		for ( int i = 0; i<symbolTable.getItems().size(); i++) {
			symbolTable.getItems().clear(); 
	    } 
	}	
	private void clearFileBtn() {
		//clear incase of next click
		lexemes.clear();
		classification.clear();
		lexemesByLine.clear();
		classificationByLine.clear();
		line_numByLine.clear();
		output.clear();
		code.clear();
		identifiers.clear();
		values.clear();
		for ( int i = 0; i<lexemeTable.getItems().size(); i++) {
			lexemeTable.getItems().clear(); 
	    } 
		for ( int i = 0; i<symbolTable.getItems().size(); i++) {
			symbolTable.getItems().clear(); 
	    }
	}
	private void setSymbolTable() {
		System.out.println("\n=======SYMBOL TABLE======");
		if(identifiers.size()!=0) {
			for(int i=0;i<identifiers.size();i++) {
				System.out.println(identifiers.get(i) + " = "+ values.get(i));
			}
			ObservableList<Identifier> symTable = FXCollections.observableArrayList();
			for(int i=0;i<identifiers.size();i++) {
				symTable.add(new Identifier(identifiers.get(i), values.get(i).toString()));
			}
//			symTable = symTable.sorted();													// SORTING ONLY WORKS SOMETIMES??????????
			symbolTable.setItems(symTable);													//add to tableview content	
			System.out.println("identifier count: "+identifiers.size());
		}
	}
	
//	private void SyntaxChecker() {															//function for other syntax checking
//		//check if every variable declaration has a declared value
//		for(int i=0;i<lexemes.size();i++) {
//			if(lexemes.get(i).matches("ITZ")) {
//				try{
//					//check if next lexeme is a literal
//					if(!classification.get(i+1).matches("Literal")){
//						System.out.println(classification.get(i+1));
//						//!classification.get(i+1).matches("String Delimiter")
//						hasSyntaxError = true;
//					}
//				}catch(Exception e){
//					hasSyntaxError = true;
//				}
//			}
//		}
//	}
	
	private String evaluateArithmetic(ArrayList<String> lexemeLine, ArrayList<String> classificationLine) {
		Stack<Number> stack = new Stack<Number>();
		
		ArrayList<String> line = (ArrayList<String>) lexemeLine.clone();								//get the reverse of each line
		Collections.reverse(line);
		ArrayList<String> line_class = (ArrayList<String>) classificationLine.clone();					//get the reverse of each line_class
		Collections.reverse(line_class);
		
		for(int j=0;j<lexemeLine.size();j++) {															//for every token of a line
			if(line.get(j).matches("^(-?\\d*\\.\\d+)$")) {												//float/numbar
				stack.push(Float.parseFloat(line.get(j)));
			}else if(line.get(j).matches("^(-?\\d+)$")) {												//integer/numbr
				stack.push(Integer.parseInt(line.get(j)));
			}else if(line_class.get(j).equals("Arithmetic Operation Keyword")){							//if operator
				boolean isFloat = false;
				Number op1 = stack.pop();
				Number op2 = stack.pop();
				
				if(op1 instanceof Float || op2 instanceof Float) isFloat = true;						//check if float
				if(isFloat) {																			//if isFloat, the result must also be float/numbar
					if(line.get(j).matches("^(SUM OF)$")) {					//addition
						stack.push(op1.floatValue() + op2.floatValue());
					}
					else if(line.get(j).matches("^(DIFF OF)$")) {			//subtraction
						stack.push(op1.floatValue() - op2.floatValue());
					}
					else if(line.get(j).matches("^(PRODUKT OF)$")) {		//multiplication
						stack.push(op1.floatValue() * op2.floatValue());
					}
					else if(line.get(j).matches("^(QUOSHUNT OF)$")) {		//division
						stack.push(op1.floatValue() / op2.floatValue());
					}
					else if(line.get(j).matches("^(MOD OF)$")) {			//mod division
						stack.push(op1.floatValue() % op2.floatValue());
					}
					else if(line.get(j).matches("^(BIGGR OF)$")) {			//bigger
						if(op1.floatValue() >= op2.floatValue()) {
							stack.push(op1.floatValue());
						}else stack.push(op2.floatValue());
					}
					else if(line.get(j).matches("^(SMALLR OF)$")) {			//smaller
						if(op1.floatValue() <= op2.floatValue()) {
							stack.push(op1.floatValue());
						}else stack.push(op2.floatValue());
					}
				}else {	//if !isFloat, the result must be an integer/numbr
					if(line.get(j).matches("^(SUM OF)$")) {					//addition
						stack.push(op1.intValue() + op2.intValue());
					}
					else if(line.get(j).matches("^(DIFF OF)$")) {			//subtraction
						stack.push(op1.intValue() - op2.intValue());
					}
					else if(line.get(j).matches("^(PRODUKT OF)$")) {		//multiplication
						stack.push(op1.intValue() * op2.intValue());
					}
					else if(line.get(j).matches("^(QUOSHUNT OF)$")) {		//division
						stack.push(op1.intValue() / op2.intValue());
					}
					else if(line.get(j).matches("^(MOD OF)$")) {			//mod division
						stack.push(op1.intValue() % op2.intValue());
					}
					else if(line.get(j).matches("^(BIGGR OF)$")) {			//bigger
						if(op1.intValue() >= op2.intValue()) {
							stack.push(op1.intValue());
						}else stack.push(op2.intValue());
					}
					else if(line.get(j).matches("^(SMALLR OF)$")) {			//smaller
						if(op1.intValue() <= op2.intValue()) {
							stack.push(op1.intValue());
						}else stack.push(op2.intValue());
					}
				}
			}else if(identifiers.contains(line.get(j))) {													//if varident
				for(int k=0;k<identifiers.size();k++) {
					if(identifiers.get(k).equals(line.get(j))) {
						if(values.get(k).matches("^(-?\\d*\\.\\d+)$")) {			//float/numbar
							stack.push(Float.parseFloat(values.get(k)));
						}else if(values.get(k).matches("^(-?\\d+)$")) {				//integer/numbr
							stack.push(Integer.parseInt(values.get(k)));
						}
					}
				}
			}
		}
		Number result = stack.pop();														//the last item on the stack is the result
//		System.out.print(lexemeLine.toString() + " ");
//		System.out.println("result = "+result);
		stack.clear();		
		
		//update values of identifier
		for(int i=0;i<identifiers.size();i++) {
			if(identifiers.get(i).equals("IT")) {
				//update value of IT
				values.set(i, result.toString());
				break;
			}
		}
		
//		System.out.println("symbolTable: "+identifiers.size());
//		for(int k=0;k<identifiers.size();k++) {
//			System.out.println(identifiers.get(k) + " = "+ values.get(k));
//		}
		return result.toString();
	}
	
	private void varDecInit() {
		boolean varDecCheck = false,
				varIdentCheck = false,
				varAssCheck = false,
				outIdentCheck = false,
				strDelCheck = false;
		String output = "";
		
		//initialize IT
		identifiers.add("IT");																					//rener add mo nalang kaya agad si IT with NOOB value para di marami instance
		values.add("NOOB");
		
		for (int lex=0; lex<lexemeTable.getItems().size(); lex++) {		
//			System.out.println("Output: "+output);
			// ============== I HAS A ==============	
			if (classification.get(lex).equals("Variable Declaration")) {
//				System.out.println("[Variable Declaration]");
				varDecCheck = true;
				continue;
			}
			if (varDecCheck) {
				if (classification.get(lex).equals("Variable Identifier")) {
//					System.out.println("[Variable Identifier]");
					varIdentCheck = true;
					varDecCheck = false;
				}
				// If next lexeme is not ITZ, var is NOOB
				if (!classification.get(lex+1).equals("Variable Assignment") ) {
//					System.out.println("[NOOB Variable]");
//					System.out.println("Adding: "+lexemes.get(lex-1));
					addLiteralSymbol(lexemes.get(lex), "NOOB");
					varIdentCheck = false;
					continue;
				}
			}
			if (varIdentCheck) {
				if (classification.get(lex).equals("Variable Assignment")) {
//					System.out.println("[Variable Assignment]");
					varAssCheck = true;
				}
			}
			if (varAssCheck) {
				if (classification.get(lex).equals("Literal")) {
//					System.out.println("[Literal]");
					addLiteralSymbol(lexemes.get(lex-2), lexemes.get(lex));	
						// get varident and its value from lexeme table
					varAssCheck = false;
					continue;					
				}
			}
		}
	}
	
	private String printVisible(ArrayList<String> line, ArrayList<String> line_class) {							//function for printing to terminal
		String output = "";
		for(int i=0;i<line.size();i++) {
			//if visible varident
			if (line_class.get(i).equals("Variable Identifier") || 
					line_class.get(i).equals("Implicit Variable")) {
				output += values.get(identifiers.indexOf(line.get(i))).toString();
			}
			//if boolean expression
			else if(line_class.contains("Boolean Operation Keyword")) {
				output += evaluateBoolean(line,line_class);
				break;
			}
			//if comparison expression
			else if(line_class.contains("Comparison Operation Keyword")) {
				output += evaluateComparison(line,line_class);
				break;
			}
			//if arithmetic expression
			else if(line_class.contains("Arithmetic Operation Keyword")) {
				output += evaluateArithmetic(line,line_class);
				break;
			}
			//if visible literal
			else if (line_class.get(i).contains("Literal")) {
				output += line.get(i).toString();
			}
		}
		output += "\n";
		return output;
	}
	
	private void getGimmeh(ArrayList<String> line, ArrayList<String> line_class) {									//function that handles user input
		
		//update UI of dialogbox
		TextInputDialog user_input = new TextInputDialog(); 
		user_input.setTitle("User Input");
		user_input.setHeaderText("User Input");
		user_input.setContentText("Enter value: ");
		
		//get user input
		Optional<String> gimmeh_input = user_input.showAndWait(); 
		
		//check if user input something
		if(gimmeh_input.isPresent()) {
			//update value of varident
			for(int i=0;i<line.size();i++) {
				if (line_class.get(i).equals("Variable Identifier") || 
						line_class.get(i).equals("Implicit Variable")) {
					values.set(identifiers.indexOf(line.get(i)), gimmeh_input.get().toString());
					break;
				}
			}
		}
		
	}
	
	private String evaluateBoolean(ArrayList<String> lexemeLine, ArrayList<String> classificationLine) {			//function for boolean operation
		//1 = WIN
		//0 = FAIL
		System.out.print("line: "+ lexemeLine.toString());
		Stack<Boolean> stack = new Stack<Boolean>();
		
		ArrayList<String> line = (ArrayList<String>) lexemeLine.clone();										//get the reverse of each line
		Collections.reverse(line);
		ArrayList<String> line_class = (ArrayList<String>) classificationLine.clone();							//get the reverse of each line_class
		Collections.reverse(line_class);
		
		for(int j=0;j<line.size();j++) {
			//if WIN[true]
			if(line.get(j).equals("WIN")) {
				stack.push(true);
			}
			//if FAIL[false]
			else if(line.get(j).equals("FAIL")) {
				stack.push(false);
			}
			//if NOT
			else if(line.get(j).equals("NOT")) {						//not
				boolean op = stack.pop();
				//if WIN
				if(op == true) {
					//push 0
					stack.push(false);
				}
				//if FAIL
				else stack.push(true);
			}
			//if infinite arity AND
			else if(line.get(j).equals("ALL OF")) {						//infinite arity and
				//if stack has at least 1 false
				if(stack.contains(false)){
					stack.clear();
					stack.push(false);
				}else {
					stack.clear();
					stack.push(true);
				}
			}
			//if infinite arity OR
			else if(line.get(j).equals("ANY OF")) {						//infinite arity or
				//if stack has at least 1 true
				if(stack.contains(true)){
					stack.clear();
					stack.push(true);
				}else {
					stack.clear();
					stack.push(false);
				}
			}
			else if(identifiers.contains(line.get(j))) {				//if varident
				for(int k=0;k<identifiers.size();k++) {
					if(identifiers.get(k).equals(line.get(j))) {
						if(values.get(k).equals("WIN")) {
							stack.push(true);
						}else stack.push(false);
					}
				}
			}
			//if OPERATOR
			else if(line_class.get(j).equals("Boolean Operation Keyword")) {
				boolean op1 = stack.pop();
				boolean op2 = stack.pop();
				
				//cases for boolean operator
				if(line.get(j).equals("BOTH OF")) {						//and
					stack.push(op1 & op2);
				}
				else if(line.get(j).equals("EITHER OF")) {				//or
					stack.push(op1 | op2);
				}
				else if(line.get(j).equals("WON OF")) {					//or
					stack.push(op1 ^ op2);
				}
			}
		}
		//last element of stack is result
		String result = "";
		if(stack.peek() == false) result = "FAIL";
		else result = "WIN";
		System.out.println("result = " + result);
		
		//update IT
		for(int i=0;i<identifiers.size();i++) {
			if(identifiers.get(i).equals("IT")) {
				//update value of IT
				values.set(i, result.toString());
				break;
			}
		}
		
		return result;
	}
	
	private String checkDataType(String var, String s) {
		String result = "Literal";
		
		ArrayList<ArrayList<String>> line = (ArrayList<ArrayList<String>>) lexemesByLine.clone();
		Collections.reverse(line);
		ArrayList<ArrayList<String>> line_class = (ArrayList<ArrayList<String>>) classificationByLine.clone();
		Collections.reverse(line_class);
		
		for(int i=0;i<line.size();i++) {
			if(line.get(i).contains(var) && line.get(i).contains(s)) {
				//check if value is declared as string literal
				try {
					if(line_class.get(i).get(line.get(i).indexOf(s)-1).equals("String Delimiter")) {
						result = "String Literal";
						break;
					}
				}catch(Exception e) {}
			}
		}
		
		return result;
	}
	
	private String evaluateComparison(ArrayList<String> lexemeLine, ArrayList<String> classificationLine) {
		String result = "";
		Stack<String> stack = new Stack<String>();				//will hold the token/literal
		Stack<String> datatype = new Stack<String>();			//will hold the datatype of the literal
		
		ArrayList<String> line = (ArrayList<String>) lexemeLine.clone();								//get the reverse of each line
		Collections.reverse(line);
		ArrayList<String> line_class = (ArrayList<String>) classificationLine.clone();					//get the reverse of each line_class
		Collections.reverse(line_class);
		
		for(int j=0;j<line.size();j++) {
			System.out.println("stack: " + stack.toString());
			System.out.println("datatype: " + datatype.toString());
			
			if(line.get(j).equals("WIN")) {																//WIN
				stack.push(line.get(j));
				datatype.push("boolean");
			}else if(line.get(j).equals("FAIL")) {														//FAIL
				stack.push(line.get(j));
				datatype.push("boolean");
			}
			else if(line_class.get(j).equals("String Literal")) {
				stack.push(line.get(j));
				datatype.push("string");
			}else if(line.get(j).matches("^(-?\\d*\\.\\d+)$")) {										//float/numbar
				stack.push(line.get(j));
				datatype.push("float");
			}else if(line.get(j).matches("^(-?\\d+)$")) {												//integer/numbr
				stack.push(line.get(j));
				datatype.push("integer");
			}
			else if(identifiers.contains(line.get(j))) {												//if varident
				for(int k=0;k<identifiers.size();k++) {
					if(identifiers.get(k).equals(line.get(j))) {
						//check datatype of value
						String temp = checkDataType(line.get(j),values.get(k));
						
						if(temp.equals("String Literal")) {												//string
							stack.push(values.get(identifiers.indexOf(line.get(j))));
							datatype.push("string");
						}else {
							//cases for values of varident
							if(values.get(k).matches("^(-?\\d*\\.\\d+)$")) {							//float/numbar
								stack.push(values.get(identifiers.indexOf(line.get(j))));
								datatype.push("float");
							}else if(values.get(k).matches("^(-?\\d+)$")) {								//integer/numbr
								stack.push(values.get(identifiers.indexOf(line.get(j))));
								datatype.push("integer");
							}else if(values.get(k).equals("WIN")) {										//WIN
								stack.push(values.get(identifiers.indexOf(line.get(j))));
								datatype.push("boolean");
							}else if(values.get(k).equals("FAIL")) {									//FAIL
								stack.push(values.get(identifiers.indexOf(line.get(j))));
								datatype.push("boolean");
							}
						}
					}
				}
			}
			else if(line_class.get(j).equals("Arithmetic Operation Keyword")) {						//if compound
				System.out.println("stack: " + stack.toString());
				System.out.println("datatype: " + datatype.toString());
				
				String dt1 = datatype.pop();
				String dt2 = datatype.pop();
				
				String op1 = stack.pop();
				String op2 = stack.pop();
				
				//check if both are of the valid datatype
				if((dt1.equals("float") || dt1.equals("integer")) &&
					(dt2.equals("float") || dt2.equals("integer"))	) {								//the tokens must be either integer or float to perform arithmetic
					ArrayList<String> l = new ArrayList<String>();
					ArrayList<String> c = new ArrayList<String>();
					
					l.add(line.get(j));
					l.add(op1.toString());
					l.add(op2.toString());
					c.add("Arithmetic Operation Keyword");
					c.add("Literal");
					c.add("Literal");
					
					if(dt1.equals("float") || dt2.equals("float")) {								//check if float
						String op = evaluateArithmetic(l,c);
						//System.out.println("l: " + l + "result = " + op);
						Float temp = Float.parseFloat(op);
						stack.push(temp.toString());
						datatype.push("float");
					}else {																			//if !isFloat, the result must be an integer/numbr
						String op = evaluateArithmetic(l,c);
						//System.out.println("l: " + l + "result = " + op);
						Integer temp = Integer.parseInt(op);
						stack.push(temp.toString());
						datatype.push("integer");
					}
				}else {
					stack.push("FAIL");
					datatype.push("boolean");
				}
			}
			else if(line_class.get(j).equals("Comparison Operation Keyword")){								//if operator
				
				String dt1 = datatype.pop();
				String dt2 = datatype.pop();
				String op1 = stack.pop();
				String op2 = stack.pop();
				
				//check if same datatype
				if(!dt1.equals(dt2) &&
						line.get(j).equals("BOTH SAEM")) {													//if not same datatype for BOTH SAEM, return FAIL
					stack.push("FAIL");
					datatype.push("boolean");
				}else if(!dt1.equals(dt2) &&
						line.get(j).equals("DIFFRINT")) {													//if not same datatype for DIFFRINT, return WIN
					stack.push("WIN");
					datatype.push("boolean");
				}
				else {																						//if same datatype
					System.out.println("stack: " + stack.toString());
					if(dt1.equals("float") && dt2.equals("float")) {										//check if float
						if(line.get(j).matches("^(BOTH SAEM)$")) {					//equality
							if(Float.compare(Float.parseFloat(op1),Float.parseFloat(op2)) == 0){
								stack.push("WIN");
								datatype.push("boolean");
							}else {
								stack.push("FAIL");
								datatype.push("boolean");
							}
						}
						else if(line.get(j).matches("^(DIFFRINT)$")) {				//non equality
							if(Float.compare(Float.parseFloat(op1),Float.parseFloat(op2)) != 0){
								stack.push("WIN");
								datatype.push("boolean");
							}else {
								stack.push("FAIL");
								datatype.push("boolean");
							}
						}
					}
					
					else if(dt1.equals("integer") && dt2.equals("integer")) {								//check if int
						if(line.get(j).matches("^(BOTH SAEM)$")) {					//equality
							if(Integer.compare(Integer.parseInt(op1),Integer.parseInt(op2)) == 0){
								stack.push("WIN");
								datatype.push("boolean");
							}else {
								stack.push("FAIL");
								datatype.push("boolean");
							}
						}
						else if(line.get(j).matches("^(DIFFRINT)$")) {				//non equality
							if(Integer.compare(Integer.parseInt(op1),Integer.parseInt(op2)) != 0){
								stack.push("WIN");
								datatype.push("boolean");
							}else {
								stack.push("FAIL");
								datatype.push("boolean");
							}
						}
					}
					else if(dt1.equals("string") && dt2.equals("string")) {									//check if both string
						if(line.get(j).matches("^(BOTH SAEM)$")) {					//equality
							if(op1.equals(op2)) {
								stack.push("WIN");
								datatype.push("boolean");
							}else {
								stack.push("FAIL");
								datatype.push("boolean");
							}
						}
						else if(line.get(j).matches("^(DIFFRINT)$")) {				//non equality
							if(!op1.equals(op2)) {
								stack.push("WIN");
								datatype.push("boolean");
							}else {
								stack.push("FAIL");
								datatype.push("boolean");
							}
						}
					}
					else if(dt1.equals("boolean") && dt2.equals("boolean")) {								//check if boolean
						if(line.get(j).matches("^(BOTH SAEM)$")) {					//equality
							if(op1.equals(op2)) {
								stack.push("WIN");
								datatype.push("boolean");
							}else {
								stack.push("FAIL");
								datatype.push("boolean");
							}
						}
						else if(line.get(j).matches("^(DIFFRINT)$")) {				//non equality
							if(!op1.equals(op2)) {
								stack.push("WIN");
								datatype.push("boolean");
							}else {
								stack.push("FAIL");
								datatype.push("boolean");
							}
						}
					}
				}
			}
//			System.out.println("stack: " + stack.toString());
//			System.out.println("datatype: " + datatype.toString());
		}
		//the last item on the stack is result
		result = stack.pop();
		
		//update IT variable
		for(int i=0;i<identifiers.size();i++) {
			if(identifiers.get(i).equals("IT")) {
				//update value of IT
				values.set(i, result.toString());
				break;
			}
		}
		return result;
	}
	
	private void variableAssignment(ArrayList<String> lexemeLine, ArrayList<String> classificationLine) {
		String value = "";
		//System.out.print("line: "+ lexemeLine.toString());
		//cases for variable assignment
		ArrayList<String> l = new ArrayList<String>();
		ArrayList<String> c = new ArrayList<String>();
		
		for(String e : lexemeLine.subList(1, lexemeLine.size())) l.add(e);
		for(String e : classificationLine.subList(1, lexemeLine.size())) c.add(e);
		
		if(classificationLine.contains("Boolean Operation Keyword")) {
			value = evaluateBoolean(l,c);
		}
		else if(classificationLine.contains("Comparison Operation Keyword")) {
			value = evaluateComparison(l,c);
		}
		else if(classificationLine.contains("Arithmetic Operation Keyword")) {
			value = evaluateArithmetic(l,c);
		}
		else if(classificationLine.contains("String Literal")) {
			value = lexemeLine.get(classificationLine.indexOf("String Literal"));
		}
		else if(classificationLine.contains("Literal")) {
			//look for index of literal
			value = lexemeLine.get(classificationLine.indexOf("Literal"));
		}
		//System.out.println("value = "+ value);
		
		
		//set value of varident
		for(int i=0;i<identifiers.size();i++) {
			if(identifiers.get(i).equals(lexemeLine.get(0))) {
				values.set(i, value);
				break;
			}
		}
	}
	
	private boolean variableAssignmentSyntaxChecker(ArrayList<String> lexemeLine, ArrayList<String> classificationLine) {	//function for checking syntax of variable declaration
		
		if(lexemeLine.size() <= 2) return false;													//the size of the line must at least be 3
		if(!identifiers.contains(lexemeLine.get(0))) return false;									//the variable used must be declared/IT
		if(!classificationLine.get(1).equals("Assignment Keyword")) return false;					//if no R keyword
		
		//else no syntax error
		return true;
	}
	
	private boolean GimmehChecker(ArrayList<String> lexemeLine, ArrayList<String> classificationLine) {			//checker for invalid IO syntax

		if(lexemeLine.size() == 1 || lexemeLine.size()>2) return false;											//the gimmeh statement should only be gimmeh varident
		if(!identifiers.contains(lexemeLine.get(1))) return false;												//if variable not declared/IT
		
		//else no syntax error
		return true;
	}	
	
	private void printError(int line_number) {
		String out = "";
		out = "$lci "+ file.getName()+"\n";
		out += "[ ! ] Error in line "+line_number;
		
		hasSyntaxError = true;																		
		setTerminal(out);																			//print error to interface
	}
	
	private ArrayList<Integer> evaluateIfElse(String IT, int i, ArrayList<ArrayList<String>> lexeme, ArrayList<ArrayList<String>> classification){
		ArrayList<Integer> skip = new ArrayList<>();
		int h, current=0, buffer = 0;
		if (IT.equals("WIN")) {
			for (h=i+1; h<classification.size(); h++) {
				current = h;
				System.out.println("YUM: "+classification.get(h)+" h: "+h);
				if (classification.get(h).contains("O RLY Keyword")) buffer+=1;
				else if (classification.get(h).contains("NO WAI Keyword")) {
					if (buffer == 0) {
						break;
					}
					else buffer-=1;
				}
				else if (classification.get(h).contains("If-Then Delimiter")) break;
			}
			for (h=current+1; h<classification.size(); h++) {
				if (classification.get(h).contains("O RLY Keyword")) buffer+=1;
				else if (classification.get(h).contains("NO WAI Keyword")) {
					if (buffer == 0) break;
					else buffer-=1;
				}
				else if (classification.get(h).contains("If-Then Delimiter")) break;
				skip.add(h);
			}
		} else {
			for (h=i+1; h<classification.size(); h++) {
				
				skip.add(h);
				if (classification.get(h).contains("O RLY Keyword")) buffer+=1;
				else if (classification.get(h).contains("NO WAI Keyword")) {
					if (buffer == 0) {
						break;
					}
					else buffer-=1;
				}
				else if (classification.get(h).contains("If-Then Delimiter")) break; 
			}
		}
		return skip;
	}
	
	private ArrayList<Integer> evaluateSwitch(String IT, int i, ArrayList<ArrayList<String>> lexeme, ArrayList<ArrayList<String>> classification) {
		System.out.println("I GOT IN.");
		ArrayList<Integer> skip = new ArrayList<>();
		int h, buffer = 0;
		boolean skipFlag = true, enteredFlag = false;
		for (h=i+1;h<classification.size();h++) {
			System.out.println("skipFlag: "+skipFlag);
			if (classification.get(h).contains("OMG Keyword")) {				
				System.out.println("Comparing "+lexeme.get(h).get(1)+ " and "+IT);
				if (lexeme.get(h).get(1).equals(IT)) {
					skipFlag = false;
					enteredFlag = true;
				}
			}
			else if (classification.get(h).contains("Switch-Case Delimiter")) buffer+=1;
			else if (classification.get(h).contains("If-Then Delimiter")) {
				if (buffer == 0) break;
				else buffer-=1;
			}
			else if (classification.get(h).contains("GTFO Keyword")) skipFlag = true;
			else if (classification.get(h).contains("OMGWTF Keyword")) {
				if (!enteredFlag) skipFlag = false;
			}
			else if (skipFlag) skip.add(h);
		}		
		return skip;
	}
	
	private void runProgram() {																		//function for execute button
		String IT = "";
		String output = "";
		ArrayList<Integer> skipList = new ArrayList<>();
		ArrayList<Integer> newSkips = new ArrayList<>();
		
		//varDecInit yung dati mong runProgram
		//cinall ko lang para gumana yung with variable dec/ init
		//pakipattern nalang parang sa baba para maayos hehe
		varDecInit();																							
																												
		//check every line statement/s
		for(int i=0;i<lexemesByLine.size();i++) {
			if (!skipList.isEmpty())
			if (skipList.contains(i)) {
				skipList.remove((Object) i);
				continue;
			}
			ArrayList<String> line = lexemesByLine.get(i);
			ArrayList<String> line_class = classificationByLine.get(i);
			//cases for every line
			
			//if line has var dec/init
			
			//if line has var assignment
			if(line_class.contains("Assignment Keyword")) {												
				if(variableAssignmentSyntaxChecker(line, line_class)) {									//check if valid syntax
					variableAssignment(line, line_class);												//perform variable assignment
				}else {
					printError(line_numByLine.get(i));													//if error found, print line number error then break
					break;
				}
			}
			//if line has gimmeh
			else if(line_class.contains("Input Keyword")) {
				if(GimmehChecker(line, line_class)) {													//check if valid syntax
					getGimmeh(line, line_class);														//perform gimmeh
				}else {
					printError(line_numByLine.get(i));													//if error found, print line number error then break
					break;
				}
			}
			//if line has visible
			else if(line_class.contains("Output Keyword")) {
				output += printVisible(line,line_class);
			}
			//if line has comparison
			else if(line_class.contains("Comparison Operation Keyword")) {
				IT = evaluateComparison(line,line_class);
				//System.out.println("IT = " + IT);
			}
			//if line has boolean
			else if(line_class.contains("Boolean Operation Keyword")) {
				IT = evaluateBoolean(line,line_class);
				//System.out.println("IT = " + IT);
			}
			//if line has arithmetic
			else if(line_class.contains("Arithmetic Operation Keyword")) {
				IT = evaluateArithmetic(line,line_class);
				//System.out.println("IT = " + IT);
			}
			//if line has if-else
			else if(line_class.contains("O RLY Keyword")) {
				newSkips = evaluateIfElse(IT,i,lexemesByLine,classificationByLine);
				newSkips.forEach(skipList::add);
			}
			//if line has switch case
			else if(line_class.contains("Switch-Case Delimiter")) {
				newSkips = evaluateSwitch(values.get(identifiers.indexOf("IT")),i,lexemesByLine,classificationByLine);
				newSkips.forEach(skipList::add);
			}
			
			setTerminal(output);						//print current status of terminal
			setSymbolTable();							//print current status of symbol table
		}
		//reset output/terminal content
		output = "";
	}
	
	private void addMouseEventHandler() {
		file_btn.setOnMouseClicked(new EventHandler<MouseEvent>(){							//eventhandler for file chooser
			public void handle(MouseEvent e) {
				int line_number = 0;														//current line number
				hasSyntaxError = false;
				hasMultiLineComment = false;
				clearFileBtn();																//clear
				
				//file reading implementation
				file = fileChooser.showOpenDialog(stage);
				try {
					if(file != null) {		//if file not empty
						LinkedHashMap<String,String> map = new LinkedHashMap();
						BufferedReader br = new BufferedReader(new FileReader(file));
						String program = "", str;
						String out = "";
						
						while((str=br.readLine())!=null) {											//read each line of file
							line_number++;															//increment line_number for every line found
							program = program + line_number + " " + str + "\n";						//for printing the source code
							
							lexemeLine.clear();														//clear lexemeByLine
							classificationLine.clear();
//							System.out.println("reading line("+line_number+"):"+str);
							
							//checker if comments encountered to SKIP reading the line
							if(removeTabs(str).matches("\s*TLDR")) hasMultiLineComment = false;		//if TLDR, end of multiline comment
							if(isComment(removeTabs(str))) continue;								//ignore comment/s
							
							
							//if string can't be split (one word-line)
							if(isOneWord(removeTabs(str))) lexemeChecker(removeTabs(str));			
							//else, string can be split to words
							else {
								String[] words = str.split("\t| ");									//split each word by space delimiter  //TOKENIZE
//								System.out.println("words:"+Arrays.toString(words));
								String s = new String();
								
								if(words.length != 0 && isComment(words[0])) continue;				//ignore comments
								if(words.length != 0) s = words[0];									//assign the first word to string s
								
								for(int i=1;i<(words.length);i++) {									//start from the 2nd word
									//System.out.println("checking word:" + words[i]);
									if(s.matches("^(TLDR)$")) {										//end of multiline comment
										hasMultiLineComment = false;
										continue;
									}
									
									//ignore comment/s
									if(hasMultiLineComment) continue;								
									if(isComment(s)) break;		
									
									try{ s = lexemeChecker(s); }									//check token if lexeme
									catch(Exception e1) {											//if error occur during pattern checking, syntax error
										hasSyntaxError = true;
									}
									if(words.length != 1) {											//add next word to the string to compare
										if(isStringEmpty(s)) s = words[i];							//set new s
										else s = s + " " + words[i];								//append the next word to current s
									}
									if(i == (words.length-1)) {										//check if last lexeme
										s = lexemeChecker(s);										//tokenize last laxeme			
										if(!isStringEmpty(s)) hasSyntaxError = true;				//if there is an unclassified token left on string
									}
//									System.out.println("current s:"+s);		
								}
//								System.out.println();
							}
							
							if(!isStringEmpty(str)) hasHAI();										//checker if source code has HAI as code delimiter
							
							//if there is a syntax error, print error prompt
							if(hasSyntaxError) {												
								printError(line_number);											//print error to interface
								break;																//terminate reading file if there's an error
							}
							//update lexemesByLine
							if(!lexemeLine.isEmpty()) {
								line_numByLine.add(line_number);									//update arraylist of line_numbers	
								lexemesByLine.add((ArrayList<String>) lexemeLine.clone());
								classificationByLine.add((ArrayList<String>) classificationLine.clone());
							}
						}
						hasKTHXBYE(); 																//file must be delimited by a closing KTHXBYE
						
						if(!hasSyntaxError) {														//if no error, update interface
							setSourceCode(program);
							setLexemeTable();
						}else printError(line_number);												//if with error, print error	
					}else System.out.println("[!] No file selected.");								//print error if no file is selected
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		execute_btn.setOnMouseClicked(new EventHandler<MouseEvent>(){								//eventhandler for execute button
			public void handle(MouseEvent e) {
				//if has syntax error, cant be clicked
				if(!hasSyntaxError) {
					clearExecuteBtn();																	//clear for next click
					runProgram();																		//run the program by line
				}
			}
		});
	}
}
