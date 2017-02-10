package com.austinv11.etf.test;

import com.austinv11.etf.util.parsing.ETFParser;
import org.json.JSONObject;

import java.io.*;

public class ETFTester {

    public static void main(String[] args) throws IOException {
        readEtf(ETFTester.class.getResourceAsStream("/test.etf"));

        readJson(ETFTester.class.getResourceAsStream("/test.json"));
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

        long init2 = System.currentTimeMillis();
        System.out.println(new JSONObject(sb.toString()).toMap().toString());
        System.out.println(System.currentTimeMillis() - init2);
    }
}
