package com.beichende.device.widget;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename GpuRenderer.java
 * @time 2018/8/28 9:31
 * @copyright(C) 2018 song
 */
public class GpuRenderer implements GLSurfaceView.Renderer {
    //GPU 渲染器
    public String gl_renderer;

    //GPU 供应商
    public String gl_vendor;

    //GPU 版本
    public String gl_version;

    //GPU  扩展名
    public String gl_extensions;
    private GpuRendererCallback gpuRendererCallback;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {
        gl.glClearColor(0,0,0,0);//GLSurfaceView未初始化，您需要初始化它,否则空指针异常
        gl_renderer = gl.glGetString(GL10.GL_RENDERER);
        gl_vendor = gl.glGetString(GL10.GL_VENDOR);
        gl_version = gl.glGetString(GL10.GL_VERSION);
        gl_extensions = gl.glGetString(GL10.GL_EXTENSIONS);
        Log.d("GpuRenderer", "onSurfaceCreated = " + gl_renderer);
        if (gpuRendererCallback != null) {
            gpuRendererCallback.info(gl_renderer, gl_vendor, gl_version, gl_extensions);
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

    public interface GpuRendererCallback {
        void info(String renderer, String vendor, String version, String extensions);
    }

    public void setGpuRendererCallback(GpuRendererCallback gpuRendererCallback) {
        this.gpuRendererCallback = gpuRendererCallback;
    }
}
