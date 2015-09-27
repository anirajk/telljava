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
#include <ch_ethz_tell_Transaction.h>
#include <jni.h>
#include <tellstore/ClientManager.hpp>
#include <tellstore/TransactionRunner.hpp>
#include <iostream>

namespace {

enum class TxState {
    Initial,
    Commit,
    Abort,
    Done
};

struct ImplementationDetails {
    tell::store::SingleTransactionRunner<void> txRunner;
    ImplementationDetails(tell::store::ClientManager<void>& clientManager)
        : txRunner(clientManager)
    {}
    TxState state;
    void* result;
    void run(tell::store::ClientHandle& handle) {
        auto tx = handle.startTransaction(tell::store::TransactionType::ANALYTICAL);
        runTx(handle, tx);
    }
    void runTx(tell::store::ClientHandle& handle, tell::store::ClientTransaction& tx) {
        state = TxState::Initial;
        while (state != TxState::Done) {
            switch (state) {
                case TxState::Initial:
                    break;
                case TxState::Commit:
                    tx.commit();
                    state = TxState::Done;
                    break;
                case TxState::Abort:
                    tx.abort();
                    state = TxState::Done;
                    break;
                case TxState::Done:
                    std::cerr << "FATAL: invalid state in: " << __FILE__ << ':' << __LINE__ << std::endl;
                    std::terminate();
            }
            txRunner.block();
        }
    }
};

} // anonymous namespace

jlong Java_ch_ethz_tell_Transaction_startTx(JNIEnv*, jclass, jlong clientManagerPtr) {
    auto clientManager = reinterpret_cast<tell::store::ClientManager<void>*>(clientManagerPtr);
    auto impl = new ImplementationDetails{ *clientManager };
    impl->txRunner.execute([impl](tell::store::ClientHandle& handle){
        impl->run(handle);
    });
    impl->txRunner.wait();
    return reinterpret_cast<jlong>(impl);
}

jboolean Java_ch_ethz_tell_Transaction_commit(JNIEnv*, jclass, jlong ptr) {
    auto impl = reinterpret_cast<ImplementationDetails*>(ptr);
    impl->state = TxState::Commit;
    impl->txRunner.unblock();
    impl->txRunner.wait();
    delete impl;
    return true;
}

void Java_ch_ethz_tell_Transaction_abort(JNIEnv*, jclass, jlong ptr) {
    auto impl = reinterpret_cast<ImplementationDetails*>(ptr);
    impl->state = TxState::Abort;
    impl->txRunner.unblock();
    impl->txRunner.wait();
    delete impl;
}


