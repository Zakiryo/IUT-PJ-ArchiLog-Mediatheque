package exception;

public class RestrictionException extends Throwable {
    public RestrictionException() {
        super("Vous n'avez pas l'âge pour réserver ou emprunter ce document.");
    }
}
