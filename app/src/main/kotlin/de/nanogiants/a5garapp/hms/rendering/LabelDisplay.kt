/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.nanogiants.a5garapp.hms.rendering

import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import android.util.Pair
import com.huawei.hiar.ARPlane
import com.huawei.hiar.ARPlane.PlaneType.UNKNOWN_FACING
import com.huawei.hiar.ARPose
import com.huawei.hiar.ARTrackable.TrackingState.TRACKING
import de.nanogiants.a5garapp.hms.Banner
import de.nanogiants.a5garapp.hms.common.ShaderUtil
import de.nanogiants.a5garapp.hms.rendering.WorldShaderUtil.labelProgram
import timber.log.Timber
import java.io.Serializable
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.ArrayList
import java.util.Comparator
import kotlin.math.abs

/**
 * This class demonstrates how to use ARPlane, including how to obtain the center point of a plane.
 * If the plane type can be identified, it is also displayed at the center of the plane. Otherwise,
 * "other" is displayed.
 *
 * @author HW
 * @since 2020-04-08
 */
class LabelDisplay {
  private val textures = IntArray(TEXTURES_SIZE)

  // Allocate a temporary list/matrix here to reduce the number of allocations per frame.
  private val modelMatrix = FloatArray(MATRIX_SIZE)
  private val modelViewMatrix = FloatArray(MATRIX_SIZE)
  private val modelViewProjectionMatrix = FloatArray(MATRIX_SIZE)

  // A 2 * 2 rotation matrix applied to the uv coordinates.
  private val planeAngleUvMatrix = FloatArray(PLANE_ANGLE_MATRIX_SIZE)
  private var mProgram = 0
  private var glPositionParameter = 0
  private var glModelViewProjectionMatrix = 0
  private var glTexture = 0
  private var glPlaneUvMatrix = 0

