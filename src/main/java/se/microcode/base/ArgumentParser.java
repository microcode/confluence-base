package se.microcode.base;

import aQute.lib.osgi.Clazz;
import com.opensymphony.webwork.ServletActionContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Map;

public class ArgumentParser
{
    public static Object parse(Object args, Map<String, String> params) throws ClassCastException
    {
        HttpServletRequest request = ServletActionContext.getRequest();

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
                name = f.getName().toLowerCase();
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

                case SERVLET_REQUEST:
                {
                    if (request != null)
                    {
                        value = request.getParameter(name);
                    }
                }
                break;
            }

            if (value == null)
            {
                continue;
            }

            Type type = f.getType();

            try
            {
                if (type == String.class)
                {
                    f.set(args, value);
                }
                else if (type == int.class)
                {
                    f.setInt(args, Integer.valueOf(value));
                }
                else if (type == String[].class)
                {
                    String values[] = value.split(",");
                    f.set(args, values);
                }
                else if (type == boolean.class)
                {
                    f.setBoolean(args, "true".equalsIgnoreCase(value));
                }
                else if (f.getType().isEnum())
                {
                    f.set(args, Enum.valueOf((Class<Enum>)f.getType(), value.toUpperCase()));
                }
                else
                {
                    throw new ClassCastException("Invalid argument declaration");
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
