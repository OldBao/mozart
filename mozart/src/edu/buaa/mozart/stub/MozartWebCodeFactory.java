package edu.buaa.mozart.stub;

import org.cpntools.accesscpn.model.Code;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.impl.ModelFactoryImpl;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.MozartDataConstruct;
import org.mindswap.owls.process.variable.Input;
import org.mindswap.owls.process.variable.Output;

import edu.buaa.composer.ComposerConfig;
import edu.buaa.mozart.ML.CodeSegment;
import edu.buaa.mozart.ML.Tuple;
import edu.buaa.mozart.color.Var;
import edu.buaa.mozart.color.VarFactory;
import edu.buaa.mozart.notes.ComposeException;

public final class MozartWebCodeFactory {
    
//    public Code getInitCode(PetriNet net, String inputTuple){
//    	StringBuilder gBuilder = new StringBuilder();
//    	gBuilder.append("input"+inputTuple + ";\n");
//    	gBuilder.append("output()\n");
//    	gBuilder.append("action(\n" + 
//    									 "let\n" + 
//    									 "in\n");
//    	gBuilder.append("openConnection(\""+ ComposerConfig.CONN_NAME + "\","+
//    									"\"" + ComposerConfig.SERVER_ADDR + "\","	+ 
//    									ComposerConfig.WS_STUB_PORT+");\n");
//        gBuilder.append("(\n)\nend);");
//        
//        
//    	Code code = ModelFactoryImpl.eINSTANCE.createCode();
//        code.setText(gBuilder.toString());
//        code.setParent(net);
//        return code;
//    }
    
    public CodeSegment getInitCode(Tuple inputTuple){
    	CodeSegment cs = new CodeSegment();
    	cs.addInput(inputTuple);
        return cs;
    }
    
    public Code getProcessCode(AtomicProcess process, MozartDataConstruct mdc, PetriNet net) throws ComposeException{
        	String conn = "\"" + ComposerConfig.CONN_NAME + "\"";
            
    		Code code = ModelFactoryImpl.eINSTANCE.createCode();
            StringBuilder inputBuilder, outputBuilder, actionBuilder;
            
            inputBuilder = new StringBuilder();
            outputBuilder = new StringBuilder();
            actionBuilder = new StringBuilder();
            
            inputBuilder.append("input(");
            for (Input input : process.getInputs()){
                Var var = VarFactory.getInstance().getVarFromProcessVar(mdc, input);
				inputBuilder.append(var.getVarName());
                inputBuilder.append(",");
    		}
            inputBuilder.replace(inputBuilder.length()-1, inputBuilder.length(), ");\n");
            
            
            outputBuilder.append("output(");
            for (Output output : process.getOutputs()){
                Var var = VarFactory.getInstance().getVarFromProcessVar(mdc,output);
				outputBuilder.append(var.getVarName());
                outputBuilder.append(",");
    		}
            outputBuilder.replace(outputBuilder.length()-1, outputBuilder.length(), ");\n");
            
            actionBuilder.append("action(\n");
            actionBuilder.append("let\n");
            actionBuilder.append("val mozart_ws = \"" + process.getURI()+ "\"\n");
            actionBuilder.append("in\n");
            
            actionBuilder.append("send(" + conn + ",mozart_ws,"+ ComposerConfig.STRING_ENCODING + ");\n");
            actionBuilder.append("send(" + conn + "," + process.getInputs().size() + ","+ ComposerConfig.INTEGER_ENCODING + ");\n");
            
            for (Input input : process.getInputs()){
				Var var = VarFactory.getInstance().getVarFromProcessVar(mdc, input);
                actionBuilder.append("send(" + conn + ","  + var.getVarName() + "," + var.getColor().getEncoding() + ");\n");
    		}
            
            actionBuilder.append("(\n");
            for(Output output: process.getOutputs()){
				Var var = VarFactory.getInstance().getVarFromProcessVar(mdc,output);
            	actionBuilder.append("receive(" + conn  + "," + var.getColor().getDecoding() + "),\n");
            }
            actionBuilder.delete(actionBuilder.length()-2, actionBuilder.length()-1);
            actionBuilder.append(")\n");
            
            actionBuilder.append("end);\n");
            
            String codeTxt = inputBuilder.toString() + outputBuilder.toString() + actionBuilder.toString();
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

	public CodeSegment getExitCode() {
        CodeSegment cs = new CodeSegment();
        cs.addAction("closeConnection(\""+ ComposerConfig.CONN_NAME+ "\")");
        
        return cs;
	}
}
