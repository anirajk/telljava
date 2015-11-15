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

import java.util.Arrays;

public class Transaction {
    private long mImpl;
    
    private static native long startTx(long clientManager, long scanMemoryManager);
    private static native long startTx(long transactionId, long clientManager, long scanMemoryManager);

    private Transaction(long impl) {
        mImpl = impl;
    }

    public static Transaction startTransaction(ClientManager manager) {
        return new Transaction(startTx(manager.getClientManagerPtr(), manager.getScanMemoryManagerPtr()));
    }

    public static Transaction startTransaction(long transactionId, ClientManager manager) {
        return new Transaction(startTx(transactionId, manager.getClientManagerPtr(), manager.getScanMemoryManagerPtr()));
    }

    private static native boolean commit(long impl);
    public final boolean commit() {
        return commit(mImpl);
    }

    private static native void abort(long impl);
    public final void abort() {
        abort(mImpl);
    }

    private static native long getTransactionId(long impl);
    public final long getTransactionId() {
        return getTransactionId(mImpl);
    }

    private static native void startScan(long impl,
                                         String tableName,
                                         byte queryType,
                                         long selectionLength,
                                         long selection,
                                         long queryLength,
                                         long query);


    private static native long schemaForTableImpl(long impl, String name);
    public Schema schemaForTable(String name) {
        return new Schema(schemaForTableImpl(mImpl, name));
    }

    
    public ScanIterator scan(ScanQuery scanQuery, String tableName)
    {
        Pair<Long, Long> selection = scanQuery.serializeSelection();
        byte queryType = ScanQuery.QueryType.FULL.toUnderlying();
        long queryLength = 0;
        long query = 0;

        if (scanQuery.isProjection()) {
            queryType = ScanQuery.QueryType.PROJECTION.toUnderlying();
            Pair<Long, Long> projection = scanQuery.serializeProjection();
            queryLength = projection.first;
            query = projection.second;
        }

        if (scanQuery.isAggregation()) {
            queryType = ScanQuery.QueryType.AGGREGATION.toUnderlying();
            Pair<Long, Long> aggregation = scanQuery.serializeAggregation();
            queryLength = aggregation.first;
            query = aggregation.second;
        }

        startScan(mImpl, tableName, queryType, selection.first, selection.second, queryLength, query);

        sun.misc.Unsafe unsafe = Unsafe.getUnsafe();
        unsafe.freeMemory(selection.second);
        if (query > 0)
            unsafe.freeMemory(query);
        return new ScanIterator(mImpl);
    }
}
