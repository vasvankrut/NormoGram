/*
This file is part of NormoGram, an unofficial Telegram client.

For license and copyright information please follow the link in the repository root.
*/
#include "settings/sections/settings_normogram.h"

#include "core/application.h"
#include "core/core_settings.h"
#include "core/file_utilities.h"
#include "lang/lang_keys.h"
#include "settings/sections/settings_main.h"
#include "settings/settings_builder.h"
#include "settings/settings_common_session.h"
#include "ui/boxes/single_choice_box.h"
#include "ui/layers/generic_box.h"
#include "ui/painter.h"
#include "ui/ui_utility.h"
#include "ui/widgets/buttons.h"
#include "ui/wrap/vertical_layout.h"
#include "window/window_session_controller.h"
#include "styles/style_menu_icons.h"
#include "styles/style_settings.h"

#include <QtGui/QPainterPath>
#include <QtWidgets/QApplication>

namespace Settings {
namespace {

using namespace Builder;

constexpr auto kShowSecondsKey = "normogram/show-seconds";
constexpr auto kDontRoundViewsKey = "normogram/dont-round-views";
constexpr auto kDontRoundMembersKey = "normogram/dont-round-members";
constexpr auto kIconIndexKey = "normogram/icon-index";
constexpr auto kCustomIconPathKey = "normogram/custom-icon-path";

constexpr auto kIconBlue = 0;
constexpr auto kIconNight = 1;
constexpr auto kIconOriginal = 2;
constexpr auto kIconCustom = 3;

[[nodiscard]] QString IconPathForIndex(int index) {
	switch (index) {
	case kIconNight: return u":/gui/art/normogram-night.png"_q;
	case kIconOriginal: return u":/gui/art/logo_256.png"_q;
	case kIconCustom: return NormoGramCustomIconPath();
	default: return u":/gui/art/normogram-blue.png"_q;
	}
}

void BuildRoundingSectionContent(SectionBuilder &builder) {
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

	const auto dontRoundViews = builder.addCheckbox({
		.id = u"normogram/dont_round_views"_q,
		.title = tr::lng_normogram_dont_round_views(),
		.checked = NormoGramDontRoundViews(),
		.keywords = { u"views"_q, u"rounding"_q, u"counters"_q },
	});
	if (dontRoundViews) {
		dontRoundViews->checkedChanges() | rpl::on_next([=](bool checked) {
			Core::App().settings().writePref<bool>(kDontRoundViewsKey, checked);
			Core::App().saveSettingsDelayed();
		}, dontRoundViews->lifetime());
	}

	builder.addSkip(st::settingsCheckboxesSkip);

	const auto dontRoundMembers = builder.addCheckbox({
		.id = u"normogram/dont_round_members"_q,
		.title = tr::lng_normogram_dont_round_members(),
		.checked = NormoGramDontRoundMembers(),
		.keywords = { u"subscribers"_q, u"members"_q, u"rounding"_q },
	});
	if (dontRoundMembers) {
		dontRoundMembers->checkedChanges() | rpl::on_next([=](bool checked) {
			Core::App().settings().writePref<bool>(
				kDontRoundMembersKey,
				checked);
			Core::App().saveSettingsDelayed();
		}, dontRoundMembers->lifetime());
	}
}

class NormoGramRoundingSettings final : public Section<NormoGramRoundingSettings> {
public:
	NormoGramRoundingSettings(
		QWidget *parent,
		not_null<Window::SessionController*> controller);

