package edu.buaa.mozart.color;


public 
class Pair<P, V> {
    private P mP;
    private V mV;
    
    public Pair(){
    	
    }
	public Pair(P p, V v){
		mP = p;
		mV = v;
	}
    
    public void setFirst(P p ){
    	mP = p;
    }
    public void setSecond(V v) {
    	mV = v;
    }
    
    @Override
	public
    int hashCode(){
    	return mP.hashCode() * 33 + mV.hashCode();
    }
    
    @Override
    public
    String toString(){
    	return "(" + mP.toString() + "," +mV.toString() +")";
    }
    @Override
    public 
    boolean equals(Object o) {
    	Pair<P, V> p = (Pair<P, V>)o;
        boolean result = p.mP.equals(mP) && p.mV.equals(mV);
        return result;
    }
}
