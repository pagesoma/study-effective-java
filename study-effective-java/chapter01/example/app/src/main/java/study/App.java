package study;

public class App {
    public String getGreeting() {
        return "Hello Effective Java!";
    }

    public static void main(String[] args) {
        System.out.println(new App().getGreeting());
    }
}
