package net.sportified.signage.controls

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import net.sportified.signage.R

class SettingsMenuButton : AppCompatButton {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    private fun init() {
//        width= resources.getDimension(R.dimen.your_dimen).toInt()
//        height= resources.getDimension(R.dimen.your_dimen).toInt()
//
//        setBackgroundColor(ContextCompat.getColor(context,R.color.yourColor))
    }

}