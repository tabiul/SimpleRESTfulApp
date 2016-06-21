package com.restapp.pojo;

import com.restapp.utils.Common;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Date;

public class DateAdapter extends XmlAdapter<String, Date> {
    @Override
    public Date unmarshal(String v) throws Exception {
        return Common.dateFormatWithTime.parse(v);
    }

    @Override
    public String marshal(Date v) throws Exception {
        return Common.dateFormatWithTime.format(v);
    }
}
