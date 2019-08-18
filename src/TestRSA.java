/**
 * TestRSA
 */
public class TestRSA {

    public static void main(String[] args) {
        // formal test
        // first 有两个人
        Person1 person1 = new Person1();
        Person2 person2 = new Person2();
        System.out.println("begin");
        // person1 产生一对密钥
        person1.generateKeys();
        System.out.println("1");
        // person1将public给person2
        person1.givePKToOtherPerson(person2);
        System.out.println("2");
        // person2发送加密消息
        person2.send("hello world !,小泽华，你好呀!");
        System.out.println("3");
        // 打印加密后的消息
        Server.println();
        System.out.println("4");
        // person1 解密
        person1.receive();
        System.out.println("end");
    }
}

// 公钥
class PubKey {
    int n;
    int e;

    PubKey(int n, int e) {
        this.n = n;
        this.e = e;
    }
}

// 私钥
class PriKey {
    int n;
    int d;

    PriKey(int n, int d) {
        this.n = n;
        this.d = d;
    }
}

// 用于保存n, e, d
class EDN {
    int n, e, d;

    public EDN(int n, int e, int d) {
        this.n = n;
        this.e = e;
        this.d = d;
    }
}

// 模拟接收方
class Person1 {
    private PubKey pubKey;
    private PriKey priKey;

    public void generateKeys() {
        EDN edn = MyUtil.generateKeys();
        this.pubKey = generatePubK(edn);
        this.priKey = generatePriK(edn);
        System.out.println("pub key: {" + pubKey.e + "," + pubKey.n + "}");
        System.out.println("pri key: {" + priKey.d + "," + priKey.n + "}");
    }

    public void givePKToOtherPerson(Person2 person2) {
        person2.setPubKey(pubKey);
    }

    public String receive() {
        String str = Server.sendStr;
        String originStr = decodeStr(str, priKey);
        System.out.println(originStr);

        return originStr;
    }

    public String decodeStr(String str, PriKey priKey) {
        return MyUtil.convertStr(str, priKey.d, priKey.n);
    }

    private PubKey generatePubK(EDN edn) {
        return new PubKey(edn.n, edn.e);
    }

    private PriKey generatePriK(EDN edn) {
        return new PriKey(edn.n, edn.d);
    }

}

// 模拟发送方
class Person2 {
    private PubKey pubKey;

    public void setPubKey(PubKey pubKey) {
        this.pubKey = pubKey;
    }

    public void send(String str) {
        Server.sendStr = incodeStr(str, pubKey);
    }

    private String incodeStr(String str, PubKey pubKey) {
        return MyUtil.convertStr(str, pubKey.e, pubKey.n);
    }
}

// 工具类
class MyUtil {
    // 在小于256的数中找出最大的两个质数
    private static int num = 256;
    /**
     * e e和n组成公钥 d d和n组成私钥 (q,p) q、p乘积为n n (mod n)模n eulFN 欧拉函数值
     */
    private static int e, d, q, p, n, eulFN;

    static {
        int[] twoPrimes = getMaxTwoPrimeInN(num);
        q = twoPrimes[0];
        p = twoPrimes[1];
        n = q * p;
        eulFN = (q - 1) * (p - 1);
        e = getRelPrime(eulFN);
        int[] xyr = new int[3];
        positiveA(e, eulFN, xyr);
        d = xyr[0];
    }

    // 通过“扩展欧几里得算法”求解裴蜀定理时
    // 求解出的d可能为负数，通过d+k（eulFN）将d转化为正数
    static void positiveA(int a, int b, int[] XYR) {
        extOJLD(a, b, XYR);
        int x = XYR[0];
        while (x < 0) {
            x += b;
        }

        XYR[0] = x;
    }

