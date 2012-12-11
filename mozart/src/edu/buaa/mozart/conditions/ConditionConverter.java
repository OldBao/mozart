package edu.buaa.mozart.conditions;

import java.util.Set;

import org.mindswap.owls.expression.Condition;
import org.mindswap.owls.expression.Condition.SWRL;
import org.mindswap.owls.process.variable.ProcessVar;

import edu.buaa.mozart.notes.ComposeException;

public class ConditionConverter {
    
    private static SWRLConvert mSWRLConverter = new SWRLConvert();
   
	public static <P extends ProcessVar>
	MozartCondition getMozartCondition(Condition condition, Set<P> contextVars) throws ComposeException {
		if (condition instanceof SWRL){
			return mSWRLConverter.getMozartCondition(condition, contextVars);
		} else {
			throw new ComposeException("Condtion " + condition + "not supported");
		}
	}
}
