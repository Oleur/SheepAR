package com.js.sheepar.ui.sheepassets

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.js.sheepar.R
import com.js.sheepar.databinding.ActivitySheepAssetsBinding
import com.js.sheepar.domain.box.AssetFilter
import com.js.sheepar.ui.extension.checkIsSupportedDeviceOrFinish
import java.util.concurrent.CompletableFuture

class SheepAssetsActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context) = Intent(context, SheepAssetsActivity::class.java)
    }

    private lateinit var binding: ActivitySheepAssetsBinding

    private var sheepAsset1Renderable: ModelRenderable? = null
    private var sheepAsset2Renderable: ModelRenderable? = null
    private var sheepAsset3Renderable: ModelRenderable? = null
    private var arFragment: ArFragment? = null

    private var hasFinishedLoading: Boolean = false
    private var assetFilter = AssetFilter.Sheep1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkIsSupportedDeviceOrFinish()) {
            return
        }

        binding = ActivitySheepAssetsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        arFragment = supportFragmentManager.findFragmentById(R.id.sheepAssetFragment) as ArFragment

        // Build all the sheep models.
        makeSheepAssets()

        // Set up a tap gesture detector.
        arFragment?.setOnTapArPlaneListener { hitResult, _, _ ->
            if (!hasFinishedLoading) {
                return@setOnTapArPlaneListener
            }

            // Create the Anchor.
            val anchorNode = AnchorNode(hitResult.createAnchor())
            anchorNode.setParent(arFragment!!.arSceneView.scene)

            // Create the transformable andy and add it to the anchor.
            TransformableNode(arFragment!!.transformationSystem).apply {
                renderable = when(assetFilter) {
                    AssetFilter.Sheep1 -> sheepAsset1Renderable
                    AssetFilter.Sheep2 -> sheepAsset2Renderable
                    AssetFilter.Sheep3 -> sheepAsset3Renderable
                }
                setParent(anchorNode)
                select()
            }
        }

        binding.asset1.setOnClickListener {
            assetFilter = AssetFilter.Sheep1
            applyFilter()
        }
        binding.asset2.setOnClickListener {
            assetFilter = AssetFilter.Sheep2
            applyFilter()
        }
        binding.asset3.setOnClickListener {
            assetFilter = AssetFilter.Sheep3
            applyFilter()
        }
    }

    private fun makeSheepAssets() {
        val sheep1 = ModelRenderable.builder().setSource(this, Uri.parse("sheep1.sfb")).build()
        val sheep2 = ModelRenderable.builder().setSource(this, Uri.parse("scene.sfb")).build()
        val sheep3 = ModelRenderable.builder().setSource(this, Uri.parse("PUSHILIN_sheep.sfb")).build()

        CompletableFuture.allOf(sheep1, sheep2, sheep3).handle { _, _ ->
            sheepAsset1Renderable = sheep1.get()
            sheepAsset2Renderable = sheep2.get()
            sheepAsset3Renderable = sheep3.get()

            binding.progress.visibility = View.GONE

            hasFinishedLoading = true
        }
    }

    private fun applyFilter() {
        when (assetFilter) {
            AssetFilter.Sheep1 -> {
                binding.asset1.alpha = 1f
                binding.asset2.alpha = 0.2f
                binding.asset3.alpha = 0.2f
            }
            AssetFilter.Sheep2 -> {
                binding.asset1.alpha = 0.2f
                binding.asset2.alpha = 1f
                binding.asset3.alpha = 0.2f
            }
            AssetFilter.Sheep3 -> {
                binding.asset1.alpha = 0.2f
                binding.asset2.alpha = 0.2f
                binding.asset3.alpha = 0.5f
            }
        }
    }
}
