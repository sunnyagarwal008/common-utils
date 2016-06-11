/*
*  @version     1.0, 02/03/16
*  @author sunny
*/

package in.bucheeng.common.utils;

import java.util.List;

public final class ExponentialBackOff {

    private static final int[] FIBONACCI = new int[] { 1, 1, 2, 3, 5, 8, 13, 21, 34, 55 };

    private ExponentialBackOff() {

    }

    public static <T> T execute(ExponentialBackOffFunction<T> fn, int attempts, List<Class<? extends Exception>> expectedErrors) {
        for (int attempt = 0; attempt < attempts; attempt++) {
            try {
                return fn.execute();
            } catch (Exception e) {
                handleFailure(attempt, expectedErrors, e);
            }
        }
        throw new RuntimeException("Failed");
    }

    private static void handleFailure(int attempt, List<Class<? extends Exception>> expectedErrors, Exception e) {
        if (e.getCause() != null && !expectedErrors.contains(e.getCause().getClass()))
            throw new RuntimeException(e);
        doWait(attempt);
    }

    private static void doWait(int attempt) {
        try {
            Thread.sleep(FIBONACCI[attempt] * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
