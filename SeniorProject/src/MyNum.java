
class MyNum {

    double pi = 3.14;

    public int method(double pi_val) {
        int x = 0;
        double y = 1.0;
        
        if (x < 3) {
            System.out.println(x);
            x = 7;

        } else if (y > x) {
            System.out.print(y);
            y = x + 3;

        } else {
            System.out.print("You suck!");
        }
        return x;
    }
}