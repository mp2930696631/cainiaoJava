/**
 * TestHash
 */
public class TestHash {

    public static void main(String[] args) {
        ZehuaHash hash = new ZehuaHash();
        int index = 0;
        int testKey = 0;
        while ((index++) < 20) {
            int k = (int) (Math.random() * 100);
            if (index == 10) {
                testKey = k;
            }
            try {
                hash.add(k, "String " + k);
                int[] keys = hash.getKeys();
                String[] values = hash.getValues();
                TestHash.formatOutput(k, keys, values);
                Thread.sleep(1000);
            } catch (ZehuaHashOutOfIndex e) {
                e.printStackTrace();
                hash.doubleCapacity();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("finally testKey:" + testKey + " value: " + hash.getValue(testKey));
    }

    private static void formatOutput(int k, int[] keys, String[] values) {
        System.out.println("********************************************");
        System.out.println("key: " + k);
        System.out.println("now keys: ");
        for (int x : keys) {
            System.out.print(x + " ");
        }
        System.out.println();
        System.out.println("now values: ");
        for (String x : values) {
            System.out.println(x);
        }
        System.out.println("********************************************");
    }

}