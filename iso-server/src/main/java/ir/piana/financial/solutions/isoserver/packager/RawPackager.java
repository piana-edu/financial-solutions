package ir.piana.financial.solutions.isoserver.packager;

import org.jpos.iso.*;

import java.util.Arrays;

public class RawPackager extends ISOBasePackager {
    @Override
    public byte[] pack(ISOComponent m) {
        try {
            return (byte[]) m.getValue(); // use m.set(value) before send
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public int unpack(ISOComponent m, byte[] b) throws ISOException {
        m.set(new ISOField(0, Arrays.toString(b))); // store raw
        return b.length;
    }
}
