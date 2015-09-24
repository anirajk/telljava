package ch.ethz.tell;

public class ClientManager {
    private long mImplPtr;

    private static native long getClientManagerPtr(long implPtr);
    final long getClientManagerPtr() {
        return getClientManagerPtr(mImplPtr);
    }

    public ClientManager(String commitManager, String tellStore) {
        mImplPtr = init(commitManager, tellStore);
    }

    public final long getImplPtr() {
        return mImplPtr;
    }

    private native long init(String commitManager, String tellStore);

    private native void shutdown(long ptr);

    public final void close() {
        shutdown(mImplPtr);
    }

    private native void setMaxPendingResponsesImpl(long ptr, long value);
    private native long getMaxPendingResponsesImpl(long ptr);
    private native void setNumNetworkThreadsImpl(long ptr, long value);
    private native long getNumNetworkThreadsImpl(long ptr);
    private native void setReceiveBufferCountImpl(long ptr, long value);
    private native long getReceiveBufferCountImpl(long ptr);
    private native void setSendBufferCountImpl(long ptr, long value);
    private native long getSendBufferCountImpl(long ptr);
    private native void setBufferLengthImpl(long ptr, long value);
    private native long getBufferLengthImpl(long ptr);
    private native void setCompletionQueueLengthImpl(long ptr, long value);
    private native long getCompletionQueueLengthImpl(long ptr);
    private native void setSendQueueLengthImpl(long ptr, long value);
    private native long getSendQueueLengthImpl(long ptr);

    public final void setMaxPendingResponses(long value) {
        setMaxPendingResponsesImpl(mImplPtr, value);
    }

    public final long getMaxPendingResponses() {
        return getMaxPendingResponsesImpl(mImplPtr);
    }

    public final void setNumNetworkThreads(long value) {
        setNumNetworkThreadsImpl(mImplPtr, value);
    }

    public final long getNumNetworkThreads() {
        return getNumNetworkThreadsImpl(mImplPtr);
    }

    public final void setReceiveBufferCount(long value) {
        setReceiveBufferCountImpl(mImplPtr, value);
    }

    public final long getReceiveBufferCount() {
        return getReceiveBufferCountImpl(mImplPtr);
    }

    public final void setSendBufferCount(long value) {
        setSendBufferCountImpl(mImplPtr, value);
    }

    public final long getSendBufferCount() {
        return getSendBufferCountImpl(mImplPtr);
    }

    public final void setBufferLength(long value) {
        setBufferLengthImpl(mImplPtr, value);
    }

    public final long getBufferLength() {
        return getBufferLengthImpl(mImplPtr);
    }

    public final void setCompletionQueueLength(long value) {
        setCompletionQueueLengthImpl(mImplPtr, value);
    }

    public final long getCompletionQueueLength() {
        return getCompletionQueueLengthImpl(mImplPtr);
    }

    public final void setSendQueueLength(long value) {
        setSendQueueLengthImpl(mImplPtr, value);
    }

    public final long getSendQueueLength() {
        return getSendQueueLengthImpl(mImplPtr);
    }
}
