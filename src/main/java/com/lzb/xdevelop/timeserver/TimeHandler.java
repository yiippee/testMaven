package com.lzb.xdevelop.timeserver;

import com.lzb.xdevelop.nioserver.*;
import com.lzb.xdevelop.nioserver.event.*;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ʱ���ѯ������
 */
public class TimeHandler extends EventAdapter {
    public TimeHandler() {
    }

    public void onWrite(Request request, Response response) throws Exception {
        String command = new String(request.getDataInput());
        String time = null;
        Date date = new Date();

        // �жϲ�ѯ����
        if (command.equals("GB")) {
            // ���ĸ�ʽ
            DateFormat cnDate = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL, Locale.CHINA);
            time = cnDate.format(date);
        }
        else {
            // Ӣ�ĸ�ʽ
            DateFormat enDate = DateFormat.getDateTimeInstance(DateFormat.FULL,
                DateFormat.FULL, Locale.US);
            time = enDate.format(date);
        }

        response.send(time.getBytes());
    }
}
