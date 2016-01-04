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

public class Projection implements Serializable, Comparable<Projection> {
    private static final long serialVersionUID = 7526472295622776150L;

    public Projection (short fieldIndex, String name, Field.FieldType fieldType, boolean notNull) {
        this.fieldIndex = fieldIndex;
        this.name = name;
        this.fieldType = fieldType;
        this.notNull = notNull;
    }

    public short fieldIndex;  // field index in source scheme
    public String name;       // name with which you will find the projected column in the result scheme
    public Field.FieldType fieldType; // type of the resulting projection (equal to the type in the source scheme)
    public boolean notNull;        // can this field be null?

    public int compareTo (Projection other) {
        return Short.compare(this.fieldIndex, other.fieldIndex);
    }
}