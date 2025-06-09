package ir.piana.financial.solutions.serverclientraw;

import ir.piana.financial.solutions.common.tools.NatsMsgBrokerService;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.ISO87BPackager;

import java.io.*;
import java.net.Socket;
import java.time.Duration;

public class PianaServerSocket implements Closeable {
    private long startTimeMillis;
    private long lastRequestTimeMillis;
    private Socket socket;
    private DataInputStream inputStream;
    private OutputStream outputStream;
    private String destinationSubject;
    private boolean bigEndian;
    private int socketTimeout = 60000;
    private int lenByteCount = 4;

    private PianaServerSocket(
            Socket socket, DataInputStream inputStream, OutputStream outputStream, String destinationSubject,
            boolean bigEndian) {
        this.bigEndian = bigEndian;
        this.startTimeMillis = System.currentTimeMillis();
        this.lastRequestTimeMillis = startTimeMillis;
        this.socket = socket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.destinationSubject = destinationSubject;
    }

    public static PianaServerSocket initializeSocket(
            Socket socket, String destinationSubject, boolean bigEndian, int readTimeoutMillis) throws IOException {
        socket.setSoTimeout(readTimeoutMillis);
        return new PianaServerSocket(socket,
                new DataInputStream(new BufferedInputStream(socket.getInputStream())),
                socket.getOutputStream(), destinationSubject, bigEndian);
    }

    public void handleSocketCommunication(NatsMsgBrokerService msgBrokerService) throws IOException {
        do {
            byte[] request = getRequest();
            try {
                ISOMsg msg = new ISOMsg();
                msg.setPackager(new ISO87BPackager());
                ISO87BPackager iso87BPackager = new ISO87BPackager();
                iso87BPackager.unpack(msg, request);
                System.out.println(msg);
            } catch (ISOException e) {
                throw new RuntimeException(e);
            }
            lastRequestTimeMillis = System.currentTimeMillis();
            byte[] response = msgBrokerService.requestReply(destinationSubject, request, Duration.ofSeconds(30));
            sendResponse(response);
        } while (true);
    }

    public byte[] getRequest() throws IOException {
        int len = readLen();
        byte[] bytes = readBytes(len);
        return bytes;
    }

    public void sendResponse(byte[] response) throws IOException {
        writeLen(response.length);
        writeBytes(response);
    }

    public int readLen() throws IOException {
        return bigEndian ? readLenBE() : readLenLE();
    }

    public void writeLen(int len) throws IOException {
        if (bigEndian)
            writeLenBE(len);
        else writeLenLE(len);
    }

    public int readLenLE() throws IOException {
        byte[] bytes = new byte[lenByteCount];
        int len = inputStream.read(bytes); // least significant byte

        for (byte aByte : bytes) {
            if (bytes[0] == -1)
                throw new EOFException("Stream ended before reading 2 bytes");
        }

        int tmp = 0;
        for (int i = len - 1; i >= 0; i--) {
            tmp |= bytes[i] << (8 * i);
        }

        return tmp;
    }

    public void writeLenLE(int len) throws IOException {
        byte[] lenBytes = new byte[]{
                (byte) (len >> 8 & 0xFF),
                (byte) (len & 0xFF)
        };
        outputStream.write(lenBytes);
    }

    public int readLenBE() throws IOException {
        byte[] bytes = new byte[lenByteCount];
        int len = inputStream.read(bytes);

        for (byte aByte : bytes) {
            if (bytes[0] == -1)
                throw new EOFException("Stream ended before reading 2 bytes");
        }

        return asciiBytesToInt(bytes);

        /*int tmp = 0;
        for (int i = 0; i < bytes.length; i++) {
            tmp |= bytes[i] << (8 * bytes.length - 1 - i);
        }

        return tmp;*/
    }

    public static int asciiBytesToInt(byte[] bytes) {
        int value = 0;
        for (byte b : bytes) {
            if (b < '0' || b > '9') {
                throw new IllegalArgumentException("Invalid ASCII digit: " + (char) b);
            }
            value = value * 10 + (b - '0');
        }
        return value;
    }

    public void writeLenBE(int len) throws IOException {
        byte[] lenBytes = new byte[]{
                (byte) (len & 0xFF),
                (byte) (len >> 8 & 0xFF)
        };
        outputStream.write(lenBytes);
    }

    public byte[] readBytes(int length) throws IOException {
        byte[] bytes = new byte[length];
        inputStream.readFully(bytes);
        return bytes;
    }

    public void writeBytes(byte[] bytes) throws IOException {
        outputStream.write(bytes);
    }

    @Override
    public void close() throws IOException {
        if (socket != null) {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            socket.close();
        }
    }
}