	[[nodiscard]] rpl::producer<QString> title() override;

private:
	void setupContent();

};

NormoGramRoundingSettings::NormoGramRoundingSettings(
	QWidget *parent,
	not_null<Window::SessionController*> controller)
: Section(parent, controller) {
	setupContent();
}

rpl::producer<QString> NormoGramRoundingSettings::title() {
	return tr::lng_normogram_rounding();
}

void NormoGramRoundingSettings::setupContent() {
	const auto content = Ui::CreateChild<Ui::VerticalLayout>(this);

	const auto buildMethod = [](
			not_null<Ui::VerticalLayout*> container,
			not_null<Window::SessionController*> controller,
			Fn<void(Type)> showOther,
			rpl::producer<> showFinished) {
		const auto isPaused = Window::PausedIn(
			controller,
			Window::GifPauseReason::Layer);
		auto builder = SectionBuilder(WidgetContext{
			.container = container,
			.controller = controller,
			.showOther = std::move(showOther),
			.isPaused = isPaused,
		});
		BuildRoundingSectionContent(builder);
	};

	build(content, buildMethod);
	Ui::ResizeFitChild(this, content);
}

const auto kRoundingMeta = BuildHelper({
	.id = NormoGramRoundingSettings::Id(),
	.parentId = NormoGramId(),
	.title = &tr::lng_normogram_rounding,
	.icon = &st::menuIconManage,
}, [](SectionBuilder &builder) {
	BuildRoundingSectionContent(builder);
});

class AppIconButton final : public Ui::SettingsButton {
public:
	AppIconButton(QWidget *parent, Fn<void()> onClick)
	: Ui::SettingsButton(parent, tr::lng_normogram_app_icon(), st::settingsButton) {
		setClickedCallback(std::move(onClick));
	}

protected:
	void paintEvent(QPaintEvent *e) override {
		Ui::SettingsButton::paintEvent(e);

		const auto image = NormoGramIconImage();
		const auto name = NormoGramIconName(NormoGramIconIndex());

		auto p = Painter(this);
		if (!image.isNull()) {
			const auto size = st::normogramIconPreviewSize;
			const auto rect = QRect(
				st::settingsButton.iconLeft,
				(height() - size) / 2,
				size,
				size);
			auto path = QPainterPath();
			path.addRoundedRect(
				rect,
				st::normogramIconPreviewRadius,
				st::normogramIconPreviewRadius);
			p.setClipPath(path);
			p.drawImage(rect, image);
			p.setClipPath(QPainterPath());
		}
		p.setFont(st::settingsButton.style.font);
		p.setPen(st::windowSubTextFg);
		p.drawText(
			QRect(
				0,
				0,
				width() - st::settingsButton.padding.right(),
				height()),
			Qt::AlignRight | Qt::AlignVCenter,
			name);
	}

};

void BuildNormoGramSectionContent(SectionBuilder &builder) {
	const auto controller = builder.controller();

	const auto showIconPicker = [=] {
		const auto options = std::vector<QString>{
			tr::lng_normogram_icon_blue(tr::now),
			tr::lng_normogram_icon_night(tr::now),
			tr::lng_normogram_icon_original(tr::now),
			tr::lng_normogram_icon_custom(tr::now),
		};
		controller->show(Box([=](not_null<Ui::GenericBox*> box) {
			SingleChoiceBox(box, {
				.title = tr::lng_normogram_app_icon(),
				.options = options,
				.initialSelection = NormoGramIconIndex(),
				.callback = [=](int index) {
					if (index == kIconCustom) {
						const auto filters = u"Image files (*.png *.jpg *.jpeg);;"_q
							+ FileDialog::AllFilesFilter();
						FileDialog::GetOpenPath(
							Core::App().getFileDialogParent(),
							tr::lng_normogram_icon_custom(tr::now),
							filters,
							[=](const FileDialog::OpenResult &result) {
								if (result.paths.isEmpty()) {
									return;
								}
								const auto path = result.paths.front();
								if (QImage(path).isNull()) {
									return;
								}
								Core::App().settings().writePref<QByteArray>(
									kCustomIconPathKey,
									path.toUtf8());
								Core::App().settings().writePref<QByteArray>(
									kIconIndexKey,
									QByteArray::number(kIconCustom));
								Core::App().saveSettingsDelayed();
								NormoGramApplyIcon();
							});
						return;
					}
					Core::App().settings().writePref<QByteArray>(
						kIconIndexKey,
						QByteArray::number(index));
					Core::App().saveSettingsDelayed();
					NormoGramApplyIcon();
				},
			});
		}));
	};

	builder.addSkip();

	builder.add([=](const WidgetContext &ctx) {
		return SectionBuilder::WidgetToAdd{
			.widget = object_ptr<AppIconButton>(ctx.container, showIconPicker),
		};
	});

	builder.addSkip(st::settingsCheckboxesSkip);

	builder.addSectionButton({
		.title = tr::lng_normogram_rounding(),
		.targetSection = NormoGramRoundingId(),
		.icon = { &st::menuIconManage },
		.keywords = { u"rounding"_q, u"seconds"_q, u"views"_q, u"members"_q },
	});
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

	const auto buildMethod = [](
			not_null<Ui::VerticalLayout*> container,
			not_null<Window::SessionController*> controller,
			Fn<void(Type)> showOther,
			rpl::producer<> showFinished) {
		const auto isPaused = Window::PausedIn(
			controller,
			Window::GifPauseReason::Layer);
		auto builder = SectionBuilder(WidgetContext{
			.container = container,
			.controller = controller,
			.showOther = std::move(showOther),
			.isPaused = isPaused,
		});
		BuildNormoGramSectionContent(builder);
	};

	build(content, buildMethod);
	Ui::ResizeFitChild(this, content);
}

const auto kMeta = BuildHelper({
	.id = NormoGramSettings::Id(),
	.parentId = MainId(),
	.title = &tr::lng_normogram_settings,
	.icon = &st::menuIconManage,
}, [](SectionBuilder &builder) {
	BuildNormoGramSectionContent(builder);
});

} // namespace

Type NormoGramId() {
	return NormoGramSettings::Id();
}

Type NormoGramRoundingId() {
	return NormoGramRoundingSettings::Id();
}

bool NormoGramShowSeconds() {
	return Core::App().settings().readPref<bool>(kShowSecondsKey, false);
}

bool NormoGramDontRoundViews() {
	return Core::App().settings().readPref<bool>(kDontRoundViewsKey, false);
}

bool NormoGramDontRoundMembers() {
	return Core::App().settings().readPref<bool>(kDontRoundMembersKey, false);
}

int NormoGramIconIndex() {
	const auto raw = Core::App().settings().readPref<QByteArray>(
		kIconIndexKey,
		QByteArray());
	return raw.isEmpty() ? kIconBlue : raw.toInt();
}

QString NormoGramCustomIconPath() {
	const auto raw = Core::App().settings().readPref<QByteArray>(
		kCustomIconPathKey,
		QByteArray());
	return raw.isEmpty() ? QString() : QString::fromUtf8(raw);
}

QString NormoGramIconName(int index) {
	switch (index) {
	case kIconNight: return tr::lng_normogram_icon_night(tr::now);
	case kIconOriginal: return tr::lng_normogram_icon_original(tr::now);
	case kIconCustom: return tr::lng_normogram_icon_custom(tr::now);
	default: return tr::lng_normogram_icon_blue(tr::now);
	}
}

QImage NormoGramIconImage() {
	const auto index = NormoGramIconIndex();
	const auto path = IconPathForIndex(index);
	auto image = QImage(path);
	return image.isNull()
		? QImage(u":/gui/art/normogram-blue.png"_q)
		: image;
}

void NormoGramApplyIcon() {
	const auto icon = QIcon(Ui::PixmapFromImage(NormoGramIconImage()));
	QApplication::setWindowIcon(icon);
	for (const auto widget : QApplication::topLevelWidgets()) {
		widget->setWindowIcon(icon);
		widget->update();
	}
}

} // namespace Settings
