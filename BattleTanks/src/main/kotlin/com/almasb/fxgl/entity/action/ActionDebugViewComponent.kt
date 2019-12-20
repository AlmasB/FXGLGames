package com.almasb.fxgl.entity.action

import com.almasb.fxgl.dsl.components.view.ChildViewComponent
import com.almasb.fxgl.entity.component.Required
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.scene.text.Text

/**
 * Displays what action is currently being performed by an entity.
 *
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
@Required(ActionComponent::class)
class ActionDebugViewComponent : ChildViewComponent(0.0, -30.0, isTransformApplied = false) {

    // TODO: create an API for DebugViewComponents that auto disable themselves if mode == release?

    private val text = Text()

    private lateinit var actionComponent: ActionComponent

    init {
        // TODO: UI design this
        val bg = Rectangle(100.0, 20.0, Color.color(0.9, 0.9, 0.9, 0.65))

        text.fill = Color.BLUE
        text.font = Font.font(18.0)
        text.translateY = 15.0

        viewRoot.children.addAll(bg, text)
    }

    override fun onAdded() {
        // TODO: not clear immediately that this needs to be called ...
        super.onAdded()

    }

    override fun onUpdate(tpf: Double) {
        text.text = actionComponent.currentAction.toString().removeSuffix("Action")
    }
}