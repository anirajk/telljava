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

public class ScanMemoryManager {
    private long mImpl;

    private native long createImpl(long clientManager, long chunkCount, long chunkLength);

    public ScanMemoryManager(ClientManager clientManager, long chunkCount, long chunkLength) {
        mImpl = createImpl(clientManager.getClientManagerPtr(), chunkCount, chunkLength);
    }

    @Override
    protected void finalize() throws Throwable {
        destroy();
    }

    private native void destroyImpl(long impl);
    public final void destroy() {
        if (mImpl == 0) return;
        destroyImpl(mImpl);
        mImpl = 0;
    }

    final long getPtr() {
        return mImpl;
    }
}
