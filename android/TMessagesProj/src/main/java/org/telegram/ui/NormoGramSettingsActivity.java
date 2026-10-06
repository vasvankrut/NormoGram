package org.telegram.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.TextSettingsCell;

public class NormoGramSettingsActivity extends BaseFragment {

    private static final String[] ICON_COMPONENTS = {
        "org.telegram.messenger.NormoGramDefaultIcon",
        "org.telegram.messenger.NormoGramNightIcon"
    };

    private TextSettingsCell iconCell;
    private TextCheckCell secondsCell;

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

        iconCell = new TextSettingsCell(context);
        iconCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        iconCell.setTextAndValue(
            LocaleController.getString(R.string.NormoGramAppIcon),
            getIconTitle(getSelectedIcon()),
            true
        );
        iconCell.setOnClickListener(view -> showIconPicker());
        settingsLayout.addView(iconCell, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            AndroidUtilities.dp(56)
        ));

        TextInfoPrivacyCell iconInfo = new TextInfoPrivacyCell(context);
        iconInfo.setText(LocaleController.getString(R.string.NormoGramAppIconInfo));
        settingsLayout.addView(iconInfo, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        secondsCell = new TextCheckCell(context);
        secondsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        secondsCell.setTextAndCheck(
            LocaleController.getString(R.string.NormoGramShowSeconds),
            SharedConfig.normogramShowSeconds,
            false
        );
        secondsCell.setOnClickListener(view -> toggleSeconds());
        settingsLayout.addView(secondsCell, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            AndroidUtilities.dp(50)
        ));

        TextInfoPrivacyCell secondsInfo = new TextInfoPrivacyCell(context);
        secondsInfo.setText(LocaleController.getString(R.string.NormoGramShowSecondsInfo));
        settingsLayout.addView(secondsInfo, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        return fragmentView;
    }

    private void toggleSeconds() {
        SharedConfig.normogramShowSeconds = !SharedConfig.normogramShowSeconds;
        SharedConfig.saveConfig();
        secondsCell.setChecked(SharedConfig.normogramShowSeconds);
        for (int account = 0; account < UserConfig.MAX_ACCOUNT_COUNT; account++) {
            NotificationCenter.getInstance(account).postNotificationName(
                NotificationCenter.updateInterfaces,
                MessagesController.UPDATE_MASK_ALL
            );
        }
    }

    private void showIconPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.NormoGramAppIcon));
        builder.setItems(new CharSequence[] {
            LocaleController.getString(R.string.NormoGramIconBlue),
            LocaleController.getString(R.string.NormoGramIconNight)
        }, (dialog, which) -> setSelectedIcon(which));
        showDialog(builder.create());
    }

    private int getSelectedIcon() {
        return getParentActivity()
            .getSharedPreferences("mainconfig", Context.MODE_PRIVATE)
            .getInt("normogramAppIcon", 0);
    }

    private String getIconTitle(int icon) {
        return LocaleController.getString(icon == 1
            ? R.string.NormoGramIconNight
            : R.string.NormoGramIconBlue);
    }

    private void setSelectedIcon(int selected) {
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
        if (iconCell != null) {
            iconCell.setTextAndValue(
                LocaleController.getString(R.string.NormoGramAppIcon),
                getIconTitle(selected),
                true
            );
        }
    }
}
