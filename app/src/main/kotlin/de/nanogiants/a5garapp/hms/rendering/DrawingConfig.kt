/**
 * Created by appcom interactive GmbH on 13.09.2020
 * Copyright © 2020 appcom interactive GmbH. All rights reserved.
 */

package de.nanogiants.a5garapp.hms.rendering

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.graphics.drawable.toBitmap


class DrawingConfig(values: DrawingValues) {

  private var values = DrawingValues.default

  fun setPaint(paint: Paint) = apply { values = values.copy(paint = paint) }

  @JvmOverloads
  fun setText(text: TextValues, isMultiline: Boolean = values.isMultiline) = apply {
    values = values.copy(
      text = text,
      isMultiline = isMultiline
    )
  }

  fun setBackground(bitmap: Bitmap) = apply {
    values = values.copy(bitmap = bitmap.copy(bitmap.config, true))
  }

  fun setBackground(drawable: Drawable) = apply {
    val bitmap = drawable.toBitmap(config = Bitmap.Config.ARGB_8888)

    setBackground(bitmap)
  }

  fun draw(adjustmentBody: DrawingValues.() -> Unit = values.defaultPosition) =
    values.draw(adjustmentBody)

  data class TextValues(
    /** Text to be displayed */
    val text: String,
    /** Padding in px - not taken in account when not multiline */
    val padding: Int = 0,
    /** Text alignment, defaults to center - not taken in account when not multiline */
    val alignment: Layout.Alignment = Layout.Alignment.ALIGN_CENTER
  )

  data class DrawingValues(
    val paint: Paint,
    val text: TextValues,
    val isMultiline: Boolean,
    val bitmap: Bitmap
  ) {

    val defaultPosition: DrawingValues.() -> Unit = {
      val canvas = Canvas(bitmap)
      val textWidth = canvas.width - text.padding
      val textLayout = StaticLayout(
        text.text, TextPaint(paint), textWidth, text.alignment, 1f, 0f, false
      )
      val bounds = Rect().apply {
        if (isMultiline) {
          top = 0
          left = 0
          right = textWidth
          bottom = textLayout.height
        } else {
          paint.getTextBounds(text.text, 0, text.text.length, this)
        }
      }

      val x = (bitmap.width - bounds.width()) / 2f
      val y = (bitmap.height - bounds.height()) / 2f

      if (!isMultiline) {
        canvas.drawText(text.text, x, y, paint)
      } else {
        canvas.save()
        canvas.translate(x, y)
        textLayout.draw(canvas)
        canvas.restore()
      }
    }

    fun draw(adjustmentBody: DrawingValues.() -> Unit): Bitmap {
      this.adjustmentBody()
      return bitmap
    }

    companion object {
      val default
        get() = DrawingValues(
          paint = Paint(Paint.ANTI_ALIAS_FLAG),
          text = TextValues(""),
          isMultiline = false,
          bitmap = Bitmap.createBitmap(0, 0, Bitmap.Config.ARGB_8888)
        )
    }
  }

}