  /**
   * Create the shader program for label display in the openGL thread.
   * This method will be called when [WorldRenderManager.onSurfaceCreated].
   *
   * @param labelBitmaps View data indicating the plane type.
   */
  fun init(labelBitmaps: List<Bitmap?>) {
    ShaderUtil.checkGlError("Init start.")
    if (labelBitmaps.isEmpty()) {
      Timber.e("No bitmap.")
    }
    createProgram()
    GLES20.glGenTextures(textures.size, textures, 0)
    for ((idx, labelBitmap) in labelBitmaps.withIndex()) {
      // for semantic label plane
      GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + idx)
      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[idx])
      GLES20.glTexParameteri(
        GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR
      )
      GLES20.glTexParameteri(
        GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
      )
      GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, labelBitmap, 0)
      GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
      ShaderUtil.checkGlError("Texture loading")
    }
    ShaderUtil.checkGlError("Init end.")
  }

  private fun createProgram() {
    ShaderUtil.checkGlError("program start.")
    mProgram = labelProgram
    glPositionParameter = GLES20.glGetAttribLocation(mProgram, "inPosXZAlpha")
    glModelViewProjectionMatrix = GLES20.glGetUniformLocation(mProgram, "inMVPMatrix")
    glTexture = GLES20.glGetUniformLocation(mProgram, "inTexture")
    glPlaneUvMatrix = GLES20.glGetUniformLocation(mProgram, "inPlanUVMatrix")
    ShaderUtil.checkGlError("program end.")
  }

  /**
   * Render the plane type at the center of the currently identified plane.
   * This method will be called when [WorldRenderManager.onDrawFrame].
   *
   * @param allPlanes All identified planes.
   * @param cameraPose Location and pose of the current camera.
   * @param cameraProjection Projection matrix of the current camera.
   */
  fun onDrawFrame(allPlanes: Collection<ARPlane>, cameraPose: ARPose, cameraProjection: FloatArray) {
    val sortedPlanes = getSortedPlanes(allPlanes, cameraPose)
    val cameraViewMatrix = FloatArray(MATRIX_SIZE)
    cameraPose.inverse().toMatrix(cameraViewMatrix, 0)
    drawSortedPlans(sortedPlanes, cameraViewMatrix, cameraProjection)
  }

  fun onDrawBannerList(banners: List<Banner>, cameraPose: ARPose, cameraProjection: FloatArray) {
    val cameraViewMatrix = FloatArray(MATRIX_SIZE)
    cameraPose.inverse().toMatrix(cameraViewMatrix, 0)
    drawBanners(banners, cameraViewMatrix, cameraProjection)
  }

  private fun drawBanners(banners: List<Banner>, cameraViewMatrix: FloatArray, cameraProjection: FloatArray) {
    ShaderUtil.checkGlError("Draw sorted plans start.")
    GLES20.glDepthMask(false)
    GLES20.glEnable(GLES20.GL_BLEND)
    GLES20.glBlendFuncSeparate(
      GLES20.GL_DST_ALPHA, GLES20.GL_ONE, GLES20.GL_ZERO, GLES20.GL_ONE_MINUS_SRC_ALPHA
    )
    GLES20.glUseProgram(mProgram)
    GLES20.glEnableVertexAttribArray(glPositionParameter)

    banners.forEach {

      loadImage(it)
      val planeMatrix = FloatArray(MATRIX_SIZE)
      it.anchor.pose.toMatrix(planeMatrix, 0)
      System.arraycopy(planeMatrix, 0, modelMatrix, 0, MATRIX_SIZE)
      val scaleU = 1.0f / LABEL_WIDTH

      // Set the value of the plane angle uv matrix.
      planeAngleUvMatrix[0] = scaleU
      planeAngleUvMatrix[1] = 0.0f
      planeAngleUvMatrix[2] = 0.0f
      val scaleV = 1.0f / LABEL_HEIGHT
      planeAngleUvMatrix[3] = scaleV
//      var idx = plane.label.ordinal
//      Timber.d("Plane getLabel:$idx")
//      idx = Math.abs(idx)
      GLES20.glActiveTexture(GLES20.GL_TEXTURE11)
      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[11])
      GLES20.glUniform1i(glTexture, 11)
      GLES20.glUniformMatrix2fv(glPlaneUvMatrix, 1, false, planeAngleUvMatrix, 0)
      drawBanner(cameraViewMatrix, cameraProjection)
    }
    GLES20.glDisableVertexAttribArray(glPositionParameter)
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    GLES20.glDisable(GLES20.GL_BLEND)
    GLES20.glDepthMask(true)
    ShaderUtil.checkGlError("Draw sorted plans end.")
  }

  private fun loadImage(banner: Banner) {
    GLES20.glActiveTexture(GLES20.GL_TEXTURE11)
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[11])
    GLES20.glTexParameteri(
      GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR
    )
    GLES20.glTexParameteri(
      GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
    )
    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, banner.bitmap, 0)
    GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
  }

  private fun drawBanner(cameraViewMatrix: FloatArray, cameraProjection: FloatArray) {
    ShaderUtil.checkGlError("Draw label start.")
    Matrix.multiplyMM(modelViewMatrix, 0, cameraViewMatrix, 0, modelMatrix, 0)
    Matrix.multiplyMM(modelViewProjectionMatrix, 0, cameraProjection, 0, modelViewMatrix, 0)
    val halfWidth = LABEL_WIDTH / 2.0f
    val halfHeight = LABEL_HEIGHT / 2.0f
    val vertices = floatArrayOf(
      -halfWidth, -halfHeight, 1f,
      -halfWidth, halfHeight, 1f,
      halfWidth, halfHeight, 1f,
      halfWidth, -halfHeight, 1f
    )

    // The size of each floating point is 4 bits.
    val vetBuffer = ByteBuffer.allocateDirect(4 * vertices.size)
      .order(ByteOrder.nativeOrder()).asFloatBuffer()
    vetBuffer.rewind()
    for (i in vertices.indices) {
      vetBuffer.put(vertices[i])
    }
    vetBuffer.rewind()

    // The size of each floating point is 4 bits.
    GLES20.glVertexAttribPointer(
      glPositionParameter, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
      false, 4 * COORDS_PER_VERTEX, vetBuffer
    )

    // Set the sequence of OpenGL drawing points to generate two triangles that form a plane.
    val indices = shortArrayOf(0, 1, 2, 0, 2, 3)

    // Size of the allocated buffer.
    val idxBuffer = ByteBuffer.allocateDirect(2 * indices.size)
      .order(ByteOrder.nativeOrder()).asShortBuffer()
    idxBuffer.rewind()
    for (i in indices.indices) {
      idxBuffer.put(indices[i])
    }
    idxBuffer.rewind()
    GLES20.glUniformMatrix4fv(glModelViewProjectionMatrix, 1, false, modelViewProjectionMatrix, 0)
    GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, idxBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, idxBuffer)
    ShaderUtil.checkGlError("Draw label end.")
  }

  private fun getSortedPlanes(allPlanes: Collection<ARPlane>, cameraPose: ARPose): ArrayList<ARPlane> {
    // Planes must be sorted by the distance from the camera so that we can
    // first draw the closer planes, and have them block the further planes.
    val pairPlanes: MutableList<Pair<ARPlane, Float>> = ArrayList()
    for (plane in allPlanes) {
      if (plane.type == UNKNOWN_FACING
        || plane.trackingState != TRACKING || plane.subsumedBy != null
      ) {
        continue
      }

      // Store the normal vector of the current plane.
      val planeNormalVector = FloatArray(3)
      val planeCenterPose = plane.centerPose
      planeCenterPose.getTransformedAxis(1, 1.0f, planeNormalVector, 0)

      // Calculate the distance from the camera to the plane. If the value is negative,
      // it indicates that the camera is behind the plane (the normal vector distinguishes
      // the front side from the back side).
      val distanceBetweenPlaneAndCamera =
        (cameraPose.tx() - planeCenterPose.tx()) * planeNormalVector[0] + (cameraPose.ty() - planeCenterPose.ty()) * planeNormalVector[1] + (cameraPose.tz() - planeCenterPose.tz()) * planeNormalVector[2]
      pairPlanes.add(Pair(plane, distanceBetweenPlaneAndCamera))
    }
    pairPlanes.sortWith(PlanCompare())
    val sortedPlanes = ArrayList<ARPlane>()
    for (eachPlane in pairPlanes) {
      sortedPlanes.add(eachPlane.first)
    }
    return sortedPlanes
  }

  /**
   * Sort the planes.
   *
   * @author HW
   * @since 2020-04-17
   */
  internal class PlanCompare : Comparator<Pair<ARPlane, Float>>, Serializable {
    override fun compare(planA: Pair<ARPlane, Float>, planB: Pair<ARPlane, Float>): Int {
      return planB.second.compareTo(planA.second)
    }

    companion object {
      private const val serialVersionUID = -7710923839970415650L
    }
  }

  private fun drawSortedPlans(sortedPlanes: ArrayList<ARPlane>, cameraViews: FloatArray, cameraProjection: FloatArray) {
    ShaderUtil.checkGlError("Draw sorted plans start.")
    GLES20.glDepthMask(false)
    GLES20.glEnable(GLES20.GL_BLEND)
    GLES20.glBlendFuncSeparate(
      GLES20.GL_DST_ALPHA, GLES20.GL_ONE, GLES20.GL_ZERO, GLES20.GL_ONE_MINUS_SRC_ALPHA
    )
    GLES20.glUseProgram(mProgram)
    GLES20.glEnableVertexAttribArray(glPositionParameter)
    for (plane in sortedPlanes) {
      val planeMatrix = FloatArray(MATRIX_SIZE)
      plane.centerPose.toMatrix(planeMatrix, 0)
      System.arraycopy(planeMatrix, 0, modelMatrix, 0, MATRIX_SIZE)
      val scaleU = 1.0f / LABEL_WIDTH

      // Set the value of the plane angle uv matrix.
      planeAngleUvMatrix[0] = scaleU
      planeAngleUvMatrix[1] = 0.0f
      planeAngleUvMatrix[2] = 0.0f
      val scaleV = 1.0f / LABEL_HEIGHT
      planeAngleUvMatrix[3] = scaleV
      var idx = plane.label.ordinal
      Timber.d("Plane getLabel:$idx")
      idx = abs(idx)
      GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + idx)
      GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[idx])
      GLES20.glUniform1i(glTexture, idx)
      GLES20.glUniformMatrix2fv(glPlaneUvMatrix, 1, false, planeAngleUvMatrix, 0)
      drawLabel(cameraViews, cameraProjection)
    }
    GLES20.glDisableVertexAttribArray(glPositionParameter)
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    GLES20.glDisable(GLES20.GL_BLEND)
    GLES20.glDepthMask(true)
    ShaderUtil.checkGlError("Draw sorted plans end.")
  }

  private fun drawLabel(cameraViews: FloatArray, cameraProjection: FloatArray) {
    ShaderUtil.checkGlError("Draw label start.")
    Matrix.multiplyMM(modelViewMatrix, 0, cameraViews, 0, modelMatrix, 0)
    Matrix.multiplyMM(modelViewProjectionMatrix, 0, cameraProjection, 0, modelViewMatrix, 0)
    val halfWidth = LABEL_WIDTH / 2.0f
    val halfHeight = LABEL_HEIGHT / 2.0f
    val vertices = floatArrayOf(
      -halfWidth, -halfHeight, 1f,
      -halfWidth, halfHeight, 1f,
      halfWidth, halfHeight, 1f,
      halfWidth, -halfHeight, 1f
    )

    // The size of each floating point is 4 bits.
    val vetBuffer = ByteBuffer.allocateDirect(4 * vertices.size)
      .order(ByteOrder.nativeOrder()).asFloatBuffer()
    vetBuffer.rewind()
    for (i in vertices.indices) {
      vetBuffer.put(vertices[i])
    }
    vetBuffer.rewind()

    // The size of each floating point is 4 bits.
    GLES20.glVertexAttribPointer(
      glPositionParameter, COORDS_PER_VERTEX, GLES20.GL_FLOAT,
      false, 4 * COORDS_PER_VERTEX, vetBuffer
    )

    // Set the sequence of OpenGL drawing points to generate two triangles that form a plane.
    val indices = shortArrayOf(0, 1, 2, 0, 2, 3)

    // Size of the allocated buffer.
    val idxBuffer = ByteBuffer.allocateDirect(2 * indices.size)
      .order(ByteOrder.nativeOrder()).asShortBuffer()
    idxBuffer.rewind()
    for (i in indices.indices) {
      idxBuffer.put(indices[i])
    }
    idxBuffer.rewind()
    GLES20.glUniformMatrix4fv(glModelViewProjectionMatrix, 1, false, modelViewProjectionMatrix, 0)
    GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, idxBuffer.limit(), GLES20.GL_UNSIGNED_SHORT, idxBuffer)
    ShaderUtil.checkGlError("Draw label end.")
  }

  companion object {
    private val LS = System.lineSeparator()
    private const val COORDS_PER_VERTEX = 3
    private const val LABEL_WIDTH = 0.3f
    private const val LABEL_HEIGHT = 0.3f
    private const val TEXTURES_SIZE = 12
    private const val MATRIX_SIZE = 16
    private const val PLANE_ANGLE_MATRIX_SIZE = 4
  }
}