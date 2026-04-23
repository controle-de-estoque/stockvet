package ufrpe.stockvet.exceptions.usuario;

public class EmailInvalidoException extends RuntimeException {
    public EmailInvalidoException() {
        super("Endereço de E-mail invalido.");
    }
}
