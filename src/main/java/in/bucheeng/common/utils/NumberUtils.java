/*
 *  @version     1.0, Jan 20, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {

    public static double round2(double d) {
        long result = Math.round((d * 100));
        return (result / 100.0);
    }

    public static double roundToDecimals(double d, int c) {
        long result = Math.round(d * Math.pow(10, c));
        return (result / Math.pow(10, c));
    }

    public static BigDecimal getPercentageFromTotal(BigDecimal totalAmount, BigDecimal percentage) {
        return totalAmount.multiply(percentage).divide(new BigDecimal(100).add(percentage), 2, RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal getPercentageFromBase(BigDecimal baseAmount, BigDecimal percentage) {
        return baseAmount.multiply(percentage).divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal getPercentageFromBaseAndPercentageAmount(BigDecimal baseAmount, BigDecimal percetageAmount) {
        if (greaterThan(baseAmount, BigDecimal.ZERO)) {
            return percetageAmount.multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_EVEN).divide(baseAmount, 2, RoundingMode.HALF_EVEN).setScale(2,
                    RoundingMode.HALF_EVEN);
        } else {
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal stringToBigDecimal(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal newBigDecimal(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal divide(BigDecimal input, BigDecimal divisor) {
        return divide(input, divisor, 2);
    }

    public static BigDecimal divide(BigDecimal input, BigDecimal divisor, int scale) {
        if (BigDecimal.ZERO.equals(divisor)) {
            return BigDecimal.ZERO;
        }
        return input.divide(divisor, scale, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal divide(BigDecimal input, Integer divisor) {
        return divide(input, divisor, 2);
    }

    public static BigDecimal divide(BigDecimal input, Integer divisor, int scale) {
        if (divisor == 0) {
            return BigDecimal.ZERO;
        }
        return input.divide(new BigDecimal(divisor), scale, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal multiply(BigDecimal input, BigDecimal multiplicand) {
        return multiply(input, multiplicand, 2);
    }

    public static BigDecimal multiply(BigDecimal input, BigDecimal multiplicand, int scale) {
        return input.multiply(multiplicand).setScale(scale, RoundingMode.HALF_EVEN);
    }

    public static BigDecimal multiply(BigDecimal input, int multiplicand, int scale) {
        return multiply(input, new BigDecimal(multiplicand), scale);
    }

    public static BigDecimal multiply(BigDecimal input, int multiplicand) {
        return multiply(input, new BigDecimal(multiplicand), 2);
    }

    public static BigDecimal multiply(BigDecimal input, double multiplicand) {
        return multiply(input, new BigDecimal(multiplicand), 2);
    }

    public static BigDecimal multiply(BigDecimal input, double multiplicand, int scale) {
        return multiply(input, new BigDecimal(multiplicand), scale);
    }

    public static boolean greaterThan(BigDecimal lhs, BigDecimal rhs) {
        return lhs.compareTo(rhs) == 1;
    }

    public static boolean lessThan(BigDecimal lhs, BigDecimal rhs) {
        return lhs.compareTo(rhs) == -1;
    }

    public static boolean equals(BigDecimal lhs, BigDecimal rhs) {
        return lhs.compareTo(rhs) == 0;
    }

    private static int intValue(char c) {
        return c - 48;
    }

    private static String[] TENS  = { "", "Ten", " Twenty", " Thirty", " Forty", " Fifty", " Sixty", " Seventy", " Eighty", " Ninety" };
    private static String[] UNITS = new String[] {
            "",
            " One",
            " Two",
            " Three",
            " Four",
            " Five",
            " Six",
            " Seven",
            " Eight",
            " Nine",
            " Ten",
            " Eleven",
            " Twelve",
            " Thirteen",
            " Fourteen",
            " Fifteen",
            " Sixteen",
            " Seventeen",
            " Eighteen",
            " Nineteen"          };
    private static String[] DIGIT = { "", " Hundred", " Thousand", " Lakh", " Crore" };

    private static int digitCount(int num) {
        int cnt = 0;
        while (num > 0) {
            num = num / 10;
            cnt++;
        }
        return cnt;
    }

    private static String toWordsTwoDigit(int number) {
        if (number > 19) {
            return TENS[number / 10] + UNITS[number % 10];
        } else {
            return UNITS[number];
        }
    }

    private static String toWordsThreeDigit(int numq) {
        int numr, nq;
        nq = numq / 100;
        numr = numq % 100;
        if (numr == 0) {
            return UNITS[nq] + DIGIT[1];
        } else {
            return UNITS[nq] + DIGIT[1] + " and" + toWordsTwoDigit(numr);
        }
    }

    public static String toWords(int number) {
        if (number < 0) {
            throw new IllegalArgumentException("Zero or Negative number not for conversion");
        } else if (number == 0) {
            return "Zero";
        }

        StringBuilder builder = new StringBuilder();
        WHILE: while (number > 0) {
            int len = digitCount(number);
            switch (len) {
                case 7:
                case 6:
                    builder.append(toWordsTwoDigit(number / 100000) + DIGIT[3]);
                    number = number % 100000;
                    break;
                case 5:
                case 4:
                    builder.append(toWordsTwoDigit(number / 1000) + DIGIT[2]);
                    number = number % 1000;
                    break;
                case 3:
                    builder.append(toWordsThreeDigit(number));
                    break WHILE;
                case 2:
                    builder.append(toWordsTwoDigit(number));
                    break WHILE;
                case 1:
                    builder.append(UNITS[number]);
                    break WHILE;
                default:
                    builder.append(" ").append(toWords(number / 10000000)).append(DIGIT[4]);
                    number = number % 10000000;
                    break;
            }
        }
        return builder.deleteCharAt(0).toString();
    }

    public static void main(String[] args) {
        System.out.println((new BigDecimal(1)).scale());

    }

}
