package com.tao.luhelper;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;

/**
 * Created by Tao on 2017/10/25.
 */

public class GlobleUtil {
    public static String getString(String key, String defValue) {
        BufferedReader reader = null;
        try {
            File file = new File(Environment.getExternalStorageDirectory(), "/Download/luhelper/lu.txt");
            reader = new BufferedReader(new FileReader(file));
            String l = null;
            while ((l = reader.readLine()) != null) {
                if (l.indexOf(key) == 0) {
                    return l.substring(key.length() + 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                }
            }
        }

        return defValue;
    }

    public static void putString(String key, String value) {
        StringBuffer strContent = new StringBuffer();
        File file = new File(Environment.getExternalStorageDirectory(), "/Download/luhelper/lu.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String l = null;
            while ((l = reader.readLine()) != null) {
                if (l.indexOf(key) != 0) {
                    if (strContent.length() > 0) {
                        strContent.append('\n');
                    }
                    strContent.append(l);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (strContent.length() > 0) {
            strContent.append('\n');
        }
        strContent.append(key);
        strContent.append(':');
        strContent.append(value);

        try {
            File filePath = new File(Environment.getExternalStorageDirectory(), "/Download/luhelper");
            if (!filePath.exists()) {
                filePath.mkdirs();
            }
            FileWriter writer = new FileWriter(file);
            writer.write(strContent.toString());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return Boolean.parseBoolean(getString(key, Boolean.toString(defValue)));
    }

    public static void putBoolean(String key, boolean value) {
        putString(key, Boolean.toString(value));
    }

    public static int getInt(String key, int defValue) {
        return Integer.parseInt(getString(key, Integer.toString(defValue)));
    }

    public static void putInt(String key, int value) {
        putString(key, Integer.toString(value));
    }
}
