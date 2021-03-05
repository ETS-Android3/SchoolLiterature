package fitisov123.schoolliterature;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Algorithm {
    public static int min(int a, int b) {
        if (a <= b) {
            return a;
        } else {
            return b;
        }
    }

    public static int kmp(String text, String sample) {
        StringBuilder union = new StringBuilder(sample);
        union.append('^');
        union.append(text);
        int n = union.length();
        int[] pi = new int[n];
        pi[0] = 0;
        for (int i = 1; i < n; i++) {
            int j = pi[i - 1];
            while (j > 0 && union.charAt(i) != union.charAt(j)) {
                j = pi[j - 1];
            }
            if (union.charAt(i) == union.charAt(j)) {
                j++;
            }
            pi[i] = j;
            if(pi[i] == sample.length()) {
                return i - 2 * sample.length();
            }
        }
        return -1;
    }

    public static <ObjectType extends Comparable<ObjectType>> ArrayList<ObjectType> mergeSort(ArrayList<ObjectType> arrayList, int l, int r){
        if(arrayList == null) {
            return null;
        }
        if(arrayList.isEmpty()) {
            return arrayList;
        }

        ArrayList<ObjectType> res = new ArrayList<>();
        if (l == r) {
            res.add(arrayList.get(l));
            return res;
        }
        ArrayList<ObjectType> left, right;
        int m = (l + r) / 2;
        left = mergeSort(arrayList, l, m);
        right = mergeSort(arrayList, m+1, r);
        int curL = 0, curR = 0;
        for (int i = l; i <= r; i++) {
            if (curL == left.size()) {
                res.add(right.get(curR++));
                continue;
            }
            if (curR == right.size()) {
                res.add(left.get(curL++));
                continue;
            }
            ObjectType curLeft = left.get(curL), curRight = right.get(curR);
            if (curLeft.compareTo(curRight) < 0) {
                res.add(left.get(curL++));
            } else {
                res.add(right.get(curR++));
            }
        }
        return res;
    }

    public static void logMessage(String message) {
        Logger logger = Logger.getLogger(Algorithm.class.getName());
        logger.log(Level.INFO, message);
    }
}
