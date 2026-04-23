package ufrpe.stockvet.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Dados {
    private String email;
    private String primeiroNome;
    private String ultimoNome;
    private String pergunta;
    private String senha;
    private int funcaoId;
    private int estoqueId;
}
