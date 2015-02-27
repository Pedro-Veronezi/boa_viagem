package br.com.casadocodigo.boaviagem.widget;/*
 * Copyright (C) 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import br.com.casadocodigo.boaviagem.R;

/**
 * Layout which an {@link android.widget.EditText} to show a floating label when the hint is hidden
 * due to the user inputting text.
 *
 * @see <a href="https://dribbble.com/shots/1254439--GIF-Mobile-Form-Interaction">Matt D. Smith on Dribble</a>
 * @see <a href="http://bradfrostweb.com/blog/post/float-label-pattern/">Brad Frost's blog post</a>
 */
public class FloatLabelLayout extends FrameLayout {
 
    private static final long ANIMATION_DURATION = 150;
    private static final float DEFAULT_PADDING_LEFT_RIGHT_DP = 5f;
 
    private static final String SAVED_SUPER_STATE = "SAVED_SUPER_STATE";
    private static final String SAVED_LABEL_VISIBILITY = "SAVED_LABEL_VISIBILITY";
    private static final String SAVED_HINT = "SAVED_HINT";
    private static final String SAVED_ERROR_LABEL_VISIBILITY = "SAVED_ERROR_LABEL_VISIBILITY";

    private EditText editText;
    private TextView label;
    private Trigger trigger;
    private CharSequence hint;

    /**
     * Error Label for editText
     */
    private TextView errorLabel;


    public FloatLabelLayout(Context context) {
        this(context, null);
    }
 
    public FloatLabelLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public FloatLabelLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
 
        final TypedArray a = context
                .obtainStyledAttributes(attrs, R.styleable.FloatLabelLayout);
 
        final int sidePadding = a.getDimensionPixelSize(
                R.styleable.FloatLabelLayout_floatLabelSidePadding,
                dipsToPix(DEFAULT_PADDING_LEFT_RIGHT_DP));

        // Configure the FloatLabel
        label = new TextView(context);
        label.setPadding(sidePadding, 0, sidePadding, 0);
        label.setVisibility(INVISIBLE);

        label.setTextAppearance(context,
                a.getResourceId(R.styleable.FloatLabelLayout_floatLabelTextAppearance,
                        R.style.TextAppearance_pveronezi_FloatLabel)
        );

        // Configure the label for errors
        errorLabel = new TextView(context);
        errorLabel.setPadding(sidePadding, 0, sidePadding, 0);
        errorLabel.setVisibility(INVISIBLE);
        //errorLabel.setText(a.getText(R.styleable.FloatLabelLayout_floatLabelErrorText));
        //errorLabel.setText("Teste de Erro");

        errorLabel.setTextAppearance(context,
                a.getResourceId(R.styleable.FloatLabelLayout_floatLabelErrorTextAppearance,
                        R.style.TextAppearance_pveronezi_FloatErrorLabel)
        );
 
        int triggerInt = a.getInt(R.styleable.FloatLabelLayout_floatLabelTrigger, Trigger.TYPE.getValue());
        trigger = Trigger.fromValue(triggerInt);
 
        addView(label, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(errorLabel, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));

