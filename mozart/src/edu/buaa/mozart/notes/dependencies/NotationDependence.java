package edu.buaa.mozart.notes.dependencies;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.mindswap.owls.process.variable.ProcessVar;

import edu.buaa.mozart.notes.Notation;

public class NotationDependence {
    public interface Visitor {
    	public void visit_dep(Notation srcNotation, Notation dstNotation, Set<ProcessVar> vars);
    }
    public class GraphArc {
    	private Set<ProcessVar> mVars;
        private GraphNode mSource, mDst;
        
        public GraphArc(){
        	mVars = new HashSet<ProcessVar>();
        }

		public GraphNode getSource() {
			return mSource;
		}

		public void setSource(GraphNode mSource) {
			this.mSource = mSource;
		}

		public GraphNode getDst() {
			return mDst;
		}

		public void setDst(GraphNode mDst) {
			this.mDst = mDst;
		}
        
        public void addVar(ProcessVar var) {
        	mVars.add(var);
        }
        
        public Set<ProcessVar> getAllVars(){
        	return mVars;
        }
        
        public String toString(){
        	return mSource.getNotation() + "==>" + mDst.getNotation();
        }
     }
    
	class GraphNode {
		private Notation mNotation;
		private List<GraphArc> mNextArcs;
        private boolean mVisit;
        public GraphNode (Notation notation){
        	setNotation(notation);
        	mNextArcs = new ArrayList<GraphArc>();
        }
		public Notation getNotation() {
			return mNotation;
		}
		public void setNotation(Notation mNotation) {
			this.mNotation = mNotation;
		}
		public List<GraphNode> getNextNodes() {
            List<GraphNode> nodes = new ArrayList<GraphNode>();
            for (GraphArc arc : mNextArcs) {
            	nodes.add(arc.getDst());
            }
            return nodes;
		}
		public List<GraphArc> getAllArcs(){
			return mNextArcs;
		}
        
		public void addNextNodes(List<GraphNode> mNextNodes) {
            for (GraphNode node: mNextNodes) {
                addTo(node);
            }
		}
        
        public GraphArc addTo(GraphNode node) {
            GraphArc tmpArc = getArcTo(node);
            if (null == tmpArc){
            	tmpArc = new GraphArc();
            	tmpArc.setDst(node);
            	tmpArc.setSource(this);
                mNextArcs.add(tmpArc);
            }             	
            return tmpArc;
        }
        
        public GraphArc getArcTo(GraphNode node) {
        	for (GraphArc arc : mNextArcs) {
        		if (arc.getSource() == this && arc.getDst() == node) {
        			return arc;
        		}
        	}        	
        	return null;
        }
        
        public boolean isCombineTo(GraphNode node) {
            return getArcTo(node) != null;
        }
		public boolean isVisit() {
			return mVisit;
		}
		public void setVisit(boolean mVisit) {
			this.mVisit = mVisit;
		}
	}
    
    private Map<Notation, GraphNode> mGraphNodes;
    
    public NotationDependence(){
    	mGraphNodes = new HashMap<Notation, GraphNode>();
    }
    public void addNotation(Notation notation){
        if (!mGraphNodes.containsKey(notation)){
        	GraphNode node = new GraphNode(notation);
        	mGraphNodes.put(notation, node);
        }
    }
    
    public GraphArc addDependency(Notation sourceNotation, Notation dstNotation) {
    	GraphNode sourceNode = 	mGraphNodes.get(sourceNotation);
    	GraphNode dstNode 		 =  	mGraphNodes.get(dstNotation);
        
    	if (sourceNode != null  && dstNode != null) {
            System.out.println("add Dependency :"  + sourceNotation + "==>" + dstNotation);
    		return sourceNode.addTo(dstNode);
    	}
    	return null;
    }
    
    public List<Notation> getDependencies(Notation notation){
        GraphNode node = mGraphNodes.get(notation);
    	List<Notation> notations = new ArrayList<Notation>();
        for(GraphNode nextnode : node.getNextNodes())
        	notations.add(nextnode.getNotation());
        return notations;
    }
    
    public void Visit(GraphNode startNode) {
        if (mVisitor == null)
        	return;
        Queue<GraphNode> nodeQueue = new ArrayDeque<GraphNode>();
        for (GraphNode node  : mGraphNodes.values()){
        	node.setVisit(false);
        }
        
        nodeQueue.add(startNode);
    	while(!nodeQueue.isEmpty()){
            GraphNode node = nodeQueue.poll();
            if(node.isVisit()) {
            	continue;
            }
            node.setVisit(true);
            for (GraphArc arc : node.getAllArcs()) {
                mVisitor.visit_dep(arc.getSource().getNotation(), arc.getDst().getNotation(), arc.getAllVars());
            	nodeQueue.add(arc.getDst());
            }
    	}
    }
    
    public void Visit(Notation notation) {
    	GraphNode node = mGraphNodes.get(notation);
        if (node != null) {
        	Visit(node);
        }
    }
    
    public void setVisitor(Visitor visitor) {
    		mVisitor = visitor;
    }
    private Visitor mVisitor;
}
