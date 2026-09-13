package item01;

import java.math.BigDecimal;

public class ItemRunner {

    public static void main(String[] args) {
        System.out.println("=== ITEM 1: STATIC FACTORY METHODS ===\n");

        // 1. Nomes claros e autoexplicativos
        PaymentTransaction pix = PaymentTransaction.createPixTransaction("cpf-123456", new BigDecimal("150.00"));
        PaymentTransaction card = PaymentTransaction.createCreditCardTransaction("tok_visa_abc", new BigDecimal("350.00"));
        System.out.println("1. Com nomes claros:");
        System.out.println("   " + pix);
        System.out.println("   " + card);

        // 2. Reutilização de instâncias (Controle de Instâncias / Cache)
        PaymentTransaction zero1 = PaymentTransaction.zeroBrl();
        PaymentTransaction zero2 = PaymentTransaction.zeroBrl();
        System.out.println("\n2. Controle de instância (Cache):");
        System.out.println("   Mesma referência de memória? " + (zero1 == zero2)); // true! Zero alocação de heap extra.

        // 3 e 4. Retornando subtipos dinamicamente baseados na entrada
        PaymentTransaction brTx = PaymentTransaction.ofAutoCurrency("TX-BR-1", new BigDecimal("50.00"), "BR");
        PaymentTransaction usTx = PaymentTransaction.ofAutoCurrency("TX-US-2", new BigDecimal("50.00"), "US");
        System.out.println("\n3 e 4. Variação polimórfica de subtipos:");
        System.out.println("   BR: " + brTx.getClass().getSimpleName() + " -> " + brTx);
        System.out.println("   US: " + usTx.getClass().getSimpleName() + " -> " + usTx);

        // =====================================================================
        // AS 2 DESVANTAGENS (CONTRAS) APONTADAS POR JOSHUA BLOCH:
        // =====================================================================
        /*
         * CONTRA 1: Classes sem construtores públicos ou protegidos não podem ter subclasses.
         * Exemplo: Não conseguimos dar 'public class CustomTx extends PaymentTransaction'
         * fora deste pacote.
         * -> NOTA DO AUTOR: Isso pode ser uma bênção disfarçada, pois encoraja
         * Composição em vez de Herança (Item 18).
         *
         * CONTRA 2: Métodos de fábrica estáticos são difíceis de encontrar na documentação Javadoc.
         * Diferente dos construtores (que ficam em destaque na API Doc), eles se misturam
         * com outros métodos estáticos normais.
         * -> SOLUÇÃO: Seguir as convenções de nomenclatura padronizadas da indústria!
         */

        System.out.println("\n=== NOMENCLATURAS PADRÃO DO MERCADO PARA STATIC FACTORIES ===");
        System.out.println("from      -> Conversão de 1 parâmetro: Date.from(instant)");
        System.out.println("of        -> Agregação de múltiplos parâmetros: List.of(1, 2, 3), EnumSet.of(A, B)");
        System.out.println("valueOf   -> Alternativa a from/of: BigInteger.valueOf(100), String.valueOf(10)");
        System.out.println("instance  -> Retorna instância pré-configurada: StackWalker.getInstance()");
        System.out.println("create    -> Garante a criação de uma NOVA instância: Array.newInstance(...)");
        System.out.println("getType   -> Usado quando a fábrica está em classe diferente: Files.getFileStore(path)");
        System.out.println("newType   -> Idem ao anterior, gerando novo objeto: Files.newBufferedReader(path)");
    }
}