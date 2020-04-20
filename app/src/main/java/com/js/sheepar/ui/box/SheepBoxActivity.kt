package com.js.sheepar.ui.box

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.js.sheepar.R
import com.js.sheepar.databinding.ActivitySheepBoxBinding
import com.js.sheepar.domain.box.BoxFilter
import com.js.sheepar.ui.extension.checkIsSupportedDeviceOrFinish

class SheepBoxActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, SheepBoxActivity::class.java)
    }

    private lateinit var binding: ActivitySheepBoxBinding

    private var sheepBoxRenderable: ModelRenderable? = null
    private var arFragment: ArFragment? = null

    private var boxFilter: BoxFilter = BoxFilter.Color

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkIsSupportedDeviceOrFinish()) {
            return
        }

        binding = ActivitySheepBoxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arFragment = supportFragmentManager.findFragmentById(R.id.sheepBoxFragment) as ArFragment

        // Display a colored textured cube
        makeColoredSheepBox()

        arFragment?.setOnTapArPlaneListener { hitResult, _, _ ->
            if (sheepBoxRenderable == null) {
                return@setOnTapArPlaneListener
            }

            // Create the Anchor.
            val anchorNode = AnchorNode(hitResult.createAnchor())
            anchorNode.setParent(arFragment!!.arSceneView.scene)

            // Create the transformable node and add it to the anchor.
            TransformableNode(arFragment!!.transformationSystem).apply {
                setParent(anchorNode)
                renderable = sheepBoxRenderable
                select()
            }
        }

        binding.texture1.setOnClickListener {
            boxFilter = BoxFilter.Color
            applyFilter()
            makeColoredSheepBox()
        }
        binding.texture2.setOnClickListener {
            boxFilter = BoxFilter.Texture
            applyFilter()
            makeTextureSheepBox()
        }
        binding.texture3.setOnClickListener {
            boxFilter = BoxFilter.Transparency
            applyFilter()
            makeTransparentColoredSheepBox()
        }
    }

    private fun makeColoredSheepBox() {
        MaterialFactory.makeOpaqueWithColor(this, Color(getColor(R.color.colorAccent)))
            .thenAccept { material ->
                sheepBoxRenderable = ShapeFactory.makeCube(
                    Vector3(0.2f, 0.2f, 0.2f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material)
            }
    }

    private fun makeTextureSheepBox() {
        Texture.builder().setSource(BitmapFactory.decodeResource(resources, R.drawable.texture_cardboard))
            .build()
            .thenAccept { texture ->
                MaterialFactory.makeOpaqueWithTexture(this, texture)
                    .thenAccept { material ->
                        sheepBoxRenderable = ShapeFactory.makeCube(
                            Vector3(0.2f, 0.2f, 0.2f),
                            Vector3(0.0f, 0.15f, 0.0f),
                            material)
                    }
            }
    }

    private fun makeTransparentColoredSheepBox() {
        MaterialFactory.makeTransparentWithColor(this,
            Color(getColor(R.color.colorAccent50)))
            .thenAccept { material ->
                sheepBoxRenderable = ShapeFactory.makeCube(
                    Vector3(0.2f, 0.2f, 0.2f),
                    Vector3(0.0f, 0.15f, 0.0f),
                    material)
            }
    }

    private fun applyFilter() {
        when (boxFilter) {
            BoxFilter.Color -> {
                binding.texture1.alpha = 1f
                binding.texture2.alpha = 0.2f
                binding.texture3.alpha = 0.2f
            }
            BoxFilter.Texture -> {
                binding.texture1.alpha = 0.2f
                binding.texture2.alpha = 1f
                binding.texture3.alpha = 0.2f
            }
            BoxFilter.Transparency -> {
                binding.texture1.alpha = 0.2f
                binding.texture2.alpha = 0.2f
                binding.texture3.alpha = 0.5f
            }
        }
    }
}
