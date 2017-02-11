package com.austinv11.etf.test;

import com.austinv11.etf.util.parsing.ETFParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.json.JSONObject;

import java.io.*;
import java.util.Arrays;

public class ETFTester {

    public static void main(String[] args) throws IOException {
        readEtf(ETFTester.class.getResourceAsStream("/test.etf"));

        readJson(ETFTester.class.getResourceAsStream("/test.json"));

        readGson(ETFTester.class.getResourceAsStream("/test.json"));
    }

    private static void readEtf(InputStream is) throws IOException {
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
        System.out.println(new ETFParser(dataArray, 131, false, true).nextMap().toString());
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
