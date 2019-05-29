package com.polarlooptheory.loopaint

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import kotlinx.android.synthetic.main.activity_main.*
import android.app.Dialog
import android.provider.MediaStore
import android.widget.Button
import androidx.core.graphics.drawable.DrawableCompat
import com.github.nisrulz.sensey.Sensey
import com.github.nisrulz.sensey.ShakeDetector
import com.pes.androidmaterialcolorpickerdialog.ColorPicker
import java.util.*

class MainActivity : AppCompatActivity() {

    private var paint = true
    private var color : Int = Color.BLACK

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sizebutton.text = drawView.getSize().toInt().toString()
        changeButtonColor(drawView.getColor())
        Sensey.getInstance().init(this)
        val shakeListener = object : ShakeDetector.ShakeListener {
            override fun onShakeDetected() {
                val size = drawView.getSize()
                if(paint)
                    drawView.clearCanvas(color,size)
                else
                    drawView.clearCanvas(Color.WHITE,size)
            }

            override fun onShakeStopped() {
                print("stopped")
            }
        }
        Sensey.getInstance().startShakeDetection(30f,10,shakeListener)
        //region Buttons
        clearbutton.setOnClickListener {
            val size = drawView.getSize()
            if(paint)
                drawView.clearCanvas(color,size)
            else
                drawView.clearCanvas(Color.WHITE,size)
        }
        erasebutton.setOnClickListener {
            if(paint){
                drawView.changeColor(Color.WHITE)
                erasebutton.setImageResource(R.drawable.paint)
                colorbutton.isEnabled = false
            }
            else {
                drawView.changeColor(color)
                erasebutton.setImageResource(R.drawable.eraser)
                colorbutton.isEnabled = true
            }
            paint = !paint
        }
        sizebutton.setOnClickListener {
            val d = Dialog(this@MainActivity)
            d.setTitle("NumberPicker")
            d.setContentView(R.layout.numberdialog)
            val b1 = d.findViewById(R.id.button1) as Button
            val b2 = d.findViewById(R.id.button2) as Button
            val np = d.findViewById(R.id.numberPicker1) as NumberPicker
            np.minValue = 4
            np.maxValue = 64
            np.wrapSelectorWheel = false
            np.value = drawView.getSize().toInt()
            b1.setOnClickListener{
                drawView.changeSize(np.value.toFloat())
                sizebutton.text = np.value.toString()
                d.dismiss()
            }
            b2.setOnClickListener{
                d.dismiss() // dismiss the dialog
            }
            d.show()
        }
        colorbutton.setOnClickListener {
            val col = Color.valueOf(drawView.getColor())
            val r = (col.red()*255).toInt()
            val g = (col.green()*255).toInt()
            val b = (col.blue()*255).toInt()
            val cp = ColorPicker(this, r, g, b)
            cp.show()
            cp.setCallback {
                val clr = Color.rgb(cp.red,cp.green,cp.blue)
                changeButtonColor(clr)
                drawView.changeColor(clr)
                color = clr
                cp.dismiss()
            }
        }
        savebutton.setOnClickListener {
            val generator = Random()
            var n = 10000
            n = generator.nextInt(n)
            val fname = "Image-$n.jpg"
            MediaStore.Images.Media.insertImage(contentResolver,drawView.bitmap(),fname,"Paint screen")
        }
        //endregion

    }

    fun changeButtonColor(color: Int){
        var buttonDrawable = colorbutton.background
        buttonDrawable = DrawableCompat.wrap(buttonDrawable)
        buttonDrawable.setTint(color)
        colorbutton.background = buttonDrawable
    }

    override fun onDestroy() {
        super.onDestroy()
        Sensey.getInstance().stop()
    }
}
