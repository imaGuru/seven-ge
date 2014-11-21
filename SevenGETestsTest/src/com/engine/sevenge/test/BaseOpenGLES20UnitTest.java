
package com.engine.sevenge.test;

import java.util.concurrent.CountDownLatch;

import android.test.ActivityInstrumentationTestCase2;

import com.engine.sevenge.OpenGLES20UnitTestActivity;
import com.engine.sevenge.SevenGE;
import com.engine.sevenge.io.IO;

public class BaseOpenGLES20UnitTest extends ActivityInstrumentationTestCase2<OpenGLES20UnitTestActivity> {
	private OpenGLES20UnitTestActivity activity;

	public BaseOpenGLES20UnitTest () {
		super(OpenGLES20UnitTestActivity.class);
	}

	@Override
	public void setUp () {
		activity = getActivity();
		SevenGE.io = new IO(activity);
	}

	@Override
	public void tearDown () {
		activity.finish();
		SevenGE.io = null;
	}

	public void runOnGLThread (final TestWrapper test) throws Throwable {
		final CountDownLatch latch = new CountDownLatch(1);

		activity.getSurfaceView().queueEvent(new Runnable() {
			public void run () {
				test.executeWrapper();
				latch.countDown();
			}
		});

		latch.await();
		test.rethrowExceptions();
	}

	public static abstract class TestWrapper {
		private Error error = null;
		private Throwable throwable = null;

		public TestWrapper () {
		}

		public void executeWrapper () {
			try {
				executeTest();
			} catch (Error e) {
				synchronized (this) {
					error = e;
				}
			} catch (Throwable t) {
				synchronized (this) {
					throwable = t;
				}
			}
		}

		public void rethrowExceptions () {
			synchronized (this) {
				if (error != null) {
					throw error;
				}

				if (throwable != null) {
					throw new RuntimeException("Unexpected exception", throwable);
				}
			}
		}

		public abstract void executeTest () throws Throwable;
	}

}
