package com.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by suyashbhagwat on 17/2/17.
 */

public class CustomTextView extends TextView {

    private Context mContext;
    private static final int DEFAULT_FONT_FACE = 0;
    private static final int FONT_FACE_MEDIUM = 1;
    private static final int FONT_FACE_LIGHT = 2;
    private static final int FONT_FACE_ITALIC = 3;
    private static final int FONT_FACE_BOLD = 4;
    private static final int FONT_FACE_THIN = 5;
    private ArrayList<SpannablePatternItem> patterns;
    private boolean mIsLinkClickable,mMobileNumberClickable;
    private int mSpannableColor;

    public CustomTextView(Context context) {
        super(context);
        mContext =context;
        init(null);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext =context;
        init(attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext =context;
        init(attrs);
    }

    private void init(AttributeSet attrs) {

        int fontFace = DEFAULT_FONT_FACE;
        boolean isLinkClickable = false;
        boolean isMobileNumberClickable = false;

        int spannableColor = android.R.color.holo_blue_light;

        TypedArray ta = attrs == null ? null : getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);

        if(ta!=null){

            fontFace = ta.getInteger(R.styleable.CustomTextView_fontFace, fontFace);
            isLinkClickable = ta.getBoolean(R.styleable.CustomTextView_linkClickable,isLinkClickable);
            spannableColor = ta.getColor(R.styleable.CustomTextView_spannableColor,spannableColor);
            isMobileNumberClickable = ta.getBoolean(R.styleable.CustomTextView_mobileClickable,isMobileNumberClickable);

            mIsLinkClickable= isLinkClickable;
            mSpannableColor =spannableColor;
            mMobileNumberClickable = isMobileNumberClickable;

            setTextStyle(fontFace);
            setSpannableString();
            ta.recycle();
        }
    }

    private void setSpannableString() {
        patterns =new ArrayList<>();
        if(mIsLinkClickable){

            patterns.add(new SpannablePatternItem(Patterns.WEB_URL,mSpannableColor, new SpannableClickedListener() {
                @Override
                public void onSpanClicked(String text) {

                }
            }));

            SpannableStringBuilder result = build(this.getText());
            this.setText(result);
            this.setMovementMethod(LinkMovementMethod.getInstance());
        }

        if(mMobileNumberClickable) {

            String mobileNumberRegex= "^(\\+\\d{1,3}[- ]?)?\\d{10}$";
            Pattern patternMobile = Pattern.compile(mobileNumberRegex, Pattern.CASE_INSENSITIVE);
            patterns.add(new SpannablePatternItem(patternMobile,mSpannableColor, new SpannableClickedListener() {
                @Override
                public void onSpanClicked(String text) {

                }
            }));

            SpannableStringBuilder result = build(this.getText());
            this.setText(result);
            this.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    // This builds the pattern span into a `SpannableStringBuilder`
    // Requires a CharSequence to be passed in to be applied to
    public SpannableStringBuilder build(CharSequence editable) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(editable);
        for (SpannablePatternItem item : patterns) {
            Matcher matcher = item.pattern.matcher(ssb);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                StyledClickableSpan url = new StyledClickableSpan(item);
                ssb.setSpan(url, start, end, 0);
            }
        }
        return ssb;
    }
    public void setTextStyle(int textFontFace) {
        Typeface typeface = null;
        switch (textFontFace){

            case DEFAULT_FONT_FACE :

                typeface = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Roboto-Regular.ttf");
                break;

            case FONT_FACE_MEDIUM:

                typeface = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Roboto-Medium.ttf");
                break;

            case FONT_FACE_BOLD:

                typeface = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Roboto-Bold.ttf");
                break;

            case FONT_FACE_ITALIC:

                typeface = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Roboto-LightItalic.ttf");
                break;

            case FONT_FACE_LIGHT:

                typeface = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Roboto-Light.ttf");
                break;

            case FONT_FACE_THIN:

                typeface = Typeface.createFromAsset(mContext.getAssets(),
                        "fonts/Roboto-Thin.ttf");
                break;
        }
        this.setTypeface(typeface, Typeface.NORMAL);
    }
}

class SpannablePatternItem {

    public Pattern pattern;
    public int spannableColor;
    public SpannableClickedListener mSpannableClickedListener;

    public SpannablePatternItem(Pattern pattern, int mSpannableColor,SpannableClickedListener spannableClickedListener) {
        this.pattern = pattern;
        this.spannableColor = mSpannableColor;
        this.mSpannableClickedListener = spannableClickedListener;
    }
}

class StyledClickableSpan extends ClickableSpan {
    SpannablePatternItem item;

    public StyledClickableSpan(SpannablePatternItem item) {
        this.item = item;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.linkColor = this.item.spannableColor;
        super.updateDrawState(ds);
    }

    @Override
    public void onClick(View widget) {
        if (item.mSpannableClickedListener != null) {
            TextView tv = (TextView) widget;
            Spanned span = (Spanned) tv.getText();
            int start = span.getSpanStart(this);
            int end = span.getSpanEnd(this);
            CharSequence text = span.subSequence(start, end);
            item.mSpannableClickedListener.onSpanClicked(text.toString());
        }
        widget.invalidate();
    }
}

interface SpannableClickedListener {
    void onSpanClicked(String text);
}
