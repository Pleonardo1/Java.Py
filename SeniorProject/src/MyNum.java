public class MyNum {
    // This is a comment
    public class Test {

        public Test () {

        }

        public void method() {
            ;
        }
    }

    public String testWhiles () { // This is another comment
        int start = 0; /* this is another comment */
        do {
            if (start == 0) {
                break;
            }
            do  {
                if (start == 0) {
                    break;
                }
            } while (true);
        } while (true);

        while (start < 3) {
            System.out.println("Start");
            start++;
        }
        return "goodBye";
    }

    public static void main (String... args) {
        MyNum myNum = new MyNum ();
        String response = myNum.testWhiles();
        testTryCatch();
        testIfElse();
        testEnhancedLoop();
    }

    public static void testTryCatch(){
        try {
            int crazyNum = 1*(4 + 6) - 85 / (3 % 2);
        }
        catch(Exception e) {
            System.out.println("Whoops, I'm broken");
        } finally {
            System.out.print("TEST");
        }
    }

    public static void testIfElse () {
        int x = 50;
        if (x <= 100) {
            System.out.println("X is less than or equal to 100");
        }
        else if (x > 49) {
            System.out.println("X is greater than 49");
        }
        else {
            System.out.println("X is equal to 50");
        }
    }

    public static void testEnhancedLoop () {
        int [] numbers = {5, 10, 15, 20, 25};

        for (int x : numbers) {
            System.out.print (x);
            System.out.print(",");
        }
        System.out.print("\n");
        String [] names = {"Lexi", "Niki", "Patrick", "Josh"};

        for(String name: names) {
            System.out.print(name);
            System.out.print(",");
        }

        System.out.println();

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 1; j++) {
                System.out.println(j);
            }
        }
    }
}
