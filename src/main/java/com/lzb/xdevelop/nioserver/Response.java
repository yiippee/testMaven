package com.lzb.xdevelop.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * <p>Title: ��Ӧ��</p>
 * <p>Description: ������ͻ��˷�������</p>
 * @author starboy
 * @version 1.0
 */

public class Response {
    private SocketChannel sc;

    public Response(SocketChannel sc) {
        this.sc = sc;
    }

    /**
     * ��ͻ���д����
     * @param data byte[]������Ӧ����
     */
    public void send(byte[] data) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        buffer.put(data, 0, data.length);
        buffer.flip();
        sc.write(buffer);
    }
}
