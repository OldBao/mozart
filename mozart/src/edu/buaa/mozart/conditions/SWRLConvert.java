package edu.buaa.mozart.conditions;


import org.mindswap.owls.expression.Condition;
import org.mindswap.owls.expression.Expression.SWRL;

public class SWRLConvert implements Convert{

    
	public String convert(SWRL swrl) {
        return "";
	}

	@Override
	public String convert(Condition condition) {
        if (condition instanceof SWRL)
        	return convert((SWRL)condition);
        else
        	return null;
	}

}
