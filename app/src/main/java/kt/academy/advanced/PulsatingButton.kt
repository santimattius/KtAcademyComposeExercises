package kt.academy.advanced

import android.animation.ValueAnimator
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun Modifier.pulsatingBorder(
    color: Color,
    pulseDuration: Duration = 1.seconds,
    borderWidth: Dp = 4.dp,
): Modifier = this then PulsatingBorderElement(color, pulseDuration, borderWidth)

private data class PulsatingBorderElement(
    val color: Color,
    val pulseDuration: Duration,
    val borderWidth: Dp,
) : ModifierNodeElement<PulsatingBorderNode>() {
    override fun create(): PulsatingBorderNode = PulsatingBorderNode(color, pulseDuration, borderWidth)

    override fun update(node: PulsatingBorderNode) {
        val durationChanged = node.pulseDuration != pulseDuration
        node.color = color
        node.pulseDuration = pulseDuration
        node.borderWidth = borderWidth
        if (durationChanged) {
            node.updateAnimator()
        } else {
            node.invalidateDraw()
        }
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "pulsatingBorder"
        properties["color"] = color
        properties["pulseDuration"] = pulseDuration
        properties["borderWidth"] = borderWidth
    }
}

private class PulsatingBorderNode(
    var color: Color,
    var pulseDuration: Duration,
    var borderWidth: Dp
) : Modifier.Node(), DrawModifierNode {

    private var alpha = 0.3f
    private var animator: ValueAnimator? = null

    override fun onAttach() {
        updateAnimator()
    }

    override fun onDetach() {
        animator?.cancel()
        animator = null
    }

    fun updateAnimator() {
        animator?.cancel()
        animator = createAnimator().apply {
            start()
        }
    }

    private fun createAnimator() = ValueAnimator.ofFloat(0.3f, 1f).apply {
        duration = pulseDuration.inWholeMilliseconds
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener { animator ->
            this@PulsatingBorderNode.alpha = animator.animatedValue as Float
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        drawRect(
            color = color.copy(alpha = alpha),
            style = Stroke(width = borderWidth.toPx()),
            size = size
        )
    }
}

@Preview
@Composable
fun PulsatingBorderPreview() {
    Text(
        text = "Pulsating Button",
        modifier = Modifier
            .padding(16.dp)
            .pulsatingBorder(Color.Blue)
            .padding(16.dp)
    )
}
