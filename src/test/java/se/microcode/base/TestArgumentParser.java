package se.microcode.base;

import junit.framework.TestCase;
import se.microcode.base.ArgumentParser;
import sun.jvm.hotspot.debugger.cdbg.basic.BasicEnumType;

import java.util.HashMap;

public class TestArgumentParser extends TestCase
{
    public void testBasicTestParameters()
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("test1", "test1");
        params.put("test2", "2");
        params.put("test3", "test3,test3");
        params.put("test4", "1,2,3");
        params.put("test5", "basic3");
        params.put("test6", "basic1,basic2,basic3");
        params.put("test7", "true");

        BasicTestParameters args = (BasicTestParameters)ArgumentParser.parse(new BasicTestParameters(), params, null);

        assertEquals(args.test1, "test1");
        assertEquals(args.test2, 2);
        assertTrue(java.util.Arrays.equals(args.test3, new String[]{"test3", "test3"}));
        assertTrue(java.util.Arrays.equals(args.test4, new int[] { 1, 2, 3 }));
        assertEquals(args.test5, BasicTestEnum.BASIC3);
        assertTrue(java.util.Arrays.equals(args.test6, new BasicTestEnum[] { BasicTestEnum.BASIC1, BasicTestEnum.BASIC2, BasicTestEnum.BASIC3 }));
        assertEquals(args.test7, true);
    }

    public void testExternalResolver()
    {
        HashMap<String,String> params = new HashMap<String, String>();
        params.put("test1", "test1");
        params.put("test8", "wrong");

        ArgumentResolver resolver = new ArgumentResolver()
        {
            public String get(String name)
            {
                if ("test8".equalsIgnoreCase(name))
                {
                    return "test8";
                }
                return null;
            }
        };

        BasicTestParameters args = (BasicTestParameters)ArgumentParser.parse(new BasicTestParameters(), params, resolver);

        assertEquals(args.test1, "test1");
        assertEquals(args.test8, "test8");
    }
}