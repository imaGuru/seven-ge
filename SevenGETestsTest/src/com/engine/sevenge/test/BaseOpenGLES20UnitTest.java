
package com.engine.sevenge.test;

import android.test.ActivityInstrumentationTestCase2;

import com.sevenge.IO;
import com.sevenge.SevenGE;
import com.sevenge.utils.UnitTestActivity;

public class BaseOpenGLES20UnitTest extends ActivityInstrumentationTestCase2<UnitTestActivity> {
	private UnitTestActivity activity;

	public BaseOpenGLES20UnitTest () {
		super(UnitTestActivity.class);
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
		Thread t = new Thread(new Runnable() {
			public void run () {
				test.executeWrapper();
			}
		});
		activity.getSurfaceView().queueEvent(t);
		t.join();
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
