package edu.buaa.mozart.color;

import org.cpntools.accesscpn.model.HLDeclaration;
import org.cpntools.accesscpn.model.ModelFactory;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.cpntypes.CPNType;
import org.cpntools.accesscpn.model.declaration.DeclarationFactory;
import org.cpntools.accesscpn.model.declaration.TypeDeclaration;

import edu.buaa.utils.IDFactory;

public class Color {
	private String mTypeName;
    private CPNType mType;
    private String mEncoding, mDecoding;
    
    
    public Color(String typeName, CPNType type) {
        mTypeName = typeName;
        mType = type;
    }
    
    public Color(String typeName, CPNType type, String encoding, String decoding) {
        mTypeName = typeName;
        mType = type;
        mEncoding = encoding;
        mDecoding = decoding;
    }
    
    @Override
    public boolean
    equals(Object obj) {
    	Color color = (Color) obj;
    	return mTypeName.equals(color.mTypeName);
    }

    public String getEncoding(){
    	return mEncoding;
    }
    
    public String getDecoding(){
        return mDecoding;
    }

	public String getTypeName() {
		return mTypeName;
	}


	public void setTypeName(String mTypeName) {
		this.mTypeName = mTypeName;
	}

	public CPNType getType() {
		return mType;
	}

	public TypeDeclaration getTypeDeclaration(){
		TypeDeclaration typeDecl = DeclarationFactory.INSTANCE.createTypeDeclaration();
		typeDecl.setTypeName(mTypeName);
		typeDecl.setSort(mType);	
        return typeDecl;
	}
    
    public void addDeclarationToNet(PetriNet net){
		HLDeclaration hlyDecl = ModelFactory.INSTANCE.createHLDeclaration();
		hlyDecl.setStructure(getTypeDeclaration());
		hlyDecl.setId(IDFactory.getInstance().getRandomId());
        hlyDecl.setParent(net);
    }
}
