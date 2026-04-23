package ufrpe.stockvet.exceptions.usuario;

public class PerguntaInvalidaException extends RuntimeException {
    public PerguntaInvalidaException() {
        super("Pergunta invalida");
    }
}
