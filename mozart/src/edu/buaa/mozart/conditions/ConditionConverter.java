package edu.buaa.mozart.conditions;

import org.mindswap.owls.expression.Condition;
import org.mindswap.owls.expression.Condition.SWRL;

import edu.buaa.mozart.notes.ComposeException;

public class ConditionConverter {
    
    private static ConditionConverter mInstance = new ConditionConverter();
    public static ConditionConverter getInstance(){
    	return mInstance;
    }
    
    private static SWRLConvert mSWRLConverter = new SWRLConvert();
    private ConditionConverter(){
    	
    }
	public static String convert(Condition condition) throws ComposeException {
		if (condition instanceof SWRL){
			return mSWRLConverter.convert(condition);
		} else {
			throw new ComposeException("Condtion " + condition + "not supported");
		}
	}
}
