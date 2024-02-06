package ast;

import java.util.ArrayList;

import lib.Pair;

public class ModuleNode implements Node{

	private String name;
	private ArrayList<String> vars ;
	private int state = 0;
	private ArrayList<String> commands = new ArrayList<String>();
	private ArrayList<Pair<String,ArrayList<Integer>>> recursions = new ArrayList<Pair<String,ArrayList<Integer>>>();
	private ArrayList<Pair<String,ArrayList<Integer>>> notUsedRecursions = new ArrayList<Pair<String,ArrayList<Integer>>>();
	private int lastState = -1;
	
	
	public ModuleNode(String _name, ArrayList<String> _vars) {
		name = _name;
		vars = _vars;
	}


	public int getMaxState() {
		int maxState = 0;
		for(Pair<String,ArrayList<Integer>> el : recursions) {
			if(el.getSecond().get(el.getSecond().size()-1)>=maxState) {
				maxState = el.getSecond().get(el.getSecond().size()-1);
			}
		}
		if(state>=maxState) {
			maxState = state;
		}
		return maxState;
	}

	public int getLastState() {
		return lastState;
	}
	
	public void setLastState(int val) {
		lastState = val;
	}

	public int getValueRecursion(String rec) {

		for(Pair<String,ArrayList<Integer>> el : recursions) {
			if(el.getFirst().equals(rec)) {
				return el.getSecond().get(0);
			}
		}
		return -1;
	}

	public int getMaxValueRecursion(String rec) {
		int max = -1;
		for(Pair<String,ArrayList<Integer>> el : recursions) {
			if(el.getFirst().equals(rec)) {
				for(int val : el.getSecond()) {
					if(val>=max) {
						max = val;
					}
				}
			}
		}
		return max;
	}

	public ArrayList<Integer> getNewStates(String rec){
		for(Pair<String,ArrayList<Integer>> el : notUsedRecursions) {
			if(el.getFirst().equals(rec)) {
				return el.getSecond();
			}
		}
		return null;
	}

	public int getMaxNewStates(String rec) {
		int max = -1;
		for(Pair<String,ArrayList<Integer>> el : notUsedRecursions) {
			if(el.getFirst().equals(rec)) {
				for(int val : el.getSecond()) {
					if(val>=max) {
						max = val;
					}
				}
			}
		}
		return max;
	}

	public int getMinNewStates(String rec) {
		int min = -1;
		for(Pair<String,ArrayList<Integer>> el : notUsedRecursions) {
			if(el.getFirst().equals(rec)) {
				min = el.getSecond().get(0);
				for(int val : el.getSecond()) {
					if(val<=min) {
						min = val;
					}
				}
			}
		}
		return min;
	}


	public int getNewState(String rec) {
		for(Pair<String,ArrayList<Integer>> el : notUsedRecursions) {
			if(el.getFirst().equals(rec) ) {
				if(el.getSecond().size()>0) {
					return el.getSecond().get(0);
				}
				else {
					return -1;
				}
			}
		}
		return -1;
	}

	public void removeNewState(String rec) {
		for(Pair<String,ArrayList<Integer>> el : notUsedRecursions) {
			if(el.getFirst().equals(rec)) {
				el.getSecond().remove(0);
			}
		}
	}

	public void removeState(String rec, int _state) {
		int indexState = -1;
		for(Pair<String,ArrayList<Integer>> el : notUsedRecursions) {
			if(el.getFirst().equals(rec)) {
				for(int i=0; i<el.getSecond().size(); i++) {
					if(el.getSecond().get(i)==_state) {
						indexState = i;
					}
				}
			}
		}
		if(indexState!=-1) {
			for(Pair<String,ArrayList<Integer>> el : notUsedRecursions) {
				if(el.getFirst().equals(rec)) {
					el.getSecond().remove(indexState);
				}
			}
		}
	}

	public void setNewState(String rec, int state) {
		boolean found = false;
		for(Pair<String,ArrayList<Integer>> el : notUsedRecursions) {
			if(el.getFirst().equals(rec)) {
				found = true;
				ArrayList<Integer> tmp = new ArrayList<Integer>();
				if(el.getSecond()==null) {
					tmp.add(state);
					el.setSecond(tmp) ;
				}
				else {

					if(!el.getSecond().contains(state)) {
						el.getSecond().add(state);
					}
				}
			}
		}
		if(!found) {
			ArrayList<Integer> tmp = new ArrayList<Integer>();
			tmp.add(state);
			Pair<String,ArrayList<Integer>> pair = new Pair<String,ArrayList<Integer>>(rec,tmp);
			notUsedRecursions.add(pair);
		}
	}
	public void setNewStateIndex(String rec, int state, int index) {
		boolean found = false;
		for(Pair<String,ArrayList<Integer>> el : notUsedRecursions) {
			if(el.getFirst().equals(rec)) {
				found = true;
				ArrayList<Integer> tmp = new ArrayList<Integer>();
				if(el.getSecond()==null) {
					tmp.add(index,state);
					el.setSecond(tmp) ;
				}
				else {
					if(!el.getSecond().contains(state)) {
						el.getSecond().add(index,state);
					}
				}
			}
		}
		if(!found) {
			ArrayList<Integer> tmp = new ArrayList<Integer>();
			tmp.add(index,state);
			Pair<String,ArrayList<Integer>> pair = new Pair<String,ArrayList<Integer>>(rec,tmp);
			notUsedRecursions.add(pair);
		}
	}

	public void setValueRecursion(String rec, int state) {
		boolean found = false;
		for(Pair<String,ArrayList<Integer>> el : recursions) {
			if(el.getFirst().equals(rec)) {
				found = true;
				ArrayList<Integer> tmp = new ArrayList<Integer>();
				if(el.getSecond()==null) {
					tmp.add(state);
					el.setSecond(tmp) ;
				}
				else {
					el.getSecond().add(state);
				}
			}
		}
		if(!found) {
			ArrayList<Integer> tmp = new ArrayList<Integer>();
			tmp.add(state);
			Pair<String,ArrayList<Integer>> pair = new Pair<String,ArrayList<Integer>>(rec,tmp);
			recursions.add(pair);
		}
	}

	@Override
	public String toPrint() {
		return name;
	}

	public int getMaxFinState() {
		int maxState = 0;
		for(Pair<String,ArrayList<Integer>> pair : recursions) {
			for(int i : pair.getSecond()) {
				if(i>=maxState) {
					maxState = i;
				}
			}
		}
		for(Pair<String,ArrayList<Integer>> pair : notUsedRecursions) {
			for(int i : pair.getSecond()) {
				if(i>=maxState) {
					maxState = i;
				}
			}
		}
		return maxState;
	}

	public void setState() {
		state = state + 1;
	}

	public void addCommand(String stat) {
		commands.add(stat);
	}

	public ArrayList<String> getVars(){
		return vars;
	}

	public ArrayList<String> getCommands(){
		return commands;
	}



	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {
		return null;
	}


	@Override
	public ArrayList<String> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}


}
