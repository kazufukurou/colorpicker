[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](https://opensource.org/licenses/Apache-2.0)

# colorpicker
Simple color picker for Android

## Setup
In root `build.gradle`
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

In module `build.gradle`
```
dependencies {
  implementation 'com.github.artyommironov:anyadapter:1.0.0'
}
```

## Usage

In layout
```xml
<com.artyommironov.colorpicker.ColorPicker
    android:id="@+id/colorPicker"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```
In code
```kotlin
colorPicker.barSpacing = dpToPx(10)
colorPicker.barHeight = dpToPx(40)
colorPicker.mode = ColorPicker.Mode.RGBA
colorPicker.onColorChange = { view.setBackgroundColor(colorPicker.color) }
colorPicker.color = Color.CYAN
```

## Screenshot
![sample](/sample.png)

## License
```txt
Copyright 2021 Artyom Mironov

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
