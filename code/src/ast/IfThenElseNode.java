package ast;

import java.util.ArrayList;
import java.util.Iterator;

import lib.Functions;

public class IfThenElseNode implements Node {

	private ArrayList<String> roles;
	private ArrayList<String> conds; 
	private Node thenStat;
	private Node elseStat;

	public IfThenElseNode(ArrayList<String> r, ArrayList<String> c, Node then, Node elseS) {
		roles = r;
		conds = c;
		thenStat = then;
		elseStat = elseS;
	}

	@Override
	public ArrayList<String> getRoles(){

		return roles;
	}

	@Override
	public String toPrint() {
		return "if-then-else";
	}

	@Override
	public String generateCode(ArrayList<Node> mods, int index, int maxIndex, boolean isCtmc, ArrayList<String> labels, String prot) {
		ArrayList<String> rolesTmp = new ArrayList<String>();
		ArrayList<String> condsTmp = new ArrayList<String>();

		for(String el : roles) {
			rolesTmp.add(Functions.changeIndex(el,index,maxIndex));
		}
		for(String el : conds) {
			condsTmp.add(Functions.returnStringNewIndex(el,index,maxIndex));
		}
		for(int i=0; i<roles.size(); i++) {
			for(Node el2 : mods) {
				if(el2.toPrint().equals(rolesTmp.get(i))) {
					int state = ((ModuleNode) el2).getValueRecursion(prot);


					if(state == -1) {
						state = ((ModuleNode) el2).getMaxState();
						((ModuleNode) el2).setValueRecursion(prot,state);
					}
					else {

						if(((ModuleNode) el2).getLastState()==-1) {
							state = ((ModuleNode) el2).getNewState(prot);

							String tmp = rolesTmp.get(i)+"=";
							String tmpStm = tmp + (state-1);
							int indexCont = -1;
							boolean flag = false;
							for(String stm :  ((ModuleNode) el2).getCommands()) {
								if(stm.contains(tmpStm)) {
									flag = true;
								}
							}
							if(!flag) {state--;}
							else {
								((ModuleNode) el2).removeNewState(prot);
							}
							((ModuleNode) el2).setValueRecursion(prot,state);

						}
						else {
							if(((ModuleNode) el2).getMaxValueRecursion(prot)<((ModuleNode) el2).getNewState(prot)) {
								state = ((ModuleNode) el2).getNewState(prot);
								if(state==1) {state = state + 1;}
								while((state-1)>=((ModuleNode) el2).getNewState(prot)) {
									((ModuleNode) el2).removeNewState(prot);
								}

							}
							else {

								state = ((ModuleNode) el2).getMaxValueRecursion(prot);

							}

						}


					}
					String tmp = rolesTmp.get(i)+"=";
					String tmpStm = tmp + state;
					int indexCont = -1;
					for(String stm :  ((ModuleNode) el2).getCommands()) {
						if(stm.contains(tmpStm)) {
							state++;
							tmpStm = tmp + state;
						}
					}
					String stat = "";
					boolean ifte = false;
					for(String comms : ((ModuleNode) el2).getCommands()) {
						if(comms.contains("IFTE")) {
							stat = comms.substring(0,comms.indexOf(" -> IFTE"));
							ifte = true;
						}
					}
					String stat2 = "";
					boolean ifte2 = false;
					for(String comms : ((ModuleNode) el2).getCommands()) {
						if(comms.contains("IFTE")) {
							stat2 = comms.substring(0,comms.indexOf(" -> IFTE"));
							ifte2 = true;
						}
					}

					if(!ifte) {
						stat = "[] (" + rolesTmp.get(i) + "=" +  state + ")" ;
					}
					int indexTmp = stat.indexOf(tmp);
					String statTmp = stat;

					stat = stat + "&"+condsTmp.get(i) + " -> " ;

					/*if(thenStat instanceof RecNode){

						int stateRec = ((ModuleNode) el2).getValueRecursion(thenStat.toPrint());
						if(stateRec == -1) {

							((ModuleNode) el2).setValueRecursion(thenStat.toPrint(),state+1);
							state = state + 1 ;
						}
						else {
							state = stateRec;
						}

					}*/
					String statNew = "";

					int stateNew = Character.getNumericValue(stat.charAt(indexTmp+tmp.length()))+1;
					if(stateNew<=((ModuleNode) el2).getMaxFinState()) {

						stateNew = ((ModuleNode) el2).getMaxFinState() + 1;

					}
					if(thenStat instanceof RecNode) {
						stateNew = ((ModuleNode) el2).getValueRecursion(thenStat.toPrint());
					}
					if(thenStat instanceof EndNode || stateNew == -1) {
						stat = stat + "(" + rolesTmp.get(i) + "'=" + "TBD" + ");"; 
					}

					else {
						stat = stat + "1 : (" + rolesTmp.get(i) + "'=" + stateNew + ");"; 
						if(!(thenStat instanceof RecNode) ){
							statNew = "[] (" + rolesTmp.get(i) + "=" +  stateNew + ") -> " + "IFTE";
						}
					}
					Iterator<String> itr = ((ModuleNode) el2).getCommands().iterator();
					while (itr.hasNext()) {
						String comms = itr.next();
						if(comms.contains("IFTE") && ifte && comms.substring(0, comms.indexOf(" -> IFTE")).equals(statTmp)) {
							itr.remove();
						}
					}
					if(thenStat instanceof IfThenElseNode) {
						((ModuleNode) el2).setNewStateIndex(prot, stateNew,0);
					}
					else {
						((ModuleNode) el2).setNewState(prot, stateNew);
					}
					((ModuleNode) el2).addCommand(statNew);
					((ModuleNode) el2).addCommand(stat);
					if(thenStat instanceof EndNode || thenStat instanceof RecNode) {
						((ModuleNode) el2).setLastState(-1);
					}
					thenStat.generateCode(mods,index,maxIndex,isCtmc,labels,prot);

					if(!ifte2) {
						stat2 = "[] (" + rolesTmp.get(i) + "=" +  state + ")" ;

					}

					String tmp2 = rolesTmp.get(i)+"=";
					int indexTmp2 = stat2.indexOf(tmp);
					String statTmp2 = stat2;
					stat2 = stat2 + "&!"+condsTmp.get(i) + " -> " ;

					String statNew2 = "";
					int stateNew2 = Character.getNumericValue(stat2.charAt(indexTmp2+tmp2.length()))+1;
					if(stateNew2<=((ModuleNode) el2).getMaxFinState()) {
						stateNew2 = ((ModuleNode) el2).getMaxFinState() + 1;
					}
					if(stateNew2==stateNew) {
						stateNew2++;
					}
					if(elseStat instanceof EndNode || elseStat instanceof RecNode) {
						((ModuleNode) el2).setLastState(-1);
					}
					if(elseStat instanceof RecNode) {
						stateNew2 = ((ModuleNode) el2).getValueRecursion(elseStat.toPrint());
					}
					if(elseStat instanceof EndNode || stateNew2 == -1) {
						stat2 = stat2 + "1 : (" + rolesTmp.get(i) + "'=" + "TBD" + ");"; 
					}
					else {
						stat2 = stat2 + "1 : (" + rolesTmp.get(i) + "'=" + stateNew2 + ");"; 
						if(!(elseStat instanceof RecNode) ){
							statNew2 = "[] (" + rolesTmp.get(i) + "=" +  stateNew2 + ") -> " + "IFTE";
						}
					}

					Iterator<String> itr2 = ((ModuleNode) el2).getCommands().iterator();
					while (itr2.hasNext()) {
						String comms = itr2.next();
						if(comms.contains("IFTE") && ifte2 && comms.substring(0, comms.indexOf(" -> IFTE")).equals(statTmp2)) {
							itr.remove();
						}
					}

					//((ModuleNode) el2).setNewState(prot, state);
					if(elseStat instanceof IfThenElseNode) {
						((ModuleNode) el2).setNewStateIndex(prot, stateNew2,0);
					}
					else {
						((ModuleNode) el2).setNewState(prot, stateNew2);
					}

					((ModuleNode) el2).addCommand(stat2);
					((ModuleNode) el2).addCommand(statNew2);
					elseStat.generateCode(mods,index,maxIndex,isCtmc,labels,prot);

				}
			}
		}

		return null;
	}

}
