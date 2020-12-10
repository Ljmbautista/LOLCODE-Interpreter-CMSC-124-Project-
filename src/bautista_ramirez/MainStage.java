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
	
	public String lexemeChecker(String s, int line_num) {
		boolean containsComma = false;
		
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
		else if(s.matches("^(MKAY)$")) {
			addLexeme(s,"Boolean Operation Delimiter");
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
			lexemeChecker(",",line_num);
		}
		return s;
	}	
	
	public boolean hasHAI(int line_num) {														//function for checking if has opening code delimiter
		//the code must be start with HAI
		if(lexemes.size() != 0 && lexemes.get(0).matches("HAI") == false) {
			hasSyntaxError = true;
			displayError(line_num,"Delimiter HAI not found");
			return false;
		}
		
		//has delimiter
		return true;
	}
	
	public boolean hasKTHXBYE(int line_num){													//function for checking if has closing code delimiter
		//the code must be delimited by KTHXBYE
		if(lexemes.get(lexemes.size()-1).matches("KTHXBYE") == false) {
			hasSyntaxError = true;
			displayError(line_num,"Delimiter KTHXBYE not found");
			return false;
		}
		
		//has delimiter
		return true;
	}	
	
	private void addLiteralSymbol(String identifier, String value) {						//function for adding symbols				
		identifiers.add(identifier);
		values.add(value);
	}
	
	private boolean isComment(String str) {													//function checker if comment
		//check if string is a comment
		if(str.matches("^(OBTW)$")) hasMultiLineComment = true;								
		if(str.matches("^(BTW)$") || str.matches("^(OBTW)$") || str.matches("^(TLDR)$")){
			return true;
		}
		//else not a comment
		else return false;
	}	
	
	private void setLexemeTable() {															//function for adding elements to Lexeme TableView
		//printing of lexemes lexemeTable
		ObservableList<Lexeme> lexTable = FXCollections.observableArrayList();
		for(int i=0;i<lexemes.size();i++) {
			lexTable.add(new Lexeme(lexemes.get(i), classification.get(i)));
		}
		lexemeTable.setItems(lexTable);		//add to tableview content	
	}	
	
	private void setSourceCode(String program) {											//function for adding elements to source code text area
		code.setText(program);
	}	
	
	private void setTerminal(String out) {													//function for adding elements to execute text area
		output.setText(out);
		System.out.println(out);
	}	
	
	private boolean isOneWord(String str) {													//function checker for one-word token
		if(!str.contains(" ")) return true;
		else return false;
	}	
	private String removeTabs(String str) {													//function for removing tabs from the source code
		if(str.contains("\\x{09}") || str.contains("\t")) {									
			str = str.replaceAll("[\\x{09}\t]+", "");
		}
		return str;
	}	
	
	private boolean isStringEmpty(String s) {												//function checker if string = ""
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
	
	private void clearFileBtn() {															//function for clearing file button
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
	private void setSymbolTable() {															//function for setting symbol table (GUI)
		
		if(identifiers.size()!=0) {
			//updating symbol table
			ObservableList<Identifier> symTable = FXCollections.observableArrayList();
			for(int i=0;i<identifiers.size();i++) {
				symTable.add(new Identifier(identifiers.get(i), values.get(i).toString()));
			}
			symbolTable.setItems(symTable);	//add to tableview content	
			
			
			//printing
			System.out.println("\n=======SYMBOL TABLE======");
			for(int i=0;i<identifiers.size();i++) {
				System.out.println(identifiers.get(i) + " = "+ values.get(i));
			}
			System.out.println("identifier count: "+identifiers.size());
		}
	}
	
	//function for arithmetic syntax checking using STACK
	private boolean ArithmeticSyntaxChecker(ArrayList<String> lexemeLine, ArrayList<String> classificationLine, int line_num) {
		Stack<String> stack = new Stack<String>();
		int exprCount = 0, opCount = 0, ANCount = 0;
		boolean hasNewExpression = false;					//flag if new expression found
		
		ArrayList<String> l = (ArrayList<String>) lexemeLine.clone();
		ArrayList<String> c = (ArrayList<String>) classificationLine.clone();
		
		//remove string delimiter from the arraylists
		for(int i=0;i<l.size();i++) {
			if(l.get(i).equals("\"")) {
				l.remove(i);
			}
			if(c.get(i).equals("String Delimiter")) {
				c.remove(i);
			}
		}
		
		//if only arith operator found
		if(l.size()==1){
			displayError(line_num,"Missing operand");
			return false;
		}
		
		//check every lexeme if they are valid lexeme for arithmetic op
		for(int i=0;i<c.size();i++) {
			//if new expression found on same line
			if(hasNewExpression) {
				displayError(line_num,"Invalid syntax for nesting of arithmetic operation");
				return false; 
			}
			
			//if lexeme is ARITH OP Keyword
			if(c.get(i).equals("Arithmetic Operation Keyword")) {
				stack.add(l.get(i));
				
				//check if nested expression
				if(i>0) exprCount++;
			}
			//if lexeme is AN
			else if(c.get(i).equals("AN Keyword")) {
				ANCount++;
			}
			//if lexeme is an operand (literal/varident)
			else if(isFloat(l.get(i)) || l.get(i).contains("WIN") || l.get(i).contains("FAIL") || 	
				isInteger(l.get(i)) || identifiers.contains(l.get(i))) {
				opCount++;
			}
			//if lexeme is not classified, syntax error
			else {
				displayError(line_num,"Invalid character found for Arithmetic Operation");
				return false;
			}
			
			//if there is more than one AN, syntax error
			if(ANCount >= 2) {
				displayError(line_num,"Unexpected AN keyword found");
				return false;
			}
			//if there is more  two consecutive operand, syntax error
			if(opCount >= 2 && ANCount == 0) {
				displayError(line_num,"Unexpected operand found");
				return false;
			}
			
			//if operands are two varident/literal or atleast one expr and atleast 1 operand and 1 AN (nested op)
			if((opCount == 2 && ANCount == 1) || (exprCount >= 1 && opCount >= 1 && ANCount == 1)) {
				if(!stack.isEmpty()) {
					//if the previous condition is true, there is a new expression found : error
					if(stack.size() == 1) hasNewExpression = true;
					
					//else, valid syntax for current expression
					stack.pop();
					
					//if there are 2 literal operand
					if((opCount == 2 && ANCount == 1)) opCount = 0;
					
					//if nested operand
					if(((exprCount >= 1 && opCount >= 1 && ANCount == 1))) {
						opCount--;
						exprCount--;
					}
					ANCount--;
				}
				//if stack is empty, syntax error
				else {
					displayError(line_num,"Invalid syntax for arithmetic operation");
					return false;
				}
			}
			System.out.println("expr:"+exprCount+"\nopcount:"+opCount+"\nancount:"+ANCount+"\n");
		}	
		
		//the conditions must be true to perform arithmetic operation
		if(stack.isEmpty() && opCount == 0 && ANCount == 0 && exprCount == 0) return true;
		else {
			displayError(line_num,"Missing operand");
			return false;
		}
	}
	
	private boolean isFloat(String s) {													//function for checking if float
		if(s.matches("^(-?\\d*\\.\\d+)$")) return true;
		else return false;
	}
	
	private boolean isInteger(String s) {												//function for checking if integer
		if(s.matches("^(-?\\d+)$")) return true;
		else return false;
	}
	
	//function for semantics of arithmetic
	private String evaluateArithmetic(ArrayList<String> lexemeLine, ArrayList<String> classificationLine) {
		Stack<Number> stack = new Stack<Number>();
		
		ArrayList<String> line = (ArrayList<String>) lexemeLine.clone();	
		ArrayList<String> line_class = (ArrayList<String>) classificationLine.clone();	
		
		Collections.reverse(line);			//get the reverse of each line
		Collections.reverse(line_class);	//get the reverse of each line_class
		
		for(int j=0;j<lexemeLine.size();j++) {		
			//if float/numbar literal
			if(isFloat(line.get(j))) {									
				stack.push(Float.parseFloat(line.get(j)));
			}
			//if integer/numbr literal
			else if(isInteger(line.get(j))) {							
				stack.push(Integer.parseInt(line.get(j)));
			}
			//extra credit
			//if TROOF literal
			else if(line.get(j).contains("WIN")) {						
				stack.push(1);			//WIN - typecast to 1
			}else if(line.get(j).contains("FAIL")) {					
				stack.push(0);			//FAIL - typecast to 0
			}
			//end-of-extra-credit
			
			//if operator keyword
			else if(line_class.get(j).equals("Arithmetic Operation Keyword")){							
				boolean isFloat = false;
				Number op1 = stack.pop();
				Number op2 = stack.pop();
				
				if(op1 instanceof Float || op2 instanceof Float) isFloat = true;						
				if(isFloat) {																		//if isFloat, the result must also be float/numbar
					
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
				}else {																				//if !isFloat, the result must be an integer/numbr
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
			}
			//if varident - get its value
			else if(identifiers.contains(line.get(j))) {													
				for(int k=0;k<identifiers.size();k++) {
					if(identifiers.get(k).equals(line.get(j))) {
						if(isFloat(values.get(k))) {								//float/numbar
							stack.push(Float.parseFloat(values.get(k)));
						}
						else if(isInteger(values.get(k))) {							//integer/numbr
							stack.push(Integer.parseInt(values.get(k)));
						}
						else if(values.get(k).contains("WIN")) {
							stack.push(1);
						}
						else if(values.get(k).contains("FAIL")) {
							stack.push(0);
						}
					}
				}
			}
		}
		//the last item on the stack is the result
		Number result = stack.pop();		
		stack.clear();		
		
		//update values of identifier
		for(int i=0;i<identifiers.size();i++) {
			if(identifiers.get(i).equals("IT")) {
				//update value of IT
				values.set(i, result.toString());
				break;
			}
		}
		return result.toString();
	}
	
	//function for var dec/init
	private void varDecInit(int i, ArrayList<String> lexeme, ArrayList<String> classification, int line_num) {
		System.out.println("CLASS: "+classification);
		if (classification.size() == 2) {
			if (classification.get(1).equals("Variable Identifier")) {
				addLiteralSymbol(lexeme.get(1).toString(),"NOOB");
			}
		}

		else if (classification.size() == 4) {
			if (classification.get(1).equals("Variable Identifier")) {
				if (classification.get(3).equals("Literal")) {
					addLiteralSymbol(lexeme.get(1).toString(),lexeme.get(3).toString());
				}
				if (classification.get(3).equals("Variable Identifier")) {
//					System.out.println("GOING HERE HERE HERE HERE: "+values.get(identifiers.indexOf(lexeme.get(3))));
					addLiteralSymbol(lexeme.get(1).toString(),values.get(identifiers.indexOf(lexeme.get(3))));
				}
			}
		}
		
		else if (classification.size() == 6) {
			if (classification.get(1).equals("Variable Identifier")) {
				if (classification.get(4).equals("String Literal")) 
					addLiteralSymbol(lexeme.get(1).toString(),lexeme.get(4).toString());
			}
		}
		
		else {
			if (classification.get(1).equals("Variable Identifier")) {
				String value = "";
				
				//cases for variable assignment
				ArrayList<String> l = new ArrayList<String>();
				ArrayList<String> c = new ArrayList<String>();
				
				for(String e : lexeme.subList(3, lexeme.size())) l.add(e);
				for(String e : classification.subList(3, lexeme.size())) c.add(e);
				System.out.println("line: "+ l.toString());
				
				if(classification.contains("Boolean Operation Keyword")) {
					if(BooleanSyntaxChecker(l,c,line_num)) {
						value = evaluateBoolean(l,c);
					}
				}
				else if(classification.contains("Comparison Operation Keyword")) {
					if(ComparisonSyntaxChecker(l,c,line_num)) {
						value = evaluateComparison(l,c);
					}
				}
				else if(classification.contains("Arithmetic Operation Keyword")) {
					if(ArithmeticSyntaxChecker(l,c,line_num)) {
						value = evaluateArithmetic(l,c);
					}
				}
				else if(lexeme.contains("SMOOSH")) {
					if(ConcatSyntaxChecker(l,c,line_num)) {
						value = evaluateConcat(l,c);
					}
				}
				else if(classification.contains("String Literal")) {
					value = lexeme.get(classification.indexOf("String Literal"));
				}
				else if(classification.contains("Literal")) {
					//look for index of literal
					value = lexeme.get(classification.indexOf("Literal"));
				}
				//if visible varident
				else if (classification.get(1).equals("Variable Identifier") || classification.get(1).equals("Implicit Variable")) {
					//get the value of the varident
					value = values.get(identifiers.indexOf(lexeme.get(1)));
				}
				addLiteralSymbol(lexeme.get(1),value);
				//set value of varident
				for(int j=0;j<identifiers.size();j++) {
					if(identifiers.get(j).equals(lexeme.get(1))) {
						values.set(j, value);
						break;
					}
				}
			}
		}
	}
	
	private String specialCharacterChecker(String s) {														//function for special characters in string/yarn
		//:) - newline
		if(s.contains(":)")) {
			s = s.replaceAll("\\:\\)", "\n");
		}
		//:> - tab
		if(s.contains(":>")) {
			s = s.replaceAll("\\:\\>", "\t");
		}
		//:> - g
		if(s.contains(":o")) {
			s = s.replaceAll("\\:o", "g");
		}
		//:" - "
		if(s.contains(":\"")) {
			s = s.replaceAll("\\:\"", "\"");
		}
		//:: - :
		if(s.contains("::")) {
			s = s.replaceAll("\\:\\:", ":");
		}
		
		return s;
	}
	
	private String printVisible(ArrayList<String> line, ArrayList<String> line_class) {							//function for printing to terminal
		String output = "";
		boolean shouldSkip = false;
		int index = 0;		//index of last part of operation
		
		for(int i=0;i<line.size();i++) {
			
			//check if should skip
			if(i==(index+1)) shouldSkip = false;
			if(shouldSkip) continue;
			
			//if visible varident
			if (line_class.get(i).equals("Variable Identifier") || 
				line_class.get(i).equals("Implicit Variable")) {
				//get the value of the varident
				output += specialCharacterChecker(values.get(identifiers.indexOf(line.get(i))).toString());
			}
			//if visible literal
			else if (line_class.get(i).contains("Literal")) {
				output += specialCharacterChecker(line.get(i).toString());
			}
			//if boolean expression
			else if(line_class.get(i).contains("Boolean Operation Keyword")) {
				ArrayList<String> temp = new ArrayList<String>();
				ArrayList<String> temp_class = new ArrayList<String>();
				
				//look for index of last AN-<literal> pair
				index = 0;
				if(line.contains("AN")) {
					for(int j=i+2;j<line.size();j++) {
						try {
							System.out.println(line.get(j)+"::"+line.get(j+1));
							if(line_class.get(j).equals("AN Keyword")) {
								index = j+1;
								shouldSkip = true;
							}
						}catch(Exception e) {}
					}
					for(int j=i;j<=index;j++) {
						//add the only needed tokens for boolean
						temp.add(line.get(j));
						temp_class.add(line_class.get(j));
					}
				}else {
					for(int j=i;j<line.size();j++) {
						//add the only needed tokens for boolean
						temp.add(line.get(j));
						temp_class.add(line_class.get(j));
					}
					shouldSkip = true;
				}
				
				
				output += evaluateBoolean(temp,temp_class);
			}
			//if comparison expression
			else if(line_class.get(i).contains("Comparison Operation Keyword")) {
				ArrayList<String> temp = new ArrayList<String>();
				ArrayList<String> temp_class = new ArrayList<String>();
				
				//look for index of last AN-<literal> pair
				index = 0;
				for(int j=i+2;j<line.size();j++) {
					try {
						System.out.println(line.get(j)+"::"+line.get(j+1));
						if(line_class.get(j).equals("AN Keyword")) {
							index = j+1;
							shouldSkip = true;
						}
					}catch(Exception e) {}
				}
				
				for(int j=i;j<=index;j++) {
					//add the only needed tokens for comparison
					temp.add(line.get(j));
					temp_class.add(line_class.get(j));
				}
				output += evaluateComparison(temp, temp_class);
			}
			//if arithmetic expression
			else if(line_class.get(i).contains("Arithmetic Operation Keyword")) {
				ArrayList<String> temp = new ArrayList<String>();
				ArrayList<String> temp_class = new ArrayList<String>();
				
				//look for index of last AN-<literal> pair
				index = 0;
				for(int j=i+2;j<line.size();j++) {
					try {
						System.out.println(line.get(j)+"::"+line.get(j+1));
						if(line_class.get(j).equals("AN Keyword")) {
							index = j+1;
							shouldSkip = true;
						}
					}catch(Exception e) {}
				}
				
				//add to array that will be used for arithmetic operation
				for(int j=i;j<=index;j++) {
					//add the only needed tokens for arithmetic
					temp.add(line.get(j));
					temp_class.add(line_class.get(j));
				}
				//evaluate arithop
				output += evaluateArithmetic(temp,temp_class);
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
	
	//function for boolean syntax checking
	private boolean BooleanSyntaxChecker(ArrayList<String> lexemeLine, ArrayList<String> classificationLine, int line_num) {

		ArrayList<String> l = (ArrayList<String>) lexemeLine.clone();
		ArrayList<String> c = (ArrayList<String>) classificationLine.clone();
		
		//remove string delimiter from the arraylists
		for(int i=0;i<l.size();i++) {
			if(l.get(i).equals("\"")) {
				l.remove(i);
			}
			if(c.get(i).equals("String Delimiter")) {
				c.remove(i);
			}
		}
		
		//if NOT keyword (NOT <op>)
		if(l.get(0).equals("NOT")){
			//line should only have 2 lexemes NOT <op>
			if(l.size()>2) {
				displayError(line_num, "Unexepcted character found");
				return false;
			}
			
			if(c.get(1).contains("Literal") || c.get(1).contains("Identifier")) {
				//check if operand is of valid type for TROOF operation
				if(classificationLine.get(1).contains("Identifier")) {
					//if varident
					if(!(values.get(identifiers.indexOf(l.get(1))).equals("WIN") ||
							values.get(identifiers.indexOf(l.get(1))).equals("FAIL") ||
							values.get(identifiers.indexOf(c.get(1))).contains("Literal")
							)) {
						displayError(line_num, "Invalid character found");
						return false;
					}
				}else {
					//if literal
					if(!(l.get(1).contains("WIN") || 
						l.get(1).contains("FAIL") || 
						c.get(1).contains("Literal"))) {
						displayError(line_num, "Invalid character found");
						return false;
					}
				}
			}
			//if no syntax error, return true
			return true;
		}
		else if(l.contains("ALL OF") || l.contains("ANY OF")) {
			//infinite arity syntax checker
			System.out.println(l.toString());
			//there should only be one instance of ALL OF/ANY OF
			if(Collections.frequency(l, "ALL OF")>1) {
				displayError(line_num, "Multiple 'ALL OF' found");
				return false;
			}
			if(Collections.frequency(l, "ANY OF")>1) {
				displayError(line_num, "Multiple 'ANY OF' found");
				return false;
			}
			
			//ALL OF/ANY OF should be the first lexeme
			if(!l.isEmpty()) {
				if(!(l.get(0).equals("ALL OF") || l.get(0).equals("ANY OF"))) {
					displayError(line_num, "No Boolean operator (ALL OF/ANY OF) on first token");
					return false;
				}
			}
			
			//there should be a MKAY delimiter
			if(!l.get(l.size()-1).equals("MKAY")) {
				displayError(line_num, "No 'MKAY' found");
				return false;
			}
			
			//check every lexeme
			for(int i=1;i<(l.size()-1);i++) {
				//if last element
				if(i==(l.size()-2)) {
					//last element should not be AN or OPERATOR
					if(l.get(i).equals("AN") || c.get(i).contains("Operation Keyword")) {
						displayError(line_num, "Missing operand");
						return false;
					}
				}
				//if NOT
				else if(l.get(i).contains("NOT")) {
					//must be followed by a literal / operator
					if(!(c.get(i+1).contains("Literal") || identifiers.contains(l.get(i+1)) || c.get(i+1).contains("Operation Keyword"))) {
						displayError(line_num, "Missing operand");
						return false;
					}
				}
				//if literal
				else if(c.get(i).contains("Literal") ||
						identifiers.contains(l.get(i))) {
					//a literal should be follwed by AN
					if(!l.get(i+1).equals("AN")) {
						displayError(line_num, "Missing AN Keyword");
						return false;
					}
				}
				//if operator
				else if(c.get(i).contains("Operation Keyword")) {
					//an operator must be followed by a literal/operator
					if(!(c.get(i+1).contains("Literal") || identifiers.contains(l.get(i+1)) || c.get(i+1).contains("Operation Keyword"))) {
						displayError(line_num, "Missing operand");
						return false;
					}
					//if an operator is found, there should be at least 3 lexeme after
					if(((l.size()-2)-i) < 3) {
						displayError(line_num, "Missing operand");
						return false;
					}
				}
				//if AN
				else if(c.get(i).contains("AN")) {
					//must be followed by a literal / operator
					if(!(c.get(i+1).contains("Literal") || identifiers.contains(l.get(i+1)) || c.get(i+1).contains("Operation Keyword"))) {
						displayError(line_num, "Missing operand");
						return false;
					}
				}
			}
			
			//else no error, return true
			return true;
		}
		else {
			//nested boolean op
			
			Stack<String> stack = new Stack<String>();
			int exprCount = 0, opCount = 0, ANCount = 0;
			boolean hasNewExpression = false;
			
			for(int i=0;i<c.size();i++) {
				System.out.println("lexeme:"+l.get(i));
				
				//if new expression found on same line
				if(hasNewExpression) {
					displayError(line_num,"Invalid syntax for nesting of boolean operation");
					return false; 
				}
				
				//if lexeme is NOT 
				if(l.get(i).contains("NOT")) {
					try {
						//check next lexeme if valid
						if(!(c.get(i+1).contains("Literal") || identifiers.contains(l.get(i+1)) || c.get(i+1).contains("Operation Keyword"))) {
							displayError(line_num, "Missing operand");
							return false;
						}
					}catch(Exception e) {
						displayError(line_num, "Missing operand");
						return false;
					}
				}
				//if lexeme is ARITH OP Keyword
				else if(c.get(i).contains("Operation Keyword")) {
					stack.add(c.get(i));
					
					//check if nested expression
					if(i>0) exprCount++;
				}
				//if lexeme is AN
				else if(c.get(i).equals("AN Keyword")) {
					ANCount++;
				}
				//if lexeme is an operand
				else if(c.get(i).contains("Literal") ||
						identifiers.contains(l.get(i))) {
					opCount++;
				}
				//if lexeme is not classified, syntax error
				else {
					displayError(line_num,"Invalid character for boolean operation");
					return false;
				}
				
				//if there is more than one AN, syntax error
				if(ANCount >= 2) {
					displayError(line_num,"Unexpected AN Keyword found");
					return false;
				}
				//if there is more than two operand, syntax error
				if(opCount >= 2 && ANCount == 0) {
					displayError(line_num,"Unexpected operand found");
					return false;
				}
				
				//if operands are two varident/literal or atleast one expr and atleast 1 operand and 1 AN
				if((opCount == 2 && ANCount == 1) || (exprCount >= 1 && opCount >= 1 && ANCount == 1)) {
					if(!stack.isEmpty()) {
						if(stack.size() == 1) hasNewExpression = true;
						stack.pop();
						
						//if there are 2 operand literal
						if((opCount == 2 && ANCount == 1)) opCount = 0;
						
						//if nested operand
						if(((exprCount >= 1 && opCount >= 1 && ANCount == 1))) {
							opCount--;
							exprCount--;
						}
						ANCount--;
					}
					//if stack is empty, syntax error
					else {
						displayError(line_num,"Invalid syntax for boolean operation");
						return false;
					}
				}
				System.out.println("expr:"+exprCount+"\nopcount:"+opCount+"\nancount:"+ANCount+"\n");
			}	
			//the conditions must be true to perform arithmetic operation
			if(stack.isEmpty() && opCount == 0 && ANCount == 0 && exprCount == 0) return true;
			else {
				displayError(line_num,"Missing operand");
				return false;
			}
		}
	}
	
	private String evaluateBoolean(ArrayList<String> lexemeLine, ArrayList<String> classificationLine) {			//function for boolean operation
		System.out.print("line: "+ lexemeLine.toString());
		Stack<Boolean> stack = new Stack<Boolean>();
		
		ArrayList<String> line = (ArrayList<String>) lexemeLine.clone();										//get the reverse of each line
		Collections.reverse(line);
		ArrayList<String> line_class = (ArrayList<String>) classificationLine.clone();							//get the reverse of each line_class
		Collections.reverse(line_class);
		
		
		//check every lexeme if valid for boolean op
		for(int j=0;j<line.size();j++) {
			//if WIN[true]
			if(line.get(j).equals("WIN")) {
				stack.push(true);
			}
			//if FAIL[false]
			else if(line.get(j).equals("FAIL")) {
				stack.push(false);
			}
			//if string/numbr/numbar typecast to WIN
			else if(line_class.get(j).contains("Literal")) {
				stack.push(true);
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
			//if varident, get the value
			else if(identifiers.contains(line.get(j))) {				
				for(int k=0;k<identifiers.size();k++) {
					if(identifiers.get(k).equals(line.get(j))) {
						if(values.get(k).equals("FAIL")) {
							stack.push(false);
						}else stack.push(true);
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
			//if COMPARISON
			else if(line_class.get(j).equals("Comparison Operation Keyword")) {
				boolean op1 = stack.pop();
				boolean op2 = stack.pop();
	
				ArrayList<String> l = new ArrayList<String>();
				ArrayList<String> c = new ArrayList<String>();
				
				l.add(line_class.get(j));
				c.add("Comparison Operation Keyword");
				if(op1 == true) l.add("WIN");
				else l.add("FAIL");
				if(op2 == true) l.add("WIN");
				else l.add("FAIL");
				c.add("Literal");
				c.add("Literal");
				
				//evaluate comparison op
				String value = evaluateComparison(l,c);
				
				//check returned value
				if(value.equals("WIN")) stack.push(true);
				else stack.push(false);
			}
			System.out.println("stack:"+stack.toString());
		}
		//last element of stack is result
		String result = "";
		if(stack.peek() == false) result = "FAIL";
		else result = "WIN";
		
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
	
	private String checkDataType(String var, String s) {															//function that checks datatype of a varident
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
	
	//comparison syntax checker
	private boolean ComparisonSyntaxChecker(ArrayList<String> lexemeLine, ArrayList<String> classificationLine, int line_num) {
		Stack<String> stack = new Stack<String>();
		int exprCount = 0, opCount = 0, ANCount = 0;
		boolean hasNewExpression = false;
		
		ArrayList<String> l = (ArrayList<String>) lexemeLine.clone();
		ArrayList<String> c = (ArrayList<String>) classificationLine.clone();
		
		
		//remove string delimiter from the arraylists
		for(int i=0;i<l.size();i++) {
			if(l.get(i).equals("\"")) {
				l.remove(i);
			}
			if(c.get(i).equals("String Delimiter")) {
				c.remove(i);
			}
		}
		
		//check lexeme if valid for comparison op
		for(int i=0;i<c.size();i++) {
			System.out.println("lexeme:"+l.get(i));
			
			//if new expression found on same line
			if(hasNewExpression) {
				displayError(line_num,"Invalid syntax for nesting of comparison operation");
				return false; 
			}
			
			//if lexeme is NOT 
			if(l.get(i).contains("NOT")) {
				try {
					//check next lexeme if valid
					if(!(c.get(i+1).contains("Literal") || identifiers.contains(l.get(i+1)) || c.get(i+1).contains("Operation Keyword"))) {
						displayError(line_num, "Missing operand");
						return false;
					}
				}catch(Exception e) {
					displayError(line_num, "Missing operand");
					return false;
				}
			}
			//if lexeme is ARITH OP Keyword
			else if(c.get(i).contains("Operation Keyword")) {
				stack.add(c.get(i));
				
				//check if nested expression
				if(i>0) exprCount++;
			}
			//if lexeme is AN
			else if(c.get(i).equals("AN Keyword")) {
				ANCount++;
			}
			//if lexeme is an operand
			else if(c.get(i).contains("Literal") ||
					identifiers.contains(l.get(i))) {
				opCount++;
			}
			//if lexeme is not classified, syntax error
			else {
				displayError(line_num,"Invalid character for comparison operation found");
				return false;
			}
			
			//if there is more than one AN, syntax error
			if(ANCount >= 2) {
				displayError(line_num, "Missing operand");
				return false;
			}
			//if there is more than two operand, syntax error
			if(opCount >= 2 && ANCount == 0) {
				displayError(line_num, "Missing AN keyword");
				return false;
			}
			
			//if operands are two varident/literal or atleast one expr and atleast 1 operand and 1 AN
			if((opCount == 2 && ANCount == 1) || (exprCount >= 1 && opCount >= 1 && ANCount == 1)) {
				if(!stack.isEmpty()) {
					if(stack.size() == 1) hasNewExpression = true;
					stack.pop();
					
					//if there are 2 operand literal
					if((opCount == 2 && ANCount == 1)) opCount = 0;
					
					//if nested operand
					if(((exprCount >= 1 && opCount >= 1 && ANCount == 1))) {
						opCount--;
						exprCount--;
					}
					ANCount--;
				}
				//if stack is empty, syntax error
				else {
					displayError(line_num,"Invalid syntax for comparison operation");
					return false;
				}
			}
			System.out.println("expr:"+exprCount+"\nopcount:"+opCount+"\nancount:"+ANCount+"\n");
		}	
		//the conditions must be true to perform arithmetic operation
		if(stack.isEmpty() && opCount == 0 && ANCount == 0 && exprCount == 0) return true;
		else {
			displayError(line_num, "Missing operand");
			return false;
		}
	}
	
	//function for semantics of comparison
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
			else if(line_class.get(j).equals("String Literal")) {										//string/yarn
				stack.push(line.get(j));
				datatype.push("string");
			}
			else if(isFloat(line.get(j))) {																//float/numbar
				stack.push(line.get(j));
				datatype.push("float");
			}else if(isInteger(line.get(j))) {															//integer/numbr
				stack.push(line.get(j));
				datatype.push("integer");
			}
			else if(identifiers.contains(line.get(j))) {												//if varident
				for(int k=0;k<identifiers.size();k++) {
					if(identifiers.get(k).equals(line.get(j))) {
						//check datatype of value
						String temp = checkDataType(line.get(j),values.get(k));
						
						if(temp.equals("String Literal")) {												//string/yarn
							stack.push(values.get(identifiers.indexOf(line.get(j))));
							datatype.push("string");
						}else {
							//cases for values of varident
							if(isFloat(values.get(k))) {												//float/numbar
								stack.push(values.get(identifiers.indexOf(line.get(j))));
								datatype.push("float");
							}else if(isInteger(values.get(k))) {										//integer/numbr
								stack.push(values.get(identifiers.indexOf(line.get(j))));
								datatype.push("integer");
							}else if(values.get(k).equals("WIN")) {										//WIN
								stack.push(values.get(identifiers.indexOf(line.get(j))));
								datatype.push("boolean");
							}else if(values.get(k).equals("FAIL")) {									//FAIL
								stack.push(values.get(identifiers.indexOf(line.get(j))));
								datatype.push("boolean");
							}else {
								//else, assume that string
								stack.push(values.get(identifiers.indexOf(line.get(j))));
								datatype.push("string");
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
						Float temp = Float.parseFloat(op);
						
						stack.push(temp.toString());
						datatype.push("float");
					}else {																			//if !isFloat, the result must be an integer/numbr
						String op = evaluateArithmetic(l,c);
						Integer temp = Integer.parseInt(op);
						
						stack.push(temp.toString());
						datatype.push("integer");
					}
				}else {	//if not same datatype, FAIL
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
			//if a NOT keyword
			else if(line.get(j).contains("NOT")) {
				String op1 = stack.pop();
				datatype.pop();
				
				if(op1.equals("WIN")) stack.push("FAIL");
				else stack.push("WIN");
				datatype.push("boolean");
			}
			//if boolean operator
			else if(line_class.get(j).equals("Boolean Operation Keyword")) {
				ArrayList<String> l = new ArrayList<String>();
				ArrayList<String> c = new ArrayList<String>();
				
				l.add(line.get(j));
				c.add(line_class.get(j));
				
				//if infinite arity
				if(line_class.get(j).equals("ANY OF") || line_class.get(j).equals("ALL OF")) {
					for(int i=0;i<stack.size();i++) {
						l.add(stack.pop());
						datatype.pop();
						c.add("Literal");
					}
				}else {
					//add only two elements
					datatype.pop();
					datatype.pop();
					l.add(stack.pop());
					l.add(stack.pop());
					c.add("Literal");
					c.add("Literal");
				}
				//update stack
				stack.push(evaluateBoolean(l,c));
				datatype.push("boolean");
			}
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
	
	//function for variable assignment
	private void variableAssignment(ArrayList<String> lexemeLine, ArrayList<String> classificationLine, int line_num) {
		String value = "";
		//System.out.print("line: "+ lexemeLine.toString());
		//cases for variable assignment
		ArrayList<String> l = new ArrayList<String>();
		ArrayList<String> c = new ArrayList<String>();
		
		//remove the unecessary tokens
		for(String e : lexemeLine.subList(2, lexemeLine.size())) l.add(e);
		for(String e : classificationLine.subList(2, lexemeLine.size())) c.add(e);
		
		//if line has boolean
		if(classificationLine.contains("Boolean Operation Keyword")) {
			if(BooleanSyntaxChecker(l,c,line_num)) {
				value = evaluateBoolean(l,c);
			}
		}
		//if line has comparison
		else if(classificationLine.contains("Comparison Operation Keyword")) {
			if(ComparisonSyntaxChecker(l,c,line_num)) {
				value = evaluateComparison(l,c);
			}
		}
		//if line has arith
		else if(classificationLine.contains("Arithmetic Operation Keyword")) {
			if(ArithmeticSyntaxChecker(l,c,line_num)) {
				value = evaluateArithmetic(l,c);
			}
		}
		//if line has smoosh
		else if(lexemeLine.contains("SMOOSH")) {
			if(ConcatSyntaxChecker(l,c,line_num)) {
				value = evaluateConcat(l,c);
			}
		}
		//if string literal
		else if(classificationLine.contains("String Literal")) {
			value = lexemeLine.get(classificationLine.indexOf("String Literal"));
		}
		//if other literal
		else if(classificationLine.contains("Literal")) {
			//look for index of literal
			value = lexemeLine.get(classificationLine.indexOf("Literal"));
		}
		//if varident
		else if (classificationLine.get(2).equals("Variable Identifier") || classificationLine.get(2).equals("Implicit Variable")) {
			//get the value of the varident
			value = values.get(identifiers.indexOf(lexemeLine.get(2)));
		}
		
		//set value of varident
		for(int i=0;i<identifiers.size();i++) {
			if(identifiers.get(i).equals(lexemeLine.get(0))) {
				values.set(i, value);
				break;
			}
		}
	}
	
	//function for checking syntax of variable declaration
	private boolean variableAssignmentSyntaxChecker(ArrayList<String> lexemeLine, ArrayList<String> classificationLine, int line_num) {	
		
		if(lexemeLine.size() <= 2) {		//the size of the line must at least be 3
			displayError(line_num,"Missing operand");
			return false;																			
		}
		if(!identifiers.contains(lexemeLine.get(0))) {		//the variable used must be declared/IT
			displayError(line_num,"Undeclared varident found");
			return false;									
		}
		if(!classificationLine.get(1).equals("Assignment Keyword")) {
			displayError(line_num,"Invalid syntax for Assignment Operation");
			return false;					//if no R keyword
		}
		
		//else no syntax error
		return true;
	}
	
	private boolean GimmehChecker(ArrayList<String> lexemeLine, ArrayList<String> classificationLine, int line_num) {			//checker for invalid IO syntax

		if(lexemeLine.size() == 1 || lexemeLine.size()>2) { //the gimmeh statement should only be GIMMEH <varident>
			displayError(line_num,"Unexpected character found");
			return false;				
		}
		if(!identifiers.contains(lexemeLine.get(1))) {		//if variable not declared/IT
			displayError(line_num,"Undeclared varident found");
			return false;												
		}
		
		//else no syntax error
		return true;
	}	
	
	private void printError(int line_number) {																				//function for printing line number error
		String out = "";
		out = "$lci "+ file.getName()+"\n";
		out += "[ ! ] Error in line "+line_number;
		
		hasSyntaxError = true;																		
		setTerminal(out);	//print error to interface
	}
	
	private void displayError(int line_number, String err) {
		String out = "";
		out = "$lci "+ file.getName()+"\n";
		out += "[ ! ] Error in line "+line_number+": "+ err;
		
		hasSyntaxError = true;																		
		setTerminal(out);	//print error to interface
	}
	
	//function for if-else semantics
	private ArrayList<Integer> evaluateIfElse(String IT, int i, ArrayList<ArrayList<String>> lexeme, ArrayList<ArrayList<String>> classification){
		ArrayList<Integer> skip = new ArrayList<>();
		int h, current=0, buffer = 0, decrement = 2;
		if (IT.equals("WIN")) {
			for (h=i+1; h<classification.size(); h++) {
				System.out.println("Buffer: "+buffer);
				System.out.println("Lexeem: "+lexeme.get(h));
				current = h;
				if (classification.get(h).contains("O RLY Keyword")) buffer+=2;
				else if (classification.get(h).contains("NO WAI Keyword")) {
					if (buffer == 0) {
						System.out.println("BREAKED");
						break;
					}
					else {
						decrement = 1;
						buffer-=decrement;
					}
				}
				else if (classification.get(h).contains("If-Then Delimiter")) {
					if (buffer == 0) {
						System.out.println("BREAKED");
						break;
					}
					else buffer-=decrement;
				}
			}
			buffer = decrement = 0;
			for (h=current; h<classification.size(); h++) {
				if (classification.get(h).contains("O RLY Keyword")) buffer+=2;
				else if (classification.get(h).contains("If-Then Delimiter")) {
					if (buffer == 0) break;
					else {
						decrement = 1;
						buffer-=decrement;
					}		
				}
				skip.add(h);
			}
		} else {
			for (h=i+1; h<classification.size(); h++) {				
				if (classification.get(h).contains("O RLY Keyword")) buffer+=2;
				else if (classification.get(h).contains("NO WAI Keyword")) {
					if (buffer == 0) break;
					else {
						decrement-=1;
						buffer-=decrement;
					}
				} 
				else if (classification.get(h).contains("If-Then Delimiter")) {
					if (buffer == 0) break;
					else buffer-=decrement;
				}
				skip.add(h);
			}
		}
		return skip;
	}
	
	//function for switch case semantics
	private ArrayList<Integer> evaluateSwitch(String IT, int i, ArrayList<ArrayList<String>> lexeme, ArrayList<ArrayList<String>> classification) {
		System.out.println("I GOT IN.");
		ArrayList<Integer> skip = new ArrayList<>();
		int h, buffer = 0;
		boolean skipFlag = true, enteredFlag = false;
		for (h=i+1;h<classification.size();h++) {
			if (classification.get(h).contains("OMG Keyword")) {
				if (lexeme.get(h).size() == 2) {					
					if (lexeme.get(h).get(1).equals(IT)) {
						skipFlag = false;
						enteredFlag = true;
					}
				}
				else if (lexeme.get(h).size() == 4) {
					if (lexeme.get(h).get(2).equals(IT)) {
						skipFlag = false;
						enteredFlag = true;
					}
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
	
	private String evaluateConcat(ArrayList<String> lexemeLine, ArrayList<String> classificationLine) {			//function for string concatenation
		String result = "";
		
		for(int i=0;i<lexemeLine.size();i++) {
			//if literal
			if(classificationLine.get(i).contains("Literal")) {
				result += lexemeLine.get(i);
			}
			//if varident
			else if(classificationLine.get(i).contains("Identifier")) {
				result += values.get(identifiers.indexOf(lexemeLine.get(i)));
			}
			//else ignore
		}
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
	
	private boolean varDecChecker(ArrayList<String> lexemeLine, ArrayList<String> classificationLine) {
		System.out.println(identifiers);
		if (identifiers.contains(lexemeLine.get(1))) return false;
		return true;
	}
	
	private boolean IfElseSyntaxChecker(int i, ArrayList<ArrayList<String>> lexeme, ArrayList<ArrayList<String>> classification) {
		boolean unclosed = true;
		int buffer = 0;
		
		for (int j=i+1; j<lexeme.size(); j++) {
			if (classification.get(j).contains("O RLY Keyword")) {
				System.out.println("Encountered an O RLY");
				buffer+=1;
			}
			else if (classification.get(j).contains("If-Then Delimiter")) {
				if (buffer == 0) {
					unclosed = false;
					break;
				}
				else {
					System.out.println("Encountered an OIC");
					buffer-=1;
				}
			}
		}
		
		
		
		if (unclosed) {
			displayError(line_numByLine.get(i),"O RLY not closed");
			return false;
		}
		
		return true;
	}
	
	private boolean SwitchSyntaxChecker(int i, ArrayList<ArrayList<String>> lexeme, ArrayList<ArrayList<String>> classification) {
		boolean unclosed = true, noLiteral = false;
		int buffer = 0;
		
		for (int j=i+1; j<lexeme.size(); j++) {
			if (classification.get(j).contains("Switch-Case Delimiter")) {
				System.out.println("Encountered a WTF");
				buffer+=1;
			}
			else if (classification.get(j).contains("If-Then Delimiter")) {
				if (classification.get(j).size() == 1) {
					displayError(line_numByLine.get(j),"No case literal");
					noLiteral = true;
				}
				if (buffer == 0) {
					unclosed = false;
					break;
				}
				else {
					System.out.println("Encountered an OIC");
					buffer-=1;
				}
			}
		}
		if (noLiteral) {
			return false;		
		}
		if (unclosed) {
			displayError(line_numByLine.get(i),"WTF? not closed");
			return false;
		}
		
		return true;
	}
	
	//syntax checker of concat
	private boolean ConcatSyntaxChecker(ArrayList<String> lexemeLine, ArrayList<String> classificationLine, int line_num) {		
		@SuppressWarnings("unchecked")
		ArrayList<String> l = (ArrayList<String>) lexemeLine.clone();
		@SuppressWarnings("unchecked")
		ArrayList<String> c = (ArrayList<String>) classificationLine.clone();
		
		
		//remove string delimiter from the arraylists
		for(int i=0;i<l.size();i++) {
			if(l.get(i).equals("\"")) {
				l.remove(i);
			}
			if(c.get(i).equals("String Delimiter")) {
				c.remove(i);
			}
		}
		
		//the last element should be a literal/varident
		if(!(c.get(l.size()-1).contains("Literal") || 
			c.get(l.size()-1).contains("Identifier") || 
			c.get(l.size()-1).contains("String Delimiter"))) {
			displayError(line_num, "Missing operand");
			return false;
		}
		//the line should at least have 4 lexemes
		if(l.size()<4) {
			displayError(line_num, "Missing operand");
			return false;
		}
		//should only have one SMOOSH which should be at the start
		if(Collections.frequency(l, "SMOOSH") > 1 || !l.get(0).equals("SMOOSH")) {
			displayError(line_num, "Multiple 'SMOOSH' found");
			return false;
		}
		
		
		//check literal-AN pair
		for(int i=1;i<l.size();i++) {
			if(c.get(i).contains("AN Keyword")) {
				try{
					boolean op1 = false;
					boolean op2 = false;
					
					//check if valid for concatenation
					if(c.get(i-1).contains("Literal") || c.get(i-1).contains("Identifier")) {
						op1 = true;
					}
					if(c.get(i+1).contains("Literal") || c.get(i+1).contains("Identifier")) {
						op2 = true;
					}
					if(op1==false || op2==false) {
						displayError(line_num, "Invalid character for concatenation");
						return false; 
					}
				}
				catch(Exception e) {
					displayError(line_num, "Invalid character for concatenation");
					return false;
				}
			}
		}
		
		//if no error, valid syntax
		return true;
	}
	
	private void instantiateIT() {																	//function that instantiates IT 
		//instantiate IT implicit variable											
		identifiers.add("IT");
		values.add("NOOB");
	}
	
	private void runProgram() {																		//function for execute button
		String IT = "";
		String output = "";
		ArrayList<Integer> skipList = new ArrayList<>();
		ArrayList<Integer> newSkips = new ArrayList<>();
		
		//instantiate IT implicit variable		
		instantiateIT();
		
		//check every line statement/s
		for(int i=0;i<lexemesByLine.size();i++) {
			//skip line of code if line number is in skipList
			if (!skipList.isEmpty())
				if (skipList.contains(i)) {
					skipList.remove((Object) i);
					continue;
				}
			ArrayList<String> line = lexemesByLine.get(i);
			ArrayList<String> line_class = classificationByLine.get(i);
			
			//******cases for every line*******//
			//if line has var dec/init
			if (line_class.contains("Variable Declaration")) {
				if (varDecChecker(line, line_class)) {					
					varDecInit(i, line, line_class,line_numByLine.get(i));
				} else break;
			}			
			//if line has var assignment
			else if(line_class.contains("Assignment Keyword")) {												
				if(variableAssignmentSyntaxChecker(line, line_class,line_numByLine.get(i))) {	//check if valid syntax
					variableAssignment(line, line_class,line_numByLine.get(i));						
				}else break;																	//if invalid syntax, print error
			}
			//if line has gimmeh
			else if(line_class.contains("Input Keyword")) {
				if(GimmehChecker(line, line_class,line_numByLine.get(i))) {						//check if valid syntax
					getGimmeh(line, line_class);						
				}else break;																	//if invalid syntax, print error
			}
			//if line has visible
			else if(line_class.contains("Output Keyword")) {
				output += printVisible(line,line_class);
			}
			//if line has comparison
			else if(line_class.contains("Comparison Operation Keyword")) {						//check if valid syntax
				if(ComparisonSyntaxChecker(line,line_class,line_numByLine.get(i))) {
					IT = evaluateComparison(line,line_class);			
				}else break;																	//if invalid syntax, print error
			}
			//if line has boolean
			else if(line_class.contains("Boolean Operation Keyword")) {		
				if(BooleanSyntaxChecker(line,line_class,line_numByLine.get(i))) {				//check if valid syntax
						IT = evaluateBoolean(line,line_class);
				}else break;																	//if invalid syntax, print error
			}
			//if line has arithmetic
			else if(line_class.contains("Arithmetic Operation Keyword")) {
				if(ArithmeticSyntaxChecker(line,line_class,line_numByLine.get(i))) {			//check if valid syntax
					IT = evaluateArithmetic(line,line_class);
				}else break;																	//if invalid syntax, print error
			}
			//if line has if-else
			else if(line_class.contains("O RLY Keyword")) {
				if (IfElseSyntaxChecker(i,lexemesByLine,classificationByLine)) {					
					newSkips = evaluateIfElse(IT,i,lexemesByLine,classificationByLine);
					newSkips.forEach(skipList::add);
				} else break;
			}
			//if line has switch case
			else if(line_class.contains("Switch-Case Delimiter")) {
				if (SwitchSyntaxChecker(i,lexemesByLine,classificationByLine)) {
					newSkips = evaluateSwitch(values.get(identifiers.indexOf("IT")),i,lexemesByLine,classificationByLine);
					newSkips.forEach(skipList::add);
				} else break;
			}
			//if line has SMOOSH (concat)
			else if(line_class.contains("Concatenation Operation Keyword")) {
				if(ConcatSyntaxChecker(line,line_class,line_numByLine.get(i))) {
					IT = evaluateConcat(line,line_class);
				}else break;
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
							
							//checker if comments encountered to SKIP reading the line
							if(removeTabs(str).matches("\s*TLDR")) hasMultiLineComment = false;		//if TLDR, end of multiline comment
							if(isComment(removeTabs(str))) continue;								//ignore comment/s
							
							
							//if string can't be split (one word-line)
							if(isOneWord(removeTabs(str))) lexemeChecker(removeTabs(str),line_number);			
							//else, string can be split to words
							else {
								String[] words = str.split("\t| ");									//split each word by space delimiter  //TOKENIZE
								String s = new String();
								
								if(words.length != 0 && isComment(words[0])) continue;				//ignore comments
								if(words.length != 0) s = words[0];									//assign the first word to string s
								
								for(int i=1;i<(words.length);i++) {									//start from the 2nd word
									if(s.matches("^(TLDR)$")) {										//end of multiline comment
										hasMultiLineComment = false;
										continue;
									}
									
									//ignore comment/s
									if(hasMultiLineComment) continue;								
									if(isComment(s)) break;		
									
									try{ s = lexemeChecker(s,line_number); }						//check token if lexeme
									catch(Exception e1) {											//if error occur during pattern checking, syntax error
										hasSyntaxError = true;
										displayError(line_number,"Unclassified token found");
									}
									if(words.length != 1) {											//add next word to the string to compare
										if(isStringEmpty(s)) s = words[i];							//set new s
										else s = s + " " + words[i];								//append the next word to current s
									}
									if(i == (words.length-1)) {										//check if last lexeme
										s = lexemeChecker(s,line_number);							//tokenize last laxeme			
										if(!isStringEmpty(s)) {
											hasSyntaxError = true;									//if there is an unclassified token left on string
											displayError(line_number,"Unclassified token found");
										}
									}	
								}
							}
							
							if(!isStringEmpty(str)) {
								if(!hasHAI(line_number)) break;										//checker if source code has HAI as code delimiter
							}
							
							//if there is a syntax error, print error prompt
							if(hasSyntaxError) {												
								//printError(line_number);											//print error to interface
								break;																//terminate reading file if there's an error
							}
							//update lexemesByLine
							if(!lexemeLine.isEmpty()) {
								line_numByLine.add(line_number);									//update arraylist of line_numbers	
								lexemesByLine.add((ArrayList<String>) lexemeLine.clone());
								classificationByLine.add((ArrayList<String>) classificationLine.clone());
							}
						}
						
						if(!hasSyntaxError && hasKTHXBYE(line_number)) {							//if no error, update interface
							setSourceCode(program);
							setLexemeTable();
						}
						//else printError(line_number);												//if with error, print error	
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