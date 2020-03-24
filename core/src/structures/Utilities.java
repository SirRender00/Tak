package structures;

public class Utilities {

    public static int min(int... ints) {
        int m = Integer.MAX_VALUE;

        for (Integer n : ints) {
            if (n < m) {
                m = n;
            }
        }

        return m;
    }

    public static int max(int... ints) {
        int m = Integer.MIN_VALUE;

        for (Integer n : ints) {
            if (n > m) {
                m = n;
            }
        }

        return m;
    }
}
