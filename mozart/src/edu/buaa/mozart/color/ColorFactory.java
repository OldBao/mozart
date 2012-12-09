package edu.buaa.mozart.color;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.cpntypes.CPNBool;
import org.cpntools.accesscpn.model.cpntypes.CPNInt;
import org.cpntools.accesscpn.model.cpntypes.CPNProduct;
import org.cpntools.accesscpn.model.cpntypes.CPNString;
import org.cpntools.accesscpn.model.cpntypes.CpntypesFactory;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLType;
import org.mindswap.owl.vocabulary.XSD;
import org.mindswap.owls.process.variable.ProcessVar;

import edu.buaa.composer.ComposerConfig;
import edu.buaa.mozart.notes.ComposeException;

public final class ColorFactory {
	private Map<OWLType, Color> mBasicType;
    private Vector<Color> mTypeSet;
    
    OWLOntology mOnt;
    private Color mControlColor;
    
    private static ColorFactory mInstance = new ColorFactory();
    
    public static ColorFactory getInstance(){
    	return mInstance;
    }
    
    public void addAllColorToNet(PetriNet net){
    	for (Color color: mTypeSet) {
    		color.addDeclarationToNet(net);
    	}
    }
    
    private ColorFactory(){
    	mBasicType = new HashMap<OWLType, Color>();
        mTypeSet    =  new Vector<Color>();
		OWLKnowledgeBase mKB = OWLFactory.createKB();
		mOnt = mKB.createOntology(null);
        
    	CPNInt intType = CpntypesFactory.INSTANCE.createCPNInt();
    	CPNString stringType = CpntypesFactory.INSTANCE.createCPNString();
    	CPNBool boolType = CpntypesFactory.INSTANCE.createCPNBool();
        
        Color     intColor= new Color("INT", intType, ComposerConfig.INTEGER_ENCODING, ComposerConfig.INTEGER_DECODING); 
        Color     stringColor= new Color("STRING", stringType, ComposerConfig.STRING_ENCODING, ComposerConfig.STRING_DECODING); 
        Color     boolColor= new Color("BOOL", boolType, ComposerConfig.BOOL_ENCODING, ComposerConfig.BOOL_DECODING); 
        
        mBasicType.put(mOnt.getDataType(XSD.xsdInt), intColor);
        mBasicType.put(mOnt.getDataType(XSD.xsdShort), intColor);
        mBasicType.put(mOnt.getDataType(XSD.xsdLong), intColor);
        mBasicType.put(mOnt.getDataType(XSD.xsdByte), intColor);
        mBasicType.put(mOnt.getDataType(XSD.xsdString), stringColor);
        mBasicType.put(mOnt.getDataType(XSD.xsdBoolean), boolColor);
        
        mTypeSet.add(intColor);
        mTypeSet.add(stringColor);
        mTypeSet.add(boolColor);
        
        CPNBool controlType = CpntypesFactory.INSTANCE.createCPNBool();
        mControlColor = new Color("CONTROL", controlType);
        mTypeSet.add(mControlColor);
    }
    
	public <P extends ProcessVar>	
	Color getColorWithoutControl(List<P> vars, String colorName) throws ComposeException{
		if (vars.size() == 0) {
            throw new ComposeException("Color can't be null");
		} else if (vars.size() == 1){
			return getBasicColor(vars.get(0));
		}else{
			CPNProduct type =  getProductTypeFromVars(vars);
            Color retColor = new Color(colorName, type);
            mTypeSet.add(retColor);
            return retColor;
		}
	} 
	
    public Color getBasicColor(ProcessVar var) {
    	return mBasicType.get(var.getParamType());
    }
    
    public 
    Color getControlColor(){
    	return mControlColor;
    }
    
    private boolean colorExists(String colorName){
        for (Color color : mTypeSet){
        	if (color.getTypeName().equals(colorName)) {
                return true;
        	}
        }
        return false;
    }
    
	public <P extends ProcessVar> 
	Color getColorWithControl(List<P> vars, String colorName) throws ComposeException{
		if (vars.size() == 0) {
            throw new ComposeException("for null vars, please call getControlColor()");
		} else {
			CPNProduct type = getProductTypeFromVars(vars);
			Color retColor = new Color(colorName, type);
            type.addSort(mControlColor.getTypeName());
            mTypeSet.add(retColor);
            return retColor;
		}
	}
    
	private <P extends ProcessVar> 
	CPNProduct getProductTypeFromVars(List<P> vars) throws ComposeException{
		CPNProduct pType = CpntypesFactory.INSTANCE.createCPNProduct();
        
        for (P v : vars) {
        	Color color = mBasicType.get(v.getParamType());
            if (color == null)
            	throw new ComposeException("XSD " + v.getParamType() + " not supported");
            pType.addSort(color.getTypeName());
        }
        return pType;
    }
}
