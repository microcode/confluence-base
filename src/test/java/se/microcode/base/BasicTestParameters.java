package se.microcode.base;

public class BasicTestParameters
{
    public String test1;
    public int test2;
    public String[] test3;
    public int[] test4;
    public BasicTestEnum test5;
    public BasicTestEnum[] test6;
    public boolean test7;

    @Argument(name = "test8", source = ArgumentSource.EXTERNAL)
    public String test8;
}
