package com.sshakusora.shadowsandpetals.compat.transfer.transaction;

public final class Transaction implements TransactionContext, AutoCloseable {
    private boolean committed;
    private Transaction() {}
    public static Transaction openRoot() { return new Transaction(); }
    public void commit() { committed = true; }
    public boolean isCommitted() { return committed; }
    @Override public void close() {}
}