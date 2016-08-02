package com.leautolink.leautocamera.domain.respone;

/**
 * Created by tianwei on 16/6/22.
 */
public class GetTokenInfo extends BaseInfo<Object>{
    
    private MapBean map;


    public MapBean getMap() {
        return map;
    }

    public void setMap(MapBean map) {
        this.map = map;
    }

    public static class MapBean {
        private int id;
        private String token;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
