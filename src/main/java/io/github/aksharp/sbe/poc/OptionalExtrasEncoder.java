/* Generated SBE (Simple Binary Encoding) message codec */
package io.github.aksharp.sbe.poc;

import org.agrona.MutableDirectBuffer;

@SuppressWarnings("all")
public class OptionalExtrasEncoder
{
    public static final int ENCODED_LENGTH = 1;
    private MutableDirectBuffer buffer;
    private int offset;

    public OptionalExtrasEncoder wrap(final MutableDirectBuffer buffer, final int offset)
    {
        this.buffer = buffer;
        this.offset = offset;

        return this;
    }

    public MutableDirectBuffer buffer()
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

    public OptionalExtrasEncoder clear()
    {
        buffer.putByte(offset, (byte)(short)0);
        return this;
    }

    public OptionalExtrasEncoder sunRoof(final boolean value)
    {
        byte bits = buffer.getByte(offset);
        bits = (byte)(value ? bits | (1 << 0) : bits & ~(1 << 0));
        buffer.putByte(offset, bits);
        return this;
    }

    public static byte sunRoof(final byte bits, final boolean value)
    {
        return (byte)(value ? bits | (1 << 0) : bits & ~(1 << 0));
    }

    public OptionalExtrasEncoder sportsPack(final boolean value)
    {
        byte bits = buffer.getByte(offset);
        bits = (byte)(value ? bits | (1 << 1) : bits & ~(1 << 1));
        buffer.putByte(offset, bits);
        return this;
    }

    public static byte sportsPack(final byte bits, final boolean value)
    {
        return (byte)(value ? bits | (1 << 1) : bits & ~(1 << 1));
    }

    public OptionalExtrasEncoder cruiseControl(final boolean value)
    {
        byte bits = buffer.getByte(offset);
        bits = (byte)(value ? bits | (1 << 2) : bits & ~(1 << 2));
        buffer.putByte(offset, bits);
        return this;
    }

    public static byte cruiseControl(final byte bits, final boolean value)
    {
        return (byte)(value ? bits | (1 << 2) : bits & ~(1 << 2));
    }
}