    // aX+bY=R
    // 通过“扩展欧几里得算法”求解裴蜀定理
    private static void extOJLD(int a, int b, int[] XYR) {
        if (b == 0) {
            XYR[0] = 1;
            XYR[1] = 0;
            XYR[2] = a;
            return;
        }

        extOJLD(b, a % b, XYR);
        int x = 0;
        int y = 0;
        x = XYR[1];
        y = XYR[0] - (int) (Math.floor(a / b)) * XYR[1];
        XYR[0] = x;
        XYR[1] = y;
    }

    // return num1^num2 mod num3
    // 求模
    static int getMod(int num1, int num2, int num3) {
        int[] nums = new int[num2];
        for (int i = 0; i < num2; i++) {
            nums[i] = num1;
        }

        return recGetMod(nums, num3);
    }

    // 加解密时，完成字符串的转换
    static String convertStr(String str, int num1, int num2) {
        StringBuffer stringBuffer = new StringBuffer();
        char[] chs = str.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            int chInt = chs[i];
            char modCh = codeInt(chInt, num1, num2);
            stringBuffer.append(modCh);
        }

        return stringBuffer.toString();
    }

    // 加解密时， 完成int到char的转换
    static char codeInt(int num1, int num2, int num3) {
        int modInt = MyUtil.getMod(num1, num2, num3);
        char ch = (char) modInt;
        return ch;
    }

    // 高次幂求模递归函数
    private static int recGetMod(int[] nums, int modNum) {
        int len = nums.length;
        // 以三个为一组，因为char是16位，而long是64位，出去符号位为63位，三个16位二进制数相乘不会溢出
        if (len <= 3) {
            long n = 1;
            for (int i = 0; i < len; i++) {
                n *= nums[i];
            }
            return (int) (n % modNum);
        }

        int a = len % 3;
        int b = len / 3;
        int[] nextNums;
        if (a != 0) {
            nextNums = new int[b + 1];
        } else {
            nextNums = new int[b];
        }

        for (int i = 0; i < b; i++) {
            long num1 = nums[i * 3];
            long num2 = nums[i * 3 + 1];
            long num3 = nums[i * 3 + 2];
            int m = (int) ((num1 * num2 * num3) % modNum);
            nextNums[i] = m;
        }

        if (a != 0) {
            long n = 1;
            for (int i = 0; i < a; i++) {
                n *= nums[i + b * 3];
            }
            int m = (int) (n % modNum);
            nextNums[b] = m;
        }

        return recGetMod(nextNums, modNum);
    }

    // 生成私钥和公钥
    static EDN generateKeys() {
        return new EDN(n, e, d);
    }

    // 获取小于或等于a的最大的两个质数
    private static int[] getMaxTwoPrimeInN(int a) {
        if (a <= 10) {
            System.out.println("必须大于10！");
            return null;
        }

        int[] maxTwoPrimes = new int[2];
        int len = 0;
        for (int i = a; i >= 2; i--) {
            if (isPrime(i)) {
                maxTwoPrimes[len++] = i;
                if (len == 2) {
                    break;
                }
            }
        }

        return maxTwoPrimes;
    }

    // 获取与a互质的数
    private static int getRelPrime(int a) {
        if (a <= 10) {
            System.out.println("必须大于10！");
            return -1;
        }

        int res = -1;
        for (int i = a - 1; i >= 2; i--) {
            if (isPrime(i) && a % i != 0) {
                res = i;
                break;
            }
        }

        return res;
    }

    // 判断一个数是否为质数
    private static boolean isPrime(int a) {
        if (a <= 1) {
            return false;
        }

        if (a == 2 || a == 3) {
            return true;
        }

        if (a % 6 != 1 && a % 6 != 5) {
            return false;
        }

        int sq = (int) Math.floor(Math.sqrt(a));
        for (int i = 5; i < sq; i += 6) {
            if (a % i == 0 || a % (i + 2) == 0) {
                return false;
            }
        }

        return true;
    }
}

// 模拟服务器
class Server {
    static String sendStr;

    static void println() {
        System.out.println(sendStr);
    }
}