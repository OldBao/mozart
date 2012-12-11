package edu.buaa.mozart.conditions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.list.OWLList;
import org.mindswap.owl.vocabulary.SWRLB;
import org.mindswap.owls.expression.Condition;
import org.mindswap.owls.process.variable.ProcessVar;
import org.mindswap.swrl.Atom;
import org.mindswap.swrl.BuiltinAtom;
import org.mindswap.swrl.SWRLDataObject;

import edu.buaa.mozart.color.Var;
import edu.buaa.mozart.notes.ComposeException;

public final class MozartSWRLCondition implements MozartCondition {

    private Condition.SWRL 	  mCondition;
    
    public void setCondtion(Condition.SWRL condition){
    	mCondition = condition;
    }
	@Override
	public <P extends ProcessVar>
	List<P> getDependVars(Set<P> varSet) throws ComposeException {
        List<P> vars = new ArrayList<P>();
        for (Atom a : mCondition.getBody()){
            //TODO partiallize required for this code
            if (a instanceof BuiltinAtom)
            for (SWRLDataObject obj : ((BuiltinAtom) a).getArguments()) {
                if (obj.isVariable()){
                	vars.add(obj.getProcessVar(varSet));
                }
        	} else {
        		throw new ComposeException("currently not supported atom besides builtins");
        	}
        }
		return vars;
	}

	@Override
	public String translateToML(List<Var> exprVars) throws ComposeException {
        for (Atom a : mCondition.getBody()){
            if (a instanceof BuiltinAtom){
                BuiltinAtom atom = (BuiltinAtom) a;
                OWLIndividual builtin = atom.getBuiltin();
                
                if (SWRLB.equal.equals(builtin)){return composeML(EQUAL, exprVars);}
                if (SWRLB.greaterThan.equals(builtin)){return composeML(GREATER_THAN, exprVars);}
                if (SWRLB.greaterThanOrEqual.equals(builtin)){return composeML(GREATER_THAN_OR_EQUAL, exprVars);}
                if (SWRLB.lessThan.equals(builtin)){return composeML(LESS_THAN, exprVars);}
                if (SWRLB.lessThanOrEqual.equals(builtin)){return composeML(LESS_THAN_OR_EQUAL, exprVars);}
                if (SWRLB.notEqual.equals(builtin)){return composeML(NOT_EQUAL, exprVars);}
            }
        }
		return null;
	}
	
    
    private String composeML(String func, List<Var> exprVars){
    	if (exprVars.size() == 0)
    		return func + "()";
        StringBuilder result = new StringBuilder();
        result.append(func + "(");
        int i;
        for (i =0; i < exprVars.size() - 1; i++) {
        	result.append(exprVars.get(i).getVarName() + ",");
        }
        result.append(exprVars.get(i).getVarName() + ")");
        return result.toString();
    }
    
    private static String EQUAL = "equal";
    private static String NOT_EQUAL = "equal";
    private static String GREATER_THAN = "greaterThan";
    private static String GREATER_THAN_OR_EQUAL = "greatherThanOrEuqal";
    private static String LESS_THAN = "lessThan";
    private static String LESS_THAN_OR_EQUAL = "lessThanOrEqual";

}
