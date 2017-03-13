package com.austinv11.etf.test;

import com.austinv11.etf.ETFConfig;
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
    public static final byte[] COMPRESSION_TEST = {-125, 80, 0, 0, 3, -48, 120, -100, -115, -109, -67, -114, -45, 64, 16, -57, 55, 33, 9, -71, -100, -94, 67, -128, 4, -27, 21, -76, 65, -21, 117, -4, 5, -51, 93, 116, 84, -120, -122, 2, -119, -54, 26, 123, -57, -55, -126, 99, 91, -69, -101, -64, 73, 20, -89, 123, 5, 30, -126, 71, -96, 66, 8, 120, 10, -34, -128, 55, -96, -96, 97, -42, 36, 40, -57, -91, 64, -78, -20, -39, -113, -7, -49, -1, -73, 59, -74, -116, -79, -98, 100, 29, 105, 41, 24, 73, 54, 72, -83, -122, 28, 75, 26, 117, -105, -12, -70, 39, -107, -55, 107, 45, 39, 115, -80, -8, 6, -50, 39, 13, -59, -34, 36, 112, 107, -9, -73, 107, 6, -115, 81, 117, 101, -74, -117, -81, 72, 103, -66, 82, -91, 52, 78, -89, -29, -92, -69, -110, 117, -107, -84, -122, -116, -99, 92, -124, -73, -17, 78, 58, -110, 29, -82, 42, 88, -125, 42, 33, 43, 81, -78, -98, -43, 43, -92, -60, -125, 70, -93, -63, 42, 71, 67, -125, 91, -115, 86, 107, 42, -100, -26, 11, -88, 42, 44, 91, -67, -82, -45, -21, 59, 3, 27, 69, 118, -15, -2, -47, -73, -77, -42, -18, 72, -103, 116, -109, -77, -111, 116, -77, 71, 37, 24, -101, 46, -55, 37, -52, 49, 109, -109, -70, -20, -14, -13, -49, -113, -65, -38, -92, 3, -115, -71, 106, 20, 86, -42, 41, -9, -36, -44, -128, -100, 89, -48, 46, 60, -10, -78, 92, -120, 48, 42, 10, 17, -121, -98, -49, 81, -48, 35, 4, -8, 17, 15, -4, 2, 48, 115, -101, -58, -18, 40, -76, 90, -86, 10, 108, -35, -90, -11, 18, 30, 70, 59, 38, -101, -109, -93, 31, 119, 38, 29, 55, 51, 92, 25, -44, 21, 44, 91, 111, -125, -45, -107, -79, -86, 106, 83, -20, 121, -125, 127, 2, 124, 107, -81, 81, -50, -34, 125, -38, 24, -2, 111, 74, 58, -19, 47, -5, 41, -5, -1, 82, 78, 61, 4, -31, 65, -106, -95, -128, 68, 38, 65, -52, -123, -32, -110, 79, -127, 99, -98, 67, -46, -94, -36, -56, 106, -69, 83, 112, 15, 116, 44, 66, 111, -41, 51, -69, 124, -48, -68, -24, 94, -121, 126, 90, -37, 89, 109, -81, 67, -45, -99, -113, 53, -106, 96, 93, 59, 45, 84, -29, -102, 96, -76, 105, 47, 66, 106, -99, 6, 60, -29, 62, 34, 20, 113, -30, 121, -98, -100, 74, 63, -119, -24, -114, 56, 15, -117, 64, -120, -72, -112, -84, 111, 22, -96, -27, 107, -70, 100, 106, -77, -98, 43, -20, -112, -121, -14, 10, 112, 18, -57, 28, -93, -36, 11, 34, -52, -117, 60, 70, 33, -117, -48, 67, -6, 6, -68, 16, 18, 64, -18, -30, -54, 125, -80, 17, -113, 125, 42, -122, 75, 106, 98, -38, 93, -71, -9, 22, -4, -21, -9, 15, -22, 49, 53, -3, -31, -78, -128, 20, -85, -74, -57, -1, 106, 93, 57, -117, -101, -49, -12, -61, -29, -103, -85, 52, 92, -93, 86, -123, -38, -39, 56, 118, 27, 83, -125, -106, 58, 100, 110, 28, 4, -93, 127, 117, 13, 125, 42, 84, 55, -32, 6, 6, -120, -79, 67, -39, -3, -25, 79, 78, -49, 94, -2, 6, -117, -122, 11};
    public static final ETFConfig CONFIG = new ETFConfig()
            .setIncludeHeader(true)
            .setCompression(false)
            .setIncludeDistributionHeader(false)
            .setBert(false)
            .setVersion(ETFConstants.VERSION)
            .setLoqui(true);

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ETFParser parser = CONFIG.createParser(COMPRESSION_TEST);
        System.out.println(parser.toString());
        
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
