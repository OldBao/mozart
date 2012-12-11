package edu.buaa.mozart.color;


import java.util.HashMap;
import java.util.Map;

import org.cpntools.accesscpn.model.PetriNet;
import org.mindswap.owls.process.MozartDataConstruct;
import org.mindswap.owls.process.variable.ProcessVar;
import org.mindswap.owls.vocabulary.OWLS;

import edu.buaa.mozart.notes.ComposeException;

public class VarFactory {
	private Map<Pair<MozartDataConstruct,ProcessVar>, Var> mVarMap;
	
    private Var mControlVar, mConditionVar;
	private static VarFactory mInstance = new VarFactory();
    
	private VarFactory(){
		mVarMap = new HashMap<Pair<MozartDataConstruct, ProcessVar>, Var>();
        mControlVar = new Var("control", ColorFactory.getInstance().getControlColor());
        mConditionVar = new Var("condition", ColorFactory.getInstance().getConditionColor());
	}
    
    public static VarFactory getInstance(){
    	return mInstance;
    }
    
    public  <V extends ProcessVar> Var getVarFromProcessVar(MozartDataConstruct perform, ProcessVar processVar) throws ComposeException{
        Pair<MozartDataConstruct, ProcessVar> newPair = new Pair<MozartDataConstruct, ProcessVar>();
        newPair.setFirst(perform);
        newPair.setSecond(processVar);
    	if (mVarMap.containsKey(newPair)){
    		return mVarMap.get(newPair);
    	} else {
            Color varColor = ColorFactory.getInstance().getBasicColor(processVar);
            if (varColor == null) {
            	throw new ComposeException("var " + processVar + " 's type " + processVar.getParamType() + " not supported now");
            } 
            String newVarName = perform.getLocalName() + "_" +processVar.getLocalName();
            
            Var newVar = new Var(newVarName, varColor);
            mVarMap.put(newPair, newVar);
            return newVar;
    	}
    }
    public void addAllVarToNet(PetriNet net){
    	for (Var var : mVarMap.values()){
    		var.addVarToNet(net);
    	}
        mControlVar.addVarToNet(net);
        mConditionVar.addVarToNet(net);
    }

	public Var getControlVar() {
		return mControlVar;
	}

	public Var getConditionVar() {
		return mConditionVar;
	}

	public void setConditionVar(Var mConditionVar) {
		this.mConditionVar = mConditionVar;
	}
    
}
