package com.szxb.buspay.db.entity.bean;

import java.util.List;

/**
 * 作者：Tangren on 2018-07-24
 * 包名：com.szxb.buspay.db.entity.bean
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class ConfigParam {

    private List<ConfigBean> config;

    public List<ConfigBean> getConfig() {
        return config;
    }

    public void setConfig(List<ConfigBean> config) {
        this.config = config;
    }

    public static class ConfigBean {
        /**
         * ip : 112.35.80.147
         * port : 21
         * user : zbbusftpdan
         * psw : ftp1234!@#$
         * mch_id : 10000009
         * city_code : 370300
         * url_ip : 111.230.85.238
         */

        private String ip;
        private int port;
        private String user;
        private String psw;
        private String mch_id;
        private String city_code;
        private String url_ip;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPsw() {
            return psw;
        }

        public void setPsw(String psw) {
            this.psw = psw;
        }

        public String getMch_id() {
            return mch_id;
        }

        public void setMch_id(String mch_id) {
            this.mch_id = mch_id;
        }

        public String getCity_code() {
            return city_code;
        }

        public void setCity_code(String city_code) {
            this.city_code = city_code;
        }

        public String getUrl_ip() {
            return url_ip;
        }

        public void setUrl_ip(String url_ip) {
            this.url_ip = url_ip;
        }

        @Override
        public String toString() {
            return "ConfigBean{" +
                    "ip='" + ip + '\'' +
                    ", port=" + port +
                    ", user='" + user + '\'' +
                    ", psw='" + psw + '\'' +
                    ", mch_id='" + mch_id + '\'' +
                    ", city_code='" + city_code + '\'' +
                    ", url_ip='" + url_ip + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ConfigParam{" +
                "config=" + config +
                '}';
    }
}
