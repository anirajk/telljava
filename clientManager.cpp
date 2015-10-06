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
#include <ch_ethz_tell_ClientManager.h>
#include <tellstore/ClientConfig.hpp>
#include <tellstore/ClientManager.hpp>
#include "clientManager.h"

using namespace tell::store;

using ImplementationDetails = telljava::ClientManager;

crossbow::string to_string(JNIEnv* env, jstring str) {
    auto ptr = env->GetStringUTFChars(str, nullptr);
    crossbow::string res(ptr, env->GetStringUTFLength(str));
    env->ReleaseStringUTFChars(str, ptr);
    return res;
}

jlong Java_ch_ethz_tell_ClientManager_getClientManagerPtr(JNIEnv*, jclass, jlong impl) {
    auto o = reinterpret_cast<ImplementationDetails*>(impl);
    return reinterpret_cast<jlong>(&o->clientManager);
}

jlong Java_ch_ethz_tell_ClientManager_getScanMemoryManagerPtr(JNIEnv*, jclass, jlong impl) {
    auto o = reinterpret_cast<ImplementationDetails*>(impl);
    return reinterpret_cast<jlong>(&o->scanMemoryManager);
}

jlong Java_ch_ethz_tell_ClientManager_init(JNIEnv* env,
        jobject self,
        jstring commitManager,
        jstring tellStore,
        jlong chunkCount,
        jlong chunkSize)
{
    auto cM = to_string(env, commitManager);
    auto tS = to_string(env, tellStore);
    ClientConfig config;
    config.commitManager = ClientConfig::parseCommitManager(cM);
    config.tellStore = ClientConfig::parseTellStore(tS);
    auto res = new ImplementationDetails(std::move(config));
    res->scanMemoryManager = std::move(res->clientManager.allocateScanMemory(size_t(chunkCount), size_t(chunkSize)));
    return reinterpret_cast<jlong>(res);
}

void Java_ch_ethz_tell_ClientManager_shutdown(JNIEnv* env, jobject, jlong ptr) {
    delete reinterpret_cast<ImplementationDetails*>(ptr);
}

/* getters and setters for configuration */

void Java_ch_ethz_tell_ClientManager_setMaxPendingResponsesImpl(JNIEnv*, jobject, jlong obj, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(obj);
    o->mConfig.maxPendingResponses = value;
}

jlong Java_ch_ethz_tell_ClientManager_getMaxPendingResponsesImpl(JNIEnv*, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->mConfig.maxPendingResponses;
}

void Java_ch_ethz_tell_ClientManager_setNumNetworkThreadsImpl(JNIEnv *, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->mConfig.numNetworkThreads = value;
}

jlong Java_ch_ethz_tell_ClientManager_getNumNetworkThreadsImpl(JNIEnv *, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->mConfig.numNetworkThreads;
}

void Java_ch_ethz_tell_ClientManager_setReceiveBufferCountImpl(JNIEnv *, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->mConfig.infinibandConfig.receiveBufferCount = value;
}

jlong Java_ch_ethz_tell_ClientManager_getReceiveBufferCountImpl(JNIEnv *, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->mConfig.infinibandConfig.receiveBufferCount;
}

void Java_ch_ethz_tell_ClientManager_setSendBufferCountImpl(JNIEnv *, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->mConfig.infinibandConfig.sendBufferCount = value;
}

jlong Java_ch_ethz_tell_ClientManager_getSendBufferCountImpl(JNIEnv *, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->mConfig.infinibandConfig.sendBufferCount;
}

void Java_ch_ethz_tell_ClientManager_setBufferLengthImpl(JNIEnv *, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->mConfig.infinibandConfig.bufferLength = value;
}

jlong Java_ch_ethz_tell_ClientManager_getBufferLengthImpl(JNIEnv *, jobject self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->mConfig.infinibandConfig.bufferLength;
}

void Java_ch_ethz_tell_ClientManager_setCompletionQueueLengthImpl(JNIEnv *, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->mConfig.infinibandConfig.completionQueueLength = value;
}

jlong Java_ch_ethz_tell_ClientManager_getCompletionQueueLengthImpl(JNIEnv *, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->mConfig.infinibandConfig.completionQueueLength;
}

void Java_ch_ethz_tell_ClientManager_setSendQueueLengthImpl(JNIEnv*, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->mConfig.infinibandConfig.sendQueueLength = value;
}

jlong Java_ch_ethz_tell_ClientManager_getSendQueueLengthImpl(JNIEnv*, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->mConfig.infinibandConfig.sendQueueLength;
}


