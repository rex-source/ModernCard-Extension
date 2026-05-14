package com.myapp.moderncard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;

import java.util.HashMap;
import java.util.Map;

@DesignerComponent(
    version = 1,
    description = "Modern card UI with gradients, rounded corners, and shadows.",
    category = ComponentCategory.EXTENSION,
    nonVisible = true,
    iconName = "images/extension.png"
)
@SimpleObject(external = true)
public class ModernCard extends AndroidNonvisibleComponent {

    private final Activity activity;
    private final Context context;

    private String gradientStart = "#667EEA";
    private String gradientEnd   = "#764BA2";
    private int    gradientAngle = 135;
    private int    cornerRadius  = 18;
    private int    elevationDp   = 8;
    private String textColor     = "#FFFFFF";
    private String subtextColor  = "#CCFFFFFF";
    private int    paddingDp     = 20;

    private final Map<String, LinearLayout> cardMap = new HashMap<>();

    public ModernCard(ComponentContainer container) {
        super(container.$form());
        this.activity = container.$context() instanceof Activity
                ? (Activity) container.$context()
                : (Activity) container.$form();
        this.context = container.$context();
    }

    @SimpleProperty(description = "Starting color of the gradient (hex).")
    public String GradientStart() { return gradientStart; }

    @DesignerProperty(editorType = "color", defaultValue = "&HFF667EEA")
    @SimpleProperty
    public void GradientStart(String color) { gradientStart = color; }

    @SimpleProperty(description = "Ending color of the gradient (hex).")
    public String GradientEnd() { return gradientEnd; }

    @DesignerProperty(editorType = "color", defaultValue = "&HFF764BA2")
    @SimpleProperty
    public void GradientEnd(String color) { gradientEnd = color; }

    @SimpleProperty(description = "Gradient angle in degrees (0=left-right, 90=bottom-top, 135=diagonal).")
    public int GradientAngle() { return gradientAngle; }

    @DesignerProperty(editorType = "integer", defaultValue = "135")
    @SimpleProperty
    public void GradientAngle(int angle) { gradientAngle = angle; }

    @SimpleProperty(description = "Corner radius in dp.")
    public int CornerRadius() { return cornerRadius; }

    @DesignerProperty(editorType = "integer", defaultValue = "18")
    @SimpleProperty
    public void CornerRadius(int dp) { cornerRadius = dp; }

    @SimpleProperty(description = "Shadow elevation in dp.")
    public int ElevationDp() { return elevationDp; }

    @DesignerProperty(editorType = "integer", defaultValue = "8")
    @SimpleProperty
    public void ElevationDp(int dp) { elevationDp = dp; }

    @SimpleProperty(description = "Main text color.")
    public String TextColor() { return textColor; }

    @DesignerProperty(editorType = "color", defaultValue = "&HFFFFFFFF")
    @SimpleProperty
    public void TextColor(String color) { textColor = color; }

    @SimpleProperty(description = "Subtitle text color.")
    public String SubtextColor() { return subtextColor; }

    @DesignerProperty(editorType = "color", defaultValue = "&HCCFFFFFF")
    @SimpleProperty
    public void SubtextColor(String color) { subtextColor = color; }

    @SimpleProperty(description = "Card internal padding in dp.")
    public int PaddingDp() { return paddingDp; }

    @DesignerProperty(editorType = "integer", defaultValue = "20")
    @SimpleProperty
    public void PaddingDp(int dp) { paddingDp = dp; }

    @SimpleFunction(description = "Creates a modern card inside a layout. cardId must be unique.")
    public String CreateCard(AndroidViewComponent container,
                             final String cardId,
                             String title,
                             String subtitle,
                             String value) {
        if (cardMap.containsKey(cardId)) return "";

        ViewGroup parent = (ViewGroup) container.getView();
        int px = dpToPx(paddingDp);

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(px, px, px, px);
        card.setTag(cardId);

        applyBackground(card, buildGradient());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            card.setElevation(dpToPx(elevationDp));
        }

        TextView tvSub = new TextView(context);
        tvSub.setText(subtitle);
        tvSub.setTextSize(12);
        tvSub.setTextColor(parseColor(subtextColor));
        card.addView(tvSub);

