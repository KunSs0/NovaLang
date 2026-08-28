package com.novalang.runtime.codegen;

import com.novalang.runtime.CompiledNova;
import com.novalang.runtime.Nova;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 逐项验证 Zeus hotbar 从 JavaScript 迁移到 Nova 所需的语言与宿主契约。
 */
@DisplayName("Zeus UI 迁移边界可行性")
class CompiledUiMigrationFeasibilityTest {

    private static final String COMPONENT_SOURCE =
            "import java com.novalang.runtime.codegen.BoxNodeFixture\n" +
            "import java com.novalang.runtime.codegen.CarouselLibFixture\n" +
            "import java com.novalang.runtime.codegen.DisposableFixture\n" +
            "import java com.novalang.runtime.codegen.EventListenerFixture\n" +
            "import java com.novalang.runtime.codegen.NodeFixture\n" +
            "import java com.novalang.runtime.codegen.SelectedUpdateEventFixture\n" +
            "import java com.novalang.runtime.codegen.TweenPropsFixture\n" +
            "import java com.novalang.runtime.codegen.UiComponentFixture\n" +
            "import java com.novalang.runtime.codegen.UiHostFixture\n" +
            "import java java.lang.Runnable\n" +
            "class InventoryListener(val owner: UiComponentFixture) : EventListenerFixture {\n" +
            "    fun onEvent(key: String, event: Any) { owner.onInventoryEvent(event) }\n" +
            "}\n" +
            "class HotbarTweenProps(val targetAlpha: Double) : TweenPropsFixture {\n" +
            "    fun alpha(): Double = targetAlpha\n" +
            "}\n" +
            "class TweenCompletion(val owner: UiComponentFixture) : Runnable {\n" +
            "    fun run() { owner.onTweenComplete() }\n" +
            "}\n" +
            "class HotbarComponent : UiComponentFixture {\n" +
            "    var host: UiHostFixture? = null\n" +
            "    var eventHandle: DisposableFixture? = null\n" +
            "    var controller: DisposableFixture? = null\n" +
            "    var driver: NodeFixture? = null\n" +
            "    var tip: NodeFixture? = null\n" +
            "    var selectedSlot: Int = -1\n" +
            "    var tweenCompleteCount: Int = 0\n" +
            "    var disposed: Boolean = false\n" +
            "    fun onAttach(context: UiHostFixture) {\n" +
            "        host = context\n" +
            "        val library = context.libs().get(\"carousel\") as CarouselLibFixture\n" +
            "        controller = library.createController(context.lifecycle())\n" +
            "        val slots = context.root().getDeepChild(\"slots\") as BoxNodeFixture\n" +
            "        tip = context.root().getDeepChild(\"tip\") as BoxNodeFixture\n" +
            "        driver = context.graphics().builder()\n" +
            "            .box()\n" +
            "            .name(\"carousel_driver\")\n" +
            "            .visible(false)\n" +
            "            .width(0.0)\n" +
            "            .height(0.0)\n" +
            "            .parent(slots)\n" +
            "            .attach()\n" +
            "        eventHandle = context.events().on(\"zeus:player_inventory/selected_update\", InventoryListener(this))\n" +
            "    }\n" +
            "    fun onInventoryEvent(event: Any) {\n" +
            "        val selectedEvent = event as SelectedUpdateEventFixture\n" +
            "        selectedSlot = selectedEvent.getSelectedSlot()\n" +
            "    }\n" +
            "    fun playTween() {\n" +
            "        val activeHost = host\n" +
            "        val activeTip = tip\n" +
            "        if (activeHost != null && activeTip != null) {\n" +
            "            activeHost.tween().to(activeTip, HotbarTweenProps(1.0), 10.0, TweenCompletion(this))\n" +
            "        }\n" +
            "    }\n" +
            "    fun onTweenComplete() { tweenCompleteCount = tweenCompleteCount + 1 }\n" +
            "    fun onDestroy() {\n" +
            "        if (disposed) {\n" +
            "            return\n" +
            "        }\n" +
            "        disposed = true\n" +
            "        val activeHandle = eventHandle\n" +
            "        if (activeHandle != null) {\n" +
            "            activeHandle.dispose()\n" +
            "            eventHandle = null\n" +
            "        }\n" +
            "        val activeHost = host\n" +
            "        val activeTip = tip\n" +
            "        if (activeHost != null && activeTip != null) {\n" +
            "            activeHost.tween().clearAll(activeTip)\n" +
            "        }\n" +
            "        val activeDriver = driver\n" +
            "        if (activeDriver != null) {\n" +
            "            activeDriver.removeSelf()\n" +
            "            driver = null\n" +
            "        }\n" +
            "        val activeController = controller\n" +
            "        if (activeController != null) {\n" +
            "            activeController.dispose()\n" +
            "            controller = null\n" +
            "        }\n" +
            "        tip = null\n" +
            "        host = null\n" +
            "    }\n" +
            "    fun getSelectedSlot(): Int = selectedSlot\n" +
            "    fun getTweenCompleteCount(): Int = tweenCompleteCount\n" +
            "    fun isDisposed(): Boolean = disposed\n" +
            "}\n" +
            "fun createUiComponent(): UiComponentFixture = HotbarComponent()\n";

