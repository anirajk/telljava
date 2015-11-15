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

import java.io.Serializable;
import java.util.ArrayList;

import ch.ethz.tell.ScanQuery.CmpType;
import ch.ethz.tell.ScanQuery.AggrType;

public class CNFClause implements Serializable {

    private static final long serialVersionUID = 7526472295622776140L;

    private ArrayList<Predicate> predicates;

    public CNFClause() {
        this.predicates = new ArrayList<>();
    }

    public final void addPredicate(CmpType type, short field, PredicateType value) {
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

    public final short field(int idx) {
        return predicates.get(idx).field;
    }

    public final PredicateType value(int idx) {
        return predicates.get(idx).value;
    }

    public final Predicate get(int idx) {
        return predicates.get(idx);
    }
}