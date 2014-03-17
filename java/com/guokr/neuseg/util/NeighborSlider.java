package com.guokr.neuseg.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import clojure.lang.ASeq;
import clojure.lang.IPersistentMap;
import clojure.lang.ISeq;
import clojure.lang.Obj;
import clojure.lang.PersistentList;

public class NeighborSlider extends ASeq {

    private static final long serialVersionUID = 2828087430289126339L;

    private static class RingPools {

        private Map<Integer, Queue<Object[]>> pools = new HashMap<Integer, Queue<Object[]>>();

        public synchronized void claim(Object[] ring) {
            int size = ring.length;
            Queue<Object[]> pool = pools.get(size);
            if (pool == null) {
                pool = new LinkedBlockingQueue<Object[]>();
                pools.put(size, pool);
            }

            if (!pool.contains(ring)) {
                pool.add(ring);
            }
        }

        public synchronized Object[] lease(int radius) {
            int ringsize = 2 * radius + 1;
            Queue<Object[]> pool = pools.get(ringsize);
            if (pool == null) {
                pool = new LinkedBlockingQueue<Object[]>();
                pools.put(ringsize, pool);
            }

            if (pool.size() > 0) {
                return pool.poll();
            } else {
                return new Object[ringsize];
            }
        }

    }

    private static RingPools RINGS = new RingPools();

    private int              radius;
    private Object           fill;
    private ISeq             current;

    private Object[]         ring;

    public NeighborSlider(int radius, Object fill, ISeq original) {
        this.radius = radius;
        this.fill = fill;
        this.current = original;

        this.ring = RINGS.lease(radius);
        for (int i = 0; i < radius; i++) {
            this.ring[i] = fill;
        }

        ISeq next = original == null ? PersistentList.EMPTY : original;
        for (int i = 0; i < radius + 1; i++) {
            Object head = next.first();
            this.ring[i] = head == null ? fill : head;
            next = next.next();
            next = next == null ? PersistentList.EMPTY : next;
        }
    }

    private NeighborSlider(int radius, Object fill, ISeq current, Object[] ring) {
        this.radius = radius;
        this.fill = fill;
        this.current = current;
        this.ring = ring;
    }

    @Override
    public Object first() {
        Object list = PersistentList.create(Arrays.asList(ring));
        return list;
    }

    @Override
    public ISeq next() {
        ISeq nextseq = current.next();

        int ringsize = 2 * radius + 1;
        Object[] nextring = RINGS.lease(radius);

        if (nextseq != null && !nextseq.equals(PersistentList.EMPTY)) {

            for (int i = 0; i < ringsize - 1; i++) {
                nextring[i] = ring[i + 1];
            }
            nextring[ringsize - 1] = nextseq.first();

            return new NeighborSlider(radius, fill, nextseq, nextring);

        } else {

            if (ring[radius + 1].equals(fill)) {

                return PersistentList.EMPTY;

            } else {

                for (int i = 0; i < ringsize - 1; i++) {
                    nextring[i] = ring[i + 1];
                }
                nextring[ringsize - 1] = fill;

                return new NeighborSlider(radius, fill, PersistentList.EMPTY, nextring);

            }

        }
    }

    @Override
    public Obj withMeta(IPersistentMap meta) {
        return this;
    }

    public void finalize() {
        RINGS.claim(ring);
        ring = null;
    }

}
