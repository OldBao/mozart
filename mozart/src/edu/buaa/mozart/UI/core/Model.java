package edu.buaa.mozart.UI.core;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owls.service.Service;

import edu.buaa.composer.Composer;
import edu.buaa.composer.impl.Mozart;

public final class Model {
	private Composer mComposer;
    private OWLOntology mOnt;
    private OWLKnowledgeBase mKB;
    
    private URI mURI;
    private List<Service> mServices;
    private boolean mURIchanged = false;
    
    public Model(){
    	mComposer = new Mozart();
        mKB = OWLFactory.createKB();
        mOnt = mKB.createOntology(null);
    }
    public void setURI(URI uri){
        mURIchanged = true;
    	mURI = uri;
    }
    
    public List<Service> getServices() throws IOException{
        if(mURIchanged || mServices != null)
        	mServices = mKB.readAllServices(mURI);
        return mServices;
    }
}
