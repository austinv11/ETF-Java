package com.austinv11.etf.test;

import com.austinv11.etf.ETFConfig;
import com.austinv11.etf.common.TermTypes;
import com.austinv11.etf.parsing.ETFParser;
import com.austinv11.etf.util.ETFConstants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.junit.Assert;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ETFTester {

    //These test cases were taken from: https://github.com/ccubed/Earl/blob/master/unit_tests.py
    public static final char[] SMALL_INT = {131,97,10};
    public static final char[] BIG_INT = {131, 98, 0, 0, 4, 176};
    public static final char[] FLOAT = {131,70,64,9,33,250,252,139,0,122};
    public static final char[] MAP = {131,116,0,0,0,1,109,0,0,0,1,100,97,10};
    public static final char[] LIST = {131,108,0,0,0,3,97,1,97,2,97,3,106};
    public static final char[] NIL = {131, 106};
    public static final byte[] TEST = {-125, 80, 0, 0, 3, -46, 120, -100, -115, -109, -67, -114, -45, 64, 16, -57, 55, 38, 9, -71, -100, -94, 67, 64, 1, -35, 21, -76, 65, -10, 38, -2, -126, -26, 46, 58, 42, 68, 67, -127, 68, 101, -115, 119, -57, -55, -126, 99, 91, -69, -101, -64, 73, 20, -89, 123, 5, 30, -126, 71, -96, 66, 8, 120, 10, -34, -128, 55, -96, -96, 97, -42, 36, 40, -57, -91, 64, -78, -20, -39, -113, -7, -49, -1, -73, 59, -74, -116, -79, -82, 100, 29, 105, 41, 24, 74, -42, -49, -84, 6, -127, 37, -115, -68, 37, -67, -18, 73, 101, 68, -83, -27, 120, 14, 22, -33, -64, -7, -72, -95, 56, 24, 7, -119, 91, -68, -65, 93, 52, 104, -116, -86, 43, -77, 93, -11, 95, -111, -46, 124, -91, 74, 105, -100, 82, -57, -119, 123, -110, 121, 74, 86, 3, -58, 78, 46, -94, -37, 119, -57, 29, -55, 14, 87, 21, -84, 65, -107, -112, -105, 40, 89, -41, -22, 21, 82, -30, 65, -93, -47, 96, 37, -48, -48, -32, 86, -93, -43, -102, 74, 103, 98, 1, 85, -123, 101, -85, -25, 57, -67, -98, 115, -80, 81, 100, 23, -17, 31, 125, 59, 107, 13, 15, -107, -55, 54, 57, 27, 73, 55, 123, 84, -126, -79, -39, -110, 108, -62, 28, -77, 54, -55, 99, -105, -97, 127, 126, -4, -43, 38, 29, 104, 20, -86, 81, 88, 89, -89, -36, 117, 83, 125, 114, 102, 65, -69, -16, 56, -56, 5, -25, 81, 92, 20, 60, -119, -126, -119, -113, -100, 30, -50, 97, 18, -5, -31, -92, 0, -52, -35, -90, -111, 59, 11, -83, -106, -86, 2, 91, -73, 105, -35, -44, -113, -30, 29, -109, -51, -55, -47, -113, 59, -29, -114, -101, 25, -84, 12, -22, 10, -106, -83, -73, -2, -23, -54, 88, 85, -75, 41, -10, -68, -63, 63, 1, -66, -75, -41, 40, 103, -17, 62, 109, 12, -1, 55, 37, -99, -10, -105, -3, -108, -67, 127, 41, -89, 1, 2, 15, 32, -49, -111, 67, 42, -45, 48, -15, 57, -9, -91, 63, 5, 31, -123, -128, -76, 69, -71, -111, -41, 118, -89, -32, 30, -24, -124, 71, -63, -82, 103, 118, -7, -96, 121, -31, 93, -121, 126, 90, -37, 89, 109, -81, 67, -45, -99, -113, 52, -106, 96, 93, 63, 45, 84, -29, -102, 96, -72, -23, 47, 66, 106, -99, -54, 34, 77, -91, -96, -85, -32, 73, -110, -124, 62, -124, -120, 73, 62, -107, -45, 73, -112, -89, 34, -116, 64, -78, -98, 89, -128, -106, -81, -23, -110, -87, -51, -70, -82, -80, 67, 30, -56, 43, -64, 105, -110, -8, 24, -117, 32, -116, 81, 20, 34, 65, 46, -117, 40, 64, -6, -122, 126, -63, 37, -112, -52, 14, -82, -36, 7, 27, -5, -55, -124, -118, -31, -110, -102, -104, 118, 87, -18, -67, 5, -1, -6, -3, -125, 122, 76, 77, 127, -72, 44, 32, -61, -86, -19, -15, -65, 90, 87, -50, -30, -26, 51, -3, -16, 120, -26, 42, 13, -42, -88, 85, -95, 118, 54, -114, -36, -58, -52, -96, -91, 14, -103, 27, 7, -63, -24, 111, 93, 67, -113, 10, -43, 13, -72, -127, 1, 98, -20, 80, 118, -17, -7, -109, -45, -77, -105, -65, 1, 87, -67, 12};
    public static final ETFConfig CONFIG = new ETFConfig()
            .setIncludeHeader(true)
            .setCompression(false)
            .setIncludeDistributionHeader(false)
            .setBert(false)
            .setVersion(ETFConstants.VERSION)
            .setLoqui(true);

    private static void printParser(ETFParser parser) {
        while (!parser.isFinished()) {
            if (parser.peek() == TermTypes.MAP_EXT) {
                Map map = parser.nextMap();
                map.forEach((k, v) -> {
                    printParser(CONFIG.createParser(CONFIG.createWriter(true).write(k).toBytes(), true));
                    printParser(CONFIG.createParser(CONFIG.createWriter(true).write(v).toBytes(), true));
                });
            } else {
                System.out.println(parser.peek());
                Object obj = parser.next();
                if (obj instanceof byte[]) {
                    System.out.println(new String((byte[]) obj));
                } else {
                    System.out.println(obj);
                }
                System.out.println(Arrays.toString(CONFIG.createWriter(true).write(obj).toBytes()));
            }
        }
    }
    
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ETFParser parser = CONFIG.setIncludeHeader(true).createParser(TEST);
        printParser(parser);
        
        readEtf(ETFTester.class.getResourceAsStream("/test.etf"));

        readJson(ETFTester.class.getResourceAsStream("/test.json"));

        readGson(ETFTester.class.getResourceAsStream("/test.json"));

        testCase(SMALL_INT, 10, "small int");
        testCase(BIG_INT, 1200, "big int");
        testCase(FLOAT, 3.141592, "float");
        Map<String, Integer> map = new HashMap<>();
        map.put("d", 10);
        testCase(MAP, map, "map");
        testCase(LIST, Arrays.asList(1,2,3), "list");
        testCase(NIL, null, "nil");
    }

    private static byte[] charsToBytes(char[] chars) {
        byte[] array = new byte[chars.length];
        for (int i = 0; i < chars.length; i++)
            array[i] = (byte) chars[i];
        return array;
    }

    private static void testCase(char[] etf, Object expected, String message) {
        Object next = CONFIG.createParser(charsToBytes(etf), true).next();
        System.out.printf("Expected: %s, Parsed: %s%n", next == null ? "null" : expected.toString(), next == null ? "null" : next.toString());
        Assert.assertTrue(message, next == null ? expected == null : next.equals(expected));
    }

    private static void readEtf(InputStream is) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        byte[] dataArray = buffer.toByteArray();

        System.out.println("ETF: ");
        long init = System.currentTimeMillis();
        System.out.println(CONFIG.createParser(dataArray, true).nextMap().toString());
        System.out.println(System.currentTimeMillis() - init);
    }

    private static void readJson(InputStream is2) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is2));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("org.json: ");
        long init2 = System.currentTimeMillis();
        System.out.println(new JSONObject(sb.toString()).toMap().toString());
        System.out.println(System.currentTimeMillis() - init2);
    }

    private static void readGson(InputStream inputStream) {
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Gson gson = new Gson();

        System.out.println("Gson (POGO): ");
        long init3 = System.currentTimeMillis();
        TestObject object = gson.fromJson(sb.toString(), TestObject.class);
        System.out.println(object.toString());
        System.out.println(System.currentTimeMillis() - init3);

        Gson gson2 = new Gson();
        System.out.println("Gson (manual parsing): ");
        long init2 = System.currentTimeMillis();
        JsonObject jo = gson2.fromJson(sb.toString(), JsonObject.class);
        System.out.println(jo.toString());
        System.out.println(System.currentTimeMillis() - init2);
    }

    private static class TestObject {

        public int op;
        public String s;
        public String t;
        public Event d;

        private static class Event {
            public String[] _trace;
            public int heartbeat_interval;

            @Override
            public String toString() {
                return String.format("{_trace=%s, heartbeat_interval=%d}", Arrays.toString(_trace), heartbeat_interval);
            }
        }

        @Override
        public String toString() {
            return String.format("{op=%d, s=%s, t=%s, d=%s}", op, s, t, d.toString());
        }
    }
}
