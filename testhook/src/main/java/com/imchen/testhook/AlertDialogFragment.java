package com.imchen.testhook;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by imchen on 2017/9/28.
 */

public class AlertDialogFragment extends AppCompatDialogFragment {

    private EditText mConsoleViewEt;
    private Button mConfirmBtn;
    private Button mCloseBtn;


    public interface DialogFragmentDataImp{//定义一个与Activity通信的接口，使用该DialogFragment的Activity须实现该接口
        void showMessage(String message);
    }

    public static AlertDialogFragment newInstance(String message){
        //创建一个带有参数的Fragment实例
        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("message", message);
        fragment.setArguments(bundle);//把参数传递给该DialogFragment
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fragment_console,null);
        mConsoleViewEt= (EditText) view.findViewById(R.id.et_console_view);
        mCloseBtn= (Button) view.findViewById(R.id.btn_cancel);
        mConfirmBtn= (Button) view.findViewById(R.id.btn_confirm);


        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragmentDataImp impl= (DialogFragmentDataImp) getActivity();
                impl.showMessage(getArguments().getString("message"));
                dismiss();
            }
        });
        return super.onCreateDialog(savedInstanceState);
    }

}
