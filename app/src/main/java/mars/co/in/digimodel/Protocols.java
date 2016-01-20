package mars.co.in.digimodel;

/**
 * Created by manthan on 20/1/16.
 */
public class Protocols {
    public static String convertIntIPtoStringIP(int ip) {
        return (String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff),
                (ip >> 16 & 0xff), (ip >> 24 & 0xff)));
    }
}
