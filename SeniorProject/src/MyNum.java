
public class MyNum {

    public void method() {
        int[] x = { 1, 2, 3 };

        for (int i = 0; i < 3; i++) {
            System.out.print(x[i]);
        }
    }

    public void method2() {
        int x = 1;
    }

    public static void main (String[] args) {
        MyNum num = new MyNum();
        num.method();
    }
}