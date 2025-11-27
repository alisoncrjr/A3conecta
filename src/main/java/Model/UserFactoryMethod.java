package Model;

import Utils.ValidationUtil;

public class UserFactoryMethod {

    public static User createUser(String name, String email, String password, String type, String doc) {
        String passwordValidationError = ValidationUtil.validarSenha(password);

        if (!ValidationUtil.isNotNullOrEmpty(name) ||
                !ValidationUtil.validarEmail(email) ||
                passwordValidationError != null) {
            throw new IllegalArgumentException("Dados de usuário inválidos. " + (passwordValidationError != null ? passwordValidationError : ""));
        }

        if ("Cliente".equalsIgnoreCase(type)) {
            if (!ValidationUtil.validarCPF(doc)) {
                throw new IllegalArgumentException("CPF inválido para Cliente.");
            }

            return new Cliente(name, email, password, doc, "", "");
        } else if ("Prestador".equalsIgnoreCase(type)) {
            if (!ValidationUtil.validarCNPJ(doc)) {
                throw new IllegalArgumentException("CNPJ inválido para Prestador.");
            }
            return new Prestador(name, email, password, doc, "", "", "", "", "default_profile.png");
        } else {
            throw new IllegalArgumentException("Tipo de usuário desconhecido.");
        }
    }
}