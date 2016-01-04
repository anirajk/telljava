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

import ch.ethz.tell.Field;
import ch.ethz.tell.Field.FieldType;

public class Schema {

    private long mImpl;

    private final native long construct();
    private final native void destruct(long ptr);

    public Schema() {
        mImpl = construct();
    }

    public Schema(long impl) {
        mImpl = impl;
    }

    @Override
    protected void finalize() {
        destruct(mImpl);
    }

    public final native boolean addFieldImpl(long self, short type, String fieldName, boolean notNull);
    public final boolean addField(FieldType type, String fieldName, boolean notNull) {
        return addFieldImpl(mImpl, type.toUnderlying(), fieldName, notNull);
    }

    private final native boolean allNotNullImpl(long self);
    public final boolean allNotNull() {
        return allNotNullImpl(mImpl);
    }

    private final FieldType[] fromUnderlyings(int[] un) {
        FieldType[] res = new FieldType[un.length];
        for (int i = 0; i < un.length; ++i) {
            res[i] = FieldType.values()[un[i]];
        }
        return res;
    }

    private final native int[] fixedSizeFieldsImpl(long self);
    public final FieldType[] fixedSizeFields() {
        return fromUnderlyings(fixedSizeFieldsImpl(mImpl));
    }

    private final native int[] variableSizedFieldsImpl(long self);
    public final FieldType[] variableSizedFields() {
        return fromUnderlyings(variableSizedFieldsImpl(mImpl));
    }

    private final native short idOfImpl(long self, String columnName);
    public final short idOf(String columnName) { return idOfImpl(mImpl, columnName); }

    private final native long getHeaderLengthImpl(long self);
    public final long getHeaderLength() { return getHeaderLengthImpl(mImpl);    }

    private final native short typeOfImpl(long self, String columnName);
    private final native boolean nullabiltyOfImpl(long self, String columnName);

    public final Field getFieldByName(String columnName) {
        Field result = new Field();
        result.index = idOfImpl(mImpl, columnName);
        result.fieldName = columnName;
        result.fieldType = FieldType.fromUnderlying(typeOfImpl(mImpl, columnName));
        result.notNull = nullabiltyOfImpl(mImpl, columnName);
        return result;
    }

    private native final String[] getFieldNamesImpl(long self);
    public final String[] getFieldNames() { return getFieldNamesImpl(mImpl); }

}
