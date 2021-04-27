/*
 * Copyright 2021 Artyom Mironov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.artyommironov.colorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.RectF
import android.graphics.Shader.TileMode
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class ColorPicker : View {
  private val rects = Array(4) { RectF() }
  private val paint = Paint()
  private val hsv = listOf(Bar.H, Bar.S, Bar.V).map { it.max.toFloat() }.toFloatArray()
  private var argb: Int = Color.WHITE
  private var currentBar: Bar? = null
  var alphaBackground by Delegates.observable<Drawable>(
    SquareTileDrawable(8.dp, Color.WHITE, Color.LTGRAY)
  ) { _, _, _ -> invalidate() }
  @get:Px
  var barHeight by Delegates.observable(32.dp) { _, old, new -> if (new != old) requestLayout() }
  @get:Px
  var barSpacing by Delegates.observable(8.dp) { _, old, new -> if (new != old) requestLayout() }
  var mode by Delegates.observable(Mode.HSVA) { _, old, new -> if (new != old) requestLayout() }
  var onColorChange: () -> Unit = {}
  var color: Int
    @ColorInt get() = getPanelColor(argb.alpha)
    set(value) = setPanelColor(value)

  constructor(ctx: Context) : super(ctx)
  constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs)
  constructor(ctx: Context, attrs: AttributeSet, defStyle: Int) : super(ctx, attrs, defStyle)

  init {
    paint.style = Style.FILL
    paint.isAntiAlias = true
    setWillNotDraw(false)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    getBars().forEach {
      drawBar(canvas, it)
      drawThumb(canvas, it)
    }
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    rects.forEachIndexed { index, rect ->
      rect.set(
        paddingLeft.toFloat(),
        (paddingTop + barHeight * index + barSpacing * index).toFloat(),
        (width - paddingRight).toFloat(),
        (paddingTop + barHeight * (index + 1) + barSpacing * index).toFloat()
      )
    }
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val w = MeasureSpec.getSize(widthMeasureSpec)
    val h = when (mode) {
      Mode.A -> barHeight
      Mode.HSV, Mode.RGB -> barHeight * 3 + barSpacing * 2
      Mode.HSVA, Mode.RGBA -> barHeight * 4 + barSpacing * 3
    } + paddingTop + paddingBottom
    setMeasuredDimension(w, h)
  }

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouchEvent(event: MotionEvent): Boolean {
    currentBar = when (event.action) {
      MotionEvent.ACTION_DOWN -> getBars().find { getRect(it).contains(event.x, event.y) }
      MotionEvent.ACTION_MOVE -> currentBar
      else -> null
    }
    val bar = currentBar ?: return super.onTouchEvent(event)
    val rect = getRect(bar).takeIf { it.width() > 0 } ?: return super.onTouchEvent(event)
    setBarValue(bar, (event.x - rect.left).coerceIn(0f, rect.width()) * bar.max / rect.width())
    invalidate()
    onColorChange()
    return true
  }

  private fun drawThumb(canvas: Canvas, bar: Bar) {
    val rect = getRect(bar)
    val thumbRadius = 1.dp
    val x = getBarValue(bar) * rect.width() / bar.max + rect.left
    val left = (x - thumbRadius).coerceIn(rect.left, rect.right - thumbRadius * 2f)
    val right = (x + thumbRadius).coerceIn(rect.left + thumbRadius * 2f, rect.right)
    val dark = when (mode) {
      Mode.HSV, Mode.HSVA -> getBarValue(Bar.V) < 0.5f
      else -> color.red + color.green + color.blue < 128 * 3
    }
    paint.color = if (dark) Color.WHITE else Color.BLACK
    paint.shader = null
    canvas.drawRect(left, rect.top, right, rect.bottom, paint)
  }

  private fun drawBar(canvas: Canvas, bar: Bar) {
    val rect = getRect(bar)
    if (bar == Bar.A) {
      alphaBackground.setBounds(rect.left.toInt(), rect.top.toInt(), rect.right.toInt(), rect.bottom.toInt())
      alphaBackground.draw(canvas)
    }
    val colors = when (bar) {
      Bar.H -> IntArray(Bar.H.max) { getHSVColor(Bar.A.max, h = it.toFloat()) }
      Bar.S -> intArrayOf(getHSVColor(Bar.A.max, s = 0f), getHSVColor(Bar.A.max, s = Bar.S.max.toFloat()))
      Bar.V -> intArrayOf(getHSVColor(Bar.A.max, v = 0f), getHSVColor(Bar.A.max, v = Bar.V.max.toFloat()))
      Bar.R -> intArrayOf(getRGBColor(Bar.A.max, r = 0), getRGBColor(Bar.A.max, r = Bar.R.max))
      Bar.G -> intArrayOf(getRGBColor(Bar.A.max, g = 0), getRGBColor(Bar.A.max, g = Bar.G.max))
      Bar.B -> intArrayOf(getRGBColor(Bar.A.max, b = 0), getRGBColor(Bar.A.max, b = Bar.B.max))
      Bar.A -> intArrayOf(getPanelColor(0), getPanelColor(Bar.A.max))
    }
    paint.shader = when (colors.size == 2) {
      true -> LinearGradient(rect.left, rect.top, rect.right, rect.top, colors[0], colors[1], TileMode.CLAMP)
      else -> LinearGradient(rect.left, rect.top, rect.right, rect.top, colors, null, TileMode.CLAMP)
    }
    canvas.drawRect(rect, paint)
  }

  private fun getBarValue(bar: Bar): Float = when (bar) {
    Bar.H -> hsv[0]
    Bar.S -> hsv[1]
    Bar.V -> hsv[2]
    Bar.R -> argb.red.toFloat()
    Bar.G -> argb.green.toFloat()
    Bar.B -> argb.blue.toFloat()
    Bar.A -> argb.alpha.toFloat()
  }

  private fun setBarValue(bar: Bar, value: Float) {
    when (bar) {
      Bar.H -> hsv[0] = value
      Bar.S -> hsv[1] = value
      Bar.V -> hsv[2] = value
      Bar.R -> argb = getRGBColor(r = value.toInt())
      Bar.G -> argb = getRGBColor(g = value.toInt())
      Bar.B -> argb = getRGBColor(b = value.toInt())
      Bar.A -> argb = getRGBColor(a = value.toInt())
    }
    when (bar) {
      Bar.H, Bar.S, Bar.V -> argb = color
      Bar.R, Bar.G, Bar.B -> Color.RGBToHSV(argb.red, argb.green, argb.blue, hsv)
      else -> Unit
    }
  }

  private fun getBars(): List<Bar> = when (mode) {
    Mode.HSVA -> listOf(Bar.H, Bar.S, Bar.V, Bar.A)
    Mode.RGBA -> listOf(Bar.R, Bar.G, Bar.B, Bar.A)
    Mode.HSV -> listOf(Bar.H, Bar.S, Bar.V)
    Mode.RGB -> listOf(Bar.R, Bar.G, Bar.B)
    Mode.A -> listOf(Bar.A)
  }

  private fun getRect(bar: Bar): RectF = when {
    bar == Bar.S || bar == Bar.G -> rects[1]
    bar == Bar.V || bar == Bar.B -> rects[2]
    bar == Bar.A && mode != Mode.A -> rects[3]
    else -> rects[0]
  }

  private fun setPanelColor(@ColorInt color: Int) {
    argb = color
    Color.RGBToHSV(color.red, color.green, color.blue, hsv)
    invalidate()
  }

  @ColorInt
  private fun getPanelColor(a: Int): Int = when (mode) {
    Mode.HSV, Mode.HSVA -> getHSVColor(a = a)
    else -> getRGBColor(a = a)
  }

  @ColorInt
  private fun getHSVColor(a: Int = argb.alpha, h: Float = hsv[0], s: Float = hsv[1], v: Float = hsv[2]): Int {
    return Color.HSVToColor(a, floatArrayOf(h, s , v))
  }

  @ColorInt
  private fun getRGBColor(a: Int = argb.alpha, r: Int = argb.red, g: Int = argb.green, b: Int = argb.blue): Int {
    return Color.argb(a, r, g, b)
  }

  private val Int.dp: Int get() = (this * resources.displayMetrics.density).roundToInt()

  enum class Mode { HSVA, RGBA, HSV, RGB, A }

  private enum class Bar(val max: Int) { H(360), S(1), V(1), R(255), G(255), B(255), A(255) }
}
