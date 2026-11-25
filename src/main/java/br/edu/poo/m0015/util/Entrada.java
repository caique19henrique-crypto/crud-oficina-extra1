package br.edu.poo.m0015.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

/** Leitura de dados do teclado com validação e repetição em caso de erro. */
public class Entrada {

    private static final Scanner SC = new Scanner(System.in);
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    // aceita placa antiga (ABC1234) e Mercosul (ABC1D23)
    private static final Pattern PLACA = Pattern.compile("^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$");

    public static int lerInt(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(SC.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, digite um número inteiro.");
            }
        }
    }

    public static int lerIntFaixa(String msg, int min, int max) {
        while (true) {
            int v = lerInt(msg);
            if (v >= min && v <= max) return v;
            System.out.println("Digite um número entre " + min + " e " + max + ".");
        }
    }

    /** Enter em branco retorna null (usado nas atualizações). */
    public static Integer lerIntFaixaOpcional(String msg, int min, int max) {
        while (true) {
            System.out.print(msg);
            String s = SC.nextLine().trim();
            if (s.isEmpty()) return null;
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) return v;
                System.out.println("Digite um número entre " + min + " e " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido, digite um número inteiro.");
            }
        }
    }

    /** Se obrigatorio=false, Enter em branco retorna "" (usado nas atualizações). */
    public static String lerTexto(String msg, boolean obrigatorio, int max) {
        while (true) {
            System.out.print(msg);
            String s = SC.nextLine().trim();
            if (s.isEmpty() && !obrigatorio) return "";
            if (s.isEmpty()) {
                System.out.println("Esse campo é obrigatório.");
            } else if (s.length() > max) {
                System.out.println("Máximo de " + max + " caracteres.");
            } else {
                return s;
            }
        }
    }

    public static String lerEmail(String msg, boolean obrigatorio) {
        while (true) {
            String s = lerTexto(msg, obrigatorio, 100);
            if (s.isEmpty() || EMAIL.matcher(s).matches()) return s;
            System.out.println("E-mail inválido.");
        }
    }

    /** Lê o CEP e devolve só os 8 dígitos. */
    public static String lerCep(String msg, boolean obrigatorio) {
        while (true) {
            String s = lerTexto(msg, obrigatorio, 20).replaceAll("\\D", "");
            if (s.isEmpty() && !obrigatorio) return "";
            if (s.length() == 8) return s;
            System.out.println("CEP inválido, precisa ter 8 dígitos.");
        }
    }

    /** Lê a placa (com ou sem hífen) e devolve em maiúsculas, 7 caracteres. */
    public static String lerPlaca(String msg, boolean obrigatorio) {
        while (true) {
            String s = lerTexto(msg, obrigatorio, 20).toUpperCase().replace("-", "").replace(" ", "");
            if (s.isEmpty() && !obrigatorio) return "";
            if (PLACA.matcher(s).matches()) return s;
            System.out.println("Placa inválida (ex: ABC1234 ou ABC1D23).");
        }
    }

    /** Valor em reais maior que zero. Se não obrigatório, Enter em branco retorna null. */
    public static BigDecimal lerValor(String msg, boolean obrigatorio) {
        while (true) {
            String s = lerTexto(msg, obrigatorio, 20);
            if (s.isEmpty()) return null;
            try {
                BigDecimal v = new BigDecimal(s.replace(",", "."));
                if (v.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("O valor precisa ser maior que zero.");
                } else if (v.compareTo(new BigDecimal("99999999.99")) > 0) {
                    System.out.println("Valor muito grande.");
                } else {
                    return v.setScale(2, RoundingMode.HALF_UP);
                }
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido (ex: 49.90).");
            }
        }
    }

    /** Lê um valor de enum (sem diferenciar maiúscula/minúscula). Se não obrigatório, Enter retorna null. */
    public static <E extends Enum<E>> E lerEnum(String msg, Class<E> tipo, boolean obrigatorio) {
        while (true) {
            String s = lerTexto(msg, obrigatorio, 20);
            if (s.isEmpty()) return null;
            try {
                return Enum.valueOf(tipo, s.toUpperCase().replace(" ", "_").replace("-", "_"));
            } catch (IllegalArgumentException e) {
                System.out.println("Opção inválida. Use: " + Arrays.toString(tipo.getEnumConstants()));
            }
        }
    }

    public static boolean confirmar(String msg) {
        System.out.print(msg + " (s/n): ");
        return SC.nextLine().trim().equalsIgnoreCase("s");
    }
}
