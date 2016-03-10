package se.microcode.base;

import com.opensymphony.webwork.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

public class ArgumentParser
{
    private static final HashMap<Class<?>,Class<?>> classes;
    static
    {
        classes = new HashMap<Class<?>, Class<?>>();
        classes.put(boolean.class, Boolean.class);
        classes.put(byte.class, Byte.class);
        classes.put(short.class, Short.class);
        classes.put(char.class, Character.class);
        classes.put(int.class, Integer.class);
        classes.put(long.class, Long.class);
        classes.put(float.class, Float.class);
        classes.put(double.class, Double.class);
    }

    public static Object parse(Object args, Map<String, String> params, ArgumentResolver resolver) throws ClassCastException
    {
        for (Field f : args.getClass().getFields())
        {
            Argument argument = (Argument)f.getAnnotation(Argument.class);
            String name;
            ArgumentSource source;

            if (argument != null)
            {
                name = argument.name();
                source = argument.source();
            }
            else
            {
                name = f.getName();
                source = ArgumentSource.PARAMETERS;
            }

            String value = null;

            switch (source)
            {
                case PARAMETERS:
                {
                    value = (String)params.get(name);
                }
                break;

                case EXTERNAL:
                {
                    if (resolver != null)
                    {
                        value = resolver.get(name);
                    }
                }
                break;
            }

            if (value == null)
            {
                continue;
            }

            Class<?> c = f.getType();

            try
            {
                if (c.isEnum())
                {
                    f.set(args, Enum.valueOf((Class<Enum>)c, value.toUpperCase()));
                }
                else if (c.isArray())
                {
                    String values[] = value.split(",");
                    Class<?> ic = c.getComponentType();
                    Class<?> wrapper = classes.get(ic);

                    if (wrapper != null)
                    {
                        try
                        {
                            Method m = wrapper.getMethod("valueOf", new Class[] { String.class });
                            Object arr = Array.newInstance(ic, values.length);
                            for (int i = 0, n = values.length; i != n; ++i)
                            {
                                Object o = m.invoke(null, new Object[] { values[i] });
                                Array.set(arr, i, o);
                            }
                            f.set(args, arr);
                        }
                        catch (NoSuchMethodException e)
                        {
                            int j = 0;
                        }
                        catch (InvocationTargetException e)
                        {
                            int j = 0;
                        }
                    }
                    else if (ic.isEnum())
                    {
                        Object arr = Array.newInstance(ic, values.length);
                        for (int i = 0, n = values.length; i != n; ++i)
                        {
                            Object o = Enum.valueOf((Class<Enum>)ic, values[i].toUpperCase());
                            Array.set(arr, i, o);
                        }
                        f.set(args, arr);
                    }
                    else
                    {
                        Object arr = Array.newInstance(ic, values.length);
                        for (int i = 0, n = values.length; i != n; ++i)
                        {
                            Array.set(arr, i, values[i]);
                        }
                        f.set(args, arr);
                    }
                }
                else
                {
                    Class<?> wrapper = classes.get(c);
                    if (wrapper != null)
                    {
                        try
                        {
                            Method m = wrapper.getMethod("valueOf", new Class[] { String.class });
                            Object o = m.invoke(null, new Object[] { value });
                            f.set(args, o);
                        }
                        catch (InvocationTargetException e)
                        {
                            f.set(args, value);
                        }
                        catch (NoSuchMethodException e)
                        {
                            f.set(args, value);
                        }
                    }
                    else
                    {
                        f.set(args, value);
                    }
                }
            }
            catch (IllegalAccessException e)
            {
                continue;
            }
        }

        return args;
    }
}
