package org.telegram.ui;

import android.content.Context;
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
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;

public class NormoGramRoundingActivity extends BaseFragment {

    private TextCheckCell secondsCell;
    private TextCheckCell viewsCell;
    private TextCheckCell membersCell;

    @Override
    public View createView(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        fragmentView = frameLayout;

        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setTitle(LocaleController.getString(R.string.NormoGramRounding));

        LinearLayout settingsLayout = new LinearLayout(context);
        settingsLayout.setOrientation(LinearLayout.VERTICAL);
        settingsLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        frameLayout.addView(settingsLayout, new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ));

        TextInfoPrivacyCell intro = new TextInfoPrivacyCell(context);
        intro.setText(LocaleController.getString(R.string.NormoGramRoundingInfo));
        settingsLayout.addView(intro, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        secondsCell = new TextCheckCell(context);
        secondsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        secondsCell.setTextAndCheck(
            LocaleController.getString(R.string.NormoGramShowSeconds),
            SharedConfig.normogramShowSeconds,
            true
        );
        secondsCell.setOnClickListener(view -> {
            SharedConfig.normogramShowSeconds = !SharedConfig.normogramShowSeconds;
            SharedConfig.saveConfig();
            secondsCell.setChecked(SharedConfig.normogramShowSeconds);
            refreshInterfaces();
        });
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

        viewsCell = new TextCheckCell(context);
        viewsCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        viewsCell.setTextAndCheck(
            LocaleController.getString(R.string.NormoGramDontRoundViews),
            SharedConfig.normogramDontRoundViews,
            true
        );
        viewsCell.setOnClickListener(view -> {
            SharedConfig.normogramDontRoundViews = !SharedConfig.normogramDontRoundViews;
            SharedConfig.saveConfig();
            viewsCell.setChecked(SharedConfig.normogramDontRoundViews);
            refreshInterfaces();
        });
        settingsLayout.addView(viewsCell, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            AndroidUtilities.dp(50)
        ));

        TextInfoPrivacyCell viewsInfo = new TextInfoPrivacyCell(context);
        viewsInfo.setText(LocaleController.getString(R.string.NormoGramDontRoundViewsInfo));
        settingsLayout.addView(viewsInfo, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        membersCell = new TextCheckCell(context);
        membersCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        membersCell.setTextAndCheck(
            LocaleController.getString(R.string.NormoGramDontRoundMembers),
            SharedConfig.normogramDontRoundMembers,
            true
        );
        membersCell.setOnClickListener(view -> {
            SharedConfig.normogramDontRoundMembers = !SharedConfig.normogramDontRoundMembers;
            SharedConfig.saveConfig();
            membersCell.setChecked(SharedConfig.normogramDontRoundMembers);
            refreshInterfaces();
        });
        settingsLayout.addView(membersCell, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            AndroidUtilities.dp(50)
        ));

        TextInfoPrivacyCell membersInfo = new TextInfoPrivacyCell(context);
        membersInfo.setText(LocaleController.getString(R.string.NormoGramDontRoundMembersInfo));
        settingsLayout.addView(membersInfo, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        return fragmentView;
    }

    private void refreshInterfaces() {
        for (int account = 0; account < UserConfig.MAX_ACCOUNT_COUNT; account++) {
            NotificationCenter.getInstance(account).postNotificationName(
                NotificationCenter.updateInterfaces,
                MessagesController.UPDATE_MASK_ALL
            );
        }
    }
}
