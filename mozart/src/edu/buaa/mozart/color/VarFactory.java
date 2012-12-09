package edu.buaa.mozart.color;

import java.util.HashMap;
import java.util.Map;

import org.cpntools.accesscpn.model.PetriNet;
import org.mindswap.owls.process.variable.ProcessVar;

import edu.buaa.mozart.notes.ComposeException;

public class VarFactory {
	private Map<ProcessVar, Var> mVarMap;
	
    private Var mControlVar;
	private static VarFactory mInstance = new VarFactory();
    
	private VarFactory(){
		mVarMap = new HashMap<ProcessVar, Var>();
        mControlVar = new Var("control", ColorFactory.getInstance().getControlColor());
	}
    
    public static VarFactory getInstance(){
    	return mInstance;
    }
    
    public Var getVarFromProcessVar(ProcessVar processVar) throws ComposeException{
    	if (mVarMap.containsKey(processVar)){
    		return mVarMap.get(processVar);
    	} else {
            Color varColor = ColorFactory.getInstance().getBasicColor(processVar);
            if (varColor == null) {
            	throw new ComposeException("var " + processVar + " 's type " + processVar.getParamType() + " not supported now");
            } 
            
            int i  = 0;
            String newVarName = "";
            Var newVar;
            do{
            	if (i == 0) {
            		newVarName = processVar.getLocalName();
            	}else{
            		newVarName = newVarName + i;
            	}
            	i++;
            	newVar = new Var(newVarName, varColor);
            }while (mVarMap.containsValue(newVar));
            
            mVarMap.put(processVar, newVar);
            return newVar;
    	}
    }
    public void addAllVarToNet(PetriNet net){
    	for (Var var : mVarMap.values()){
    		var.addVarToNet(net);
    	}
        mControlVar.addVarToNet(net);
    }

	public Var getControlVar() {
		return mControlVar;
	}
    
}
