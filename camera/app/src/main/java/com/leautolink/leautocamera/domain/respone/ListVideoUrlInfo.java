package com.leautolink.leautocamera.domain.respone;

/**
 * Created by lixinlei on 16/3/14.
 */
public class ListVideoUrlInfo extends BaseRespone {
    private URLListInfo   data;


    public class URLListInfo{
        private URLList video_list;
        private String  user_id;
        private String video_id;
        private String video_name;
        public class URLList{
          private URLInfo video_1;
          private URLInfo video_2;

            public class URLInfo{
               private String main_url;
               private String vwidth;
               private String vheight;
               private String gbr;
               private String storePath;
               private String vtype;
               private String definition;

                public String getMain_url() {
                    return main_url;
                }

                public String getVwidth() {
                    return vwidth;
                }

                public String getVheight() {
                    return vheight;
                }

                public String getGbr() {
                    return gbr;
                }

                public String getStorePath() {
                    return storePath;
                }

                public String getVtype() {
                    return vtype;
                }

                public String getDefinition() {
                    return definition;
                }
            }

            public URLInfo getVideo_1() {
                return video_1;
            }

            public URLInfo getVideo_2() {
                return video_2;
            }
        }

        public URLList getVideo_list() {
            return video_list;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getVideo_id() {
            return video_id;
        }

        public String getVideo_name() {
            return video_name;
        }
    }

    public URLListInfo getData() {
        return data;
    }
}
