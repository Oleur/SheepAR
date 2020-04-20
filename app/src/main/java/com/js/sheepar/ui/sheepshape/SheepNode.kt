package com.js.sheepar.ui.sheepshape

import android.animation.ObjectAnimator
import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.CheckBox
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Vector3Evaluator
import com.google.ar.sceneform.rendering.ViewRenderable
import com.js.sheepar.R

class SheepNode(
    private val context: Context,
    private val action: (isChecked: Boolean) -> Unit
) : Node(), Node.OnTapListener {

    private var animator: ObjectAnimator? = null
    private var sheepCard: Node? = null

    init {
        setOnTapListener(this)
    }

    override fun onActivate() {
        if (scene == null) {
            throw IllegalStateException("Scene is null!")
        }

        if (sheepCard == null) {
            sheepCard = Node()
            sheepCard!!.setParent(this)
            sheepCard!!.isEnabled = false
            sheepCard!!.localPosition = Vector3(0f, 0.5f, 0f)
            sheepCard!!.localRotation = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 90f)
            sheepCard!!.localScale = Vector3(0.5f, 0.5f, 0.5f)

            ViewRenderable.builder()
                .setView(context, R.layout.layout_sheep_card)
                .build()
                .thenAccept { renderable ->
                    sheepCard!!.renderable = renderable
                    bindViews(renderable.view)
                    animator = createAnimator(localPosition, Vector3(0f, 0f, 0.5f))
                }
        }
    }

    override fun onDeactivate() {
        animator?.cancel()
        animator = null
    }

    private fun bindViews(root: View) {
        root.findViewById<CheckBox>(R.id.sheep_card_action_move).setOnCheckedChangeListener { _, isChecked ->
            action(isChecked)
            if (isChecked) {
                animator?.start()
            } else {
                animator?.cancel()
                createAnimator(localPosition, Vector3(0f, 0f, 0.0f)).apply {
                    repeatCount = 0
                    duration = 500
                }.start()
            }
        }
    }

    private fun createAnimator(startPosition: Vector3, endPosition: Vector3) = ObjectAnimator().apply {
        setObjectValues(startPosition, endPosition)
        setPropertyName("localPosition")
        setEvaluator(Vector3Evaluator())
        interpolator = LinearInterpolator()
        target = this@SheepNode
        duration = 2000
        repeatMode = ObjectAnimator.REVERSE
        repeatCount = ObjectAnimator.INFINITE
    }

    override fun onTap(hitTestResult: HitTestResult, motionEvent: MotionEvent) {
        sheepCard?.isEnabled = !sheepCard!!.isEnabled
    }
}
