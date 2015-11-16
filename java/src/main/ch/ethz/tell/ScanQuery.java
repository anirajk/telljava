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

import ch.ethz.tell.Schema.FieldType;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;

public class ScanQuery implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    public enum CmpType {
        EQUAL           ((byte)1),
        NOT_EQUAL       ((byte)2),
        LESS            ((byte)3),
        LESS_EQUAL      ((byte)4),
        GREATER         ((byte)5),
        GREATER_EQUAL   ((byte)6),
        LIKE            ((byte)7),
        NOT_LIKE        ((byte)8),
        IS_NULL         ((byte)9),
        IS_NOT_NULL     ((byte)10);
        private byte value;

        CmpType(byte value) {
            this.value = value;
        }

        public final byte toUnderlying() {
            return value;
        }

    }

    public enum AggrType {
        MIN             ((byte)1),
        MAX             ((byte)2),
        SUM             ((byte)3),
        CNT             ((byte)4);
        private byte value;

        AggrType(byte value) {this.value = value;}

        public final byte toUnderlying() {return value; }
    }

    public enum QueryType {
        FULL((byte) 1),
        PROJECTION((byte) 2),
        AGGREGATION((byte) 3);
        private byte value;

        QueryType(byte value) {
            this.value = value;
        }

        public final byte toUnderlying() {
            return this.value;
        }
    }

    private int partitionKey;
    private int partitionValue;
    private List<CNFClause> selections;
    private List<Short> projections;
    private List<Aggregation> aggregations;

    public ScanQuery() {
        this(0, 0);
    }

    /**
     * creates a scan query object with partition key and partition value on primary key, which means all tuples with
     * (primary-key mod partition-key) == partition-value
     * are returned.
     *
     * @param partitionKey the number of (Spark-) partitions to be scanned
     * @param partitionValue the partition index to look for
     */
    public ScanQuery(int partitionKey, int partitionValue) {
        this.partitionKey = partitionKey;
        this.partitionValue = partitionValue;
        this.selections = new ArrayList<>();
        this.projections = new ArrayList<>();
        this.aggregations = new ArrayList<>();
    }

    public ScanQuery(int partitionKey, int partitionValue, ScanQuery other) {
        this.partitionKey = partitionKey;
        this.partitionValue = partitionValue;
        this.selections = other.selections;
        this.projections = other.projections;
        this.aggregations = other.aggregations;
    }

    public void addSelection(CNFClause clause) {
        selections.add(clause);
    }

    public boolean addProjection(short field) {
        if (this.aggregations.size() > 0)     // we cannot have aggregations and projections at the same time!
            return false;
        projections.add(field);
        return true;
    }

    public boolean addAggregation(AggrType type, short field, String name, FieldType fieldType) {
        return addAggregation(new Aggregation(type, field, name, fieldType));
    }

    public boolean addAggregation(Aggregation aggregation) {
        if (this.projections.size() > 0)     // we cannot have aggregations and projections at the same time!
            return false;
        aggregations.add(aggregation);
        return true;
    }

    public boolean isProjection() {
        return this.projections.size() > 0;
    }

    public boolean isAggregation() {
        return this.aggregations.size() > 0;
    }

    private Map<Short, List<Pair<Predicate, Byte>>> prepareSelectionSerialization() {
        Map<Short, List<Pair<Predicate, Byte>>> res = new TreeMap<>();
        byte pos = 0;
        for (CNFClause selection : selections) {
            int predicates = selection.numPredicates();
            for (int i = 0; i < predicates; ++i) {
                short cId = selection.field(i);
                List<Pair<Predicate, Byte>> list = new ArrayList<>();
                if (res.containsKey(cId)) {
                    list = res.get(cId);
                } else {
                    list = new ArrayList<>();
                    res.put(cId, list);
                }
                Pair<Predicate, Byte> p = new Pair<>(selection.get(i), pos);
                list.add(p);
            }
            ++pos;
        }
        return res;
    }

    private final long selctionSize(Map<Short, List<Pair<Predicate, Byte>>> selectionMap) {
        long res = 16 + 8*selectionMap.size();
        for (Map.Entry<Short, List<Pair<Predicate, Byte>>> e : selectionMap.entrySet()) {
            for (Pair<Predicate, Byte> p : e.getValue()) {
                res += 8;
                PredicateType value = p.first.value;
                switch (value.getType()) {
                    // Fields smaller than 64 bit can be stored inside the 8 byte allocated for the predicate
                    case Bool:
                    case Short:
                    case Int:
                    case Float:
                        break;
                    // 64 bit fields need another 8 byte in the predicate
                    case Long:
                    case Double:
                        res += 8;
                        break;
                    // Variable sized fields store the size in the predicate followed by the 8 byte padded data
                    case String:
                        String s = value.value();
                        res += s.getBytes(Charset.forName("UTF-8")).length;
                        res += (res % 8 == 0 ? 0 : 8 - (res % 8));
                        break;
                    case ByteArray:
                        byte[] v = value.value();
                        res += v.length;
                        res += (res % 8 == 0 ? 0 : 8 - (res % 8));
                        break;
                }
            }
        }
        return res;
    }

    /**
     * See tellstore/util/scanQueryBatchProcessor.hpp for a description of the memory
     * format of a scan query.
     *
     * @returns pair(size, address)
     */
    final Pair<Long, Long> serializeSelection() {
        Map<Short, List<Pair<Predicate, Byte>>> selectionMap = prepareSelectionSerialization();
        sun.misc.Unsafe unsafe = Unsafe.getUnsafe();
        long size = this.selctionSize(selectionMap);
        long res = unsafe.allocateMemory(size);
        unsafe.putInt(res, selectionMap.size());
        unsafe.putInt(res + 4, selections.size());
        unsafe.putInt(res + 8, partitionKey);
        unsafe.putInt(res + 12, partitionValue);
        long offset = 16;
        try {
            for (Map.Entry<Short, List<Pair<Predicate, Byte>>> e : selectionMap.entrySet()) {
                // Column Id
                unsafe.putShort(res + offset, e.getKey());
                offset += 2;
                // Number of predicates for this column
                short numPreds = (short) e.getValue().size();
                unsafe.putShort(res + offset, numPreds);
                offset += 6; // padding
                for (Pair<Predicate, Byte> p : e.getValue()) {
                    unsafe.putByte(res + offset, p.first.type.toUnderlying());
                    offset += 1;
                    unsafe.putByte(res + offset, p.second);
                    offset += 1;
                    PredicateType data = p.first.value;
                    byte[] arr = null;
                    switch (data.getType()) {
                        case Bool:
                            boolean v = data.value();
                            unsafe.putShort(res + offset, (short) (v ? 1 : 0));
                            offset += 6;
                            break;
                        case Short:
                            unsafe.putShort(res + offset, data.value());
                            offset += 6;
                            break;
                        case Int:
                            offset += 2;
                            unsafe.putInt(res + offset, data.value());
                            offset += 4;
                            break;
                        case Long:
                            offset += 6;
                            unsafe.putLong(res + offset, data.value());
                            offset += 8;
                            break;
                        case Float:
                            offset += 2;
                            unsafe.putFloat(res + offset, data.value());
                            offset += 4;
                            break;
                        case Double:
                            offset += 6;
                            unsafe.putDouble(res + offset, data.value());
                            offset += 8;
                            break;
                        case String:
                            String str = data.value();
                            arr = str.getBytes(Charset.forName("UTF-8"));
                        case ByteArray:
                            offset += 2;
                            if (arr == null) arr = data.value();
                            unsafe.putInt(res + offset, arr.length);
                            offset += 4;
                            for (int i = 0; i < arr.length; ++i) {
                                unsafe.putByte(res + offset + i, arr[i]);
                            }
                            offset += arr.length;
                            if (arr.length % 8 != 0) {
                                // padding
                                offset += (8 - (arr.length % 8));
                            }
                            break;
                    }
                }
            }
            return new Pair<Long, Long>(size, res);
        } catch (Exception e) {
            unsafe.freeMemory(res);
            throw e;
        }
    }

    /**
     * See tellstore/tests/client/TestClient.cpp for examples.
     *
     * @returns pair(size, address)
     */
    final Pair<Long, Long> serializeProjection() {
        Collections.sort(this.projections);
        sun.misc.Unsafe unsafe = Unsafe.getUnsafe();
        long size = 2*this.projections.size();
        long res = unsafe.allocateMemory(size);
        try {
            int i = 0;
            for (Short s: this.projections) {
                unsafe.putShort(res + i, s);
                i += 2;
            }
            return new Pair<Long, Long>(size, res);
        } catch (Exception e) {
            unsafe.freeMemory(res);
            throw e;
        }
    }

    /**
     * See tellstore/tests/client/TestClient.cpp for examples.
     *
     * @returns pair(size, address)
     */
    final Pair<Long, Long> serializeAggregation() {
        Collections.sort(this.aggregations);
        sun.misc.Unsafe unsafe = Unsafe.getUnsafe();
        long size = 4*this.aggregations.size();
        long res = unsafe.allocateMemory(size);
        try {
            int i = 0;
            for (Aggregation aggr: this.aggregations) {
                unsafe.putShort(res + i, aggr.field);
                unsafe.putShort(res + i + 2, aggr.type.toUnderlying());
                i += 4;
            }
            return new Pair<Long, Long>(size, res);
        } catch (Exception e) {
            unsafe.freeMemory(res);
            throw e;
        }
    }

    /**
     * @return a result schema for the result, if it is a projection or aggregation
     */
    final Schema getAggregationResultSchema() {
        if (aggregations.size() < 0) {
            Schema result = new Schema();
            Collections.sort(aggregations);
            for (Aggregation aggr: aggregations) {
                result.addField(aggr.fieldType, aggr.name, true);
            }
            return result;
        }
        throw new RuntimeException("can only get result schema for aggregation!");
    }
}

