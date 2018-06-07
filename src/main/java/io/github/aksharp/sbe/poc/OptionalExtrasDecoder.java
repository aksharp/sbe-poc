/* Generated SBE (Simple Binary Encoding) message codec */
package io.github.aksharp.sbe.poc;

import org.agrona.DirectBuffer;

@SuppressWarnings("all")
public class OptionalExtrasDecoder
{
    public static final int ENCODED_LENGTH = 1;
    private DirectBuffer buffer;
    private int offset;

    public OptionalExtrasDecoder wrap(final DirectBuffer buffer, final int offset)
    {
        this.buffer = buffer;
        this.offset = offset;

        return this;
    }

    public DirectBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public int encodedLength()
    {
        return ENCODED_LENGTH;
    }

    public boolean sunRoof()
    {
        return 0 != (buffer.getByte(offset) & (1 << 0));
    }

    public static boolean sunRoof(final byte value)
    {
        return 0 != (value & (1 << 0));
    }

    public boolean sportsPack()
    {
        return 0 != (buffer.getByte(offset) & (1 << 1));
    }

    public static boolean sportsPack(final byte value)
    {
        return 0 != (value & (1 << 1));
    }

    public boolean cruiseControl()
    {
        return 0 != (buffer.getByte(offset) & (1 << 2));
    }

    public static boolean cruiseControl(final byte value)
    {
        return 0 != (value & (1 << 2));
    }

    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        builder.append('{');
        boolean atLeastOne = false;
        if (sunRoof())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("sunRoof");
            atLeastOne = true;
        }
        if (sportsPack())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("sportsPack");
            atLeastOne = true;
        }
        if (cruiseControl())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("cruiseControl");
            atLeastOne = true;
        }
        builder.append('}');

        return builder;
    }
}
