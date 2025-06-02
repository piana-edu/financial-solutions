package ir.piana.financial.solutions.isoserver.channels;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOPackager;

import java.io.IOException;

public class BinaryChannel extends BaseChannel {
    public BinaryChannel(ISOPackager packager) throws IOException {
        super(packager);
    }

    @Override
    protected int getHeaderLength() {
        return 2; // 2-byte length header
    }

    @Override
    public byte[] getHeader() {
        return null; // No extra message header
    }

    @Override
    protected void sendMessageLength(int len) throws IOException {
        // 2-byte big-endian
        serverOut.write((len >> 8) & 0xFF);
        serverOut.write(len & 0xFF);
    }

    @Override
    protected int getMessageLength() throws IOException {
        int high = serverIn.read();
        int low = serverIn.read();
        return (high << 8) | low;
    }
}
