package edu.buaa.mozart.notes;

import java.lang.reflect.InvocationTargetException;

import org.cpntools.accesscpn.model.Name;
import org.cpntools.accesscpn.model.Page;
import org.cpntools.accesscpn.model.PetriNet;
import org.cpntools.accesscpn.model.Transition;
import org.cpntools.accesscpn.model.impl.ModelFactoryImpl;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Process;

import edu.buaa.utils.IDFactory;
import edu.buaa.utils.QuickFactory;

/**
 * @author zhanggx
 *	 @description
 */
public abstract class Notation {
	public static Notation getNotationFromProcess(Process process, Page page) 
			throws ComposeException{
		Notation notation = null;
		if(process instanceof AtomicProcess){
			notation = new AtomicClef(page);
		}
		else if (process instanceof CompositeProcess){
			ControlConstruct struct = ((CompositeProcess)process).getComposedOf();
			String notationClassName = struct.getClass().getName()+"Chord";
			try{
				Class<Notation> chordClass = 
						(Class<Notation>) 
						ClassLoader.getSystemClassLoader().loadClass(notationClassName);
				notation = 
						chordClass.getConstructor(new Class[]{Page.class}).newInstance(page);
			}catch(ClassNotFoundException ex){
				throw new ComposeException("Not support construct " + notationClassName);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		else {
			throw new ComposeException("currently not support simple process");
		}
		return notation;
	}
	
	protected Notation(Page cpnPage){
		mCPNPage = cpnPage;
		
		//create start and action transistion
		mStartTransition = QuickFactory.getTransition(cpnPage, "InputBinding");
		mEndTransition   = QuickFactory.getTransition(cpnPage, "OutputBinding");
	}
	
	abstract public void compose(Process process) throws ComposeException;
	
	protected Transition mStartTransition;
	protected Transition mEndTransition;
	protected Page           mCPNPage;
	private String        	    mNotationName;

	/**
	 * @return the mStartTransition
	 */
	public Transition getStartTransition() {
		return mStartTransition;
	}

	/**
	 * @param mStartTransition the mStartTransition to set
	 */
	public void setStartTransition(Transition mStartTransition) {
		this.mStartTransition = mStartTransition;
	}

	/**
	 * @return the mEndTransition
	 */
	public Transition getEndTransition() {
		return mEndTransition;
	}

	/**
	 * @param mEndTransition the mEndTransition to set
	 */
	public void setEndTransition(Transition mEndTransition) {
		this.mEndTransition = mEndTransition;
	}

	/**
	 * @return the mNotationName
	 */
	public String getNotationName() {
		return mNotationName;
	}

	/**
	 * @param mNotationName the mNotationName to set
	 */
	public void setNotationName(String mNotationName) {
		this.mNotationName = mNotationName; 
	}
}
