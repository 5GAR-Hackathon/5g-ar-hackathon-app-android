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

import android.opengl.GLES20
import timber.log.Timber

/**
 * This class is used to read shader code and compile links.
 *
 * @author HW
 * @since 2020-04-05
 */
object ShaderUtil {

  /**
   * Check OpenGL ES running exceptions and throw them when necessary.
   *
   * @param label Program label.
   */
  fun checkGlError(label: String) {
    var lastError = GLES20.GL_NO_ERROR
    var error = GLES20.glGetError()
    while (error != GLES20.GL_NO_ERROR) {
      Timber.e("$label: glError $error")
      lastError = error
      error = GLES20.glGetError()
    }
    if (lastError != GLES20.GL_NO_ERROR) {
      throw ArDemoRuntimeException("$label: glError $lastError")
    }
  }
}