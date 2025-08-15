package com.gkmonk.pos.model;

public class MaxCartonProjection {

    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Integer getMaxCounterNo() {
        return maxCounterNo;
    }

    public void setMaxCounterNo(Integer maxCounterNo) {
        this.maxCounterNo = maxCounterNo;
    }

    private Integer maxCounterNo;

}
