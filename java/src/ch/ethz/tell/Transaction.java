package ch.ethz.tell;

public class Transaction {
    private long mImpl;
    
    private static native long startTx(long clientManager);

    private Transaction(long impl) {
        mImpl = impl;
    }

    public static Transaction startTransaction(ClientManager manager) {
        return new Transaction(startTx(manager.getClientManagerPtr()));
    }

    private static native boolean commit(long impl);
    public final boolean commit() {
        return commit(mImpl);
    }

    private static native void abort(long impl);
    public final void abort() {
        abort(mImpl);
    }

}
