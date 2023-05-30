package librairies.bttp;

public class Codage {
    public static String coder(String message) {
        return message.replace("\n", "##");
    }

    public static String decoder(String message) {
        return message.replace("##", "\n");
    }
}
