package edu.buaa.mozart.stub;

import org.cpntools.accesscpn.model.Code;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.impl.ModelFactoryImpl;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;

import edu.buaa.composer.ComposerConfig;
import edu.buaa.mozart.color.Var;
import edu.buaa.mozart.color.VarFactory;
import edu.buaa.mozart.notes.ComposeException;

public final class MozartWebCodeFactory {
    
    public Code getInitCode(PetriNet net){
    	StringBuilder gBuilder = new StringBuilder();
    	gBuilder.append("input()\n");
    	gBuilder.append("output()\n");
    	gBuilder.append("action\n");
    	gBuilder.append("openConnection("+ ComposerConfig.CONN_NAME + "," + ComposerConfig.WS_STUB_PORT+")\n");
        
        System.out.println("Init Code Segment\n" + gBuilder.toString());
        
    	Code code = ModelFactoryImpl.eINSTANCE.createCode();
        code.setText(gBuilder.toString());
        code.setParent(net);
        return code;
    }
    
    public Code getProcessCode(AtomicProcess process, PetriNet net) throws ComposeException{
        	String conn = "\"" + ComposerConfig.CONN_NAME + "\"";
            
    		Code code = ModelFactoryImpl.eINSTANCE.createCode();
            StringBuilder inputBuilder, outputBuilder, actionBuilder;
            
            inputBuilder = new StringBuilder();
            outputBuilder = new StringBuilder();
            actionBuilder = new StringBuilder();
            
            inputBuilder.append("input(");
            for (Input input : process.getInputs()){
                Var var = VarFactory.getInstance().getVarFromProcessVar(input);
				inputBuilder.append(var.getVarName());
                inputBuilder.append(",");
    		}
            inputBuilder.replace(inputBuilder.length()-1, inputBuilder.length(), ")\n");
            
            
            outputBuilder.append("output(");
            for (Output output : process.getOutputs()){
                Var var = VarFactory.getInstance().getVarFromProcessVar(output);
				outputBuilder.append(var.getVarName());
                outputBuilder.append(",");
    		}
            outputBuilder.replace(outputBuilder.length()-1, outputBuilder.length(), ")\n");
            
            actionBuilder.append("action(\n");
            actionBuilder.append("let\n");
            actionBuilder.append("val mozart_ws = \"" + process.getURI()+ "\"\n");
            actionBuilder.append("in\n");
            
            actionBuilder.append("send(" + conn + ",mozart_ws,"+ ComposerConfig.STRING_ENCODING + ")\n");
            actionBuilder.append("send(" + conn + "," + process.getInputs().size() + ","+ ComposerConfig.INTEGER_ENCODING + ")\n");
            
            for (Input input : process.getInputs()){
				Var var = VarFactory.getInstance().getVarFromProcessVar(input);
                actionBuilder.append("send(" + conn + ","  + var.getVarName() + "," + var.getColor().getEncoding() + ");\n");
    		}
            
            actionBuilder.append("(\n");
            for(Output output: process.getOutputs()){
				Var var = VarFactory.getInstance().getVarFromProcessVar(output);
            	actionBuilder.append("receive(" + conn  + "," + var.getColor().getDecoding() + "),\n");
            }
            actionBuilder.delete(actionBuilder.length()-2, actionBuilder.length()-1);
            actionBuilder.append(")\n");
            
            actionBuilder.append("end)\n");
            
            String codeTxt = inputBuilder.toString() + outputBuilder.toString() + actionBuilder.toString();
            System.out.println("code segment : \n" + codeTxt);
            code.setText(codeTxt);
            code.setParent(net);
            return code ;
    }
    
	public static MozartWebCodeFactory getInstance(){
		return mInstance;
	}
    
	
	private MozartWebCodeFactory(){
    	
    }
    
	private static MozartWebCodeFactory mInstance = new MozartWebCodeFactory();
}
