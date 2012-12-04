package edu.buaa.mozart.notes;

import edu.buaa.composer.Composer;
import edu.buaa.composer.NotationContext;

public abstract class DataChord extends Notation{

    public DataChord(){}
    
	abstract public void compose(Composer composer, NotationContext context)
			throws ComposeException;
}
