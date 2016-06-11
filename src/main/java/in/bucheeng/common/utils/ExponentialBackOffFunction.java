/*
*  @version     1.0, 02/03/16
*  @author sunny
*/

package in.bucheeng.common.utils;

@FunctionalInterface
public interface ExponentialBackOffFunction<T> {

    T execute();
}
