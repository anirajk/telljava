#include <ch_ethz_tell_ClientManager.h>
#include <tellstore/ClientConfig.hpp>
#include <tellstore/ClientManager.hpp>

using namespace tell::store;

namespace {

struct ImplementationDetails {
    ImplementationDetails() : clientManager(config) {}
    tell::store::ClientConfig config;
    tell::store::ClientManager<void> clientManager;
};

} // impl

crossbow::string to_string(JNIEnv* env, jstring str) {
    auto ptr = env->GetStringUTFChars(str, nullptr);
    crossbow::string res(ptr, env->GetStringUTFLength(str));
    env->ReleaseStringUTFChars(str, ptr);
    return ptr;
}

jlong Java_ch_ethz_tell_ClientManager_getClientManagerPtr(JNIEnv*, jclass, jlong impl) {
    auto o = reinterpret_cast<ImplementationDetails*>(impl);
    return reinterpret_cast<jlong>(&o->clientManager);
}

jlong Java_ch_ethz_tell_ClientManager_init(JNIEnv* env, jobject self, jstring commitManager, jstring tellStore) {
    auto res = new ImplementationDetails();
    auto cM = to_string(env, commitManager);
    auto tS = to_string(env, tellStore);
    res->config.commitManager = ClientConfig::parseCommitManager(cM);
    res->config.tellStore = ClientConfig::parseTellStore(tS);
    return reinterpret_cast<jlong>(res);
}

void Java_ch_ethz_tell_ClientManager_shutdown(JNIEnv* env, jobject, jlong ptr) {
    delete reinterpret_cast<ImplementationDetails*>(ptr);
}

/* getters and setters for configuration */

void Java_ch_ethz_tell_ClientManager_setMaxPendingResponsesImpl(JNIEnv*, jobject, jlong obj, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(obj);
    o->config.maxPendingResponses = value;
}

jlong Java_ch_ethz_tell_ClientManager_getMaxPendingResponsesImpl(JNIEnv*, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->config.maxPendingResponses;
}

void Java_ch_ethz_tell_ClientManager_setNumNetworkThreadsImpl(JNIEnv *, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->config.numNetworkThreads = value;
}

jlong Java_ch_ethz_tell_ClientManager_getNumNetworkThreadsImpl(JNIEnv *, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->config.numNetworkThreads;
}

void Java_ch_ethz_tell_ClientManager_setReceiveBufferCountImpl(JNIEnv *, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->config.infinibandConfig.receiveBufferCount = value;
}

jlong Java_ch_ethz_tell_ClientManager_getReceiveBufferCountImpl(JNIEnv *, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->config.infinibandConfig.receiveBufferCount;
}

void Java_ch_ethz_tell_ClientManager_setSendBufferCountImpl(JNIEnv *, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->config.infinibandConfig.sendBufferCount = value;
}

jlong Java_ch_ethz_tell_ClientManager_getSendBufferCountImpl(JNIEnv *, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->config.infinibandConfig.sendBufferCount;
}

void Java_ch_ethz_tell_ClientManager_setBufferLengthImpl(JNIEnv *, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->config.infinibandConfig.bufferLength = value;
}

jlong Java_ch_ethz_tell_ClientManager_getBufferLengthImpl(JNIEnv *, jobject self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->config.infinibandConfig.bufferLength;
}

void Java_ch_ethz_tell_ClientManager_setCompletionQueueLengthImpl(JNIEnv *, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->config.infinibandConfig.completionQueueLength = value;
}

jlong Java_ch_ethz_tell_ClientManager_getCompletionQueueLengthImpl(JNIEnv *, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->config.infinibandConfig.completionQueueLength;
}

void Java_ch_ethz_tell_ClientManager_setSendQueueLengthImpl(JNIEnv*, jobject, jlong self, jlong value) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    o->config.infinibandConfig.sendQueueLength = value;
}

jlong Java_ch_ethz_tell_ClientManager_getSendQueueLengthImpl(JNIEnv*, jobject, jlong self) {
    auto o = reinterpret_cast<ImplementationDetails*>(self);
    return o->config.infinibandConfig.sendQueueLength;
}


