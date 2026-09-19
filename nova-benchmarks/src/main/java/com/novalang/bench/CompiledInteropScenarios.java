package com.novalang.bench;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 只用于已编译 Nova 字节码的 Java 互操作场景。
 */
final class CompiledInteropScenarios {

    static final int INTEROP_CALLS_PER_RUN = 10000;
    private static final int CHECKSUM_MODULUS = 1000000007;
    private static final Map<String, CompiledInteropScenario> SCENARIOS = buildScenarios();

    private CompiledInteropScenarios() {
    }

    static CompiledInteropScenario byName(String name) {
        CompiledInteropScenario scenario = SCENARIOS.get(name);
        if (scenario == null) {
            throw new IllegalArgumentException("Unknown compiled interop scenario: " + name);
        }
        return scenario;
    }

    static Map<String, CompiledInteropScenario> all() {
        return SCENARIOS;
    }

    private static Map<String, CompiledInteropScenario> buildScenarios() {
        Map<String, CompiledInteropScenario> scenarios = new LinkedHashMap<String, CompiledInteropScenario>();
        scenarios.put("member_price_quote", new CompiledInteropScenario(
                "member_price_quote",
                "简单：会员价格计算，quoteMemberPrice() × 10000",
                memberPriceQuoteSource(),
                INTEROP_CALLS_PER_RUN,
                new java.util.function.IntSupplier() {
                    @Override
                    public int getAsInt() {
                        return runMemberPriceQuoteNative();
                    }
                }));
        scenarios.put("order_line_settlement", new CompiledInteropScenario(
                "order_line_settlement",
                "中等：订单行结算，settleOrderLine() × 10000",
                orderLineSettlementSource(),
                INTEROP_CALLS_PER_RUN,
                new java.util.function.IntSupplier() {
                    @Override
                    public int getAsInt() {
                        return runOrderLineSettlementNative();
                    }
                }));
        scenarios.put("payment_risk_evaluation", new CompiledInteropScenario(
                "payment_risk_evaluation",
                "复杂：支付风险评分，evaluatePaymentRisk() × 10000",
                paymentRiskEvaluationSource(),
                INTEROP_CALLS_PER_RUN,
                new java.util.function.IntSupplier() {
                    @Override
                    public int getAsInt() {
                        return runPaymentRiskEvaluationNative();
                    }
                }));
        return Collections.unmodifiableMap(scenarios);
    }

    private static String memberPriceQuoteSource() {
        return "import java com.novalang.bench.CompiledInteropBusinessApi\n"
                + "\n"
                + "fun run(): Int {\n"
                + "  var checksum = 0\n"
                + "  for (i in 0..<10000) {\n"
                + "    val quoted = CompiledInteropBusinessApi.quoteMemberPrice(1299 + (i % 97), i % 4, i % 13)\n"
                + "    checksum = (checksum + quoted) % 1000000007\n"
                + "  }\n"
                + "  return checksum\n"
                + "}\n"
                + "run()";
    }

    private static String orderLineSettlementSource() {
        return "import java com.novalang.bench.CompiledInteropBusinessApi\n"
                + "\n"
                + "fun run(): Int {\n"
                + "  var rollingState = 17\n"
                + "  var checksum = 0\n"
                + "  for (i in 0..<10000) {\n"
                + "    rollingState = CompiledInteropBusinessApi.settleOrderLine(599 + (i % 401), 1 + (i % 5), i % 4, i % 7, rollingState)\n"
                + "    checksum = (checksum + rollingState) % 1000000007\n"
                + "  }\n"
                + "  return checksum\n"
                + "}\n"
                + "run()";
    }

    private static String paymentRiskEvaluationSource() {
        return "import java com.novalang.bench.CompiledInteropBusinessApi\n"
                + "\n"
                + "fun run(): Int {\n"
                + "  var riskState = 23\n"
                + "  var checksum = 0\n"
                + "  for (i in 0..<10000) {\n"
                + "    riskState = CompiledInteropBusinessApi.evaluatePaymentRisk(100000 + i, 1000 + ((i * 37) % 90000), i % 5, i % 8, (i * 11) % 100, riskState)\n"
                + "    checksum = (checksum + riskState) % 1000000007\n"
                + "  }\n"
                + "  return checksum\n"
                + "}\n"
                + "run()";
    }

    private static int runMemberPriceQuoteNative() {
        int checksum = 0;
        for (int i = 0; i < INTEROP_CALLS_PER_RUN; i++) {
            int quoted = CompiledInteropBusinessApi.quoteMemberPrice(1299 + (i % 97), i % 4, i % 13);
            checksum = (checksum + quoted) % CHECKSUM_MODULUS;
        }
        return checksum;
    }

    private static int runOrderLineSettlementNative() {
        int rollingState = 17;
        int checksum = 0;
        for (int i = 0; i < INTEROP_CALLS_PER_RUN; i++) {
            rollingState = CompiledInteropBusinessApi.settleOrderLine(
                    599 + (i % 401), 1 + (i % 5), i % 4, i % 7, rollingState);
            checksum = (checksum + rollingState) % CHECKSUM_MODULUS;
        }
        return checksum;
    }

    private static int runPaymentRiskEvaluationNative() {
        int riskState = 23;
        int checksum = 0;
        for (int i = 0; i < INTEROP_CALLS_PER_RUN; i++) {
            riskState = CompiledInteropBusinessApi.evaluatePaymentRisk(
                    100000 + i, 1000 + ((i * 37) % 90000), i % 5, i % 8,
                    (i * 11) % 100, riskState);
            checksum = (checksum + riskState) % CHECKSUM_MODULUS;
        }
        return checksum;
    }
}
