package ch.ethz.tell;

import java.util.ArrayList;

public class ScanQuery {
	public enum CmpType {
		EQUAL,
		LESS_EQUAL,
		GREATER_EQUAL,
		LESS,
		GREATER,
		STARTS_WITH,
		ENDS_WITH
	}
	
    public class CNFCLause {
    	private class Predicate {
    		public CmpType type;
    		public int field;
    		public PredicateType value;
    	}
    	private ArrayList<Predicate> predicates;
    	
    	public final void addPredicate(CmpType type, int field, PredicateType value) {
    		Predicate p = new Predicate();
    		p.type = type;
    		p.field = field;
    		p.value = value;
    		predicates.add(p);
    	}
    	
    	public final int numPredicates() {
    		return predicates.size();
    	}
    	
    	public final CmpType type(int idx) {
    		return predicates.get(idx).type;
    	}
    	
    	public final int field(int idx) {
    		return predicates.get(idx).field;
    	}
    	
    	public final PredicateType value(int idx) {
    		return predicates.get(idx).value;
    	}
    }

    private ArrayList<CNFCLause> selections;
    private ArrayList<Integer> projections;

    public void addSelection(CNFCLause clause) {
        selections.add(clause);
    }

    public void addProjection(int field) {
        projections.add(field);
    }
}

