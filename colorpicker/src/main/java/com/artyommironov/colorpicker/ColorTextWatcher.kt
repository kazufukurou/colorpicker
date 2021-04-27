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

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class ColorTextWatcher(private val colorPicker: ColorPicker) : TextWatcher {
  private var isEditingColor = false

  override fun beforeTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}
  override fun onTextChanged(p1: CharSequence, p2: Int, p3: Int, p4: Int) {}

  override fun afterTextChanged(editable: Editable) {
    isEditingColor = true
    try {
      colorPicker.color = Color.parseColor("#${editable.toString().padEnd(8, '0')}")
    } catch (e: Exception) {
    }
    isEditingColor = false
  }

  fun updateEditText(edit: EditText) {
    if (isEditingColor) return

    edit.removeTextChangedListener(this)
    edit.setText("")
    edit.append(Integer.toHexString(colorPicker.color).padStart(8, '0'))
    edit.addTextChangedListener(this)
  }
}
