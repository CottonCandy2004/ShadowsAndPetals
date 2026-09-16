package com.sshakusora.shadowsandpetals.compat.transfer.transaction;

import java.util.ArrayList;
import java.util.List;

public final class Transaction implements TransactionContext, AutoCloseable {
    private final List<Runnable> rollbackActions = new ArrayList<>();
    private boolean committed;
    private boolean closed;
    private Transaction() {}
    public static Transaction openRoot() { return new Transaction(); }
    @Override
    public void addRollback(Runnable action) {
        if (!closed && !committed && action != null) {
            rollbackActions.add(action);
        }
    }
    public void commit() {
        if (!closed) {
            committed = true;
            rollbackActions.clear();
        }
    }
    public boolean isCommitted() { return committed; }
    @Override
    public void close() {
        if (closed) {
            return;
        }
        if (!committed) {
            for (int index = rollbackActions.size() - 1; index >= 0; index--) {
                rollbackActions.get(index).run();
            }
            rollbackActions.clear();
        }
        closed = true;
    }
}
