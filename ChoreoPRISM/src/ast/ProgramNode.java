package ast;

import java.util.ArrayList;

public class ProgramNode implements Node{

	public Node preamble ;
	public ArrayList<Node> modules = new ArrayList<Node>();
	public ArrayList<Node> protocols = new ArrayList<Node>();
	public int n ;

	ProgramNode(Node _preamble, ArrayList<Node> _modules, ArrayList<Node> _protocols, int _n){
		preamble = _preamble;
		modules = _modules;
		protocols = _protocols;
		n = _n;
	}

	@Override
	public String toPrint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {
		String program = "";
		isCtmc = ((PreambleNode) preamble).isCtmc();
		program = program + preamble.generateCode(modules,index,n,isCtmc,labels,prot);

		for(Node el : protocols) {
			el.generateCode(modules,index,n,isCtmc,labels,el.toPrint());
		}
		program = program + "\n";
		for(Node el : modules) {
			program = program + "module " + el.toPrint() + "\n\n";
			program = program + el.toPrint() + " : [0..TBD] init 0;\n";
			if(((ModuleNode) el).getVars()!=null) {
				for(String el2 : ((ModuleNode) el).getVars()) {
					program = program + el2 + "\n";
				}
			}
			program = program + "\n";
			for(String el2 : ((ModuleNode) el).getCommands()) {				
				program = program + el2 + "\n";
			}
			program = program + "\nendmodule\n\n";
			//program = program.replaceAll("TBD", String.valueOf(((ModuleNode) el).getMaxFinState()+1));
		}
		return program;
	}

	@Override
	public ArrayList<String> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

}
