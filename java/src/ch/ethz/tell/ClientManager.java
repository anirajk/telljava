package ch.ethz.tell;

public class ClientManager {
    private long mImplPtr;

    public ClientManager(String commitManager, String tellStore) {
        mImplPtr = init(commitManager, tellStore);
    }

    private native long init(String commitManager, String tellStore);

    private native void shutdown(long ptr);

    public void close() {
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

    void setMaxPendingResponses(long value) {
        setMaxPendingResponsesImpl(mImplPtr, value);
    }

    long getMaxPendingResponses() {
        return getMaxPendingResponsesImpl(mImplPtr);
    }

    void setNumNetworkThreads(long value) {
        setNumNetworkThreadsImpl(mImplPtr, value);
    }

    long getNumNetworkThreads() {
        return getNumNetworkThreadsImpl(mImplPtr);
    }

    public void setReceiveBufferCount(long value) {
        setReceiveBufferCountImpl(mImplPtr, value);
    }

    public long getReceiveBufferCount() {
        return getReceiveBufferCountImpl(mImplPtr);
    }

    public void setSendBufferCount(long value) {
        setSendBufferCountImpl(mImplPtr, value);
    }

    public long getSendBufferCount() {
        return getSendBufferCountImpl(mImplPtr);
    }

    public void setBufferLength(long value) {
        setBufferLengthImpl(mImplPtr, value);
    }

    public long getBufferLength() {
        return getBufferLengthImpl(mImplPtr);
    }

    public void setCompletionQueueLength(long value) {
        setCompletionQueueLengthImpl(mImplPtr, value);
    }

    public long getCompletionQueueLength() {
        return getCompletionQueueLengthImpl(mImplPtr);
    }

    public void setSendQueueLength(long value) {
        setSendQueueLengthImpl(mImplPtr, value);
    }

    public long getSendQueueLength() {
        return getSendQueueLengthImpl(mImplPtr);
    }
}
