package com.imchen.testhook;

import android.app.Activity;
import android.app.Dialog;

public interface iappscript {
	public int onActivity(Activity act) throws Exception;
	public int onDialog(Dialog dlg)throws Exception;
}
