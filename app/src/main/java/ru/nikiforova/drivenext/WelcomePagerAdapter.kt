package ru.nikiforova.drivenext

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter

class WelcomePagerAdapter(
    private val context: Context,
    private val listener: OnButtonClickListener // Добавлен обработчик кнопок
) : PagerAdapter() {

    private val layouts = intArrayOf(
        R.layout.activity_onboarding_first,
        R.layout.activity_onboarding_second,
        R.layout.activity_onboarding_third
    )

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(layouts[position], container, false)

        val nextButton: Button? = view.findViewById(R.id.next_btn)
        val skipButton: Button? = view.findViewById(R.id.miss_btn)

        nextButton?.setOnClickListener {
            listener.onNextClicked(position)
        }

        skipButton?.setOnClickListener {
            listener.onSkipClicked()
        }

        container.addView(view)
        return view
    }

    override fun getCount(): Int = layouts.size

    override fun isViewFromObject(view: View, obj: Any): Boolean = view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }
}

// Интерфейс для обработки нажатий кнопок
interface OnButtonClickListener {
    fun onNextClicked(position: Int)
    fun onSkipClicked()
}
