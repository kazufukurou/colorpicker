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

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.Px

class SquareTileDrawable(
  @Px private val size: Int,
  @ColorInt private val color1: Int,
  @ColorInt private val color2: Int
) : Drawable() {
  private val paint = Paint()
  private val rect = Rect()

  override fun getOpacity(): Int = PixelFormat.OPAQUE

  override fun setAlpha(alpha: Int) {
    paint.alpha = alpha
  }

  override fun setColorFilter(colorFilter: ColorFilter?) {
    paint.colorFilter = colorFilter
  }

  override fun draw(canvas: Canvas) {
    paint.color = color1
    canvas.drawRect(bounds, paint)
    paint.color = color2
    var x = bounds.left
    var y = bounds.top
    var oddLine = false
    while (y < bounds.bottom) {
      if (x >= bounds.right) {
        y += size
        oddLine = !oddLine
        x = bounds.left + (if (oddLine) size else 0)
      }
      rect.set(x, y, (x + size).coerceAtMost(bounds.right), (y + size).coerceAtMost(bounds.bottom))
      canvas.drawRect(rect, paint)
      x += 2 * size
    }
  }
}
