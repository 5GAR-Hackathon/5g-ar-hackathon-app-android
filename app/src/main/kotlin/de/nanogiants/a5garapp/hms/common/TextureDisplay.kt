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
package de.nanogiants.a5garapp.hms.common

import android.opengl.GLES11Ext
import android.opengl.GLES20
import com.huawei.hiar.ARFrame
import de.nanogiants.a5garapp.hms.common.MatrixUtil.getProjectionMatrix
import de.nanogiants.a5garapp.hms.common.MatrixUtil.originalMatrix
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

/**
 * This is a common class for drawing camera textures that you can use to display camera images on the screen.
 *
 * @author hw
 * @since 2020-03-25
 */
class TextureDisplay {
  /**
   * Obtain the texture ID.
   *
   * @return Texture id.
   */
  var externalTextureId = 0
    private set
  private var mProgram = 0
  private var mPosition = 0
  private var mCoord = 0
  private var mMatrix = 0
  private var mTexture = 0
  private var mCoordMatrix = 0
  private var mVerBuffer: FloatBuffer? = null
  private var mTexTransformedBuffer: FloatBuffer? = null
  private var mTexBuffer: FloatBuffer? = null
  private val mProjectionMatrix = FloatArray(MATRIX_SIZE)
  private val coordMatrixs: FloatArray

  /**
   * The constructor is a texture rendering utility class, used to create a texture rendering object.
   */
  init {
    coordMatrixs = originalMatrix
    initBuffers()
  }

  /**
   * This method is called when [android.opengl.GLSurfaceView.Renderer.onSurfaceChanged]
   * to update the projection matrix.
   *
   * @param width Width.
   * @param height Height
   */
  fun onSurfaceChanged(width: Int, height: Int) {
    getProjectionMatrix(mProjectionMatrix, width, height)
  }

  /**
   * This method is called when [android.opengl.GLSurfaceView.Renderer.onSurfaceCreated]
   * to initialize the texture ID and create the OpenGL ES shader program.
   */
  fun init() {
    val textures = IntArray(1)
    GLES20.glGenTextures(1, textures, 0)
    externalTextureId = textures[0]
    generateExternalTexture()
    createProgram()
  }

  /**
   * If the texture ID has been created externally, this method is called when
   * [android.opengl.GLSurfaceView.Renderer.onSurfaceCreated].
   *
   * @param textureId Texture id.
   */
  fun init(textureId: Int) {
    externalTextureId = textureId
    generateExternalTexture()
    createProgram()
  }

  /**
   * Render each frame. This method is called when [android.opengl.GLSurfaceView.Renderer.onDrawFrame].
   *
   * @param frame ARFrame
   */
  fun onDrawFrame(frame: ARFrame?) {
    checkGlError("On draw frame start.")
    if (frame == null) {
      return
    }
    if (frame.hasDisplayGeometryChanged()) {
      frame.transformDisplayUvCoords(mTexBuffer, mTexTransformedBuffer)
    }
    clear()
    GLES20.glDisable(GLES20.GL_DEPTH_TEST)
    GLES20.glDepthMask(false)
    GLES20.glUseProgram(mProgram)

    // Set the texture ID.
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, externalTextureId)

    // Set the projection matrix.
    GLES20.glUniformMatrix4fv(mMatrix, 1, false, mProjectionMatrix, 0)
    GLES20.glUniformMatrix4fv(mCoordMatrix, 1, false, coordMatrixs, 0)

    // Set the vertex.
    GLES20.glEnableVertexAttribArray(mPosition)
    GLES20.glVertexAttribPointer(mPosition, 2, GLES20.GL_FLOAT, false, 0, mVerBuffer)

    // Set the texture coordinates.
    GLES20.glEnableVertexAttribArray(mCoord)
    GLES20.glVertexAttribPointer(mCoord, 2, GLES20.GL_FLOAT, false, 0, mTexTransformedBuffer)

