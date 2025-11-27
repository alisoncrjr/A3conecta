package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    /**
     * Valida senha forte: Mínimo 8 caracteres, 1 maiúscula, 1 minúscula, 1 número, 1 especial.
     * @param senha A senha a ser validada.
     * @return null se a senha for forte, ou a mensagem de erro específica.
     */
    public static String validarSenha(String senha) {
        if (senha == null || senha.length() < 8) {
            return "Senha deve ter no mínimo 8 caracteres.";
        }
        if (!senha.matches(".*[A-Z].*")) {
            return "Senha deve conter pelo menos uma letra maiúscula.";
        }
        if (!senha.matches(".*[a-z].*")) {
            return "Senha deve conter pelo menos uma letra minúscula.";
        }
        if (!senha.matches(".*\\d.*")) {
            return "Senha deve conter pelo menos um número.";
        }
        if (!senha.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~`].*")) {
            return "Senha deve conter pelo menos um caractere especial.";
        }
        return null;
    }

    /**
     * Valida um CPF (apenas dígitos).
     * @param cpf O CPF com 11 dígitos.
     * @return true se válido, false caso contrário.
     */
    public static boolean validarCPF(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", "");

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {

            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 >= 10) digito1 = 0;

            if ((cpf.charAt(9) - '0') != digito1) return false;


            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 >= 10) digito2 = 0;

            return (cpf.charAt(10) - '0') == digito2;
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean validarCNPJ(String cnpj) {
        cnpj = cnpj.replaceAll("[^0-9]", "");

        if (cnpj.length() != 14 || cnpj.matches("(\\d)\\1{13}")) {
            return false;
        }

        try {
            int[] peso = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;


            for (int i = 0; i < 12; i++) {
                soma += (cnpj.charAt(i) - '0') * peso[i];
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 > 9) digito1 = 0;

            if ((cnpj.charAt(12) - '0') != digito1) return false;


            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += (cnpj.charAt(i) - '0') * peso2[i];
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 > 9) digito2 = 0;

            return (cnpj.charAt(13) - '0') == digito2;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isNotNullOrEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}