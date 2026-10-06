/*
This file is part of NormoGram, an unofficial Telegram client.

For license and copyright information please follow the link in the repository root.
*/
#include "settings/sections/settings_normogram.h"

#include "core/application.h"
#include "core/core_settings.h"
#include "lang/lang_keys.h"
#include "settings/settings_builder.h"
#include "settings/settings_common_session.h"
#include "settings/sections/settings_main.h"
#include "ui/widgets/checkbox.h"
#include "ui/ui_utility.h"
#include "ui/wrap/vertical_layout.h"
#include "styles/style_menu_icons.h"
#include "styles/style_settings.h"

#include <QtWidgets/QApplication>

namespace Settings {
namespace {

constexpr auto kShowSecondsKey = "normogram/show-seconds";
constexpr auto kNightIconKey = "normogram/night-icon";

void ApplyApplicationIcon() {
	const auto path = NormoGramUseNightIcon()
		? u":/gui/art/normogram-night.png"_q
		: u":/gui/art/normogram-blue.png"_q;
	const auto icon = QIcon(path);
	QApplication::setWindowIcon(icon);
	for (const auto widget : QApplication::topLevelWidgets()) {
		widget->setWindowIcon(icon);
	}
}

class NormoGramSettings final : public Section<NormoGramSettings> {
public:
	NormoGramSettings(
		QWidget *parent,
		not_null<Window::SessionController*> controller);

	[[nodiscard]] rpl::producer<QString> title() override;

private:
	void setupContent();
};

const auto kMeta = BuildHelper({
	.id = NormoGramSettings::Id(),
	.parentId = MainId(),
	.title = &tr::lng_normogram_settings,
	.icon = &st::menuIconManage,
}, [](SectionBuilder &builder) {
	builder.addSkip();

	const auto showSeconds = builder.addCheckbox({
		.id = u"normogram/show_seconds"_q,
		.title = tr::lng_normogram_show_seconds(),
		.checked = NormoGramShowSeconds(),
		.keywords = { u"time"_q, u"seconds"_q, u"timestamp"_q },
	});
	if (showSeconds) {
		showSeconds->checkedChanges() | rpl::on_next([=](bool checked) {
			Core::App().settings().writePref<bool>(kShowSecondsKey, checked);
			Core::App().saveSettingsDelayed();
		}, showSeconds->lifetime());
	}

	builder.addSkip(st::settingsCheckboxesSkip);

	const auto nightIcon = builder.addCheckbox({
		.id = u"normogram/night_icon"_q,
		.title = tr::lng_normogram_night_icon(),
		.checked = NormoGramUseNightIcon(),
		.keywords = { u"app"_q, u"icon"_q, u"appearance"_q },
	});
	if (nightIcon) {
		nightIcon->checkedChanges() | rpl::on_next([=](bool checked) {
			Core::App().settings().writePref<bool>(kNightIconKey, checked);
			Core::App().saveSettingsDelayed();
			ApplyApplicationIcon();
		}, nightIcon->lifetime());
	}
});

const SectionBuildMethod kNormoGramSection = kMeta.build;

NormoGramSettings::NormoGramSettings(
		QWidget *parent,
		not_null<Window::SessionController*> controller)
: Section(parent, controller) {
	setupContent();
}

rpl::producer<QString> NormoGramSettings::title() {
	return tr::lng_normogram_settings();
}

void NormoGramSettings::setupContent() {
	const auto content = Ui::CreateChild<Ui::VerticalLayout>(this);
	build(content, kNormoGramSection);
	Ui::ResizeFitChild(this, content);
}

} // namespace

Type NormoGramId() {
	return NormoGramSettings::Id();
}

bool NormoGramShowSeconds() {
	return Core::App().settings().readPref<bool>(kShowSecondsKey, false);
}

bool NormoGramUseNightIcon() {
	return Core::App().settings().readPref<bool>(kNightIconKey, false);
}

} // namespace Settings
