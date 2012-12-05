package edu.buaa.mozart.color;

import org.cpntools.accesscpn.model.HLDeclaration;
import org.cpntools.accesscpn.model.ModelFactory;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.declaration.DeclarationFactory;
import org.cpntools.accesscpn.model.declaration.VariableDeclaration;

import edu.buaa.utils.IDFactory;

public class Var {
    private String mVarName;
    private Color  mColor;
    
	public Var(String varName, Color color) {
		mVarName = varName;
        mColor 		   = color;
	}
    
    @Override
    public int
    hashCode(){
    	return mVarName.hashCode();
    }
    
    @Override
	public
    boolean equals(Object obj) {
    	Var other = (Var) obj;
    	return mVarName.equals(other.mVarName);
    }
    
	public String getVarName(){
		return mVarName;
	}
    
    public Color getColor(){
    	return mColor;
    }
	
    private VariableDeclaration getVarDecl(){
		VariableDeclaration varDecl = DeclarationFactory.INSTANCE.createVariableDeclaration();
		varDecl.addVariable(mVarName);
		varDecl.setTypeName(mColor.getTypeName());
        return varDecl;
    }
    
    public void addVarToNet(PetriNet net) {
    	HLDeclaration hlDecl = ModelFactory.INSTANCE.createHLDeclaration();
    	hlDecl.setStructure(getVarDecl());
    	hlDecl.setId(IDFactory.getInstance().getRandomId());
        hlDecl.setParent(net);
    }
    
}
