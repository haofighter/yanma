package com.szxb.buspay.util;

import android.os.Environment;
import android.text.TextUtils;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.MainActivity;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.util.rx.RxBus;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static java.lang.System.arraycopy;

/**
 * 作者：Tangren on 2018-07-17
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class HexUtil {

    /**
     * byte 2 hex
     *
     * @param data .
     * @return .
     */
    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();


    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte) ((value >> 8 * i) & 0xff);
        }
        return b;
    }

    /**
     * 检查是否有文件
     *
     * @param name .
     * @return .
     */

    public static boolean check(String name) {
        String path = Environment.getExternalStorageDirectory() + "/" + name;
        File file = new File(path);
        //文件是否存在
        return file.exists();
    }

    /**
     * 根据线路号下载更新线路文件
     *
     * @param lineName .
     * @return .
     */
    public static String fileName(String lineName) {
        try {
            String start = lineName.substring(0, 2);
            String end = lineName.substring(2, 4);
            int i = Integer.parseInt(start, 16);
            int i1 = Integer.parseInt(end, 16);
            return i + "," + i1 + ".json";
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * 合并byte数组
     *
     * @param datas .
     * @return .
     */
    public static byte[] mergeByte(byte[]... datas) {
        int length = 0;
        byte[] endData = new byte[2048];
        for (byte[] data : datas) {
            arraycopy(data, 0, endData, length, data.length);
            length += data.length;
        }
        byte[] data = new byte[length];
        arraycopy(endData, 0, data, 0, length);
        return data;
    }


    /**
     * 10进制串转为BCD码<br/>
     *
     * @param data 10进制串
     * @return byte[] BCD码
     */
    public static byte[] str2Bcd(String data) {
        if (data.length() == 0) {
            return new byte[0];
        }

        String str = data;
        // 奇数个数字需左补零
        if (str.length() % 2 != 0) {
            str = "0" + str;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        char[] cs = str.toCharArray();
        for (int i = 0; i < cs.length; i += 2) {
            int high = cs[i] - 48;
            int low = cs[i + 1] - 48;
            baos.write(high << 4 | low);
        }
        return baos.toByteArray();
    }

    /**
     * byte[]bcd 转str
     *
     * @param bytes .
     * @return .
     */
    public static String bcd2Str(byte[] bytes) {
        if (bytes.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            int h = ((aByte & 0xff) >> 4) + 48;
            sb.append((char) h);
            int l = (aByte & 0x0f) + 48;
            sb.append((char) l);
        }
        return sb.toString();
    }

    /**
     * 十六进制串转化为byte数组
     *
     * @return the array of byte
     */
    public static byte[] hex2byte(String hex)
            throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = Integer.valueOf(byteint).byteValue();
        }
        return b;
    }

    public static void parseLine(LineInfoEntity onLineInfo) {
        parseLine(onLineInfo, null);
    }

    public static void parseLine(LineInfoEntity onLineInfo, String busNo) {
        BusApp.getPosManager().setChinese_name(onLineInfo.getChinese_name());
        BusApp.getPosManager().setBasePrice(Util.string2Int(onLineInfo.getFixed_price()));
        BusApp.getPosManager().setLineName(onLineInfo.getChinese_name());
        BusApp.getPosManager().setCoefficent(onLineInfo.getCoefficient());
        String lineNo = onLineInfo.getLine();
        BusApp.getPosManager().setLineNo(lineNo);
        if (lineNo.length() >= 2) {
            BusApp.getPosManager().setUnitno(onLineInfo.getLine().substring(0, 2));
        }
        if (busNo != null) {
            BusApp.getPosManager().setBusNo(busNo);
        }
        if (AppUtil.isForeground(MainActivity.class.getName())) {
            RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.REFRESH_VIEW));
        }
    }

    /**
     * @param version ftp黑名单版本
     * @return 是否需要更新 true 更新，否则不更新
     */
    public static boolean checkBlackVersion(String version) {
        String lastBlackVersion = BusApp.getPosManager().getBlackVersion();
        return !TextUtils.equals(lastBlackVersion, version);
    }
}
