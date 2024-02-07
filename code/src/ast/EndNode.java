package ast;

import java.util.ArrayList;

public class EndNode implements Node {

	ArrayList<Node> roles ;
	
	public EndNode(ArrayList<Node> _roles) {roles = _roles;}
	
	@Override
	public String toPrint() {
		return "END";
	}
	
	@Override
	public ArrayList<String> getRoles(){
		return new ArrayList<String>();
	}

	

	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {
		
		return "";
	}

}
