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

package com.artyommironov.colorpickersample

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.artyommironov.colorpicker.ColorPicker
import com.artyommironov.colorpicker.ColorTextWatcher
import com.artyommironov.colorpickersample.databinding.MainBinding

class MainActivity : Activity() {
  private val binding by lazy { MainBinding.inflate(layoutInflater) }
  private val colorTextWatcher by lazy { ColorTextWatcher(binding.colorPicker) }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(binding.root)
    init()
    render()
  }

  private fun init() = with(binding) {
    edit.addTextChangedListener(colorTextWatcher)
    buttonColorMode.setOnClickListener {
      colorPicker.mode = ColorPicker.Mode.values().run {
        this[(colorPicker.mode.ordinal + 1).takeIf { it < size } ?: 0]
      }
      render()
    }
    colorPicker.onColorChange = ::render
    colorPicker.color = Color.CYAN
  }

  private fun render() = with(binding) {
    colorTextWatcher.updateEditText(edit)
    viewColor.setBackgroundColor(colorPicker.color)
    buttonColorMode.text = colorPicker.mode.name
  }
}
