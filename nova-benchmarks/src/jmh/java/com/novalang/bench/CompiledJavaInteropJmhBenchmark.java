package com.novalang.bench;

import com.novalang.runtime.CompiledNova;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

/**
 * 仅衡量已生成 JVM 字节码的 Nova → Java 互操作执行时间。
 * 每次基准调用都完成一个固定含 10,000 次 Java API 调用的业务场景。
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 8, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Fork(value = 1, jvmArgsAppend = {"-Xms512m", "-Xmx512m"})
@Threads(1)
public class CompiledJavaInteropJmhBenchmark {

    @State(Scope.Benchmark)
    public static class ScenarioState {

        @Param({"member_price_quote", "order_line_settlement", "payment_risk_evaluation"})
        public String scenario;

        CompiledInteropScenario interopScenario;
        CompiledNova novaCompiled;
        int expectedResult;

        @Setup(Level.Trial)
        public void setUp() {
            interopScenario = CompiledInteropScenarios.byName(scenario);
            if (interopScenario.getInteropCallsPerRun() != CompiledInteropScenarios.INTEROP_CALLS_PER_RUN) {
                throw new IllegalStateException("Unexpected interop call count for " + scenario
                        + ": " + interopScenario.getInteropCallsPerRun());
            }

            expectedResult = interopScenario.runJavaNative();
            novaCompiled = ScriptBenchSupport.compileNova(interopScenario.getNovaSource());
            if (!novaCompiled.isBytecodeMode()) {
                throw new IllegalStateException("Nova did not produce bytecode for " + scenario);
            }

            int compiledResult = ScriptBenchSupport.toInt(novaCompiled.run());
            if (compiledResult != expectedResult) {
                throw new IllegalStateException("Nova compiled mismatch for " + scenario
                        + ": expected=" + expectedResult + ", actual=" + compiledResult);
            }
        }
    }

    /**
     * 测量阶段只运行 Setup 已生成的 CompiledNova，不包含解析、MIR 执行或字节码生成。
     */
    @Benchmark
    public Object novaCompiledRun(ScenarioState state) {
        return state.novaCompiled.run();
    }

    /**
     * 同一 10,000 次业务 API 调用的 Java Native 基线。
     */
    @Benchmark
    public int javaNative(ScenarioState state) {
        return state.interopScenario.runJavaNative();
    }
}
