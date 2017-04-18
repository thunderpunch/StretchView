# StretchView

a stretchable view which is used together with CoordinatorLayout

the effect is shown as below

|bottom|right|
|:---:|:---:|
|![vertical](/gif/bottom.gif)|![horizontal](/gif/right.gif)|


## Usage

1. 在布局中添加StretchView,并确保它是CoordinatorLayout的直系子视图。

2. 在StretchView内部添加一个子视图作为内容视图，且只能添加一个。

3. 添加一个NestedScrollingChild作为嵌套滑动发起方，同时绑定自定义的TranslationBehavior。

   ```xml
   <android.support.design.widget.CoordinatorLayout
   	android:layout_width="match_parent"
   	android:layout_height="match_parent"
   	tools:context="com.thunderpunch.stretchview.MainActivity">

   	<com.thunderpunch.stretchview.view.StretchView
       	android:id="@+id/sv"
       	style="@style/AppTheme.AppBarOverlay"
       	android:layout_width="match_parent"
       	android:layout_height="wrap_content"
       	app:stretchFactor="0.4"
       	android:background="@color/colorPrimary"
       	app:direction="bottom">

       	<!-- StretchView can host only one direct child -->

   	</com.thunderpunch.stretchview.view.StretchView>
     
   	<android.support.v4.widget.NestedScrollView
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       app:layout_behavior="com.thunderpunch.stretchview.behavior.TranslationBehavior">
     
   	</android.support.v4.widget.NestedScrollView>
   </android.support.design.widget.CoordinatorLayout>
   ```

4. （可选）添加嵌套滑动过程中的回调

   ```java
   final StretchView sv = (StretchView) findViewById(R.id.sv);
   sv.setOnStretchListener(new StretchView.OnStretchListener() {
       @Override
       public void onTranslation(int trans) {
           if (sv.getDirection() == StretchView.BOTTOM || sv.getDirection() == StretchView.RIGHT) {
               trans = -trans;
           }
           if (trans > 0) {
               if (trans >= sv.getContentSpace()) {
                   //拉伸状态
                  
               } else {
                   //非拉伸状态
                  
               }
           }
       }
   });
   ```

## XML attributes

| name          | format | description                              |
| ------------- | ------ | ---------------------------------------- |
| stretchFactor | float  | 拉伸比例 ( 拉伸比例 x StretchView直系子视图在拉伸方向的边长 = 最大拉伸量 ) |
| direction     | enum   | 初始隐藏方位 支持left，right，bottom，当 direction = left 或 right 时，StretchView将响应横向的嵌套滑动，当direction = bottom 时，响应纵向嵌套滑动 |



## Customizations

- **自定义childview的绘制方式**

  如果不设置，默认使用DefaultDrawHelper，绘制效果见[bottom.gif](/gif/bottom.gif)

  ```java
  public void setDrawHelper(StretchDrawHelper drawHelper) {
      this.mDrawHelper = drawHelper;
  }
  ```

  ```java
  public static abstract class StretchDrawHelper {

      protected StretchView v;

      public StretchDrawHelper(StretchView v) {
          this.v = v;
      }

      /**
       * @return 是否支持布局方向 {@link StretchView.DirectionOption}
       */
      public abstract boolean supportDirection(@DirectionOption int direction);

      /**
       * 绘制childview前的回调
       *
       * @return 绘制childview用到的变换矩阵
       */
      public abstract Matrix draw(Canvas canvas, int translation);

      /**
       * chidlview绘制完成后的回调
       */
      public void onDrawComplete(Canvas canvas, int translation) {

      }
  }
  ```

  ```java
  @Override
  protected void dispatchDraw(Canvas canvas) {
      final View child = getChildAt(0);
      if (child == null || child.getVisibility() == GONE) return;
      if (mDrawHelper == null) {
          mDrawHelper = new DefaultDrawHelper(this);
      }
      if (mDrawHelper.supportDirection(mDirection)) {
          canvas.save();
          canvas.concat(mDrawHelper.draw(canvas, mTranslation));
          drawChild(canvas, child, getDrawingTime());
          canvas.restore();
          mDrawHelper.onDrawComplete(canvas, mTranslation);
      } else {
          super.dispatchDraw(canvas);
      }
  }
  ```



## License

```
MIT License

Copyright (c) 2017 thunderpunch

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
