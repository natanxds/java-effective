package item01;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class PaymentTransaction {

    private final String id;
    private final BigDecimal amount;
    private final String currency;
    private final String paymentMethod;
    private final Instant timestamp;

    private static final PaymentTransaction ZERO_BRL_TRANSACTION =
            new PaymentTransaction("ZERO-000", BigDecimal.ZERO, "BRL", "NONE", Instant.EPOCH);

    private PaymentTransaction(String id, BigDecimal amount, String currency, String paymentMethod, Instant timestamp) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.timestamp = timestamp;
    }

    // =========================================================================
    // AS 5 VANTAGENS (PROS) DOS MÉTODOS ESTÁTICOS DE FÁBRICA:
    // =========================================================================

    /**
     * VANTAGEM 1: Eles têm nomes significativos.
     * O nome do método documenta explicitamente a intenção da criação,
     * ao contrário de um construtor 'new PaymentTransaction(...)'.
     */
    public static PaymentTransaction createPixTransaction(String pixKey, BigDecimal amount) {
        Objects.requireNonNull(pixKey, "Chave Pix não pode ser nula");
        validatePositiveAmount(amount);
        return new PaymentTransaction("PIX-" + pixKey.hashCode(), amount, "BRL", "PIX", Instant.now());
    }

    public static PaymentTransaction createCreditCardTransaction(String cardNumber, BigDecimal amount) {
        Objects.requireNonNull(cardNumber);
        validatePositiveAmount(amount);
        return new PaymentTransaction("CC-" + cardNumber.hashCode(), amount, "BRL", "CC", Instant.now());
    }

    /**
     * VANTAGEM 2: Não são obrigados a criar um novo objeto a cada chamada.
     * Permite cache de instâncias pré-construídas (como Boolean.valueOf() ou Integer.valueOf()).
     */
    public static PaymentTransaction zeroBrl() {
        // Retorna a instância compartilhada em memória, economizando GC
        return ZERO_BRL_TRANSACTION;
    }

    /**
     * VANTAGEM 3: Podem retornar qualquer subtipo do tipo de retorno declarado.
     * VANTAGEM 4: O tipo do objeto retornado pode variar dependendo dos parâmetros de entrada.
     * (Exemplo: EnumSet no JDK retorna RegularEnumSet se <= 64 elementos ou JumboEnumSet se > 64).
     */
    public static PaymentTransaction ofAutoCurrency(String id, BigDecimal amount, String countryCode) {
        validatePositiveAmount(amount);

        // A classe cliente nem precisa saber que existem implementações especializadas
        if ("BR".equalsIgnoreCase(countryCode)) {
            return new DomesticTransaction(id, amount, "BRL", Instant.now());
        } else {
            return new InternationalTransaction(id, amount, "USD", Instant.now(), new BigDecimal("0.05")); // 5% IOF
        }
    }

    /**
     * VANTAGEM 5: A classe do objeto retornado não precisa existir quando a classe do método é escrita.
     * Base de Service Provider Frameworks (como JDBC DriverManager.getConnection()).
     */

    private static void validatePositiveAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser positivo.");
        }
    }

    // =========================================================================
    // SUBTIPOS PRIVADOS / PACOTE (Para ilustrar Vantagens 3 e 4)
    // =========================================================================

    private static class DomesticTransaction extends PaymentTransaction {
        DomesticTransaction(String id, BigDecimal amount, String currency, Instant timestamp) {
            super(id, amount, currency, "DOMESTIC_WIRE", timestamp);
        }
    }

    private static class InternationalTransaction extends PaymentTransaction {
        private final BigDecimal internationalTaxRate;

        InternationalTransaction(String id, BigDecimal amount, String currency, Instant timestamp, BigDecimal taxRate) {
            super(id, amount, currency, "CROSS_BORDER", timestamp);
            this.internationalTaxRate = taxRate;
        }

        public BigDecimal getTaxRate() {
            return internationalTaxRate;
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("Transaction[%s | %s %s | via %s | at %s]",
                id, amount, currency, paymentMethod, timestamp);
    }

}
