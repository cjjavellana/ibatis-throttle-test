package com.cjavellana;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

import com.ibatis.common.util.SampleType;
import com.ibatis.common.util.ThrottledPool;

/**
 * Created by cjavellana on 18/7/14.
 */
public class IbatisThrottledPoolTest {

    private static final int POOL_SIZE = 5;
    private final Log LOGGER = LogFactory.getLog(IbatisThrottledPoolTest.class);

    @Test
    public void testThrottledPool() {
        ThrottledPool pool = new ThrottledPool(SampleType.class, POOL_SIZE);
        try {
        	List<Thread> threadList = new ArrayList<Thread>();
            for (int i = 0; i < 500; i++) {
                LOGGER.debug(String.format("Creating Thread %s", i));
                Thread t = new Thread(new Transaction(pool, i));
                t.start();
                threadList.add(t);
            }
            
            //Wait for threads to finish
            for (Thread t : threadList) {
            	t.join();
            }
            
        } catch (Exception e) {
            LOGGER.error("An error has occurred", e);
        }

    }

    class Transaction implements Runnable {
        private final Log LOGGER = LogFactory.getLog(Transaction.class);
        private ThrottledPool p;
        private int id;

        public Transaction(ThrottledPool p, int id) {
            this.p = p;
            this.id = id;
        }

        public void run() {

            Object o = p.pop();
            SampleType type = (SampleType) o;
            LOGGER.debug(String.format("Popped Id: %s", type.getId()));

            try {
                Thread.sleep(700);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            p.push(type);

            LOGGER.debug(String.format("This Thread is Finishing: %s", id));
        }

    }
}
