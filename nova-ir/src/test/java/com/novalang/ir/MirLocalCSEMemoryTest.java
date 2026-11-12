package com.novalang.ir;

import com.novalang.ir.mir.BasicBlock;
import com.novalang.ir.mir.MirFunction;
import com.novalang.ir.mir.MirInst;
import com.novalang.ir.mir.MirModule;
import com.novalang.ir.mir.MirOp;
import com.novalang.ir.mir.MirTerminator;
import com.novalang.ir.mir.MirType;
import com.novalang.ir.pass.mir.MirLocalCSE;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

/**
 * MirLocalCSE 状态存储的规模回归测试。
 *
 * <p>稠密实现会为每个基本块分配并保留 maxLocal 长度的 int 数组，
 * 在大函数上形成 O(基本块数 × 局部变量数) 的内存占用。</p>
 */
@DisplayName("MirLocalCSE 内存规模")
class MirLocalCSEMemoryTest {

    @Test
    @DisplayName("大量基本块与局部变量不应按块分配全长数组")
    void testLargeFunctionStateIsSparse() {
        int blockCount = 2000;
        int maxLocal = 200_000;
        MirFunction function = new MirFunction("huge", MirType.ofVoid(),
                Collections.emptyList(), Collections.emptySet());
        for (int i = 0; i < blockCount; i++) {
            BasicBlock block = new BasicBlock(i);
            block.addInstruction(new MirInst(MirOp.MOVE, maxLocal - 1,
                    new int[]{maxLocal - 2}, null, null));
            block.setTerminator(new MirTerminator.Goto(null, (i + 1) % blockCount));
            function.getBlocks().add(block);
        }
        MirModule module = new MirModule("test", Collections.emptyList(),
                Collections.singletonList(function));

        assertTimeoutPreemptively(Duration.ofSeconds(10), () ->
                assertDoesNotThrow(() -> new MirLocalCSE().run(module)));
    }
}
