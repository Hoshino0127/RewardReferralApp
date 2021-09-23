package my.edu.tarc.rewardreferralapp

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil.*
import androidx.viewpager2.widget.ViewPager2
import my.edu.tarc.rewardreferralapp.data.IntroSlide
import my.edu.tarc.rewardreferralapp.adapter.IntroSliderAdapter
import my.edu.tarc.rewardreferralapp.databinding.ActivityGetStartedBinding
import my.edu.tarc.rewardreferralapp.functions.CheckUser

class GetStarted : AppCompatActivity() {

    private lateinit var binding: ActivityGetStartedBinding

    private val introSliderAdapter = IntroSliderAdapter (
        listOf (
            IntroSlide(
                "1",
                "1",
                R.drawable.intro_slider_1
            ),
            IntroSlide(
                "2",
                "2",
                R.drawable.intro_slider_2
            ),
            IntroSlide(
                "3",
                "3",
                R.drawable.intro_slider_3
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_started)



        binding = setContentView(this, R.layout.activity_get_started)

        binding.introSliderViewPager.adapter = introSliderAdapter

        setupIndicators()

        setCurrentIndicator(0)

        binding.introSliderViewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        var doppelgangerPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (binding.introSliderViewPager.currentItem == 2){
                    binding.btnGetStarted.text = "Get Started"
                } else {
                    binding.btnGetStarted.text = "Next"
                }
            }
        }

        binding.introSliderViewPager.registerOnPageChangeCallback(doppelgangerPageChangeCallback)

        binding.btnGetStarted.setOnClickListener() {

            if (binding.introSliderViewPager.currentItem + 1 < introSliderAdapter.itemCount){
                binding.introSliderViewPager.currentItem += 1
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setupIndicators(){

        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8,0,8,0)
        for(i in indicators.indices){
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }

            binding.indicatorContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.indicatorContainer.childCount

        for (i in 0 until childCount) {
            val imageView = binding.indicatorContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }

}