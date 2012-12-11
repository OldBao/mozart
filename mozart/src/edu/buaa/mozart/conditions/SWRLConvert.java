package edu.buaa.mozart.conditions;


import java.util.Set;

import org.mindswap.owls.expression.Condition;
import org.mindswap.owls.expression.Expression.SWRL;
import org.mindswap.owls.process.variable.ProcessVar;

import edu.buaa.mozart.notes.ComposeException;

public class SWRLConvert implements Convert{
	public MozartCondition getMozartCondition(Condition.SWRL swrl) throws ComposeException {
        MozartSWRLCondition condition = new MozartSWRLCondition();
        condition.setCondtion((Condition.SWRL)swrl);
        
        return condition;
	}

	@Override
	public  <P extends ProcessVar>
	MozartCondition getMozartCondition(Condition condition, Set<P> vars) throws ComposeException {
        if (condition instanceof SWRL)
        	return getMozartCondition((Condition.SWRL)condition);
        else
        	return null;
	}

}
