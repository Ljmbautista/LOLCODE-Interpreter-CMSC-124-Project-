package bautista_ramirez;

public class Identifier {
	public String identifier;
	public String value;
	
	// Constructor
	public Identifier(String identifier, String value){
		this.identifier = identifier;
		this.value = value;
	}
	
	// Getters for lexeme and frequency classification attributes
	public String getIdentifier() {
		return this.identifier;
	}
	public String getVal() {
		return this.value;
	}
}
