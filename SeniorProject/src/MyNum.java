class MyNum {
    public static void main(String[] args) {

        boolean isX = true;
        String name = "joe";
    }

    public double test () throws IllegalAccessException {
        int num = 5;
        double pi = 3.14;

        if (pi < num) {
            pi = num;
            return 0;
        } else if (pi < num) {
            pi = -num;
            return num + pi;
        } else {
            pi = 0;
            return num - pi;
        }
    }
}