    private static CompiledNova compiled;

    @BeforeAll
    static void compileComponent() {
        compiled = new Nova().compileToBytecode(COMPONENT_SOURCE, "hotbar-migration-feasibility.nova");
    }

    @Test
    @DisplayName("libs.get 保持注册表语法并可强转为声明库类型")
    void libraryRegistryShouldReturnDeclaredLibraryInstance() {
        AttachedFixture fixture = attachComponent();

        assertEquals("carousel", fixture.host.libs().getLastRequestedId());
        assertEquals(1, fixture.carousel.getControllerCreateCount());
        assertNotNull(fixture.carousel.getLastController());
    }

    @Test
    @DisplayName("getDeepChild 返回节点基类后可强转为声明节点类型")
    void deepChildLookupShouldSupportDeclaredNodeCast() {
        AttachedFixture fixture = attachComponent();
        UiHostFixture.BoxBuilder builder = fixture.host.graphics().state().getLastBoxBuilder();

        assertSame(fixture.host.slots(), builder.getParent());

        fixture.component.playTween();

        assertSame(fixture.host.tip(), fixture.host.tween().getTarget());
    }

    @Test
    @DisplayName("box 非空并保留连续链式 builder 调用")
    void boxBuilderShouldRemainNonNullAndFluent() {
        AttachedFixture fixture = attachComponent();
        UiHostFixture.BuilderEntry entry = fixture.host.graphics().state();
        UiHostFixture.BoxBuilder builder = entry.getLastBoxBuilder();

        assertEquals(1, entry.getBoxCallCount());
        assertNotNull(builder);
        assertEquals("carousel_driver", builder.getName());
        assertFalse(builder.isVisible());
        assertEquals(0.0, builder.getWidth());
        assertEquals(0.0, builder.getHeight());
        assertSame(fixture.host.slots(), builder.getParent());
        assertInstanceOf(BoxNodeFixture.class, builder.getAttachedNode());
    }

    @Test
    @DisplayName("事件总线持有声明监听器并由组件强转事件载荷")
    void eventBusShouldUseDeclaredListenerAndTypedPayloadCast() {
        AttachedFixture fixture = attachComponent();

        assertTrue(fixture.host.events().getListenerClassName().contains("InventoryListener"));
        assertFalse(fixture.host.events().getListenerClassName().contains("Lambda"));

        fixture.host.events().emit(
                "zeus:player_inventory/selected_update",
                new SelectedUpdateEventFixture(6));

        assertEquals(6, fixture.component.getSelectedSlot());
    }

    @Test
    @DisplayName("Tween 使用声明参数类和声明完成回调实例")
    void tweenShouldUseDeclaredPropsAndCompletionInstances() {
        AttachedFixture fixture = attachComponent();

        fixture.component.playTween();

        assertInstanceOf(TweenPropsFixture.class, fixture.host.tween().getProps());
        assertEquals(1.0, fixture.host.tween().getProps().alpha());
        assertEquals(10.0, fixture.host.tween().getDuration());
        assertTrue(fixture.host.tween().getCompletionClassName().contains("TweenCompletion"));
        assertFalse(fixture.host.tween().getCompletionClassName().contains("Lambda"));

        fixture.host.tween().finish();

        assertEquals(1, fixture.component.getTweenCompleteCount());
    }

    @Test
    @DisplayName("同一组件实例持有资源并按 hotbar 顺序幂等销毁")
    void componentShouldDisposeRetainedResourcesExactlyOnce() {
        AttachedFixture fixture = attachComponent();
        fixture.component.playTween();
        UiHostFixture.TrackedDisposable eventHandle = fixture.host.events().getHandle();
        UiHostFixture.TrackedDisposable controller = fixture.carousel.getLastController();
        BoxNodeFixture driver = fixture.host.graphics().state().getLastBoxBuilder().getAttachedNode();

        fixture.component.onDestroy();
        fixture.component.onDestroy();

        assertTrue(fixture.component.isDisposed());
        assertEquals(1, eventHandle.getDisposeCount());
        assertEquals(1, fixture.host.tween().getClearCount());
        assertEquals(1, driver.getRemoveCount());
        assertEquals(1, controller.getDisposeCount());
        assertEquals(List.of(
                "event.dispose",
                "tween.clearAll",
                "driver.removeSelf",
                "controller.dispose"), fixture.host.lifecycle().entries());
    }

    private static AttachedFixture attachComponent() {
        UiHostFixture host = new UiHostFixture();
        CarouselLibFixture carousel = new CarouselLibFixture();
        host.libs().register(carousel);
        UiComponentFixture component = assertInstanceOf(
                UiComponentFixture.class, compiled.call("createUiComponent"));

        component.onAttach(host);

        return new AttachedFixture(host, carousel, component);
    }

    private static final class AttachedFixture {

        private final UiHostFixture host;
        private final CarouselLibFixture carousel;
        private final UiComponentFixture component;

        private AttachedFixture(
                UiHostFixture host,
                CarouselLibFixture carousel,
                UiComponentFixture component) {
            this.host = host;
            this.carousel = carousel;
            this.component = component;
        }
    }
}
