package edu.buaa.transformer;

import org.cpntools.accesscpn.model.PetriNet;
import org.mindswap.owls.service.Service;

public interface MozartBuilder {
	public  PetriNet transform(Service service);
}
