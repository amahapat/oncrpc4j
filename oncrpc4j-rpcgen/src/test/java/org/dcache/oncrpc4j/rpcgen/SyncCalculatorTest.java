package org.dcache.oncrpc4j.rpcgen;

import org.dcache.xdr.RpcAuthTypeUnix;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SyncCalculatorTest extends AbstractCalculatorTest {

    @Test
    public void testSyncAdd() throws Exception {
        CalculationResult result = client.add_1(1, 2, 0, null, null);
        Assert.assertNotNull(result);
        Assert.assertEquals(3, result.getResult());
    }

    @Test
    public void testSyncAddSimple() throws Exception {
        Assert.assertEquals(3, client.addSimple_1(1, 2, 0, null, null));
    }

    @Test(expected = TimeoutException.class)
    public void testSyncAddTimeout() throws Exception {
        client.add_1(3, 4, CalculatorServerImpl.SLEEP_MILLIS / 10, TimeUnit.MILLISECONDS, null);
    }

    @Test
    public void testPerCallAuth() throws Exception {
        serverImpl.getMethodCalls(); //clear it just in case
        client.add_1(5, 6, 0, null, null);
        List<MethodCall> calls = serverImpl.getMethodCalls();
        Assert.assertEquals(1, calls.size());
        MethodCall call = calls.get(0);
        Assert.assertTrue(call.getPrincipalNames().isEmpty()); //because thats how the super class constructs the client
        client.add_1(7, 8, 0, null, new RpcAuthTypeUnix(1, 2, new int[]{3, 4}, 5, "bob"));
        calls = serverImpl.getMethodCalls();
        Assert.assertEquals(1, calls.size());
        call = calls.get(0);
        Assert.assertEquals(new HashSet<>(Arrays.asList("1", "2", "3", "4")), call.getPrincipalNames());
    }
}
