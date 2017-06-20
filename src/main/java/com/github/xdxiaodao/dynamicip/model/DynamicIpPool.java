package com.github.xdxiaodao.dynamicip.model;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created with dynamicip
 * 动态ip池
 * User zhangmuzhao
 * Date 2017/6/16
 * Time 18:18
 * Desc
 */
public class DynamicIpPool extends PriorityBlockingQueue<DynamicIp> {

    private Set<Pair<String, Integer>> ipAndPortSet = Collections.synchronizedSet(new HashSet<Pair<String, Integer>>());

    private DynamicIpPool() {

    }

    public static DynamicIpPool me() {
        return new DynamicIpPool();
    }

    /**
     * Inserts the specified element into this priority queue. As the queue is
     * unbounded this method will never block.
     *
     * @param dynamicIp the element to add
     * @throws ClassCastException   if the specified element cannot be compared
     *                              with elements currently in the priority queue according to the
     *                              priority queue's ordering
     * @throws NullPointerException if the specified element is null
     */
    @Override
    public void put(DynamicIp dynamicIp) {
        if (ipAndPortSet.contains(dynamicIp.getKey())) {
            return;
        }
        super.put(dynamicIp);
        ipAndPortSet.add(dynamicIp.getKey());
    }

    /**
     * Removes a single instance of the specified element from this queue,
     * if it is present.  More formally, removes an element {@code e} such
     * that {@code o.equals(e)}, if this queue contains one or more such
     * elements.  Returns {@code true} if and only if this queue contained
     * the specified element (or equivalently, if this queue changed as a
     * result of the call).
     *
     * @param o element to be removed from this queue, if present
     * @return <tt>true</tt> if this queue changed as a result of the call
     */
    @Override
    public boolean remove(Object o) {
        boolean result = super.remove(o);
        ipAndPortSet.remove(((DynamicIp)o).getKey());
        return result;
    }
}
