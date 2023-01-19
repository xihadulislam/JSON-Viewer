# JSON-Viewer
JSON parser and viewer 

### Screenshots

<img src="https://github.com/xihadulislam/JSON-Viewer/blob/master/ss/ss.png" height="500em" /> 

<br/>
<br/>

<img src="https://github.com/xihadulislam/JSON-Viewer/blob/master/ss/ss2.png" height="500em" /> 


#XML code
```xml
<?xml version="1.0" encoding="utf-8"?>
<HorizontalScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/hsv"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true"
    android:orientation="vertical">

    <com.xihadulislam.jsonviewer.JsonView
        android:id="@+id/rv_json"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content" />

</HorizontalScrollView>
```

<br/>
<br/>

#MainActivity.kt

```kt
      Thread {
            val fileInString: String =
                applicationContext.assets.open("demo.json").bufferedReader().use { it.readText() }
            runOnUiThread {
                mRecyclewView.bindJson(fileInString)
            }

        }.start()
```

## LICENSE

```
  @Copyright 2023 xihadulislam, All right reserved.

   Licensed under the Apache License, Version 2.0 (the "License");
   you can use this file except in compliance with the License.
  
       http://www.apache.org/licenses/LICENSE-2.0
       
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
