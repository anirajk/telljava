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
#include <ch_ethz_tell_ScanIterator.h>
#include <jni.h>
#include <tellstore/ClientManager.hpp>
#include <tellstore/TransactionRunner.hpp>
#include <commitmanager/SnapshotDescriptor.hpp>
#include <iostream>
#include "helpers.hpp"

namespace {

enum class TxState {
    Initial,
    Commit,
    Abort,
    Done,
    Scan,
    ScanDone
};

struct ScanArgs {
    crossbow::string tableName;
    tell::store::ScanQueryType queryType;
    uint32_t selectionLength;
    const char* selection;
    uint32_t queryLength;
    const char* query;
};

struct ImplementationDetails {
    tell::store::SingleTransactionRunner<void> txRunner;
    std::atomic<TxState> state;
    tell::store::ScanMemoryManager& scanMemoryManager;
    ImplementationDetails(tell::store::ClientManager<void>& clientManager, tell::store::ScanMemoryManager& scanMemoryManager)
        : txRunner(clientManager)
        , state(TxState::Initial)
        , scanMemoryManager(scanMemoryManager)
        , snapshot(nullptr)
        , chunk(std::make_tuple(nullptr, nullptr))
    {}
    ScanArgs scanArgs;
    std::unique_ptr<tell::commitmanager::SnapshotDescriptor> snapshot;
    std::tuple<const char*, const char*> chunk;
    void run(tell::store::ClientHandle& handle) {
        snapshot = handle.startTransaction(tell::store::TransactionType::ANALYTICAL);
        runTx(handle);
    }
    void run(uint64_t baseVersion, tell::store::ClientHandle& handle) {
        using namespace tell::commitmanager;
        snapshot = SnapshotDescriptor::create(0, baseVersion, baseVersion, nullptr);
        runTx(handle);
    }
    void scan(tell::store::ClientHandle& handle) {
        auto tableResp = handle.getTable(scanArgs.tableName);
        auto table = tableResp->get();
        auto analyticalSnapshot = tell::store::ClientHandle::createAnalyticalSnapshot(snapshot->lowestActiveVersion(),
                snapshot->baseVersion());
        auto scanResult = handle.scan(table, *analyticalSnapshot, scanMemoryManager, scanArgs.queryType,
                scanArgs.selectionLength, scanArgs.selection,
                scanArgs.queryLength, scanArgs.query);
        txRunner.block();
        while (scanResult->hasNext()) {
            chunk = scanResult->nextChunk();
            txRunner.block();
        }
        state = TxState::ScanDone;
    }
    void runTx(tell::store::ClientHandle& handle) {
        state = TxState::Initial;
        while (state != TxState::Done) {
            switch (state.load()) {
                case TxState::Initial:
                    break;
                case TxState::Commit:
                case TxState::Abort:
                    handle.commit(*snapshot);
                    state = TxState::Done;
                    txRunner.unblock();
                    return;
                case TxState::Done:
                    std::cerr << "FATAL: invalid state in: " << __FILE__ << ':' << __LINE__ << std::endl;
                    std::terminate();
                case TxState::Scan:
                    scan(handle);
                    break;
                case TxState::ScanDone:
                    std::cerr << "FATAL: invalid state in: " << __FILE__ << ':' << __LINE__ << std::endl;
                    std::terminate();
            }
            txRunner.block();
        }
    }
};

} // anonymous namespace

jboolean Java_ch_ethz_tell_ScanIterator_next(JNIEnv* env, jclass, jlong ptr) {
    auto impl = reinterpret_cast<ImplementationDetails*>(ptr);
    impl->txRunner.unblock();
    impl->txRunner.wait();
    if (impl->state == TxState::ScanDone) {
        return false;
    }
    return true;
}

jlong Java_ch_ethz_tell_ScanIterator_address(JNIEnv*, jclass, jlong ptr) {
    auto impl = reinterpret_cast<ImplementationDetails*>(ptr);
    return reinterpret_cast<jlong>(std::get<0>(impl->chunk));
}

jlong Java_ch_ethz_tell_ScanIterator_length(JNIEnv*, jclass, jlong ptr) {
    auto impl = reinterpret_cast<ImplementationDetails*>(ptr);
    auto& chunk = impl->chunk;
    return reinterpret_cast<jlong>(std::get<1>(chunk) - std::get<0>(chunk));
}

jlong Java_ch_ethz_tell_Transaction_startTx__JJ(JNIEnv*, jclass, jlong clientManagerPtr, jlong scanMemoryManager) {
    auto clientManager = reinterpret_cast<tell::store::ClientManager<void>*>(clientManagerPtr);
    auto impl = new ImplementationDetails{ *clientManager, *reinterpret_cast<tell::store::ScanMemoryManager*>(scanMemoryManager) };
    impl->txRunner.execute([impl](tell::store::ClientHandle& handle){
        impl->run(handle);
    });
    impl->txRunner.wait();
    return reinterpret_cast<jlong>(impl);
}

jlong Java_ch_ethz_tell_Transaction_startTx__JJJ(JNIEnv*, jclass, jlong txId, jlong clientManagerPtr, jlong scanMemoryManager) {
    auto clientManager = reinterpret_cast<tell::store::ClientManager<void>*>(clientManagerPtr);
    auto impl = new ImplementationDetails{ *clientManager, *reinterpret_cast<tell::store::ScanMemoryManager*>(scanMemoryManager) };
    impl->txRunner.execute([impl, txId](tell::store::ClientHandle& handle){
        impl->run(txId, handle);
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

jlong Java_ch_ethz_tell_Transaction_getTransactionId(JNIEnv *, jclass, jlong ptr) {
    auto impl = reinterpret_cast<ImplementationDetails*>(ptr);
    return impl->snapshot->baseVersion();
}

void Java_ch_ethz_tell_Transaction_abort(JNIEnv*, jclass, jlong ptr) {
    auto impl = reinterpret_cast<ImplementationDetails*>(ptr);
    impl->state = TxState::Abort;
    impl->txRunner.unblock();
    impl->txRunner.wait();
    delete impl;
}

void Java_ch_ethz_tell_Transaction_startScan(JNIEnv* env,
        jclass,
        jlong ptr,
        jstring tableName,
        jbyte queryType,
        jlong selectionLength,
        jlong selection,
        jlong queryLength,
        jlong query)
{
    auto impl = reinterpret_cast<ImplementationDetails*>(ptr);
    ScanArgs& args = impl->scanArgs;
    args.tableName = to_string(env, tableName);
    args.queryType = crossbow::from_underlying<tell::store::ScanQueryType>(uint8_t(queryType));
    args.selectionLength = uint32_t(selectionLength);
    args.selection = reinterpret_cast<const char*>(selection);
    args.queryLength = uint32_t(queryLength);
    args.query = reinterpret_cast<const char*>(query);
    std::cout<<"STARTING SCAN"<<std::endl;
    impl->state = TxState::Scan;
    std::cout<<"UNBLOCK SCAN"<<std::endl;
    impl->txRunner.unblock();
    std::cout<<"WAIT SCAN"<<std::endl;
    impl->txRunner.wait();
    std::cout<<"OUT SCAN"<<std::endl;
}


