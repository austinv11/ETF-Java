package com.austinv11.etf.test;

import com.austinv11.etf.ETFConfig;
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
    public static final ETFConfig CONFIG = new ETFConfig()
            .setIncludeHeader(false)
            .setCompression(false)
            .setIncludeDistributionHeader(false)
            .setBert(false)
            .setVersion(ETFConstants.VERSION)
            .setLoqui(true);

    public static void main(String[] args) throws IOException, ClassNotFoundException {
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
