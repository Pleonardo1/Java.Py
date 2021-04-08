
public class MyNum {

    public MyNum (int grade, char letter) {

    }

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
        int [] test1 = new int[7];
        int [] test2 = new int[] {1, 2, 3};
        int array [] = {1,2,3};
        int grade = 100;
        char letter = 'A';
        MyNum num = new MyNum(grade, letter);
        num.method();
    }
}