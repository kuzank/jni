import jnilib.TestJNI;

public class Demo {
    static {
        try {
            System.loadLibrary("TestJNI");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Native code library failed to load.\n" + e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        TestJNI test = new TestJNI();
        test.sayHello();
    }
}
