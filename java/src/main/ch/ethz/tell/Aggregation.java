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

import ch.ethz.tell.ScanQuery.AggrType;
import ch.ethz.tell.Field.FieldType;

public class Aggregation implements Serializable, Comparable<Aggregation> {
    private static final long serialVersionUID = 7526472295622776150L;

    public Aggregation (AggrType type, short fieldIndex, String name, FieldType fieldType) {
        this.type = type;
        this.fieldIndex = fieldIndex;
        this.name = name;
        this.fieldType = fieldType;

        switch (type) {
            case MIN:
            case MAX:
            case SUM:
                this.notNull = false;
                break;

            case CNT:
                this.notNull = true;
                break;

            default:
                throw new RuntimeException("Unknown Aggregation Type");
        }
    }

    public AggrType type;
    public short fieldIndex; // index within source schema
    public String name; // name with which you will find the aggregation in the result schema
    public FieldType fieldType; // type of the resulting aggregation
    public boolean notNull;

    public int compareTo (Aggregation other) {
        return Short.compare(this.fieldIndex, other.fieldIndex);
    }
}