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
    public static final byte[] TEST = {-125, 116, 0, 0, 0, 4, 109, 0, 0, 0, 2, 111, 112, 98, 0, 0, 0, 2, 109, 0, 0, 0, 1, 115, 115, 3, 110, 105, 108, 109, 0, 0, 0, 1, 116, 115, 3, 110, 105, 108, 109, 0, 0, 0, 1, 100, 116, 0, 0, 0, 5, 109, 0, 0, 0, 8, 99, 111, 109, 112, 114, 101, 115, 115, 115, 4, 116, 114, 117, 101, 109, 0, 0, 0, 5, 115, 104, 97, 114, 100, 108, 0, 0, 0, 2, 98, 0, 0, 0, 0, 98, 0, 0, 0, 1, 106, 109, 0, 0, 0, 15, 108, 97, 114, 103, 101, 95, 116, 104, 114, 101, 115, 104, 111, 108, 100, 98, 0, 0, 0, -6, 109, 0, 0, 0, 10, 112, 114, 111, 112, 101, 114, 116, 105, 101, 115, 116, 0, 0, 0, 5, 109, 0, 0, 0, 7, 36, 100, 101, 118, 105, 99, 101, 109, 0, 0, 0, 9, 68, 105, 115, 99, 111, 114, 100, 52, 74, 109, 0, 0, 0, 17, 36, 114, 101, 102, 101, 114, 114, 105, 110, 103, 95, 100, 111, 109, 97, 105, 110, 109, 0, 0, 0, 0, 109, 0, 0, 0, 3, 36, 111, 115, 109, 0, 0, 0, 8, 77, 97, 99, 32, 79, 83, 32, 88, 109, 0, 0, 0, 8, 36, 98, 114, 111, 119, 115, 101, 114, 109, 0, 0, 0, 9, 68, 105, 115, 99, 111, 114, 100, 52, 74, 109, 0, 0, 0, 9, 36, 114, 101, 102, 101, 114, 114, 101, 114, 109, 0, 0, 0, 0, 109, 0, 0, 0, 5, 116, 111, 107, 101, 110, 109, 0, 0, 0, 63, 66, 111, 116, 32, 77, 84, 89, 119, 79, 68, 77, 52, 77, 122, 103, 120, 77, 122, 77, 49, 78, 106, 65, 53, 77, 122, 81, 48, 46, 67, 99, 56, 95, 54, 103, 46, 66, 104, 87, 102, 117, 85, 48, 101, 90, 109, 68, 98, 49, 74, 120, 87, 114, 83, 82, 102, 90, 114, 103, 83, 85, 82, 103};
    public static final ETFConfig CONFIG = new ETFConfig()
            .setIncludeHeader(false)
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
        ETFParser parser = CONFIG.setIncludeHeader(true).createParser(TEST, true);
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