        a.recycle();
    }
 
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(SAVED_SUPER_STATE, super.onSaveInstanceState());
        bundle.putInt(SAVED_LABEL_VISIBILITY, label.getVisibility());
        bundle.putInt(SAVED_ERROR_LABEL_VISIBILITY, errorLabel.getVisibility());
        bundle.putCharSequence(SAVED_HINT, hint);
        return bundle;
    }
 
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            label.setVisibility(bundle.getInt(SAVED_LABEL_VISIBILITY));
            errorLabel.setVisibility(bundle.getInt(SAVED_ERROR_LABEL_VISIBILITY));
            hint = bundle.getCharSequence(SAVED_HINT);
 
            // retrieve super state
            state = bundle.getParcelable(SAVED_SUPER_STATE);
        }
        super.onRestoreInstanceState(state);
    }
 
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof EditText) {
            // If we already have an EditText, throw an exception
            if (editText != null) {
                throw new IllegalArgumentException("We already have an EditText, can only have one");
            }

            // Update the layout params so that the EditText is at the bottom, with enough top
            // margin to show the label
            final LayoutParams lp = new LayoutParams(params);
            lp.gravity = Gravity.BOTTOM;
            lp.topMargin = (int) label.getTextSize();
            lp.bottomMargin = (int) errorLabel.getTextSize();
            params = lp;
 
            setEditText((EditText) child);
        }
 
        // Carry on adding the View...
        super.addView(child, index, params);
    }
 
    protected void setEditText(EditText editText) {
        this.editText = editText;
 
        label.setText(this.editText.getHint());
 
        if (hint == null) {
            hint = this.editText.getHint();
        }
 
        // Add a TextWatcher so that we know when the text input has changed
        this.editText.addTextChangedListener(mTextWatcher);
 
        // Add focus listener to the EditText so that we can notify the label that it is activated.
        // Allows the use of a ColorStateList for the text color on the label
        this.editText.setOnFocusChangeListener(mOnFocusChangeListener);
 
        // if view already had focus we need to manually call the listener
        if (trigger == Trigger.FOCUS && this.editText.isFocused()) {
            mOnFocusChangeListener.onFocusChange(this.editText, true);
        }
 
    }
 
    /**
     * @return the {@link android.widget.EditText} text input
     */
    public EditText getEditText() {
        return editText;
    }
 
    /**
     * @return the {@link android.widget.TextView} label
     */
    public TextView getLabel() {
        return label;
    }
 
    /**
     * Show the label using an animation
     */
    protected void showLabel() {
        if (label.getVisibility() != View.VISIBLE) {
            label.setVisibility(View.VISIBLE);
            label.setAlpha(0f);
            label.setTranslationY(label.getHeight());
            label.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null).start();
        }
    }
 
    /**
     * Hide the label using an animation
     */
    protected void hideLabel() {
        // TODO Voltar a cor da linha  do EditText

        label.setAlpha(1f);
        label.setTranslationY(0f);
        label.animate()
                .alpha(0f)
                .translationY(label.getHeight())
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        label.setVisibility(View.GONE);
                    }
                }).start();
    }
    /**
     * Show the label using an animation
     */
    public void showError() {

        if (errorLabel.getVisibility() != View.VISIBLE) {
            errorLabel.setVisibility(View.VISIBLE);
            errorLabel.setAlpha(0f);
            errorLabel.setTranslationY(errorLabel.getHeight());
            errorLabel.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null).start();

            //editText.setHighlightColor(getResources().getColor(android.R.color.holo_blue_light));
            // TODO Muda a cor da linha  do  EditText.
            //editText.getBackground().setColorFilter(getResources().getColor(R.color.error), PorterDuff.Mode.SRC_OVER);
            editText.invalidate();
            //editText.setTextAppearance(getContext(), an);
       }
    }

    /**
     * Hide the label using an animation
     */
    public void hideError() {
        errorLabel.setAlpha(1f);
        errorLabel.setTranslationY(0f);
        errorLabel.animate()
                .alpha(0f)
                .translationY(errorLabel.getHeight())
                .setDuration(ANIMATION_DURATION)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        errorLabel.setVisibility(View.GONE);
                    }
                }).start();
    }
    /**
     * Helper method to convert dips to pixels.
     */
    private int dipsToPix(float dps) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps,
                getResources().getDisplayMetrics());
    }
 
    private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
 
        @Override
        public void onFocusChange(View view, boolean focused) {
            label.setActivated(focused);
 
            if (trigger == Trigger.FOCUS) {
                if (focused) {
                    editText.setHint("");
                    showLabel();
                } else {
                    if (TextUtils.isEmpty(editText.getText())) {
                        editText.setHint(hint);
                        hideLabel();
                    }
                }
            }
        }
    };
    
    public void setErrorLabel(String s){
        errorLabel.setText(s);
        invalidate();
        requestLayout();
    }
    
    public String getErrorLabel(){
        return  errorLabel.getText().toString();
    }
 
    private TextWatcher mTextWatcher = new TextWatcher() {
 
        @Override
        public void afterTextChanged(Editable s) {
            // only takes affect if trigger is set to TYPE
            if (trigger != Trigger.TYPE) {
                return;
            }
 
            if (TextUtils.isEmpty(s)) {
                hideLabel();
                editText.getBackground().clearColorFilter();
            } else {
                showLabel();
            }
        }
 
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
 
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
 
    };
 
    public static enum Trigger {
        TYPE(0),
        FOCUS(1);
 
        private final int mValue;
 
        private Trigger(int i) {
            mValue = i;
        }
 
        public int getValue() {
            return mValue;
        }
 
        public static Trigger fromValue(int value) {
            Trigger[] triggers = Trigger.values();
            for (int i = 0; i < triggers.length; i++) {
                if (triggers[i].getValue() == value) {
                    return triggers[i];
                }
            }
 
            throw new IllegalArgumentException(value + " is not a valid value for " + Trigger.class.getSimpleName());
        }
    }
}