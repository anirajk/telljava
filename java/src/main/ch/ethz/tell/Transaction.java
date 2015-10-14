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

    private Transaction(long impl) {
        mImpl = impl;
    }

    public static Transaction startTransaction(ClientManager manager) {
        return new Transaction(startTx(manager.getClientManagerPtr(), manager.getScanMemoryManagerPtr()));
    }

    private static native boolean commit(long impl);
    public final boolean commit() {
        return commit(mImpl);
    }

    private static native void abort(long impl);
    public final void abort() {
        abort(mImpl);
    }

    private static native void startScan(long impl,
                                         String tableName,
                                         byte queryType,
                                         long selectionLength,
                                         long selection,
                                         long queryLength,
                                         long query);

    
    public ScanIterator scan(ScanQuery query,
                             String tableName,
                             short[] projection)
    {
        sun.misc.Unsafe unsafe = Unsafe.getUnsafe();
        byte queryType = ScanQuery.QueryType.FULL.toUnderlying();
        long projLength = 0;
        long proj = 0;
        if (projection != null) {
            Arrays.sort(projection, 0, projection.length);
            projLength = projection.length * 2;
            proj = unsafe.allocateMemory(projLength);
            for (int i = 0; i < projection.length; ++i) {
                unsafe.putShort(proj + 2*i, projection[i]);
            }
            queryType = ScanQuery.QueryType.PROJECTION.toUnderlying();
        }
        Pair<Long, Long> selection = query.serialize();
        startScan(mImpl, tableName, queryType, selection.first, selection.second, projLength, proj);
        unsafe.freeMemory(selection.second);
        unsafe.freeMemory(proj);
        return new ScanIterator(mImpl);
    }
}
