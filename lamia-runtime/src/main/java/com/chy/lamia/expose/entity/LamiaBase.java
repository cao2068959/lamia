package com.chy.lamia.expose.entity;

import java.util.Date;

public class LamiaBase {

    String lamiaName;
    Long count;

    Date createTime;


    public String getLamiaName() {
        return lamiaName;
    }

    public void setLamiaName(String lamiaName) {
        this.lamiaName = lamiaName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
