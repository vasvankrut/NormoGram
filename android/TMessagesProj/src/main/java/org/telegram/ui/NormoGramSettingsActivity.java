package org.telegram.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;
import org.telegram.ui.Components.RadioButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class NormoGramSettingsActivity extends BaseFragment {

    private static final int ICON_BLUE = 0;
    private static final int ICON_NIGHT = 1;
    private static final int ICON_ORIGINAL = 2;
    private static final int ICON_CUSTOM = 3;

    private static final String[] ICON_COMPONENTS = {
        "org.telegram.messenger.NormoGramDefaultIcon",
        "org.telegram.messenger.NormoGramNightIcon",
        "org.telegram.messenger.NormoGramOriginalIcon"
    };

    private ImageView iconPreviewView;
    private TextView iconValueView;

    @Override
    public View createView(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        fragmentView = frameLayout;

        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setTitle(LocaleController.getString(R.string.NormoGramSettings));

        LinearLayout settingsLayout = new LinearLayout(context);
        settingsLayout.setOrientation(LinearLayout.VERTICAL);
        settingsLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        frameLayout.addView(settingsLayout, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));

        settingsLayout.addView(buildIconRow(context), new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            AndroidUtilities.dp(56)
        ));

        TextInfoPrivacyCell iconInfo = new TextInfoPrivacyCell(context);
        iconInfo.setText(LocaleController.getString(R.string.NormoGramAppIconInfo));
        settingsLayout.addView(iconInfo, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextSettingsCell roundingCell = new TextSettingsCell(context);
        roundingCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        roundingCell.setTextAndValue(
            LocaleController.getString(R.string.NormoGramRounding),
            "",
            true
        );
        roundingCell.setOnClickListener(view -> presentFragment(new NormoGramRoundingActivity()));
        settingsLayout.addView(roundingCell, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            AndroidUtilities.dp(56)
        ));

        TextInfoPrivacyCell roundingInfo = new TextInfoPrivacyCell(context);
        roundingInfo.setText(LocaleController.getString(R.string.NormoGramRoundingInfo));
        settingsLayout.addView(roundingInfo, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        return fragmentView;
    }

    private View buildIconRow(Context context) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        row.setPadding(AndroidUtilities.dp(16), 0, AndroidUtilities.dp(16), 0);

        iconPreviewView = new ImageView(context);
        iconPreviewView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        row.addView(iconPreviewView, new LinearLayout.LayoutParams(
            AndroidUtilities.dp(40),
            AndroidUtilities.dp(40)
        ));

        TextView titleView = new TextView(context);
        titleView.setTextSize(16);
        titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        titleView.setText(LocaleController.getString(R.string.NormoGramAppIcon));
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        );
        titleParams.leftMargin = AndroidUtilities.dp(16);
        row.addView(titleView, titleParams);

        iconValueView = new TextView(context);
        iconValueView.setTextSize(14);
        iconValueView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteValueText));
        iconValueView.setGravity(Gravity.END);
        iconValueView.setSingleLine(true);
        iconValueView.setEllipsize(android.text.TextUtils.TruncateAt.END);
        row.addView(iconValueView, new LinearLayout.LayoutParams(
            AndroidUtilities.dp(160),
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        row.setOnClickListener(view -> showIconPicker());
        updateIconCell();
        return row;
    }

    private void updateIconCell() {
        if (iconPreviewView == null) {
            return;
        }
        int icon = getSelectedIcon();
        iconPreviewView.setImageDrawable(getIconPreviewDrawable(icon));
        iconValueView.setText(getIconTitle(icon));
    }

    private void showIconPicker() {
        Context context = getParentActivity();
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(0, AndroidUtilities.dp(8), 0, AndroidUtilities.dp(8));

        final AlertDialog[] dialogRef = new AlertDialog[1];
        for (int icon = ICON_BLUE; icon <= ICON_CUSTOM; icon++) {
            container.addView(buildIconOptionRow(context, icon, dialogRef));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.NormoGramAppIcon));
        builder.setView(container);
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        dialogRef[0] = builder.create();
        showDialog(dialogRef[0]);
    }

    private View buildIconOptionRow(Context context, int icon, AlertDialog[] dialogRef) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(AndroidUtilities.dp(20), AndroidUtilities.dp(10), AndroidUtilities.dp(20), AndroidUtilities.dp(10));
        row.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6), Color.TRANSPARENT, Theme.getColor(Theme.key_listSelector)));

        ImageView preview = new ImageView(context);
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        preview.setImageDrawable(getIconPreviewDrawable(icon));
        row.addView(preview, new LinearLayout.LayoutParams(
            AndroidUtilities.dp(44),
            AndroidUtilities.dp(44)
        ));

        TextView title = new TextView(context);
        title.setTextSize(16);
        title.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        title.setText(getIconTitle(icon));
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        );
        titleParams.leftMargin = AndroidUtilities.dp(16);
        row.addView(title, titleParams);

        RadioButton radio = new RadioButton(context);
        radio.setSize(AndroidUtilities.dp(22));
        radio.setColor(Theme.getColor(Theme.key_dialogRadioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
        radio.setChecked(getSelectedIcon() == icon, false);
        row.addView(radio, new LinearLayout.LayoutParams(
            AndroidUtilities.dp(22),
            AndroidUtilities.dp(22)
        ));

        row.setOnClickListener(view -> {
            if (dialogRef[0] != null) {
                dialogRef[0].dismiss();
            }
            if (icon == ICON_CUSTOM) {
                openCustomIconPicker();
            } else {
                setSelectedIcon(icon);
            }
        });
        return row;
    }

    private void openCustomIconPicker() {
        final HashMap<Object, Object> photos = new HashMap<>();
        final ArrayList<Object> order = new ArrayList<>();
        PhotoPickerActivity fragment = new PhotoPickerActivity(0, null, photos, order, 1, false, null, false);
        fragment.setMaxSelectedPhotos(1, false);
        fragment.setDelegate(new PhotoPickerActivity.PhotoPickerActivityDelegate() {
            @Override
            public void selectedPhotosChanged() {
            }

            @Override
            public void actionButtonPressed(boolean canceled, boolean notify, int scheduleDate, int scheduleRepeatPeriod) {
                if (canceled || order.isEmpty()) {
                    return;
                }
                Object object = photos.get(order.get(0));
                String path = null;
                if (object instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry entry = (MediaController.PhotoEntry) object;
                    path = entry.imagePath != null ? entry.imagePath : entry.path;
                } else if (object instanceof MediaController.SearchImage) {
                    path = ((MediaController.SearchImage) object).imagePath;
                }
                if (path != null) {
                    applyCustomIcon(path);
                }
            }

            @Override
            public void onCaptionChanged(CharSequence caption) {
            }
        });
        presentFragment(fragment);
    }

    private void applyCustomIcon(String path) {
        Context context = getParentActivity();
        Bitmap bitmap = ImageLoader.loadBitmap(path, null, 192, 192, true);
        if (bitmap == null) {
            return;
        }
        File output = new File(context.getFilesDir(), "normogram_custom_icon.png");
        try (FileOutputStream stream = new FileOutputStream(output)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (Exception e) {
            return;
        }
        context.getSharedPreferences("mainconfig", Context.MODE_PRIVATE)
            .edit()
            .putString("normogramCustomIconPath", output.getAbsolutePath())
            .apply();
        setSelectedIcon(ICON_CUSTOM);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createCustomShortcut(context, bitmap);
        }
    }

    private void createCustomShortcut(Context context, Bitmap bitmap) {
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);
        if (shortcutManager == null || !shortcutManager.isRequestPinShortcutSupported()) {
            return;
        }
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent == null) {
            return;
        }
        ShortcutInfo shortcut = new ShortcutInfo.Builder(context, "normogram_custom_icon")
            .setShortLabel(LocaleController.getString(R.string.AppName))
            .setIcon(Icon.createWithBitmap(bitmap))
            .setIntent(launchIntent)
            .build();
        shortcutManager.requestPinShortcut(shortcut, null);
    }

    private int getSelectedIcon() {
        return getParentActivity()
            .getSharedPreferences("mainconfig", Context.MODE_PRIVATE)
            .getInt("normogramAppIcon", 0);
    }

    private String getCustomIconPath() {
        return getParentActivity()
            .getSharedPreferences("mainconfig", Context.MODE_PRIVATE)
            .getString("normogramCustomIconPath", null);
    }

    private String getIconTitle(int icon) {
        switch (icon) {
            case ICON_NIGHT:
                return LocaleController.getString(R.string.NormoGramIconNight);
            case ICON_ORIGINAL:
                return LocaleController.getString(R.string.NormoGramIconOriginal);
            case ICON_CUSTOM:
                return LocaleController.getString(R.string.NormoGramIconCustom);
            default:
                return LocaleController.getString(R.string.NormoGramIconBlue);
        }
    }

    private Drawable getIconPreviewDrawable(int icon) {
        Context context = getParentActivity();
        switch (icon) {
            case ICON_NIGHT:
                return ContextCompat.getDrawable(context, R.mipmap.ic_normogram_night_launcher);
            case ICON_ORIGINAL:
                return ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
            case ICON_CUSTOM: {
                String path = getCustomIconPath();
                if (path != null) {
                    Bitmap bitmap = ImageLoader.loadBitmap(path, null, 160, 160, true);
                    if (bitmap != null) {
                        return new android.graphics.drawable.BitmapDrawable(context.getResources(), bitmap);
                    }
                }
                return ContextCompat.getDrawable(context, R.drawable.camera);
            }
            default:
                return ContextCompat.getDrawable(context, R.mipmap.ic_normogram_launcher);
        }
    }

    private void setSelectedIcon(int selected) {
        if (selected == ICON_CUSTOM) {
            getParentActivity()
                .getSharedPreferences("mainconfig", Context.MODE_PRIVATE)
                .edit()
                .putInt("normogramAppIcon", selected)
                .apply();
            updateIconCell();
            return;
        }
        if (selected < 0 || selected >= ICON_COMPONENTS.length) {
            return;
        }
        PackageManager packageManager = getParentActivity().getPackageManager();
        for (int i = 0; i < ICON_COMPONENTS.length; i++) {
            ComponentName component = new ComponentName(
                getParentActivity(),
                ICON_COMPONENTS[i]
            );
            packageManager.setComponentEnabledSetting(
                component,
                i == selected
                    ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                    : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            );
        }
        getParentActivity()
            .getSharedPreferences("mainconfig", Context.MODE_PRIVATE)
            .edit()
            .putInt("normogramAppIcon", selected)
            .apply();
        updateIconCell();
    }
}
