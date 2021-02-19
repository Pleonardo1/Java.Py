class MyNum {
    public static void main(String[] args) {
        int num = 5;
        double pi = 3.14;
        boolean isX = true;
        String name = "joe";

        for (String i : args) {
            name += i;
            pi = num + pi;
        }

        for (int j = 0, k = 1; pi > num; name += "", isX = !isX) {
            pi -= -1.0;
        }

        if (pi < num) {
            pi = num;
        } else if (pi < num) {
            pi = -num;
        } else {
            pi = 0;
        }
    }
}