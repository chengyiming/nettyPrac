package com.cym.util;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FormatUtil {

    public static DecimalFormat decimalFormat(int fractions) {
        // todo 这是什么意思
        DecimalFormat df = new DecimalFormat("#0.0");
        df.setRoundingMode(RoundingMode.HALF_UP);
        df.setMinimumFractionDigits(fractions);
        df.setMaximumFractionDigits(fractions);
        return df;
    }

    public static void main(String[] args) {
        System.out.println(decimalFormat(3));
    }
}
