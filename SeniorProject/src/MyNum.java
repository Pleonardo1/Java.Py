
class MyNum {

    public int memberField1 = -1;
    public String memberField2 = "";

    public MyNum() {
        memberField1 = 0;
        this.memberField2 = "";
    }

    public MyNum(int x, double y, String z) {
        this();
    }

    public int method1() {
        int x = 0;
        double y = 3.14;
        boolean z = true;

        x = x + (int) y;

        return 10;
    }

    public void method2(String s) {
        method1();
        System.out.println(s + memberField1 + memberField2);
    }

    public static void main(String[] args) {
        MyNum myNum = new MyNum(1, 2.0, "xyz");

        myNum.method1();
        myNum.method2("  ");
    }
}