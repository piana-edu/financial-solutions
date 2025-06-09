package ir.piana.financial.solutions.isoserver.channels;

import org.jpos.iso.BaseChannel;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;

import java.io.IOException;

public class RawByteChannel extends BaseChannel {
    @Override
    protected void sendMessageLength(int len) throws IOException {
        serverOut.write(len >> 8);
        serverOut.write(len);
    }

    @Override
    protected int getMessageLength() throws IOException {
        int byte1 = serverIn.read();
        int byte2 = serverIn.read();
        return (byte1 << 8) | byte2;
    }

    @Override
    public void send(ISOMsg m) throws IOException {
        byte[] raw = (byte[]) m.getValue(); // we'll put raw bytes in ISOMsg
        sendMessageLength(raw.length);
        serverOut.write(raw);
        serverOut.flush();
    }

    @Override
    public ISOMsg receive() throws IOException, ISOException {
        int len = getMessageLength();
        byte[] buf = new byte[len];
        serverIn.readFully(buf);

        ISOMsg msg = new ISOMsg();         // Empty ISOMsg
        msg.setValue(buf);                 // Store raw data in it
        return msg;
    }
}
