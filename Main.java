import java.io.IOException;

class Main {
    public static void main(String[] args) {
        try {
            System.out.println((new CalcParser(System.in)).Expr());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}

