package edu.buaa.mozart.conditions;

import java.util.Set;

import org.mindswap.owls.expression.Condition;
import org.mindswap.owls.process.variable.ProcessVar;

import edu.buaa.mozart.notes.ComposeException;

public interface Convert {
	public <P extends ProcessVar> 
	MozartCondition getMozartCondition(Condition condition, Set<P> vars)
			throws ComposeException;
}
