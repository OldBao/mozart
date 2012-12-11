package edu.buaa.mozart.conditions;

import java.util.List;
import java.util.Set;

import org.mindswap.owls.process.variable.ProcessVar;

import edu.buaa.mozart.color.Var;
import edu.buaa.mozart.notes.ComposeException;

public interface MozartCondition {
	String translateToML(List<Var> exprVars) throws ComposeException;
	<T extends ProcessVar>List<T> getDependVars(Set<T> varSet)
			throws ComposeException;
}
