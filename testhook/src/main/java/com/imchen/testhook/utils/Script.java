package com.imchen.testhook.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;

import com.imchen.testhook.iappscript;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Script {

	private static LinkedList<Object> objsearch = new LinkedList<Object>();
	static iappscript iap=null;
	static boolean bolNotifyStart = false;
	
	public static void startScript(iappscript _iap) {
		iap=_iap;
		
		try {

			LogUtil.log("startScript....");
			Handler hd = new Handler(Looper.getMainLooper());
			hd.postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						int sltime = enumView();
						if (sltime == 0) {
							sltime = 1;
						}
						Handler hd = new Handler(Looper.getMainLooper());					
						hd.postDelayed(this, sltime * 1000);
					} catch (Exception e) {
						LogUtil.log(e);
					}
				}
			}, 1000);

		} catch (Exception ex) {
			LogUtil.log(ex);
		}
	}

	public static int enumView() throws Exception {

		Class<?> clsWindowManagerImpl;
		Method mgetDefault;
		try {
			clsWindowManagerImpl = Context.class.getClassLoader().loadClass(
					"android.view.WindowManagerGlobal");
			mgetDefault = clsWindowManagerImpl.getMethod("getInstance",
					new Class[] {});
		} catch (Exception e) {
			clsWindowManagerImpl = Context.class.getClassLoader().loadClass(
					"android.view.WindowManagerImpl");
			mgetDefault = clsWindowManagerImpl.getMethod("getDefault",
					new Class[] {});
		}

		Object objWindowManagerImpl = mgetDefault.invoke(clsWindowManagerImpl,
				new Object[] {});

		Field fView = clsWindowManagerImpl.getDeclaredField("mViews");
		fView.setAccessible(true);

		ArrayList<View> mViews = null;

		try {
			View[] mViews1 = (View[]) fView.get(objWindowManagerImpl);
			if (mViews1 != null) {
				mViews = new ArrayList<View>();
				for (int i = 0; i < mViews1.length; i++) {
					mViews.add(mViews1[i]);
				}
			}
		} catch (Exception ex) {

		}

		if (mViews == null) {
			try {
				mViews = (ArrayList<View>) fView.get(objWindowManagerImpl);
			} catch (Exception ex) {

			}
		}

		if (mViews != null && mViews.size() > 0) {			 

			for (int i = mViews.size() - 1; i >= 0; i--) {

				View v = mViews.get(i);

				if (v.isShown() == false) {
					continue;
				}

				try {

					Class<?> clsTest = Context.class
							.getClassLoader()
							.loadClass(
									"com.android.internal.policy.impl.PhoneWindow$DecorView");

					if (clsTest.isInstance(v)) {
						Field fd = clsTest.getDeclaredField("this$0");
						fd.setAccessible(true);
						Object obj = fd.get(v);

						clsTest = Context.class.getClassLoader().loadClass(
								"com.android.internal.policy.impl.PhoneWindow");
						Method md = clsTest.getMethod("getCallback",
								new Class[] {});
						md.setAccessible(true);

						Object cbobj = md.invoke(obj, new Object[] {});

						if ((Object) cbobj instanceof Dialog) {
							return onDialog((Dialog) cbobj);
						} else if ((Object) cbobj instanceof Activity) {
							return onActivity((Activity) cbobj);
						} else {
							cbobj = testView(cbobj);
							if ((Object) cbobj instanceof Dialog) {
								return onDialog((Dialog) cbobj);
							} else if ((Object) cbobj instanceof Activity) {
								return onActivity((Activity) cbobj);
							}
						}
					}

				} catch (Exception e) {
					LogUtil.log(e);
				}
			}

		}
		// }

		return 1;

	}

	private static int onActivity(Activity cbobj) throws Exception {
		return iap.onActivity(cbobj);
	}

	private static int onDialog(Dialog cbobj) throws Exception{
		return iap.onDialog(cbobj);
	}

	public static void printallActivityView(Activity act, boolean _bolShowOnly) {

		View vRoot = act.getWindow().getDecorView();
		printAllShownView(vRoot, 0, _bolShowOnly);
	}

	public static void printAllShownTextView(View v, int level) {

		if (v != null) {
			if (v.isShown()) {
				String sHeader = "";
				for (int i = 0; i < level; i++) {
					sHeader = sHeader + "--";
				}
				String slog = sHeader + " " + v.toString() + " "
						+ Integer.toHexString(v.getId()) + " " + v.getLeft()
						+ " " + v.getTop() + " " + getLeftFromScreen(v) + " "
						+ getTopFromScreen(v);
				if (v instanceof TextView) {
					slog = slog + " text:"
							+ ((TextView) v).getText().toString();
				}
				LogUtil.log(slog);
				if (v instanceof ViewGroup) {
					ViewGroup vg = (ViewGroup) v;
					for (int j = 0; j < vg.getChildCount(); j++) {
						printAllShownTextView(vg.getChildAt(j), level + 1);
					}
				}
			}
		}
	}

	public static TextView findTextViewbytext(Activity act, String text) {
		return findTextViewbytext(act.getWindow().getDecorView(), text);
	}

	public static TextView findTextViewbytext(View view, String text) {

		TextView r = null;

		if (view instanceof TextView) {
			TextView tv = (TextView) view;
			if (tv.getText().toString().trim().equals(text)) {
				return tv;
			}
		}

		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			for (int i = 0; i < vg.getChildCount(); i++) {
				View vc = vg.getChildAt(i);
				r = findTextViewbytext(vc, text);
				if (r != null)
					return r;
			}
		}

		return r;
	}

	public static View findViewbyid(Activity act, int id) {
		return findViewbyid(act.getWindow().getDecorView(), id);
	}

	public static View findViewbyid(View view, int id) {

		View r = null;

		if (view.getId() == id) {
			return view;
		}

		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			for (int i = 0; i < vg.getChildCount(); i++) {
				View vc = vg.getChildAt(i);
				r = findViewbyid(vc, id);
				if (r != null)
					return r;
			}
		}

		return r;
	}

	public static void findViewsbyid(Activity act, int id, LinkedList<View> ls) {
		findViewsbyid(act.getWindow().getDecorView(), id, ls);
	}

	public static void findViewsbyid(View view, int id, LinkedList<View> ls) {

		View r = null;

		if (view.getId() == id) {
			ls.add(view);
		}

		if (view instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) view;
			for (int i = 0; i < vg.getChildCount(); i++) {
				View vc = vg.getChildAt(i);
				findViewsbyid(vc, id, ls);
			}
		}

	}

	public static void printallDialogViewWithText(Dialog dlg) {
		if (dlg == null) {

			return;
		}
		View vRoot = dlg.getWindow().getDecorView();
		printAllShownTextView(vRoot, 0);
	}

	public static void KeyEvent(Activity act, int key) {
		if (checkIsActiveAct(act) == false)
			return;
		android.view.KeyEvent kv = new android.view.KeyEvent(
				android.view.KeyEvent.ACTION_DOWN, key);
		act.dispatchKeyEvent(kv);
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			// TODO: handle exception
		}
		kv = new android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, key);
		act.dispatchKeyEvent(kv);
	}

	public static boolean checkIsActiveDialog(Dialog dlg) {

		if (dlg != null && dlg.isShowing()) {// && dlg.getWindow().isActive()) {
			return true;
		}
		return false;
	}

	public static void KeyEvent(Dialog dlg, int key) {
		if (checkIsActiveDialog(dlg) == false)
			return;
		android.view.KeyEvent kv = new android.view.KeyEvent(
				android.view.KeyEvent.ACTION_DOWN, key);
		dlg.dispatchKeyEvent(kv);
		try {
			Thread.sleep(10);
		} catch (Exception e) {
			// TODO: handle exception
		}
		kv = new android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, key);
		dlg.dispatchKeyEvent(kv);
	}

	public static void MouseClick(Activity act, int x, int y, long sltime) {
		// 濡剝瀚欑憴锔芥嚋

		if (checkIsActiveAct(act) == false)
			return;

		setFocusToActvityWindow(act);

		// log.mylog("MouseClick x=" + x + " y=" + y + " update time="
		// + SystemClock.uptimeMillis());
		MotionEvent ev;
		ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
		boolean r = act.dispatchTouchEvent(ev);
		// LogUtil.log("action_down r=" + r);

		try {
			Thread.sleep(sltime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
		boolean r2 = act.dispatchTouchEvent(ev);
		// LogUtil.log("ACTION_UP r=" + r2);

	}

	public static class clsUiMove extends Thread {

		int evtype = 0;

		Activity act;

		int x1, y1;
		int x2, y2;

		int stx, sty;
		int kx, ky;
		boolean bolwait = false;

		public clsUiMove(Activity _act, int _x1, int _y1, int _x2, int _y2) {
			act = _act;
			x1 = _x1;
			x2 = _x2;
			y1 = _y1;
			y2 = _y2;
			stx = Math.abs(x2 - x1) / 10;
			sty = Math.abs(y2 - y1) / 10;
			if (stx == 0)
				stx = 1;
			if (sty == 0)
				sty = 1;
			kx = x2 > x1 ? 1 : -1;
			ky = y2 > y1 ? 1 : -1;
			start();
		}

		private void waitforEventEnd() {
			while (bolwait) {
				trysleep(1);
			}
		}

		@Override
		public void run() {

			bolwait = true;
			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					MotionEvent ev;
					ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
							SystemClock.uptimeMillis(),
							MotionEvent.ACTION_DOWN, x1, y1, 0);
					boolean r = act.dispatchTouchEvent(ev);
					bolwait = false;
				}
			});
			waitforEventEnd();

			while (x1 != x2 || y1 != y2) {
				if (x1 != x2) {
					x1 = x1 + kx * stx;
				}
				if (y1 != y2) {
					y1 = y1 + ky * sty;
				}
				bolwait = true;
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						MotionEvent ev;
						ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
								SystemClock.uptimeMillis(),
								MotionEvent.ACTION_MOVE, x1, y1, 0);
						LogUtil.log("move " + x1 + " " + y1);
						boolean r = act.dispatchTouchEvent(ev);
						bolwait = false;
					}
				});
				waitforEventEnd();
			}

			new Handler(Looper.getMainLooper()).post(new Runnable() {
				@Override
				public void run() {
					MotionEvent ev;
					ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
							SystemClock.uptimeMillis(), MotionEvent.ACTION_UP,
							x2, y2, 0);
					boolean r = act.dispatchTouchEvent(ev);
					bolwait = false;
				}
			});
			waitforEventEnd();

		}
	}

	public static void MouseScrollUp(Activity act, int x, int y1, int y2,
			long sltime) {
		// 濡剝瀚欑憴锔芥嚋

		if (checkIsActiveAct(act) == false)
			return;

		setFocusToActvityWindow(act);

		MotionEvent ev;
		ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y1, 0);
		boolean r = act.dispatchTouchEvent(ev);

		trysleep(100);

		ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y2, 0);
		boolean r2 = act.dispatchTouchEvent(ev);

	}

	public static void MouseRandClickView(Activity act, View v) {

		int rx, ry;
		rx = getLeftFromScreen(v);
		ry = getTopFromScreen(v);

		rx = getRandInt(rx + v.getWidth() - 1, rx + 1);
		ry = getRandInt(ry + v.getHeight() - 1, ry + 1);

		MouseClick(act, rx, ry, getRandInt(200, 180));

	}

	public static int getLeftFromScreen(View v) {

		ViewParent vp = v.getParent();
		if (!(vp instanceof View)) {
			vp = null;
		}
		if (vp != null) {
			return getLeftFromScreen((View) vp) + v.getLeft() - v.getScrollX();
		}
		return v.getLeft() - v.getScrollX();
	}

	public static int getTopFromScreen(View v) {
		ViewParent vp = v.getParent();
		if (!(vp instanceof View)) {
			vp = null;
		}
		if (vp != null) {
			return getTopFromScreen((View) vp) + v.getTop() - v.getScrollY();
		}
		return v.getTop() - v.getScrollY();
	}

	public static void printAllShownView(View v, int level,
			boolean _bolPrintShowOnly) {

		if (v != null) {
			if (_bolPrintShowOnly == false
					|| (_bolPrintShowOnly == true && v.isShown())) {
				String sHeader = "";
				for (int i = 0; i < level; i++) {
					sHeader = sHeader + "--";
				}
				LogUtil.log(sHeader
						+ " "
						+ v.toString()
						+ " "
						+ Integer.toHexString(v.getId())
						+ " "
						+ v.getLeft()
						+ " "
						+ v.getTop()
						+ " "
						+ getLeftFromScreen(v)
						+ " "
						+ getTopFromScreen(v)
						+ "  "
						+ (v instanceof TextView ? ((TextView) v).getText()
								: ""));
				if (v instanceof ViewGroup) {
					ViewGroup vg = (ViewGroup) v;
					for (int j = 0; j < vg.getChildCount(); j++) {
						printAllShownView(vg.getChildAt(j), level + 1,
								_bolPrintShowOnly);
					}
				}
			}
		}

	}

	public static int getRandInt(int max, int min) {
		Random random = new Random();
		if (min > max) {
			int tmp = min;
			min = max;
			max = tmp;
		}
		int s = random.nextInt(max - min + 1) + min;// random.nextInt(max) %
													// (max - min + 1) + min;

		return s;
	}

	private static boolean checkActivityPausedOrStopped(Activity act) {

		Object objCurAt = null;

		try {

			Class<?> clsAt = String.class.getClassLoader().loadClass(
					"android.app.ActivityThread");
			Method mdcurrentActivityThread = clsAt.getDeclaredMethod(
					"currentActivityThread", new Class[] {});
			mdcurrentActivityThread.setAccessible(true);
			objCurAt = mdcurrentActivityThread.invoke(clsAt, new Object[] {});

			if (objCurAt == null) {
				Field fd = Thread.class.getDeclaredField("localValues");
				fd.setAccessible(true);
				Object ValuesMain = fd.get(Looper.getMainLooper().getThread());
				Field fdObject = ValuesMain.getClass()
						.getDeclaredField("table");
				fdObject.setAccessible(true);
				Object[] objTable = (Object[]) fdObject.get(ValuesMain);
				for (int i = 0; i < objTable.length; i++) {
					if (objTable[i] != null
							&& objTable[i].getClass().getName()
									.equals("android.app.ActivityThread")) {
						objCurAt = objTable[i];
						break;
					}
				}
			}

			if (objCurAt != null) {

				Field fd = clsAt.getDeclaredField("mActivities");
				fd.setAccessible(true);
				Object hs = fd.get(objCurAt);

				Method mdget = hs.getClass().getDeclaredMethod("get",
						new Class[] { Object.class });
				mdget.setAccessible(true);

				Field fdmToken = Activity.class.getDeclaredField("mToken");
				fdmToken.setAccessible(true);
				IBinder mToken = (IBinder) fdmToken.get(act);

				Object objactrec = mdget.invoke(hs, new Object[] { mToken });

				if (objactrec != null) {
					Field fdpaused = objactrec.getClass().getDeclaredField(
							"paused");
					fdpaused.setAccessible(true);

					Field fdstopped = objactrec.getClass().getDeclaredField(
							"stopped");
					fdstopped.setAccessible(true);

					boolean paused = fdpaused.getBoolean(objactrec);
					boolean stopped = fdpaused.getBoolean(objactrec);

					if (paused) {
						return true;
					}
					if (stopped) {
						return true;
					}
				} else {
					return true;
				}
			}

		} catch (Exception ex) {

		}

		return false;

	}

	public static boolean checkIsActiveAct(Activity act) {
		// System.out.println("act="+act.toString()+" isFinishing="+act.isFinishing()+" isRestricted="+act.isRestricted()+" isActive="+act.getWindow().isActive());
		if (act != null && act.isFinishing() == false
				&& act.isRestricted() == false && act.getWindow().isActive()
				&& checkActivityPausedOrStopped(act) == false) {
			return true;
		}
		return false;
	}

	public static void setFocusToActvityWindow(Activity act) {

		IBinder ib = act.getWindow().getDecorView().getWindowToken();

		try {

			Class<?> clsIWindow = null;

			clsIWindow = Class.forName("android.view.IWindow");

			final Class[] clsList = clsIWindow.getClasses();
			// log.mylog(clsList[0].toString());

			Method MasInterface = clsList[0].getMethod("asInterface",
					new Class[] { IBinder.class });

			Object ObjIbProxy = (Object) MasInterface.invoke(clsList[0],
					new Object[] { ib });

			// log.mylog(ObjIbProxy.getClass().toString());

			Method MwindowFocusChanged = ObjIbProxy.getClass().getMethod(
					"windowFocusChanged",
					new Class[] { boolean.class, boolean.class });

			MwindowFocusChanged.invoke(ObjIbProxy, new Object[] { true, true });

			// windowFocusChanged(ib, true, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void MouseClick(View v, int x, int y, long sltime) {
		// 濡剝瀚欑憴锔芥嚋

		if (v == null) {
			return;
		}

		// log.mylog("click on v:" + v.toString());
		// log.mylog("MouseClick x=" + x + " y=" + y + " update time="
		// + SystemClock.uptimeMillis());

		MotionEvent ev;
		ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, x, y, 0);
		boolean r = v.dispatchTouchEvent(ev);
		// log.mylog("action_down r=" + r);

		try {
			Thread.sleep(sltime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ev = MotionEvent.obtain(SystemClock.uptimeMillis(),
				SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, x, y, 0);
		boolean r2 = v.dispatchTouchEvent(ev);
		// log.mylog("action_up r=" + r2);

	}

	private static void trysleep(long t) {
		try {
			Thread.sleep(t);
		} catch (Exception ex) {

		}
	}

	private static Object testView(Object obj) {
		objsearch.clear();
		return testView2(obj);
	}

	private static Object testView2(Object obj) {

		LogUtil.log("obj=" + obj.toString());
		objsearch.add(obj);

		try {
			Field[] fds = obj.getClass().getDeclaredFields();

			if (fds != null) {
				for (int i = 0; i < fds.length; i++) {
					fds[i].setAccessible(true);
					Object objfd = fds[i].get(obj);

					if (objsearch.indexOf(objfd) == -1) {

						if (objfd instanceof Activity) {
							return objfd;
						}
						if (objfd instanceof Dialog) {
							return objfd;
						}
						Object r = testView2(objfd);
						if (r != null) {
							return r;
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	

	public static void notifyPmScriptStartActive() {
		if (bolNotifyStart) {
			return;
		}
		try {
			LogUtil.log("notifyPmScriptStartActive start");
			Class clsServiceManager = Context.class.getClassLoader().loadClass(
					"android.os.ServiceManager");
			Method localMethod = clsServiceManager.getDeclaredMethod(
					"getService", new Class[] { String.class });
			localMethod.setAccessible(true);
			IBinder bPmscript = (IBinder) localMethod.invoke(clsServiceManager,
					new Object[] { "pmscript" });
			Parcel localParcel1 = Parcel.obtain();
			Parcel localParcel2 = Parcel.obtain();
			bPmscript.transact(IBinder.FIRST_CALL_TRANSACTION + 3,
					localParcel1, localParcel2, 0);
			localParcel2.readException();
			localParcel1.recycle();
			localParcel2.recycle();
			LogUtil.log("notifyPmScriptStartActive end");
			bolNotifyStart = true;
		} catch (Exception ex) {
			// LogUtil.log(ex);
		}
	}

	public static void notifyPmScript(long _sleeptime, int code, String msg) {
		try {
			LogUtil.log("notifyPmScript start");
			Class clsServiceManager = Context.class.getClassLoader().loadClass(
					"android.os.ServiceManager");
			Method localMethod = clsServiceManager.getDeclaredMethod(
					"getService", new Class[] { String.class });
			localMethod.setAccessible(true);
			IBinder bPmscript = (IBinder) localMethod.invoke(clsServiceManager,
					new Object[] { "pmscript" });
			Parcel localParcel1 = Parcel.obtain();
			Parcel localParcel2 = Parcel.obtain();
			localParcel1.writeLong(_sleeptime);
			localParcel1.writeInt(code);
			localParcel1.writeString(msg);
			bPmscript.transact(IBinder.FIRST_CALL_TRANSACTION + 2,
					localParcel1, localParcel2, 0);
			localParcel2.readException();
			localParcel1.recycle();
			localParcel2.recycle();
			LogUtil.log("notifyPmScript end");
		} catch (Exception ex) {
			LogUtil.log(ex);
		}
	}

	public static String getBtCaption(Button bt) {
		if (bt == null)
			return "";
		String caption = bt.getText().toString();
		caption = caption.trim();
		String caption2 = "";
		for (int i = 0; i < caption.length(); i++) {
			char c = caption.charAt(i);
			if (c != ' ' && c != '\t') {
				caption2 = caption2 + c;
			}
		}

		return caption2.toLowerCase();

	}

	public static boolean clickbtbycaption(AlertDialog aldlg, String text) {

		// 点击取消
		Button bt = null;
		Button bt1 = aldlg.getButton(DialogInterface.BUTTON_POSITIVE);
		Button bt2 = aldlg.getButton(DialogInterface.BUTTON_NEUTRAL);
		Button bt3 = aldlg.getButton(DialogInterface.BUTTON_NEGATIVE);
		if (getBtCaption(bt1).equals(text)) {
			bt = bt1;
		} else if (getBtCaption(bt2).equals(text)) {
			bt = bt2;
		} else if (getBtCaption(bt3).equals(text)) {
			bt = bt3;
		}
		if (bt != null) {
			Script.MouseClick(bt, bt.getWidth() / 2, bt.getHeight() / 2, 200);
			return true;
		}
		return false;
	}

	


}
