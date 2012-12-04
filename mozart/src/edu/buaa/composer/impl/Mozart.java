package edu.buaa.composer.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cpntools.accesscpn.model.Code;
import org.cpntools.accesscpn.model.HLAnnotation;
import org.cpntools.accesscpn.model.HLDeclaration;
import org.cpntools.accesscpn.model.ModelFactory;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Place;
import org.cpntools.accesscpn.model.Sort;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.declaration.DeclarationFactory;
import org.cpntools.accesscpn.model.declaration.MLDeclaration;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Choice;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.IfThenElse;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Produce;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.variable.Output;
import org.mindswap.owls.process.variable.ProcessVar;

import edu.buaa.composer.Composer;
import edu.buaa.composer.ComposerAbility;
import edu.buaa.composer.ComposerConfig;
import edu.buaa.composer.NotationContext;
import edu.buaa.mozart.color.Color;
import edu.buaa.mozart.color.ColorFactory;
import edu.buaa.mozart.color.Var;
import edu.buaa.mozart.color.VarFactory;
import edu.buaa.mozart.notes.AtomicClef;
import edu.buaa.mozart.notes.ChoiceChord;
import edu.buaa.mozart.notes.ComposeException;
import edu.buaa.mozart.notes.CompositeClef;
import edu.buaa.mozart.notes.IfThenElseChord;
import edu.buaa.mozart.notes.Notation;
import edu.buaa.mozart.notes.PerformChord;
import edu.buaa.mozart.notes.ProduceChord;
import edu.buaa.mozart.notes.SequenceChord;
import edu.buaa.mozart.stub.MozartWebCodeFactory;
import edu.buaa.utils.IDFactory;
import edu.buaa.utils.QuickFactory;

public class Mozart extends Composer implements ComposerAbility{
    PetriNet mNet;
    Page		  mPage;  //如果是层次Petri网，Page就不只一个了
    Set<Perform> mPerformSet; //用来记录所有已经处理的Perform，用来转化过程中，根据Perform的ID查找Perform对象

    public Mozart(){
    	mNet = QuickFactory.getPetriNet();
    	mPage = QuickFactory.getPage(mNet, "Mozart Page");
        mPerformSet = new HashSet<Perform>();
    }
    
	@Override
	public void composeAtomicClef(AtomicProcess process, AtomicClef clef,
			NotationContext context) {
		try {
			String clefName   = process.getLocalName();
            
			Place inputPlace  = QuickFactory.getPlace(mPage, clefName + "_INPUT");
			Color inputColor = ColorFactory.getInstance().getColorWithControl(process.getInputs(), clefName.toUpperCase() + "_INPUT");
            Sort   inputSort    = QuickFactory.getSort(mNet, inputColor.getTypeName());
            inputPlace.setSort(inputSort);
            
			Place outputPlace   = QuickFactory.getPlace(mPage, clefName + "_OUTPUT");
			Color outputColor = ColorFactory.getInstance().getColorWithControl(process.getOutputs(), clefName.toUpperCase() + "_OUTPUT");
            Sort   outputSort    = QuickFactory.getSort(mNet, outputColor.getTypeName());
            outputPlace.setSort(outputSort);
            
            Transition wsTransition = QuickFactory.getTransition(mPage, clefName);
            Code code = MozartWebCodeFactory.getInstance().getProcessCode(process, mNet);
            wsTransition.setCode(code);
            //wsTransition.setCondition(QuickFactory.getCondition(mNet, "control=true");
            
           HLAnnotation inArcAnno = getAnnoFromVars(process.getInputs(), true);
           QuickFactory.combine(mPage, inputPlace, wsTransition, inArcAnno);
           HLAnnotation outArcAnno = getAnnoFromVars(process.getOutputs(),true);
           QuickFactory.combine(mPage, wsTransition, outputPlace, outArcAnno);
		} catch (ComposeException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void composeSequenceChrod(Sequence sequenceProcess,
			SequenceChord chrod, NotationContext context) {
		
	}

	@Override
	public void composeCompositeChord(CompositeProcess process,
			CompositeClef chord, NotationContext context) {
		
	}

	@Override
	public void composePerform(Perform perform, PerformChord chord,
			NotationContext context) {
		
	}

	@Override
	public void composeConclude(OWLIndividualList<Result> results,
			OWLIndividualList<Output> outputs, NotationContext context)
			throws ComposeException {
		
	}

	@Override
	public void composeProduce(Produce produce, ProduceChord chrod,
			NotationContext context) throws ComposeException {
		
	}

	@Override
	public void composeChoice(Choice choice, ChoiceChord choiceChord,
			NotationContext context) throws ComposeException {
		
	}

	@Override
	public void composeIfThenElse(IfThenElse ite, IfThenElseChord iteChord,
			NotationContext context) throws ComposeException {
		
	}

	@Override
	public PetriNet Compose(Process process) throws ComposeException {
        if (process instanceof AtomicProcess) {
        	//single process compose
        }
        degress_compose(process);
        
        addAllDecl();
		return mNet;
	}
    
     //必须最后调用, 因为模型转化中间过程可能生成新的color
    private void addAllDecl(){
        addBooleanED();
        ColorFactory.getInstance().addAllColorToNet(mNet);
        VarFactory.getInstance().addAllVarToNet(mNet);
    }
    
    private void addBooleanED(){
        MLDeclaration mlDecl = DeclarationFactory.INSTANCE.createMLDeclaration();
        mlDecl.setCode("use \"" + ComposerConfig.BOOL_DECL_PATH + "\";");
        
        HLDeclaration hlyDecl = ModelFactory.INSTANCE.createHLDeclaration();
        hlyDecl.setStructure(mlDecl);
        hlyDecl.setId(IDFactory.getInstance().getRandomId());
        hlyDecl.setParent(mNet);
    }
    
    private void addBooleanED_raw(){
    	String booleanDecl = 
    			"fun boolEncode (to_send) = \n" +
    			"Byte.stringToBytes(Bool.toString(to_send))\n" +
    			"fun boolDecode (received) = \n" + 
    			"case Bool.fromString(Byte.bytesToString(received)) of\n" +
    			"SOME received =>\n" +
    			"(  print(Bool.toString(received));\n" +
    			"received\n" +
    			")\n"+
    			"| NONE =>\n" +  
    			"raise InvalidDataExn(\"Failure receiving data: Boolean expected\");\n";
    }
    
    private void degress_compose(OWLIndividual individual) throws ComposeException {
    		if (individual == null)
    			return;
    		Notation notation = individual.getMozartNotation();

    		NotationContext context = new NotationContext();
    		notation.compose(this, context);
	}

    //从变量中获取HL表达式
    //注意：control约定在最后一个，也就是说必须为(...,control)的形式
	private <P extends ProcessVar> 
    HLAnnotation getAnnoFromVars(List<P> vars, boolean hasControl) throws ComposeException{
		HLAnnotation anno = ModelFactory.INSTANCE.createHLAnnotation();
        StringBuilder annoBuilder  = new StringBuilder();
        annoBuilder.append("(");
        
        for(P v : vars) {
        	Var pVar = VarFactory.getInstance().getVarFromProcessVar(v);
        	annoBuilder.append(pVar.getVarName());
            annoBuilder.append(",");
        }
        if (hasControl){
        	annoBuilder.append(VarFactory.getInstance().getControlVar().getVarName());
        	annoBuilder.append(")");
        }else {
        	annoBuilder.replace(annoBuilder.length()-1, annoBuilder.length(), ")");
        }
        anno.setText(annoBuilder.toString());
		anno.setParent(mNet);
        
        return anno;
    }
}