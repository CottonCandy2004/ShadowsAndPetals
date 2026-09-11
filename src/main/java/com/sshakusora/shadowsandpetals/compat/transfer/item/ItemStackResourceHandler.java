package com.sshakusora.shadowsandpetals.compat.transfer.item;

import com.sshakusora.shadowsandpetals.compat.transfer.ResourceHandler;
import com.sshakusora.shadowsandpetals.compat.transfer.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

public abstract class ItemStackResourceHandler implements ResourceHandler<ItemResource> {
    protected abstract ItemStack getStack();
    protected abstract void setStack(ItemStack stack);
    protected abstract boolean isValid(ItemResource resource);
    protected abstract int getCapacity(ItemResource resource);
    @Override public int size(){return 1;}
    @Override public ItemResource getResource(int index){return index==0?ItemResource.of(getStack()):ItemResource.EMPTY;}
    @Override public long getAmountAsLong(int index){return index==0?getStack().getCount():0;}
    @Override public long getCapacityAsLong(int index, ItemResource resource){return index==0?getCapacity(resource):0;}
    @Override public boolean isValid(int index, ItemResource resource){return index==0 && isValid(resource);}
    @Override public int insert(int index, ItemResource resource,int amount,TransactionContext tx){return 0;}
    @Override public int extract(int index, ItemResource resource,int amount,TransactionContext tx){return 0;}
}