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

public class ScanIterator implements Serializable {
    private static final long serialVersionUID = 7526472295622770144L;
    private long mImpl;

    private static native boolean next(long impl);
    public final boolean next() {
        return next(mImpl);
    }

    private static native long address(long impl);
    public final long address() {
        return address(mImpl);
    }

    private static native long length(long impl);
    public final long length() {
        return length(mImpl);
    }

    ScanIterator(long impl) {
        mImpl = impl;
    }

}