    // Number of vertices.
    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    GLES20.glDisableVertexAttribArray(mPosition)
    GLES20.glDisableVertexAttribArray(mCoord)
    GLES20.glDepthMask(true)
    GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    checkGlError("On draw frame end.")
  }

  private fun generateExternalTexture() {
    GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, externalTextureId)
    GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE)
    GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE)
    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST.toFloat())
    GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST.toFloat())
  }

  private fun createProgram() {
    mProgram = createGlProgram()
    mPosition = GLES20.glGetAttribLocation(mProgram, "vPosition")
    mCoord = GLES20.glGetAttribLocation(mProgram, "vCoord")
    mMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix")
    mTexture = GLES20.glGetUniformLocation(mProgram, "vTexture")
    mCoordMatrix = GLES20.glGetUniformLocation(mProgram, "vCoordMatrix")
  }

  private fun initBuffers() {
    // Initialize the size of the vertex buffer.
    val byteBufferForVer = ByteBuffer.allocateDirect(32)
    byteBufferForVer.order(ByteOrder.nativeOrder())
    mVerBuffer = byteBufferForVer.asFloatBuffer().apply {
      put(POS)
      position(0)
    }

    // Initialize the size of the texture buffer.
    val byteBufferForTex = ByteBuffer.allocateDirect(32)
    byteBufferForTex.order(ByteOrder.nativeOrder())
    mTexBuffer = byteBufferForTex.asFloatBuffer().apply {
      put(COORD)
      position(0)
    }

    // Initialize the size of the transformed texture buffer.
    val byteBufferForTransformedTex = ByteBuffer.allocateDirect(32)
    byteBufferForTransformedTex.order(ByteOrder.nativeOrder())
    mTexTransformedBuffer = byteBufferForTransformedTex.asFloatBuffer()
  }

  /**
   * Clear canvas.
   */
  private fun clear() {
    GLES20.glClearColor(RGB_CLEAR_VALUE, RGB_CLEAR_VALUE, RGB_CLEAR_VALUE, 1.0f)
    GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT)
  }

  companion object {
    private val LS = System.lineSeparator()
    private val BASE_FRAGMENT = ("#extension GL_OES_EGL_image_external : require" + LS
        + "precision mediump float;" + LS
        + "varying vec2 textureCoordinate;" + LS
        + "uniform samplerExternalOES vTexture;" + LS
        + "void main() {" + LS
        + "    gl_FragColor = texture2D(vTexture, textureCoordinate );" + LS
        + "}")
    private val BASE_VERTEX = ("attribute vec4 vPosition;" + LS
        + "attribute vec2 vCoord;" + LS
        + "uniform mat4 vMatrix;" + LS
        + "uniform mat4 vCoordMatrix;" + LS
        + "varying vec2 textureCoordinate;" + LS
        + "void main(){" + LS
        + "    gl_Position = vMatrix*vPosition;" + LS
        + "    textureCoordinate = (vCoordMatrix*vec4(vCoord,0,1)).xy;" + LS
        + "}")

    // Coordinates of a vertex.
    private val POS = floatArrayOf(-1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, -1.0f)

    // Texture coordinates.
    private val COORD = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f)
    private const val MATRIX_SIZE = 16
    private const val RGB_CLEAR_VALUE = 0.8157f
    private fun createGlProgram(): Int {
      val vertex = loadShader(GLES20.GL_VERTEX_SHADER, BASE_VERTEX)
      if (vertex == 0) {
        return 0
      }
      val fragment = loadShader(GLES20.GL_FRAGMENT_SHADER, BASE_FRAGMENT)
      if (fragment == 0) {
        return 0
      }
      var program = GLES20.glCreateProgram()
      if (program != 0) {
        GLES20.glAttachShader(program, vertex)
        GLES20.glAttachShader(program, fragment)
        GLES20.glLinkProgram(program)
        val linkStatus = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GLES20.GL_TRUE) {
          glError("Could not link program:" + GLES20.glGetProgramInfoLog(program))
          GLES20.glDeleteProgram(program)
          program = 0
        }
      }
      return program
    }

    private fun loadShader(shaderType: Int, source: String): Int {
      var shader = GLES20.glCreateShader(shaderType)
      if (0 != shader) {
        GLES20.glShaderSource(shader, source)
        GLES20.glCompileShader(shader)
        val compiled = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0)
        if (compiled[0] == 0) {
          glError("Could not compile shader:$shaderType")
          glError("GLES20 Error:" + GLES20.glGetShaderInfoLog(shader))
          GLES20.glDeleteShader(shader)
          shader = 0
        }
      }
      return shader
    }

    private fun glError(index: Any) {
      Timber.e("glError: $index")
    }
  }
}