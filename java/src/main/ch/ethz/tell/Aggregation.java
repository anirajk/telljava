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

import ch.ethz.tell.ScanQuery.CmpType;
import ch.ethz.tell.ScanQuery.AggrType;

public class Aggregation implements Serializable, Comparable<Aggregation> {
    private static final long serialVersionUID = 7526472295622776150L;

    public Aggregation (AggrType type, short field) {
        this.type = type;
        this.field = field;
    }

    public AggrType type;
    public short field;

    public int compareTo (Aggregation o2) {
        Aggregation o1 = this;
        if (o1.field == o2.field)
            return ((Byte) o1.type.toUnderlying()).compareTo((Byte) o2.type.toUnderlying());
        else
            return ((Short) o1.field).compareTo((Short) o2.field);
    }
}