/*
 * (C) Copyright 2015 ETH Zurich Systems Group (http://www.systems.ethz.ch/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Markus Pilman <mpilman@inf.ethz.ch>
 *     Simon Loesing <sloesing@inf.ethz.ch>
 *     Thomas Etter <etterth@gmail.com>
 *     Kevin Bocksrocker <kevin.bocksrocker@gmail.com>
 *     Lucas Braun <braunl@inf.ethz.ch>
 */
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

