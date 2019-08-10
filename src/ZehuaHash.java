import java.util.Enumeration;
import java.util.Vector;

/**
 * ZehuaHash
 */
public class ZehuaHash {
    private Vector<Integer> keys;
    private String[] values;
    private Vector<Integer> realKeys;

    private int initSize = 5;
    private int capacity;
    private int length;

    public ZehuaHash() {
        this.length = 0;
        this.capacity = initSize;
        this.keys = new Vector<Integer>();
        this.values = new String[initSize];
        this.realKeys = new Vector<Integer>();
    }

    public void add(int k, String v) throws ZehuaHashOutOfIndex {
        // 如果key相同,则替换
        if (keys.contains(k)) {
            remove(k);
        }

        int initAddr = hashFun(k);

        int index = 0;
        if (values[initAddr] == null) {
            index = initAddr;
        } else {
            while (index < capacity) {
                if (values[index] == null) {
                    break;
                }

                index++;
            }
        }

        if (index >= capacity) {
            // 有冲突，没空间
            throw new ZehuaHashOutOfIndex();
        }

        values[index] = v;
        length++;
        keys.add(k);
        realKeys.add(index);

    }

    public int[] getKeys() {
        int[] ks = new int[keys.size()];
        int index = 0;
        Enumeration<Integer> e = keys.elements();
        while (e.hasMoreElements()) {
            ks[index++] = e.nextElement();
        }
        return ks;
    }

    public String[] getValues() {
        int index = 0;
        String[] vs = new String[length];
        for (int i = 0; i < capacity; i++) {
            if (values[i] != null) {
                vs[index++] = values[i];
            }
        }
        return vs;
    }

    public String getValue(int k) {
        // 是否存在
        if (keys.contains(k)) {
            int index = keys.indexOf(k);
            int realKey = realKeys.elementAt(index);
            return values[realKey];
        }

        return null;
    }

    public String remove(int k) {
        int index = keys.indexOf(k);
        int realKey = realKeys.elementAt(index);
        keys.remove(index);
        realKeys.remove(index);
        String v = values[realKey];
        values[realKey] = null;
        length--;
        return v;
    }

    private int hashFun(int k) {
        return k % 3;
    }

    public void doubleCapacity() {
        capacity *= 2;
        String[] newValues = new String[capacity];
        for (int i = 0; i < values.length; i++) {
            newValues[i] = values[i];
        }
        this.values = newValues;
    }

}

class ZehuaHashOutOfIndex extends Exception {

    @Override
    public void printStackTrace() {
        System.out.println("sorry! ZehuaHash no no no no!");
    }
}