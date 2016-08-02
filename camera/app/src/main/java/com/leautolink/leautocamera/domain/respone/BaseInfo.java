package com.leautolink.leautocamera.domain.respone;

import java.util.List;

/**
 * Created by lixinlei on 16/6/17.
 */
public class BaseInfo <T> {
   private  String    msg ;
   private  int       code;
   private  int       total;

   private List<T>    rows;




    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
