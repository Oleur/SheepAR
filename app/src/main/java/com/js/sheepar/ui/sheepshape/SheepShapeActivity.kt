package com.js.sheepar.ui.sheepshape

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.ar.core.Config
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.QuaternionEvaluator
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.math.Vector3Evaluator
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.js.sheepar.R
import com.js.sheepar.core.plus
import com.js.sheepar.databinding.ActivitySheepShapeBinding
import com.js.sheepar.ui.extension.checkIsSupportedDeviceOrFinish

class SheepShapeActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, SheepShapeActivity::class.java)
    }

    private lateinit var binding: ActivitySheepShapeBinding

    private var sheepNode: SheepNode? = null
    private var sheepHeadNode: Node? = null

    private var sheepHead: ModelRenderable? = null
    private var sheepLeftEar: ModelRenderable? = null
    private var sheepRightEar: ModelRenderable? = null
    private var sheepMainBody: ModelRenderable? = null
    private var sheepWoolBody1: ModelRenderable? = null
    private var sheepWoolBody2: ModelRenderable? = null
    private var sheepWoolBody3: ModelRenderable? = null
    private var sheepWoolBody4: ModelRenderable? = null
    private var sheepLeftFrontLeg: ModelRenderable? = null
    private var sheepRightFrontLeg: ModelRenderable? = null
    private var sheepLeftBackLeg: ModelRenderable? = null
    private var sheepRightBackLeg: ModelRenderable? = null

    private var arFragment: ArFragment? = null

    private var headbangAnimatorSet: AnimatorSet? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkIsSupportedDeviceOrFinish()) {
            return
        }

        binding = ActivitySheepShapeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arFragment = supportFragmentManager.findFragmentById(R.id.sheepShapeFragment) as ArFragment

        // Display a colored textured cube
        makeSheepAssets()


        val config = arFragment?.arSceneView?.session?.config?.apply {
            lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR

            /*val isDepthSupported = arFragment?.arSceneView?.session?.isDepthModeSupported(Config.DepthMode.AUTOMATIC)
            if (isDepthSupported!!) {
                depthMode = Config.DepthMode.AUTOMATIC
            }*/
        }
        arFragment?.arSceneView?.session?.configure(config)

        arFragment?.arSceneView?.isLightDirectionUpdateEnabled = true
        arFragment?.arSceneView?.isLightEstimationEnabled = true
        arFragment?.setOnTapArPlaneListener { hitResult, _, _ ->
            if (sheepNode != null) {
                return@setOnTapArPlaneListener
            }

            createSheep(hitResult)
        }
    }

    private fun createSheep(hitResult: HitResult) {
        // Create the Anchor.
        val anchorNode = AnchorNode(hitResult.createAnchor())
        anchorNode.setParent(arFragment!!.arSceneView.scene)

        sheepNode = SheepNode(this) { isChecked ->
            if (headbangAnimatorSet == null) {
                headbangAnimatorSet = createHeadbangAnimator(Quaternion.axisAngle(Vector3(0f, 0f, 1f), -45f))
            }
            if (isChecked) headbangAnimatorSet?.start() else headbangAnimatorSet?.cancel()
        }.apply {
            setParent(anchorNode)
            localRotation = Quaternion.axisAngle(Vector3(0f, 1f, 0f), -90f)
        }

        val legPositions = arrayOf(
            Vector3(-0.1f, 0f, 0.1f), Vector3(0.1f, 0f, 0.1f),
            Vector3(-0.1f, 0f, -0.1f), Vector3(0.1f, 0f, -0.1f)
        )
        val legRenderables = arrayOf(
            sheepRightBackLeg, sheepRightFrontLeg,
            sheepLeftBackLeg, sheepLeftFrontLeg
        )
        legPositions.forEachIndexed { index, legPosition ->
            Node().apply {
                renderable = legRenderables[index]
                localPosition = legPosition
                setParent(sheepNode)
            }
        }

        // Build body node
        val bodyPositions = arrayOf(
            Vector3(0f, 0.08f, 0f), Vector3(0f, 0f, 0.08f),
            Vector3(0f, 0f, -0.08f), Vector3(-0.08f, 0f, 0f)
        )
        val bodyRenderables = arrayOf(
            sheepWoolBody1, sheepWoolBody2,
            sheepWoolBody3, sheepWoolBody4
        )
        Node().apply {
            renderable = sheepMainBody
            localPosition = Vector3(0f, 0.15f, 0f)
            setParent(sheepNode)
        }.also { bodyNode ->
            bodyPositions.forEachIndexed { index, bodyPosition ->
                Node().apply {
                    renderable = bodyRenderables[index]
                    localPosition = bodyPosition
                    setParent(bodyNode)
                }
            }
        }

        sheepHeadNode = Node().apply {
            renderable = sheepHead
            localPosition = Vector3(0.2f, 0.15f, 0f)
            setParent(sheepNode)
        }.also { headNode ->
            Node().apply {
                renderable = sheepLeftEar
                localPosition = Vector3(0f, 0.05f, 0.06f)
                setParent(headNode)
            }

            Node().apply {
                renderable = sheepLeftEar
                localPosition = Vector3(0f, 0.05f, -0.06f)
                setParent(headNode)
            }
        }
    }

    private fun makeSheepAssets() {
        MaterialFactory.makeOpaqueWithColor(this, Color(ResourcesCompat.getColor(resources, R.color.sheepBlack, theme)))
            .thenAccept { material ->
                sheepHead = ShapeFactory.makeSphere(0.1f, Vector3(0.0f, 0.0f, 0.0f), material)
                sheepLeftEar = ShapeFactory.makeSphere(0.05f, Vector3(0.0f, 0.0f, 0.0f), material)
                sheepRightEar = ShapeFactory.makeSphere(0.05f, Vector3(0.0f, 0.0f, 0.0f), material)
                sheepLeftBackLeg = ShapeFactory.makeCylinder(0.05f, 0.2f, Vector3(0.0f, 0.0f, 0.0f), material)
                sheepLeftFrontLeg = ShapeFactory.makeCylinder(0.05f, 0.2f, Vector3(0.0f, 0.0f, 0.0f), material)
                sheepRightBackLeg = ShapeFactory.makeCylinder(0.05f, 0.2f, Vector3(0.0f, 0.0f, 0.0f), material)
                sheepRightFrontLeg = ShapeFactory.makeCylinder(0.05f, 0.2f, Vector3(0.0f, 0.0f, 0.0f), material)
            }

        Texture.builder().setSource(BitmapFactory.decodeResource(resources, R.drawable.texture_sheepwool))
            .build()
            .thenAccept {
                MaterialFactory.makeOpaqueWithTexture(this, it)
                    .thenAccept { material ->
                        sheepMainBody = ShapeFactory.makeSphere(0.2f, Vector3(0.0f, 0.0f, 0.0f), material)
                        sheepWoolBody1 = ShapeFactory.makeSphere(0.15f, Vector3(0.0f, 0.0f, 0.0f), material)
                        sheepWoolBody2 = ShapeFactory.makeSphere(0.15f, Vector3(0.0f, 0.0f, 0.0f), material)
                        sheepWoolBody3 = ShapeFactory.makeSphere(0.15f, Vector3(0.0f, 0.0f, 0.0f), material)
                        sheepWoolBody4 = ShapeFactory.makeSphere(0.15f, Vector3(0.0f, 0.0f, 0.0f), material)
                    }
            }
    }

    private fun createHeadbangAnimator(headRotation: Quaternion) = AnimatorSet().apply {
        val rotation = Quaternion.multiply(
            headRotation, Quaternion.axisAngle(
                Vector3(0f, 0.5f, 0f),
                45f
            )
        )
        val startPosition = sheepHeadNode!!.localPosition
        val endPosition = startPosition + Vector3(0f, -0.05f, 0f)
        playTogether(
            headRotationAnimator(rotation), headTranslationAnimator(
                startPosition,
                endPosition
            )
        )
    }

    private fun headRotationAnimator(headRotation: Quaternion) = ObjectAnimator().apply {
        setObjectValues(headRotation)
        setPropertyName("localRotation")
        setEvaluator(QuaternionEvaluator())
        interpolator = AccelerateInterpolator()
        target = sheepHeadNode
        duration = 200
        repeatMode = ObjectAnimator.REVERSE
        repeatCount = ObjectAnimator.INFINITE
    }

    private fun headTranslationAnimator(startPosition: Vector3, endPosition: Vector3) = ObjectAnimator().apply {
        setObjectValues(startPosition, endPosition)
        setPropertyName("localPosition")
        setEvaluator(Vector3Evaluator())
        interpolator = AccelerateInterpolator()
        target = sheepHeadNode
        duration = 200
        repeatMode = ObjectAnimator.REVERSE
        repeatCount = ObjectAnimator.INFINITE
    }
}
