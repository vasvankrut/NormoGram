/*
This file is part of NormoGram, an unofficial Telegram client.

For license and copyright information please follow the link in the repository root.
*/
#pragma once

#include "settings/settings_type.h"

#include <QImage>
#include <QString>

namespace Settings {

[[nodiscard]] Type NormoGramId();
[[nodiscard]] Type NormoGramRoundingId();
[[nodiscard]] bool NormoGramShowSeconds();
[[nodiscard]] bool NormoGramDontRoundViews();
[[nodiscard]] bool NormoGramDontRoundMembers();
[[nodiscard]] int NormoGramIconIndex();
[[nodiscard]] QString NormoGramCustomIconPath();
[[nodiscard]] QString NormoGramIconName(int index);
[[nodiscard]] QImage NormoGramIconImage();
void NormoGramApplyIcon();

} // namespace Settings
