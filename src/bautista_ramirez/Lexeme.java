package bautista_ramirez;

public class Lexeme {
	public String lexeme;
	public String classification;
	
	// Constructor
	public Lexeme(String lexeme, String classification){
		this.lexeme = lexeme;
		this.classification = classification;
	}
	
	// Getters for lexeme and frequency classification attributes
	public String getLexeme() {
		return this.lexeme;
	}
	public String getClassification() {
		return this.classification;
	}
}
