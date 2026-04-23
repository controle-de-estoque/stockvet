package ufrpe.stockvet.DTO;

public record RegistroDTO(
		String email,
		String senha,
		String nome,
		String sobrenome,
		String primeiroPet,
		FuncaoPayload funcao
) {
	public record FuncaoPayload(String name) {
	}
}