        TextView tvTitle = new TextView(context);
        tvTitle.setText(title);
        tvTitle.setTextSize(15);
        tvTitle.setTextColor(parseColor(textColor));
        tvTitle.setTypeface(tvTitle.getTypeface(), android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTitle.setMargins(0, dpToPx(4), 0, 0);
        tvTitle.setLayoutParams(lpTitle);
        card.addView(tvTitle);

        TextView tvVal = new TextView(context);
        tvVal.setText(value);
        tvVal.setTextSize(26);
        tvVal.setTextColor(parseColor(textColor));
        tvVal.setTypeface(tvVal.getTypeface(), android.graphics.Typeface.BOLD);
        LinearLayout.LayoutParams lpVal = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpVal.setMargins(0, dpToPx(10), 0, 0);
        tvVal.setLayoutParams(lpVal);
        card.addView(tvVal);

        card.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { CardClicked(cardId); }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, dpToPx(14));
        parent.addView(card, lp);

        cardMap.put(cardId, card);
        return cardId;
    }

    @SimpleFunction(description = "Updates title, subtitle, and value of an existing card.")
    public void UpdateCard(String cardId, String title, String subtitle, String value) {
        LinearLayout card = cardMap.get(cardId);
        if (card == null) return;
        applyBackground(card, buildGradient());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            card.setElevation(dpToPx(elevationDp));
        }
        if (card.getChildCount() >= 3) {
            ((TextView) card.getChildAt(0)).setText(subtitle);
            ((TextView) card.getChildAt(0)).setTextColor(parseColor(subtextColor));
            ((TextView) card.getChildAt(1)).setText(title);
            ((TextView) card.getChildAt(1)).setTextColor(parseColor(textColor));
            ((TextView) card.getChildAt(2)).setText(value);
            ((TextView) card.getChildAt(2)).setTextColor(parseColor(textColor));
        }
    }

    @SimpleFunction(description = "Removes a card by its ID.")
    public void RemoveCard(String cardId) {
        LinearLayout card = cardMap.get(cardId);
        if (card == null) return;
        ViewGroup parent = (ViewGroup) card.getParent();
        if (parent != null) parent.removeView(card);
        cardMap.remove(cardId);
    }

    @SimpleFunction(description = "Sets a solid color background instead of gradient.")
    public void SetSolidBackground(String cardId, String colorHex) {
        LinearLayout card = cardMap.get(cardId);
        if (card == null) return;
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(parseColor(colorHex));
        bg.setCornerRadius(dpToPx(cornerRadius));
        applyBackground(card, bg);
    }

    @SimpleEvent(description = "Fires when a card is tapped. cardId identifies which card.")
    public void CardClicked(String cardId) {
        EventDispatcher.dispatchEvent(this, "CardClicked", cardId);
    }

    private GradientDrawable buildGradient() {
        GradientDrawable gd = new GradientDrawable(
                angleToOrientation(gradientAngle),
                new int[]{ parseColor(gradientStart), parseColor(gradientEnd) });
        gd.setCornerRadius(dpToPx(cornerRadius));
        return gd;
    }

    private void applyBackground(View view, GradientDrawable bg) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(bg);
        } else {
            view.setBackgroundDrawable(bg);
        }
    }

    private GradientDrawable.Orientation angleToOrientation(int angle) {
        int snapped = ((angle + 22) / 45) * 45 % 360;
        switch (snapped) {
            case 0:   return GradientDrawable.Orientation.LEFT_RIGHT;
            case 45:  return GradientDrawable.Orientation.BL_TR;
            case 90:  return GradientDrawable.Orientation.BOTTOM_TOP;
            case 135: return GradientDrawable.Orientation.BR_TL;
            case 180: return GradientDrawable.Orientation.RIGHT_LEFT;
            case 225: return GradientDrawable.Orientation.TR_BL;
            case 270: return GradientDrawable.Orientation.TOP_BOTTOM;
            case 315: return GradientDrawable.Orientation.TL_BR;
            default:  return GradientDrawable.Orientation.TL_BR;
        }
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private int parseColor(String hex) {
        try { return Color.parseColor(hex); }
        catch (IllegalArgumentException e) { return Color.WHITE; }
    }
}
