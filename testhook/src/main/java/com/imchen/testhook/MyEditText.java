package com.imchen.testhook;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/*
 * Custom widget of EditText with two icon.
 * Created by lizhongquan on 16-1-6.
 */
public class MyEditText extends EditText {

    private Drawable hintIcon = null;
    private Drawable toolIcon = null;

    /**
     * HintIcon clickListener
     */
    private OnClickListenerWithEditTextHintIcon onClickListenerWithEditTextHintIcon = null;

    /**
     * Default clear the EditText
     */
    private OnClickListenerWithEditTextToolIcon onClickListenerWithEditTextToolIcon = null;

    /**
     * Default fixed the Height
     */
    private boolean fixed = true;

    public MyEditText(Context context) {
        super(context);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MyEditText,
                0, 0);

        hintIcon = typedArray.getDrawable(R.styleable.MyEditText_hintIcon);
        toolIcon = typedArray.getDrawable(R.styleable.MyEditText_toolIcon);
        fixed = typedArray.getBoolean(R.styleable.MyEditText_fixed, true);

        if (toolIcon != null && fixed) {
            setHeight(toolIcon.getIntrinsicHeight());
        }

        setCompoundDrawablesWithIntrinsicBounds(hintIcon, null, null, null);

        setCompoundDrawablePadding(10);

        typedArray.recycle();

        onClickListenerWithEditTextToolIcon = new OnClickListenerWithEditTextToolIcon() {
            @Override
            public void onClick() {
                setText("");
            }
        };
    }

    /**
     * Override the touchEvent to judge whether click toolIcon or hintIcon
     *
     * @param event motionEvent
     * @return super
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (hintIcon != null) {
                if (event.getX() < hintIcon.getIntrinsicWidth()
                        && event.getX() > 0) {
                    if (getCompoundDrawables()[0] != null
                            && onClickListenerWithEditTextHintIcon != null) {
                        onClickListenerWithEditTextHintIcon.onClick();
                    }
                }
            }

            if (toolIcon != null) {
                if (event.getX() > (getWidth()
                        - toolIcon.getIntrinsicWidth())
                        && event.getX() < getWidth()) {
                    if (getCompoundDrawables()[2] != null) {
                        onClickListenerWithEditTextToolIcon.onClick();
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * the clickListener of click hintIcon
     *
     * @param clickListenerOfHintIcon OnClickListenerWithEditTextHintIcon
     */
    public void setOnClickListenerWithEditTextHintIcon(
            OnClickListenerWithEditTextHintIcon clickListenerOfHintIcon) {
        this.onClickListenerWithEditTextHintIcon = clickListenerOfHintIcon;
    }

    /**
     * the clickListener of click toolIcon
     *
     * @param clickListenerOfToolIcon OnClickListenerWithEditTextToolIcon
     */
    public void setOnClickListenerWithEditTextToolIcon(
            OnClickListenerWithEditTextToolIcon clickListenerOfToolIcon) {
        this.onClickListenerWithEditTextToolIcon = clickListenerOfToolIcon;
    }

    /**
     * onTextChange
     *
     * @param text         text
     * @param start        start
     * @param lengthBefore lengthBefore
     * @param lengthAfter  lengthAfter
     */
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        if (text.length() > 0 && getCompoundDrawables()[2] == null && toolIcon != null) {
//            hintIcon.setBounds(10, 0, 10, 0);
            setCompoundDrawablesWithIntrinsicBounds(hintIcon, null, toolIcon, null);
        }

        if (text.length() == 0 && getCompoundDrawables()[2] != null && toolIcon != null) {
            setCompoundDrawablesWithIntrinsicBounds(hintIcon, null, null, null);
        }
    }

    /**
     * interface when click hintIcon
     */
    public interface OnClickListenerWithEditTextHintIcon {
        void onClick();
    }

    /**
     * interface when click toolIcon
     */
    public interface OnClickListenerWithEditTextToolIcon {
        void onClick();
    }

}
