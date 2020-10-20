package com.cym.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigProperties {
    private String propertiesName = "";
    private Properties properties = new Properties();
    public ConfigProperties(){}
    public ConfigProperties(String fileName) {
        this.propertiesName = fileName;
    }
    //todo protected的范围是啥来着
    protected void loadFromFile() {
        InputStream in = null;
        //todo 这里可能会出现乱码问题
        try {
            String filePath = IOUtil.getResourcePath(propertiesName);
            in = new FileInputStream(filePath);
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(in);
        }
    }

    public String readProperty(String key) {
        String value = "";
        value = properties.getProperty(key);
        return value;
    }

    public String getValue(String key) {
         return readProperty(key);
    }

    public int getIntValue(String key) {
        //todo 不能使用（int） readProperty(key)
        return Integer.parseInt(readProperty(key));
    }
}
