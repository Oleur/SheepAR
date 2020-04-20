package com.js.sheepar.ui.home

import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.view.animation.BounceInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.js.sheepar.databinding.ActivityMainBinding
import com.js.sheepar.ui.box.SheepBoxActivity
import com.js.sheepar.ui.sheepassets.SheepAssetsActivity
import com.js.sheepar.ui.sheepshape.SheepShapeActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.image.setOnClickListener {
            animateSheep { /*Do nothing*/ }
        }

        binding.arDemo1.setOnClickListener {
            animateSheep {
                startActivity(SheepBoxActivity.newIntent(this))
            }
        }
        binding.arDemo2.setOnClickListener {
            animateSheep {
                startActivity(SheepShapeActivity.newIntent(this))
            }
        }
        binding.arDemo3.setOnClickListener {
            animateSheep {
                startActivity(SheepAssetsActivity.newIntent(this))
            }
        }
        binding.arDemo4.setOnClickListener { }
    }

    private fun animateSheep(action: () -> Unit) {
        binding.image.animate().translationY(-150f).setDuration(200).setInterpolator(AccelerateInterpolator()).withEndAction {
            binding.image.animate().translationY(0f).setDuration(200).setInterpolator(BounceInterpolator()).withEndAction(action)
        }
    }
}
