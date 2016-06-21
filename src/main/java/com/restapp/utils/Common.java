package com.restapp.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Common {
    public static final DateFormat dateFormatWithTime = new SimpleDateFormat(
            "dd/MM/yyyy HH:mm");
    public static final DateFormat dateFormatWithoutTime = new SimpleDateFormat(
            "dd/MM/yyyy");

}
