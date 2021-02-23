import java.io.IOException;

class MyNum {

    private int num = 5;

    public MyNum () {
    }

    public int getMyNum() {
        return num;
    }

    public Object testObject() {
        return new Object();
    }

    public static void main(String[] args) {

        MyNum test = new MyNum();
        test.getMyNum();

        test.testObject().toString().getClass();

        System.out.println();
        System.out.println("Hello World");

    }
}