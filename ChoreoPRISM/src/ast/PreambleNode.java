package ast;

import java.util.ArrayList;

public class PreambleNode implements Node {

	private ArrayList<String> vars ;
	private boolean isCtmc ;
	
	public PreambleNode(ArrayList<String> _vars, boolean _ctmc) {
		vars = new ArrayList<String>();
		for(String el : _vars) {
			vars.add(el);
		}
		isCtmc = _ctmc;
	}
	
	public boolean isCtmc() {
		return isCtmc;
	}
	
	@Override
	public String toPrint() {
		return null;
	}
	


	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {
		String code = "";
		for(String el : vars) {
			code = code + el.substring(1,el.length()-1) + "\n";
		}
		return code;
	}

	@Override
	public ArrayList<String> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

}
